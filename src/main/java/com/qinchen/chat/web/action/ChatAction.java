package com.qinchen.chat.web.action;

import com.qinchen.chat.common.util.ResultUtil;
import com.qinchen.chat.core.vo.LiveChatLogVo;
import com.qinchen.chat.common.util.MyMapPoolUtil;
import com.qinchen.chat.core.vo.OnlineUserVo;
import com.qinchen.chat.core.vo.ReportLiveVo;
import io.netty.channel.group.ChannelGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

@RestController
@RequestMapping("/webSocket/")
public class ChatAction {

    private static final Logger logger = LoggerFactory.getLogger(ChatAction.class);

    /**
     * 查询聊天内容
     * @param taskId
     * @return
     */
    @RequestMapping("getChatContentInfo")
    public ResultUtil getChatContentInfo(Long taskId){
        try {
            List<LiveChatLogVo> list = MyMapPoolUtil.totalContentMap.get(taskId);
            if(list != null && list.size() > 0){
                MyMapPoolUtil.totalContentMap.remove(taskId);
                return ResultUtil.success(list);
            }
           return ResultUtil.success(new ArrayList<>());
        }catch (Exception e){
            logger.error("seller getChatContentInfo error"+e.getMessage());
            return ResultUtil.error(0,e.getMessage());
        }
    }

    /**
     * 实时在线人数
     * @return
     */
    @RequestMapping("getOnlineUserInfo")
    public ResultUtil getOnlineUserInfo(Long taskId){
        try {
            ChannelGroup onlineUser = MyMapPoolUtil.chatGroupMap.get(taskId);
            OnlineUserVo vo = new OnlineUserVo();
            vo.setTaskId(taskId);
            if(onlineUser != null){
                vo.setTotalUserNum(onlineUser.size());
            }
            return ResultUtil.success(vo);
        }catch (Exception e){
            logger.error(" getOnlineUserInfo error"+e.getMessage());
            return ResultUtil.error(0,e.getMessage());
        }
    }


    /**
     * 查询某个任务的总用户数
     * @param taskId
     * @return
     */
    @RequestMapping("getTotalUserNum")
    public ResultUtil getTotalUserNum(Long taskId){
        try {
            Set set = MyMapPoolUtil.totalUserMap.get(taskId);
            ReportLiveVo vo = new ReportLiveVo();
            if(set != null && set.size() > 0){
                //MyMapPoolUtil.totalUserMap.remove(taskId);
                Integer totalChatNum = MyMapPoolUtil.totalChatMap.get(taskId);
                vo.setTotalUserNum(set.size());
                vo.setTotalChatNum(totalChatNum);
                //MyMapPoolUtil.totalChatMap.remove(taskId);
            }
            return ResultUtil.success(vo);
        }catch (Exception e){
            logger.error("seller getTotalUserNum error"+e.getMessage());
            return ResultUtil.error(0,e.getMessage());
        }
    }


    /**
     * 查询用户互动次数
     * @param taskId
     * @return
     */
    @RequestMapping("getTotalChatNum")
    public ResultUtil getTotalChatNum(Long taskId){
        try {
            Map<Long, Integer> totalChat = MyMapPoolUtil.totalContentNumMap.get(taskId);
            if(totalChat != null){
                //MyMapPoolUtil.totalContentNumMap.remove(taskId);
                return ResultUtil.success(totalChat);
            }
            return ResultUtil.success(new HashMap<Long,Integer>());

        }catch (Exception e){
            logger.error("seller getTotalChatNum error"+e.getMessage());
            return ResultUtil.error(0,e.getMessage());
        }
    }



}
