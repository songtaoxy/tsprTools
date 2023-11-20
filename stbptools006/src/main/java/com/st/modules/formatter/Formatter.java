package com.st.modules.formatter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.st.modules.alibaba.fastjson.v1_2_76.FastJsonUtil;
import com.st.modules.alibaba.vos.Person;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author: st
 * @date: 2023/11/8 12:40
 * @version: 1.0
 * @description:
 */
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Slf4j
public class Formatter<L, M, O> {

    private String time;
    private String topic;
    private String des;
    private Map<String, M> map;
    private List<L> list;
    private JSONObject jsonObject;
    private JSONArray jsonArray;
    private Object object;
    private O obj;


    public Formatter() {
        this.time = Formatter.getDate();
    }


    public static Formatter init() {

        return new Formatter();
    }

    public static Formatter<Object, String, Object> init2() {

        return new Formatter<Object, String, Object>();
    }

    public static String getDate() {

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-DD HH:mm:SS");
        return simpleDateFormat.format(date);
    }

    public Map<String, M> buildMap() {
        setMap(new HashMap<String, M>());
        return getMap();
    }


    public List<L> buildList() {
        setList(new ArrayList<L>());
        return getList();
    }

    public JSONObject buildJS() {
        setJsonObject(new JSONObject());
        return getJsonObject();
    }

    public JSONArray buildJSA() {
        setJsonArray(new JSONArray());
        return getJsonArray();
    }

    /**
     * <li>先new对象</li>
     * <li>在将对象赋值</li>
     * <li>再将对象放到formatter中</li>
     *
     * @param o
     * @return
     */
    public Object buildObject(Object o) {
//        setObject(new Object());
        setObject(o);
        return getObject();
    }


    /**
     * <li>先根据反射, 返回对象, 此时对象的属性是空</li>
     * <li>对象赋值</li>
     *
     * @param clazz
     * @return
     */
    public O buildObj(Class<O> clazz) {
        O o = null;
        try {
            o = clazz.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        setObj(o);
        return getObj();
    }

    public String format() {
        String ls=System.lineSeparator();
        String format = FastJsonUtil.format(this);
        String topic1 = "Topic: "+this.getTopic();
        String time1 =  "Time : " +this.getTime();
        String sp = "======================";
        String sp2 = "......................";
        String msg = ls+ls+sp+ls+time1+ls+topic1+ls+sp2+format+ls+sp;
        return msg;

    }

    public static void main(String[] args) {
        /*String topic = "/log/test/this is a test case";
        JSONObject jsonObject = FastJsonUtil.buildJS();
        //jsonObject.put("topic", topic);
        jsonObject.put("zip name", "x.zp");
        jsonObject.put("zip path", "/data/yonyou/nchome/nclogs.log");

        Formatter logBody = new Formatter();
        logBody.setTopic(topic);
//		logBody.setInfos_js(jsonObject);
        //log.info(FastJsonUtil.format(logBody));
        System.out.println(FastJsonUtil.format(logBody));*/

        test();

    }

    public static void test() {

        Formatter formatter = Formatter.init();

        JSONObject jsonObject1 = formatter.buildJS();
        jsonObject1.put("hi", "hi");

        JSONArray jsonArray1 = formatter.buildJSA();
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("j2", "j2");
        jsonArray1.add(jsonObject2);

        formatter.setTopic("test");
        formatter.setDes("this is a test");

        Map map1 = formatter.buildMap();
        map1.put("map1", "map-value");

        List list1 = formatter.buildList();
        list1.add(new Person());

        // 先从fomatter获取对象 -> 再改变对象状态
        Person pObj = (Person) formatter.buildObj(Person.class);
        pObj.setName("reflect");


        // 先构建对象 -》 改变对象状态 -》 放到formatter中
        Person person = FastJsonUtil.o2o(pObj, Person.class);
        person.setName("先赋值");
        Object o = formatter.buildObject(person);


        String format = formatter.format();
        System.out.println(format);
        //log.info(format);
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public Map<String, M> getMap() {
        return map;
    }

    public void setMap(Map<String, M> map) {
        this.map = map;
    }

    public List<L> getList() {
        return list;
    }

    public void setList(List<L> list) {
        this.list = list;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public JSONArray getJsonArray() {
        return jsonArray;
    }

    public void setJsonArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public O getObj() {
        return obj;
    }

    public void setObj(O obj) {
        this.obj = obj;
    }

    @Override
    public String toString() {
        return "Formatter{" +
                "time='" + time + '\'' +
                ", topic='" + topic + '\'' +
                ", des='" + des + '\'' +
                ", map=" + map +
                ", list=" + list +
                ", jsonObject=" + jsonObject +
                ", jsonArray=" + jsonArray +
                ", object=" + object +
                ", obj=" + obj +
                '}';
    }
}
