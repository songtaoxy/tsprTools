package com.st.practice.mockito;

import javafx.beans.binding.When;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
public class MockitoStaicMethodWihtJunit4Test {

  @Mock MockitoStaicMethodWihtJunit4 mockitoStaicMethodWihtJunit4;

	@Test
	public void add() {
    when(MockitoStaicMethodWihtJunit4.add(1, 2)).thenReturn(4);

    System.out.println(MockitoStaicMethodWihtJunit4.add(1, 2));
	}
}