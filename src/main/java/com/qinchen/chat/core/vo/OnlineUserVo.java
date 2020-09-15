package com.qinchen.chat.core.vo;

public class OnlineUserVo {

    private Long taskId;

    private Integer totalUserNum = 0;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Integer getTotalUserNum() {
        return totalUserNum;
    }

    public void setTotalUserNum(Integer totalUserNum) {
        this.totalUserNum = totalUserNum;
    }
}
