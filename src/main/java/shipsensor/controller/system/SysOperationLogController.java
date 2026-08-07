package shipsensor.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import shipsensor.entity.SysOperationLog;
import shipsensor.service.SysOperationLogService;

import java.util.List;

@RestController
@RequestMapping("/sysOperationLog")
public class SysOperationLogController {

    @Autowired
    private SysOperationLogService sysOperationLogService;

    @GetMapping("/{id}")
    public SysOperationLog getById(@PathVariable Long id) {
        return sysOperationLogService.getById(id);
    }

    @GetMapping
    public List<SysOperationLog> getAll() {
        return sysOperationLogService.list();
    }

    @PostMapping
    public boolean save(@RequestBody SysOperationLog log) {
        return sysOperationLogService.save(log);
    }

    @PostMapping("/batch")
    public boolean saveBatch(@RequestBody List<SysOperationLog> logList) {
        return sysOperationLogService.saveBatch(logList);
    }

    @PutMapping
    public boolean update(@RequestBody SysOperationLog log) {
        return sysOperationLogService.updateById(log);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return sysOperationLogService.removeById(id);
    }

    // ... 前面的代码不变

    @GetMapping("/page")
    public IPage<SysOperationLog> getPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            // 建议参数名也改为 userName，保持语义一致
            @RequestParam(required = false) String userName,
            // 如果数据库没有 module 字段，建议暂时去掉这个参数，或者改为 targetTable
            @RequestParam(required = false) String targetTable
    ) {
        if (size > 100) size = 100;
        LambdaQueryWrapper<SysOperationLog> wrapper = new LambdaQueryWrapper<>();

        // 修改点 1：使用 getUserName 匹配实体类字段
        if (userName != null && !userName.isBlank()) {
            wrapper.like(SysOperationLog::getUserName, userName);
        }

        // 修改点 2：使用 getTargetTable 匹配实体类字段 (或者如果你添加了 module 字段则用 getModule)
        if (targetTable != null && !targetTable.isBlank()) {
            wrapper.eq(SysOperationLog::getTargetTable, targetTable);
        }

        wrapper.orderByDesc(SysOperationLog::getCreateTime);
        return sysOperationLogService.page(new Page<>(page, size), wrapper);
    }
}