package com.travel.model;

import java.util.List;

import com.travel.util.MyMultipartFile;

public class MyTripDTO {
	private long num;
	private String userId;
	private String userName;
	private String subject;
	private String content;
	private int hitCount;
	private String reg_date;
	
	private int replyCount;
	private int myTripLikeCount;
	private int myTripReplyCount;

	private List<MyMultipartFile> listFile;
    
    private long fileNum;
	private String saveFilename;
	private String originalFilename;
	private long fileSize;
	
	private List<String> listFileSavename;
	
	public List<String> getListFileSavename() {
		return listFileSavename;
	}
	public void setListFileSavename(List<String> listFileSavename) {
		this.listFileSavename = listFileSavename;
	}
	public long getNum() {
		return num;
	}
	public void setNum(long num) {
		this.num = num;
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
	public int getHitCount() {
		return hitCount;
	}
	public void setHitCount(int hitCount) {
		this.hitCount = hitCount;
	}
	public String getReg_date() {
		return reg_date;
	}
	public void setReg_date(String reg_date) {
		this.reg_date = reg_date;
	}
	public List<MyMultipartFile> getListFile() {
		return listFile;
	}
	public void setListFile(List<MyMultipartFile> listFile) {
		this.listFile = listFile;
	}
	public long getFileNum() {
		return fileNum;
	}
	public void setFileNum(long fileNum) {
		this.fileNum = fileNum;
	}
	public String getSaveFilename() {
		return saveFilename;
	}
	public void setSaveFilename(String saveFilename) {
		this.saveFilename = saveFilename;
	}
	public String getOriginalFilename() {
		return originalFilename;
	}
	public void setOriginalFilename(String originalFilename) {
		this.originalFilename = originalFilename;
	}
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}	
    public int getReplyCount() {
		return replyCount;
	}
	public void setReplyCount(int replyCount) {
		this.replyCount = replyCount;
	}
	public int getMyTripLikeCount() {
		return myTripLikeCount;
	}
	public void setMyTripLikeCount(int myTripLikeCount) {
		this.myTripLikeCount = myTripLikeCount;
	}
	public int getMyTripReplyCount() {
		return myTripReplyCount;
	}
	public void setMyTripReplyCount(int myTripReplyCount) {
		this.myTripReplyCount = myTripReplyCount;
	}
}
