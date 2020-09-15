package com.qinchen.chat.core.enums;


public enum ReqMsgTypeEnum {

    CHAT_OPEN(100,"讲师打开直播间"),
    CHAT_JOIN(110,"用户加入直播间"),
    USER_SEND_MSG(120,"用户发送消息"),
    LIVE_SEND_MSG(130,"讲师发送消息"),
    CHAT_CLOSE(140,"讲师解散房间"),
    CHAT_QUIT(150,"用户退出房间"),
    LEAVE_LIVE(160,"讲师暂时离开直播间"),
    AGAIN_JOIN_LIVE(170,"讲师再次进入直播间"),
    KEEP_LIVE(1000,"用户保持连接");

    private Integer code;
    private String desc;

    ReqMsgTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据 Code 获取对应枚举类
     *
     * @param code -
     * @return code不存在时为空
     */
    public static ReqMsgTypeEnum getByCode(Integer code){
        if (null == code) {
            return null;
        }
        for (ReqMsgTypeEnum value : ReqMsgTypeEnum.values()) {
            if (value.getCode().equals(code)){
                return value;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
