package com.qinchen.chat.core.service;

import com.alibaba.fastjson.JSON;
import com.qinchen.chat.common.constant.RedisConstant;
import com.qinchen.chat.common.util.*;
import com.qinchen.chat.core.bean.ReportLiveUserDetailBean;
import com.qinchen.chat.core.bean.SocketMessageBean;
import com.qinchen.chat.core.bean.UserJoinMessageBean;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.EventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.transform.Result;
import java.util.*;
import java.util.concurrent.Callable;

@Service
public class UserLiveService {

    private static final Logger logger = LoggerFactory.getLogger(UserLiveService.class);
    @Autowired
    private JedisUtil jedisUtil;
    @Autowired
    private SendMessageService sendMessageService;


    /**
     * 用户加入直播间
     * @param ctx
     * @param msg
     * @param socketMsg
     * @return
     */
    public boolean chatJoin(ChannelHandlerContext ctx, TextWebSocketFrame msg, SocketMessageBean socketMsg, EventExecutorGroup connPool){
        UserJoinMessageBean userJoin = JSON.parseObject(socketMsg.getData(), UserJoinMessageBean.class);
        MyMapPoolUtil.onlineUserMap.put(socketMsg.getTaskId(),MyMapPoolUtil.onlineUserMap.get(socketMsg.getTaskId()) == null ? 1 : MyMapPoolUtil.onlineUserMap.get(socketMsg.getTaskId())+1);
        if(!sendMessageService.isExistChatGroup(socketMsg.getTaskId())){
            logger.info("chatJoin"+JSON.toJSONString(socketMsg));
            if(MyMapPoolUtil.userChannelMap.get(socketMsg.getTaskId()) == null){
                List<Channel> channels = new ArrayList<>();
                channels.add(ctx.channel());
                MyMapPoolUtil.userChannelMap.put(socketMsg.getTaskId(),channels);
            }else{
                MyMapPoolUtil.userChannelMap.get(socketMsg.getTaskId()).add(ctx.channel());
            }
            // 累计用户人数
            if(MyMapPoolUtil.totalUserMap.get(socketMsg.getTaskId()) == null){
                Set set = new HashSet<>();
                set.add(userJoin.getUserId());
                MyMapPoolUtil.totalUserMap.put(socketMsg.getTaskId(),set);
            }else {
                MyMapPoolUtil.totalUserMap.get(socketMsg.getTaskId()).add(userJoin.getUserId());
            }
        }else{
            logger.info("chatJoin2"+JSON.toJSONString(socketMsg));
            // 累计用户人数
            if(MyMapPoolUtil.totalUserMap.get(socketMsg.getTaskId()) == null){
                Set set = new HashSet<>();
                set.add(userJoin.getUserId());
                MyMapPoolUtil.totalUserMap.put(socketMsg.getTaskId(),set);
            }else {
                MyMapPoolUtil.totalUserMap.get(socketMsg.getTaskId()).add(userJoin.getUserId());
            }
            MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId()).add(ctx.channel());
            userJoin.setTotalNum(MyMapPoolUtil.onlineUserMap.get(socketMsg.getTaskId()));
            userJoin.setType(socketMsg.getType());
            //Channel liveChannel = MyMapPoolUtil.liveChannelMap.get(socketMsg.getTaskId());
            // 发送给主播
            /*sendMessageService.sendMessage(MyMapPoolUtil.liveChannelMap.get(socketMsg.getTaskId()),ResultUtil.success(userJoin));
            userJoin.setTotalNum(MyMapPoolUtil.onlineUserMap.get(socketMsg.getTaskId()));
            userJoin.setType(socketMsg.getType());
            ChannelGroup channels = MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId());
            for(Channel userChannel: channels){
                if(userChannel != liveChannel){
                    // 发送给用户
                    sendMessageService.sendMessage(userChannel,ResultUtil.success(userJoin));
                }
            }*/
            // 消息
            sendMessageService.sendGroupMessage(MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId()),ResultUtil.success(userJoin));
        }
        connPool.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                MyMapPoolUtil.channelTaskAndUserMap.put(ctx.channel(),socketMsg.getTaskId()+"_"+userJoin.getUserId());
                Long userId = userJoin.getUserId();
                JedisUtil.Hash mHash = jedisUtil.HASH;
                String key = RedisConstant.getLiveUserKey(socketMsg.getTaskId());
                String userValue = mHash.hget(key, String.valueOf(userId));
                ReportLiveUserDetailBean report = null;
                if(StringUtils.isNotBlank(userValue)){
                    report = JSON.parseObject(userValue, ReportLiveUserDetailBean.class);
                    report.setStartTime(System.currentTimeMillis());
                }else {
                    report = new ReportLiveUserDetailBean();
                    parseLiveUserInfo(report,userJoin,socketMsg.getTaskId(),socketMsg.getShopId(),socketMsg.getLiveId());
                }
                mHash.hset(key,userId + "", JSON.toJSONString(report));
                jedisUtil.expire(key,new Random().nextInt(600)+1000*3600*8);
                return null;
            }
        });
        return true;
    }


    /**
     * 用户离开直播间
     * @param ctx
     * @param msg
     * @param socketMsg
     * @return
     */
    public boolean chatQuit(ChannelHandlerContext ctx, TextWebSocketFrame msg, SocketMessageBean socketMsg, EventExecutorGroup connPool){
        if(sendMessageService.isExistChatGroup(socketMsg.getTaskId())){
            MyMapPoolUtil.onlineUserMap.put(socketMsg.getTaskId(),MyMapPoolUtil.onlineUserMap.get(socketMsg.getTaskId()) == null ? 0 : MyMapPoolUtil.onlineUserMap.get(socketMsg.getTaskId())-1);
            logger.info("chatQuit"+JSON.toJSONString(socketMsg));
            MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId()).remove(ctx.channel());
            UserJoinMessageBean userJoin = JSON.parseObject(socketMsg.getData(), UserJoinMessageBean.class);
            userJoin.setTotalNum(MyMapPoolUtil.onlineUserMap.get(socketMsg.getTaskId()));
            userJoin.setType(socketMsg.getType());
            //Channel liveChannel = MyMapPoolUtil.liveChannelMap.get(socketMsg.getTaskId());
            /*for(Channel userChannel: MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId())){
                if(userChannel != liveChannel){
                    // 发送给用户
                    sendMessageService.sendMessage(userChannel,ResultUtil.success(userJoin));
                }
            }
            // 累计用户人数
            userJoin.setTotalNum(MyMapPoolUtil.onlineUserMap.get(socketMsg.getTaskId()));
            userJoin.setType(socketMsg.getType());
            // 发送给主播
            sendMessageService.sendMessage(MyMapPoolUtil.liveChannelMap.get(socketMsg.getTaskId()),ResultUtil.success(userJoin));*/
            // 群发
            sendMessageService.sendGroupMessage(MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId()), ResultUtil.success(userJoin));
            connPool.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    Long userId = userJoin.getUserId();
                    JedisUtil.Hash mHash = jedisUtil.HASH;
                    String key = RedisConstant.getLiveUserKey(socketMsg.getTaskId());
                    String userValue = mHash.hget(key, String.valueOf(userId));
                    if(StringUtils.isNotBlank(userValue)){
                        ReportLiveUserDetailBean report = JSON.parseObject(userValue, ReportLiveUserDetailBean.class);
                        long nowCurrent = System.currentTimeMillis();
                        report.setTotalViewNum(report.getTotalViewNum() + nowCurrent - report.getStartTime());
                        report.setEndTime(nowCurrent);
                        mHash.hset(key,userId+"",JSON.toJSONString(report));
                        jedisUtil.expire(key,new Random().nextInt(600)+1000*3600*8);
                    }
                    MyMapPoolUtil.channelTaskAndUserMap.remove(ctx.channel());
                    MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId()).remove(ctx.channel());
                    ctx.channel().close();
                    return null;
                }
            });
        }else{
            logger.info("chatQuit2"+JSON.toJSONString(socketMsg));
        }
        return true;
    }

    /**
     * 解析用户信息
     * @param userJoin
     * @param taskId
     * @return
     */
    private ReportLiveUserDetailBean parseLiveUserInfo(ReportLiveUserDetailBean report, UserJoinMessageBean userJoin, Long taskId, Long shopId, Long liveId){
        long time = System.currentTimeMillis();
        report.setTaskId(taskId);
        report.setShopId(shopId);
        report.setLiveId(liveId);
        report.setStartTime(time);
        report.setFirstTime(time);
        report.setTotalViewNum(0L);
        report.setUserId(userJoin.getUserId());
        report.setHeadUrl(userJoin.getHeadUrl());
        report.setNickName(userJoin.getNickName());
        report.setUserId(userJoin.getUserId());
        return report;
    }



}
