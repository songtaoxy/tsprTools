package com.st.api.practice.toString;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import javafx.beans.binding.ObjectBinding;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.ULongLongSeqHelper;

import java.sql.SQLOutput;

/**
 * @author: st
 * @date: 2021/6/18 10:48
 * @version: 1.0
 * @description:
 */
@Slf4j
public class ObjString {

	public static void main(String[] args) {
		Object object = null;
		int param = 100;
		log.info("\n接收信息概述,接收的信息:  \n" +
				"+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n" +
				"service  : yonyou-metadata-parent\n" +
				"Class    : ReportMetadataConcreteController.java\n" +
				"Method   : getOwnedAttributeByYonql\n" +
				"URL      : http://u8cms-daily.yyuap.com/mdd-report/entity/fullname\n" +
				"param    : {}\n" +
				"+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++",param );

		//Preconditions.checkNotNull(object,"yonql不能为空");
		//System.out.porintln(object.toString());

		log.info("\n响应结果概述, 响应给前端:  \n" +
				"+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n" +
				"service  : yonyou-metadata-parent\n" +
				"Class    : ReportMetadataConcreteController.java\n" +
				"Method   : getOwnedAttributeByYonql\n" +
				"Result   : {}\n" +
				"+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++",param);

		log.info("this is \n good");
	}
}
