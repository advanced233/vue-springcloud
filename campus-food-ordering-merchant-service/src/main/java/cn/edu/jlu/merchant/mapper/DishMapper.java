package cn.edu.jlu.merchant.mapper;

import cn.edu.jlu.merchant.entity.Dish;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
    // 使用 @Select 注解定义查询
    @Select("SELECT * FROM dish WHERE merchant_id = #{merchantId}")
    List<Dish> selectByMerchantId(Long merchantId);
}
