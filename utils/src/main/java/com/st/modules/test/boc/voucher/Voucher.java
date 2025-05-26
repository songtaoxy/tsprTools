package com.st.modules.test.boc.voucher;



    import com.st.modules.constant.FileConst;
    import com.st.modules.file.FileUtil;
    import com.st.modules.file.clean.FileCleanupManager;
    import com.st.modules.tar.TarUtils;
    import com.st.modules.time.TimeUtils;
    import lombok.extern.slf4j.Slf4j;
    import org.jetbrains.annotations.NotNull;

    import java.io.File;
    import java.io.IOException;
    import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.*;
    import java.util.stream.Collectors;
@Slf4j
public class Voucher {

        public static final String appPath = System.getProperty("user.dir");

        public static void main(String[] args) throws Exception {
            // 1. 查询凭证数据（Map结构）
//            List<Map<String, Object>> allVouchers = voucherRepository.queryVouchers(params);
            List<Map<String, Object>> allVouchers = Arrays.asList(
                    // 收款类型
                    mapOf("type", "收款", "no", "SK001", "date", "2024-05-25", "amount", "1000", "remark", "A客户回款"),
                    mapOf("type", "收款", "no", "SK002", "date", "2024-05-26", "amount", "1500", "remark", "B客户回款"),
                    // 付款类型
                    mapOf("type", "付款", "no", "FK001", "date", "2024-05-25", "amount", "800", "remark", "供应商付款"),
                    // 报销类型
                    mapOf("type", "报销", "no", "BX001", "date", "2024-05-24", "amount", "300", "remark", "差旅报销"),
                    mapOf("type", "报销", "no", "BX002", "date", "2024-05-26", "amount", "500", "remark", "办公用品")
            );

            // 2, convert: list<map> -> list<BO>
            List<VoucherTransmitBO> voucherTransmitBOS = VoucherTransmitBO.mapListToBOList(allVouchers);

            // 3. 按类型分组
            Map<String, List<VoucherTransmitBO>> typeGroup = voucherTransmitBOS.stream().collect(Collectors.groupingBy(VoucherTransmitBO::getType));

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
            List<String> formattedAll = formatVouchersParallel(typeOrder, typeGroup, typeStartGlobalIndex);
//            System.out.println(String.join("\n", formattedAll));

            // 7. 写入文件(txt)
            String fileName = TimeUtils.time2Str();
            String tempFilePath = FileConst.buildVoucherTxt(fileName);
            File tempFile = wirteTxtFile(tempFilePath, formattedAll);

            // 8, 压缩: 将txt压缩成tar.gz
            File tempFileTarGz = compressWithTargz(fileName, tempFile);

            boolean flag=false;
            // 9. FTP上传tar.gz
//            flag = ftpUpload(filePath, "/目标目录/voucher_export.txt");

            // 10, 延迟清理文件: 如果ftp上传成功, 则清理文件(延迟)
           if(flag){
               FileCleanupManager.register(tempFile);
               FileCleanupManager.register(tempFileTarGz);
           }
        }

    @NotNull
    private static File compressWithTargz(String fileName, File tempFile) throws IOException {
        String tempFileTarGzPath=FileConst.buildVoucherTarGz(fileName +"/x/y");
        File tempFileTarGz = FileUtil.createFileOverwrite(tempFileTarGzPath);
        TarUtils.compressToTarGz(tempFile,new File(tempFileTarGzPath));
        return tempFileTarGz;
    }

    @NotNull
    private static File wirteTxtFile(String tempFilePath, List<String> formattedAll) throws IOException {
        File tempFile = FileUtil.createFileOverwrite(tempFilePath);
        System.out.println(tempFile.getAbsolutePath());
        String fileContent = String.join("\n", formattedAll);
        Files.write(Paths.get(tempFile.getAbsolutePath()), fileContent.getBytes(StandardCharsets.UTF_8));
        log.info("write done");
        return tempFile;
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
            result.sort(Comparator.comparingInt(line -> Integer.parseInt(line.split(",")[0])));
            return result;
        }

        public static String formatVoucher(int globalIdx, int localIdx, String type, VoucherTransmitBO bo) {
            return globalIdx + "," + localIdx + "," + type + "," +
                    bo.getNo() + "," +
                    bo.getDate() + "," +
                    bo.getAmount() + "," +
                    bo.getRemark();
        }

        // FTP 上传（实现略）
        public static void ftpUpload(String filePath, String remotePath) {
            // ...上传逻辑
        }

        // 假设 VoucherRepository、params 已定义
//        static VoucherRepository voucherRepository = new VoucherRepository();
//        static Map<String, Object> params = new HashMap<>();

        // 辅助map构造
        public static Map<String, Object> mapOf(Object... kv) {
            Map<String, Object> map = new HashMap<>();
            for (int i = 0; i < kv.length; i += 2) {
                map.put(String.valueOf(kv[i]), kv[i + 1]);
            }
            return map;
        }


    }

