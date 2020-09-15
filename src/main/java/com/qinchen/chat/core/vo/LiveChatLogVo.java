package com.qinchen.chat.core.vo;

import java.util.Date;

public class LiveChatLogVo {

    private Long shopId;

    private Long userId;

    private Integer type;

    private String nickName;

    private String headUrl;

    private Long taskId;

    private Long liveId;

    private String content;

    private Date inputTime;

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getLiveId() {
        return liveId;
    }

    public void setLiveId(Long liveId) {
        this.liveId = liveId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getInputTime() {
        return inputTime;
    }

    public void setInputTime(Date inputTime) {
        this.inputTime = inputTime;
    }

}