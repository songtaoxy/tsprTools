package com.st.utils.json;

import com.st.utils.log.LogUtils;
import com.st.utils.string.StringUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonUtilsTest {

  @Test
  void isJson() {
    String s = "{\"name\":\"Michael\",\"age\":24,\"birthday\":\"2018-09-09\"}";
    String s2 = "[{\"name\":\"Michael\",\"age\":24,\"birthday\":\"2018-09-09\"}]";
    String s3 = "[{\"name\":\"Michael\",\"age\":24,\"birthday\":\"2018-09-09\"}];";
    String s4 = "{\"name\":\"Michael\",\"age\":24,\"birthday\":\"2018-09-09\"};";
    String s5 =
        "[\n"
            + "\t{\n"
            + "\t\t\"birthday\":\"2018-09-09\",\n"
            + "\t\t\"name\":\"Michael\",\n"
            + "\t\t\"age\":24\n"
            + "\t},\n"
            + "\t{\n"
            + "\t\t\"birthday\":\"2018-09-09\",\n"
            + "\t\t\"name\":\"Michael\",\n"
            + "\t\t\"age\":24\n"
            + "\t}\n"
            + "\n"
            + "]\n";

    String s6 =
        "{\"k1\":\"v1\",\n"
            + "\"k2\":[\n"
            + "\t{\n"
            + "\t\t\"birthday\":\"2018-09-09\",\n"
            + "\t\t\"name\":\"Michael\",\n"
            + "\t\t\"age\":24\n"
            + "\t},\n"
            + "\t{\n"
            + "\t\t\"birthday\":\"2018-09-09\",\n"
            + "\t\t\"name\":\"Michael\",\n"
            + "\t\t\"age\":24\n"
            + "\t}\n"
            + "\n"
            + "]\n"
            + "}\n";

    LogUtils.foal(s,"");
    LogUtils.foal(s2,"");
    LogUtils.foal(s3,"");
    LogUtils.foal(s4,"");
    LogUtils.foal(s5,"");
    LogUtils.foal(s6,"");
  }
}
