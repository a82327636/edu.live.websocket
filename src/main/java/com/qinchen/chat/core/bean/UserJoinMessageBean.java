package com.qinchen.chat.core.bean;

public class UserJoinMessageBean extends SendMessageBean{

    private Integer totalNum;// 总用户数

    public Integer getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }
}
