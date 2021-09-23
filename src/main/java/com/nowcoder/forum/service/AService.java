package com.nowcoder.forum.service;

import com.nowcoder.forum.dao.ADao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
//@Scope("prototype")
public class AService {

    @Autowired
    private ADao aDao;

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
}
