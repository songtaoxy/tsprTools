package com.st.modules.test.boc.voucher;

import com.st.modules.constant.FileConst;
import com.st.modules.file.FileUtils;
import com.st.modules.file.tar.TarUtils;
import com.st.modules.time.TimeUtils;
import com.sun.istack.internal.NotNull;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


@Slf4j
public class VoucherUtils {

    /**
     * <pre>
     *  命名规则:
     *  - AP_GL_+5位一级行代码+OU编码+_+8位年月_001.TXT(tar.gz)
     *  - 返回格式AP_GL_+5位一级行代码+OU编码+_+8位年月_001_RET.TXT (tar.gz)
     * </pre>
     * @return
     */
    public static Map<String,String> buildFilePath(String  orgOuKey){
    String name = "";

    Map<String,String> fileMap = new HashMap<String, String>();

    // dir
    String dir = FileConst.fglsDistributeFile;

    // file elements
    String prefix = "AP_GL_";
    String org_L1 = orgOuKey.substring(0,5);
    String ou_code = orgOuKey.substring(5);
    String date_flag = TimeUtils.time2StrCust("yyyyMMdd");
    String seri = "_001";

    String txtFileName= prefix+org_L1+ou_code+"_"+date_flag+seri+FileConst.filePostFixTxt;
    String tarGzFileName= prefix+org_L1+ou_code+"_"+date_flag+seri+FileConst.filePostFixTarGz;

    // file path
        String txtFilePath = dir+txtFileName;
        String tarGzFilePath = dir + tarGzFileName;


    // build response
        fileMap.put("txtFilePath", txtFilePath);
        fileMap.put("tarGzFilePath", tarGzFilePath);

        return fileMap;
}

    /**
     * 并行流式处理所有类型，内部循环用for，尽可能减少流式操作
     */
    public static List<String> formatVouchersParallel(
            List<String> typeOrder,
            Map<String, List<VoucherTransmitBO>> typeGroup,
            Map<String, Integer> typeStartGlobalIndex
    ) {
        // 用线程安全集合接收结果
        List<String> result = Collections.synchronizedList(new ArrayList<>());

        typeOrder.parallelStream().forEach(type -> {
            List<VoucherTransmitBO> vouchers = typeGroup.get(type);
            int startGlobal = typeStartGlobalIndex.get(type);
            for (int i = 0; i < vouchers.size(); i++) {
                int localIdx = i + 1;
                int globalIdx = startGlobal + i;
                VoucherTransmitBO voucherTransmitBO = vouchers.get(i);
                String line = formatVoucher(globalIdx, localIdx, type, voucherTransmitBO);
                result.add(line);
            }
        });

        // 并行流插入顺序无法保证，需排序
        result.sort(Comparator.comparingInt(line -> Integer.parseInt(line.split("\\"+FileConst.verticalLine)[0].trim())));
        return result;
    }

    public static String formatVoucher(int globalIdx, int localIdx, String type, VoucherTransmitBO bo) {
        String v = FileConst.verticalLine;
        return  globalIdx + v +
                bo.getUserJeSourceName()+ v +
                bo.getUserJeCategoryName()+ v +
                bo.getJeBatchName()+ v +
                bo.getJeHeaderName()+ v +
                bo.getTransactionNum()+ v +
                bo.getAccountingDate()+ v +
                bo.getCurrencyCode()+v+
                bo.getCurrencyRate()+v+
                bo.getImageNumber()+v+
                bo.getImageAddress()+v+
                localIdx+v+
                bo.getSegment1()+v+
                bo.getSegment2()+v+
                bo.getSegment3()+v+
                bo.getSegment4()+v+
                bo.getSegment5()+v+
                bo.getSegment6()+v+
                bo.getSegment7()+v+
                bo.getSegment8()+v+
                bo.getSegment9()+v+
                bo.getEnteredDr()+v+
                bo.getEnteredCr()+v+
                bo.getAccountedDr()+v+
                bo.getAccountedCr()+v+
                bo.getLineDesc()+v+
                bo.getReference21()+v+
                bo.getReference22()+v+
                bo.getReference23()+v+
                bo.getReference24()+v+
                bo.getReference25()+v+
                bo.getReference26()+v+
                bo.getReference27()+v+
                bo.getReference28()+v+
                bo.getReference29()+v+
                bo.getReference30()+v+
                bo.getGlSlLinkId()+v+
                bo.getGlSlLinkTable();

    }


    @NotNull
    public static File compressWithTargz(File tempFile,String orgOuKey) throws IOException {
        Map<String, String> filePathMap = buildFilePath(orgOuKey);
        String tempFileTarGzPath= filePathMap.get("tarGzFilePath");

        File tempFileTarGz = FileUtils.createFileOverwrite(tempFileTarGzPath);
        TarUtils.compressToTarGz(tempFile,new File(tempFileTarGzPath));
        return tempFileTarGz;
    }

    @NotNull
    public static File wirteTxtFile(String tempFilePath, List<String> formattedAll) throws IOException {
            File tempFile = FileUtils.createFileOverwrite(tempFilePath);
        System.out.println(tempFile.getAbsolutePath());
        String fileContent = String.join("\n", formattedAll);
        Files.write(Paths.get(tempFile.getAbsolutePath()), fileContent.getBytes(StandardCharsets.UTF_8));
        log.info("write done");
        return tempFile;
    }


    // FTP 上传（实现略）
    public static void ftpUpload(String filePath, String remotePath) {
        // ...上传逻辑
    }


}
