package com.st.modules.time;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;


class DateFieldSelectorTest {

    List<BO> list = Arrays.asList(
            new BO("A", LocalDate.of(2025, 1, 1)),
            new BO("B", LocalDate.of(2023, 6, 15)),
            new BO("C", LocalDate.of(2024, 3, 20))
    );

    @Test
    void getMinByDateField() {
        BO minBo = DateFieldSelector.getMinByDateField(list, BO::getGldetailts);
        System.out.println("最小日期对象: " + minBo);
    }

    @Test
    void getMaxByDateField() {
        BO maxBo = DateFieldSelector.getMaxByDateField(list, BO::getGldetailts);
        System.out.println("最大日期对象: " + maxBo);
    }
}


@Data
@AllArgsConstructor
@NoArgsConstructor
class BO {
    private String name;
    private LocalDate gldetailts;
}
