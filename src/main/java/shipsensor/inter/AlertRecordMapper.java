package shipsensor.inter;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import shipsensor.entity.AlertRecord;

import java.util.List;
import java.util.Map;

@Mapper
public interface AlertRecordMapper extends BaseMapper<AlertRecord> {

    @Select("SELECT handle_status AS handleStatus, COUNT(*) AS count FROM alert_record GROUP BY handle_status")
    List<Map<String, Object>> selectAlertStatusSummary();

    @Select("SELECT HOUR(alert_time) AS hour, COUNT(*) AS count FROM alert_record GROUP BY HOUR(alert_time)")
    List<Map<String, Object>> selectAlertHourDistribution();
}
