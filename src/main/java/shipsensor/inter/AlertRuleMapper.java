package shipsensor.inter;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import shipsensor.entity.AlertRule;

import java.util.List;

/**
 * 告警规则配置表 Mapper 接口
 */
@Mapper
public interface AlertRuleMapper extends BaseMapper<AlertRule> {
    void insertBatch(List<AlertRule> rules);
}