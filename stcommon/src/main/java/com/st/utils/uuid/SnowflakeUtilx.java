package com.st.utils.uuid;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.IdUtil;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.annotation.PostConstruct;

/**
 * @author: st
 * @date: 2022/6/10 14:38
 * @version: 1.0
 * @description:
 */
public class SnowflakeUtilx {

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	//private long workerId = 0;//为终端ID
	private long workerId = NetUtil.ipv4ToLong(NetUtil.getLocalhostStr());//为终端ID


	private long dataCenterId = 1;//数据中心ID
	private Snowflake snowflake = IdUtil.createSnowflake(workerId,dataCenterId);
	@PostConstruct
	public void init(){
		workerId = NetUtil.ipv4ToLong(NetUtil.getLocalhostStr());
	}
	public synchronized String snowflakeId(){
		System.out.println(workerId);
		return String.valueOf(snowflake.nextId());
	}
	public synchronized long snowflakeId(long workerId,long dataCenterId){
		Snowflake snowflake = IdUtil.createSnowflake(workerId, dataCenterId);
		return snowflake.nextId();
	}

	public static void main(String[] args) {
		SnowflakeUtilx snowflakeUtil = new SnowflakeUtilx();
		String s = snowflakeUtil.snowflakeId();
		System.out.println(s);
	}
}
