package cn.edu.jlu.user.service;

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
     */
    public String login(String username, String password) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            return "用户不存在";
        }
        // 对比密码（使用MD5加密对比）
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!md5Password.equals(user.getPassword())) {
            return "密码错误";
        }
        // MVP阶段直接返回一个登录成功的提示，后续可使用JWT或Session返回Token
        return "登录成功，欢迎 " + username;
    }
}
