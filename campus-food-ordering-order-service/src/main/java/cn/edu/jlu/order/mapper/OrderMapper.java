package cn.edu.jlu.order.mapper;

import cn.edu.jlu.order.entity.Order;
import cn.edu.jlu.order.entity.OrderItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
