package cn.edu.jlu.admin.mapper;

import cn.edu.jlu.admin.entity.Admin;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminMapper extends BaseMapper<Admin> {
    // MyBatis-Plus 会自动实现 CRUD 方法
}
