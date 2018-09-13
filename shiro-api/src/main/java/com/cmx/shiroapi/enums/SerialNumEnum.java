package com.cmx.shiroapi.enums;

public enum SerialNumEnum {

    NULL(0, "", "空前缀", ""),
    FINANCE(1, "fi", "账务流水序列号", "account_flow_no"),
    CHARGE(2, "ch", "支付单序列号", "charge_no"),
    ORDER(3, "odr", "订单序列号", "order_id"),
    RECHARGE(4, "rcg", "充值单序列号", "recharge_no"),
    BALANCE_BONUS(5, "bbn", "增额序列号", "balance_bonus_no"),
    BUSINESE(6, "bu", "业务流水序列号", "business_flow_no"),
    LOG(7, "lo", "日志详情序列号", "log_no"),
    NOTIFY(8, "no", "异步通知序列号", "channel_notify_no"),
    REFUND(9, "re", "退款单序列号", "refund_no"),
    MEMBER(10, "meb", "用户序列号", "member_id"),
    MERCHANT_NUMBER(11, "me", "商户序列号", "merch_no"),
    APPID(12, "app", "商户应用序列号", "app_id"),
    SETTLE_ACCOUNT(13, "stl", "结算账户序列号", "settle_account_id"),
    OPENID(14, "pn", "用户序列号", "open_id"),
    TESTID(15, "test_", "测试序列号", "test_id"),
    TASK_NO(16, "ts", "任务序列号", "task_no"),
    BATCH_NO(17, "bc", "对账序列号", "batch_no"),
    ROYALTY_ID(18, "ro", "分润对象序列号", "royalty_id"),
    ROYALTY_SETTLEMENT_ID(19, "rs", "分润结算序列号", "royalty_settlement_id"),
    ROYALTY_TRANSACTION_ID(20, "rts", "分润结算明细序列号", "royalty_transaction_id"),
    TRANSFER_ID(21, "ts", "转账序列号", "transfer_id"),
    ACCOUNT_ID(22, "ac", "账户序列号", "account_id"),
    ACCOUNT_FLOW_ID(23, "ac", "账户序列号", "account_flow_id"),
    WITHDRAWAL_ID(24, "wi", "提现序列号", "withdrawal_id");

    private final int businessId;
    private final String typeCode;
    private final String typeCN;
    private final String typeEN;

    SerialNumEnum(int businessId, String typeCode, String typeCN, String typeEN) {
        this.businessId = businessId;
        this.typeCode = typeCode;
        this.typeCN = typeCN;
        this.typeEN = typeEN;
    }

    public int getBusinessId() {
        return businessId;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public String getTypeCN() {
        return typeCN;
    }

    public String getTypeEN() {
        return typeEN;
    }
}
