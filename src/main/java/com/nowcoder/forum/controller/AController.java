package com.nowcoder.forum.controller;

import com.nowcoder.forum.service.AService;
import com.nowcoder.forum.util.CommunityConstant;
import com.nowcoder.forum.util.CommunityUtil;
import com.sun.xml.internal.ws.api.ha.StickyFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/a")
public class AController {

    @Autowired
    private AService aService;

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello() {
        return "hello spring boot.";
    }

    @RequestMapping("/data")
    @ResponseBody
    public String getData() {
        return aService.find();
    }

    // 底层的处理请求响应方式
    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response) {
        // 获取请求数据
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        //很老的迭代器 跟iterator一样 但不推荐使用 但是request对象很早就有就保留了
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            System.out.println(name + ":" + value);
        }
        System.out.println(request.getParameter("code"));

        // 返回响应数据
        response.setContentType("text/html;charset=utf-8");
        // java7新语法 在try里面加括号会自动加个finally关闭 但前提要有close方法
        try (
                PrintWriter writer = response.getWriter();
        ) {
            writer.write("<h1>牛客网<h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // GET请求处理 两种传参方式 一种是?后接 一种是写入路劲中
    // /students?current=1&limit=20 模拟分页 传入两个参数：第几页 显示多少
    @RequestMapping(path = "/students", method = RequestMethod.GET)
    @ResponseBody
    // 只要参数名和传入的参数名保持一致其实就能得到 DispatcherServlet检测到的话会自动传入
    public String getStudents(
            @RequestParam(name = "current", required = false, defaultValue = "1") int current,
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }

    // /students/123
    @RequestMapping(path = "/students/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id) {
        System.out.println(id);
        return "a student";
    }


    // POST提交请求处理方式 处理浏览器的表单提交的数据
    @RequestMapping(path = "/student", method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name, int age) {
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

    // 向浏览器返回响应数据
    // html数据 不加ResponseBody 则默认返回html
    @RequestMapping(path = "/teacher", method = RequestMethod.GET)
    public ModelAndView getTeacher() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("name", "张三");
        mav.addObject("age", 30);
        mav.setViewName("/demo/view"); // 设置模板路劲名 thymeleaf默认就是html模板 不用写后缀 view.html
        return mav;
    }

    @RequestMapping(path = "/school", method = RequestMethod.GET)
    public String getSchool(Model model) { // 此时返回值类型写的是String 则返回的是路径
        model.addAttribute("name", "北京大学");
        model.addAttribute("age", 80);
        return "/demo/view";
    }

    // 向浏览器响应json数据 一般在异步请求中
    @RequestMapping(path = "/emp", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getEmp() {
        // 此时DispatcherServlet会自动将map专程json字符串
        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "张三");
        emp.put("age", 23);
        emp.put("salary", 8000.00);
        return emp;
    }

    @RequestMapping(path = "/emps", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getEmps() {
        // 此时DispatcherServlet会自动将map专程json字符串
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "张三");
        emp.put("age", 23);
        emp.put("salary", 8000.00);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name", "李四");
        emp.put("age", 25);
        emp.put("salary", 9000.00);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name", "王五");
        emp.put("age", 29);
        emp.put("salary", 10000.00);
        list.add(emp);
        return list;
    }

    // cookie示例
    @RequestMapping(path = "/cookie/set", method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response) {
        // 创建cookie
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
        // 设置生效范围
        cookie.setPath("/community/a");
        // 设置生效时间 默认的话存在内存里 关闭浏览器就消失
        cookie.setMaxAge(60 * 3); // 单位second
        response.addCookie(cookie);
        return "set cookie";
    }

    @RequestMapping(path = "/cookie/get", method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code") String code) {
        System.out.println(code);
        return "get cookie";
    }

    // session示例
    @RequestMapping(path = "/session/set", method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session) {
        // session可以存对象
        session.setAttribute("id", 1);
        session.setAttribute("name", "test");
        return "set session";
    }

    @RequestMapping(path = "/session/get", method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session) {
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get session";
    }

    // AJAX示例
    @RequestMapping(path = "/ajax", method = RequestMethod.POST)
    @ResponseBody
    public String testAjax(String name, int age) {
        System.out.println(name);
        System.out.println(age);
        return CommunityUtil.getJSONString(0, "操作成功！");
    }
}
