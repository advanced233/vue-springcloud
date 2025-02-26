package cn.edu.jlu.user.service;

import cn.edu.jlu.user.entity.LoginResponse;
import cn.edu.jlu.user.entity.User;
import cn.edu.jlu.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.util.DigestUtils;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 用户注册
     */
    public String register(User user) {
        // 检查用户名是否已存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        User existingUser = userMapper.selectOne(queryWrapper);
        if (existingUser != null) {
            return "用户名已存在";
        }
        // 简单加密密码（MD5，仅供演示；实际开发中建议使用更安全的加密方式）
        String md5Password = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
        user.setPassword(md5Password);
        int result = userMapper.insert(user);
        return result > 0 ? "注册成功" : "注册失败";
    }

    /**
     * 用户登录
     * 先查用户名，如果不存在就提示 "用户不存在"；
     * 如果密码不匹配就提示 "密码错误"；
     * 否则返回用户ID和 "登录成功，欢迎 xxx"
     */
    public LoginResponse login(String username, String password) {
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);

        // 创建一个响应对象
        LoginResponse resp = new LoginResponse();

        if (user == null) {
            // 用户不存在
            resp.setUserId(null);
            resp.setMessage("用户不存在");
            return resp;
        }

        // 先对比密码（MD5加密）
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!md5Password.equals(user.getPassword())) {
            // 密码错误
            resp.setUserId(null);
            resp.setMessage("密码错误");
            return resp;
        }

        // 否则登录成功
        resp.setUserId(user.getId());
        resp.setMessage("登录成功，欢迎 " + username);
        return resp;
    }
}
