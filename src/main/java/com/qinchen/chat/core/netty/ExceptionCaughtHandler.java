package com.qinchen.chat.core.netty;

import com.alibaba.fastjson.JSON;
import com.qinchen.chat.common.constant.RedisConstant;
import com.qinchen.chat.common.util.AppContextUtil;
import com.qinchen.chat.common.util.JedisUtil;
import com.qinchen.chat.common.util.MyMapPoolUtil;
import com.qinchen.chat.common.util.StringUtils;
import com.qinchen.chat.core.bean.ReportLiveUserDetailBean;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.Callable;

public class ExceptionCaughtHandler extends ChannelInboundHandlerAdapter {

    private static Logger log = LoggerFactory.getLogger(ExceptionCaughtHandler.class);

    /**
     * 发生异常
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        try {
            if(cause instanceof Exception){
                // 关闭通道
                String taskAndUser = MyMapPoolUtil.channelTaskAndUserMap.get(ctx.channel());
                if(StringUtils.isNotBlank(taskAndUser)){
                    String[] split = taskAndUser.split("_");
                    if(split.length == 2){
                        MyNioWebSocketHandler.connPool.submit(new Callable<Object>() {
                            @Override
                            public Object call() throws Exception {
                                String userId = split[1];
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
                    MyMapPoolUtil.channelTaskAndUserMap.remove(ctx.channel());
                }
                ctx.channel().close();
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("发生异常_"+e.getMessage(),e);
        }
    }


}
