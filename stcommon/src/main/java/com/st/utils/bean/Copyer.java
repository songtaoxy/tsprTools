package com.st.utils.bean;

import cn.hutool.core.bean.BeanUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author: st
 * @date: 2023/1/14 00:19
 * @version: 1.0
 * @description:
 */
@Slf4j
public class Copyer {

	//private static MapStructCopier mapStructCopier = Mappers.getMapper(MapStructCopier.class);

	/*public 	<T, S> T  copy(S s, Class<T> c) {
		return mapStructCopier.copy(s,c);
	}*/

/*	@Mapper
	public interface MapStructCopier {
		<T, S> T copy(S s,Class<T> c);
	}*/

	@SneakyThrows
	public static void main(String[] args) {
		Person tom = Person.builder().name("tom").age(18).build();
		//Person copy = new Copyer().copy(tom, Person.class);

		Person copy = new Copyer().copy(tom, Person.class);

		log.info(tom + "..." + tom.toString());
		log.info(copy + "..." + copy.toString());


	}

	public <K, T> T copy(K source, Class<T> target) throws Exception {
		return BeanUtil.toBean(source, target);
	}
}

@AllArgsConstructor
@Data
@Builder
class Person {
	private String name;
	private Integer age;

}
