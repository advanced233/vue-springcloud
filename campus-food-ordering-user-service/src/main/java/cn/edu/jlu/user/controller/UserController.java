package cn.edu.jlu.user.controller;

import cn.edu.jlu.user.entity.LoginResponse;
import cn.edu.jlu.user.entity.UpdateUserStatusDTO;
import cn.edu.jlu.user.entity.User;
import cn.edu.jlu.user.entity.UserPersonalInfoDTO;
import cn.edu.jlu.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*") // 允许所有域名跨域访问
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
    public LoginResponse login(@RequestParam String username, @RequestParam String password) {
        return userService.login(username, password);
    }

    /**
     * 获取用户个人信息（包括地址）
     * URL：GET /user/personal-info?userId=xxx
     */
    @GetMapping("/personal-info")
    public UserPersonalInfoDTO getPersonalInfo(@RequestParam Long userId) {
        return userService.getUserPersonalInfo(userId);
    }

    /**
     * 更新用户个人信息（包括地址）
     * URL：PUT /user/personal-info
     * 参数：JSON格式的 UserPersonalInfoDTO 对象
     */
    @PutMapping("/personal-info")
    public String updatePersonalInfo(@RequestBody UserPersonalInfoDTO dto) {
        return userService.updateUserPersonalInfo(dto);
    }

    /**
     * 获取所有用户信息（仅管理员可用）
     * URL：GET /user/all
     */
    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * 更新用户状态（封禁、解禁）
     * URL：PUT /user/status
     * 参数：JSON 格式的 UpdateUserStatusDTO 对象（包含 userId 和 status）
     */
    @PutMapping("/status")
    public String updateUserStatus(@RequestBody UpdateUserStatusDTO dto) {
        // TODO: 实际开发中建议在这里添加管理员权限校验
        return userService.updateUserStatus(dto.getUserId(), dto.getStatus());
    }

}
