package com.st.modules.test.boc.voucher;


import com.st.modules.file.clean.FileCleanupManager;
import com.st.modules.time.TimeUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class VoucherV1 {

        public static final String appPath = System.getProperty("user.dir");

        public static void main(String[] args) throws Exception {
            // 1. 查询凭证数据（Map结构）(模拟)
            List<Map<String, Object>> base = Arrays.asList(
                    TestDatas.mapOf("type", "收款", "no", "SK001", "date", "2024-05-25", "amount", "1000", "remark", "A客户回款", "orgL1AndOuCode", "AAA"),
                    TestDatas.mapOf("type", "收款", "no", "SK002", "date", "2024-05-26", "amount", "1500", "remark", "B客户回款", "orgL1AndOuCode", "BBB"),
                    TestDatas.mapOf("type", "付款", "no", "FK001", "date", "2024-05-25", "amount", "800", "remark", "供应商付款", "orgL1AndOuCode", "AAA"),
                    TestDatas.mapOf("type", "报销", "no", "BX001", "date", "2024-05-24", "amount", "300", "remark", "差旅报销", "orgL1AndOuCode", "AAA"),
                    TestDatas.mapOf("type", "报销", "no", "BX002", "date", "2024-05-26", "amount", "500", "remark", "办公用品", "orgL1AndOuCode", "BBB")
            );

            List<Map<String, Object>> allTestData = TestDatas.generateTestVouchers(base, 1000, 350);


            // 2, convert: list<map> -> list<BO>
            Map<String, Object> extraMap = new HashMap<>();
            List<VoucherTransmitBO> voucherTransmitBOS = VoucherTransmitBO.mapListToBOList(allTestData,extraMap);

            // 根据”一级行及OU“分组
            Map<String, List<VoucherTransmitBO>> orgAndOuGroup = voucherTransmitBOS.stream().collect(Collectors.groupingBy(VoucherTransmitBO::getOrgL1AndOuCode));


            // 3. 按类型分组
            Map<String, List<VoucherTransmitBO>> typeGroup =voucherTransmitBOS.stream().collect(Collectors.groupingBy(VoucherTransmitBO::getUserJeCategoryName));

            // 4. 类型顺序
            List<String> typeOrder = new ArrayList<>(typeGroup.keySet());
            Collections.sort(typeOrder); // 可自定义顺序

            // 5. 计算每类全局编号起始值
            Map<String, Integer> typeStartGlobalIndex = new HashMap<>();
            int startIdx = 1;
            for (String type : typeOrder) {
                typeStartGlobalIndex.put(type, startIdx);
                startIdx += typeGroup.get(type).size();
            }

            // 6. 并行流式生成格式化数据
            List<String> formattedAll = VoucherUtils.formatVouchersParallel(typeOrder, typeGroup, typeStartGlobalIndex);
//            System.out.println(String.join("\n", formattedAll));

            // 7. 写入文件(txt)
            String fileName = TimeUtils.time2Str();
            Map<String, String> filePath = VoucherUtils.buildFilePath(null);
            String tempFilePath = filePath.get("txtFilePath");
            File tempFile = VoucherUtils.wirteTxtFile(tempFilePath, formattedAll);

            // 8, 压缩: 将txt压缩成tar.gz
            File tempFileTarGz =VoucherUtils.compressWithTargz(tempFile,null);

            log.info("txt:"+tempFile.getAbsolutePath());
            log.info("targz:"+tempFileTarGz.getAbsolutePath());

            boolean flag=false;
            // 9. FTP上传tar.gz
//            flag = ftpUpload(filePath, "/目标目录/voucher_export.txt");

//            flag =true;
            // 10, 延迟清理文件: 如果ftp上传成功, 则清理文件(延迟)
            if(flag){
                FileCleanupManager.register(tempFile);
                FileCleanupManager.register(tempFileTarGz);
            }
        }


    // 辅助map构造
    public static Map<String, Object> mapOf(Object... kv) {
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            map.put(String.valueOf(kv[i]), kv[i + 1]);
        }
        return map;
    }
}

