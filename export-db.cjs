const mysql = require('mysql2/promise');
const fs = require('fs');
const path = require('path');

const DB_HOST = '192.168.1.101';
const DB_PORT = 3306;
const DB_USER = 'tongyan';
const DB_PASS = '2005';
const DB_NAME = 'ship_sensor';
const OUTPUT = path.join(__dirname, 'ship_sensor_export.sql');
const BATCH_SIZE = 5000;

function escapeVal(v) {
  if (v === null) return 'NULL';
  if (typeof v === 'number') return String(v);
  if (v instanceof Date) return `'${v.toISOString().replace('T', ' ').substring(0, 19)}'`;
  if (v instanceof Uint8Array || Buffer.isBuffer(v)) return "X'" + v.toString('hex') + "'";
  return "'" + String(v).replace(/\\/g, '\\\\').replace(/'/g, "\\'") + "'";
}

async function main() {
  const conn = await mysql.createConnection({
    host: DB_HOST, port: DB_PORT, user: DB_USER, password: DB_PASS
  });

  const ws = fs.createWriteStream(OUTPUT, { flags: 'w' });
  ws.write(`-- ship_sensor database export\n`);
  ws.write(`-- Generated at ${new Date().toISOString()}\n`);
  ws.write(`CREATE DATABASE IF NOT EXISTS \`${DB_NAME}\` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;\n`);
  ws.write(`USE \`${DB_NAME}\`;\n`);
  ws.write('SET FOREIGN_KEY_CHECKS=0;\n\n');

  const [tables] = await conn.query(`SHOW TABLES FROM \`${DB_NAME}\``);
  const tableNameKey = Object.keys(tables[0])[0];

  for (const row of tables) {
    const tableName = row[tableNameKey];

    const [countRes] = await conn.query(`SELECT COUNT(*) as cnt FROM \`${DB_NAME}\`.\`${tableName}\``);
    const totalRows = countRes[0].cnt;
    console.log(`Exporting: ${tableName} (${totalRows} rows)`);

    const [createRes] = await conn.query(`SHOW CREATE TABLE \`${DB_NAME}\`.\`${tableName}\``);
    ws.write(`DROP TABLE IF EXISTS \`${tableName}\`;\n`);
    ws.write(createRes[0]['Create Table'] + ';\n\n');

    if (totalRows === 0) { console.log('  empty, skipped.'); continue; }

    const [colRes] = await conn.query(`SELECT * FROM \`${DB_NAME}\`.\`${tableName}\` LIMIT 1`);
    const cols = Object.keys(colRes[0]);
    const colList = cols.map(c => '`' + c + '`').join(',');

    // Check if table has an 'id' column for cursor-based pagination
    const hasId = cols.includes('id');
    let exported = 0;
    let lastId = 0;

    if (hasId) {
      // Cursor-based: WHERE id > lastId ORDER BY id LIMIT N
      while (true) {
        const [batch] = await conn.query(
          `SELECT * FROM \`${DB_NAME}\`.\`${tableName}\` WHERE id > ? ORDER BY id LIMIT ?`,
          [lastId, BATCH_SIZE]
        );
        if (batch.length === 0) break;

        const valuesList = batch.map(r => '(' + cols.map(c => escapeVal(r[c])).join(',') + ')');
        ws.write(`INSERT INTO \`${tableName}\` (${colList}) VALUES\n${valuesList.join(',\n')};\n`);

        lastId = batch[batch.length - 1].id;
        exported += batch.length;
        if (exported % 50000 === 0) process.stdout.write(`  ${exported}/${totalRows}\r`);
      }
    } else {
      // Fallback: LIMIT/OFFSET for tables without id column
      for (let offset = 0; offset < totalRows; offset += BATCH_SIZE) {
        const [batch] = await conn.query(
          `SELECT * FROM \`${DB_NAME}\`.\`${tableName}\` LIMIT ${BATCH_SIZE} OFFSET ${offset}`
        );
        if (batch.length === 0) break;
        const valuesList = batch.map(r => '(' + cols.map(c => escapeVal(r[c])).join(',') + ')');
        ws.write(`INSERT INTO \`${tableName}\` (${colList}) VALUES\n${valuesList.join(',\n')};\n`);
        exported += batch.length;
      }
    }

    ws.write('\n');
    console.log(`  done: ${exported} rows`);
  }

  ws.write('SET FOREIGN_KEY_CHECKS=1;\n');
  ws.end();
  await new Promise(resolve => ws.on('finish', resolve));

  const fileSize = (fs.statSync(OUTPUT).size / 1024 / 1024).toFixed(2);
  console.log(`\nDone! File: ${OUTPUT} (${fileSize} MB)`);
  await conn.end();
}

main().catch(e => { console.error('Export failed:', e); process.exit(1); });
