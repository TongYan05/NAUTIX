package shipsensor.service;

import org.springframework.stereotype.Component;
import shipsensor.dto.AiChatResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 日常生活问答助手（中英双语）。
 *
 * 作为平台业务问答之外的通用兜底能力：回答时间日期、四则运算、常用单位换算、
 * BMI 计算以及高频生活常识类问题。全部逻辑在本地完成，不依赖外部大模型服务。
 *
 * 匹配原则：只在问题不含平台业务关键词时由调用方路由进来，
 * 避免"平台有多少艘船"这类问题被误判为数字计算。
 */
@Component
public class DailyLifeAssistant {

    // ==================== 时间日期 ====================

    private static final Pattern TIME_PATTERN = Pattern.compile(
            "(现在|当前)?(几[点时]|什么时间|现在时间|时间[了吧吗])|what time|current time|time now|time is it");

    private static final Pattern DATE_PATTERN = Pattern.compile(
            "今天.{0,3}(几号|几日|什么日期|多少号)|what'?s? the date|date today|today'?s date|what date");

    private static final Pattern WEEKDAY_PATTERN = Pattern.compile(
            "(今天|现在|明天|后天|昨天).{0,3}(星期|周|礼拜)[几多少]?|星期几|周几|礼拜几|what day is it|what day|day of the week|what'?s today");

    // ==================== 四则运算 ====================

    private static final Pattern CALC_HINT_PATTERN = Pattern.compile(
            "[0-9.]+\\s*(加|减|乘|除|乘以|除以|\\+|-|\\*|×|÷|x)\\s*[0-9.]+|[0-9.]+\\s*的\\s*[0-9.]+\\s*百分之|[0-9.]+\\s*%\\s*(是|of)?|[0-9.]+\\s*[+%\\-×÷*/]\\s*[0-9.]+");

    private static final Pattern CALC_WORD_PATTERN = Pattern.compile(
            "(times|multiplied by|divided by|plus|minus)\\s*[0-9.]+");

    // ==================== 单位换算 ====================

    private static final Pattern CONVERT_PATTERN = Pattern.compile(
            "([0-9]+(?:\\.[0-9]+)?)\\s*" +
            "(公里|千米|km|meters?|metres?|米|m|厘米|cm|毫米|mm|英里|mile|miles|海里|节|英尺|feet|foot|ft|inch|inches|英寸|公斤|千克|kg|kilograms?|斤|克|g|grams?|磅|lb|lbs|pounds?|盎司|oz|ounces?|吨|t|升|l|ml|毫升|摄氏度|℃|度|华氏度|℉|fahrenheit|celsius|平方米|平米|㎡|acres?|亩)" +
            "\\s*(?:度|等于|转换成?|换算成?|转为|转成|=|是多少|多少|to|into|in)?\\s*" +
            "(英里|mile|miles|公里|千米|km|米|meters?|metres?|m|英尺|feet|foot|ft|英寸|inch|inches|公斤|千克|kg|斤|磅|lb|lbs|pounds?|盎司|oz|克|g|升|l|毫升|ml|华氏度|℉|fahrenheit|摄氏度|℃|celsius|平方米|平米|㎡|acres?|亩)?");

    // ==================== BMI ====================

    private static final Pattern BMI_HEIGHT_PATTERN = Pattern.compile(
            "([0-9]{2,3}(?:\\.[0-9]+)?)\\s*(?:cm|厘米|公分|centimeters?|centimetres?)");
    private static final Pattern BMI_WEIGHT_KG_PATTERN = Pattern.compile(
            "([0-9]{2,3}(?:\\.[0-9]+)?)\\s*(?:kg|公斤|千克|kilograms?|kilos?)");
    private static final Pattern BMI_WEIGHT_JIN_PATTERN = Pattern.compile("([0-9]{2,3}(?:\\.[0-9]+)?)\\s*斤");

    // ==================== 生活常识知识库（中英双语） ====================

    /** keys：命中词（含中英，全部小写匹配）；answers：[中文答案, 英文答案]。 */
    private record KnowledgeEntry(List<String> keys, String zh, String en) {
    }

    private final List<KnowledgeEntry> knowledge = Arrays.asList(
            new KnowledgeEntry(
                    Arrays.asList("失眠", "睡不着", "入睡困难", "睡眠质量", "insomnia", "can'?t sleep", "sleep well", "sleep tips"),
                    "改善睡眠的常见建议：\n" +
                    "1. 固定作息，每天同一时间起床和睡觉，周末也别差太多；\n" +
                    "2. 睡前 1 小时远离手机和电脑屏幕，可调暗灯光；\n" +
                    "3. 下午 3 点后避免咖啡、浓茶；睡前别饮酒、别吃太饱；\n" +
                    "4. 床只用来睡觉，躺 20 分钟睡不着就起身做点放松的事，有困意再回床；\n" +
                    "5. 卧室保持安静、凉爽（18~22℃）和黑暗。\n" +
                    "如果失眠每周超过 3 晚并持续 1 个月以上，建议就医评估。",
                    "Common tips for better sleep:\n" +
                    "1. Keep a consistent schedule — same wake-up and bed time, even on weekends.\n" +
                    "2. Avoid screens for 1 hour before bed; dim the lights.\n" +
                    "3. Cut coffee and strong tea after 3 pm; avoid alcohol and heavy meals at night.\n" +
                    "4. Use the bed only for sleep. If you can't fall asleep in ~20 minutes, get up and relax until sleepy.\n" +
                    "5. Keep the bedroom quiet, dark and cool (18–22°C).\n" +
                    "If insomnia happens 3+ nights a week for over a month, consider seeing a doctor."),
            new KnowledgeEntry(
                    Arrays.asList("喝水", "喝多少水", "饮水量", "补水", "how much water", "drink water", "hydration"),
                    "一般成年人每天建议饮水 1500~1700 毫升（约 7~8 杯），高温、运动、发烧时酌量增加。" +
                    "少量多次比一次猛喝更利于吸收；晨起一杯温水有助于补充夜间流失的水分。判断是否喝够，最简单的标准是尿液呈淡黄色。",
                    "Most adults need about 1.5–1.7 litres of fluids a day (roughly 7–8 cups), more in heat, during exercise, or with a fever. " +
                    "Sipping regularly works better than drinking a lot at once. A simple check: pale-yellow urine usually means you're well hydrated."),
            new KnowledgeEntry(
                    Arrays.asList("久坐", "坐多久", "站起来活动", "sedentary", "sit too long", "desk work"),
                    "建议每坐 45~60 分钟起身活动 3~5 分钟，做做伸展、走动接水。" +
                    "久坐会减慢下肢血液循环、增加腰椎负担，屏幕前记得保持屏幕上沿与视线齐平、腰背有支撑。",
                    "Stand up and move for 3–5 minutes every 45–60 minutes of sitting. " +
                    "Prolonged sitting slows circulation and strains the lower back. Keep the top of your screen at eye level and your back supported."),
            new KnowledgeEntry(
                    Arrays.asList("运动", "锻炼", "健身", "跑步", "exercise", "workout", "running", "gym"),
                    "世界卫生组织建议成年人每周至少 150 分钟中等强度有氧运动（如快走、骑车、游泳），" +
                    "外加 2 次肌肉力量训练。循序渐进最重要：刚开始可以从每天快走 20~30 分钟做起，" +
                    "运动前后各留 5 分钟热身和拉伸，出现明显心肺不适要立即停止。",
                    "WHO recommends at least 150 minutes of moderate cardio per week (brisk walking, cycling, swimming) plus 2 strength sessions. " +
                    "Build up gradually — 20–30 minutes of brisk walking a day is a great start. Warm up and stretch, and stop if you feel chest pain or severe breathlessness."),
            new KnowledgeEntry(
                    Arrays.asList("感冒", "着凉", "流鼻涕", "打喷嚏", "cold", "flu", "runny nose", "sneezing"),
                    "普通感冒多为病毒感染，一般 5~7 天自愈。可做的是：多休息、多喝水、" +
                    "咽痛可用温盐水漱口、鼻塞可用热蒸汽熏蒸；发烧或头痛可按说明书服用对乙酰氨基酚或布洛芬。" +
                    "抗生素对病毒无效，不要自行服用。若高烧超过 3 天、呼吸困难或症状明显加重，请及时就医。",
                    "The common cold is usually viral and clears in 5–7 days. Rest, fluids, warm salt-water gargles for sore throat and steam for congestion all help. " +
                    "Paracetamol or ibuprofen can ease fever/headache — follow the label. Antibiotics don't work on viruses. See a doctor if high fever lasts over 3 days or breathing becomes difficult."),
            new KnowledgeEntry(
                    Arrays.asList("发烧", "发热", "体温", "fever", "temperature is high"),
                    "成人腋下体温超过 37.3℃ 视为发热。38.5℃ 以下且精神尚可时，以物理降温为主：减少衣物、温水擦拭、多饮水；" +
                    "超过 38.5℃ 可按说明书服用退烧药。持续高热 3 天以上、出现意识模糊或抽搐等情况应立即就医。",
                    "A fever is roughly above 37.3°C (armpit). Below 38.5°C, if you feel okay, use physical cooling: lighter clothing, lukewarm wiping, plenty of fluids. " +
                    "Above 38.5°C you may take fever medication as directed. Seek urgent care for fever lasting 3+ days, confusion, or seizures."),
            new KnowledgeEntry(
                    Arrays.asList("煮饭", "米饭", "米水比例", "电饭煲", "cook rice", "rice cooker"),
                    "电饭煲煮白米饭的米水比大约 1 : 1.1~1.2（体积比），新米少放、陈米多放一点；" +
                    "米提前浸泡 20 分钟口感更松软；煮好后焖 5~10 分钟再开盖、打松。",
                    "For a rice cooker, use roughly a 1 : 1.1–1.2 rice-to-water ratio by volume; older rice needs a bit more water. " +
                    "Soaking 20 minutes makes it fluffier. Let it rest 5–10 minutes after cooking, then fluff."),
            new KnowledgeEntry(
                    Arrays.asList("鸡蛋", "煮蛋", "溏心", "boil egg", "boiled egg", "soft boiled"),
                    "冷水下锅、水开后计时：3 分钟流心、5 分钟溏心、8 分钟全熟。" +
                    "煮好立刻捞出过凉水，更好剥壳。",
                    "Start in cold water, then time from boiling: 3 min runny yolk, 5 min soft-boiled, 8 min fully set. " +
                    "Cool in ice water right after for easier peeling."),
            new KnowledgeEntry(
                    Arrays.asList("牛奶", "喝奶", "补钙", "milk", "calcium"),
                    "《中国居民膳食指南》建议每天摄入 300~500 毫升液态奶或相当量的奶制品，" +
                    "是性价比最高的补钙方式。乳糖不耐受可以选择酸奶或无乳糖牛奶。",
                    "Dietary guidelines suggest 300–500 ml of milk (or equivalent dairy) per day — one of the most cost-effective calcium sources. " +
                    "If you're lactose intolerant, try yoghurt or lactose-free milk."),
            new KnowledgeEntry(
                    Arrays.asList("熬夜", "几点睡", "睡眠时间", "stay up late", "how much sleep", "sleep schedule"),
                    "成年人建议每晚睡 7~9 小时，尽量在 23 点前入睡。" +
                    "长期熬夜会打乱生物钟，影响记忆、情绪和代谢；如果不得不晚睡，第二天别靠过量咖啡因硬撑，可以补 20~30 分钟午觉。",
                    "Adults should sleep 7–9 hours per night, ideally falling asleep before 23:00. " +
                    "Chronic late nights disrupt memory, mood and metabolism. After a late night, a 20–30 minute nap helps — don't overdo caffeine."),
            new KnowledgeEntry(
                    Arrays.asList("眼睛", "用眼", "近视", "屏幕", "eye strain", "myopia", "screen time", "digital eyes"),
                    "遵循 20-20-20 法则：每看屏幕 20 分钟，向 20 英尺（约 6 米）外远眺 20 秒。" +
                    "屏幕亮度与环境光接近，多眨眼防干涩；每天 2 小时以上的户外活动对预防青少年近视很有帮助。",
                    "Follow the 20-20-20 rule: every 20 minutes at a screen, look at something 20 feet (6 m) away for 20 seconds. " +
                    "Match screen brightness to the room, blink often, and spend 2+ hours outdoors daily to help prevent myopia in children."),
            new KnowledgeEntry(
                    Arrays.asList("减肥", "减脂", "体重管理", "weight loss", "lose weight", "dieting", "get fit"),
                    "健康减重的核心是热量缺口：每天比消耗少摄入 300~500 千卡，配合每周 150 分钟以上运动。" +
                    "优先减少含糖饮料、油炸食品和夜宵；蛋白质吃够（约每公斤体重 1.2~1.6 克）更抗饿、保肌肉。" +
                    "不建议极端节食，每周减 0.5~1 公斤是可持续的速度。",
                    "Sustainable weight loss comes from a modest calorie deficit (300–500 kcal/day) plus 150+ minutes of weekly activity. " +
                    "Cut sugary drinks, fried food and late-night snacks first; keep protein high (~1.2–1.6 g per kg body weight) to stay full and protect muscle. " +
                    "Avoid crash diets — 0.5–1 kg per week is a durable pace."),
            new KnowledgeEntry(
                    Arrays.asList("咖啡", "咖啡因", "coffee", "caffeine"),
                    "健康成年人每天咖啡因摄入建议不超过 400 毫克（约 2~3 杯美式）。" +
                    "下午 2~3 点后尽量别喝，半衰期约 5 小时，太晚会影响入睡；空腹喝容易刺激胃。",
                    "Healthy adults should stay under ~400 mg of caffeine a day (about 2–3 Americanos). " +
                    "Avoid it after 2–3 pm — its half-life is ~5 hours and it can disturb sleep. Drinking on an empty stomach may irritate it."),
            new KnowledgeEntry(
                    Arrays.asList("酒", "喝酒", "解酒", "alcohol", "hangover", "drinking"),
                    "最健康的饮酒量是零。若难免应酬：不要空腹喝、控制总量（男性一天酒精不超过 25 克）、多喝水加速代谢。" +
                    "所谓解酒药没有循证依据，时间才是唯一的解药。",
                    "The healthiest amount of alcohol is none. If you do drink: never on an empty stomach, keep totals low, and alternate with water. " +
                    "Sober-up remedies have no real evidence — only time works."),
            new KnowledgeEntry(
                    Arrays.asList("晒被", "除螨", "螨虫", "sun duvet", "dust mite"),
                    "紫外线真正起主要作用的是高温和干燥：晴天把被子正反各晒 2~3 小时，" +
                    "收回来后轻轻拍打去除尘螨尸体碎屑；不能日晒的可以用烘干机高温档 20 分钟替代。",
                    "It's heat and dryness that kill dust mites, not UV directly. Sun the duvet 2–3 hours per side on a clear day and beat it gently afterwards. " +
                    "A hot dryer cycle for ~20 minutes is a good alternative."),
            new KnowledgeEntry(
                    Arrays.asList("快递", "几天到", "delivery time", "how long does delivery"),
                    "国内普通快递一般 2~4 天，同城多数次日达；具体时效与发货地、快递公司和天气有关。" +
                    "这个问题涉及实时物流，建议以寄件平台或快递单号查询结果为准。",
                    "Standard parcel delivery is typically 2–4 days domestically, often next-day within the same city. " +
                    "Exact timing depends on origin, carrier and weather — check the tracking number for the live status."),
            new KnowledgeEntry(
                    Arrays.asList("今天天气", "明天下雨", "带伞", "weather forecast", "rain tomorrow", "umbrella"),
                    "这个问题需要查询实时气象信息，我目前没有接入天气服务，无法给出准确预报。" +
                    "建议直接看手机天气应用。如果问的是平台传感器库里的海域天气数据，可以直接说\"海域天气怎么样\"。",
                    "That needs live weather data, which I'm not connected to, so I can't give a forecast — your phone weather app is the fastest route. " +
                    "If you meant the platform's sea-weather dataset, just ask \"how is the sea weather?\"."),
            new KnowledgeEntry(
                    Arrays.asList("怎么做菜", "菜谱", "做饭", "recipe", "how to cook", "cooking"),
                    "家常菜通用公式：热锅凉油，葱姜蒜爆香，主料大火快炒，出锅前盐、生抽调味。" +
                    "番茄炒蛋示例：2 个鸡蛋加几滴水炒至半凝固盛出，1 个番茄炒出汁，加半勺糖一勺盐，回锅翻匀即可。",
                    "A universal stir-fry formula: hot wok, cold oil, aromatics first (ginger/scallion/garlic), high heat on the main ingredient, season at the end with salt and light soy sauce. " +
                    "Example — scrambled eggs with tomato: fry 2 eggs with a splash of water until half set, remove; cook 1 tomato until juicy, add ½ tsp sugar and 1 tsp salt, return eggs and toss."),
            new KnowledgeEntry(
                    Arrays.asList("垃圾分类", "垃圾怎么分", "recycling", "rubbish", "garbage sort"),
                    "按国内常见四分法：可回收物（纸类、塑料瓶、金属、玻璃）、" +
                    "厨余/湿垃圾（剩菜剩饭、果皮菜叶）、有害垃圾（电池、药品、灯管）、其他/干垃圾（用过的纸巾、一次性餐具、烟蒂）。" +
                    "各城市细则略有差异，以当地规定为准。",
                    "Australia commonly uses a four-bin system: yellow-lid recycling (paper, plastic, metal, glass), green-lid organics/garden, " +
                    "red-lid general waste, plus soft-plastic and e-waste drop-offs. China uses recyclable / kitchen / hazardous / other. " +
                    "Always follow your local council's rules — they vary."),
            new KnowledgeEntry(
                    Arrays.asList("时差", "悉尼时间", "澳洲时间", "time difference", "sydney time", "australia time", "beijing time"),
                    "悉尼（新南威尔士）时间：冬令时比北京时间快 2 小时，夏令时（约 10 月首个周日至 4 月首个周日）快 3 小时。" +
                    "现在是 " + nowSydney(),
                    "Sydney (NSW) runs 2 hours ahead of Beijing in winter, and 3 hours ahead during daylight saving (first Sunday of October to first Sunday of April). " +
                    "Right now in Sydney it's " + nowSydney()),
            new KnowledgeEntry(
                    Arrays.asList("签证", "打工度假", "485", "visa"),
                    "签证政策变化频繁，我无法给出可靠的实时信息。" +
                    "建议直接查询澳大利亚内政部（Home Affairs）或中国外交部的官方页面，以官方信息为准。",
                    "Visa policies change often and I can't give reliable live info. " +
                    "Please check the official Australian Department of Home Affairs website for authoritative details.")
    );

    private String nowSydney() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    // ==================== 对外入口 ====================

    /**
     * 判断问题是否属于日常生活范畴（中英问法均可）。
     */
    public boolean matches(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }
        String lower = text.toLowerCase(Locale.ROOT);

        if (TIME_PATTERN.matcher(lower).find()
                || DATE_PATTERN.matcher(lower).find()
                || WEEKDAY_PATTERN.matcher(lower).find()) {
            return true;
        }
        if (lower.contains("计算") || lower.contains("等于多少") || lower.contains("多少钱")
                || lower.contains("百分之") || lower.contains("%")
                || lower.contains("calculate") || lower.contains("compute")
                || lower.contains("equals") || lower.contains("what is ") && CALC_WORD_PATTERN.matcher(lower).find()
                || CALC_HINT_PATTERN.matcher(lower).find()) {
            return true;
        }
        if (isConvertQuery(lower)) {
            return true;
        }
        if (lower.contains("bmi") || lower.contains("体重指数")) {
            return true;
        }
        for (KnowledgeEntry entry : knowledge) {
            for (String k : entry.keys()) {
                if (lower.contains(k)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 回答日常生活问题；无法归类时返回 null，由调用方继续走兜底逻辑。
     *
     * @param lang "zh"（默认）或 "en"
     */
    public AiChatResponse answer(String text, String lang) {
        if (text == null || text.isBlank()) {
            return null;
        }
        AiText t = AiText.of(lang);
        String lower = text.toLowerCase(Locale.ROOT);

        // 1) 时间日期类优先（短句，避免与业务数据混淆）
        if (WEEKDAY_PATTERN.matcher(lower).find()) {
            return weekday(t);
        }
        if (TIME_PATTERN.matcher(lower).find() && !lower.contains("数据") && !lower.contains("sensor")) {
            return timeNow(t);
        }
        if (DATE_PATTERN.matcher(lower).find() && !lower.contains("数据") && !lower.contains("sensor")) {
            return dateNow(t);
        }

        // 2) BMI
        if (lower.contains("bmi") || lower.contains("体重指数")) {
            AiChatResponse bmi = bmi(text, t);
            if (bmi != null) {
                return bmi;
            }
        }

        // 3) 单位换算
        AiChatResponse conv = convert(lower, t);
        if (conv != null) {
            return conv;
        }

        // 4) 算术计算
        if (lower.contains("计算") || lower.contains("等于多少") || lower.contains("百分之")
                || lower.contains("%") || lower.contains("calculate") || lower.contains("compute")
                || CALC_HINT_PATTERN.matcher(lower).find() || CALC_WORD_PATTERN.matcher(lower).find()) {
            AiChatResponse calc = calculate(text, t);
            if (calc != null) {
                return calc;
            }
        }

        // 5) 常识库
        for (KnowledgeEntry entry : knowledge) {
            for (String k : entry.keys()) {
                if (lower.contains(k)) {
                    return new AiChatResponse(t.s(entry.zh(), entry.en()), "daily_life",
                            Arrays.asList(t.s("现在几点了？", "What time is it?"),
                                    t.s("128乘以7等于多少？", "What is 128 * 7?"),
                                    t.s("5公里是多少英里？", "How many miles is 5 km?")));
                }
            }
        }

        return null;
    }

    /**
     * 无法命中具体条目时给出的友好引导。
     */
    public AiChatResponse smallTalkFallback(String text, String lang) {
        AiText t = AiText.of(lang);
        return new AiChatResponse(
                t.s("这个问题我想了一下，暂时没有可靠的答案。\n\n" +
                        "我能帮上的忙包括：\n" +
                        "- 平台数据：船舶、传感器、告警、航线、港口、海域天气\n" +
                        "- 日常生活：现在几点、今天星期几、算术计算、单位换算、BMI、健康常识\n\n" +
                        "换个问法试试？也可以输入\"帮助\"查看完整提问示例。",
                        "I thought about that one, but I don't have a reliable answer.\n\n" +
                        "Here's what I can help with:\n" +
                        "- Platform data: ships, sensors, alerts, routes, ports, sea weather\n" +
                        "- Daily life: time, dates, arithmetic, unit conversion, BMI, health tips\n\n" +
                        "Try rephrasing, or type \"help\" for the full list of examples."),
                "unknown",
                Arrays.asList(t.s("现在几点了？", "What time is it?"),
                        t.s("今天星期几？", "What day is it?"),
                        t.s("128乘以7等于多少？", "What is 128 * 7?")));
    }

    // ==================== 时间 ====================

    private AiChatResponse timeNow(AiText t) {
        LocalDateTime now = LocalDateTime.now();
        String clock = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String date = now.format(DateTimeFormatter.ofPattern(
                t.isEn() ? "EEEE, yyyy-MM-dd" : "yyyy年M月d日 EEEE",
                t.isEn() ? Locale.ENGLISH : Locale.CHINA));
        String s = t.s("现在是 " + date + " " + clock + "。",
                "It's " + clock + " on " + date + ".");
        return new AiChatResponse(s, "daily_time",
                Arrays.asList(t.s("今天几号？", "What's today's date?"),
                        t.s("平台有多少艘船？", "How many ships are there?")));
    }

    private AiChatResponse dateNow(AiText t) {
        LocalDate today = LocalDate.now();
        String s = t.s("今天是 " + today.format(DateTimeFormatter.ofPattern("yyyy年M月d日"))
                        + "，" + weekdayCN(today.getDayOfWeek().getValue()) + "。",
                "Today is " + today.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH)) + ".");
        return new AiChatResponse(s, "daily_date",
                Arrays.asList(t.s("现在几点了？", "What time is it?"),
                        t.s("128乘以7等于多少？", "What is 128 * 7?")));
    }

    private AiChatResponse weekday(AiText t) {
        LocalDate today = LocalDate.now();
        String s = t.s("今天是 " + weekdayCN(today.getDayOfWeek().getValue())
                        + "（" + today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "）。",
                "Today is " + today.format(DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH))
                        + " (" + today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ").");
        return new AiChatResponse(s, "daily_weekday",
                Arrays.asList(t.s("现在几点了？", "What time is it?"),
                        t.s("平台有多少艘船？", "How many ships are there?")));
    }

    private String weekdayCN(int dayOfWeek) {
        return switch (dayOfWeek) {
            case 1 -> "星期一";
            case 2 -> "星期二";
            case 3 -> "星期三";
            case 4 -> "星期四";
            case 5 -> "星期五";
            case 6 -> "星期六";
            default -> "星期日";
        };
    }

    // ==================== 算术 ====================

    /**
     * 支持中文/英文运算符转符号后，交给安全递归下降解析器求值。
     */
    private AiChatResponse calculate(String raw, AiText t) {
        String expr = raw
                .replace("乘以", "*").replace("除以", "/")
                .replace("×", "*").replace("÷", "/")
                .replace("加", "+").replace("减", "-")
                .replaceAll("(?i)multiplied by", "*")
                .replaceAll("(?i)times", "*")
                .replaceAll("(?i)divided by", "/")
                .replaceAll("(?i)plus", "+")
                .replaceAll("(?i)minus", "-")
                .replace("x", "*").replace("X", "*")
                .replace("，", ",").replace("。", "").replace("？", "").replace("?", "")
                .trim();

        // "A 的 B%" / "B percent of A"
        Matcher pct = Pattern.compile("([0-9.]+)\\s*(的|%|百分之)\\s*([0-9.]+)").matcher(expr);
        if (pct.find()) {
            try {
                double a = Double.parseDouble(pct.group(1));
                double b = Double.parseDouble(pct.group(3));
                double r = a * b / 100.0;
                return new AiChatResponse(plainNum(a) + t.s(" 的 ", " × ") + plainNum(b) + "% = " + plainNum(r),
                        "daily_calc", Arrays.asList(t.s("128乘以7等于多少？", "What is 128 * 7?"),
                                t.s("5公里是多少英里？", "How many miles is 5 km?")));
            } catch (Exception ignore) {
                // 继续尝试常规解析
            }
        }
        Matcher pctEn = Pattern.compile("([0-9.]+)\\s*(?:percent of|%\\s*of)\\s*([0-9.]+)").matcher(expr);
        if (pctEn.find()) {
            try {
                double b = Double.parseDouble(pctEn.group(1));
                double a = Double.parseDouble(pctEn.group(2));
                return new AiChatResponse(plainNum(b) + "% of " + plainNum(a) + " = " + plainNum(a * b / 100.0),
                        "daily_calc", Arrays.asList(t.s("128乘以7等于多少？", "What is 128 * 7?")));
            } catch (Exception ignore) {
                // 继续
            }
        }

        // 抽取算式主体：从第一个数字或括号开始，取连续的数字与运算符
        Matcher m = Pattern.compile("[0-9(][0-9.()+\\-*/ ]*[0-9)]").matcher(expr);
        String formula = null;
        if (m.find()) {
            formula = m.group().trim();
        }
        if (formula == null || formula.isEmpty()) {
            return null;
        }

        try {
            double result = new Expr(formula).eval();
            return new AiChatResponse(formula.replaceAll("\\s+", "") + " = " + plainNum(result),
                    "daily_calc", Arrays.asList(t.s("36.5摄氏度是华氏多少度？", "Convert 36.5°C to °F"),
                            t.s("1/4加1/8等于多少？", "What is 1/4 + 1/8?")));
        } catch (Exception e) {
            return new AiChatResponse(t.s("算式 \"" + formula + "\" 我没能解析出来，麻烦换个写法，例如 \"128*7\"。",
                            "I couldn't parse \"" + formula + "\". Try a simpler form like \"128*7\"."),
                    "daily_calc", Arrays.asList(t.s("128乘以7等于多少？", "What is 128 * 7?")));
        }
    }

    /**
     * 安全的四则运算递归下降解析器：只接受数字、加减乘除括号与空白，
     * 不执行任何脚本引擎，避免表达式注入风险。
     */
    private static final class Expr {
        private final String s;
        private int pos = -1;
        private int ch;

        Expr(String text) {
            this.s = text;
        }

        double eval() {
            next();
            double r = additive();
            if (ch != -1) {
                throw new IllegalArgumentException("unexpected char");
            }
            return r;
        }

        void next() {
            do {
                ch = ++pos < s.length() ? s.charAt(pos) : -1;
            } while (ch == ' ');
        }

        double additive() {
            double left = multiplicative();
            while (ch == '+' || ch == '-') {
                int op = ch;
                next();
                double right = multiplicative();
                left = op == '+' ? left + right : left - right;
            }
            return left;
        }

        double multiplicative() {
            double left = unary();
            while (ch == '*' || ch == '/') {
                int op = ch;
                next();
                double right = unary();
                if (op == '*') {
                    left = left * right;
                } else {
                    if (right == 0) {
                        throw new ArithmeticException("div by zero");
                    }
                    left = left / right;
                }
            }
            return left;
        }

        double unary() {
            double factor;
            if (ch == '-') {
                next();
                factor = -unary();
                return factor;
            }
            if (ch == '(') {
                next();
                factor = additive();
                if (ch != ')') {
                    throw new IllegalArgumentException("missing )");
                }
                next();
                return factor;
            }
            StringBuilder sb = new StringBuilder();
            while ((ch >= '0' && ch <= '9') || ch == '.') {
                sb.append((char) ch);
                next();
            }
            if (sb.length() == 0) {
                throw new IllegalArgumentException("expected number");
            }
            return Double.parseDouble(sb.toString());
        }
    }

    // ==================== 单位换算 ====================

    private boolean isConvertQuery(String lower) {
        return lower.contains("换算") || lower.contains("转换成") || lower.contains("等于多少")
                || lower.contains("是多少") || lower.contains("convert") || lower.contains("how many")
                || CONVERT_PATTERN.matcher(lower).find();
    }

    private AiChatResponse convert(String lower, AiText t) {
        Matcher m = CONVERT_PATTERN.matcher(lower);
        if (!m.find()) {
            return null;
        }
        double value;
        try {
            value = Double.parseDouble(m.group(1));
        } catch (NumberFormatException e) {
            return null;
        }
        String from = normalizeUnit(m.group(2));
        String to = m.group(3) == null ? null : normalizeUnit(m.group(3));

        if (from == null) {
            return null;
        }

        // 没给目标单位时，按同类常用目标成对给出
        if (to == null) {
            return convertDefault(value, m.group(2), t);
        }

        Double r = doConvert(value, from, to);
        if (r != null) {
            return new AiChatResponse(plainNum(value) + " " + m.group(2) + " = " + plainNum(r) + " " + m.group(3),
                    "daily_convert", Arrays.asList(t.s("现在几点了？", "What time is it?"),
                            t.s("128乘以7等于多少？", "What is 128 * 7?")));
        }
        return new AiChatResponse(t.s("暂不支持 " + m.group(2) + " 与 " + m.group(3) + " 之间的换算。",
                        "Conversion between " + m.group(2) + " and " + m.group(3) + " isn't supported yet."),
                "daily_convert", null);
    }

    private AiChatResponse convertDefault(double value, String rawFrom, AiText t) {
        String from = normalizeUnit(rawFrom);
        Map<String, Double> targets = switch (from) {
            case "km" -> Map.of(t.s("英里", "miles"), mile(value), t.s("海里", "nmi"), value / 1.852, t.s("米", "m"), value * 1000);
            case "mile" -> Map.of(t.s("公里", "km"), value * 1.609344, t.s("海里", "nmi"), value * 0.868976);
            case "nmi" -> Map.of(t.s("公里", "km"), value * 1.852, t.s("英里", "miles"), value / 0.868976);
            case "m" -> Map.of(t.s("英尺", "ft"), value / 0.3048, t.s("厘米", "cm"), value * 100);
            case "ft" -> Map.of(t.s("米", "m"), value * 0.3048, t.s("厘米", "cm"), value * 30.48);
            case "inch" -> Map.of(t.s("厘米", "cm"), value * 2.54, t.s("米", "m"), value * 0.0254);
            case "kg" -> Map.of(t.s("斤", "jin"), value * 2, t.s("磅", "lb"), value * 2.2046226);
            case "jin" -> Map.of(t.s("公斤", "kg"), value / 2, t.s("磅", "lb"), value / 2 * 2.2046226);
            case "g" -> Map.of(t.s("盎司", "oz"), value / 28.34952, t.s("斤", "jin"), value / 500);
            case "lb" -> Map.of(t.s("公斤", "kg"), value / 2.2046226, t.s("斤", "jin"), value / 2.2046226 * 2);
            case "c" -> Map.of("℉", value * 9 / 5 + 32, "K", value + 273.15);
            case "f" -> Map.of("℃", (value - 32) * 5 / 9, "K", (value - 32) * 5 / 9 + 273.15);
            case "sqm" -> Map.of(t.s("亩", "mu"), value * 0.0015, t.s("平方英尺", "sq ft"), value / 0.092903);
            case "mu" -> Map.of(t.s("平方米", "sq m"), value / 0.0015);
            default -> null;
        };
        if (targets == null || targets.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(plainNum(value)).append(' ').append(rawFrom)
                .append(t.s(" 约等于：\n", " ≈\n"));
        targets.forEach((k, v) -> sb.append("- ").append(plainNum(v)).append(' ').append(k).append('\n'));
        return new AiChatResponse(sb.toString().trim(), "daily_convert",
                Arrays.asList(t.s("5公里是多少英里？", "How many miles is 5 km?"),
                        t.s("现在几点了？", "What time is it?")));
    }

    private double mile(double km) {
        return km / 1.609344;
    }

    private Double doConvert(double v, String from, String to) {
        if (from == null || to == null) {
            return null;
        }
        if (from.equals(to)) {
            return v;
        }
        Double toM = toMeter(from);
        Double fromM = toMeter(to);
        if (toM != null && fromM != null) {
            return v * toM / fromM;
        }
        Double toKg = toKilogram(from);
        Double fromKg = toKilogram(to);
        if (toKg != null && fromKg != null) {
            return v * toKg / fromKg;
        }
        if (from.equals("c") && to.equals("f")) return v * 9 / 5 + 32;
        if (from.equals("f") && to.equals("c")) return (v - 32) * 5 / 9;
        if (from.equals("c") && to.equals("k")) return v + 273.15;
        if (from.equals("f") && to.equals("k")) return (v - 32) * 5 / 9 + 273.15;
        if (from.equals("sqm") && to.equals("mu")) return v * 0.0015;
        if (from.equals("mu") && to.equals("sqm")) return v / 0.0015;
        Double toL = toLiter(from);
        Double fromL = toLiter(to);
        if (toL != null && fromL != null) {
            return v * toL / fromL;
        }
        return null;
    }

    private Double toMeter(String u) {
        return switch (u) {
            case "km" -> 1000.0;
            case "m" -> 1.0;
            case "cm" -> 0.01;
            case "mm" -> 0.001;
            case "mile" -> 1609.344;
            case "nmi" -> 1852.0;
            case "ft" -> 0.3048;
            case "inch" -> 0.0254;
            default -> null;
        };
    }

    private Double toKilogram(String u) {
        return switch (u) {
            case "kg" -> 1.0;
            case "jin" -> 0.5;
            case "g" -> 0.001;
            case "lb" -> 0.45359237;
            case "oz" -> 0.028349523;
            case "t" -> 1000.0;
            default -> null;
        };
    }

    private Double toLiter(String u) {
        return switch (u) {
            case "l" -> 1.0;
            case "ml" -> 0.001;
            default -> null;
        };
    }

    private String normalizeUnit(String raw) {
        if (raw == null) {
            return null;
        }
        String u = raw.toLowerCase(Locale.ROOT);
        return switch (u) {
            case "公里", "千米", "km" -> "km";
            case "米", "m", "meter", "meters", "metre", "metres" -> "m";
            case "厘米", "cm" -> "cm";
            case "毫米", "mm" -> "mm";
            case "英里", "mile", "miles" -> "mile";
            case "海里", "节", "nmi" -> "nmi";
            case "英尺", "ft", "foot", "feet" -> "ft";
            case "英寸", "inch", "inches", "in" -> "inch";
            case "公斤", "千克", "kg", "kilogram", "kilograms", "kilo", "kilos" -> "kg";
            case "斤" -> "jin";
            case "克", "g", "gram", "grams" -> "g";
            case "磅", "lb", "lbs", "pound", "pounds" -> "lb";
            case "盎司", "oz", "ounce", "ounces" -> "oz";
            case "吨", "t" -> "t";
            case "升", "l" -> "l";
            case "毫升", "ml" -> "ml";
            case "摄氏度", "℃", "度", "celsius" -> "c";
            case "华氏度", "℉", "fahrenheit" -> "f";
            case "平方米", "平米", "㎡" -> "sqm";
            case "亩", "亩子", "acre", "acres" -> "mu";
            default -> null;
        };
    }

    // ==================== BMI ====================

    private AiChatResponse bmi(String text, AiText t) {
        Matcher hm = BMI_HEIGHT_PATTERN.matcher(text);
        double height = -1;
        if (hm.find()) {
            height = Double.parseDouble(hm.group(1));
        } else {
            Matcher h2 = Pattern.compile("(?:身高|height)\\s*([0-9]{2,3}(?:\\.?[0-9]{0,2}))").matcher(text);
            if (h2.find()) {
                height = Double.parseDouble(h2.group(1));
            }
        }
        double weightKg = -1;
        Matcher wm = BMI_WEIGHT_KG_PATTERN.matcher(text);
        if (wm.find()) {
            weightKg = Double.parseDouble(wm.group(1));
        } else {
            Matcher wj = BMI_WEIGHT_JIN_PATTERN.matcher(text);
            if (wj.find()) {
                weightKg = Double.parseDouble(wj.group(1)) / 2;
            } else {
                Matcher wl = Pattern.compile("([0-9]{2,3}(?:\\.[0-9]+)?)\\s*(?:lbs?|pounds?)").matcher(text);
                if (wl.find()) {
                    weightKg = Double.parseDouble(wl.group(1)) * 0.45359237;
                }
            }
        }
        if (height <= 0 || weightKg <= 0) {
            return new AiChatResponse(t.s("请告诉我身高和体重，例如：\"身高175cm体重70公斤的BMI是多少？\"",
                            "Give me a height and weight, e.g. \"BMI for height 175cm weight 70kg?\""),
                    "daily_bmi", null);
        }
        double h = height / 100.0;
        double bmiVal = weightKg / (h * h);
        String level;
        if (bmiVal < 18.5) {
            level = t.s("偏瘦", "underweight");
        } else if (bmiVal < 24) {
            level = t.s("正常", "normal");
        } else if (bmiVal < 28) {
            level = t.s("超重", "overweight");
        } else {
            level = t.s("肥胖", "obese");
        }
        return new AiChatResponse(
                t.s("身高 " + plainNum(height) + " cm、体重 " + plainNum(weightKg) + " kg，" +
                        "BMI = " + String.format(Locale.ROOT, "%.1f", bmiVal) +
                        "，属于\"" + level + "\"（中国成人标准：<18.5 偏瘦，18.5~23.9 正常，24~27.9 超重，≥28 肥胖）。",
                        "Height " + plainNum(height) + " cm, weight " + plainNum(weightKg) + " kg → BMI " +
                        String.format(Locale.ROOT, "%.1f", bmiVal) + " (" + level + "). " +
                        "Chinese adult standard: <18.5 underweight, 18.5–23.9 normal, 24–27.9 overweight, ≥28 obese."),
                "daily_bmi",
                Arrays.asList(t.s("怎么健康减肥？", "Healthy weight-loss tips"),
                        t.s("每天喝多少水合适？", "How much water should I drink?")));
    }

    // ==================== 工具 ====================

    private String plainNum(double v) {
        if (Double.isNaN(v) || Double.isInfinite(v)) {
            return String.valueOf(v);
        }
        java.math.BigDecimal bd = new java.math.BigDecimal(v)
                .round(new java.math.MathContext(6));
        return bd.stripTrailingZeros().toPlainString();
    }
}
