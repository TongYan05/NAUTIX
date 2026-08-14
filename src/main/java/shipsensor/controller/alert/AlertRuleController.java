package shipsensor.controller.alert;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import shipsensor.entity.AlertRule;
import shipsensor.service.AlertRuleService;

import java.util.List;

@RestController
@RequestMapping("/alertRule")
public class AlertRuleController {

    @Autowired
    private AlertRuleService alertRuleService;

    @GetMapping("/{id}")
    public AlertRule getById(@PathVariable Long id) {
        return alertRuleService.getById(id);
    }

    @GetMapping("/list")
    public List<AlertRule> getAll() {
        return alertRuleService.list();
    }

    @PostMapping
    public boolean save(@RequestBody AlertRule alertRule) {
        return alertRuleService.save(alertRule);
    }

    @PostMapping("/batch")
    public boolean saveBatch(@RequestBody List<AlertRule> ruleList) {
        return alertRuleService.saveBatch(ruleList);
    }

    @PutMapping
    public boolean update(@RequestBody AlertRule alertRule) {
        return alertRuleService.updateById(alertRule);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return alertRuleService.removeById(id);
    }

    @GetMapping("/page")
    public IPage<AlertRule> getPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String ruleName,
            @RequestParam(required = false) Integer alertLevel
    ) {
        if (size > 100) size = 100;
        LambdaQueryWrapper<AlertRule> wrapper = new LambdaQueryWrapper<>();

        if (ruleName != null && !ruleName.isBlank()) {
            wrapper.like(AlertRule::getRuleName, ruleName);
        }
        if (alertLevel != null) {
            wrapper.eq(AlertRule::getAlertLevel, alertLevel);
        }

        wrapper.orderByDesc(AlertRule::getId);
        return alertRuleService.page(new Page<>(page, size), wrapper);
    }
}