package com.van.servicesms.entity;

import java.io.Serializable;
import java.util.Date;

public class SmsTemplate implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 短信模板ID
     */
    private String templateId;

    private String templateName;

    /**
     * 模板内容
     */
    private String templateContent;

    /**
     * 模板类型（1：营销；2：通知；3：订单）
     */
    private Boolean templateType;

    private Date createTime;

    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateContent() {
        return templateContent;
    }

    public void setTemplateContent(String templateContent) {
        this.templateContent = templateContent;
    }

    public Boolean getTemplateType() {
        return templateType;
    }

    public void setTemplateType(Boolean templateType) {
        this.templateType = templateType;
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
