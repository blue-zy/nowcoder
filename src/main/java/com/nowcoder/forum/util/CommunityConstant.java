package com.nowcoder.forum.util;

public interface CommunityConstant {

    /**
     * 激活状态
     */
    int ACTIVATION_SUCCESS = 0;
    int ACTIVATION_REPEAT = 1;
    int ACTIVATION_FAILURE = -1;

    /**
     * 登录凭证超时时间
     */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 20; // 不知道为什么设置成30天会出错

    /**
     * 实体类型：帖子
     */
    int ENTITY_TYPE_POST=1;

    /**
     * 实体类型：评论
     */
    int ENTITY_TYPE_COMMENT=2;
    /**
     * 实体类型：帖子
     */
    int ENTITY_TYPE_USER=3;

}
