package com.qinchen.chat.core.netty;

import com.alibaba.fastjson.JSON;
import com.qinchen.chat.common.constant.RedisConstant;
import com.qinchen.chat.common.util.AppContextUtil;
import com.qinchen.chat.common.util.JedisUtil;
import com.qinchen.chat.common.util.StringUtils;
import com.qinchen.chat.core.bean.ReportLiveUserDetailBean;
import com.qinchen.chat.common.util.MyMapPoolUtil;
import com.qinchen.chat.core.service.NettyService;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import java.util.Random;
import java.util.concurrent.Callable;

public class MyNioWebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

	private static Logger log = LoggerFactory.getLogger(MyNioWebSocketHandler.class);

    public static EventExecutorGroup connPool =  new DefaultEventExecutorGroup(16);

    /**
     * 接收发送消息
     * @param ctx
     * @param msg
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        try {
            NettyService nettyService = (NettyService) AppContextUtil.getBean("nettyService");
            nettyService.sendMessage(ctx,msg,connPool);
        }catch (Exception e){
            log.error("webSocket交互_"+e.getMessage(),e);
        }
    }


    /**
     * 心跳检测功能
     * @param ctx
     * @param evt
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        try {
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
                if (idleStateEvent.state() == IdleState.ALL_IDLE) {
                    // 关闭通道
                    log.info("channel:" +ctx.channel().id().asShortText());
                    String taskAndUser = MyMapPoolUtil.channelTaskAndUserMap.get(ctx.channel().id().asShortText());
                    log.info("长时间没有发送心跳，清理账号:" +taskAndUser);
                    if(StringUtils.isNotBlank(taskAndUser)){
                        String[] split = taskAndUser.split("_");
                        if(split.length == 2){
                            connPool.submit(new Callable<Object>() {
                                @Override
                                public Object call() throws Exception {
                                    String userId = split[1];
                                    // 减掉在线人数
                                    if(MyMapPoolUtil.onlineUserSetMap.get(Long.valueOf(split[0])).contains(Long.valueOf(userId))){
                                        MyMapPoolUtil.onlineUserSetMap.get(Long.valueOf(split[0])).remove(Long.valueOf(userId));
                                    }
                                    JedisUtil jedisUtil = (JedisUtil) AppContextUtil.getBean("jedisUtil");
                                    JedisUtil.Hash mHash = jedisUtil.HASH;
                                    String key = RedisConstant.getLiveUserKey(Long.valueOf(split[0]));
                                    String userValue = mHash.hget(key, String.valueOf(userId));
                                    if(StringUtils.isNotBlank(userValue)){
                                        ReportLiveUserDetailBean report = JSON.parseObject(userValue, ReportLiveUserDetailBean.class);
                                        long nowCurrent = System.currentTimeMillis();
                                        report.setEndTime(nowCurrent);
                                        report.setTotalViewNum(report.getTotalViewNum() + nowCurrent - report.getStartTime());
                                        mHash.hset(key,userId+"",JSON.toJSONString(report));
                                        jedisUtil.expire(key,new Random().nextInt(600)+1000*3600*24);
                                    }
                                    MyMapPoolUtil.chatGroupMap.get(Long.valueOf(split[0])).remove(ctx.channel());
                                    return 1;
                                }
                            });
                        }
                        MyMapPoolUtil.channelTaskAndUserMap.remove(ctx.channel().id().asShortText());
                    }
                    ctx.channel().close();
                }
            }
        }catch (Exception e){
            log.error("心跳检测_"+e.getMessage(),e);
        }
    }






}