package com.qinchen.chat.core.service;

import com.alibaba.fastjson.JSON;
import com.qinchen.chat.common.util.ResultUtil;
import com.qinchen.chat.core.bean.SendMessageBean;
import com.qinchen.chat.core.bean.SocketMessageBean;
import com.qinchen.chat.common.util.MyMapPoolUtil;
import com.qinchen.chat.core.bean.UserJoinMessageBean;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class SellerLiveService {

    private static final Logger logger = LoggerFactory.getLogger(SellerLiveService.class);

    @Autowired
    private SendMessageService sendMessageService;

    /**
     * 主播开直播
     * @param ctx
     * @param msg
     * @param socketMsg
     * @return
     */
    public boolean chatOpen(ChannelHandlerContext ctx, TextWebSocketFrame msg, SocketMessageBean socketMsg) {
        if(!sendMessageService.isExistChatGroup(socketMsg.getTaskId())){
            logger.info("chatOpen"+JSON.toJSONString(socketMsg));
            MyMapPoolUtil.chatGroupMap.put(socketMsg.getTaskId(),new DefaultChannelGroup(GlobalEventExecutor.INSTANCE));
            MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId()).add(ctx.channel());
            MyMapPoolUtil.liveChannelMap.put(socketMsg.getTaskId(),ctx.channel());
            UserJoinMessageBean sendMessage = JSON.parseObject(socketMsg.getData(), UserJoinMessageBean.class);
            if(MyMapPoolUtil.userChannelMap.get(socketMsg.getTaskId()) != null){
                MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId()).addAll(MyMapPoolUtil.userChannelMap.get(socketMsg.getTaskId()));
                MyMapPoolUtil.userChannelMap.remove(socketMsg.getTaskId());
            }
            sendMessage.setType(socketMsg.getType());
            sendMessage.setTotalNum(MyMapPoolUtil.onlineUserSetMap.get(socketMsg.getTaskId()) == null ? 0 : MyMapPoolUtil.onlineUserSetMap.get(socketMsg.getTaskId()).size());
            sendMessageService.sendGroupMessage(MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId()),ResultUtil.success(sendMessage));
        }else{
            logger.info("chatOpen2"+JSON.toJSONString(socketMsg));
            UserJoinMessageBean sendMessage = JSON.parseObject(socketMsg.getData(), UserJoinMessageBean.class);
            sendMessage.setType(socketMsg.getType());
            sendMessage.setTotalNum(0);
            MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId()).add(ctx.channel());
            MyMapPoolUtil.liveChannelMap.put(socketMsg.getTaskId(),ctx.channel());
            sendMessageService.sendGroupMessage(MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId()),ResultUtil.success(sendMessage));
        }
        return true;
    }


    /**
     * 主播关闭直播间
     * @param ctx
     * @param msg
     * @param socketMsg
     * @return
     */
    public boolean chatClose(ChannelHandlerContext ctx, TextWebSocketFrame msg, SocketMessageBean socketMsg){
        if(sendMessageService.isExistChatGroup(socketMsg.getTaskId())){
            logger.info("chatClose"+JSON.toJSONString(socketMsg));
            SendMessageBean sendMessage = JSON.parseObject(socketMsg.getData(), SendMessageBean.class);
            sendMessage.setType(socketMsg.getType());
            sendMessageService.sendGroupMessage(MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId()),ResultUtil.success(sendMessage));
            ctx.channel().eventLoop().schedule(new Runnable() {
                @Override
                public void run() {
                    MyMapPoolUtil.liveChannelMap.remove(socketMsg.getTaskId());
                    MyMapPoolUtil.chatGroupMap.remove(socketMsg.getTaskId());
                    //MyMapPoolUtil.channelTaskAndUserMap.remove(ctx.channel());
                }
            },15, TimeUnit.SECONDS);
        }else {
            logger.info("chatClose2"+JSON.toJSONString(socketMsg));
        }
        return true;
    }

    /**
     * 讲师暂时离开直播间
     * @param ctx
     * @param msg
     * @param socketMsg
     * @return
     */
    public boolean leaveLive(ChannelHandlerContext ctx, TextWebSocketFrame msg, SocketMessageBean socketMsg){
        if(sendMessageService.isExistChatGroup(socketMsg.getTaskId())){
            logger.info("leaveLive"+JSON.toJSONString(socketMsg));
            SendMessageBean sendMessage = JSON.parseObject(socketMsg.getData(), SendMessageBean.class);
            sendMessage.setType(socketMsg.getType());
            sendMessageService.sendMessage(MyMapPoolUtil.liveChannelMap.get(socketMsg.getTaskId()),ResultUtil.success(sendMessage));
            MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId()).remove(ctx.channel());
            MyMapPoolUtil.liveChannelMap.remove(socketMsg.getTaskId());
            ctx.channel().close();
            //sendMessageService.sendGroupMessage(MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId()),ResultUtil.success(sendMessage));
        }else{
            logger.info("leaveLive2"+JSON.toJSONString(socketMsg));
        }
        return true;
    }

    /**
     * 讲师再次进入直播间
     * @param ctx
     * @param msg
     * @param socketMsg
     * @return
     */
    public boolean againJoinLive(ChannelHandlerContext ctx, TextWebSocketFrame msg, SocketMessageBean socketMsg){
        if(sendMessageService.isExistChatGroup(socketMsg.getTaskId())){
            logger.info("againJoinLive"+JSON.toJSONString(socketMsg));
            MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId()).add(ctx.channel());
            MyMapPoolUtil.liveChannelMap.put(socketMsg.getTaskId(),ctx.channel());
            UserJoinMessageBean sendMessage = JSON.parseObject(socketMsg.getData(), UserJoinMessageBean.class);
            sendMessage.setType(socketMsg.getType());
            sendMessage.setTotalNum(MyMapPoolUtil.onlineUserSetMap.get(socketMsg.getTaskId()) == null ? 0 : MyMapPoolUtil.onlineUserSetMap.get(socketMsg.getTaskId()).size());
            sendMessageService.sendMessage(MyMapPoolUtil.liveChannelMap.get(socketMsg.getTaskId()),ResultUtil.success(sendMessage));
        }else {
            logger.info("againJoinLive2"+JSON.toJSONString(socketMsg));
            MyMapPoolUtil.chatGroupMap.put(socketMsg.getTaskId(),new DefaultChannelGroup(GlobalEventExecutor.INSTANCE));
            MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId()).add(ctx.channel());
            MyMapPoolUtil.liveChannelMap.put(socketMsg.getTaskId(),ctx.channel());
            UserJoinMessageBean sendMessage = JSON.parseObject(socketMsg.getData(), UserJoinMessageBean.class);
            sendMessage.setType(socketMsg.getType());
            sendMessage.setTotalNum(MyMapPoolUtil.onlineUserSetMap.get(socketMsg.getTaskId()) == null ? 0 : MyMapPoolUtil.onlineUserSetMap.get(socketMsg.getTaskId()).size());
            sendMessageService.sendMessage(MyMapPoolUtil.liveChannelMap.get(socketMsg.getTaskId()),ResultUtil.success(sendMessage));
        }
        //sendMessageService.sendGroupMessage(MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId()),ResultUtil.success(sendMessage));
        return true;
    }

}
