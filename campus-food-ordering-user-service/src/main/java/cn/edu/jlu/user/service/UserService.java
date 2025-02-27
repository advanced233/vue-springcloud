package cn.edu.jlu.user.service;

import cn.edu.jlu.user.entity.LoginResponse;
import cn.edu.jlu.user.entity.User;
import cn.edu.jlu.user.entity.UserAddress;
import cn.edu.jlu.user.entity.UserPersonalInfoDTO;
import cn.edu.jlu.user.mapper.UserMapper;
import cn.edu.jlu.user.mapper.UserAddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.util.DigestUtils;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserAddressMapper userAddressMapper;

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
            resp.setStatus(null); // 或者 -1 表示不存在
            return resp;
        }

        // 检查是否封禁
        if (user.getStatus() == 0) {
            resp.setUserId(null);
            resp.setMessage("该用户已被封禁");
            resp.setStatus(0); // 0 表示被封禁
            return resp;
        }

        // 先对比密码（MD5加密）
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!md5Password.equals(user.getPassword())) {
            // 密码错误
            resp.setUserId(null);
            resp.setMessage("密码错误");
            resp.setStatus(null); // 或者 -2 表示密码错误
            return resp;
        }

        // 否则登录成功
        resp.setUserId(user.getId());
        resp.setMessage("登录成功，欢迎 " + username);
        resp.setStatus(user.getStatus());
        return resp;
    }

    /**
     * 获取用户个人信息（包括地址）
     */
    public UserPersonalInfoDTO getUserPersonalInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return null;  // 或抛出异常提示“用户不存在”
        }
        // 查询该用户的所有地址
        QueryWrapper<UserAddress> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        List<UserAddress> addresses = userAddressMapper.selectList(wrapper);

        // 组装 DTO
        UserPersonalInfoDTO dto = new UserPersonalInfoDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setPassword(user.getPassword());  // 注意：实际开发中不建议直接返回密码
        dto.setPhone(user.getPhone());
        dto.setAvatar(user.getAvatar());
        dto.setStatus(user.getStatus());
        dto.setCreateTime(user.getCreateTime());
        dto.setUpdateTime(user.getUpdateTime());
        dto.setAddresses(addresses);
        return dto;
    }

    /**
     * 更新用户个人信息（包括地址）
     * 简单实现：更新用户基本信息后，删除原地址，再插入新地址列表
     */
    public String updateUserPersonalInfo(UserPersonalInfoDTO dto) {
        // 更新用户基本信息
        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        // 如果传入的密码非空，进行 MD5 加密再更新
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            String md5Password = DigestUtils.md5DigestAsHex(dto.getPassword().getBytes());
            user.setPassword(md5Password);
        }
        user.setPhone(dto.getPhone());
        user.setAvatar(dto.getAvatar());
        user.setStatus(dto.getStatus());
        int result = userMapper.updateById(user);

        // 更新地址信息：先删除该用户所有地址，再插入新的地址列表
        QueryWrapper<UserAddress> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", dto.getId());
        userAddressMapper.delete(wrapper);

        if (dto.getAddresses() != null) {
            for (UserAddress address : dto.getAddresses()) {
                address.setUserId(dto.getId());
                userAddressMapper.insert(address);
            }
        }
        return result > 0 ? "更新成功" : "更新失败";
    }

    public List<User> getAllUsers() {
        // 查询所有用户，传入 null 表示无查询条件
        return userMapper.selectList(null);
    }

    public String updateUserStatus(Long userId, Integer status) {
        // 先检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            return "用户不存在";
        }
        // 设置新状态（0:封禁，1:正常）
        user.setStatus(status);
        int result = userMapper.updateById(user);
        return result > 0 ? "更新成功" : "更新失败";
    }
}
