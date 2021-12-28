package com.st.api.practice.regx;

import com.google.common.collect.ImmutableMap;
import com.st.utils.log.LogUtils;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class HandleSpecialCharactersTest {

  @Test
  void handleSpecialCharacters() throws Exception {
    String targetStr =
        "select id, tenantid, pubts, effectivedate , countryzone, businessid \nfrom org.func.ITOrg  \nwhere id>param$(p1)and tenantid<param$(p3) and tenantid between param$(p4) and param$(p5) and businessid=param$(p5) \ngroup by tenantid \nlimit 100 \n";

    String resultStr =
        "select id, tenantid, pubts, effectivedate , countryzone, businessid from org.func.ITOrg where id>param$(p1)and tenantid<param$(p3) and tenantid between param$(p4) and param$(p5) and businessid=param$(p5) group by tenantid limit 100";

    assertThat(HandleSpecialCharacters.handleSpecialCharacters(targetStr)).isEqualTo(resultStr);

    ImmutableMap<String, String> status = ImmutableMap.of("status", "200","k2","v2");

    LogUtils.foal(status, "");

    Map map = new HashMap<>();
    map.put("a", "v");
    map = null;

    Map map1 = Optional.ofNullable(map).orElseThrow(() -> new Exception("must be null"));
    System.out.println(map1);
  }
}
