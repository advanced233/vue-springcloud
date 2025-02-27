package cn.edu.jlu.admin.service;

import cn.edu.jlu.admin.entity.Admin;
import cn.edu.jlu.admin.entity.AdminLoginResponse;
import cn.edu.jlu.admin.mapper.AdminMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class AdminService {

    @Autowired
    private AdminMapper adminMapper;

    /**
     * 管理员注册方法
     *
     * @param admin 注册时传入的管理员信息
     * @return 注册成功或失败的提示信息
     */
    public String register(Admin admin) {
        // 检查用户名是否已存在
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", admin.getUsername());
        Admin existingAdmin = adminMapper.selectOne(queryWrapper);
        if (existingAdmin != null) {
            return "用户名已存在";
        }
        // 使用 MD5 加密密码（注意：仅作演示，实际开发中应采用更安全的加密方式）
        String md5Password = DigestUtils.md5DigestAsHex(admin.getPassword().getBytes());
        admin.setPassword(md5Password);
        int result = adminMapper.insert(admin);
        return result > 0 ? "注册成功" : "注册失败";
    }

    /**
     * 管理员登录方法
     *
     * @param username 登录用户名
     * @param password 登录密码
     * @return 返回封装的登录响应对象
     */
    public AdminLoginResponse login(String username, String password) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        Admin admin = adminMapper.selectOne(queryWrapper);

        AdminLoginResponse response = new AdminLoginResponse();
        if (admin == null) {
            // 用户名不存在
            response.setAdminId(null);
            response.setMessage("管理员不存在");
            return response;
        }

        // 对比 MD5 加密后的密码
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!md5Password.equals(admin.getPassword())) {
            response.setAdminId(null);
            response.setMessage("密码错误");
            return response;
        }

        // 登录成功
        response.setAdminId(admin.getId());
        response.setMessage("登录成功，欢迎 " + username);
        return response;
    }
}
