package com.st.modules.id.snowflake;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.util.UUID;

public class SnowFlakeUtil {

    private static final long START_TIMESTAMP = 1672531200000L; // 可自定义项目启动时间
    private static final long DATA_CENTER_ID_BITS = 5L;
    private static final long WORKER_ID_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;

    private static final long MAX_DATA_CENTER_ID = ~(-1L << DATA_CENTER_ID_BITS);
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;

    private static final long DATA_CENTER_ID = computeDataCenterId();
    private static final long WORKER_ID = computeWorkerId();

    private static long sequence = 0L;
    private static long lastTimestamp = -1L;

//    private static final String POD_NAME = System.getenv().getOrDefault("POD_NAME", fallbackHostName());
//    private static final String POD_NAMESPACE = System.getenv().getOrDefault("POD_NAMESPACE", "default");
    private static final String POD_NAME = defaultIfEmpty(System.getenv("POD_NAME"), fallbackHostName());
    private static final String POD_NAMESPACE = defaultIfEmpty(System.getenv("POD_NAMESPACE"), "default");

    /**
     * 获取 long 类型雪花 ID
     */
    public static synchronized long nextId() {
        long currentTimestamp = System.currentTimeMillis();

        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id.");
        }

        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                currentTimestamp = waitNextMillis(currentTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = currentTimestamp;

        return ((currentTimestamp - START_TIMESTAMP) << TIMESTAMP_LEFT_SHIFT)
                | (DATA_CENTER_ID << DATA_CENTER_ID_SHIFT)
                | (WORKER_ID << WORKER_ID_SHIFT)
                | sequence;
    }

    /**
     * 获取 Base62 编码 ID（更短字符串）
     */
    public static String nextIdBase62() {
        return toBase62(nextId());
    }

    /**
     * 获取 UUID 格式（非标准 UUID，兼容格式）
     */
    public static String nextIdAsUuid() {
        long id = nextId();
        return new UUID(id >>> 32, id & 0xFFFFFFFFL).toString();
    }

    /**
     * 打印实例信息，便于排查
     */
    public static String getInstanceInfo() {
        return String.format("Snowflake Instance: dataCenterId=%d, workerId=%d, podName=%s, namespace=%s, hostname=%s",
                DATA_CENTER_ID, WORKER_ID, POD_NAME, POD_NAMESPACE, fallbackHostName());
    }

    private static long waitNextMillis(long timestamp) {
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    private static long computeWorkerId() {
        long hash = hashToLong(POD_NAME);
        return hash % (MAX_WORKER_ID + 1);
    }

    private static long computeDataCenterId() {
        return hashToLong(POD_NAMESPACE) % (MAX_DATA_CENTER_ID + 1);
    }

    private static long hashToLong(String input) {
        try {
            if (input == null || input.isEmpty()) {
                input = "unknown";
            }
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes());
            long result = 0;
            for (int i = 0; i < 8; i++) {
                result = (result << 8) | (digest[i] & 0xFF);
            }
            return Math.abs(result);
        } catch (Exception e) {
            return Math.abs(input != null ? input.hashCode() : 0);
        }
    }

    private static String fallbackHostName() {
        try {
            String hostname = InetAddress.getLocalHost().getHostName();
            return (hostname != null && !hostname.isEmpty()) ? hostname : "unknown-host";
        } catch (Exception e) {
            return "unknown-host";
        }
    }

    private static final char[] BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    private static String toBase62(long value) {
        StringBuilder sb = new StringBuilder();
        while (value > 0) {
            int index = (int) (value % 62);
            sb.append(BASE62[index]);
            value /= 62;
        }
        return sb.reverse().toString();
    }

    private static String defaultIfEmpty(String value, String defaultValue) {
        return (value == null || value.isEmpty()) ? defaultValue : value;
    }

    public static void main(String[] args) throws UnknownHostException {

        System.out.println( InetAddress.getLocalHost().getHostName());
        System.out.println("Long ID: " + SnowFlakeUtil.nextId());
        System.out.println("Base62 ID: " + SnowFlakeUtil.nextIdBase62());
        System.out.println("UUID ID: " + SnowFlakeUtil.nextIdAsUuid());
        System.out.println("Instance Info: " + SnowFlakeUtil.getInstanceInfo());
    }
}