package com.st.modules.base64;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 概述:
 *   使用随机盐对明文字节进行循环异或, 然后整体 Base64 编码; 解码时恢复盐并反异或。
 *   仅用于最低强度的可逆“遮挡”(obfuscation), <不是加密>。
 * 功能清单:
 *   1) encode: 明文 -> Base64 字符串(携带版本+盐)
 *   2) decode: Base64 字符串 -> 明文
 * 使用示例:
 *   String token = SaltedBase64.encode("root@123456");
 *   String raw   = SaltedBase64.decode(token);
 * 注意事项:
 *   1) 只是混淆, 不具备加密强度; 禁止用于用户口令、密钥等真正敏感数据。
 *   2) 若需要安全加密, 使用 AES GCM 等方案。
 * 入参与出参与异常说明:
 *   - encode(plain): plain 非 null; 返回 Base64 字符串; 可能抛出 IllegalArgumentException
 *   - decode(token): token 为 encode 的输出; 返回明文; 可能抛出 IllegalArgumentException
 */
public final class SaltedBase64 {

    // 简单的版本字节, 便于以后升级格式
    private static final byte VERSION_1 = 1;
    // 盐长度, 可调; 越长越不易肉眼猜测, 但这仍不是安全
    private static final int SALT_LEN = 8;
    private static final SecureRandom RANDOM = new SecureRandom();

    private SaltedBase64() {}

    /**
     * 将明文混淆为字符串, 输出格式:
     * [1字节版本][1字节盐长][salt][payload(=plain XOR salt循环)]
     */
    public static String encode(String plaintext) {
        if (plaintext == null) throw new IllegalArgumentException("目标文本不能为空");
        byte[] salt = new byte[SALT_LEN];
        RANDOM.nextBytes(salt);

        byte[] plain = plaintext.getBytes(StandardCharsets.UTF_8);
        byte[] payload = xorWithSalt(plain, salt);

        ByteBuffer buf = ByteBuffer.allocate(1 + 1 + salt.length + payload.length);
        buf.put(VERSION_1).put((byte) salt.length).put(salt).put(payload);
        return Base64.getEncoder().encodeToString(buf.array());
    }

    /**
     * 反混淆得到明文
     */
    public static String decode(String tokenBase64) {
        if (tokenBase64 == null) throw new IllegalArgumentException("待解密的文本不能为空");
        byte[] all = Base64.getDecoder().decode(tokenBase64);
        if (all.length < 1 + 1) throw new IllegalArgumentException("待解密的文本已损坏");

        ByteBuffer buf = ByteBuffer.wrap(all);
        byte ver = buf.get();
        if (ver != VERSION_1) throw new IllegalArgumentException("解密文本不支持的版本: " + ver);

        int saltLen = buf.get() & 0xFF;
        if (saltLen <= 0 || saltLen > 64 || buf.remaining() < saltLen) {
            throw new IllegalArgumentException("盐的长度不合规");
        }
        byte[] salt = new byte[saltLen];
        buf.get(salt);

        byte[] payload = new byte[buf.remaining()];
        buf.get(payload);

        byte[] plain = xorWithSalt(payload, salt);
        return new String(plain, StandardCharsets.UTF_8);
    }

    // 循环异或: payload[i] = data[i] ^ salt[i % salt.length]
    private static byte[] xorWithSalt(byte[] data, byte[] salt) {
        byte[] out = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            out[i] = (byte) (data[i] ^ salt[i % salt.length]);
        }
        return out;
    }

    // 简单演示
    public static void main(String[] args) {
        String enc = encode("fms#11111");
        String dec = decode(enc);
        System.out.println("ENC: " + enc);
        System.out.println("DEC: " + dec);
    }
}

