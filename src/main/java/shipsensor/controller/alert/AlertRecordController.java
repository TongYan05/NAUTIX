package shipsensor.controller.alert;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import shipsensor.entity.AlertRecord;
import shipsensor.service.AlertRecordService;

import java.util.List;

@RestController
@RequestMapping("/alertRecord")
public class AlertRecordController {

    @Autowired
    private AlertRecordService alertRecordService;

    @GetMapping("/{id}")
    public AlertRecord getById(@PathVariable Long id) {
        return alertRecordService.getById(id);
    }

    @GetMapping("/list")
    public List<AlertRecord> getAll() {
        return alertRecordService.list();
    }

    @PostMapping
    public boolean save(@RequestBody AlertRecord alertRecord) {
        return alertRecordService.save(alertRecord);
    }

    @PostMapping("/batch")
    public boolean saveBatch(@RequestBody List<AlertRecord> recordList) {
        return alertRecordService.saveBatch(recordList);
    }

    @PutMapping
    public boolean update(@RequestBody AlertRecord alertRecord) {
        return alertRecordService.updateById(alertRecord);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return alertRecordService.removeById(id);
    }

    @GetMapping("/page")
    public IPage<AlertRecord> getPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long shipId,
            // 建议：将参数名也统一为 handleStatus
            @RequestParam(required = false) Integer handleStatus
    ) {
        if (size > 100) size = 100;
        LambdaQueryWrapper<AlertRecord> wrapper = new LambdaQueryWrapper<>();

        if (shipId != null) {
            wrapper.eq(AlertRecord::getShipId, shipId);
        }
        if (handleStatus != null) { // 使用新的参数名
            // 修正这里的方法引用
            wrapper.eq(AlertRecord::getHandleStatus, handleStatus);
        }

        // 报警记录通常按时间倒序查看
        wrapper.orderByDesc(AlertRecord::getAlertTime);
        return alertRecordService.page(new Page<>(page, size), wrapper);
    }
}