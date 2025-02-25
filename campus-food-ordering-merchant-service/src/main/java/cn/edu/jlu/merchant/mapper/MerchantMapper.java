package cn.edu.jlu.merchant.mapper;

import cn.edu.jlu.merchant.entity.Merchant;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MerchantMapper extends BaseMapper<Merchant> {
    // MyBatis-Plus 已提供基本 CRUD 方法
}
