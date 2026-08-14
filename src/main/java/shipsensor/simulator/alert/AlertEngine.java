package shipsensor.simulator.alert;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shipsensor.entity.AlertEvent;
import shipsensor.entity.AlertRecord;
import shipsensor.entity.AlertRule;
import shipsensor.inter.AlertEventMapper;
import shipsensor.inter.AlertRecordMapper;
import shipsensor.inter.AlertRuleMapper;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class AlertEngine {

    private final AlertRuleMapper alertRuleMapper;

    private final AlertRecordMapper alertRecordMapper;

    private final AlertEventMapper alertEventMapper;

    /**
     * typeId -> AlertRule
     */
    private final Map<Integer, List<AlertRule>> ruleCache =
            new ConcurrentHashMap<>();

    @PostConstruct
    public void loadRules() {

        List<AlertRule> rules =
                alertRuleMapper.selectList(null);

        for (AlertRule rule : rules) {

            ruleCache
                    .computeIfAbsent(
                            rule.getTypeId(),
                            k -> new ArrayList<>())
                    .add(rule);

        }

        System.out.println("Alert rules loaded : " + rules.size());

    }

    /**
     * 检查报警
     */
    public boolean check(Integer shipId,
                         Integer typeId,
                         Double value) {

        List<AlertRule> rules =
                ruleCache.get(typeId);

        if (rules == null) {
            return false;
        }

        boolean alarm = false;

        for (AlertRule rule : rules) {

            if (match(rule, value)) {

                processAlarm(shipId, rule, value);

                alarm = true;

            } else {

                recover(shipId, rule);

            }

        }

        return alarm;

    }

    /**
     * 判断是否满足
     */
    private boolean match(AlertRule rule,
                          Double value) {

        double threshold =
                rule.getThresholdValue().doubleValue();

        return switch (rule.getOperator()) {

            case ">" -> value > threshold;

            case "<" -> value < threshold;

            case ">=" -> value >= threshold;

            case "<=" -> value <= threshold;

            case "=" ->
                    Math.abs(value - threshold) < 0.0001;

            default -> false;

        };

    }

    /**
     * 处理报警
     */
    private void processAlarm(Integer shipId,
                              AlertRule rule,
                              Double value) {

        QueryWrapper<AlertEvent> wrapper =
                new QueryWrapper<>();

        wrapper.eq("ship_id", shipId)
                .eq("rule_id", rule.getId())
                .eq("status", 1);

        AlertEvent event =
                alertEventMapper.selectOne(wrapper);

        if (event == null) {

            createEvent(shipId, rule, value);

            createRecord(shipId, rule, value);

            return;

        }

        if (value > event.getMaxValue()) {

            event.setMaxValue(value);

            alertEventMapper.updateById(event);

        }

    }

    /**
     * 创建报警事件
     */
    private void createEvent(Integer shipId,
                             AlertRule rule,
                             Double value) {

        AlertEvent event =
                new AlertEvent();

        event.setShipId(shipId);

        event.setRuleId(rule.getId());

        event.setStartTime(LocalDateTime.now());

        event.setMaxValue(value);

        event.setStatus(1);

        alertEventMapper.insert(event);

    }

    /**
     * 创建报警记录
     */
    private void createRecord(Integer shipId,
                              AlertRule rule,
                              Double value) {

        AlertRecord record =
                new AlertRecord();

        record.setShipId(shipId);

        record.setRuleId(rule.getId());

        record.setTriggerValue(
                BigDecimal.valueOf(value));

        record.setAlertTime(
                LocalDateTime.now());

        record.setHandleStatus(0);

        alertRecordMapper.insert(record);

    }

    /**
     * 恢复报警
     */
    private void recover(Integer shipId,
                         AlertRule rule) {

        QueryWrapper<AlertEvent> wrapper =
                new QueryWrapper<>();

        wrapper.eq("ship_id", shipId)
                .eq("rule_id", rule.getId())
                .eq("status", 1);

        AlertEvent event =
                alertEventMapper.selectOne(wrapper);

        if (event == null) {
            return;
        }

        event.setEndTime(LocalDateTime.now());

        long seconds =
                Duration.between(
                                event.getStartTime(),
                                event.getEndTime())
                        .getSeconds();

        event.setDurationSeconds((int) seconds);

        event.setStatus(2);

        alertEventMapper.updateById(event);

    }



}

