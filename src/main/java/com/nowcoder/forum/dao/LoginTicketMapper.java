package com.nowcoder.forum.dao;

import com.nowcoder.forum.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
// 声明为不推荐使用
@Deprecated
public interface LoginTicketMapper {

    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired) ",
            "value ",
            "(#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    @Select({
            "select id,user_id,ticket,status,expired ",
            "from login_ticket ",
            "where ticket=#{ticket}"
    })
    LoginTicket selectLoginTicket(String ticket);

    @Update({
            "update login_ticket set status=#{status} ",
            "where ",
            "ticket=#{ticket}"
    })
    int updateStatus(String ticket, int status);

}
