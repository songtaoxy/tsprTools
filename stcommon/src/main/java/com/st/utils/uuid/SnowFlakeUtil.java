package com.st.utils.uuid;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.IdUtil;
import com.google.errorprone.annotations.Var;

/**
 * @author: st
 * @date: 2022/6/10 14:51
 * @version: 1.0
 * @description:
 */

/**
 *  根据雪花算法, 生成分布式id
 */
public class SnowFlakeUtil {

	/** 机器id */
	private long machineId ;

	/** 数据中心id */
	private long dataCenterId ;

	private static final Long localHostStr = NetUtil.ipv4ToLong(NetUtil.getLocalhostStr());



	public SnowFlakeUtil(long machineId, long dataCenterId) {
		this.machineId = machineId;
		this.dataCenterId = dataCenterId;
	}

	/**
	 * 成员类，SnowFlakeUtil的实例对象的保存域
	 */
	private static class IdGenHolder {
		private static final SnowFlakeUtil instance = new SnowFlakeUtil();
	}

	/**
	 * 外部调用获取SnowFlakeUtil的实例对象，确保不可变
	 */
	public static SnowFlakeUtil get() {
		return IdGenHolder.instance;
	}

	/**
	 * 初始化构造，无参构造有参函数，默认节点都是0
	 */
	public SnowFlakeUtil() {
		//this(0L, 0L);
		this(localHostStr, 0L);
	}

	private Snowflake snowflake = IdUtil.getSnowflake(machineId,dataCenterId);

	public synchronized long id(){
		return snowflake.nextId();
	}


	public synchronized String strId(){
		return String.valueOf(snowflake.nextId());
	}

	public static Long getId() {
		return SnowFlakeUtil.get().id();
	}

	public static String  getIdStr() {
		return SnowFlakeUtil.get().strId();
	}

	/**
	 *
	 * @param args
	 */

	public static void main(String[] args) {
		String id = SnowFlakeUtil.getIdStr();
		Long id1 = SnowFlakeUtil.getId();
		System.out.println(id);
		System.out.println(id1);
	}
}
