package com.st.api.practice.regx;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: st
 * @date: 2021/5/24 10:09
 * @version: 1.0
 * @description:
 */

@Slf4j
public class ParamsUtils {
    public static void main(String[] args) {

        String yonQL = "select id, tenantid, pubts, effectivedate , countryzone, businessid from org.func.ITOrg  where id<param$(id) and id>param$(id2)";

//        String yonQL = "select id, tenantid, pubts, effectivedate , countryzone, businessid from org.func.ITOrg  where     id    !=    param$(id) and id>  param$(id2) and tenantid=param$(p3) and pubts in (param$(p4),param$(p5)) group by id having sum(id)>param$(p6)";

        JsonObject sqlParams = parseParams(yonQL);
        JsonArray itemsArray = sqlParams.getAsJsonArray("params").getAsJsonArray();
        log.info(itemsArray.toString());

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("params", itemsArray);



        log.info(jsonObject.toString());
    }


    public static void mathchParam() {

        Map fullParm = new HashMap<String, String>();

        // 需要解析的YonQL
//        String yonQL = "select id, tenantid, pubts, effectivedate , countryzone, businessid from org.func.ITOrg  where id<param$(id) and id>param$(id2)";
        String yonQL = "select id, tenantid, pubts, effectivedate , countryzone, businessid from org.func.ITOrg  where     id    <    param$(id) and id>  param$(id2)";

        Matcher matcherFull = buildRegexMatcher(yonQL, "param\\$\\(\\w+\\)");
        while (matcherFull.find()) {
            System.out.println(matcherFull.group());
        }

        Matcher matcherFiled = buildRegexMatcher(yonQL, "(?<=param\\$\\()\\w+(?=\\))");
        while (matcherFiled.find()) {
            System.out.println(matcherFiled.group());
        }

        System.out.println(fullParm);
    }

    public static void matchParamAndSave() {


        JsonObject addtionalElements = new JsonObject();

        JsonArray paramsArray = new JsonArray();
        addtionalElements.add("params", paramsArray);


        JsonObject iterms = new JsonObject();
        JsonArray itermsArray = new JsonArray();
        iterms.add("iterms", itermsArray);
        paramsArray.add(iterms);


        JsonObject filedDetails = new JsonObject();
        filedDetails.addProperty("code", "code");
        filedDetails.addProperty("dataType", "defaultType");
        filedDetails.addProperty("value", "defaultValue");
        filedDetails.addProperty("labels", "defaultLabels");

        itermsArray.add(filedDetails);


        Map fullParm = new HashMap<String, String>();

        String yonQL = "select id, tenantid, pubts, effectivedate , countryzone, businessid from org.func.ITOrg  where id<param$(id) and id>param$(id2)";

        Matcher matcherFull = buildRegexMatcher(yonQL, "param\\$\\(\\w+\\)");
        while (matcherFull.find()) {

            String full = matcherFull.group();
            System.out.println(full);

            Matcher matcherFiled = buildRegexMatcher(full, "(?<=param\\$\\()\\w+(?=\\))");
            while (matcherFiled.find()) {
                String field = matcherFiled.group();
                System.out.println(field);

                JsonObject filedDetailsCurrent = new JsonObject();
                filedDetailsCurrent.addProperty("formalParam", full);
                filedDetailsCurrent.addProperty("code", field);
                filedDetailsCurrent.addProperty("dataType", "defaultType");
                filedDetailsCurrent.addProperty("value", "defaultValue");
                filedDetailsCurrent.addProperty("labels", "defaultLabels");
                itermsArray.add(filedDetailsCurrent);
                fullParm.put(full, field);
            }
        }

        System.out.println(fullParm);
        System.out.println(addtionalElements);
    }

    /**
     * 根据yonql, 解析参数, 构建目标json格式
     * <p></p>
     * <p>
     * yonql:
     * <br>
     * <code>
     * "select id, tenantid, pubts, effectivedate , countryzone, businessid from org.func.ITOrg  where id &lt; param$(id) and id &gt;  param$(id2)"
     * </code>
     * <p>
     * <p>
     * 目标json格式:
     * <br>
     * <code>
     * {
     * "params": [
     * {
     * "items": [
     * {
     * "formalParam": "param$(id)",
     * "code": "id",
     * "dataType": "defaultType",
     * "value": "defaultValue",
     * "labels": "defaultLabels"
     * },
     * {
     * "formalParam": "param$(id2)",
     * "code": "id2",
     * "dataType": "defaultType",
     * "value": "defaultValue",
     * "labels": "defaultLabels"
     * }
     * ]
     * }
     * ]
     * }
     * </code>
     *
     * <p></p>
     *
     * @param yonQL
     * @return
     */
    public static JsonObject parseParams(String yonQL) {

        log.info("yonQL with params is ======> {}", yonQL);

        JsonObject addtionalElements = buildFormatedJson();


//         正则, 匹配: "param$(id2)"
        Matcher matcherFull = buildRegexMatcher(yonQL, "param\\$\\(\\w+\\)");
//        Matcher matcherFull = buildRegexMatcher(yonQL, "\\s+\\w+\\s{0,}.{1,2}\\s{0,}param\\$\\(\\w+\\)");
//        Matcher matcherFull = buildRegexMatcher(yonQL, "(?<=param\\$\\()\\w+(?=\\))");
        while (matcherFull.find()) {
            String full = matcherFull.group();
            System.out.println(full);

//              正则, 匹配"param$(id2)"中的"id2"
            Matcher matcherFiled = buildRegexMatcher(full, "(?<=param\\$\\()\\w+(?=\\))");
            while (matcherFiled.find()) {
                String field = matcherFiled.group();
                putParamsInfo(addtionalElements, full, field);
            }
        }

        log.info("the formated json throung parsing yonql is ======> {}", addtionalElements);
        return addtionalElements;
    }

    private static Matcher buildRegexMatcher(String yonQL, String s) {
        Pattern patternFull = Pattern.compile(s);
        return patternFull.matcher(yonQL);
    }

    private static void putParamsInfo(JsonObject addtionalElements, String full, String field) {

//        JsonArray itemsArray = addtionalElements.getAsJsonArray("params").get(0).getAsJsonObject().getAsJsonArray("items").getAsJsonArray();
        JsonArray paramsArray = addtionalElements.getAsJsonArray("params");


        JsonObject iterms = new JsonObject();
        JsonArray itemsArray = new JsonArray();
        iterms.add("items", itemsArray);

        JsonObject filedDetailsCurrent = new JsonObject();
        filedDetailsCurrent.addProperty("formalParam", full);
        filedDetailsCurrent.addProperty("code", field);
        filedDetailsCurrent.addProperty("dataType", "defaultType");
        filedDetailsCurrent.addProperty("defaultValues", "defaultValues");
        filedDetailsCurrent.addProperty("defaultLabels", "defaultLabels");

        itemsArray.add(filedDetailsCurrent);
        paramsArray.add(iterms);
    }

    /**
     * json的要求格式:
     * <P></P>
     * {
     * "params": [
     * {
     * "items": [
     * {
     * "formalParam": "param$(id)",
     * "code": "id",
     * "dataType": "defaultType",
     * "value": "defaultValue",
     * "labels": "defaultLabels"
     * },
     * {
     * "formalParam": "param$(id2)",
     * "code": "id2",
     * "dataType": "defaultType",
     * "value": "defaultValue",
     * "labels": "defaultLabels"
     * }
     * ]
     * }
     * ]
     * }
     *
     * @return
     */
    private static JsonObject buildFormatedJsonOneItems() {
        JsonObject addtionalElements = new JsonObject();

        JsonArray paramsArray = new JsonArray();
        addtionalElements.add("params", paramsArray);

        JsonObject iterms = new JsonObject();
        JsonArray itemsArray = new JsonArray();
        iterms.add("items", itemsArray);
        paramsArray.add(iterms);
        return addtionalElements;
    }

    private static JsonObject buildFormatedJson() {
        JsonObject addtionalElements = new JsonObject();

        JsonArray paramsArray = new JsonArray();
        addtionalElements.add("params", paramsArray);


        return addtionalElements;
    }

}
