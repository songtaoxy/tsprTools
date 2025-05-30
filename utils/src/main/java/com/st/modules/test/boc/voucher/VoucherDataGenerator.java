package com.st.modules.test.boc.voucher;
import java.util.*;
import java.text.SimpleDateFormat;

public class VoucherDataGenerator {

    public static String time2StrCust(String pattern) {
        return new SimpleDateFormat(pattern).format(new Date());
    }

    /**
     * 生成测试数据
     * @param orgl1OuCount 生成多少个 orgL1AndOuCode（即多少类）
     * @param perClassCount 每类生成多少条
     * @return List<Map<String, Object>>
     */
    public static List<Map<String, Object>> generateTestData(int orgl1OuCount, int perClassCount) {
        List<Map<String, Object>> all = new ArrayList<>();

        String[] types = {"收款", "付款", "报销"};
        Random rand = new Random();

        for (int i = 0; i < orgl1OuCount; i++) {
            String orgl1 = String.format("%05d", i); // orgl1长度5
            String oucode = String.format("%03d", i % 1000);
            String orgL1AndOuCode = orgl1 + oucode;

            int remain = perClassCount;
            int perTypeBase = perClassCount / types.length;

            for (int t = 0; t < types.length; t++) {
                String type = types[t];
                int perTypeCount = (t == types.length - 1) ? remain : perTypeBase;
                remain -= perTypeCount;

                // 每type下pk_voucher分组，设置每个pk_voucher下多少条（比如5~10条）
                int pkVoucherCount = Math.max(10, perTypeCount / 10); // 每type下有10组pk_voucher
                int detailPerVoucher = Math.max(5, perTypeCount / pkVoucherCount);

                for (int v = 0; v < pkVoucherCount; v++) {
                    String pk_voucher = String.format("%07d", rand.nextInt(10000000));
                    // 三种processFlag分布方式，循环用
                    String[] flagModes = {"ALL_S", "ALL_E", "MIX"};
                    String flagMode = flagModes[v % flagModes.length];

                    for (int d = 0; d < detailPerVoucher; d++) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("orgl1", orgl1);
                        map.put("oucode", oucode);
                        map.put("orgL1AndOuCode", orgL1AndOuCode);

                        map.put("pk_voucher", pk_voucher);
                        map.put("pk_detail", String.format("%08d", rand.nextInt(100000000)));
                        // 控制processFlag
                        String pf;
                        if ("ALL_S".equals(flagMode)) {
                            pf = "S";
                        } else if ("ALL_E".equals(flagMode)) {
                            pf = "E";
                        } else { // MIX
                            pf = (d % 2 == 0) ? "S" : "E";
                        }
                        map.put("processFlag", pf);

                        // 其它业务字段，按需填充
                        map.put("userJeSourceName", "应付款");
                        map.put("type", type);
                        // jeBatchName规则
                        map.put("jeBatchName", "AP" + oucode + time2StrCust("yyyyMMdd") + "-" + pk_voucher + "-" + map.get("pk_detail"));
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

                        all.add(map);
                    }
                }
            }
        }
        return all;
    }
}

