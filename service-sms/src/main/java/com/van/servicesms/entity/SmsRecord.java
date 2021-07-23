package com.van.servicesms.entity;

import java.io.Serializable;
import java.util.Date;

public class SmsRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 乘客手机号
     */
    private String phoneNumber;

    /**
     * 短信内容
     */
    private String smsContent;

    /**
     * 发送时间
     */
    private Date sendTime;

    /**
     * 操作人
     */
    private String operatorName;

    /**
     * 发送状态 0:失败  1: 成功
     */
    private Integer sendFlag;

    /**
     * 发送失败次数
     */
    private Integer sendNumber;

    private Date createTime;

    private Date updateTime;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSmsContent() {
        return smsContent;
    }

    public void setSmsContent(String smsContent) {
        this.smsContent = smsContent;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public Integer getSendFlag() {
        return sendFlag;
    }

    public void setSendFlag(Integer sendFlag) {
        this.sendFlag = sendFlag;
    }

    public Integer getSendNumber() {
        return sendNumber;
    }

    public void setSendNumber(Integer sendNumber) {
        this.sendNumber = sendNumber;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}