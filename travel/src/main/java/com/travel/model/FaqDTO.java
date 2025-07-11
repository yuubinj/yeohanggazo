package com.travel.model;

public class FaqDTO {
    private long num;
    private Long memberIdx;
    private String userId;
    private String userName;
    private String subject;
    private String content;
    private String reg_date;
    
    private long categoryNum;
    private String category;
    private int enabled;
    private int orderNo;
    
    public long getNum() {
        return num;
    }
    public void setNum(long num) {
        this.num = num;
    }
    public Long getMemberIdx() {
        return memberIdx;
    }
    public void setMemberIdx(Long memberIdx) {
        this.memberIdx = memberIdx;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getReg_date() {
        return reg_date;
    }
    public void setReg_date(String reg_date) {
        this.reg_date = reg_date;
    }
    public long getCategoryNum() {
        return categoryNum;
    }
    public void setCategoryNum(long categoryNum) {
        this.categoryNum = categoryNum;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public int getEnabled() {
        return enabled;
    }
    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }
    public int getOrderNo() {
        return orderNo;
    }
    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }
    
}