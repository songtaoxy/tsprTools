package com.st.modules.test.boc.voucher.mock;

import java.util.*;
import java.text.SimpleDateFormat;

public class VoucherDataGenerator2 {

    // 时间工具：格式化日期
    public static String time2StrCust(String pattern) {
        return new SimpleDateFormat(pattern).format(new Date());
    }

    /**
     * 生成测试数据
     * @param orgl1OuCount orgL1AndOuCode类数
     * @param perClassCount 每类生成多少条
     * @return List<Map<String, Object>> 测试数据
     */
    public static List<Map<String, Object>> generateTestData(int orgl1OuCount, int perClassCount) {
        List<Map<String, Object>> all = new ArrayList<>();

        String[] types = {"收款", "付款", "报销"};
        Random rand = new Random();

        for (int i = 0; i < orgl1OuCount; i++) {
            String orgl1 = String.format("%05d", i); // orgl1 长度5
            String oucode = String.format("%03d", i % 1000);
            String orgL1AndOuCode = orgl1 + oucode;

            int totalThisClass = 0;
            int perTypeCount = Math.max(100, perClassCount / types.length);

            for (String type : types) {
                // 每个type下 jeBatchName 至少5种
                int jeBatchKinds = Math.max(5, perTypeCount / 20);
                List<String> jeBatchNames = new ArrayList<>();
                for (int j = 0; j < jeBatchKinds; j++) {
                    // jeBatchName = "AP"+ouCode+日期+"-"+pk_voucher+"-"+pk_detail
                    // pk_voucher/pk_detail 用j保证唯一
                    String pk_voucher = String.format("%07d", rand.nextInt(10000000));
                    String pk_detail = String.format("%08d", rand.nextInt(100000000));
                    String jeBatchName = "AP" + oucode + time2StrCust("yyyyMMdd") + "-" + pk_voucher + "-" + pk_detail;
                    jeBatchNames.add(jeBatchName);
                }

                int perJeBatchCount = Math.max(5, perTypeCount / jeBatchKinds);

                for (String jeBatchName : jeBatchNames) {
                    // 控制processFlag三种情况
                    for (int k = 0; k < perJeBatchCount; k++) {
                        Map<String, Object> map = new HashMap<>();
                        // 1. 基础字段
                        map.put("orgl1", orgl1);
                        map.put("oucode", oucode);
                        map.put("pk_voucher", String.format("%07d", rand.nextInt(10000000)));
                        map.put("pk_detail", String.format("%08d", rand.nextInt(100000000)));
                        map.put("processFlag",
                                (k < perJeBatchCount / 3) ? "S"
                                        : (k < 2 * perJeBatchCount / 3) ? "E"
                                        : (k % 2 == 0 ? "S" : "E"));
                        // 2. 其它字段（测试可适当简化）
                        map.put("userJeSourceName", "应付款");
                        map.put("type", type);
                        map.put("jeBatchName", jeBatchName);
                        map.put("jeHeaderName", type + "凭证");
                        map.put("transactionNum", "TXN" + rand.nextInt(100000));
                        map.put("currencyCode", "CNY");
                        map.put("excrate2", "1");
                        map.put("segment1", "SEG1-" + rand.nextInt(100));
                        map.put("segment2", "SEG2-" + rand.nextInt(100));
                        map.put("accountcode", "ACCT-" + rand.nextInt(100));
                        map.put("segment4", "S4-" + rand.nextInt(100));
                        map.put("segment5", "S5-" + rand.nextInt(100));
                        map.put("segment6", "S6-" + rand.nextInt(100));
                        map.put("segment7", "S7-" + rand.nextInt(100));
                        map.put("localdebitamount", String.valueOf(rand.nextInt(10000)));
                        map.put("localcreditamount", String.valueOf(rand.nextInt(10000)));
                        map.put("debitamount", String.valueOf(rand.nextInt(10000)));
                        map.put("creditamount", String.valueOf(rand.nextInt(10000)));
                        map.put("explanation", type + "测试摘要");
                        map.put("reference21", "供应商" + rand.nextInt(100));
                        map.put("reference22", "REF22-" + rand.nextInt(100));
                        map.put("reference23", "REF23-" + rand.nextInt(100));
                        map.put("reference24", "REF24-" + rand.nextInt(100));
                        map.put("reference25", "REF25-" + rand.nextInt(100));
                        map.put("reference26", "AP Invoices");
                        map.put("reference27", "2004");
                        map.put("reference28", "REF28-" + rand.nextInt(100));
                        map.put("reference29", "REF29-" + rand.nextInt(100));
                        map.put("reference30", "CHARGE");
                        // 分组用字段
                        map.put("orgL1AndOuCode", orgL1AndOuCode);

                        all.add(map);
                        totalThisClass++;
                    }
                }
            }
            // 补足总数
            while (totalThisClass < perClassCount) {
                Map<String, Object> m = new HashMap<>();
                m.put("orgl1", orgl1);
                m.put("oucode", oucode);
                m.put("type", types[totalThisClass % types.length]);
                m.put("orgL1AndOuCode", orgL1AndOuCode);
                m.put("pk_voucher", String.format("%07d", rand.nextInt(10000000)));
                m.put("pk_detail", String.format("%08d", rand.nextInt(100000000)));
                m.put("processFlag", "S");
                m.put("jeBatchName", "AP" + oucode + time2StrCust("yyyyMMdd") + "-" + rand.nextInt(1000000) + "-" + rand.nextInt(10000000));
                all.add(m);
                totalThisClass++;
            }
        }
        return all;
    }
}
