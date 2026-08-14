package shipsensor.simulator.writer;


import org.springframework.stereotype.Component;
import shipsensor.entity.SysOperationLog;
import shipsensor.inter.SysOperationLogMapper;

import java.time.LocalDateTime;



@Component
public class OperationLogWriter {


    private final SysOperationLogMapper mapper;



    public OperationLogWriter(
            SysOperationLogMapper mapper
    ){

        this.mapper = mapper;

    }



    /**
     * 写入系统操作日志
     *
     * @param operation 操作类型
     *                  INSERT UPDATE DELETE QUERY SYNC
     *
     * @param table     操作表
     *
     * @param id        目标数据ID
     */
    public void write(
            String operation,
            String table,
            Long id
    ){


        SysOperationLog log =
                new SysOperationLog();



        log.setUserName(
                "simulator"
        );


        log.setOperationType(
                operation
        );


        log.setTargetTable(
                table
        );


        log.setTargetId(
                id
        );


        log.setCreateTime(
                LocalDateTime.now()
        );



        mapper.insert(log);


    }



    /**
     * 快捷记录新增
     */
    public void insert(
            String table,
            Long id
    ){

        write(
                "INSERT",
                table,
                id
        );

    }




    /**
     * 快捷记录修改
     */
    public void update(
            String table,
            Long id
    ){

        write(
                "UPDATE",
                table,
                id
        );

    }




    /**
     * 快捷记录删除
     */
    public void delete(
            String table,
            Long id
    ){

        write(
                "DELETE",
                table,
                id
        );

    }




    /**
     * 快捷记录查询
     */
    public void query(
            String table,
            Long id
    ){

        write(
                "QUERY",
                table,
                id
        );

    }




    /**
     * 快捷记录同步
     */
    public void sync(
            String table,
            Long id
    ){

        write(
                "SYNC",
                table,
                id
        );

    }


}