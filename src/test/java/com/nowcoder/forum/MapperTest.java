package com.nowcoder.forum;

import com.nowcoder.forum.dao.DiscussPostMapper;
import com.nowcoder.forum.dao.LoginTicketMapper;
import com.nowcoder.forum.dao.MessageMapper;
import com.nowcoder.forum.dao.UserMapper;
import com.nowcoder.forum.entity.DiscussPost;
import com.nowcoder.forum.entity.LoginTicket;
import com.nowcoder.forum.entity.Message;
import com.nowcoder.forum.entity.User;
import com.nowcoder.forum.service.DiscussPostService;
import com.nowcoder.forum.service.UserService;
import com.nowcoder.forum.util.CommunityConstant;
import org.apache.ibatis.annotations.Mapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.*;

@SpringBootTest
@ContextConfiguration(classes = ForumApplication.class)
public class MapperTest implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testSelectUser() {
        User user = userMapper.selectById(101);
        System.out.println(user);

        user = userMapper.selectByName("liubei");
        System.out.println(user);

        user = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
    }

    @Test
    public void testInsertUser() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("22222");
        user.setSalt("abc");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());
        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    public void updateUser() {
        int rows = userMapper.updateStatus(150, 1);
        System.out.println(rows);

        rows = userMapper.updateHeader(150, "http://images.nowcoder.com/head/101t.png");
        System.out.println(rows);

        rows = userMapper.updatePassword(150, "33333");
        System.out.println(rows);
    }

    @Test
    public void testSelectPosts() {
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(149, 0, 10);
        for (DiscussPost discussPost : discussPosts) {
            System.out.println(discussPost);
        }

        int rows = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);
    }


    @Test
    public void testIndexPage() {
        List<DiscussPost> list = discussPostService.findDiscussPosts(0, 0, 10);
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null) {
            for (DiscussPost post : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                System.out.println(post);
                User user = userService.findUserById(post.getUserId());
                map.put("user", user);
                System.out.println(user);
                discussPosts.add(map);
            }
        }
    }

    @Test
    public void testInsertLoginTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setTicket("aaa111");
        loginTicket.setUserId(101);
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + REMEMBER_EXPIRED_SECONDS  * 1000));

        int i = loginTicketMapper.insertLoginTicket(loginTicket);
        System.out.println(i);

        loginTicket = new LoginTicket();
        loginTicket.setTicket("aaa222");
        loginTicket.setUserId(101);
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + DEFAULT_EXPIRED_SECONDS * 1000));

        i = loginTicketMapper.insertLoginTicket(loginTicket);
        System.out.println(i);
    }

    @Test
    public void testSelectTicket() {
        LoginTicket loginTicket = loginTicketMapper.selectLoginTicket("aaa");
        System.out.println(loginTicket);

        loginTicketMapper.updateStatus("aaa", 1);

        loginTicket = loginTicketMapper.selectLoginTicket("aaa");
        System.out.println(loginTicket);

    }

    @Test
    public void testSelectLetters() {
        List<Message> messages = messageMapper.selectConversations(111, 0, 20);
        for (Message message : messages) {
            System.out.println(message);
        }
        int count = messageMapper.selectConversationCount(111);
        System.out.println(count);

        List<Message> messages1 = messageMapper.selectLetters("111_112", 0, 10);
        for (Message message : messages1) {
            System.out.println(message);
        }
        int count1 = messageMapper.selectLetterCount("111_112");
        System.out.println(count1);

        int count2 = messageMapper.selectLetterUnreadCount(131, null);
        System.out.println(count2);

        int count3 = messageMapper.selectLetterUnreadCount(131, "111_131");
        System.out.println(count3);
    }
}
