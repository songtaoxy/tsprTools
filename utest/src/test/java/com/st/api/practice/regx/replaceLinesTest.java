package com.st.api.practice.regx;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class HandleSpecialCharactersTest {

	@Test
	void handleSpecialCharacters() {
		String targetStr = "select id, tenantid, pubts, effectivedate , countryzone, businessid \nfrom org.func.ITOrg  \nwhere id>param$(p1)and tenantid<param$(p3) and tenantid between param$(p4) and param$(p5) and businessid=param$(p5) \ngroup by tenantid \nlimit 100 \n";

		String resultStr = "select id, tenantid, pubts, effectivedate , countryzone, businessid from org.func.ITOrg where id>param$(p1)and tenantid<param$(p3) and tenantid between param$(p4) and param$(p5) and businessid=param$(p5) group by tenantid limit 100";


		assertThat(HandleSpecialCharacters.handleSpecialCharacters(targetStr)).isEqualTo(resultStr);
	}
}