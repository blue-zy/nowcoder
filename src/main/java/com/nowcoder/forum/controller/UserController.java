package com.nowcoder.forum.controller;

import com.nowcoder.forum.annotation.LoginRequired;
import com.nowcoder.forum.entity.Page;
import com.nowcoder.forum.entity.User;
import com.nowcoder.forum.service.FollowService;
import com.nowcoder.forum.service.LikeService;
import com.nowcoder.forum.service.UserService;
import com.nowcoder.forum.util.CommunityConstant;
import com.nowcoder.forum.util.CommunityUtil;
import com.nowcoder.forum.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.annotation.ModelAndViewResolver;

import javax.jws.WebParam;
import javax.jws.soap.SOAPBinding;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant{

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private FollowService followService;

    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "???????????????????????????");
            return "/site/setting";
        }

        String filename = headerImage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        if (StringUtils.isBlank(suffix) || !suffix.equals(".png") || !suffix.equals(".jpg") || !suffix.equals(".jpeg")) {
            model.addAttribute("error", "????????????????????????");
            return "/site/setting";
        }

        // ?????????????????????
        filename = CommunityUtil.generateUUID() + suffix;
        File dest = new File(uploadPath + "/" + filename);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("?????????????????????" + e.getMessage());
            throw new RuntimeException("?????????????????????????????????????????????", e);
        }

        // ????????????????????????(web????????????) http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headUrl = domain + contextPath + "/user/header/" + filename;
        userService.updateHeader(user.getId(), headUrl);

        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{filename}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("filename") String filename, HttpServletResponse response) {
        // ????????????????????????
        filename = uploadPath + "/" + filename;

        // ??????????????????
        String suffix = filename.substring(filename.lastIndexOf("."));

        // ????????????
        response.setContentType("image/" + suffix);
        try (ServletOutputStream os = response.getOutputStream();
             FileInputStream fis = new FileInputStream(filename)) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("?????????????????????" + e.getMessage());
        }

    }

    @LoginRequired
    @RequestMapping(path = "/modifypassword", method = RequestMethod.POST)
    public String modifyPassword(String oldPassword, String newPassword, Model model) {
        if (StringUtils.isBlank(oldPassword) || StringUtils.isBlank(newPassword)) {
            model.addAttribute("passwordError", "?????????????????????");
            return "/site/setting";
        }

        User user = hostHolder.getUser();
        String s = CommunityUtil.md5(oldPassword + user.getSalt());

        if (!s.equals(user.getPassword())) {
            model.addAttribute("passwordError", "????????????????????????");
            return "/site/setting";
        }

        userService.updatePassword(user.getId(), CommunityUtil.md5(newPassword + user.getSalt()));

        return "redirect:/index";
    }

    // ????????????
    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfile(@PathVariable("userId") int userId, Model model ) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("?????????????????????");
        }

        // ??????
        model.addAttribute("user", user);
        // ????????????
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);
        // ?????????????????????????????????
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);

        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);
        // ?????????????????????????????????
        boolean hasFollowed = false;
        if (hostHolder.getUser() != null) {
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);
        return "/site/profile";
    }
}
