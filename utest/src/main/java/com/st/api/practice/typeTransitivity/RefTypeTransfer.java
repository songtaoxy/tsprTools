package com.st.api.practice.typeTransitivity;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: st
 * @date: 2021/5/31 16:08
 * @version: 1.0
 * @description:
 */

@Slf4j
public class RefTypeTransfer {

	public static void main(String[] args) {
		Map<Object, Object> mapTop= new HashMap<>();
		Map<Object, Object> mapSub= new HashMap<>();
		mapSub.put("sub1", "value1");

		mapTop.put("mapSub1", mapSub);
		log.info(String.valueOf(mapTop));


		Map subRep = (Map) mapTop.get("mapSub1");
		subRep.put("sub1", "value2");
		log.info(String.valueOf(mapTop));


	}
}
