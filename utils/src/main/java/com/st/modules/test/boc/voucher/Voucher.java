package com.st.modules.test.boc.voucher;



    import com.google.inject.Module;
    import com.st.modules.config.DynamicAppConfig;
    import com.st.modules.constant.FileConst;
    import com.st.modules.constant.ModulesConst;
    import com.st.modules.file.clean.FileCleanupManager;
    import com.st.modules.serialNumber.DailySystemSerialNoGenerator;
    import com.st.modules.time.TimeUtils;
    import lombok.SneakyThrows;
    import lombok.extern.slf4j.Slf4j;

    import java.io.File;
    import java.util.*;
    import java.util.stream.Collectors;
@Slf4j
public class Voucher {

        public static final String appPath = System.getProperty(FileConst.userDir);

        @SneakyThrows
        public static void main(String[] args) throws Exception {

            log.info(DynamicAppConfig.get("ftp.pass"));

            // 构建当前请求统一基础数据
            Map<String, String> baseDatas = new HashMap<>();
            String dateStr = TimeUtils.time2StrCust("yyyyMMdd");
            String dateTimeStr = TimeUtils.time2StrCust("yyyyMMddHHmmss");
            String serialnumber = DailySystemSerialNoGenerator.getInstance().nextSerial(ModulesConst.fgls);
            baseDatas.put("dateStr", dateStr);
            baseDatas.put("dateTimeStr", dateTimeStr);
            baseDatas.put("serialnumber", serialnumber);


            // 1. 查询凭证数据（Map结构）(模拟)
            List<Map<String, Object>> base = Arrays.asList(
                    TestDatas.mapOf("type", "收款", "no", "SK001", "date", "2024-05-25", "amount", "1000", "remark", "A客户回款", "orgL1AndOuCode", "AAAAAA123456789"),
                    TestDatas.mapOf("type", "收款", "no", "SK002", "date", "2024-05-26", "amount", "1500", "remark", "B客户回款", "orgL1AndOuCode", "BBBBB345678912"),
                    TestDatas.mapOf("type", "付款", "no", "FK001", "date", "2024-05-25", "amount", "800", "remark", "供应商付款", "orgL1AndOuCode", "AAAAAA123456789"),
                    TestDatas.mapOf("type", "报销", "no", "BX001", "date", "2024-05-24", "amount", "300", "remark", "差旅报销", "orgL1AndOuCode", "AAAAAA123456789"),
                    TestDatas.mapOf("type", "报销", "no", "BX002", "date", "2024-05-26", "amount", "500", "remark", "办公用品", "orgL1AndOuCode", "BBBBB345678912")
            );
//            List<Map<String, Object>> allTestData = TestDatas.generateTestVouchers(base, 1000, 350);
            List<Map<String, Object>> allTestData = VoucherDataGenerator.generateTestData();


            // 2, convert: list<map> -> list<BO>
            Map<String, Object> extraMap = new HashMap<>();
            List<VoucherTransmitBO> voucherTransmitBOS = VoucherTransmitBO.mapListToBOList(allTestData, extraMap);

            // 根据”一级行及OU“分组
            Map<String, List<VoucherTransmitBO>> orgAndOuGroup = voucherTransmitBOS.stream().collect(Collectors.groupingBy(VoucherTransmitBO::getOrgL1AndOuCode));

            orgAndOuGroup.entrySet().parallelStream().forEach(entry -> {
                String orgOuKey = entry.getKey();
                baseDatas.put("orgOuKey",orgOuKey);

                List<VoucherTransmitBO> groupBOs = entry.getValue();
                processSingleOrgOuGroup(baseDatas, groupBOs);
            });
        }

            @SneakyThrows
            private static void processSingleOrgOuGroup(Map<String, String> baseDatas, List<VoucherTransmitBO> bos) {

                // 3. 按类型分组
                Map<String, List<VoucherTransmitBO>> typeGroup = bos.stream()
                        .collect(Collectors.groupingBy(VoucherTransmitBO::getUserJeCategoryName));

                // 4. 类型顺序（自定义排序可替换）
                List<String> typeOrder = new ArrayList<>(typeGroup.keySet());
                Collections.sort(typeOrder);

                // 5. 每类全局编号起始值
                Map<String, Integer> typeStartGlobalIndex = new HashMap<>();
                int startIdx = 1;
                for (String type : typeOrder) {
                    typeStartGlobalIndex.put(type, startIdx);
                    startIdx += typeGroup.get(type).size();
                }

                // 6. 并行格式化
                List<String> formattedLines = VoucherUtils.formatVouchersParallel(typeOrder, typeGroup, typeStartGlobalIndex);

                // 7. 写入文件 fgls, 经费总账
                Map<String, String> pathMap = VoucherUtils.buildFilePath(baseDatas);
                String txtPath = pathMap.get("txtFilePath");
                File txtFile = VoucherUtils.wirteTxtFile(txtPath, formattedLines);

                // 8. 压缩
                File tarGzFile = VoucherUtils.compressWithTargz(txtFile,baseDatas);

                // 9. FTP上传（可选，演示为假）
                boolean ftpSuccess = false;
//        ftpSuccess = FtpUtils.upload(tarGzFile, "/upload/" + tarGzFile.getName());

                // 10. 成功后延迟清理
                if (ftpSuccess) {
                    FileCleanupManager.register(txtFile);
                    FileCleanupManager.register(tarGzFile);
                }

                // 日志输出
                System.out.println("[OrgOU: " + baseDatas.get("orgOuKey") + "]");
                System.out.println("TXT文件: " + txtFile.getAbsolutePath());
                System.out.println("TAR.GZ文件: " + tarGzFile.getAbsolutePath());
            }

        }


