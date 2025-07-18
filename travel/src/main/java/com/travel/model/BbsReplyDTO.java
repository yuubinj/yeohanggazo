package com.travel.model;

public class BbsReplyDTO {
    private long replyNum;        	// 댓글/답글 고유 번호 (PK)
    private long num;             	// 게시글 번호 (bbs.num)
    private String userId;        	// 작성자 ID
    private String userName;      	// 작성자 이름 (member1 조인)
    private String profilePhoto;  	// 작성자 프로필 사진 (member1 조인)
    private String content;       	// 댓글/답글 내용
    private String reg_date;       	// 작성일자
    private long parentNum;       	// 부모 댓글 번호 (댓글이면 null 또는 0, 답글이면 상위 댓글 번호)
    private int replyLike;
    private int showReply;        	// 관리자 노출 여부 (1: 보임, 0: 숨김)
    private int block;            	// 차단 여부 (1: 차단됨, 0: 정상)

    // 화면에 표시할 계산된 값들
    private int likeCount;       	// 좋아요 수
    private int disLikeCount;
    private int userReplyLike;		// 현재 로그인 사용자의 좋아요 여부 (0: 안 누름, 1: 누름)

    private int answerCount;		// 답글 수 (댓글에 달린 답글 개수, 댓글만 해당됨)
    
	public long getReplyNum() {
		return replyNum;
	}
	public void setReplyNum(long replyNum) {
		this.replyNum = replyNum;
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
	public String getProfilePhoto() {
		return profilePhoto;
	}
	public void setProfilePhoto(String profilePhoto) {
		this.profilePhoto = profilePhoto;
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
	public long getParentNum() {
		return parentNum;
	}
	public void setParentNum(long parentNum) {
		this.parentNum = parentNum;
	}
	public int getReplyLike() {
		return replyLike;
	}
	public void setReplyLike(int replyLike) {
		this.replyLike = replyLike;
	}
	public int getShowReply() {
		return showReply;
	}
	public void setShowReply(int showReply) {
		this.showReply = showReply;
	}
	public int getBlock() {
		return block;
	}
	public void setBlock(int block) {
		this.block = block;
	}
	public int getLikeCount() {
		return likeCount;
	}
	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}
	public int getUserReplyLike() {
		return userReplyLike;
	}
	public void setUserReplyLike(int userReplyLike) {
		this.userReplyLike = userReplyLike;
	}
	public int getAnswerCount() {
		return answerCount;
	}
	public void setAnswerCount(int answerCount) {
		this.answerCount = answerCount;
	}
	public int getDisLikeCount() {
		return disLikeCount;
	}
	public void setDisLikeCount(int disLikeCount) {
		this.disLikeCount = disLikeCount;
	}
    
}

