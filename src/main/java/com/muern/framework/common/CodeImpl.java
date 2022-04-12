package com.muern.framework.common;

/**
 * @author gegeza
 * @date 2022/03/31
 */
public enum CodeImpl implements Code {
    OK("000000", "操作成功"),
    FAIL("000001", "操作失败"),
    ERR_PARAMS("000002", "参数错误"),
    ERR_TOKEN("000003", "令牌错误"),
    ERR_SIGN("000004", "签名错误"),
    ;

    private final String code;
    private final String desc;

    CodeImpl(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }
}
