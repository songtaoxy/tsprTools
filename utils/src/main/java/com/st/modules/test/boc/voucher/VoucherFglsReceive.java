package com.st.modules.test.boc.voucher;

import com.st.modules.constant.FileConst;
import com.st.modules.enums.GlVoucherStatusEnum;
import com.st.modules.file.ftp.FtpUtils;
import com.st.modules.file.tar.TarUtils;
import lombok.SneakyThrows;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

public class VoucherFglsReceive {

    @SneakyThrows
    public static void main(String[] args) {

        String fglsReceiveDir = FileConst.fglsReceiveDir;
//        FtpUtils.batchDownload("remoteDir", fglsReceiveDir, f -> f.getName().endsWith(".tar. gz"));

        // 1. 列出所有tar.gz文件
        List<File> tgzFiles = listAllTarGzFiles(fglsReceiveDir);

        // 2. 并行处理
        tgzFiles.parallelStream().forEach(file -> {
            handleAndUpdateDbs(file);
        });
    }

    private static void handleAndUpdateDbs(File file) {
        String txtFileName = file.getName().replace("tar.gz", "txt");
        String tarDir = file.getParent();

        // 2.1 解压，获得txt
//        File txt = untarGzToTxt(file);
        TarUtils.extractTarGz(file.getAbsolutePath(),tarDir);
        File txt = new File(tarDir + File.separator + txtFileName);

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
                status = GlVoucherStatusEnum.FGLS_OK.getCode();
            }else {
                status = GlVoucherStatusEnum.FGLS_FAIL.getCode();
            }
            updateVoucherStatusToIssued(voucherCode,status);
        }
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
                        .voucherCode(parts[3].split("-")[2])
                        .build();





                list.add(voucherTransmitBO);
            }
        }
        return list;
    }

    public static void updateVoucherStatusToIssued(String voucherCode,String status) {
        // 推荐使用JDBC、MyBatis等持久层实现
        String sql = "UPDATE gl_voucher SET free9= '"+ status+ "' WHERE pk_voucher = '" + voucherCode+"'";
        // 执行sql...
    }
}
