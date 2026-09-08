package shipsensor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shipsensor.dto.AiChatResponse;
import shipsensor.entity.AlertRule;
import shipsensor.entity.Port;
import shipsensor.entity.Route;
import shipsensor.entity.SensorConfig;
import shipsensor.entity.SensorDict;
import shipsensor.entity.ShipInfo;
import shipsensor.entity.ShipRuntimeStatus;
import shipsensor.entity.WeatherRegion;
import shipsensor.inter.AiAssistantMapper;
import shipsensor.service.AiAssistantService;
import shipsensor.service.AiText;
import shipsensor.service.AlertRuleService;
import shipsensor.service.DashboardService;
import shipsensor.service.DailyLifeAssistant;
import shipsensor.service.PortService;
import shipsensor.service.RouteService;
import shipsensor.service.SensorConfigService;
import shipsensor.service.SensorDictService;
import shipsensor.service.ShipInfoService;
import shipsensor.service.ShipRuntimeStatusService;
import shipsensor.service.WeatherRegionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 智能客服服务实现（中英双语）。
 *
 * 采用"意图识别 + 真实数据查询"的方式工作：先判断用户问题属于哪一类业务意图，
 * 再用对应的查询从数据库中取出真实数据组织成回答。所有回答均来自本库数据，
 * 不调用任何外部大模型接口，因此在无外网环境下同样可用，也不会出现编造数据。
 *
 * 固定话术按请求语言（zh/en）输出；业务数据本身（船名、港口名、规则名等）
 * 保持库中原文。
 *
 * 性能约束：sensor_data 表数据量达亿级，涉及该表的查询统一走
 * AiAssistantMapper 中带索引条件与 LIMIT 的语句，且行数统计使用
 * information_schema 估算值，避免全表扫描。
 */
@Service
public class AiAssistantServiceImpl implements AiAssistantService {

    /**
     * 单次回答中最多展示的记录条数
     */
    private static final int MAX_ROWS = 5;

    @Autowired
    private ShipInfoService shipInfoService;

    @Autowired
    private ShipRuntimeStatusService shipRuntimeStatusService;

    @Autowired
    private SensorConfigService sensorConfigService;

    @Autowired
    private SensorDictService sensorDictService;

    @Autowired
    private AlertRuleService alertRuleService;

    @Autowired
    private RouteService routeService;

    @Autowired
    private PortService portService;

    @Autowired
    private WeatherRegionService weatherRegionService;

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private AiAssistantMapper aiAssistantMapper;

    /**
     * 日常生活问答助手：在平台业务问题之外，兜底回答时间日期、算术、
     * 单位换算、BMI 与健康生活常识等通用问题。
     */
    @Autowired
    private DailyLifeAssistant dailyLifeAssistant;

    /**
     * 传感器类型字典缓存：typeId -> SensorDict。
     * 字典仅十几条且极少变动，缓存可显著减少高频问答时的重复查询。
     */
    private final Map<Integer, SensorDict> dictCache = new LinkedHashMap<>();

    @Override
    public AiChatResponse chat(String message, String lang) {
        AiText t = AiText.of(lang);

        if (message == null || message.isBlank()) {
            return new AiChatResponse(
                    t.s("请输入您想了解的问题，例如：平台有多少艘船？远洋-00000号现在状态如何？",
                            "Please type your question, e.g. \"How many ships are on the platform?\" or \"What is 远洋-00000's status?\""),
                    "empty", helpSuggestions(t));
        }

        String text = message.trim();
        String lower = text.toLowerCase(Locale.ROOT);

        // 按意图优先级依次尝试匹配
        if (isHelpIntent(lower)) {
            return help(t);
        }
        if (isOverviewIntent(lower)) {
            return overview(t);
        }
        if (isAlertRuleIntent(lower)) {
            return alertRules(text, t);
        }
        if (isAlertIntent(lower)) {
            return alerts(text, t);
        }
        if (isWeatherIntent(lower)) {
            return weather(text, t);
        }
        if (isPortIntent(lower)) {
            return ports(text, t);
        }
        if (isRouteIntent(lower)) {
            return routes(text, t);
        }
        if (isSensorIntent(lower)) {
            return sensors(text, t);
        }
        if (isShipIntent(lower)) {
            return ships(text, t);
        }

        // 兜底：问题里包含某艘船的船名或 IMO 时，按船舶详情回答
        ShipInfo matched = findShip(text);
        if (matched != null) {
            return shipDetail(matched, t);
        }

        // 平台业务之外的日常问题：时间日期、算术、单位换算、BMI、生活常识
        if (dailyLifeAssistant.matches(text)) {
            AiChatResponse daily = dailyLifeAssistant.answer(text, lang);
            if (daily != null) {
                return daily;
            }
        }

        return dailyLifeAssistant.smallTalkFallback(text, lang);
    }

    // ==================== 意图判定 ====================
    //
    // 中文关键词用子串匹配（中文无词边界）；英文关键词必须整词匹配，
    // 否则 "supported" 会命中 "port"、"relationship" 会命中 "ship" 等造成误判。

    private boolean isHelpIntent(String lower) {
        return containsAny(lower, "帮助", "你能做什么", "会什么", "功能", "怎么用", "使用说明")
                || containsWord(lower, "help", "what can you", "how do you work", "capabilit");
    }

    private boolean isOverviewIntent(String lower) {
        return containsAny(lower, "概况", "概览", "总览", "多少艘船", "多少条船", "统计",
                "平台数据", "数据量", "多少传感器")
                || containsWord(lower, "overview", "summary", "stats",
                "how many ships", "how many vessels", "platform data", "total number");
    }

    private boolean isAlertRuleIntent(String lower) {
        return containsAny(lower, "告警规则", "报警规则", "阈值", "规则有哪些")
                || containsWord(lower, "alert rule", "alert rules", "rules exist", "threshold");
    }

    private boolean isAlertIntent(String lower) {
        return containsAny(lower, "告警", "报警", "异常", "故障")
                || containsWord(lower, "alert", "alerts", "alarm", "alarms", "warning", "warnings");
    }

    private boolean isWeatherIntent(String lower) {
        return containsAny(lower, "天气", "海况", "风浪", "气温", "台风", "雾")
                || containsWord(lower, "weather", "typhoon", "storm", "fog", "wind speed", "wave height");
    }

    private boolean isPortIntent(String lower) {
        return containsAny(lower, "港口", "码头", "停靠")
                || containsWord(lower, "port", "ports", "harbour", "harbors", "harbor");
    }

    private boolean isRouteIntent(String lower) {
        return containsAny(lower, "航线", "航程", "航行路线")
                || containsWord(lower, "route", "routes", "voyage", "longest route", "shortest route");
    }

    private boolean isSensorIntent(String lower) {
        return containsAny(lower, "传感器", "读数", "监测数据", "温度", "转速",
                "油压", "振动", "电压", "燃油", "功率")
                || containsWord(lower, "sensor", "sensors", "reading", "readings", "rpm", "vibration",
                "voltage", "fuel", "power", "pressure", "temperature");
    }

    private boolean isShipIntent(String lower) {
        return containsAny(lower, "船", "船舶", "船队")
                || containsWord(lower, "ship", "ships", "imo", "vessel", "fleet");
    }

    // ==================== 各意图回答 ====================

    private AiChatResponse help(AiText t) {
        String zh = "您好，我是 NautiX 平台的智能客服，可以帮您查询平台内的真实数据。您可以这样问我：\n\n" +
                "1. 平台概况：平台有多少艘船？\n" +
                "2. 船舶信息：远洋-00000号 的基本情况\n" +
                "3. 实时状态：远洋-00000号 现在什么状态？\n" +
                "4. 传感器数据：远洋-00000号 最新的传感器读数\n" +
                "5. 告警情况：最近的告警有哪些？/ 远洋-00000号 有告警吗？\n" +
                "6. 告警规则：告警规则有哪些？\n" +
                "7. 航线港口：航线有哪些？/ 查询中国的港口\n" +
                "8. 海域天气：天气情况怎么样？\n" +
                "9. 日常生活：现在几点 / 128乘以7等于多少 / 5公里是多少英里 / BMI / 失眠怎么办\n\n" +
                "说明：平台问题的回答全部来自本库真实数据，不会凭空生成；日常知识为内置通用常识，仅供参考。";
        String en = "Hi, I'm the NautiX smart assistant. I can query real data on this platform. Try asking:\n\n" +
                "1. Overview: How many ships are on the platform?\n" +
                "2. Ship info: profile of 远洋-00000\n" +
                "3. Live status: What is 远洋-00000's current status?\n" +
                "4. Sensors: latest sensor readings of 远洋-00000\n" +
                "5. Alerts: What are the latest alerts?\n" +
                "6. Alert rules: What alert rules exist?\n" +
                "7. Routes & ports: list routes / ports in Australia\n" +
                "8. Weather: How is the sea weather?\n" +
                "9. Daily life: What time is it? / 128 * 7? / 5 km in miles? / BMI / tips for insomnia\n\n" +
                "Note: platform answers come from the real database, never fabricated; daily-life knowledge is built-in general reference.";
        return new AiChatResponse(t.s(zh, en), "help", helpSuggestions(t));
    }

    private AiChatResponse overview(AiText t) {
        StringBuilder sb = new StringBuilder();
        sb.append(t.s("平台数据概况如下：", "Platform data overview:")).append("\n\n");

        try {
            Map<String, Long> stats = dashboardService.getStats();
            sb.append("- ").append(t.s("船舶总数", "Total ships")).append(t.colon()).append(num(stats.get("shipCount"))).append(t.s(" 艘\n", "\n"));
            sb.append("- ").append(t.s("传感器配置", "Sensor configurations")).append(t.colon()).append(num(stats.get("sensorConfigCount"))).append("\n");
            sb.append("- ").append(t.s("告警记录", "Alert records")).append(t.colon()).append(num(stats.get("alertCount"))).append("\n");
            sb.append("- ").append(t.s("海域天气区域", "Weather regions")).append(t.colon()).append(num(stats.get("weatherCount"))).append("\n");
        } catch (Exception e) {
            sb.append("- ").append(t.s("基础统计暂时无法获取", "Basic stats unavailable")).append(" (").append(brief(e)).append(")\n");
        }

        // sensor_data 为亿级大表，用 information_schema 估算值代替 COUNT(*)
        try {
            Long rows = aiAssistantMapper.selectTableRowEstimate("sensor_data");
            sb.append("- ").append(t.s("传感器监测数据约 ", "Sensor readings: approx. "))
                    .append(num(rows))
                    .append(t.s(" 条（估算值，该表数据量极大，未做精确计数）", " (estimate; the table is huge, not counted exactly)"))
                    .append("\n");
        } catch (Exception e) {
            sb.append("- ").append(t.s("传感器监测数据暂时无法估算", "Sensor readings unavailable")).append(" (").append(brief(e)).append(")\n");
        }

        try {
            sb.append("- ").append(t.s("航线总数", "Total routes")).append(t.colon()).append(num(routeService.count())).append("\n");
            sb.append("- ").append(t.s("港口总数", "Total ports")).append(t.colon()).append(num(portService.count())).append("\n");
            sb.append("- ").append(t.s("告警规则", "Alert rules")).append(t.colon()).append(num(alertRuleService.count())).append("\n");
        } catch (Exception e) {
            sb.append("- ").append(t.s("其余统计暂时无法获取", "Other stats unavailable")).append(" (").append(brief(e)).append(")\n");
        }

        try {
            List<Map<String, Object>> dist = dashboardService.getShipTypeDistribution();
            if (dist != null && !dist.isEmpty()) {
                sb.append("\n").append(t.s("船型分布：", "Ship type distribution:")).append("\n");
                for (Map<String, Object> row : dist) {
                    sb.append("- ").append(t.shipType(str(row.get("shipType"))))
                            .append(t.colon()).append(num(row.get("count")));
                    if (!t.isEn()) {
                        sb.append(" 艘");
                    }
                    sb.append("\n");
                }
            }
        } catch (Exception e) {
            sb.append("\n").append(t.s("船型分布暂时无法获取", "Ship type distribution unavailable")).append(" (").append(brief(e)).append(")\n");
        }

        return new AiChatResponse(sb.toString().trim(), "overview", Arrays.asList(
                t.s("最近的告警有哪些？", "What are the latest alerts?"),
                t.s("船型分布情况如何？", "Ship type distribution?"),
                t.s("告警规则有哪些？", "What alert rules exist?")));
    }

    private AiChatResponse alertRules(String text, AiText t) {
        StringBuilder sb = new StringBuilder();

        // 支持按关键词过滤规则；英文提问先映射回库中的中文规则名片段
        String keyword = null;
        String zhKeyword = null;
        String[][] pairs = {
                {"主机", "engine"}, {"温度", "temperature"}, {"转速", "rpm"},
                {"油压", "oil pressure"}, {"排气", "exhaust"}, {"燃油", "fuel"},
                {"振动", "vibration"}, {"电压", "voltage"}, {"电流", "current"},
                {"功率", "power"}, {"海水", "sea water"}, {"航速", "speed"},
                {"压力", "pressure"}, {"电量", "battery"},
        };
        for (String[] pair : pairs) {
            if (text.contains(pair[0]) || lowerContains(text, pair[1])) {
                keyword = t.s(pair[0], pair[1]);
                zhKeyword = pair[0];
                break;
            }
        }

        LambdaQueryWrapper<AlertRule> wrapper = new LambdaQueryWrapper<>();
        if (zhKeyword != null) {
            wrapper.like(AlertRule::getRuleName, zhKeyword);
            sb.append(t.s("与\"" + keyword + "\"相关的告警规则：\n\n",
                    "Alert rules related to \"" + keyword + "\":\n\n"));
        } else {
            sb.append(t.s("当前配置的告警规则", "Configured alert rules"))
                    .append(t.maxRows(MAX_ROWS)).append(t.colonNl());
        }
        wrapper.orderByAsc(AlertRule::getTypeId).orderByAsc(AlertRule::getThresholdValue);
        wrapper.last("LIMIT " + MAX_ROWS);

        List<AlertRule> rules = alertRuleService.list(wrapper);
        if (rules.isEmpty()) {
            return new AiChatResponse(
                    zhKeyword != null
                            ? t.s("没有找到与\"" + keyword + "\"相关的告警规则。", "No alert rules match \"" + keyword + "\".")
                            : t.s("当前没有配置任何告警规则。", "No alert rules are configured."),
                    "alert_rule",
                    Arrays.asList(t.s("告警规则有哪些？", "What alert rules exist?"),
                            t.s("最近的告警有哪些？", "What are the latest alerts?")));
        }

        for (AlertRule rule : rules) {
            sb.append("- ").append(rule.getRuleName())
                    .append(t.sep()).append(t.s("类型ID ", "type ")).append(rule.getTypeId())
                    .append(t.sep()).append(t.s("条件 ", "when ")).append(rule.getOperator()).append(" ")
                    .append(plain(rule.getThresholdValue()))
                    .append(t.sep()).append(t.s("级别 ", "level ")).append(rule.getAlertLevel())
                    .append("\n");
        }

        long total = alertRuleService.count();
        sb.append("\n").append(t.s("规则总数：", "Total rules: ")).append(num(total)).append(t.s(" 条。", "."));

        return new AiChatResponse(sb.toString().trim(), "alert_rule", Arrays.asList(
                t.s("最近的告警有哪些？", "What are the latest alerts?"),
                t.s("主机温度的告警规则", "Engine temperature alert rules"),
                t.s("告警级别是怎么划分的？", "How are alert levels defined?")));
    }

    private AiChatResponse alerts(String text, AiText t) {
        ShipInfo ship = findShip(text);
        StringBuilder sb = new StringBuilder();

        // 指定了船舶：给出该船的告警汇总
        if (ship != null) {
            sb.append(t.s("船舶【", "Alerts for ship ")).append(ship.getShipName())
                    .append(t.s("】(IMO ", " (IMO ")).append(ship.getImo())
                    .append(t.s(") 的告警情况：\n\n", "):\n\n"));

            List<Map<String, Object>> summary = aiAssistantMapper.selectShipAlertSummary(ship.getId());
            long total = 0;
            for (Map<String, Object> row : summary) {
                long count = longVal(row.get("count"));
                total += count;
                sb.append("- ").append(t.handleStatus(intVal(row.get("handleStatus"))))
                        .append(t.colon()).append(num(count)).append("\n");
            }

            if (total == 0) {
                return new AiChatResponse(
                        t.s("船舶【" + ship.getShipName() + "】目前没有告警记录。",
                                "Ship " + ship.getShipName() + " currently has no alert records."),
                        "alert",
                        Arrays.asList(t.s("最近的告警有哪些？", "What are the latest alerts?"),
                                ship.getShipName() + t.s(" 现在什么状态？", " status?")));
            }

            sb.append("\n").append(t.s("合计 ", "Total ")).append(num(total)).append(t.s(" 条。", " alerts."));
            return new AiChatResponse(sb.toString().trim(), "alert", Arrays.asList(
                    ship.getShipName() + t.s(" 的最新传感器读数", " latest sensor readings"),
                    t.s("最近的告警有哪些？", "What are the latest alerts?")));
        }

        // 未指定船舶：区分"未处理"与"全部"
        Integer handleStatus = containsAny(text.toLowerCase(Locale.ROOT),
                "未处理", "待处理", "没处理", "unhandled", "pending", "open alert", "not handled") ? 0 : null;
        sb.append(handleStatus != null
                ? t.s("最近未处理的告警", "Recent unhandled alerts") + t.maxRows(MAX_ROWS) + t.colonNl()
                : t.s("最近的告警记录", "Recent alert records") + t.maxRows(MAX_ROWS) + t.colonNl());

        List<Map<String, Object>> records = aiAssistantMapper.selectRecentAlerts(handleStatus, MAX_ROWS);
        if (records.isEmpty()) {
            return new AiChatResponse(
                    handleStatus != null
                            ? t.s("当前没有未处理的告警，平台运行状态良好。", "No unhandled alerts. The platform is in good shape.")
                            : t.s("当前没有告警记录。", "No alert records found."),
                    "alert",
                    Arrays.asList(t.s("告警规则有哪些？", "What alert rules exist?"),
                            t.s("平台概况", "Platform overview")));
        }

        for (Map<String, Object> row : records) {
            sb.append("- ").append(dateTime(row.get("alertTime")))
                    .append(t.sep()).append(str(row.get("shipName")))
                    .append(t.sep()).append(str(row.get("ruleName")))
                    .append(t.sep()).append(t.s("触发值 ", "value ")).append(plain(row.get("triggerValue")))
                    .append(t.sep()).append(t.s("级别 ", "level ")).append(row.get("alertLevel"))
                    .append(t.sep()).append(t.handleStatus(intVal(row.get("handleStatus"))))
                    .append("\n");
        }

        return new AiChatResponse(sb.toString().trim(), "alert", Arrays.asList(
                t.s("未处理的告警有哪些？", "Any unhandled alerts?"),
                t.s("主机温度的告警规则", "Engine temperature alert rules")));
    }

    private AiChatResponse weather(String text, AiText t) {
        StringBuilder sb = new StringBuilder();

        LambdaQueryWrapper<WeatherRegion> wrapper = new LambdaQueryWrapper<>();

        // 关键词 -> 库中类型编码；英文提问先映射为中文关键词走同一编码表
        String keyword = null;   // 展示用
        String code = null;      // 查询用
        String[][] kw = {
                {"台风", "TYPHOON", "typhoon"}, {"雾", "FOG", "fog"},
                {"晴", "SUNNY", "sunny"}, {"雨", "RAIN", "rain"},
                {"阴", "OVERCAST", "overcast"}, {"大浪", "ROUGH_SEA", "wave"},
                {"极端", "EXTREME", "extreme"}, {"结冰", "FREEZING", "freezing"},
                {"风暴", "STORM", "storm"},
        };
        for (String[] k : kw) {
            if (text.contains(k[0]) || lowerContains(text, k[2])) {
                keyword = t.s(k[0], k[2]);
                code = k[1];
                break;
            }
        }

        if (code != null) {
            wrapper.like(WeatherRegion::getWeatherType, code);
        }

        wrapper.orderByDesc(WeatherRegion::getUpdateTime);
        wrapper.last("LIMIT " + MAX_ROWS);

        List<WeatherRegion> regions = weatherRegionService.list(wrapper);
        if (regions.isEmpty()) {
            return new AiChatResponse(
                    t.s("没有查询到符合条件的海域天气数据。", "No matching sea weather data found."),
                    "weather",
                    Arrays.asList(t.s("天气情况怎么样？", "How is the sea weather?"),
                            t.s("平台概况", "Platform overview")));
        }

        sb.append(keyword != null
                ? t.s("与\"" + keyword + "\"相关的海域天气", "Sea weather related to \"" + keyword + "\"")
                : t.s("最新的海域天气情况", "Latest sea weather"))
                .append(t.maxRows(MAX_ROWS)).append(t.colonNl());

        for (WeatherRegion w : regions) {
            sb.append("- ").append(w.getRegionName())
                    .append(t.sep()).append(t.weatherType(w.getWeatherType()))
                    .append(t.sep()).append(t.s("气温 ", "air ")).append(plain(w.getAirTemperature())).append("℃")
                    .append(t.sep()).append(t.s("水温 ", "sea ")).append(plain(w.getSeaTemperature())).append("℃")
                    .append(t.sep()).append(t.s("风速 ", "wind ")).append(plain(w.getWindSpeed())).append(" m/s")
                    .append(t.sep()).append(t.s("浪高 ", "wave ")).append(plain(w.getWaveHeight())).append(" m")
                    .append("\n");
        }

        return new AiChatResponse(sb.toString().trim(), "weather", Arrays.asList(
                t.s("有哪些台风海域？", "Which regions have typhoons?"),
                t.s("平台概况", "Platform overview")));
    }

    private AiChatResponse ports(String text, AiText t) {
        StringBuilder sb = new StringBuilder();

        LambdaQueryWrapper<Port> wrapper = new LambdaQueryWrapper<>();

        // 国家关键词：库中 country 列存英文名，查询统一映射为英文；展示按语言选择
        String queryName = null;
        String displayZh = null;
        String displayEn = null;
        String[][] countries = {
                {"中国", "China", "China"},
                {"美国", "美国|United States", "United States"},
                {"日本", "Japan", "Japan"},
                {"韩国", "Korea", "Korea"},
                {"新加坡", "Singapore", "Singapore"},
                {"澳大利亚", "澳大利亚|Australia", "Australia"},
                {"英国", "英国|United Kingdom", "United Kingdom"},
                {"德国", "Germany", "Germany"},
                {"法国", "France", "France"},
        };
        for (String[] c : countries) {
            if (text.contains(c[0]) || lowerContainsAny(text, c[1])) {
                queryName = c[2];
                displayZh = c[0];
                displayEn = c[2];
                break;
            }
        }

        if (queryName != null) {
            wrapper.like(Port::getCountry, queryName);
            sb.append(t.s(displayZh + " 的港口", "Ports in " + displayEn))
                    .append(t.s("（最多展示 " + MAX_ROWS + " 个）", " (up to " + MAX_ROWS + ")"))
                    .append(t.colonNl());
        } else {
            sb.append(t.s("港口信息", "Port information"))
                    .append(t.s("（最多展示 " + MAX_ROWS + " 个）", " (up to " + MAX_ROWS + ")"))
                    .append(t.colonNl());
        }
        wrapper.last("LIMIT " + MAX_ROWS);

        List<Port> ports = portService.list(wrapper);
        if (ports.isEmpty()) {
            return new AiChatResponse(
                    queryName != null
                            ? t.s("没有找到属于\"" + queryName + "\"的港口记录。", "No ports found in " + queryName + ".")
                            : t.s("当前没有港口数据。", "No port data available."),
                    "port",
                    Arrays.asList(t.s("港口有多少个？", "How many ports are there?"),
                            t.s("航线有哪些？", "What routes exist?")));
        }

        for (Port p : ports) {
            sb.append("- ").append(p.getPortName())
                    .append(t.sep()).append(p.getCountry())
                    .append(t.sep()).append(t.s("坐标 ", "pos ")).append(plain(p.getLatitude())).append(", ")
                    .append(plain(p.getLongitude()));
            if (p.getMaxDraft() != null) {
                sb.append(t.sep()).append(t.s("最大吃水 ", "max draft ")).append(plain(p.getMaxDraft())).append(" m");
            }
            sb.append("\n");
        }

        long total = portService.count();
        sb.append("\n").append(t.s("港口总数：", "Total ports: ")).append(num(total));

        return new AiChatResponse(sb.toString().trim(), "port", Arrays.asList(
                t.s("中国的港口有哪些？", "Ports in China"),
                t.s("航线有哪些？", "What routes exist?")));
    }

    private AiChatResponse routes(String text, AiText t) {
        StringBuilder sb = new StringBuilder();

        LambdaQueryWrapper<Route> wrapper = new LambdaQueryWrapper<>();
        String lower = text.toLowerCase(Locale.ROOT);

        if (containsAny(lower, "最长", "最远", "距离最大", "longest")) {
            wrapper.orderByDesc(Route::getDistanceNm);
            sb.append(t.s("航程最长的航线", "Longest routes"));
        } else if (containsAny(lower, "最短", "最近", "距离最小", "shortest")) {
            wrapper.orderByAsc(Route::getDistanceNm);
            sb.append(t.s("航程最短的航线", "Shortest routes"));
        } else {
            wrapper.orderByAsc(Route::getId);
            sb.append(t.s("航线信息", "Route information"));
        }
        sb.append(t.s("（最多展示 " + MAX_ROWS + " 条）", " (up to " + MAX_ROWS + ")")).append(t.colonNl());
        wrapper.last("LIMIT " + MAX_ROWS);

        List<Route> routes = routeService.list(wrapper);
        if (routes.isEmpty()) {
            return new AiChatResponse(
                    t.s("当前没有航线数据。", "No route data available."),
                    "route",
                    Arrays.asList(t.s("港口有多少个？", "How many ports are there?"),
                            t.s("平台概况", "Platform overview")));
        }

        for (Route r : routes) {
            sb.append("- ").append(r.getRouteName())
                    .append(t.sep()).append(t.s("航程 ", "distance ")).append(plain(r.getDistanceNm())).append(t.unitNmi())
                    .append(t.sep()).append(t.s("起始港ID ", "from ")).append(r.getStartPortId())
                    .append(t.sep()).append(t.s("目的港ID ", "to ")).append(r.getEndPortId())
                    .append("\n");
        }

        long total = routeService.count();
        sb.append("\n").append(t.s("航线总数：", "Total routes: ")).append(num(total));

        return new AiChatResponse(sb.toString().trim(), "route", Arrays.asList(
                t.s("最长的航线是哪条？", "Which is the longest route?"),
                t.s("港口有多少个？", "How many ports are there?")));
    }

    private AiChatResponse sensors(String text, AiText t) {
        ShipInfo ship = findShip(text);
        StringBuilder sb = new StringBuilder();

        if (ship == null) {
            // 未指定船舶：先介绍平台支持的传感器类型
            List<SensorDict> dicts = sensorDictService.list();
            if (dicts.isEmpty()) {
                return new AiChatResponse(
                        t.s("请告诉我要查询哪艘船的传感器数据，例如：远洋-00000号 的传感器读数。",
                                "Which ship? e.g. \"sensor readings of 远洋-00000\"."),
                        "sensor",
                        Arrays.asList("远洋-00000号 " + t.s("的最新传感器读数", "latest sensor readings"),
                                t.s("平台概况", "Platform overview")));
            }

            sb.append(t.s("平台目前支持以下 ", "The platform supports ")).append(dicts.size())
                    .append(t.s(" 种传感器类型：\n\n", " sensor types:\n\n"));
            for (SensorDict d : dicts) {
                sb.append("- ").append(t.sensorType(d.getTypeCode(), d.getTypeName()))
                        .append(" (").append(d.getTypeCode()).append(")");
                if (d.getDefaultUnit() != null && !d.getDefaultUnit().isBlank()) {
                    sb.append(t.sep()).append(t.s("单位 ", "unit ")).append(d.getDefaultUnit());
                }
                sb.append("\n");
            }
            sb.append("\n").append(t.s("如需查看具体船舶的实时读数，请带上船名或 IMO 编号提问，例如：远洋-00000号 的最新传感器读数。",
                    "To see live readings of a specific ship, include its name or IMO, e.g. \"latest readings of 远洋-00000\"."));

            return new AiChatResponse(sb.toString().trim(), "sensor", Arrays.asList(
                    "远洋-00000号 " + t.s("的最新传感器读数", "latest sensor readings"),
                    "远洋-00000 " + t.s("装了哪些传感器？", "sensors?")));
        }

        sb.append(t.s("船舶【", "Sensors of ship ")).append(ship.getShipName())
                .append(t.s("】(IMO ", " (IMO ")).append(ship.getImo())
                .append(t.s(") 的传感器情况：\n\n", "):\n\n"));

        LambdaQueryWrapper<SensorConfig> configWrapper = new LambdaQueryWrapper<>();
        configWrapper.eq(SensorConfig::getShipId, ship.getId());
        configWrapper.last("LIMIT 200");
        List<SensorConfig> configs = sensorConfigService.list(configWrapper);

        if (configs.isEmpty()) {
            sb.append(t.s("该船暂未配置任何传感器。", "No sensors configured for this ship."));
            return new AiChatResponse(sb.toString(), "sensor",
                    Arrays.asList(t.s("平台概况", "Platform overview"),
                            "远洋-00000 " + t.s("装了哪些传感器？", "sensors?")));
        }

        long online = configs.stream().filter(c -> c.getStatus() != null && c.getStatus() == 1).count();
        sb.append("- ").append(t.s("已安装传感器：", "Installed sensors: ")).append(num((long) configs.size()))
                .append(t.s("（在线 " + online + " 个，离线/故障 " + ((long) configs.size() - online) + " 个）\n\n",
                        " (online " + online + ", offline/faulty " + ((long) configs.size() - online) + "))\n\n"));

        // 最新读数：走 ship_id + recorded_at 联合索引，限制条数
        List<Map<String, Object>> readings = aiAssistantMapper.selectLatestReadings(ship.getId(), MAX_ROWS);
        if (readings.isEmpty()) {
            sb.append(t.s("暂无该船的传感器监测数据。", "No sensor readings recorded for this ship."));
            return new AiChatResponse(sb.toString().trim(), "sensor",
                    Arrays.asList(ship.getShipName() + t.s(" 现在什么状态？", " status?"),
                            t.s("平台概况", "Platform overview")));
        }

        sb.append(t.s("最新读数", "Latest readings")).append(t.maxRows(MAX_ROWS)).append(t.s("：\n", ":\n"));
        for (Map<String, Object> row : readings) {
            SensorDict dict = dictOf(intVal(row.get("typeId")));
            String typeName = dict != null
                    ? t.sensorType(dict.getTypeCode(), dict.getTypeName())
                    : str(row.get("sensorName"));
            String unit = dict != null ? dict.getDefaultUnit() : null;

            sb.append("- ").append(typeName)
                    .append(t.sep()).append(plain(row.get("dataValue")));
            if (unit != null && !unit.isBlank()) {
                sb.append(" ").append(unit);
            }
            sb.append(t.sep()).append(dateTime(row.get("recordedAt"))).append("\n");
        }

        return new AiChatResponse(sb.toString().trim(), "sensor", Arrays.asList(
                ship.getShipName() + t.s(" 现在什么状态？", " status?"),
                ship.getShipName() + t.s(" 有告警吗？", " alerts?")));
    }

    /**
     * 组装单艘船舶的完整档案，包含基础信息与实时航行状态。
     */
    private AiChatResponse shipDetail(ShipInfo ship, AiText t) {
        StringBuilder sb = new StringBuilder();

        sb.append(t.s("船舶【", "Ship profile ")).append(ship.getShipName())
                .append(t.s("】基本信息：\n\n", " (IMO " + ship.getImo() + "):\n\n"));
        sb.append("- IMO").append(t.colon()).append(ship.getImo()).append("\n");
        sb.append("- ").append(t.s("船型", "Type")).append(t.colon()).append(t.shipType(nvl(ship.getShipType()))).append("\n");
        sb.append("- ").append(t.s("船籍港", "Registry port")).append(t.colon()).append(nvl(ship.getRegistryPort())).append("\n");
        sb.append("- ").append(t.s("建造年份", "Built")).append(t.colon()).append(ship.getBuildYear() != null ? ship.getBuildYear() : t.missing()).append("\n");
        sb.append("- ").append(t.s("建造船厂", "Builder")).append(t.colon()).append(nvl(ship.getShipyardBuilder())).append("\n");
        sb.append("- ").append(t.s("运营公司", "Operator")).append(t.colon()).append(nvl(ship.getOperatingCompany())).append("\n");
        sb.append("- ").append(t.s("总长", "LOA")).append(t.colon()).append(plain(ship.getLengthOverall())).append(" m\n");
        sb.append("- ").append(t.s("型宽", "Beam")).append(t.colon()).append(plain(ship.getBeam())).append(" m\n");
        sb.append("- ").append(t.s("吃水", "Draft")).append(t.colon()).append(plain(ship.getDraft())).append(" m\n");
        sb.append("- ").append(t.s("排水量", "Displacement")).append(t.colon()).append(plain(ship.getDisplacement())).append(" t\n");
        sb.append("- ").append(t.s("主机型号", "Main engine")).append(t.colon()).append(nvl(ship.getMainEngineModel())).append("\n");
        sb.append("- ").append(t.s("设计航速", "Design speed")).append(t.colon()).append(plain(ship.getSailingSpeed())).append(" kn\n");
        sb.append("- ").append(t.s("检验有效期", "Survey valid until")).append(t.colon()).append(ship.getSurveyValidDate() != null
                ? ship.getSurveyValidDate() : t.missing()).append("\n");

        try {
            ShipRuntimeStatus rt = shipRuntimeStatusService.getById(ship.getId());
            if (rt != null) {
                sb.append("\n").append(t.s("实时状态：", "Live status:")).append("\n");
                sb.append("- ").append(t.s("航行状态", "Status")).append(t.colon()).append(t.sailingStatus(rt.getSailingStatus())).append("\n");
                sb.append("- ").append(t.s("位置", "Position")).append(t.colon()).append(plain(rt.getLatitude())).append(", ")
                        .append(plain(rt.getLongitude())).append("\n");
                sb.append("- ").append(t.s("当前航速", "Speed")).append(t.colon()).append(plain(rt.getSpeed())).append(" kn\n");
                sb.append("- ").append(t.s("当前航向", "Heading")).append(t.colon()).append(plain(rt.getHeading())).append("°\n");
                sb.append("- ").append(t.s("发动机负载", "Engine load")).append(t.colon()).append(plain(rt.getEngineLoad())).append("%\n");
                sb.append("- ").append(t.s("剩余燃油", "Fuel remaining")).append(t.colon()).append(plain(rt.getFuelPercent())).append("%\n");
                sb.append("- ").append(t.s("更新时间", "Updated")).append(t.colon()).append(dateTime(rt.getLastUpdate())).append("\n");

                if (rt.getWeatherId() != null) {
                    WeatherRegion w = weatherRegionService.getById(rt.getWeatherId());
                    if (w != null) {
                        sb.append("- ").append(t.s("所在海域", "Weather region")).append(t.colon()).append(w.getRegionName())
                                .append(" (").append(t.weatherType(w.getWeatherType())).append(")\n");
                    }
                }
            } else {
                sb.append("\n").append(t.s("该船暂无实时状态数据。", "No live status data for this ship."));
            }
        } catch (Exception e) {
            sb.append("\n").append(t.s("实时状态暂时无法获取", "Live status unavailable")).append(" (").append(brief(e)).append(")");
        }

        return new AiChatResponse(sb.toString().trim(), "ship", Arrays.asList(
                ship.getShipName() + t.s(" 的最新传感器读数", " latest sensor readings"),
                ship.getShipName() + t.s(" 有告警吗？", " alerts?")));
    }

    private AiChatResponse ships(String text, AiText t) {
        ShipInfo ship = findShip(text);

        if (ship != null) {
            return shipDetail(ship, t);
        }

        StringBuilder sb = new StringBuilder();
        LambdaQueryWrapper<ShipInfo> wrapper = new LambdaQueryWrapper<>();

        // 船型筛选：库中 ship_type 存中文，英文提问词映射回中文再查
        String shipType = null;
        String shipTypeZh = null;
        String[][] types = {
                {"散货船", "bulk carrier"}, {"集装箱船", "container"}, {"油轮", "tanker"},
                {"客轮", "passenger"}, {"化学品船", "chemical"}, {"滚装船", "ro-ro"},
                {"拖轮", "tug"}, {"LNG船", "lng"}, {"渔船", "fishing"}, {"科考船", "research"},
        };
        for (String[] ty : types) {
            if (text.contains(ty[0]) || lowerContains(text, ty[1])) {
                shipType = t.s(ty[0], ty[1]);
                shipTypeZh = ty[0];
                break;
            }
        }

        if (shipTypeZh != null) {
            wrapper.like(ShipInfo::getShipType, shipTypeZh);
            sb.append(t.s("船型为\"" + shipType + "\"的船舶", "Ships of type \"" + shipType + "\""));
        } else {
            sb.append(t.s("平台船舶列表", "Ship list"));
        }
        sb.append(t.s("（最多展示 " + MAX_ROWS + " 艘）", " (up to " + MAX_ROWS + ")")).append(t.colonNl());
        wrapper.orderByAsc(ShipInfo::getId);
        wrapper.last("LIMIT " + MAX_ROWS);

        List<ShipInfo> ships = shipInfoService.list(wrapper);
        if (ships.isEmpty()) {
            return new AiChatResponse(
                    shipTypeZh != null
                            ? t.s("没有找到船型为\"" + shipType + "\"的船舶。", "No ships of type \"" + shipType + "\".")
                            : t.s("当前没有船舶数据。", "No ship data available."),
                    "ship",
                    Arrays.asList(t.s("平台有多少艘船？", "How many ships are there?"),
                            t.s("船型分布情况如何？", "Ship type distribution?")));
        }

        for (ShipInfo s : ships) {
            sb.append("- ").append(s.getShipName())
                    .append(t.sep()).append("IMO ").append(s.getImo())
                    .append(t.sep()).append(t.shipType(nvl(s.getShipType())))
                    .append(t.sep()).append(t.s("船籍港 ", "registry ")).append(nvl(s.getRegistryPort()))
                    .append("\n");
        }

        long total = shipInfoService.count();
        sb.append("\n").append(t.s("船舶总数：", "Total ships: ")).append(num(total))
                .append(t.s("。如需查看某艘船的详情，请提供船名或 IMO 编号。", ". For details, provide a ship name or IMO."));

        return new AiChatResponse(sb.toString().trim(), "ship", Arrays.asList(
                "远洋-00000 " + t.s("的基本情况", "profile"),
                t.s("船型分布情况如何？", "Ship type distribution?")));
    }

    // ==================== 辅助方法 ====================

    /**
     * 船名匹配模式。库中船名形如「远洋-00000号」，
     * 用该模式从问题中精确截出船名片段，避免把"的基本情况"等尾随文字带进查询条件。
     */
    private static final Pattern SHIP_NAME_PATTERN =
            Pattern.compile("[\\u4e00-\\u9fa5A-Za-z]+-\\d+号?");

    private ShipInfo findShip(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }

        Matcher matcher = SHIP_NAME_PATTERN.matcher(text);
        while (matcher.find()) {
            ShipInfo hit = matchShipByName(matcher.group());
            if (hit != null) {
                return hit;
            }
        }

        // 提取连续 7 位数字作为 IMO 精确匹配
        String digits = text.replaceAll("\\D+", " ").trim();
        for (String token : digits.split(" ")) {
            if (token.length() == 7) {
                ShipInfo byImo = matchShipByImo(token);
                if (byImo != null) {
                    return byImo;
                }
            }
        }

        return matchShipByName(text.trim());
    }

    private ShipInfo matchShipByImo(String imo) {
        LambdaQueryWrapper<ShipInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShipInfo::getImo, imo);
        wrapper.last("LIMIT 1");
        List<ShipInfo> found = shipInfoService.list(wrapper);
        return found.isEmpty() ? null : found.get(0);
    }

    private ShipInfo matchShipByName(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }

        LambdaQueryWrapper<ShipInfo> exact = new LambdaQueryWrapper<>();
        exact.eq(ShipInfo::getShipName, name);
        exact.last("LIMIT 1");
        List<ShipInfo> exactList = shipInfoService.list(exact);
        if (!exactList.isEmpty()) {
            return exactList.get(0);
        }

        LambdaQueryWrapper<ShipInfo> fuzzy = new LambdaQueryWrapper<>();
        fuzzy.like(ShipInfo::getShipName, name);
        fuzzy.orderByAsc(ShipInfo::getId);
        fuzzy.last("LIMIT 1");
        List<ShipInfo> fuzzyList = shipInfoService.list(fuzzy);
        return fuzzyList.isEmpty() ? null : fuzzyList.get(0);
    }

    private SensorDict dictOf(Integer typeId) {
        if (typeId == null) {
            return null;
        }
        SensorDict cached = dictCache.get(typeId);
        if (cached != null) {
            return cached;
        }
        try {
            SensorDict dict = sensorDictService.getById(typeId);
            if (dict != null) {
                dictCache.put(typeId, dict);
            }
            return dict;
        } catch (Exception e) {
            return null;
        }
    }

    private boolean containsAny(String text, String... keys) {
        for (String key : keys) {
            if (text.contains(key.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 英文关键词整词匹配：要求关键词前后不是字母或数字，
     * 避免 "supported" 命中 "port"、"relationship" 命中 "ship" 这类子串误判。
     * 调用方需传入已小写的文本；多词短语（如 "what can you"）同样适用词边界。
     */
    private boolean containsWord(String lower, String... words) {
        for (String word : words) {
            String w = word.toLowerCase(Locale.ROOT);
            if (w.isEmpty()) {
                continue;
            }
            Matcher m = Pattern.compile("(?<![a-z0-9])" + Pattern.quote(w) + "(?![a-z0-9])").matcher(lower);
            if (m.find()) {
                return true;
            }
        }
        return false;
    }

    private boolean lowerContains(String text, String key) {
        return text.toLowerCase(Locale.ROOT).contains(key.toLowerCase(Locale.ROOT));
    }

    /** key 支持用 | 分隔的多个候选。 */
    private boolean lowerContainsAny(String text, String keys) {
        String lower = text.toLowerCase(Locale.ROOT);
        for (String k : keys.split("\\|")) {
            if (lower.contains(k)) {
                return true;
            }
        }
        return false;
    }

    private List<String> helpSuggestions(AiText t) {
        return new ArrayList<>(Arrays.asList(
                t.s("平台有多少艘船？", "How many ships are there?"),
                t.s("最近的告警有哪些？", "What are the latest alerts?"),
                "远洋-00000 " + t.s("现在什么状态？", "status?"),
                t.s("传感器类型有哪些？", "Which sensor types are supported?")));
    }

    private String plain(Object value) {
        if (value == null) {
            return "N/A";
        }
        if (value instanceof BigDecimal) {
            BigDecimal bd = ((BigDecimal) value).stripTrailingZeros();
            return bd.toPlainString();
        }
        if (value instanceof Double) {
            double d = (Double) value;
            if (d == Math.floor(d) && !Double.isInfinite(d)) {
                return String.valueOf((long) d);
            }
            return String.format(Locale.ROOT, "%.2f", d);
        }
        return String.valueOf(value);
    }

    private String num(Object value) {
        if (value == null) {
            return "0";
        }
        return String.valueOf(value);
    }

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String dateTime(Object value) {
        if (value == null) {
            return "N/A";
        }
        if (value instanceof LocalDateTime) {
            return ((LocalDateTime) value).format(TIME_FORMATTER);
        }
        if (value instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) value).toLocalDateTime().format(TIME_FORMATTER);
        }
        return String.valueOf(value).replace('T', ' ');
    }

    private String str(Object value) {
        return value == null ? "N/A" : String.valueOf(value);
    }

    private String nvl(String value) {
        return value == null || value.isBlank() ? "N/A" : value;
    }

    private long longVal(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return value == null ? 0L : Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    private Integer intVal(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return value == null ? null : Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String brief(Exception e) {
        String msg = e.getMessage();
        if (msg == null || msg.isBlank()) {
            return e.getClass().getSimpleName();
        }
        return truncate(msg.split("\n")[0], 80);
    }

    private String truncate(String text, int max) {
        if (text == null) {
            return "";
        }
        return text.length() <= max ? text : text.substring(0, max) + "...";
    }
}
