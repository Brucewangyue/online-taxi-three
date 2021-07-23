package com.van.internalcommon.util;


public class JwtInfo{
    private String subject;
    private Long issueDate;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Long getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Long issueDate) {
        this.issueDate = issueDate;
    }
}