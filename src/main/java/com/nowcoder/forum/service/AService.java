package com.nowcoder.forum.service;

import com.nowcoder.forum.dao.ADao;
import com.nowcoder.forum.dao.DiscussPostMapper;
import com.nowcoder.forum.dao.UserMapper;
import com.nowcoder.forum.entity.DiscussPost;
import com.nowcoder.forum.entity.User;
import com.nowcoder.forum.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;

@Service
//@Scope("prototype")
public class AService {

    @Autowired
    private ADao aDao;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    public AService(){
        System.out.println("实例化AService");
    }

    @PostConstruct
    public void init(){
        System.out.println("init AService");
    }

    @PreDestroy
    public void destory(){
        System.out.println("销毁Aservice");
    }

    public String find() {
        return aDao.select();
    }

    // 演示事务
    // Propagation -- 传播机制 业务方法A可能调用业务方法B 并且两个业务都加了事务注解 决定事务处理以谁为准
    // REQUIRED：支持当前事务（外部事务，对于B 即是调用我的事务即A），如果不存在则创建新事务
    // REQUIRES_NEW：创建一个新的事务，并且暂定当前事务（外部事务），永远按照我的方式执行
    // NESTED：如果存在当前事务（外部事物），则嵌套在该事务中执行（独立的提交回滚），如果不存在就和REQUIRED一样了
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Object save1() {
        // 业务完整逻辑为新增一个用户并发一个帖子

        // 新增用户
        User user = new User();
        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
        user.setEmail("alpha@qq.com");
        user.setHeaderUrl("http://images.nowcoder.com/head/100t.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 新增帖子
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle("hello");
        post.setContent("新人报道");
        post.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(post);

        // 人为报错 测试事务
        Integer.valueOf("abc");

        return "ok";
    }

    // 演示编程式事务
    public Object save2() {
        // 业务完整逻辑为新增一个用户并发一个帖子
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        // 实现逻辑 有template去调
        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                // 新增用户
                User user = new User();
                user.setUsername("beta");
                user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
                user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
                user.setEmail("beta@qq.com");
                user.setHeaderUrl("http://images.nowcoder.com/head/100t.png");
                user.setCreateTime(new Date());
                userMapper.insertUser(user);

                // 新增帖子
                DiscussPost post = new DiscussPost();
                post.setUserId(user.getId());
                post.setTitle("betahello");
                post.setContent("beta报道");
                post.setCreateTime(new Date());
                discussPostMapper.insertDiscussPost(post);

                // 人为报错 测试事务
                Integer.valueOf("abc");

                return "ok";
            }
        });

    }
}
