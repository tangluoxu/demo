package com.eshore.hb.btsp114busiservice.product.util.Enums;

public enum AuditStatuEnums {
	AUDITING("提交人力部审核"), REVIEWING("待复议"),
	FAIL("审核不通过"),REVIEWED("已复议")
	;
	private final String value;
	AuditStatuEnums(String value) {
		this.value = value;
	}
	public String getValue() {
		return value;
	}

}
