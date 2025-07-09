package com.st.modules.id.uuid;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 通用唯一 ID 生成器，支持多种模式; 可以从统一入口调用, 或单独调用.
 * <pre>
 * - STANDARD: 去除连字符的标准 UUID（32 位）
 * - BASE64: 基于 UUID 的 Base64 URL 安全编码（22 位）
 * - TIME_RANDOM: 时间戳 + UUID 片段，适合可排序主键
 * - SHORT_UUID: 精简 8 字节 ID，适合压缩 UUID 显示
 * - NANO_ID: 高可定制化、适合分布式场景的随机 ID
 * </pre>
 *
 * <p></p>
 * BP
 * <pre>
 * - 若追求 短且唯一性高，使用 BASE64 或 SHORT_UUID
 * - 若要求 顺序性 + 分布式兼容，使用 TIME_RANDOM
 * - 若希望 完全可控长度且无可预测性，使用 NANO_ID
 * </pre>
 *
 * <p></p>
 * 后续扩展
 * <pre>
 * - 完整唯一 ID 框架: Snowflake ID（雪花算法）, Redis incr / DB序号方式分布式ID
 * - 唯一 ID 的线程安全性说明
 * - 与数据库主键（如 MySQL bigint、varchar）的适配建议
 * - 作为分布式 ID 的应用限制
 * - 若需生成唯一文件名、URL参数、或 Redis key 等也可扩展支持前缀、分区、校验等逻辑
 * </pre>
 *
 * <p></p>
 * usage
 * <pre>
 * {@code
 * // =========== 从统一入口调用 ============
 *         System.out.println("STANDARD     : " + UuidGenerator.generate(32, UuidGenerator.Mode.STANDARD));
 *         System.out.println("BASE64       : " + UuidGenerator.generate(22, UuidGenerator.Mode.BASE64));
 *         System.out.println("TIME_RANDOM  : " + UuidGenerator.generate(24, UuidGenerator.Mode.TIME_RANDOM));
 *         System.out.println("SHORT_UUID   : " + UuidGenerator.generate(12, UuidGenerator.Mode.SHORT_UUID));
 *         System.out.println("NANO_ID      : " + UuidGenerator.generate(21, UuidGenerator.Mode.NANO_ID));
 *
 *         // =========== 调用各个方法 ============
 *         // STANDARD UUID:
 *         //de36a582368f4e3c996e6baed2786cba
 *         System.out.println("STANDARD UUID:");
 *         String standard = UuidGenerator.generateStandardUuid(32);
 *         System.out.println(standard);
 *
 *         //BASE64 UUID:
 *         //kuNY2M9TSYqHKo6a6J0fDw
 *         System.out.println("BASE64 UUID:");
 *         String base64 = UuidGenerator.generateBase64Uuid(22);
 *         System.out.println(base64);
 *
 *         //TIME_RANDOM ID:
 *         //175202981896536d3527
 *         System.out.println("TIME_RANDOM ID:");
 *         String timeRandom = UuidGenerator.generateTimeRandomId(20);
 *         System.out.println(timeRandom);
 *
 *         //SHORT UUID:
 *         //My6NLyXQjO
 *         System.out.println("SHORT UUID:");
 *         String shortId = UuidGenerator.generateShortUuid(10);
 *         System.out.println(shortId);
 *
 *         //NANO_ID:
 *         //LcFlEFy3m41hokT4ldKGn
 *         System.out.println("NANO_ID:");
 *         String nanoId = UuidGenerator.generateNanoId(21);
 *         System.out.println(nanoId);
 * }
 * </pre>
 */
public final class UuidGenerator {

    public enum Mode {
        STANDARD, BASE64, TIME_RANDOM, SHORT_UUID, NANO_ID
    }

    private static final char[] NANO_ID_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final int DEFAULT_NANO_ID_LENGTH = 21;

    private UuidGenerator() {
        throw new UnsupportedOperationException("工具类禁止实例化");
    }

    /**
     * 主入口，根据模式与长度生成唯一 ID
     *
     * @param maxLen 返回的最大长度（范围：1~64）
     * @param mode 生成模式（枚举）
     * @return 满足长度限制的唯一 ID 字符串
     */
    public static String generate(int maxLen, Mode mode) {
        if (maxLen < 1 || maxLen > 64) {
            throw new IllegalArgumentException("长度必须在 1~64 之间");
        }

        switch (mode) {
            case STANDARD:
                return generateStandardUuid(maxLen);
            case BASE64:
                return generateBase64Uuid(maxLen);
            case TIME_RANDOM:
                return generateTimeRandomId(maxLen);
            case SHORT_UUID:
                return generateShortUuid(maxLen);
            case NANO_ID:
                return generateNanoId(Math.min(maxLen, DEFAULT_NANO_ID_LENGTH));
            default:
                throw new UnsupportedOperationException("不支持的 UUID 生成模式: " + mode);
        }
    }

    /**
     * 生成标准 UUID（去除连字符），返回值为 32 个十六进制字符（如需可截断）
     *
     * @param maxLen 期望返回的最大长度
     * @return 标准 UUID 字符串（可截断）
     */
    public static String generateStandardUuid(int maxLen) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid.length() <= maxLen ? uuid : uuid.substring(0, maxLen);
    }

    /**
     * 生成 Base64 编码 UUID，URL 安全、不带填充，默认长度为 22 字符
     *
     * @param maxLen 期望返回的最大长度
     * @return Base64 编码后的 UUID 字符串（可截断）
     */
    public static String generateBase64Uuid(int maxLen) {
        UUID uuid = UUID.randomUUID();
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        String base64 = Base64.getUrlEncoder().withoutPadding().encodeToString(buffer.array());
        return base64.length() <= maxLen ? base64 : base64.substring(0, maxLen);
    }

    /**
     * 基于当前时间戳 + UUID 片段生成的递增型主键，适合按时间排序的业务场景
     *
     * @param maxLen 最大长度限制，若不足则完整返回
     * @return 可排序的 UUID 变体
     */
    public static String generateTimeRandomId(int maxLen) {
        long ts = System.currentTimeMillis();
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        String result = ts + suffix;
        return result.length() <= maxLen ? result : result.substring(0, maxLen);
    }

    /**
     * 生成压缩后的 UUID 变体，基于 leastSignificantBits 简化为 6-bit 字符串
     *
     * @param maxLen 最大返回长度（建议不小于 8）
     * @return 精简 UUID 字符串
     */
    public static String generateShortUuid(int maxLen) {
        UUID uuid = UUID.randomUUID();
        long lsb = uuid.getLeastSignificantBits();
        StringBuilder sb = new StringBuilder();
        while (lsb != 0 && sb.length() < maxLen) {
            int idx = (int) (lsb & 63); // 取 6 位
            sb.append(NANO_ID_CHARS[idx]);
            lsb >>>= 6;
        }
        return sb.toString();
    }

    /**
     * 生成 NanoId 风格的高性能随机 ID，字符集可控、无时间性、无重复性保证
     *
     * @param len 指定长度（应 >= 1）
     * @return 随机生成的 NanoId 字符串
     */
    public static String generateNanoId(int len) {
        if (len < 1) throw new IllegalArgumentException("NanoId 长度需 >= 1");
        char[] id = new char[len];
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < len; i++) {
            id[i] = NANO_ID_CHARS[random.nextInt(NANO_ID_CHARS.length)];
        }
        return new String(id);
    }


    public static void main(String[] args) {

        // =========== 从统一入口调用 ============
        System.out.println("STANDARD     : " + UuidGenerator.generate(32, UuidGenerator.Mode.STANDARD));
        System.out.println("BASE64       : " + UuidGenerator.generate(22, UuidGenerator.Mode.BASE64));
        System.out.println("TIME_RANDOM  : " + UuidGenerator.generate(24, UuidGenerator.Mode.TIME_RANDOM));
        System.out.println("SHORT_UUID   : " + UuidGenerator.generate(12, UuidGenerator.Mode.SHORT_UUID));
        System.out.println("NANO_ID      : " + UuidGenerator.generate(21, UuidGenerator.Mode.NANO_ID));

        // =========== 调用各个方法 ============
        // STANDARD UUID:
        //de36a582368f4e3c996e6baed2786cba
        System.out.println("STANDARD UUID:");
        String standard = UuidGenerator.generateStandardUuid(32);
        System.out.println(standard);

        //BASE64 UUID:
        //kuNY2M9TSYqHKo6a6J0fDw
        System.out.println("BASE64 UUID:");
        String base64 = UuidGenerator.generateBase64Uuid(22);
        System.out.println(base64);

        //TIME_RANDOM ID:
        //175202981896536d3527
        System.out.println("TIME_RANDOM ID:");
        String timeRandom = UuidGenerator.generateTimeRandomId(20);
        System.out.println(timeRandom);

        //SHORT UUID:
        //My6NLyXQjO
        System.out.println("SHORT UUID:");
        String shortId = UuidGenerator.generateShortUuid(10);
        System.out.println(shortId);

        //NANO_ID:
        //LcFlEFy3m41hokT4ldKGn
        System.out.println("NANO_ID:");
        String nanoId = UuidGenerator.generateNanoId(21);
        System.out.println(nanoId);
    }
}

