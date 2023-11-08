package com.st.utils.xml;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: st
 * @date: 2023/11/8 16:54
 * @version: 1.0
 * @description:
 */
public class ReadXML {

	public void xml() {
		SAXReader reader = new SAXReader();
		Document document = null;
		try {
			document = reader.read(new File("/Users/songtao/downloads/data.xml"));
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		//获取整个文档
		Element rootElement = document.getRootElement();
		System.out.println("整个文档:"+rootElement.asXML());

		//获取Response节点的Result属性值
		String responseResult = rootElement.attributeValue("Result");
		System.out.println("Response节点的Result属性值:"+responseResult);

		//获取第一个Media元素
		Element mediaElement = rootElement.element("Media");
		System.out.println("第一个Media元素:"+mediaElement.asXML());

		//获取所有的Media元素
		List allMeidaElements = rootElement.elements("Media");

		//获取第一个Media元素的Name属性值
		String mediaName = mediaElement.attributeValue("Name");
		System.out.println("第一个Media元素的Name属性值:"+mediaName);

		//遍历所有的Media元素的Name属性值
		for (int i = 0; i < allMeidaElements.size(); i++) {
			Element element = (Element) allMeidaElements.get(i);
			String name = element.attributeValue("Name");
		}

		//获取第一个Meida元素的文本值
		String value = mediaElement.getText();
		System.out.println("第一个Meida元素的文本值:"+value);
	}

	public static void main(String[] args) {
		ReadXML textxml = new ReadXML();
		textxml.xml();

		}

	}
