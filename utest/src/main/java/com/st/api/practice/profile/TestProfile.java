package com.st.api.practice.profile;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.sound.midi.Soundbank;
import javax.xml.transform.sax.SAXTransformerFactory;

/**
 * @author: st
 * @date: 2021/10/28 21:15
 * @version: 1.0
 * @description:
 */
  @Slf4j
  public class TestProfile {

    public static void main(String[] args) {
      produceString();
    }

    private static String produceString() {

      for (int i = 0; i < 2000000000; i++) {
        System.out.println(i);
      }
      return "Hello World";
    }

    @Test
    void testTime() {

      System.out.println("hi");
      printok();
    }

    public static void printok() {
      System.out.println("OK");
    }
  }
