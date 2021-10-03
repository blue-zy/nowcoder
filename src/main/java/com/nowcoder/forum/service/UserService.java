package com.nowcoder.forum.service;

import com.nowcoder.forum.dao.LoginTicketMapper;
import com.nowcoder.forum.dao.UserMapper;
import com.nowcoder.forum.entity.LoginTicket;
import com.nowcoder.forum.entity.User;
import com.nowcoder.forum.util.CommunityConstant;
import com.nowcoder.forum.util.CommunityUtil;
import com.nowcoder.forum.util.MailClient;
import com.nowcoder.forum.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.jws.soap.SOAPBinding;
import java.lang.annotation.Retention;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    //    @Autowired
//    private LoginTicketMapper loginTicketMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int id) {
//        return userMapper.selectById(id);
        User user = getCache(id);
        if (user == null) {
            user = initCache(id);
        }
        return user;
    }

    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();

        // 对空值做判断处理
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空！");
            return map;
        }

        // 验证账号是否被注册过
        User u = userMapper.selectByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "该账号已存在！");
            return map;
        }

        // 验证账号是否被注册过
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "该邮箱已被注册！");
            return map;
        }

        // 注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 发送激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // http://localhost:8080/community/activation/101/code
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);

        return map;
    }

    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            clearCache(userId);
            userMapper.updateStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();

        // 判断空值
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg","密码不能为空！");
            return map;
        }

        // 验证账号
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "该账号不存在！");
            return map;
        }
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "该账号未激活！");
            return map;
        }

        password = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码不正确！");
            return map;
        }

        // 生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setUserId(user.getId());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * expiredSeconds));
//        loginTicketMapper.insertLoginTicket(loginTicket);

        // 将凭证存入redis中
        String redisKey = RedisUtil.getTicketKey(loginTicket.getTicket());
        // redis会将loginTicket对象序列化成JSON字符串保存
        redisTemplate.opsForValue().set(redisKey,loginTicket);

        map.put("ticket", loginTicket.getTicket());

        return map;
    }

    public void logout(String ticket) {
//        loginTicketMapper.updateStatus(ticket, 1);
        String redisKey = RedisUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey, loginTicket);
    }

    public LoginTicket findLoginTicket(String ticket) {
//        return loginTicketMapper.selectLoginTicket(ticket);
        String redisKey = RedisUtil.getTicketKey(ticket);
        return (LoginTicket)redisTemplate.opsForValue().get(redisKey);
    }

    public int updateHeader(int userId, String headerUrl) {
        int rows = userMapper.updateHeader(userId, headerUrl);
        clearCache(userId);
        return rows;
    }

    public int updatePassword(int userId, String password) {
        int rows = userMapper.updatePassword(userId, password);
        clearCache(userId);
        return rows;
    }

    public User findUserByName(String username) {
        return userMapper.selectByName(username);
    }

    // 缓存用户信息
    // 1.尝试从缓存中取数据 无则初始化（从数据库）
    private User getCache(int userId) {
        String redisKey = RedisUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }

    private User initCache(int userId) {
        User user = userMapper.selectById(userId);
        String redisKey = RedisUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }
    // 2.改变用户数据的时候 可以更新缓存（可能会有并发的问题）或者直接把缓存删除（常用）

    private void clearCache(int userId) {
        String redisKey = RedisUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }

}
