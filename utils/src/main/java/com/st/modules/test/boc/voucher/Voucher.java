package com.st.modules.test.boc.voucher;



    import com.st.modules.config.DynamicAppConfig;
    import com.st.modules.constant.FileConst;
    import com.st.modules.enums.v1.ModulesEnum;
    import com.st.modules.file.clean.FileCleanupManager;
    import com.st.modules.serialNumber.DailySystemSerialNoGenerator;
    import com.st.modules.test.boc.voucher.mock.VoucherDataGenerator1;
    import com.st.modules.time.TimeUtils;
    import lombok.SneakyThrows;
    import lombok.extern.slf4j.Slf4j;

    import java.io.File;
    import java.util.*;
    import java.util.stream.Collectors;

    import static com.st.modules.file.deprecated.ftpv1.FtpUploadPathchUtils.uploadSingleFileWithRemotePath;

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

            String module = ModulesEnum.FGLS.getName();
            String serialnumber = DailySystemSerialNoGenerator.getInstance().nextSerial(module);
            baseDatas.put("dateStr", dateStr);
            baseDatas.put("dateTimeStr", dateTimeStr);
            baseDatas.put("serialnumber", serialnumber);


            List<Map<String, Object>> allTestData = VoucherDataGenerator1.generateTestData();
            allTestData = VoucherDataGenerator.generateTestData(3, 1000);


            // 2, convert: list<map> -> list<BO>
            Map<String, Object> extraMap = new HashMap<>();
            List<VoucherTransmitBO> voucherTransmitBOS = VoucherTransmitBO.mapListToBOList(allTestData, extraMap);

            // 根据”一级行及OU“分组
            Map<String, List<VoucherTransmitBO>> orgAndOuGroup = voucherTransmitBOS.stream().collect(Collectors.groupingBy(VoucherTransmitBO::getOrgL1AndOuCode));

            orgAndOuGroup.entrySet().parallelStream().forEach(entry -> {
                String orgOuKey = entry.getKey();
                baseDatas.put("orgOuKey", orgOuKey);

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
        File tarGzFile = VoucherUtils.compressWithTargz(txtFile, baseDatas);

        // 9. FTP上传（可选，演示为假）
        boolean ftpSuccess = false;
        ftpSuccess = uploadSingleFileWithRemotePath(tarGzFile, DynamicAppConfig.get("ftp.remotepath") + tarGzFile.getName());

        // 10. 成功后延迟清理
        if (ftpSuccess) {
            FileCleanupManager.register(txtFile);
            FileCleanupManager.register(tarGzFile);
        }

        // 日志输出
        log.info("[OrgOU: " + baseDatas.get("orgOuKey") + "]");
        log.info("TXT文件: " + txtFile.getAbsolutePath());
        log.info("TAR.GZ文件: " + tarGzFile.getAbsolutePath());
    }


        }


