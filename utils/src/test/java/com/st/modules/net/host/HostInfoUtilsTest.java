package com.st.modules.net.host;

import com.st.modules.net.host.HostInfoUtils;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HostInfoUtilsTest {

    @Test
    public void testGetHostName() {
        String hostName = HostInfoUtils.getHostName();
        System.out.println("主机名: " + hostName);
        assertNotNull(hostName);
        assertFalse(hostName.isEmpty());
    }

    @Test
    public void testGetAllIPv4() {
        List<String> ipv4s = HostInfoUtils.getAllIPv4();
        System.out.println("所有IPv4: " + ipv4s);
        assertNotNull(ipv4s);
        // 允许无IP的极端情况, 这里只做输出演示
    }

    @Test
    public void testGetFirstIPv4() {
        String firstIp = HostInfoUtils.getFirstIPv4();
        System.out.println("第一个有效IPv4: " + firstIp);
        assertNotNull(firstIp);
        assertFalse(firstIp.isEmpty());
    }

    @Test
    public void testGetAllMAC() {
        List<String> macs = HostInfoUtils.getAllMAC();
        System.out.println("所有MAC: " + macs);
        assertNotNull(macs);
        // 允许无MAC的极端情况, 这里只做输出演示
    }

    @Test
    public void testGetFirstMAC() {
        String firstMac = HostInfoUtils.getFirstMAC();
        System.out.println("第一个有效MAC: " + firstMac);
        assertNotNull(firstMac);
        assertFalse(firstMac.isEmpty());
    }

    @Test
    public void testGetNodeUniqueId() {
        String nodeId = HostInfoUtils.getNodeUniqueId();
        System.out.println("节点唯一标识: " + nodeId);
        assertNotNull(nodeId);
        assertFalse(nodeId.isEmpty());
        // 节点唯一标识应包含主机名、IP、MAC（允许部分异常为UNKNOWN）
        assertTrue(nodeId.split("_").length == 3);
    }
}
