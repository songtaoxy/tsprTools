package com.alibaba.testable.v079.demo.inherit;

import com.alibaba.testable.core.annotation.MockContainer;
import com.alibaba.testable.core.annotation.MockInvoke;
import org.junit.jupiter.api.Test;

import static com.alibaba.testable.core.matcher.InvocationVerifier.verifyInvoked;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 演示使用@MockContainer注解实现Mock容器类的多重继承
 * Demonstrate multiple inherit of mock container class with @MockContainer annotation
 */
class DemoMultipleInheritTest {

    private DemoMultipleInherit demoMultipleInherit = new DemoMultipleInherit();

    public static class PrefixMock {
        @MockInvoke(targetClass = DemoMultipleInherit.class)
        private String prefix() {
            return "in_";
        }
    }

    public static class SuffixMock {
        @MockInvoke(targetClass = DemoMultipleInherit.class)
        private String suffix(int some, String more, Object[] parameters) {
            return "_it";
        }
    }

    @MockContainer(inherits = { PrefixMock.class, SuffixMock.class })
    public static class Mock {
        @MockInvoke(targetClass = DemoMultipleInherit.class)
        private String middle() {
            return "her";
        }
    }

    @Test
    public void should_use_mock_method_in_parent_class() {
        assertEquals("in_her_it", demoMultipleInherit.entry());
        verifyInvoked("prefix").withTimes(1);
        verifyInvoked("middle").withTimes(1);
        verifyInvoked("suffix").withTimes(1);
    }

}