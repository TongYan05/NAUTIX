package shipsensor.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import shipsensor.entity.SensorDict;
import shipsensor.service.SensorDictService;
import java.util.List;

@RestController
@RequestMapping("/sensorDict")
public class SensorDictController {

    @Autowired
    private SensorDictService sensorDictService;

    @GetMapping("/{id}")
    public SensorDict getById(@PathVariable Integer id) {
        return sensorDictService.getById(id);
    }

    @GetMapping("/list")
    public List<SensorDict> getAll() {
        return sensorDictService.list();
    }

    @PostMapping
    public boolean save(@RequestBody SensorDict sensorDict) {
        return sensorDictService.save(sensorDict);
    }

    @PostMapping("/batch")
    public boolean saveBatch(@RequestBody List<SensorDict> list) {
        return sensorDictService.saveBatch(list);
    }

    @PutMapping
    public boolean update(@RequestBody SensorDict sensorDict) {
        return sensorDictService.updateById(sensorDict);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id) {
        return sensorDictService.removeById(id);
    }

    // 【修改点】：将 /list 改为 /page，解决路径冲突
    @GetMapping("/page")
    public IPage<SensorDict> getPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortOrder) {

        if (count > 100) count = 100;

        LambdaQueryWrapper<SensorDict> wrapper = new LambdaQueryWrapper<>();

        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(SensorDict::getTypeCode, keyword)
                    .or()
                    .like(SensorDict::getTypeName, keyword));
        }

        applySort(wrapper, sortField, sortOrder);

        return sensorDictService.page(new Page<>(page, count), wrapper);
    }

    private void applySort(LambdaQueryWrapper<SensorDict> wrapper, String sortField, String sortOrder) {
        if (sortField == null || sortField.isBlank()) return;

        String[] allowed = {"id", "typeCode", "typeName"};
        boolean ok = false;
        for (String f : allowed) {
            if (f.equals(sortField)) {
                ok = true;
                break;
            }
        }
        if (!ok) return;

        boolean isAsc = "asc".equalsIgnoreCase(sortOrder);
        switch (sortField) {
            case "id": wrapper.orderBy(true, isAsc, SensorDict::getId); break;
            case "typeCode": wrapper.orderBy(true, isAsc, SensorDict::getTypeCode); break;
            case "typeName": wrapper.orderBy(true, isAsc, SensorDict::getTypeName); break;
        }
    }
}