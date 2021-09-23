package com.nowcoder.forum.dao;

import org.springframework.stereotype.Repository;

@Repository("aHibernate")
public class ADaoHibernateImpl implements ADao{
    @Override
    public String select() {
        return "Hibernate";
    }
}
