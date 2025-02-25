package cn.edu.jlu.user.controller;

import cn.edu.jlu.user.entity.User;
import cn.edu.jlu.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册接口
     * URL：POST /user/register
     * 参数：JSON格式的 User 对象
     */
    @PostMapping("/register")
    public String register(@RequestBody User user) {
        return userService.register(user);
    }

    /**
     * 用户登录接口
     * URL：POST /user/login
     * 参数：username、password（作为请求参数）
     */
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        return userService.login(username, password);
    }
}
