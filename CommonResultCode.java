package com.eshore.hb.btsp114busiservice.product.util.ResultMsg;


public enum CommonResultCode implements IResultCode {

    FAILED("1", "失败"),
    SUCCESS("0", "成功"),
    BODY_NOT_MATCH("400", "请求的数据格式不符!"),
    NOT_FOUND("404", "未找到该资源!"),
    INTERNAL_SERVER_ERROR("500", "服务器内部错误!"),
    SERVER_BUSY("503", "服务器正忙，请稍后再试!");

    private String resultCode;
    private String resultMsg;

    private CommonResultCode(String resultCode, String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

    public String getResultCode() {
        return this.resultCode;
    }

    public String getResultMsg() {
        return this.resultMsg;
    }
}
