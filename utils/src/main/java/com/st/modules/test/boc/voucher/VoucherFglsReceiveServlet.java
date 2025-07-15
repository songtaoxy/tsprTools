package com.st.modules.test.boc.voucher;

import com.st.modules.config.DynamicAppConfig;
import com.st.modules.constant.FileConst;
import com.st.modules.enums.StatusVoucherEnum;
import com.st.modules.file.FileRenameUtils;
import com.st.modules.file.clean.FileCleanupManager;
import com.st.modules.file.ftpv1.FtpDownLoadUtils;
import com.st.modules.file.ftpv1.FtpUtils;
import com.st.modules.file.tar.TarUtils;
import lombok.SneakyThrows;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class VoucherFglsReceiveServlet {

    @SneakyThrows
    public static void main(String[] args) {
        String fglsFptDir = DynamicAppConfig.get("ftp.remotepath");
//        String fglsFptDir = FileConst.fglsDistributeDir;
        String fglsReceiveDir = FileConst.fglsReceiveDir;


        FtpDownLoadUtils.batchDownload(fglsFptDir, fglsReceiveDir, f -> f.getName().endsWith(".tar.gz"));

        // 1. 列出所有tar.gz文件
        List<File> tgzFiles = listAllTarGzFiles(fglsReceiveDir);

        // 2. 并行处理
        tgzFiles.parallelStream().forEach(file -> {
            handleAndUpdateDbs(file,fglsFptDir);
        });
    }

    @SneakyThrows
    private static void handleAndUpdateDbs(File file, String fglsFtpDir) {

        // ftp path
        String name = file.getName();
        String ftpPath = fglsFtpDir + name;

        // build txt path
        String localAbsolutePathTar = file.getAbsolutePath();
        String tarDir = file.getParent();
        String txtFileName = name.replace("tar.gz", "txt");
        String localAbsolutePathText = tarDir + File.separator + txtFileName;

        // 2.1 解压，获得txt
        TarUtils.extractTarGz(localAbsolutePathTar,tarDir);
        File txt = new File(localAbsolutePathText);

        // 2.2 读取txt，封装list
        List<VoucherTransmitBO> lines = parseTxtToList(txt);

        // 2.3 按voucherCode分组
        Map<String, List<VoucherTransmitBO>> grouped = lines.stream()
                .collect(Collectors.groupingBy(VoucherTransmitBO::getVoucherCode));

        // 2.4 处理分组
        for (Map.Entry<String, List<VoucherTransmitBO>> entry : grouped.entrySet()) {
            String voucherCode = entry.getKey();
            List<VoucherTransmitBO> list = entry.getValue();
            boolean allSuccess = list.stream().allMatch(line -> "S".equals(line.getProcessFlag()));
            String globalStat = allSuccess ? "success" : "fail";

            String status = null;
            if ("success".equals(globalStat)) {
                status = StatusVoucherEnum.FGLS_OK.getCode();
            }else {
                status = StatusVoucherEnum.FGLS_FAIL.getCode();
            }
            updateVoucherStatusToIssued(voucherCode,status);
        }

        // backup: ftp
        FtpUtils.renameRemoteFile(ftpPath, ftpPath + ".bak");

        // backup: local. 原地重命名
        String bakPost =  "."+UUID.randomUUID()+".bak";
        String tarBakName = name + bakPost;
        String textBakName = txtFileName + bakPost;
        FileRenameUtils.rename(localAbsolutePathTar, tarBakName);
        FileRenameUtils.rename(localAbsolutePathText, textBakName );

        // 延迟清理
        FileCleanupManager.register(tarDir + tarBakName);
        FileCleanupManager.register(tarDir +textBakName);

    }

    public static List<File> listAllTarGzFiles(String dir) {
        File folder = new File(dir);
        File[] files = folder.listFiles((d, name) -> name.endsWith(".tar.gz"));
        return files == null ? Collections.emptyList() : Arrays.asList(files);
    }


    @SneakyThrows
    public static List<VoucherTransmitBO> parseTxtToList(File txt)  {

        List<VoucherTransmitBO> list;
        list = new ArrayList<VoucherTransmitBO>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(txt), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
//
//                VoucherTransmitBO voucherTransmitBO = VoucherTransmitBO.builder()
//                        .userJeSourceName(parts[1]) // 跳过全局行号
//                        .userJeCategoryName(parts[2])
//                        .jeBatchName(parts[3])
//                        .jeHeaderName(parts[4])
//                        .transactionNum(parts[5])
//                        .accountingDate(parts[6])
//                        .currencyCode(parts[7])
//                        .currencyRate(parts[8])
//                        .imageNumber(parts[9])
//                        .imageAddress(parts[10]) //跳过行号
//                        .segment1(parts[12])
//                        .segment2(parts[13])
//                        .segment3(parts[14])
//                        .segment4(parts[15])
//                        .segment5(parts[16])
//                        .segment6(parts[17])
//                        .segment7(parts[18])
//                        .segment8(parts[19])
//                        .segment9(parts[20])
//                        .enteredDr(parts[21])
//                        .enteredCr(parts[22])
//                        .accountedDr(parts[23])
//                        .accountedCr(parts[24])
//                        .lineDesc(parts[25])
//                        .reference21(parts[26])
//                        .reference22(parts[27])
//                        .reference23(parts[28])
//                        .reference24(parts[29])
//                        .reference25(parts[30])
//                        .reference26(parts[31])
//                        .reference27(parts[32])
//                        .reference28(parts[33])
//                        .reference29(parts[34])
//                        .reference30(parts[35])
//                        .glSlLinkId(parts[36])
//                        .glSlLinkTable(parts[37])
//                        .fileName(parts[38]) // 经费总账返回的8个字段
//                        .fileDate(parts[39])
//                        .processFlag(parts[40])
//                        .errorMessage(parts[41])
//                        .glDocNumber(parts[42])
//                        .glDocAccountingDate(parts[43])
//                        .glJeHeaderId(parts[44])
//                        .glJeLineNum(parts[45])
//                        .build();


                VoucherTransmitBO voucherTransmitBO = VoucherTransmitBO.builder()
//                        .userJeSourceName(parts[1]) // 跳过全局行号
//                        .userJeCategoryName(parts[2])
                        .jeBatchName(parts[3])
//                        .jeHeaderName(parts[4])
//                        .transactionNum(parts[5])
//                        .accountingDate(parts[6])
//                        .currencyCode(parts[7])
//                        .currencyRate(parts[8])
//                        .imageNumber(parts[9])
//                        .imageAddress(parts[10]) //跳过行号
//                        .segment1(parts[12])
//                        .segment2(parts[13])
//                        .segment3(parts[14])
//                        .segment4(parts[15])
//                        .segment5(parts[16])
//                        .segment6(parts[17])
//                        .segment7(parts[18])
//                        .segment8(parts[19])
//                        .segment9(parts[20])
//                        .enteredDr(parts[21])
//                        .enteredCr(parts[22])
//                        .accountedDr(parts[23])
//                        .accountedCr(parts[24])
//                        .lineDesc(parts[25])
//                        .reference21(parts[26])
//                        .reference22(parts[27])
//                        .reference23(parts[28])
//                        .reference24(parts[29])
//                        .reference25(parts[30])
//                        .reference26(parts[31])
//                        .reference27(parts[32])
//                        .reference28(parts[33])
//                        .reference29(parts[34])
//                        .reference30(parts[35])
//                        .glSlLinkId(parts[36])
//                        .glSlLinkTable(parts[37])
//                        .fileName(parts[38]) // 经费总账返回的8个字段
//                        .fileDate(parts[39])
                        .processFlag(parts[38])
//                        .errorMessage(parts[41])
//                        .glDocNumber(parts[42])
//                        .glDocAccountingDate(parts[43])
//                        .glJeHeaderId(parts[44])
//                        .glJeLineNum(parts[45])
                        .voucherCode(parts[3].split("-")[1])
                        .build();





                list.add(voucherTransmitBO);
            }
        }
        return list;
    }

    public static boolean updateVoucherStatusToIssued(String voucherCode,String status) {
        boolean flag = false;
        // 推荐使用JDBC、MyBatis等持久层实现
        String sql = "UPDATE gl_voucher SET free9= '"+ status+ "' WHERE pk_voucher = '" + voucherCode+"'";
        // 执行sql...
        flag=true;
        return flag;
    }
}
