package cn.edu.jlu.admin.controller;

import cn.edu.jlu.admin.entity.Admin;
import cn.edu.jlu.admin.entity.AdminLoginResponse;
import cn.edu.jlu.admin.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*") // 允许所有域名跨域访问
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * 管理员注册接口
     * URL：POST /admin/register
     * 参数：JSON 格式的 Admin 对象
     */
    @PostMapping("/register")
    public String register(@RequestBody Admin admin) {
        return adminService.register(admin);
    }

    /**
     * 管理员登录接口
     * URL：POST /admin/login
     * 参数：username、password（作为请求参数）
     */
    @PostMapping("/login")
    public AdminLoginResponse login(@RequestParam String username, @RequestParam String password) {
        return adminService.login(username, password);
    }
}
