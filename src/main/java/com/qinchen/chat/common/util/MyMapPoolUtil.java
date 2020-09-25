package com.qinchen.chat.common.util;

import com.qinchen.chat.core.vo.LiveChatLogVo;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class MyMapPoolUtil {

    /**
     * 直播间主播
     */
    public static ConcurrentMap<Long, Channel> liveChannelMap = new ConcurrentHashMap();
    /**
     * 还未开播之前用户已经进来，先保存channel
     */
    public static ConcurrentMap<Long, List<Channel>> userChannelMap = new ConcurrentHashMap();

    /**
     * 用来存储直播间群
     */
    public static ConcurrentMap<Long, ChannelGroup> chatGroupMap = new ConcurrentHashMap();

    /**
     * 用来存储累计某直播间用户人数
     */
    public static ConcurrentMap<Long, Set> totalUserMap = new ConcurrentHashMap();
    /**
     * 保存用户实时在线任务set
     */
    public static ConcurrentMap<Long, Set> onlineUserSetMap = new ConcurrentHashMap();

    /**
     * 用来存储各个直播间总互动次数
     */
    public static ConcurrentMap<Long, Integer> totalChatMap = new ConcurrentHashMap();

    /**
     * 保存用户聊天内容
     */
    public static ConcurrentMap<Long, List<LiveChatLogVo>> totalContentMap = new ConcurrentHashMap();

    /**
     * 保存用户聊天数量
     */
    public static ConcurrentMap<Long, Map<Long,Integer>> totalContentNumMap = new ConcurrentHashMap();

    /**
     * 保存任务和用户map
     */
    public static ConcurrentMap<String, String> channelTaskAndUserMap = new ConcurrentHashMap();




}
