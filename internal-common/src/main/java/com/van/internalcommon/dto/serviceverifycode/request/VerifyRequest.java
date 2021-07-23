package com.van.internalcommon.dto.serviceverifycode.request;

// todo 是否要引入
public class VerifyRequest {
//    @NotNull
    private int identity;
//    @NotNull
    private String phoneNumber;
//    @NotNull
    private String code;

    public int getIdentity() {
        return identity;
    }

    public void setIdentity(int identity) {
        this.identity = identity;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
