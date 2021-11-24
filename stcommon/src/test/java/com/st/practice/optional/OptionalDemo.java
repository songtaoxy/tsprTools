package com.st.practice.optional;

import com.alibaba.ttl.threadpool.agent.internal.javassist.NotFoundException;
import com.oracle.tools.packager.Log;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Optional;

/**
 * @author: st
 * @date: 2021/11/24 17:24
 * @version: 1.0
 * @description:
 */
@Slf4j
public class OptionalDemo {

  public static void main(String[] args) throws Exception {
    HashMap<String, Object> map = new HashMap<>();
    // map.put("k1", "v1");

    // Object y = Optional.ofNullable(map.get("k1")).orElseThrow(() -> new
    // NotFoundException("记录不存在"));
    String z = null;
    z = (String) Optional.ofNullable(map.get("k1")).orElseThrow(() -> new Exception("记录不存在"));

    // log.info((String) y);
    log.info(z);
  }
}
