package shipsensor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import shipsensor.entity.SysOperationLog;
import shipsensor.inter.SysOperationLogMapper;
import shipsensor.service.SysOperationLogService;

@Service
public class SysOperationLogServiceImpl extends ServiceImpl<SysOperationLogMapper, SysOperationLog> implements SysOperationLogService {
}