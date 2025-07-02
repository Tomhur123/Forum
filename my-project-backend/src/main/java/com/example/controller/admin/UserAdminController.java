package com.example.controller.admin;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.entity.RestBean;
import com.example.entity.dto.Account;
import com.example.entity.dto.AccountDetails;
import com.example.entity.dto.AccountPrivacy;
import com.example.entity.vo.response.AccountVO;
import com.example.mapper.AccountDetailsMapper;
import com.example.mapper.AccountMapper;
import com.example.mapper.AccountPrivacyMapper;
import com.example.service.AccountDetailsService;
import com.example.service.AccountPrivacyService;
import com.example.service.AccountService;
import com.example.utils.CacheUtils;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/admin")
public class UserAdminController {

    @Resource
    AccountService accountService;

    @Resource
    AccountDetailsService detailsService;

    @Resource
    AccountPrivacyService privacyService;

    @Resource
    StringRedisTemplate template;

    @Value("${spring.security.jwt.expire}")
    private int expire;

    @GetMapping("/list")
    public RestBean<JSONObject> getUserList(@RequestParam int page,
                                            @RequestParam int size) {
        JSONObject object = new JSONObject();
        List<AccountVO> list = accountService.page(Page.of(page, size))
                .getRecords()
                .stream()
                .map(a -> a.asViewObject(AccountVO.class))
                .toList();
        object.put("list", list);
        object.put("total", accountService.count());
        return RestBean.success(object);
    }

    @GetMapping("/details")
    public RestBean<JSONObject> getUserDetail(@RequestParam int id) {
        JSONObject object = new JSONObject();
        object.put("details", detailsService.findAccountDetailsById(id));
        object.put("privacy", privacyService.accountPrivacy(id));
        return RestBean.success(object);
    }

    @PostMapping("/save")
    public RestBean<Void> saveUser(@RequestBody JSONObject object) {
        Integer id = object.getInteger("id");
        Account account = accountService.findAccountById(id);
        Account save = object.toJavaObject(Account.class);
        handleBanned(account,save);
        BeanUtils.copyProperties(save,account,"password", "registerTime");
        accountService.saveOrUpdate(account);
        AccountDetails details = detailsService.findAccountDetailsById(id);
        AccountDetails saveDetails = object.getJSONObject("details").toJavaObject(AccountDetails.class);
        BeanUtils.copyProperties(saveDetails,details);
        detailsService.saveOrUpdate(details);
        AccountPrivacy privacy = privacyService.accountPrivacy(id);
        AccountPrivacy savePrivacy = object.getJSONObject("privacy").toJavaObject(AccountPrivacy.class);
        BeanUtils.copyProperties(savePrivacy,privacy);
        privacyService.saveOrUpdate(privacy);
        return RestBean.success();
    }

    private void handleBanned(Account oldAccount, Account newAccount) {
        String key = Const.BANNED_BLOCK + newAccount.getId();
        if(!oldAccount.isBanned() && newAccount.isBanned()) {
            template.opsForValue().set(key,Boolean.toString(true),expire, TimeUnit.HOURS);
        } else if(oldAccount.isBanned() && !newAccount.isBanned()) {
            template.delete(key);
        }
    }
}
