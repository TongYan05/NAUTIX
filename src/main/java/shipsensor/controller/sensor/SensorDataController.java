package shipsensor.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import shipsensor.entity.SensorData;
import shipsensor.service.SensorDataService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/sensorData")
public class SensorDataController {

    @Autowired
    private SensorDataService sensorDataService;

    @GetMapping("/{id}")
    public SensorData getById(@PathVariable Long id) {
        return sensorDataService.getById(id);
    }

    @GetMapping("/list")
    public List<SensorData> getAll() {
        return sensorDataService.list();
    }

    @PostMapping
    public boolean save(@RequestBody SensorData sensorData) {
        return sensorDataService.save(sensorData);
    }

    @PostMapping("/batch")
    public boolean saveBatch(@RequestBody List<SensorData> dataList) {
        return sensorDataService.saveBatch(dataList);
    }

    @PutMapping
    public boolean update(@RequestBody SensorData sensorData) {
        return sensorDataService.updateById(sensorData);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return sensorDataService.removeById(id);
    }

    @GetMapping("/page")
    public IPage<SensorData> getPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer shipId, // 【修改1】改为 Integer 以匹配实体类
            // @RequestParam(required = false) String sensorCode, // 【修改2】实体类没这个字段，建议暂时注释掉或删除
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime
    ) {
        if (size > 100) size = 100;
        LambdaQueryWrapper<SensorData> wrapper = new LambdaQueryWrapper<>();

        // 根据船舶ID筛选
        if (shipId != null) {
            wrapper.eq(SensorData::getShipId, shipId);
        }

        // 【修改3】关于 sensorCode：因为实体类没有这个字段，这里必须删掉或注释掉
    /*
    if (sensorCode != null && !sensorCode.isBlank()) {
        wrapper.eq(SensorData::getSensorCode, sensorCode);
    }
    */

        // 时间范围筛选
        // 【修改4】将 getCollectTime 改为 getRecordedAt，并解析字符串时间
        if (startTime != null && !startTime.isBlank()) {
            // 假设前端传入格式为 "yyyy-MM-dd HH:mm:ss"
            wrapper.ge(SensorData::getRecordedAt, LocalDateTime.parse(startTime.replace(" ", "T")));
        }
        if (endTime != null && !endTime.isBlank()) {
            wrapper.le(SensorData::getRecordedAt, LocalDateTime.parse(endTime.replace(" ", "T")));
        }

        // 默认按采集时间(recordedAt)倒序
        wrapper.orderByDesc(SensorData::getRecordedAt);

        return sensorDataService.page(new Page<>(page, size), wrapper);
    }
}