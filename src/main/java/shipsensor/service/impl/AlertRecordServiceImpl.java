package shipsensor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import shipsensor.entity.AlertRecord;
import shipsensor.inter.AlertRecordMapper;
import shipsensor.service.AlertRecordService;

@Service
public class AlertRecordServiceImpl extends ServiceImpl<AlertRecordMapper, AlertRecord> implements AlertRecordService {
}