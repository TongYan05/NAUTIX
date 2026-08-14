package shipsensor.controller.ship;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import shipsensor.entity.ShipInfo;
import shipsensor.service.ShipInfoService;

import java.util.List;

@RestController
@RequestMapping("/shipInfo")
public class ShipInfoController {

    @Autowired
    private ShipInfoService shipInfoService;

    @GetMapping("/{id}")
    public ShipInfo getById(@PathVariable Long id) {
        return shipInfoService.getById(id);
    }

    @GetMapping("/all")
    public List<ShipInfo> getAll() {
        return shipInfoService.list();
    }

    @PostMapping
    public boolean save(@RequestBody ShipInfo shipInfo) {
        return shipInfoService.save(shipInfo);
    }

    @PostMapping("/batch")
    public boolean saveBatch(@RequestBody List<ShipInfo> shipInfoList) {
        return shipInfoService.saveBatch(shipInfoList);
    }

    @PutMapping
    public boolean update(@RequestBody ShipInfo shipInfo) {
        return shipInfoService.updateById(shipInfo);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return shipInfoService.removeById(id);
    }

    @DeleteMapping("/batch")
    public boolean deleteBatch(@RequestBody List<Long> ids) {
        return shipInfoService.removeByIds(ids);
    }

    @GetMapping("/page")
    public IPage<ShipInfo> getPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String shipStatus,
            @RequestParam(required = false) String shipType,
            @RequestParam(required = false) String registryPort,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortOrder) {

        if (count > 100) count = 100;
        LambdaQueryWrapper<ShipInfo> wrapper = new LambdaQueryWrapper<>();

        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(ShipInfo::getShipName, keyword)
                    .or().like(ShipInfo::getImo, keyword)
                    .or().like(ShipInfo::getOperatingCompany, keyword));
        }


        if (shipType != null && !shipType.isBlank()) {
            wrapper.like(ShipInfo::getShipType, shipType);
        }

        if (registryPort != null && !registryPort.isBlank()) {
            wrapper.like(ShipInfo::getRegistryPort, registryPort);
        }

        if (startDate != null && !startDate.isBlank()) {
            wrapper.ge(ShipInfo::getCreateTime, startDate);
        }

        if (endDate != null && !endDate.isBlank()) {
            wrapper.le(ShipInfo::getCreateTime, endDate);
        }

        applySort(wrapper, sortField, sortOrder);
        return shipInfoService.page(new Page<>(page, count), wrapper);
    }

    private void applySort(LambdaQueryWrapper<ShipInfo> wrapper, String sortField, String sortOrder) {
        if (sortField == null || sortField.isBlank()) return;

        String[] allowed = {"id", "shipName", "imo", "buildYear", "lengthOverall", "sailingSpeed", "createTime"};
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
            case "id":
                wrapper.orderBy(true, isAsc, ShipInfo::getId);
                break;
            case "shipName":
                wrapper.orderBy(true, isAsc, ShipInfo::getShipName);
                break;
            case "imo":
                wrapper.orderBy(true, isAsc, ShipInfo::getImo);
                break;
            case "buildYear":
                wrapper.orderBy(true, isAsc, ShipInfo::getBuildYear);
                break;
            case "lengthOverall":
                wrapper.orderBy(true, isAsc, ShipInfo::getLengthOverall);
                break;
            case "sailingSpeed":
                wrapper.orderBy(true, isAsc, ShipInfo::getSailingSpeed);
                break;
            case "createTime":
                wrapper.orderBy(true, isAsc, ShipInfo::getCreateTime);
                break;
        }
    }
}
