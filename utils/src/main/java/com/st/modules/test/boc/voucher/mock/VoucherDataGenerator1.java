package com.st.modules.test.boc.voucher.mock;

import java.util.*;

/**
 * 模拟从数据库查询数据
 * <pre>
 *  {@code
 *        List<Map<String, Object>> allVouchers = Arrays.asList(
 *                     // 收款类型
 *                     mapOf("type", "收款", "no", "SK001", "date", "2024-05-25", "amount", "1000", "remark", "A客户回款","excrate2","1", "accountcode", "1000", "localdebitamount","100.100","localcreditamount", "200.00","debitamount", "100.00","creditamount","100.00","explanation","fdfadfasdf","orgL1AndOuCode","AAAAAA123456789","jeBatchName","AP8882025052-60010000-223353535","TestDatas","S"),
 *                     mapOf("type", "收款", "no", "SK002", "date", "2024-05-26", "amount", "1500", "remark", "B客户回款","excrate2","1", "accountcode", "1000", "localdebitamount","100.100","localcreditamount", "200.00","debitamount", "100.00","creditamount","100.00","explanation","fdfadfasdf","orgL1AndOuCode","BBBBB345678912","jeBatchName","AP8882025052-60010000-223353536","TestDatas","S"),
 *                     // 付款类型
 *                     mapOf("type", "付款", "no", "FK001", "date", "2024-05-25", "amount", "800", "remark", "供应商付款","excrate2","1", "accountcode", "1000", "localdebitamount","100.100","localcreditamount", "200.00","debitamount", "100.00","creditamount","100.00","explanation","fdfadfasdf","orgL1AndOuCode","AAAAAA123456789","jeBatchName","AP8882025052-60010000-223353535","TestDatas","E"),
 *                     // 报销类型
 *                     mapOf("type", "报销", "no", "BX001", "date", "2024-05-24", "amount", "300", "remark", "差旅报销","excrate2","1", "accountcode", "1000", "localdebitamount","100.100","localcreditamount", "200.00","debitamount", "100.00","creditamount","100.00","explanation","fdfadfasdf","orgL1AndOuCode","AAAAAA123456789","jeBatchName","AP8882025052-60010000-223353538","TestDatas","S"),
 *                     mapOf("type", "报销", "no", "BX002", "date", "2024-05-26", "amount", "500", "remark", "办公用品","excrate2","1", "accountcode", "1000", "localdebitamount","100.100","localcreditamount", "200.00","debitamount", "100.00","creditamount","100.00","explanation","fdfadfasdf","orgL1AndOuCode","BBBBB345678912","jeBatchName","AP8882025052-60010000-223353536","TestDatas","E")
 *             );
 *  }
 * </pre>
 * 对于上述数据, 先根据orgL1AndOuCode, 每类各造1000条数据; 然后, 在各自类别类, 再根据type （收款、付款、报销）, 各造300+条测试数据, 然后再在各自的type内, 同一个jeBatchName下同时满足: 至少5条记录,且这5条的TestDatas全是S,这5条的TestDatas全是E, 这5条的TestDatas既有S也有E
 *
 *<pre>
 * sql: 三张表, 应付单, 凭证, 凭证分录; 先从应付单查询所有“凭证状态未下发”的凭证id, 其中id是凭证表的主键; 根据id查询, 从凭证表查询所有的凭证; 再根据凭证表的主键查询分录表所有的凭证分录记录
 *</pre>
 */
public class VoucherDataGenerator1 {

    // 用原始字段、格式模板生成一条
    private static Map<String, Object> mapOf(
            String type, String no, String date, String amount, String remark,
            String excrate2, String accountcode, String localdebitamount,
            String localcreditamount, String debitamount, String creditamount,
            String explanation, String orgL1AndOuCode, String jeBatchName, String TestDatas
    ) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("type", type);
        m.put("no", no);
        m.put("date", date);
        m.put("amount", amount);
        m.put("remark", remark);
        m.put("excrate2", excrate2);
        m.put("accountcode", accountcode);
        m.put("localdebitamount", localdebitamount);
        m.put("localcreditamount", localcreditamount);
        m.put("debitamount", debitamount);
        m.put("creditamount", creditamount);
        m.put("explanation", explanation);
        m.put("orgL1AndOuCode", orgL1AndOuCode);
        m.put("jeBatchName", jeBatchName);
        m.put("TestDatas", TestDatas);
        return m;
    }

    public static List<Map<String, Object>> generateTestData() {
        List<Map<String, Object>> list = new ArrayList<>();
        // 你可以增减 org 或 type
        String[] orgs = {"AAAAA123456789", "BBBBB345678912"};
        String[] types = {"收款", "付款", "报销"};
        int perOrg = 1000;

        // 原始数据模板，每个type给1条, 用于字段格式
        Map<String, Object>[] templates = new Map[]{
                mapOf("收款", "SK001", "2024-05-25", "1000", "A客户回款", "1", "1000", "100.100", "200.00", "100.00", "100.00", "fdfadfasdf", "AAAAAA123456789", "AP8882025052-60010000-223353535", "S"),
                mapOf("收款", "SK002", "2024-05-26", "1500", "B客户回款", "1", "1000", "100.100", "200.00", "100.00", "100.00", "fdfadfasdf", "BBBBB345678912", "AP8882025052-60010000-223353536", "S"),
                mapOf("付款", "FK001", "2024-05-25", "800", "供应商付款", "1", "1000", "100.100", "200.00", "100.00", "100.00", "fdfadfasdf", "AAAAAA123456789", "AP8882025052-60010000-223353535", "E"),
                mapOf("报销", "BX001", "2024-05-24", "300", "差旅报销", "1", "1000", "100.100", "200.00", "100.00", "100.00", "fdfadfasdf", "AAAAAA123456789", "AP8882025052-60010000-223353538", "S"),
                mapOf("报销", "BX002", "2024-05-26", "500", "办公用品", "1", "1000", "100.100", "200.00", "100.00", "100.00", "fdfadfasdf", "BBBBB345678912", "AP8882025052-60010000-223353536", "E")
        };

        // 每个org生成数据
        for (String org : orgs) {
            int perType = perOrg / types.length; // 约333
            int idx = 1;
            for (String type : types) {
                int batchBase = 100;
                int total = perType;
                int remain = total % 15;
                int batchCount = (total - remain) / 15;
                // 造 batch，每 batch15条（5 S, 5 E, 5 混合）
                for (int i = 0; i < batchCount; i++) {
                    String jeBatchName = String.format("AP8882025052-60010000-%d-%s-%03d", System.currentTimeMillis() % 100000000, org, i);
                    // 5 S
                    for (int k = 0; k < 5; k++)
                        list.add(makeOneFromTemplate(templates, type, org, jeBatchName, idx++, "S"));
                    // 5 E
                    for (int k = 0; k < 5; k++)
                        list.add(makeOneFromTemplate(templates, type, org, jeBatchName, idx++, "E"));
                    // 5 S/E 混合
                    for (int k = 0; k < 5; k++)
                        list.add(makeOneFromTemplate(templates, type, org, jeBatchName, idx++, k % 2 == 0 ? "S" : "E"));
                }
                // 剩余条补齐
                for (int r = 0; r < remain; r++) {
                    String jeBatchName = String.format("AP8882025052-60010000-%d-%s-%03d", System.currentTimeMillis() % 100000000, org, 1000 + r);
                    String sOrE = r % 2 == 0 ? "S" : "E";
                    list.add(makeOneFromTemplate(templates, type, org, jeBatchName, idx++, sOrE));
                }
            }
        }
        return list;
    }

    // 从模板找字段，组装新map（字段值/格式100%复用模板）
    private static Map<String, Object> makeOneFromTemplate(Map<String, Object>[] templates, String type, String org, String jeBatchName, int noIdx, String testDatas) {
        Map<String, Object> base = Arrays.stream(templates)
                .filter(t -> t.get("type").equals(type) && t.get("orgL1AndOuCode").equals(org))
                .findFirst()
                .orElse(Arrays.stream(templates).filter(t -> t.get("type").equals(type)).findFirst().get());

        // 字段和格式100%一致
        return mapOf(
                type,
                (type.equals("收款") ? "SK" : type.equals("付款") ? "FK" : "BX") + String.format("%04d", noIdx),
                (String) base.get("date"),
                (String) base.get("amount"),
                (String) base.get("remark"),
                (String) base.get("excrate2"),
                (String) base.get("accountcode"),
                (String) base.get("localdebitamount"),
                (String) base.get("localcreditamount"),
                (String) base.get("debitamount"),
                (String) base.get("creditamount"),
                (String) base.get("explanation"),
                org,
                jeBatchName,
                testDatas
        );
    }

    public static void main(String[] args) {
        List<Map<String, Object>> allVouchers = generateTestData();
        System.out.println("共生成: " + allVouchers.size() + " 条数据");
        // 可遍历/打印查看
        allVouchers.stream().limit(5).forEach(System.out::println);
    }
}


