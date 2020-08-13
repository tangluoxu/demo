package com.eshore.hb.btsp114busiservice.product.util.ResultMsg;



import java.io.Serializable;
public class ResultMsg<T> implements Serializable{
    private T datas;
    private String resp_code;
    private String resp_msg;

    public ResultMsg() {
    }

    public static <T> ResultMsg<T> success() {
        ResultMsg<T> result = new ResultMsg();
        result.setResp_code(CommonResultCode.SUCCESS.getResultCode());
        result.setResp_msg(CommonResultCode.SUCCESS.getResultMsg());
        result.setDatas((T) null);
        return result;
    }

    public static <T> ResultMsg<T> success(T data) {
        ResultMsg<T> result = new ResultMsg();
        result.setResp_code(CommonResultCode.SUCCESS.getResultCode());
        result.setResp_msg(CommonResultCode.SUCCESS.getResultMsg());
        result.setDatas(data);
        return result;
    }

    public static <T> ResultMsg<T> failed(String code, String msg) {
        ResultMsg<T> result = new ResultMsg();
        result.setResp_code(code);
        result.setResp_msg(msg);
        result.setDatas((T) null);
        return result;
    }

    public static <T> ResultMsg<T> failed(String msg) {
        ResultMsg<T> result = new ResultMsg();
        result.setResp_code(CommonResultCode.FAILED.getResultCode());
        result.setResp_msg(msg);
        result.setDatas((T) null);
        return result;
    }

    public static <T> ResultMsg<T> failed(IResultCode errorCode) {
        ResultMsg<T> result = new ResultMsg();
        result.setResp_code(errorCode.getResultCode());
        result.setResp_msg(errorCode.getResultMsg());
        result.setDatas((T) null);
        return result;
    }
    public T getDatas() {
        return datas;
    }

    public void setDatas(T datas) {
        this.datas = datas;
    }

    public String getResp_code() {
        return resp_code;
    }

    public void setResp_code(String resp_code) {
        this.resp_code = resp_code;
    }

    public String getResp_msg() {
        return resp_msg;
    }

    public void setResp_msg(String resp_msg) {
        this.resp_msg = resp_msg;
    }
}
