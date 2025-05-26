package com.st.modules.host;

import java.net.*;
import java.util.*;

public class HostInfoUtils {

    // 获取主机名
    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

    // 获取jvm所在主机所在所有内网IPv4地址（不包含127.0.0.1等回环地址）
    public static List<String> getAllIPv4() {
        List<String> ips = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
            while (nics.hasMoreElements()) {
                NetworkInterface nic = nics.nextElement();
                if (!nic.isUp() || nic.isLoopback() || nic.isVirtual()) continue;
                Enumeration<InetAddress> addrs = nic.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    InetAddress ip = addrs.nextElement();
                    if (ip instanceof Inet4Address && !ip.isLoopbackAddress()) {
                        ips.add(ip.getHostAddress());
                    }
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return ips;
    }

    // 获取第一个有效IPv4地址
    public static String getFirstIPv4() {
        List<String> ips = getAllIPv4();
        return ips.isEmpty() ? "UNKNOWN" : ips.get(0);
    }

    // 获取所有MAC地址
    public static List<String> getAllMAC() {
        List<String> macs = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
            while (nics.hasMoreElements()) {
                NetworkInterface nic = nics.nextElement();
                if (!nic.isUp() || nic.isLoopback() || nic.isVirtual()) continue;
                byte[] mac = nic.getHardwareAddress();
                if (mac != null && mac.length == 6) {
                    StringBuilder sb = new StringBuilder();
                    for (byte b : mac) sb.append(String.format("%02X-", b));
                    sb.setLength(sb.length() - 1); // 去除末尾多余 -
                    macs.add(sb.toString());
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return macs;
    }

    // 获取第一个有效 MAC 地址
    public static String getFirstMAC() {
        List<String> macs = getAllMAC();
        return macs.isEmpty() ? "UNKNOWN" : macs.get(0);
    }

    // 获取 JVM 节点唯一标识（组合主机名+IP+MAC）

    /**
     * <pre>
     * - 一, 节点唯一标识:常用的是主机名 + 单一 IP + 单一 MAC的组合
     * - 二, 对于ip及mac, 往往取第一个,原因:
     * - 1, 大部分服务器只有一个物理网卡，且“第一个”通常是主要/默认的通信网卡
     * - 2, 云服务器、物理机、主流容器化部署都保证主用网卡排在第一
     * - 3, MAC 地址物理唯一性强，选取第一个一般足够区分节点
     * - 4, 避免虚拟/冗余网卡混入: 代码已过滤虚拟、回环网卡，选第一个有效的物理网卡
     * - 5, 局限性和风险
     * - 5.1, 部分场景多网卡（如双网卡容灾/多业务网段）时，第一个网卡不一定是“主通信网卡”
     * - 5.2, 某些云环境下 MAC 可能动态分配，不稳定
     * - 5.3, 容器（K8s、Docker）环境下，重启后IP/MAC 可能变化，主机名也可能不是唯一
     * - 6, 实际生产中的补充策略
     * - 6.1, 配置项/环境变量兜底, 如有节点号/物理ID配置优先用配置
     * - 6.2, 业务定制化选择网卡: 可让用户指定（如“eth0”）或自定义获取逻辑
     * - 6.3, 节点唯一性需满足业务实际区分即可: 如仅日志定位/注册中心，够用即可
     * - 7, 结论
     * - 7.1, 用第一个IP/MAC是工程实践中对“唯一标识”与“易用性”折中的主流方案，适合绝大多数普通环境
     * - 7.2, 如对唯一性要求极高，应结合业务/部署实际做更强约束
     * </pre>
     * @return
     */
    public static String getNodeUniqueId() {
        String host = getHostName();
        String ip = getFirstIPv4();
        String mac = getFirstMAC();
        return host + "_" + ip + "_" + mac;
    }

    // 示例：主方法输出全部信息
    public static void main(String[] args) {
        System.out.println("主机名: " + getHostName());
        System.out.println("第一个IPv4: " + getFirstIPv4());
        System.out.println("所有IPv4: " + getAllIPv4());
        System.out.println("第一个MAC: " + getFirstMAC());
        System.out.println("所有MAC: " + getAllMAC());
        System.out.println("唯一标识: " + getNodeUniqueId());
    }
}
