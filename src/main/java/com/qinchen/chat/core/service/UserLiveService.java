package com.qinchen.chat.core.service;

import com.alibaba.fastjson.JSON;
import com.qinchen.chat.common.constant.RedisConstant;
import com.qinchen.chat.common.util.*;
import com.qinchen.chat.core.bean.ReportLiveUserDetailBean;
import com.qinchen.chat.core.bean.SocketMessageBean;
import com.qinchen.chat.core.bean.UserJoinMessageBean;
import com.qinchen.chat.core.enums.RespMsgTypeEnum;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.EventExecutorGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.Callable;

@Service
public class UserLiveService {

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
        if(!sendMessageService.isExistChatGroup(socketMsg.getTaskId())){
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
            // 累计用户人数
            if(MyMapPoolUtil.totalUserMap.get(socketMsg.getTaskId()) == null){
                Set set = new HashSet<>();
                set.add(userJoin.getUserId());
                MyMapPoolUtil.totalUserMap.put(socketMsg.getTaskId(),set);
            }else {
                MyMapPoolUtil.totalUserMap.get(socketMsg.getTaskId()).add(userJoin.getUserId());
            }
            MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId()).add(ctx.channel());
            Channel liveChannel = MyMapPoolUtil.liveChannelMap.get(socketMsg.getTaskId());
            userJoin.setTotalNum(MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId()).size());
            userJoin.setType(socketMsg.getType());
            // 发送给主播
            sendMessageService.sendMessage(MyMapPoolUtil.liveChannelMap.get(socketMsg.getTaskId()),ResultUtil.success(userJoin));
            userJoin.setTotalNum(MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId()).size());
            userJoin.setType(socketMsg.getType());
            ChannelGroup channels = MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId());
            for(Channel userChannel: channels){
                if(userChannel != liveChannel){
                    // 发送给用户
                    sendMessageService.sendMessage(userChannel,ResultUtil.success(userJoin));
                }
            }
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
                jedisUtil.expire(key,new Random().nextInt(600)+1000*3600*12);
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
            MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId()).remove(ctx.channel());
            Channel liveChannel = MyMapPoolUtil.liveChannelMap.get(socketMsg.getTaskId());
            UserJoinMessageBean userJoin = JSON.parseObject(socketMsg.getData(), UserJoinMessageBean.class);
            userJoin.setTotalNum(MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId()).size());
            userJoin.setType(socketMsg.getType());
            for(Channel userChannel: MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId())){
                if(userChannel != liveChannel){
                    // 发送给用户
                    sendMessageService.sendMessage(userChannel,ResultUtil.success(userJoin));
                }
            }
            // 累计用户人数
            userJoin.setTotalNum(MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId()).size());
            userJoin.setType(socketMsg.getType());
            // 发送给主播
            sendMessageService.sendMessage(MyMapPoolUtil.liveChannelMap.get(socketMsg.getTaskId()),ResultUtil.success(userJoin));
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
                        jedisUtil.expire(key,new Random().nextInt(600)+1000*3600*24);
                    }
                    MyMapPoolUtil.channelTaskAndUserMap.remove(ctx.channel());
                    MyMapPoolUtil.chatGroupMap.get(socketMsg.getTaskId()).remove(ctx.channel());
                    ctx.channel().close();
                    return null;
                }
            });
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

    public void testJoin(){
        // 放入redis中 用户观看信息
                /*Long userId = userJoin.getUserId();
                JedisUtil jedisUtil = (JedisUtil) AppContextUtil.getBean("jedisUtil");
                JedisUtil.Hash mHash = jedisUtil.HASH;
                String key = RedisConstant.getLiveUserKey(socketMsg.getTaskId());
                Map<String, String> hgetAll = jedisUtil.HASH.hgetAll(key);
                ReportLiveUserDetailBean report = null;
                Map<String, String> userMap = new HashMap<>();
                if(hgetAll != null){
                    if(hgetAll.get(userId + "") != null){
                        report = JSON.parseObject(hgetAll.get(userId + ""), ReportLiveUserDetailBean.class);
                        report.setStartTime(System.currentTimeMillis());
                    }
                }else{
                    report = new ReportLiveUserDetailBean();
                    parseLiveUserInfo(report,userJoin,socketMsg.getTaskId());
                }
                userMap.put(userId + "",JSON.toJSONString(report));
                mHash.hmset(key,userMap);*/
    }


}
