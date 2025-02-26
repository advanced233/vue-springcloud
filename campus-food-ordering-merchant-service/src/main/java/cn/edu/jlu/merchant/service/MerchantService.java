package cn.edu.jlu.merchant.service;

import cn.edu.jlu.merchant.entity.Merchant;
import cn.edu.jlu.merchant.entity.Dish;
import cn.edu.jlu.merchant.entity.MerchantLoginResponse;
import cn.edu.jlu.merchant.mapper.MerchantMapper;
import cn.edu.jlu.merchant.mapper.DishMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.util.DigestUtils;
import java.util.List;

@Service
public class MerchantService {

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private DishMapper dishMapper;

    // 查询所有商家列表
    public List<Merchant> listMerchants() {
        return merchantMapper.selectList(null);
    }

    // 根据商家ID查询商家详情
    public Merchant getMerchantById(Long id) {
        return merchantMapper.selectById(id);
    }

    // 根据商家ID查询菜品列表
    public List<Dish> getDishesByMerchantId(Long merchantId) {
        return dishMapper.selectByMerchantId(merchantId);
    }

    // 商家注册：检查账号是否存在，密码加密后存入数据库
    public String registerMerchant(Merchant merchant) {
        QueryWrapper<Merchant> query = new QueryWrapper<>();
        query.eq("account", merchant.getAccount());
        Merchant existing = merchantMapper.selectOne(query);
        if (existing != null) {
            return "账号已存在";
        }
        // 简单加密密码（MD5，仅演示用，实际建议使用更安全的方式）
        String md5Password = DigestUtils.md5DigestAsHex(merchant.getPassword().getBytes());
        merchant.setPassword(md5Password);
        int result = merchantMapper.insert(merchant);
        return result > 0 ? "注册成功" : "注册失败";
    }

    // 商家登录：根据账号查询并校验密码
    public MerchantLoginResponse loginMerchant(String account, String password) {
        QueryWrapper<Merchant> query = new QueryWrapper<>();
        query.eq("account", account);
        Merchant merchant = merchantMapper.selectOne(query);

        MerchantLoginResponse resp = new MerchantLoginResponse();

        if (merchant == null) {
            resp.setMerchantId(null);
            resp.setMessage("商家不存在");
            return resp;
        }

        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!md5Password.equals(merchant.getPassword())) {
            resp.setMerchantId(null);
            resp.setMessage("密码错误");
            return resp;
        }

        // 登录成功
        resp.setMerchantId(merchant.getId());
        resp.setMessage("登录成功，欢迎 " + merchant.getName());
        return resp;
    }

    // 更新店铺信息（例如名称、logo 等），merchant 对象必须包含 id
    public String updateMerchantInfo(Merchant merchant) {
        int result = merchantMapper.updateById(merchant);
        return result > 0 ? "更新成功" : "更新失败";
    }

    // 添加新菜品
    public String addDish(Dish dish) {
        int result = dishMapper.insert(dish);
        return result > 0 ? "菜品添加成功" : "菜品添加失败";
    }

    // 修改菜品信息
    public String updateDish(Dish dish) {
        int result = dishMapper.updateById(dish);
        return result > 0 ? "菜品修改成功" : "菜品修改失败";
    }

    // 修改菜品状态（上架/下架）
    public String changeDishStatus(Long dishId, Integer status) {
        Dish dish = dishMapper.selectById(dishId);
        if (dish == null) {
            return "菜品不存在";
        }
        dish.setStatus(status);
        int result = dishMapper.updateById(dish);
        return result > 0 ? "菜品状态更新成功" : "菜品状态更新失败";
    }

    // 删除菜品
    public String deleteDish(Long dishId) {
        int result = dishMapper.deleteById(dishId);
        return result > 0 ? "菜品删除成功" : "菜品删除失败";
    }

    // 根据菜品ID查询菜品信息
    public Dish getDishById(Long dishId) {
        return dishMapper.selectById(dishId);
    }

}
