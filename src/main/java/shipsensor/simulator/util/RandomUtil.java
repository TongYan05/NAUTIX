package shipsensor.simulator.util;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机工具类
 *
 * 所有方法均为静态方法。
 */
public final class RandomUtil {

    private RandomUtil() {
    }

    /**
     * 0~1
     */
    public static double randomDouble() {
        return ThreadLocalRandom.current().nextDouble();
    }

    /**
     * min~max
     */
    public static double randomDouble(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    /**
     * min~max(包含)
     */
    public static int randomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    /**
     * true / false
     */
    public static boolean randomBoolean() {
        return ThreadLocalRandom.current().nextBoolean();
    }

    /**
     * ±range
     */
    public static double randomOffset(double range) {
        return ThreadLocalRandom.current().nextDouble(-range, range);
    }

}