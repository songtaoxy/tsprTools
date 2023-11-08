package com.st.practice.file;

import cn.hutool.json.JSONUtil;
import com.cmbc.FilesInput;
import com.cmbc.enums.BillTypeEnum;
import com.st.utils.json.fastjson.FastJsonUtil;
import com.st.utils.log2.LogBody;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;

public class FileUtils {


    public static FilesInput parseXML(String path, String code)
    {

        // optional log
        String topic ="/智能验票/上传发票/解析xml文件, 或zip中的xml文件中的特定字段: 发票代码, 发票号码";
        LogBody logBody = new LogBody();
        logBody.setTopic(topic);

        // return
        FilesInput filesInput= new FilesInput();

        // billtype
        String elecBillCode = BillTypeEnum.ELEC_BILL.getCode();
        String delecBillCode = BillTypeEnum.DELEC_BILL.getCode();


        SAXReader reader = new SAXReader();
        Document document = null;
        try {
            document = reader.read(new File(path));
        } catch (DocumentException e) {
            e.printStackTrace();
        }


        //获取整个文档
        Element rootElement = document.getRootElement();
        System.out.println("整个文档:"+rootElement.asXML());

        // 电子专/普
        if (elecBillCode.equalsIgnoreCase(code)) {
            //获取第一个Media元素
            Element mediaElement = rootElement.element("Media");
            System.out.println("第一个Media元素:"+mediaElement.asXML());

            String billCode = mediaElement.getText();
            String billNumber = mediaElement.getText();

            filesInput.setBillCode(billCode);
            filesInput.setBillNumber(billNumber);

        } else if (delecBillCode.equalsIgnoreCase(code)) { // 数电专/普

            Element mediaElement = rootElement.element("Media");
            System.out.println("第一个Media元素:"+mediaElement.asXML());

            String billNumber = mediaElement.getText();

            filesInput.setBillNumber(billNumber);
        }

        // optional log
        logBody.setInfos_obj(filesInput);
        System.out.println(FastJsonUtil.format(logBody));


        return filesInput;
    }


}
