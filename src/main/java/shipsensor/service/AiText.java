package shipsensor.service;

/**
 * 智能客服双语文案工具。
 *
 * 客服的所有固定话术（标题、字段名、状态枚举、单位、建议问题）按请求语言
 * 选择中文或英文输出；业务数据本身（船名、港口名、规则名等）保持库中原文。
 */
public final class AiText {

    private final boolean en;

    private AiText(boolean en) {
        this.en = en;
    }

    public static AiText of(String lang) {
        return new AiText(lang != null && lang.toLowerCase().startsWith("en"));
    }

    public boolean isEn() {
        return en;
    }

    /** 在中文/英文之间二选一。 */
    public String s(String zh, String enVal) {
        return en ? enVal : zh;
    }

    /** 行内分隔符。 */
    public String sep() {
        return en ? " | " : "｜";
    }

    /** 键值冒号：中文全角、英文半角。 */
    public String colon() {
        return en ? ": " : "：";
    }

    /** 标题冒号 + 空行。 */
    public String colonNl() {
        return en ? ":\n\n" : "：\n\n";
    }

    /** 括号包裹（含内部文本）。 */
    public String paren(String inner) {
        return en ? " (" + inner + ")" : "（" + inner + "）";
    }

    /** "艘"量词行：中文带量词并换行，英文只换行。 */
    public String unitShips() {
        return s(" 艘\n", "\n");
    }

    /** 未记录。 */
    public String missing() {
        return s("未记录", "N/A");
    }

    // ==================== 枚举翻译 ====================

    public String sailingStatus(String code) {
        if (code == null) {
            return s("未知", "Unknown");
        }
        return switch (code) {
            case "STOP" -> s("停泊", "Moored");
            case "LEAVING_PORT" -> s("离港中", "Leaving port");
            case "CRUISING" -> s("巡航中", "Cruising");
            case "TURNING" -> s("转向中", "Turning");
            case "ARRIVING" -> s("进港中", "Arriving");
            case "ANCHORING" -> s("锚泊中", "At anchor");
            default -> code;
        };
    }

    public String weatherType(String code) {
        if (code == null) {
            return s("未知", "Unknown");
        }
        return switch (code) {
            case "CLEAR" -> s("晴", "Clear");
            case "SUNNY" -> s("晴", "Sunny");
            case "PARTLY_CLOUDY" -> s("多云", "Partly cloudy");
            case "LOW_CLOUD" -> s("低云", "Low cloud");
            case "OVERCAST" -> s("阴", "Overcast");
            case "DRIZZLE" -> s("毛毛雨", "Drizzle");
            case "LIGHT_RAIN" -> s("小雨", "Light rain");
            case "MODERATE_RAIN" -> s("中雨", "Moderate rain");
            case "HEAVY_RAIN" -> s("大雨", "Heavy rain");
            case "RAIN_SHOWER" -> s("阵雨", "Rain showers");
            case "THUNDERSTORM" -> s("雷暴", "Thunderstorm");
            case "LIGHTNING_STORM" -> s("雷暴大风", "Lightning storm");
            case "MIST" -> s("薄雾", "Mist");
            case "FOG" -> s("雾", "Fog");
            case "SEA_FOG" -> s("海雾", "Sea fog");
            case "FREEZING_FOG" -> s("冰冻雾", "Freezing fog");
            case "LOW_VISIBILITY" -> s("低能见度", "Low visibility");
            case "SNOW" -> s("雪", "Snow");
            case "ICE_WARNING" -> s("结冰警告", "Ice warning");
            case "SAND_STORM" -> s("沙尘暴", "Sandstorm");
            case "STRONG_WIND" -> s("强风", "Strong wind");
            case "GALE" -> s("大风", "Gale");
            case "STORM_FORCE_WIND" -> s("风暴级大风", "Storm-force winds");
            case "HIGH_WAVE" -> s("大浪", "High waves");
            case "VERY_HIGH_WAVE" -> s("巨浪", "Very high waves");
            case "ROUGH_SEA" -> s("浪大", "Rough sea");
            case "TYPHOON" -> s("台风", "Typhoon");
            case "HURRICANE" -> s("飓风", "Hurricane");
            case "CYCLONE" -> s("气旋", "Cyclone");
            case "TROPICAL_STORM" -> s("热带风暴", "Tropical storm");
            case "TROPICAL_DEPRESSION" -> s("热带低压", "Tropical depression");
            case "STORM" -> s("风暴", "Storm");
            case "EXTREME_WEATHER" -> s("极端天气", "Extreme weather");
            default -> code;
        };
    }

    public String handleStatus(Integer status) {
        if (status == null) {
            return s("未知状态", "Unknown status");
        }
        return switch (status) {
            case 0 -> s("未处理", "Unhandled");
            case 1 -> s("已确认", "Acknowledged");
            case 2 -> s("已解除", "Resolved");
            default -> s("状态码 ", "code ") + status;
        };
    }

    /**
     * 库中船型为中文枚举，英文模式下映射为通用英文船型名。
     */
    public String shipType(String zhType) {
        if (zhType == null) {
            return missing();
        }
        if (!en) {
            return zhType;
        }
        return switch (zhType) {
            case "散货船" -> "Bulk Carrier";
            case "集装箱船" -> "Container Ship";
            case "油轮" -> "Tanker";
            case "客轮", "客船" -> "Passenger Ship";
            case "化学品船" -> "Chemical Tanker";
            case "滚装船" -> "Ro-Ro Ship";
            case "拖轮" -> "Tug";
            case "LNG船" -> "LNG Carrier";
            case "渔船" -> "Fishing Vessel";
            case "科考船" -> "Research Vessel";
            default -> zhType;
        };
    }

    /**
     * 传感器字典 typeCode → 英文名（中文模式仍用库中 typeName）。
     */
    public String sensorType(String typeCode, String zhFallback) {
        if (!en || typeCode == null) {
            return zhFallback;
        }
        return switch (typeCode) {
            case "SPEED" -> "Speed";
            case "HEADING" -> "Heading";
            case "ENG_TEMP" -> "Engine Temperature";
            case "ENG_RPM" -> "Engine RPM";
            case "ENG_PRESSURE" -> "Engine Pressure";
            case "EXHAUST_TEMP" -> "Exhaust Temperature";
            case "SEA_WATER_TEMP" -> "Sea Water Temperature";
            case "HUMIDITY" -> "Humidity";
            case "PRESSURE" -> "Pressure";
            case "FUEL_FLOW" -> "Fuel Flow";
            case "FUEL_LEVEL" -> "Fuel Level";
            case "VIBRATION" -> "Vibration";
            case "POWER" -> "Power";
            case "BATTERY_VOLT" -> "Battery Voltage";
            case "CURRENT" -> "Current";
            case "GPS_LAT" -> "GPS Latitude";
            case "GPS_LON" -> "GPS Longitude";
            default -> zhFallback;
        };
    }

    // ==================== 常用短语 ====================

    public String unitNmi() { return s(" 海里", " nmi"); }
    public String maxRows(int n) {
        return s("（最多展示 " + n + " 条）", " (up to " + n + " shown)");
    }
}
