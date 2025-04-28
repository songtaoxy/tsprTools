package com.st.modules.classUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class ClassUtilsTest {


    @Test
    public void validNumbers(){

        Per.PerBuilder builder = Per.builder();
        Per.PerBuilder builder1 = Per.builder();

        Class<? extends Per.PerBuilder> aClass = builder1.getClass();
        Class<? extends Per.PerBuilder> aClass1 = builder1.getClass();

        System.out.println();

        System.out.println(aClass1.getName().equals(aClass1.getName()));
        System.out.println(aClass1 == aClass);

    }

}

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
class Per{
    private String name;
}