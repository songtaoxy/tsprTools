package com.st.practice.junit5;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Junit5DemonTest {

	@Test
	@DisplayName("test assert")
	void add() {

		int a = 5;
		int b = 5;

		assertEquals(10,Junit5Demon.add(a,b));
	}
}