package shipsensor.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 系统操作审计日志表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_operation_log")
public class SysOperationLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String userName;

    private String operationType;

    private String targetTable;

    private Long targetId;

    private String oldValue;

    private String newValue;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}