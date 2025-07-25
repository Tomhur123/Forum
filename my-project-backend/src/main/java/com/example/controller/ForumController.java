package com.example.controller;

import com.alibaba.fastjson2.JSONObject;
import com.example.entity.RestBean;
import com.example.entity.dto.Account;
import com.example.entity.dto.Interact;
import com.example.entity.vo.request.AddCommentVO;
import com.example.entity.vo.request.TopicCreateVO;
import com.example.entity.vo.request.TopicUpdateVO;
import com.example.entity.vo.response.*;
import com.example.service.AccountService;
import com.example.service.TopicService;
import com.example.service.WeatherService;
import com.example.utils.Const;
import com.example.utils.ControllerUtils;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/forum")
public class ForumController {

    @Resource
    WeatherService service;

    @Resource
    TopicService topicService;

    @Resource
    AccountService accountService;

    @Resource
    ControllerUtils utils;

    @GetMapping("/weather")
    public RestBean<WeatherVO> weather(double longitude, double latitude){
        WeatherVO vo = service.fetchWeather(longitude, latitude);
        return vo == null ?
                RestBean.failure(400, "获取地理位置信息与天气失败，请联系管理员！") : RestBean.success(vo);
    }

    @GetMapping("/types")
    public RestBean<List<TopicTypeVO>> listTypes(){
        return RestBean.success(topicService
                .listTypes()
                .stream()
                .map(type -> type.asViewObject(TopicTypeVO.class))
                .toList());
    }

    @PostMapping("/create-topic")
    public  RestBean<Void> createTopic(@Valid @RequestBody TopicCreateVO vo,
                                       @RequestAttribute(Const.ATTR_USER_ID) int id){
        Account account = accountService.findAccountById(id);
        if(account.isMute()) {
            return RestBean.forbidden("此账号已被禁止发表帖子！");
        }
        return utils.messageHandle(() -> topicService.createTopic(id, vo));
    }

    @GetMapping("/list-topic")
    public RestBean<List<TopicPreviewVO>> listTopic(@RequestParam @Min(0) int page,
                                                    @RequestParam @Min(0) int type){
        return RestBean.success(topicService.listTopicByPage(page +1, type));
    }

    @GetMapping("/top-topic")
    public RestBean<List<TopicTopVO>> topTopic(){
        return RestBean.success(topicService.listTopTopics());
    }

    @GetMapping("/topic")
    public RestBean<TopicDetailVO> topic(@RequestParam @Min(0) int tid,
                                         @RequestAttribute(Const.ATTR_USER_ID) int uid){
        return RestBean.success(topicService.getTopic(tid, uid));
    }

    @GetMapping("/interact")
    public RestBean<Void> interact(@RequestParam @Min(0) int tid,
                                   @RequestParam @Pattern(regexp = "(like|collect)") String type,
                                   @RequestParam boolean state,
                                   @RequestAttribute(Const.ATTR_USER_ID) int uid){
        topicService.interact(new Interact(tid, uid, new Date(), type), state);
        return RestBean.success();
    }

    @GetMapping("/collects")
    public RestBean<List<TopicPreviewVO>> collects(@RequestAttribute(Const.ATTR_USER_ID) int uid){
        return RestBean.success(topicService.listTopicCollects(uid));
    }

    @PostMapping("/update-topic")
    public RestBean<Void> updateTopic(@Valid @RequestBody TopicUpdateVO vo,
                                      @RequestAttribute(Const.ATTR_USER_ID) int uid) {
        return utils.messageHandle(() -> topicService.updateTopic(uid, vo));
    }

    @PostMapping("/add-comment")
    public RestBean<Void> addComment(@Valid @RequestBody AddCommentVO vo,
                                     @RequestAttribute(Const.ATTR_USER_ID) int uid) {
        Account account = accountService.findAccountById(uid);
        if(account.isMute()) {
            return RestBean.forbidden("此账号已被禁止发表评论！");
        }
        return utils.messageHandle(() -> topicService.createComment(uid, vo));
    }

    @GetMapping("/comments")
    public RestBean<List<CommentVO>> comments(@RequestParam @Min(0) int tid,
                                              @RequestParam @Min(0) int page){
        return RestBean.success(topicService.comments(tid, page+1));
    }

    @GetMapping("/delete-comment")
    public RestBean<Void> deleteComment(@RequestParam @Min(0) int id,
                                        @RequestAttribute(Const.ATTR_USER_ID) int uid){
        topicService.deleteComment(id, uid);
        return RestBean.success();
    }
}
