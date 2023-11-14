package com.st.modules.google.gson;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.base.Preconditions;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.st.modules.log.LogEnum;
import com.st.modules.log.LogUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: st
 * @date: 2022/10/31 16:41
 * @version: 1.0
 * @description:
 */
@Slf4j
public class GsonUtils {


	private static Gson gson = null;

	static {
		if (null == gson) {
			gson = new GsonBuilder()
					// 对value为null的属性也进行序列化
					//.serializeNulls()
					// 时间格式化
					.setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		}
	}

	/**
	 * 获取GsonBuilder实例
	 *
	 * @return
	 */
	public static GsonBuilder builder() {
		return new GsonBuilder();
	}

	public static Gson getGson() {
		return gson;
	}

	public static JsonObject buildGJS() {
		JsonObject js;
		js = new JsonObject();
		return js;
	}


	public static JsonArray buildGJSA() {
		JsonArray jsa;
		jsa = new JsonArray();
		return jsa;
	}

	/**
	 * 将对象转为json字符串
	 *
	 * @param object
	 * @return
	 */
	public static String toJson(Object object) {
		String json = null;
		if (gson != null) {
			json = gson.toJson(object);
		}
		return json;
	}

	/**
	 * 将json字符串转为指定类型的实例
	 *
	 * @param json
	 * @param cls
	 * @param <T>
	 * @return
	 */
	public static <T> T parse(String json, Class<T> cls) {
		T t = null;
		if (gson != null) {
			t = gson.fromJson(json, cls);
		}
		return t;
	}

	/**
	 * 将json转为Map
	 *
	 * @param json
	 * @param <T>
	 * @return
	 */
	public static <T> Map<String, T> toMap(String json) {
		Map<String, T> map = null;
		if (gson != null) {
			map = gson.fromJson(json, new TypeToken<Map<String, T>>() {
			}.getType());
		}
		return map;
	}

	/**
	 * 转成list(推荐)
	 * 解决泛型在编译期间类型被擦除导致报错问题
	 */
	public static <T> List<T> jsonToList(String json, Class<T> cls) {
		Gson gson = new Gson();
		List<T> list = new ArrayList<>();
		JsonArray array = new JsonParser().parse(json).getAsJsonArray();
		for (final JsonElement elem : array) {
			list.add(gson.fromJson(elem, cls));
		}
		return list;
	}

	/**
	 * 将json转为Map List
	 *
	 * @param json
	 * @param <T>
	 * @return
	 */
	public static <T> List<Map<String, T>> toMapList(String json) {
		List<Map<String, T>> list = null;
		if (gson != null) {
			list = gson.fromJson(json, new TypeToken<List<Map<String, T>>>() {
			}.getType());
		}
		return list;
	}


	/**
	 * <li> 方式1, 没有泛型时, 推荐使用.</li>
	 * <li>两个java Bean 之间转换: S object -> T r</li>
	 * <ul> 不支持泛型: T中不可以有泛型
	 * 	  <li> Person person -> User user             ✓</li>
	 * 	  <li> Person person -> User<UserInfo> user   ❌</li>
	 *
	 * </ui>
	 *
	 * <li>基本转换 </li>
	 * <li> o2o: o, Object; 2,to; o, Object</li>
	 * <li>S: source; T: target</li>
	 * <pre>
	 *    {
	 *        // error
	 *        //Person person -> User<UserInfo> user
	 *        GsonUtils.o2o(new Person(), User<UserInfo>.class);
	 *
	 *        // correct
	 *        //Person person -> User user
	 *        GsonUtils.o2o(new Person(), User.class);
	 *    }
	 * </pre>
	 */
	public static <S, T> T o2o(S sourceObject, Class<T> targetClass) {

		Preconditions.checkArgument(ObjectUtil.isNotEmpty(sourceObject), "gson 不能为空");

		String json;
		if (sourceObject instanceof String) {
			boolean b = LogUtils.strIsorNotJSON2(null, (String) sourceObject);
			String format = StrUtil.format("入参[{}] is not validate", sourceObject);
			Preconditions.checkArgument(b, format);

			json = (String) sourceObject;
		} else {
			json = gson.toJson(sourceObject);
		}

		T t = null;
		if (gson != null) {
			t = gson.fromJson(json, targetClass);
		}
		return t;
	}

	/**
	 * <li> 方式2, 只能处理有泛型的情况, 有泛型时可以使用. 但不推荐 </li>
	 * <li>两个java Bean 之间转换: S object -> T r</li>
	 * <ul> 支持泛型: T中可以有泛型
	 * <li> Person person -> User<UserInfo> user </li>
	 * </ul>
	 * <li> o2o: o, Object; 2,to; o, Object</li>
	 * <li>S: source; T: target</li>
	 * <li>Class<T> targetClass  raw type 原生类</li>
	 * <li>G, generic type, 泛型</li>
	 * <pre>
	 *    {
	 *        // 泛型
	 *        //Person person -> User<UserInfo> user
	 *        GsonUtils.o2o2(new Person, User.class, UserInfo.class);
	 *    }
	 * </pre>
	 */
	public static <S, T, G> T o2o2(S sourceObject, Class<T> targetClass, Class<G> genericClass) {

		Preconditions.checkArgument(ObjectUtil.isNotEmpty(sourceObject), "gson 不能为空");

		String json;
		if (sourceObject instanceof String) {
			boolean b = LogUtils.strIsorNotJSON2(null, (String) sourceObject);
			String format = StrUtil.format("入参[{}] is not validate", sourceObject);
			Preconditions.checkArgument(b, format);

			json = (String) sourceObject;
		} else {
			json = gson.toJson(sourceObject);
		}


		Type type = new ParameterizedTypeImpl2<>(targetClass, genericClass);

		T t = null;
		if (gson != null) {

			t = gson.fromJson(json, type);
		}

		return t;


	}


	/**
	 * <li> 方式3, 同时兼容:无泛型+有泛型. 推荐 </li>
	 * <li>两个java Bean 之间转换: S object -> T r</li>
	 * <ul> 支持泛型: T中可以有泛型
	 * <li> Person person -> User<UserInfo> user </li>
	 * </ul>
	 * <li> o2o: o, Object; 2,to; o, Object</li>
	 * <li>S: source; T: target</li>
	 * <li>Class<T> targetClass  raw type 原生类</li>
	 * <li>G, generic type, 泛型</li>
	 * <li>Type type 指定泛型. see gson 官网</li>
	 * <pre>
	 *    {
	 *        // 有泛型时
	 *        //Person person -> User<UserInfo> user
	 *        Type type = new TypeToken<User<UesrInfo>>(){}.getType().
	 *        GsonUtils.o2o2(new Person, User.class,type)
	 *
	 *        // 无泛型时
	 *        //Person person -> User user
	 *        GsonUtils.o2o2(new Person, User.class, User.class)
	 *    }
	 * </pre>
	 */
	public static <S, T> T o2o_generic(S sourceObject, Class<T> c, Type type) {

		Preconditions.checkArgument(ObjectUtil.isNotEmpty(sourceObject), "gson 不能为空");

		String json;
		if (sourceObject instanceof String) {
			boolean b = LogUtils.strIsorNotJSON2(null, (String) sourceObject);
			String format = StrUtil.format("入参[{}] is not validate", sourceObject);
			Preconditions.checkArgument(b, format);

			json = (String) sourceObject;
		} else {
			json = gson.toJson(sourceObject);
		}


		T t = null;
		if (gson != null) {
			t = gson.fromJson(json, type);
		}
		return t;
	}

	/**
	 * Object -> Gson: JsonOjbect
	 *
	 * @param sourceObject
	 * @param <S>
	 * @return
	 */
	public static <S> JsonObject o2j(S sourceObject) {

		JsonObject jsonObject;

		Preconditions.checkArgument(ObjectUtil.isNotEmpty(sourceObject), "gson 不能为空");

		String json;
		if (sourceObject instanceof String) {
			boolean b = LogUtils.strIsorNotJSON2(null, (String) sourceObject);
			String format = StrUtil.format("入参[{}] is not validate", sourceObject);
			Preconditions.checkArgument(b, format);

			json = (String) sourceObject;
		} else {
			json = gson.toJson(sourceObject);
		}

		if (ObjectUtil.isEmpty(gson)) {
			gson = new GsonBuilder()
					// 对value为null的属性也进行序列化
					//.serializeNulls()
					// 时间格式化
					.setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		}
		jsonObject = gson.fromJson(json, JsonObject.class);


		return jsonObject;
	}

	/**
	 * <li> 将 String jsaStr -> List<T></li>
	 * <li>支持泛型</li>
	 *
	 * @param json
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	public static <T> List<T> jsaStr2List(String json, Class<T> clazz) {
		Type type = new ParameterizedTypeImpl<>(clazz);

		List<T> list = null;
		if (gson != null) {

			list = gson.fromJson(json, type);
		}
		return list;
	}

	/**
	 * <li>convert: List<T> ->  List<T> T></li>
	 * <li>支持泛型</li>
	 * <pre>
	 *     {@code
	 *     // usage
	 *     Type type = new TypeToke<List<User<UserInfo>>>(){}.getType();
	 *      List<User> list =  GsonUtils.l2l(list, User.class, type);
	 *     }
	 * </pre>
	 *
	 * @param clazz
	 * @param <T>
	 * @return rlist
	 */
	public static <T, R> List<R> l2l(List<T> list, Class<R> clazz, Type type) {

		String jsaStr = gson.toJson(list);
		List<R> rList = null;

		if (gson != null) {
			rList = gson.fromJson(jsaStr, type);
		}
		return rList;
	}

	/**
	 * <li>convert: List<T> ->  List<T> T></li>
	 * <li>支持泛型</li>
	 *
	 * @param clazz
	 * @param <T>
	 * @return rlist
	 */
	public static <T, R> List<R> list2list(List<T> list, Class<R> clazz) {

		String jsaStr = gson.toJson(list);
		List<R> rList = null;

		Type type = new ParameterizedTypeImpl<R>(clazz);
		if (gson != null) {
			rList = gson.fromJson(jsaStr, type);
		}
		return rList;
	}

	/**
	 * <li>用法, see{@link ParameterizedTypeImpl2}</li>
	 *
	 * @param <T>
	 */
	public static class ParameterizedTypeImpl<T> implements ParameterizedType {
		Class<T> clazz;

		public ParameterizedTypeImpl(Class<T> clz) {
			clazz = clz;
		}

		@Override
		public Type[] getActualTypeArguments() {
			return new Type[]{clazz};
		}

		@Override
		public Type getRawType() {
			return List.class;
		}

		@Override
		public Type getOwnerType() {
			return null;
		}
	}

	public static class ParameterizedTypeImpl2<T, G> implements ParameterizedType {
		/**
		 * 原生类
		 * <ui>如 User<Student> 中</Student>
		 * <li>User: raw type, 原生类 </li>
		 * <li>Student: genericType, 原生类的泛型类 </li>
		 * </ui>
		 */
		Class<T> tclass;
		/**
		 * 原生类的泛型类
		 */
		Class<G> gclass;

		public ParameterizedTypeImpl2(Class<T> tclass, Class<G> gclass) {
			this.tclass = tclass;
			this.gclass = gclass;
		}

		/**
		 * <li>原生类的泛型数组</li>
		 * <li> 指定泛型: 构建数组时, 只写泛型类, 无需原生类</li>
		 * <li>泛型数组中的元素可以有多个. 比如, 泛型类中, 有多个泛型</li>
		 * <li>see {@link ParameterizedTypeImpl}</li>
		 *
		 * @return
		 */
		@Override
		public Type[] getActualTypeArguments() {
			return new Type[]{gclass};
		}

		/**
		 * <li>指定原生类. rawtype</li>
		 * <li>要返回的原生类</li>
		 * <li>see {@link ParameterizedTypeImpl}</li>
		 *
		 * @return
		 */
		@Override
		public Type getRawType() {
			return tclass;
		}

		/**
		 * <li> 所拥有的类型</li>
		 *
		 * @return
		 */
		@Override
		public Type getOwnerType() {
			return null;
		}
	}


	/**
	 * 获取jsonObject中key对应的字符串
	 *
	 * @param jsonObject
	 * @param key
	 * @return
	 */
	public static String getString(JsonObject jsonObject, String key) {
		if (jsonObject == null) {
			return null;
		}
		String resultStr = null;
		if (jsonObject.has(key)) {
			JsonElement jsonElement = jsonObject.get(key);
			if (!jsonElement.isJsonNull() && jsonElement != null) {
				resultStr = jsonElement.getAsString();
			}
		}
		return resultStr;
	}


	public static Integer getInteger(JsonObject jsonObject, String key) {
		if (jsonObject == null) {
			return null;
		}

		String format = StrUtil.format("input jsonObject [{}] is invalid json", jsonObject);
		Preconditions.checkArgument(ObjectUtil.isNotEmpty(jsonObject), format);


		Integer resultStr = null;
		if (jsonObject.has(key)) {
			JsonElement jsonElement = jsonObject.get(key);
			if (!jsonElement.isJsonNull() && jsonElement != null) {
				resultStr = jsonElement.getAsInt();
			}
		}
		return resultStr;
	}

	/**
	 * 获取jsonObject中key对应的JsonObject
	 *
	 * @param jsonObject
	 * @param key
	 * @return
	 */
	public static JsonObject getJsonObject(JsonObject jsonObject, String key) {
		if (jsonObject == null) {
			return null;
		}
		JsonObject resultJsonObj = null;
		if (jsonObject.has(key)) {
			JsonElement jsonElement = jsonObject.get(key);
			if (!jsonElement.isJsonNull() && jsonElement != null) {
				resultJsonObj = jsonObject.get(key).getAsJsonObject();
			}
		}
		return resultJsonObj;
	}

	/**
	 * 获取jsonObject中key对应的JsonArray
	 *
	 * @param jsonObject
	 * @param key
	 * @return
	 */
	public static JsonArray getJsonArray(JsonObject jsonObject, String key) {
		if (jsonObject == null) {
			return null;
		}
		JsonArray resultJsonArray = null;
		if (jsonObject.has(key)) {
			if (!jsonObject.get(key).isJsonNull() && jsonObject.get(key) != null) {
				resultJsonArray = jsonObject.get(key).getAsJsonArray();
			}
		}
		return resultJsonArray;
	}

	/**
	 * list转换为JsonArray
	 *
	 * @param list
	 * @return
	 */
	public static JsonArray getJsonArrayByList(List<String> list) {
		JsonArray JsonArr = new JsonArray();
		JsonParser jParser = new JsonParser();
		if (list != null) {
			if (list.size() > 0) {
				if (list.size() >= 1 || list != null) {
					for (String str : list) {
						JsonArr.add(jParser.parse(str));
					}
				}
			}
		}
		return JsonArr;
	}


	public static <T> JsonArray list2jsa(List<T> list) {

		JsonArray jsonArray;
		String s = toJson(list);
		jsonArray = parse(s, JsonArray.class);

		return jsonArray;
	}


	/**
	 * JsonArray转list
	 *
	 * @param jsonArray
	 * @return
	 */
	public static List<String> getListByJsonArray(JsonArray jsonArray) {
		if (jsonArray != null) {
			List<String> resultList = new ArrayList<>();
			for (JsonElement jsonElement : jsonArray) {
				resultList.add(jsonElement.toString());
			}
			return resultList;
		}
		return null;
	}

	/**
	 * JsonArray 转 list
	 *
	 * @param jsonArray
	 * @return list
	 */
	public static List<JsonObject> getListObjByJsonArray(JsonArray jsonArray) {
		if (jsonArray != null) {
			List<JsonObject> resultList = new ArrayList<>();
			for (JsonElement jsonElement : jsonArray) {
				resultList.add(jsonElement.getAsJsonObject());
			}
			return resultList;
		}
		return null;
	}


	/**
	 * 将两个JsonArray 合并
	 */
	public static JsonArray getCombineJsonArray(JsonArray jsonArrayOne, JsonArray jsonArrayOther) {
		if (jsonArrayOne != null && jsonArrayOther != null && jsonArrayOne.size() > 0 && jsonArrayOther.size() > 0) {
			for (JsonElement jsonElement : jsonArrayOther) {
				if (!jsonArrayOne.contains(jsonElement)) {
					jsonArrayOne.add(jsonElement.getAsJsonObject());
				}
			}
			return jsonArrayOne;
		}
		return new JsonArray();
	}


	public static <T, R> List<R> listObject2JsonArray(List<T> list) {

		String title = LogEnum.L_gson_convert_listObjT2ListR.getTitle();
		String invalidation = LogEnum.L_gson_convert_listObjT2ListR.getInvalidation();


		//StrUtil.format("{}, inputs:[{}]; ")
		Preconditions.checkArgument(ObjectUtil.isNotEmpty(list));

		return null;

	}


	/**
	 * <li>Object t -> JsonObject js -> String s -> String s_pretty</li>
	 * <li>对象转成json格式的字符串, 并美化</li>
	 *
	 * @param t 日志具体信息
	 */
	public static <T> String toJsonPretty(T t) {

		String des1 = "content";
		String strPretty;

		JsonObject jsonObject = GsonUtils.o2o(t, JsonObject.class);

		// ========================
		// validate
		// ========================
		String format = StrUtil.format("input :: jsonObject[{}] can't be null", jsonObject);
		Preconditions.checkArgument(ObjectUtil.isNotEmpty(jsonObject), format);


		// ========================
		// build target
		// ========================
		String doubleLine = "===============================";
		String line = System.lineSeparator();
		String brace = "{}";
		String des2 = line + des1 + line + doubleLine + line + brace + line + doubleLine;


		// ========================
		// JsonObject jsonObject -> String str
		// and pretty str
		// ========================
		GsonBuilder builder = GsonUtils.builder();
		builder.setPrettyPrinting();
		//builder.serializeNulls();
		Gson gson = builder.create();
		String prettyJsonStr = gson.toJson(jsonObject);

		// ========================
		// return
		// ========================
		strPretty = StrUtil.format(des2, prettyJsonStr);

		return strPretty;

	}

/*
	public static void main(String[] args) {

	*/
/*	String content = "{\n" +
				"   \"code\": 10000,\n" +
				"   \"success\": true,\n" +
				"   \"msg\": \"token初始化成功\",\n" +
				"   \"data\": {\n" +
				"     \"expire\": 80215,\n" +
				"     \"token\": \"eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJiM\",\n" +
				"     \"type\": \"free\"\n" +
				"   }\n" +
				" }\n";*//*



	 */
/*	// ===================================
		// test o2o
		// ===================================
		Result result = GsonUtils.o2o(content, Result.class);
		log.info(GsonUtils.toJson(result));

		Object data = result.getData();
		log.info(GsonUtils.toJson(data));
		log.info(data.getClass().getName());

		//会抛出异常
		TokenInfo tokenInfo = (TokenInfo) data;
		log.info(GsonUtils.toJson(tokenInfo));*//*



	 */
/*//*
/ ===================================
		// test o2o2
		// ===================================
		log.info("========================================");
		log.info("两个bean之间转换: S s -> T g");
		log.info("o2o2, 只支持泛型");
		log.info("========================================");
		Result result2 = GsonUtils.o2o2(content, Result.class, TokenInfo.class);
		log.info(GsonUtils.toJson(result2));

		Object data2 = result2.getData();
		log.info(GsonUtils.toJson(data2));
		log.info(data2.getClass().getName());

		TokenInfo tokenInfo2 = (TokenInfo) data2;
		log.info(GsonUtils.toJson(tokenInfo2));
		log.info("========================================\n\t");

		// ===================================
		// test o2o3-带有泛型
		// ===================================
		log.info("========================================");
		log.info("两个bean之间转换: S s -> T g");
		log.info("o2o_generic: 支持泛型");
		log.info("GsonUtils.o2o_generic(content, Result.class, type)");
		log.info("========================================");
		Type type = new TypeToken<Result<TokenInfo>>() { }.getType();
		Result resultr3 = GsonUtils.o2o_generic(content, Result.class, type);
		log.info(GsonUtils.toJson(resultr3));

		Object data3 = resultr3.getData();
		log.info(GsonUtils.toJson(data3));
		log.info(data3.getClass().getName());

		//会抛出异常
		TokenInfo tokenInfo3 = (TokenInfo) data3;
		log.info(GsonUtils.toJson(tokenInfo3));
		log.info("========================================\n\t");

		// ===================================
		// test o2o3-没有泛型
		// ===================================
		log.info("========================================");
		log.info("两个bean之间转换: S s -> T g");
		log.info("o2o_generic: 支持非泛型");
		log.info("========================================");
		Result2 resultr4 = GsonUtils.o2o_generic(content, Result2.class, Result2.class);
		log.info(GsonUtils.toJson(resultr4));

*//*



	}
*/

}

@Data
@Slf4j
class Result<T> {
	/**
	 * 状态码
	 */
	private Integer code;
	/**
	 * 状态
	 */
	private Boolean success;
	/**
	 * 返回消息
	 */
	private String msg;
	/**
	 * 数据
	 */
	private T data;
}

@Data
@Slf4j
class Result2 {
	/**
	 * 状态码
	 */
	private Integer code;
	/**
	 * 状态
	 */
	private Boolean success;
	/**
	 * 返回消息
	 */
	private String msg;
	/**
	 * 数据
	 */
	//private T data;
}

@Data
class TokenInfo {
	/**
	 * 过期时间
	 */
	private Long expire;
	/**
	 * Token
	 */
	public String token;
	/**
	 * 类型
	 */
	private String type;
}





