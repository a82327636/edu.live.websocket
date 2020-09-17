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
    public Integer chatOpen(ChannelHandlerContext ctx, TextWebSocketFrame msg, SocketMessageBean socketMsg) {
        logger.info("chatOpen111111");
        if(!sendMessageService.isExistChatGroup(socketMsg.getTaskId())){
            logger.info("chatOpen222222");
            MyMapPoolUtil.chatGroupMap.put(socketMsg.getTaskId(),new DefaultChannelGroup(GlobalEventExecutor.INSTANCE));
            MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId()).add(ctx.channel());
            MyMapPoolUtil.liveChannelMap.put(socketMsg.getTaskId(),ctx.channel());
            UserJoinMessageBean sendMessage = JSON.parseObject(socketMsg.getData(), UserJoinMessageBean.class);
            if(MyMapPoolUtil.userChannelMap.get(socketMsg.getTaskId()) != null){
                MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId()).addAll(MyMapPoolUtil.userChannelMap.get(socketMsg.getTaskId()));
                /*List<Channel> channels = MyMapPoolUtil.userChannelMap.get(socketMsg.getTaskId());
                sendMessage.setTotalNum(MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId()).size());
                for(Channel channel:channels){
                    sendMessageService.sendMessage(channel, ResultUtil.success(sendMessage));
                }*/
                MyMapPoolUtil.userChannelMap.remove(socketMsg.getTaskId());
            }
            sendMessage.setType(socketMsg.getType());
            sendMessage.setTotalNum(MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId()).size());
            sendMessageService.sendGroupMessage(MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId()),ResultUtil.success(sendMessage));
        }else{
            logger.info("chatOpen333333");
            UserJoinMessageBean sendMessage = JSON.parseObject(socketMsg.getData(), UserJoinMessageBean.class);
            sendMessage.setType(socketMsg.getType());
            if(!MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId()).contains(MyMapPoolUtil.liveChannelMap.get(socketMsg.getTaskId()))){
                sendMessage.setTotalNum(MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId()).size());
                MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId()).add(ctx.channel());
                MyMapPoolUtil.liveChannelMap.put(socketMsg.getTaskId(),ctx.channel());
            }
            sendMessageService.sendGroupMessage(MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId()),ResultUtil.success(sendMessage));
        }
        logger.info("chatOpen44444");
        return null;
    }


    /**
     * 主播关闭直播间
     * @param ctx
     * @param msg
     * @param socketMsg
     * @return
     */
    public boolean chatClose(ChannelHandlerContext ctx, TextWebSocketFrame msg, SocketMessageBean socketMsg){
        logger.info("chatClose111111");
        if(sendMessageService.isExistChatGroup(socketMsg.getTaskId())){
            logger.info("chatClose222222");
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
            },30, TimeUnit.SECONDS);
        }
        logger.info("chatClose333333");
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
        logger.info("leaveLive111111");
        if(sendMessageService.isExistChatGroup(socketMsg.getTaskId())){
            logger.info("leaveLive222222");
            SendMessageBean sendMessage = JSON.parseObject(socketMsg.getData(), SendMessageBean.class);
            sendMessage.setType(socketMsg.getType());
            /*ChannelGroup channels = MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId());
            Channel liveChannel = MyMapPoolUtil.liveChannelMap.get(socketMsg.getTaskId());
            for(Channel userChannel: channels){
                if(userChannel != liveChannel){
                    // 发送讲师暂时离开消息给用户
                    sendMessageService.sendMessage(userChannel,ResultUtil.success(sendMessage));
                }
            }*/
            sendMessageService.sendGroupMessage(MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId()),ResultUtil.success(sendMessage));
        }
        logger.info("leaveLive333333");
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
        logger.info("againJoinLive111111");
        if(sendMessageService.isExistChatGroup(socketMsg.getTaskId())){
            logger.info("againJoinLive222222");
            if(!MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId()).contains(MyMapPoolUtil.liveChannelMap.get(socketMsg.getTaskId()))){
                MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId()).add(ctx.channel());
                MyMapPoolUtil.liveChannelMap.put(socketMsg.getTaskId(),ctx.channel());
            }
            UserJoinMessageBean sendMessage = JSON.parseObject(socketMsg.getData(), UserJoinMessageBean.class);
            sendMessage.setType(socketMsg.getType());
            sendMessage.setTotalNum(MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId()).size());
            /*ChannelGroup channels = MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId());
            Channel liveChannel = MyMapPoolUtil.liveChannelMap.get(socketMsg.getTaskId());
            for(Channel userChannel: channels){
                if(userChannel != liveChannel){
                    // 发送讲师再次进入消息给用户
                    sendMessageService.sendMessage(userChannel,ResultUtil.success(sendMessage));
                }
            }*/
            sendMessageService.sendGroupMessage(MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId()),ResultUtil.success(sendMessage));
        }
        logger.info("againJoinLive333333");
        return true;
    }

}
