package shipsensor.inter;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import shipsensor.entity.SysOperationLog;

/**
 * 系统操作审计日志表 Mapper 接口
 */
@Mapper
public interface SysOperationLogMapper extends BaseMapper<SysOperationLog> {
}