package com.van.internalcommon.constant;

public enum ResponseStatusEnum {
    VERIFY_CODE_ERROR(10001, "短信验证码验证失败"),

    SUCCESS(0, "success"),
    INTERNAL_SERVER_EXCEPTION(-1, "exception"),
    FAIL(1, "");

    private final int code;
    private final String value;

    private ResponseStatusEnum(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public int getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
