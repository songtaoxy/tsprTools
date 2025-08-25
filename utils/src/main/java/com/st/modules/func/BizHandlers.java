package com.st.modules.func;

import com.st.modules.enums.BizEnum;
import com.st.modules.file.build.path.PathBuilder;
import lombok.Data;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Data
public class
BizHandlers {

    public static final Map<String, Function<String,String>> pathHandlerMap;

     static {
         Map<String, Function<String, String>> pathMap = new HashMap<>();
         pathMap.put(BizEnum.FGLS.getCode(), PathBuilder::buildPath4Fgls);
         pathHandlerMap = Collections.unmodifiableMap(pathMap);
         System.out.println("");
     }



}
