package com.st.modules.func;

import com.st.modules.enums.BizEnum;

import java.util.function.Function;

public class Main {

    public static void main(String[] args) {
        Function<String, String> sf = BizHandlers.pathHandlerMap.get(BizEnum.FGLS.getCode());
        String apply = sf.apply(BizEnum.FGLS.getCode());

        System.out.println(apply);

    }
}
