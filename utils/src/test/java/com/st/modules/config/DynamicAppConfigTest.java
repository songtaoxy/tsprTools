package com.st.modules.config;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DynamicAppConfigTest {
    @Test
    public void testGetConfig() {
        String url = DynamicAppConfig.get("db.url");
        Assert.assertNotNull(url);
    }

    @Test
    public void testSwitchEnv() {
        DynamicAppConfig.switchEnv("dev");
        String devVal = DynamicAppConfig.get("db.url");
        DynamicAppConfig.switchEnv("prod");
        String prodVal = DynamicAppConfig.get("db.url");
        Assert.assertNotEquals(devVal, prodVal);
    }

}