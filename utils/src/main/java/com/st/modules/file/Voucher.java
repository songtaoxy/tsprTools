package com.st.modules.file;



    import com.st.modules.time.TimeUtils;

    import java.io.File;
    import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Voucher {

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

            // 2. 按类型分组
            Map<String, List<Map<String, Object>>> typeGroup = new HashMap<>();
            for (Map<String, Object> v : allVouchers) {
                String type = String.valueOf(v.get("type"));
                typeGroup.computeIfAbsent(type, k -> new ArrayList<>()).add(v);
            }

            // 3. 类型顺序
            List<String> typeOrder = new ArrayList<>(typeGroup.keySet());
            Collections.sort(typeOrder); // 可自定义顺序

            // 4. 计算每类全局编号起始值
            Map<String, Integer> typeStartGlobalIndex = new HashMap<>();
            int startIdx = 1;
            for (String type : typeOrder) {
                typeStartGlobalIndex.put(type, startIdx);
                startIdx += typeGroup.get(type).size();
            }

            // 5. 并行流式生成格式化数据
            List<String> formattedAll = formatVouchersParallel(typeOrder, typeGroup, typeStartGlobalIndex);

            System.out.println(String.join("\n", formattedAll));

            // 6. 写入文件

            System.out.println(System.getProperty("java.io.tmpdir"));

            File tempFile = File.createTempFile("voucher_temp", TimeUtils.time2Str() +".txt");
            // File tempFile = Files.createTempFile("myapp_", ".tmp").toFile(); // NIO写法


            String fileContent = String.join("\n", formattedAll);
            String filePath = System.getProperty("user.dir")+"/tmp/voucher_export.txt";
            filePath = tempFile.getAbsolutePath();
            System.out.println(filePath);


            FileUtil.createFile(filePath);
            Files.write(Paths.get(filePath), fileContent.getBytes(StandardCharsets.UTF_8));
            System.out.println("write done");

            boolean flag=false;
            // 7. FTP上传
//            flag = ftpUpload(filePath, "/目标目录/voucher_export.txt");

            // 添加判断(如果ftp上传成功, 则
           if(flag){tempFile.deleteOnExit();}
            System.out.println("clear ...");
        }

        /**
         * 并行流式处理所有类型，内部循环用for，尽可能减少流式操作
         */
        public static List<String> formatVouchersParallel(
                List<String> typeOrder,
                Map<String, List<Map<String, Object>>> typeGroup,
                Map<String, Integer> typeStartGlobalIndex
        ) {
            // 用线程安全集合接收结果
            List<String> result = Collections.synchronizedList(new ArrayList<>());

            typeOrder.parallelStream().forEach(type -> {
                List<Map<String, Object>> vouchers = typeGroup.get(type);
                int startGlobal = typeStartGlobalIndex.get(type);
                for (int i = 0; i < vouchers.size(); i++) {
                    int localIdx = i + 1;
                    int globalIdx = startGlobal + i;
                    Map<String, Object> v = vouchers.get(i);
                    String line = formatVoucher(globalIdx, localIdx, type, v);
                    result.add(line);
                }
            });

            // 并行流插入顺序无法保证，需排序
            result.sort(Comparator.comparingInt(line -> Integer.parseInt(line.split(",")[0])));
            return result;
        }

        public static String formatVoucher(int globalIdx, int localIdx, String type, Map<String, Object> v) {
            return globalIdx + "," + localIdx + "," + type + "," +
                    v.getOrDefault("no", "") + "," +
                    v.getOrDefault("date", "") + "," +
                    v.getOrDefault("amount", "") + "," +
                    v.getOrDefault("remark", "");
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

