package cn.edu.jlu.user.mapper;

import cn.edu.jlu.user.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    // MyBatis-Plus 已提供基本增删改查的方法
}
