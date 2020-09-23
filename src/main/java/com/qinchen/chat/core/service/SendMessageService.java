package com.qinchen.chat.core.service;

import com.alibaba.fastjson.JSON;
import com.qinchen.chat.common.util.ResultUtil;
import com.qinchen.chat.core.bean.SendMessageBean;
import com.qinchen.chat.core.enums.ReqMsgTypeEnum;
import com.qinchen.chat.core.vo.LiveChatLogVo;
import com.qinchen.chat.core.bean.SocketMessageBean;
import com.qinchen.chat.core.bean.UserJoinMessageBean;
import com.qinchen.chat.common.util.MyMapPoolUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.EventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.Callable;

@Service
public class SendMessageService {
    private static final Logger logger = LoggerFactory.getLogger(SendMessageService.class);
    /**
     * 用户/主播发消息
     * @param ctx
     * @param msg
     * @param socketMsg
     * @return
     */
    public boolean sendMsg(ChannelHandlerContext ctx, TextWebSocketFrame msg, SocketMessageBean socketMsg, EventExecutorGroup connPool){
        if(isExistChatGroup(socketMsg.getTaskId())){
            logger.info("sendMsg"+JSON.toJSONString(socketMsg));
            SendMessageBean sendMessage = JSON.parseObject(socketMsg.getData(), SendMessageBean.class);
            sendMessage.setType(socketMsg.getType());
            // 给所有人发送消息
            sendGroupMessage(MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId()), ResultUtil.success(sendMessage));
            connPool.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    // 某直播间总互动次数
                    if(MyMapPoolUtil.totalChatMap.get(socketMsg.getTaskId()) == null){
                        MyMapPoolUtil.totalChatMap.put(socketMsg.getTaskId(),1);
                    }else {
                        MyMapPoolUtil.totalChatMap.put(socketMsg.getTaskId(), MyMapPoolUtil.totalChatMap.get(socketMsg.getTaskId())+1);
                    }
                    UserJoinMessageBean userJoin = JSON.parseObject(socketMsg.getData(), UserJoinMessageBean.class);
                    // 用户在某个直播间互动次数
                    if(MyMapPoolUtil.totalContentNumMap.get(socketMsg.getTaskId()) == null){
                        Map<Long,Integer> map = new HashMap<>();
                        map.put(userJoin.getUserId(),1);
                        MyMapPoolUtil.totalContentNumMap.put(socketMsg.getTaskId(),map);
                    }else{
                        Map<Long, Integer> map = MyMapPoolUtil.totalContentNumMap.get(socketMsg.getTaskId());
                        map.put(userJoin.getUserId(),MyMapPoolUtil.totalContentNumMap.get(socketMsg.getTaskId()).get(userJoin.getUserId()) == null ? 1 : MyMapPoolUtil.totalContentNumMap.get(socketMsg.getTaskId()).get(userJoin.getUserId()) + 1);
                        MyMapPoolUtil.totalContentNumMap.put(socketMsg.getTaskId(),map);
                    }
                    // 保存聊天内容
                    if(MyMapPoolUtil.totalContentMap.get(socketMsg.getTaskId()) == null){
                        List<LiveChatLogVo> list = new ArrayList<>(128);
                        LiveChatLogVo liveLog = parseLiveChatLog(socketMsg);
                        list.add(liveLog);
                        MyMapPoolUtil.totalContentMap.put(socketMsg.getTaskId(),list);
                    }else{
                        MyMapPoolUtil.totalContentMap.get(socketMsg.getTaskId()).add(parseLiveChatLog(socketMsg));
                    }
                    return null;
                }
            });
        }else{
            logger.info("sendMsg2"+JSON.toJSONString(socketMsg));
        }
        return true;
    }


    /**
     * 发送群消息
     * @param channelGroup
     * @param message
     */
    public void sendGroupMessage(ChannelGroup channelGroup, ResultUtil message) {
        channelGroup.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(message)));
    }

    /**
     * 单个发送消息
     * @param channel
     * @param message
     */
    public void sendMessage(Channel channel, ResultUtil message) {
        channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(message)));
    }


    /**
     * 判断直播间是否已经存在
     * @param taskId
     * @return
     */
    public boolean isExistChatGroup(Long taskId){
        return MyMapPoolUtil.chatGroupMap.get(taskId) == null ? false : true;
    }

    /**
     * 构建弹幕信息
     * @param socketMsg
     */
    private static LiveChatLogVo parseLiveChatLog(SocketMessageBean socketMsg) {
        LiveChatLogVo liveLog = JSON.parseObject(socketMsg.getData(), LiveChatLogVo.class);
        liveLog.setLiveId(socketMsg.getLiveId());
        liveLog.setTaskId(socketMsg.getTaskId());
        if(socketMsg.getType() == ReqMsgTypeEnum.USER_SEND_MSG.getCode()){
            liveLog.setType(2);
        }else if(socketMsg.getType() == ReqMsgTypeEnum.LIVE_SEND_MSG.getCode()){
            liveLog.setType(1);
        }
        liveLog.setInputTime(new Date());
        return liveLog;
    }




}
