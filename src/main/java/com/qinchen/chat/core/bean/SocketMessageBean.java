package com.qinchen.chat.core.bean;

public class SocketMessageBean {

    private int type;

    private Long taskId;

    private Long liveId;

    private Long shopId;

    private String data;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }
}
