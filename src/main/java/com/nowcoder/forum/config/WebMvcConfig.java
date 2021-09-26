package com.nowcoder.forum.config;

import com.nowcoder.forum.controller.interceptor.AInterceptor;
import com.nowcoder.forum.controller.interceptor.LoginRequiredInterception;
import com.nowcoder.forum.controller.interceptor.LoginTicketInterceptor;
import com.nowcoder.forum.entity.LoginTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AInterceptor aInterceptor;

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    @Autowired
    private LoginRequiredInterception loginRequiredInterception;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(aInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png","/**/*.jpg","/**/*.jpeg")
                .excludePathPatterns("/register","/login");

        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png","/**/*.jpg","/**/*.jpeg");

        registry.addInterceptor(loginRequiredInterception)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png","/**/*.jpg","/**/*.jpeg");
    }

}
