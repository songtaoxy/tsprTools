package com.st.practice.testable;


import java.util.*;

/**
 * @author: st
 * @date: 2022/3/18 23:54
 * @version: 1.0
 * @description:
 */
public class MapValues2List {

  public static void main(String[] args) {
    Map<String, String> map = new HashMap<String, String>();
    map.put("1", "AA");
    map.put("2", "BB");
    map.put("4", "");
    map.put("3", "CC");
    map.put("5", "CC");


    MapValues2List mapValues2List = new MapValues2List();
    System.out.println(mapValues2List.mapValuesContains(map,""));
    System.out.println(mapValues2List.mapValuesContains(map,"bb"));
    System.out.println(mapValues2List.mapValuesContains(map,"  "));

    // List<String> valueList = new ArrayList<String>(valueCollection);


  }

  public boolean mapValuesContains(Map map,String src){
    Collection<String> valueCollection = map.values();
    final int size = valueCollection.size();

    String[] valueArray = new String[size];
    String[] strings = (String[]) map.values().toArray(valueArray);
    Arrays.stream(strings).forEach(System.out::println);

    List<String> tempList = Arrays.asList(strings);
    return tempList.contains(src);
  }
}

