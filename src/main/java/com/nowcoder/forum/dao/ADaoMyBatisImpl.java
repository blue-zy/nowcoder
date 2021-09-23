package com.nowcoder.forum.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public class ADaoMyBatisImpl implements ADao {

    @Override
    public String select() {
        return "MyBatis";
    }
}
