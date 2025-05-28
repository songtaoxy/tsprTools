package com.st.modules.test.boc.voucher;

import com.st.modules.id.snowflake.SnowFlakeUtil;
import com.st.modules.time.TimeUtils;
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

    // 一级行&OU
    private String orgL1;
    private String ouCode;
    private String orgL1AndOuCode;

    //	序号;是
    private String lineNo;
    //	凭证来源;是; 固定值:应付款
    private String userJeSourceName;
    //	凭证类别;是; 自定义项
    private String userJeCategoryName;
    //	凭证批名;是
    // 批次号: AP+OU编号+日期+自定义流水号
    // 示例: AP88820250526001
    private String jeBatchName;
    //	凭证名称;是
    private String jeHeaderName;
    //	交易流水号;是
    private String transactionNum;
    //	入账日期;是
    private String accountingDate;
    //	币种;是
    private String currencyCode;
    //	币种汇率;是
    private String currencyRate;
    //	影像编号;否
    private String imageNumber;
    //	影像地址;否
    private String imageAddress;
    //	凭证行号;是
    private String jeLineNum;
    //	机构段;是
    private String segment1;
    //	责任中心段;是
    private String segment2;
    //	科目段;是
    private String segment3;
    //	参考段;是
    private String segment4;
    //	产品段;是
    private String segment5;
    //	内部往来段;是
    private String segment6;
    //	专项段;是
    private String segment7;
    //	备用1段;是
    private String segment8;
    //	备用2段;是
    private String segment9;
    //	借方金额;是
    private String enteredDr;
    //	贷方金额;是
    private String enteredCr;
    //	借方入账金额;否
    private String accountedDr;
    //	贷方入账金额;否
    private String accountedCr;
    //	摘要;是
    private String lineDesc;
    //	参考1;否
    private String reference21;
    //	参考2;否
    private String reference22;
    //	参考3;否
    private String reference23;
    //	参考4;否
    private String reference24;
    //	参考5;否
    private String reference25;
    //	参考6;否
    private String reference26;
    //	参考7;否
    private String reference27;
    //	参考8;否
    private String reference28;
    //	参考9;否
    private String reference29;
    //	参考10;否
    private String reference30;
    //	来源ID;是
    private String glSlLinkId;
    //	来源表;是
    private String glSlLinkTable;

    // ##################################
    // 辅助字段
    // ##################################
    private String voucherCode;

    // ##################################
    // 经费总账返回字段. 8个
    // ##################################
    // 原始文件名
    private String fileName;
    // 文件日期 YYYY-MM-DD
    private String fileDate;
    // 处理状态: S成功; E失败
    private String processFlag;
    // 错误消息
    private String errorMessage;
    // 凭证编号
    private String glDocNumber;
    // 凭证入账日期
    private String goDocAccountingDate;
    // 凭证ID
    private String glJeHeaderId;
    // 凭证行号
    private String glJeLineNum;




    public VoucherTransmitBO(Map<String, Object> baseMap, Map<String, Object> extraMap) {

//        this.orgL1=(String) baseMap.get("orgL1");
//        this.ouCode=this.orgL1=(String) baseMap.get("ouCode");
//        this.orgL1AndOuCode = (String) baseMap.get("orgL1AndOuCode");
        String orgL1AndOuCodeTemp = (String) baseMap.get("orgL1AndOuCode");
        String org_L1 = orgL1AndOuCodeTemp.substring(0,5);
        String ou_code = orgL1AndOuCodeTemp.substring(5);
        this.orgL1AndOuCode = org_L1+ou_code;


        // 全局编号
        this.lineNo = "";
        //	凭证来源;是; 固定值:应付款
        this.userJeSourceName = "应付款";
        //	凭证类别;是; 自定义项
//        this.userJeCategoryName = "自定义项";
        this.userJeCategoryName =(String) baseMap.get("type");
        // 凭证批名: AP+OU编号+日期+自定义流水号; 示例：AP88820250526001
        this.jeBatchName="AP"+"自定义项"+ TimeUtils.time2StrCust("yyyyMMdd")+ SnowFlakeUtil.nextIdAsUuid();
        // 凭证名称：应付单默认采购发票，付款单默认付款
        this.jeHeaderName="自定义项";
        // 6、交易流水号：应付系统，应付单/付款单号
        this.transactionNum="自定义项";
        // 7、入账日期：推送此文件的日期作为入账日期，生成凭证后需要回写到应付单中的入账日期（GL日期），发送分录时增加
        // 生成凭证的入账日期,格式YYYY-MM-DD）
        this.accountingDate=TimeUtils.time2StrCust("yyyy-MM-dd");
        // 8、币种：默认值CNY，后续确定是否存在其他币种，如存在，取应付单上的币种字段
        // 表: bd_currtype
        this.currencyCode="自定义项";
        // 9、币种汇率：默认值1，后续确定是否存在其他币种，如存在，取单据上的汇率字段
        // 分录表/excrate2
        this.currencyRate= (String) baseMap.get("excrate2");
        // 10、影像编号：暂为空
        this.imageNumber="";
        // 11、影像地址：暂为空
        this.imageAddress="";
        // 12、凭证行号：发送的同一文件按照凭证类别（采购发票和付款）分别排凭证号，发送分录时增加
        // 每个类别下, 重新分配行号
        this.jeLineNum="";
        //13、机构段：根据单据上的分配行的机构字段，对应明细机构，与经费总账同一数据来源
        this.segment1="自定义项";
         // 14、责任中心段：根据应付单上的分配行的责任中心字段（部门），明细责任中心，无值默认为0（应付贷方和付款），与经费总账同一数据来源
        this.segment2="自定义项";
        // 15、科目段：凭证模板直接取分配行科目字段（末级）
        // 分录表/accountcode
        this.segment3=(String) baseMap.get("accountcode");
        // 16、参考段：按照分配行
        this.segment4="自定义项";
        // 17、产品段：按照分配行
        this.segment5="自定义项";
        // 18、内部往来段：按照分配行，跨机构往来业务
        this.segment6="自定义项";
        // 19、专项段：按照分配行
        this.segment7="自定义项";
        // 20、备用1段：默认0
        this.segment8="0";
        // 21、备用2段：默认0
        this.segment9="0";
        // 22、借方金额
        // 分录表/localdebitamount
        this.enteredDr=(String) baseMap.get("localdebitamount");
        // 23、贷方金额：应付单贷方金额-根据应付单分配行的分摊金额，涉及税额根据及是否抵扣进项税参考分配行关联的发票借贷方仅一方有值
        this.enteredCr=(String) baseMap.get("localcreditamount");
        // 24, 借方入账金额
        this.accountedDr=(String) baseMap.get("debitamount");
        // 25、贷方入账金额：确定是否存在外币，如存在，金额为贷方金额乘以汇率
        this.accountedCr=(String) baseMap.get("creditamount");
        // 26、应付单摘要：借方摘要为应付单供应商名称+应付单号+应付单行摘要（供应商名称截取前12位）
        this.lineDesc=(String) baseMap.get("explanation");
        // 27、参考1：供应商名称，使用单据表头供应商名称
        this.reference21="自定义项";
        // 28、参考2：凭证类别为发票，取发票ID（应付单主键）；凭证类别为付款，取付款ID（付款单主键） 无则为空
        this.reference22="自定义项";
        // 29、参考3：凭证类别为发票，取发票行号，贷方暂为空；凭证类别为付款，取付款ID(付款单主键)  无则为空
        this.reference23="自定义项";
        // 30、参考4：凭证类别为发票，暂为空；凭证类别为付款，取付款单单据编号
        this.reference24="自定义项";
        // 31、参考5：凭证类别为发票，取发票编号(应付单号)；凭证类别为付款，借方取发票编号（应付单号），贷方暂为空
        this.reference25="自定义项";
        // 32、参考6：凭证类别为发票，默认值AP Invoices；凭证类别为付款，默认值AP Payments
        this.reference26="自定义项";
        // 33、参考7：账套ID，取固定值2004
        this.reference27="2004";
        // 34、参考8：凭证类别为发票，暂为空；凭证类别为付款，取支付编号（付款单编号）  无则为空
        this.reference28="自定义项";
        // 35、参考9：凭证类别为发票，暂为空；凭证类别为付款，取付款ID（付款单主键）    无则为空
        this.reference29="自定义项";
        // 36、参考10：凭证类别为发票，借方固定值为CHARGE，贷方固定值为LIABILITY；凭证类别为付款，借方固定值为LIABILITY，贷方固定值为CASH
        this.reference30="自定义项";
        // 37、来源ID：为空
        this.glSlLinkId="";
        // 38、来源表：为空
        this.glSlLinkTable="";

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
    public static List<VoucherTransmitBO> mapListToBOList(
            List<Map<String, Object>> mapList,
            Map<String, Object> extraMap
    ) {
        if (mapList == null || mapList.isEmpty()) return Collections.emptyList();
        return mapList.stream()
                .map(map -> new VoucherTransmitBO(map, extraMap))
                .collect(Collectors.toList());
    }
}
