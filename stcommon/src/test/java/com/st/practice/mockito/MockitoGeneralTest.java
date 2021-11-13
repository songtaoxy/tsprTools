package com.st.practice.mockito;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * mockito功能测试 <ui>
 * <li>{@link org.junit.jupiter.api.extension.ExtendWith} Junit5 提供
 * <li>{@link org.mockito.junit.jupiter.MockitoExtension} mockito 为适配junit5 提供 </ui>
 */
// @ExtendWith(MockitoExtension.class)
@ExtendWith(MockitoExtension.class)
@PrepareForTest(MockitoGeneral.class)
class MockitoGeneralTest {

  @Mock List<String> mockedList;
  @Mock MockitoGeneral mockitoGeneral;

  @Test
  void m001() {

    // using mock object
    mockedList.add("one");
    mockedList.add("two");
    mockedList.add("two");
    mockedList.clear();

    // verification
    // 验证是否调用过一次 mockedList.add("one")方法，若不是（0次或者大于一次），测试将不通过
    verify(mockedList).add("one");
    // 验证调用过2次 mockedList.add("two")方法，若不是，测试将不通过
    verify(mockedList, times(2)).add("two");
    // 验证是否调用过一次 mockedList.clear()方法，若没有（0次或者大于一次），测试将不通过
    verify(mockedList).clear();

    // 设置桩
    when(mockedList.get(0)).thenReturn("first");
    when(mockedList.get(1)).thenThrow(new RuntimeException());

    // 打印 "first"
    System.out.println(mockedList.get(0));

    // 这里会抛runtime exception
    System.out.println(mockedList.get(1));

    // 这里会打印 "null" 因为 get(999) 没有设置
    System.out.println(mockedList.get(999));

    // Although it is possible to verify a stubbed invocation, usually it's just redundant
    // If your code cares what get(0) returns, then something else breaks (often even before
    // verify() gets executed).
    // If your code doesn't care what get(0) returns, then it should not be stubbed. Not convinced?
    // See here.
    verify(mockedList).get(0);
  }

}
