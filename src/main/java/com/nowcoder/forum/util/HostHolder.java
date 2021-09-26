package com.nowcoder.forum.util;

import com.nowcoder.forum.entity.User;
import com.nowcoder.forum.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.jws.soap.SOAPBinding;

// 持有用户的信息 用于代替session对象
@Component
public class HostHolder {

    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }

}
