package shipsensor.inter;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 智能客服专用查询。
 *
 * sensor_data 表数据量达亿级，这里的每条 SQL 都强制走索引并带 LIMIT，
 * 避免出现全表扫描拖垮数据库。
 */
@Mapper
public interface AiAssistantMapper {

    /**
     * 查询某艘船最近的传感器读数。
     * 走 sensor_data 的联合索引 idx_ship_time(ship_id, recorded_at)，倒序取前 N 条。
     */
    @Select("SELECT d.data_value AS dataValue, d.recorded_at AS recordedAt, "
            + "d.config_id AS configId, c.type_id AS typeId, c.sensor_name AS sensorName "
            + "FROM sensor_data d "
            + "INNER JOIN sensor_config c ON c.id = d.config_id "
            + "WHERE d.ship_id = #{shipId} "
            + "ORDER BY d.recorded_at DESC "
            + "LIMIT #{limit}")
    List<Map<String, Object>> selectLatestReadings(@Param("shipId") Integer shipId,
                                                   @Param("limit") int limit);

    /**
     * 查询最近的告警记录，并联表补齐船名与规则名。
     * handleStatus 传 null 表示不限处理状态。结果按时间倒序并限制条数。
     */
    @Select("SELECT r.id AS id, s.ship_name AS shipName, ar.rule_name AS ruleName, "
            + "ar.alert_level AS alertLevel, r.trigger_value AS triggerValue, "
            + "r.alert_time AS alertTime, r.handle_status AS handleStatus "
            + "FROM alert_record r "
            + "LEFT JOIN ship_info s ON s.id = r.ship_id "
            + "LEFT JOIN alert_rule ar ON ar.id = r.rule_id "
            + "WHERE (#{handleStatus} IS NULL OR r.handle_status = #{handleStatus}) "
            + "ORDER BY r.alert_time DESC "
            + "LIMIT #{limit}")
    List<Map<String, Object>> selectRecentAlerts(@Param("handleStatus") Integer handleStatus,
                                                 @Param("limit") int limit);

    /**
     * 统计某艘船的告警数量，按处理状态分组。
     * 走 alert_record 的联合索引 idx_ship_alert(ship_id, alert_time)。
     */
    @Select("SELECT handle_status AS handleStatus, COUNT(*) AS count "
            + "FROM alert_record WHERE ship_id = #{shipId} GROUP BY handle_status")
    List<Map<String, Object>> selectShipAlertSummary(@Param("shipId") Integer shipId);

    /**
     * 获取指定表的大致行数。
     * 从 information_schema 读取统计信息，避免对亿级大表执行 COUNT(*) 全表扫描。
     */
    @Select("SELECT IFNULL(TABLE_ROWS, 0) FROM information_schema.TABLES "
            + "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = #{tableName}")
    Long selectTableRowEstimate(@Param("tableName") String tableName);
}
