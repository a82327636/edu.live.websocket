package com.qinchen.chat.core.bean;


public class ReportLiveUserDetailBean {

    private Long taskId;

    private Long shopId;

    private Long liveId;

    private Long userId;

    private String nickName;

    private String headUrl;

    private Integer totalChatNum;

    private Long totalViewNum;

    private Long firstTime;

    private Long startTime;

    private Long endTime;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Long getLiveId() {
        return liveId;
    }

    public void setLiveId(Long liveId) {
        this.liveId = liveId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName == null ? null : nickName.trim();
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl == null ? null : headUrl.trim();
    }

    public Integer getTotalChatNum() {
        return totalChatNum;
    }

    public void setTotalChatNum(Integer totalChatNum) {
        this.totalChatNum = totalChatNum;
    }

    public Long getTotalViewNum() {
        return totalViewNum;
    }

    public void setTotalViewNum(Long totalViewNum) {
        this.totalViewNum = totalViewNum;
    }

    public Long getFirstTime() {
        return firstTime;
    }

    public void setFirstTime(Long firstTime) {
        this.firstTime = firstTime;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

}