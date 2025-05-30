package com.st.modules.test.boc.voucher.mock;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 模拟从数据库查询数据
 * <pre>
 *  {@code
 *        List<Map<String, Object>> allVouchers = Arrays.asList(
 *                     // 收款类型
 *                     mapOf("type", "收款", "no", "SK001", "date", "2024-05-25", "amount", "1000", "remark", "A客户回款","excrate2","1", "accountcode", "1000", "localdebitamount","100.100","localcreditamount", "200.00","debitamount", "100.00","creditamount","100.00","explanation","fdfadfasdf","orgL1AndOuCode","AAA"),
 *                     mapOf("type", "收款", "no", "SK002", "date", "2024-05-26", "amount", "1500", "remark", "B客户回款","excrate2","1", "accountcode", "1000", "localdebitamount","100.100","localcreditamount", "200.00","debitamount", "100.00","creditamount","100.00","explanation","fdfadfasdf","orgL1AndOuCode","BBB"),
 *                     // 付款类型
 *                     mapOf("type", "付款", "no", "FK001", "date", "2024-05-25", "amount", "800", "remark", "供应商付款","excrate2","1", "accountcode", "1000", "localdebitamount","100.100","localcreditamount", "200.00","debitamount", "100.00","creditamount","100.00","explanation","fdfadfasdf","orgL1AndOuCode","AAA"),
 *                     // 报销类型
 *                     mapOf("type", "报销", "no", "BX001", "date", "2024-05-24", "amount", "300", "remark", "差旅报销","excrate2","1", "accountcode", "1000", "localdebitamount","100.100","localcreditamount", "200.00","debitamount", "100.00","creditamount","100.00","explanation","fdfadfasdf","orgL1AndOuCode","AAA"),
 *                     mapOf("type", "报销", "no", "BX002", "date", "2024-05-26", "amount", "500", "remark", "办公用品","excrate2","1", "accountcode", "1000", "localdebitamount","100.100","localcreditamount", "200.00","debitamount", "100.00","creditamount","100.00","explanation","fdfadfasdf","orgL1AndOuCode","BBB")
 *             );
 *  }
 * </pre>
 * 对于上述数据, 先根据orgL1AndOuCode, 每类各造1000条数据; 然后, 在各自类别类, 再根据apptype （收款、付款、报销）, 各造300+条测试数据
 *
 *<pre>
 * sql: 三张表, 应付单, 凭证, 凭证分录; 先从应付单查询所有“凭证状态未下发”的凭证id, 其中id是凭证表的主键; 根据id查询, 从凭证表查询所有的凭证; 再根据凭证表的主键查询分录表所有的凭证分录记录
 *</pre>
 */
public class TestDatas {
    public static void main(String[] args) {
        List<Map<String, Object>> base = Arrays.asList(
                TestDatas.mapOf("type", "收款", "no", "SK001", "date", "2024-05-25", "amount", "1000", "remark", "A客户回款", "orgL1AndOuCode", "AAAAAA123456789"),
                TestDatas.mapOf("type", "收款", "no", "SK002", "date", "2024-05-26", "amount", "1500", "remark", "B客户回款", "orgL1AndOuCode", "BBBBB345678912"),
                TestDatas.mapOf("type", "付款", "no", "FK001", "date", "2024-05-25", "amount", "800", "remark", "供应商付款", "orgL1AndOuCode", "AAAAAA123456789"),
                TestDatas.mapOf("type", "报销", "no", "BX001", "date", "2024-05-24", "amount", "300", "remark", "差旅报销", "orgL1AndOuCode", "AAAAAA123456789"),
                TestDatas.mapOf("type", "报销", "no", "BX002", "date", "2024-05-26", "amount", "500", "remark", "办公用品", "orgL1AndOuCode", "BBBBB345678912")
        );

        List<Map<String, Object>> result = TestDatas.generateTestVouchers(base, 1000, 350);
        System.out.println("生成总记录: " + result.size());
        result.stream().limit(5).forEach(System.out::println);
    }

    public static List<Map<String, Object>> generateTestVouchers(List<Map<String, Object>> baseData, int totalPerOrg, int minPerType) {
        Map<String, List<Map<String, Object>>> groupedByOrg = baseData.stream()
                .collect(Collectors.groupingBy(m -> m.get("orgL1AndOuCode").toString()));

        List<Map<String, Object>> allGenerated = new ArrayList<>();

        for (Map.Entry<String, List<Map<String, Object>>> orgEntry : groupedByOrg.entrySet()) {
            String org = orgEntry.getKey();
            List<Map<String, Object>> vouchersByOrg = orgEntry.getValue();

            // 按type分组
            Map<String, List<Map<String, Object>>> groupedByType = vouchersByOrg.stream()
                    .collect(Collectors.groupingBy(v -> v.get("type").toString()));

            // 每type生成minPerType条
            for (Map.Entry<String, List<Map<String, Object>>> typeEntry : groupedByType.entrySet()) {
                String type = typeEntry.getKey();
                List<Map<String, Object>> typeData = typeEntry.getValue();

                for (int i = 1; i <= minPerType; i++) {
                    Map<String, Object> base = typeData.get(i % typeData.size());
                    Map<String, Object> copy = new HashMap<>(base);
                    copy.put("no", typeCode(type) + String.format("%04d", i) + "_" + org);
                    copy.put("amount", 100 + i);
                    copy.put("remark", copy.get("remark") + "_复制" + i);
                    copy.put("id", UUID.randomUUID().toString());
                    allGenerated.add(copy);
                }
            }

            // 补齐每 org 总数到 totalPerOrg
            while (allGenerated.stream().filter(m -> m.get("orgL1AndOuCode").equals(org)).count() < totalPerOrg) {
                Map<String, Object> base = vouchersByOrg.get(0);
                Map<String, Object> copy = new HashMap<>(base);
                copy.put("no", "补" + UUID.randomUUID().toString().substring(0, 6));
                copy.put("remark", "补充数据");
                copy.put("id", UUID.randomUUID().toString());
                allGenerated.add(copy);
            }
        }

        return allGenerated;
    }

    private static String typeCode(String type) {
        switch (type) {
            case "收款": return "SK";
            case "付款": return "FK";
            case "报销": return "BX";
            default: return "XX";
        }
    }

    public static Map<String, Object> mapOf(Object... kvs) {
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < kvs.length; i += 2) {
            map.put((String) kvs[i], kvs[i + 1]);
        }
        return map;
    }
}
