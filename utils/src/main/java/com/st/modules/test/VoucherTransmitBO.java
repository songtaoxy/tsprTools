package com.st.modules.test;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherTransmitBO {

    private String no;
    private String type;
    private String date;
    private Double amount;
    private String remark;



    // Map 封装构造
    public VoucherTransmitBO(Map<String, Object> map) {
        this.no = map.get("no") != null ? map.get("no").toString() : null;
        this.type = map.get("type") != null ? map.get("type").toString() : null;
        this.date = map.get("date") != null ? map.get("date").toString() : null;
        this.amount = map.get("amount") != null ? Double.valueOf(map.get("amount").toString()) : null;
        this.remark = map.get("remark") != null ? map.get("remark").toString() : null;
    }


    /**
     * <pre>
     *  mapList.stream().map(VoucherBO::new)
     *  有参/ok: .map(m -> new VoucherBO(m))
     *  无参/no: .map(m -> new VoucherBO())
     * </pre>
     * @param mapList
     * @return
     */
    public static List<VoucherTransmitBO> mapListToBOList(List<Map<String, Object>> mapList) {
        if (mapList == null) return Collections.emptyList();
        return mapList.stream()
                .map(VoucherTransmitBO::new)
                .collect(Collectors.toList());
    }
}
