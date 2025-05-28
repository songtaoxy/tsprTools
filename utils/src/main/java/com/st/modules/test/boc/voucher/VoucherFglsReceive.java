package com.st.modules.test.boc.voucher;

import com.st.modules.constant.FileConst;
import com.st.modules.file.ftp.FtpUtils;
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
        FtpUtils.batchDownload("remoteDir", fglsReceiveDir, f -> f.getName().endsWith(".tar. gz"));

        // 1. 列出所有tar.gz文件
        List<File> tgzFiles = listAllTarGzFiles(fglsReceiveDir);

        // 2. 并行处理
        tgzFiles.parallelStream().forEach(file -> {
            // 2.1 解压，获得txt
            File txt = untarGzToTxt(file);

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

                if ("success".equals(globalStat)) {
                    // 2.5 DB操作：更新应付单表
                    updateVoucherStatusToIssued(voucherCode);
                }
            }
        });
    }

    public static List<File> listAllTarGzFiles(String dir) {
        File folder = new File(dir);
        File[] files = folder.listFiles((d, name) -> name.endsWith(".tar.gz"));
        return files == null ? Collections.emptyList() : Arrays.asList(files);
    }

    // 一个tar.gz, 一个txt
    @SneakyThrows
    public static File untarGzToTxt(File tarGz)  {
        // 依赖 commons-compress
        File destDir = new File(tarGz.getParentFile(), "unzipped_" + tarGz.getName().replace(".tar.gz", ""));
        destDir.mkdirs();
        try (InputStream fi = new FileInputStream(tarGz);
             InputStream gzi = new GZIPInputStream(fi);
             TarArchiveInputStream tarIn = new TarArchiveInputStream(gzi)) {
            TarArchiveEntry entry;
            while ((entry = tarIn.getNextTarEntry()) != null) {
                if (entry.isFile() && entry.getName().endsWith(".txt")) {
                    File txt = new File(destDir, entry.getName());
                    try (OutputStream out = new FileOutputStream(txt)) {
                        IOUtils.copy(tarIn, out);
                    }
                    return txt;
                }
            }
        }
        throw new IOException("txt not found in " + tarGz.getName());
    }
    @SneakyThrows
    public static List<VoucherTransmitBO> parseTxtToList(File txt)  {

        List<VoucherTransmitBO> list;
        list = new ArrayList<VoucherTransmitBO>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(txt), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                VoucherTransmitBO v = new VoucherTransmitBO(); // 根据实际字段顺序
                list.add(v);
            }
        }
        return list;
    }

    public static void updateVoucherStatusToIssued(String voucherCode) {
        // 推荐使用JDBC、MyBatis等持久层实现
        // 伪代码如下
        String sql = "UPDATE pay_voucher SET voucher_stat='已下发' WHERE voucher_code=?";
        // 执行sql...
    }
}
