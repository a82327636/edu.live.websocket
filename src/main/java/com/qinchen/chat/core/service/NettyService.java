package com.qinchen.chat.core.service;

import com.alibaba.fastjson.JSON;
import com.qinchen.chat.core.bean.SocketMessageBean;
import com.qinchen.chat.core.enums.ReqMsgTypeEnum;
import com.qinchen.chat.core.netty.MyNioWebSocketHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.EventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NettyService {

    private static Logger logger = LoggerFactory.getLogger(MyNioWebSocketHandler.class);

    @Autowired
    private SellerLiveService sellerLiveService;
    @Autowired
    private UserLiveService userLiveService;
    @Autowired
    private SendMessageService sendMessageService;

    public void sendMessage(ChannelHandlerContext ctx, TextWebSocketFrame msg, EventExecutorGroup connPool){
        try {
            SocketMessageBean socketMsg = JSON.parseObject(msg.text(), SocketMessageBean.class);
            if(ReqMsgTypeEnum.CHAT_OPEN.getCode() == socketMsg.getType()){
                // 讲师开直播间
                sellerLiveService.chatOpen(ctx,msg,socketMsg);
            }else if(ReqMsgTypeEnum.CHAT_JOIN.getCode() == socketMsg.getType()){
                // 加入直播间
                userLiveService.chatJoin(ctx,msg,socketMsg,connPool);
            }else if(ReqMsgTypeEnum.USER_SEND_MSG.getCode() == socketMsg.getType()){
                // 用户发送消息
                sendMessageService.sendMsg(ctx,msg,socketMsg,connPool);
            }else if(ReqMsgTypeEnum.LIVE_SEND_MSG.getCode()== socketMsg.getType()){
                // 讲师发送消息
                sendMessageService.sendMsg(ctx,msg,socketMsg,connPool);
            }else if(ReqMsgTypeEnum.CHAT_QUIT.getCode() == socketMsg.getType()){
                // 用户退出直播间
                userLiveService.chatQuit(ctx,msg,socketMsg,connPool);
            }else if(ReqMsgTypeEnum.CHAT_CLOSE.getCode() == socketMsg.getType()){
                // 主播关闭直播间
                sellerLiveService.chatClose(ctx,msg,socketMsg);
            }else if(ReqMsgTypeEnum.LEAVE_LIVE.getCode() == socketMsg.getType()){
                // 讲师暂时离开直播间
                sellerLiveService.leaveLive(ctx,msg,socketMsg);
            }else if(ReqMsgTypeEnum.AGAIN_JOIN_LIVE.getCode() == socketMsg.getType()){
                // 讲师再次进入直播间
                sellerLiveService.againJoinLive(ctx,msg,socketMsg);
            }
        }catch (Exception e){
            logger.error("webSocket交互_"+e.getMessage(),e);
        }
    }


}
