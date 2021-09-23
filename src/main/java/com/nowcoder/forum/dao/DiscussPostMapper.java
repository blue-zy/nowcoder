package com.nowcoder.forum.dao;

import com.nowcoder.forum.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    // param注解用于给参数取别名的
    // 如果参数只有一个 并且在<if>中使用 则必须加别名
    int selectDiscussPostRows(@Param("userId") int userId);

}
