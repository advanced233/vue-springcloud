package cn.edu.jlu.merchant.controller;

import cn.edu.jlu.merchant.entity.Merchant;
import cn.edu.jlu.merchant.entity.Dish;
import cn.edu.jlu.merchant.entity.MerchantLoginResponse;
import cn.edu.jlu.merchant.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/merchant")
@CrossOrigin(origins = "*") // 允许所有域名跨域访问
public class MerchantController {

    @Autowired
    private MerchantService merchantService;

    // ---------- 商家基础查询接口 ----------
    // 查询商家列表
    @GetMapping("/list")
    public List<Merchant> listMerchants() {
        return merchantService.listMerchants();
    }

    // 查询某个商家详情
    @GetMapping("/{id}")
    public Merchant getMerchant(@PathVariable("id") Long id) {
        return merchantService.getMerchantById(id);
    }

    // 查询某个商家的菜品列表
    @GetMapping("/{id}/dish")
    public List<Dish> getDishes(@PathVariable("id") Long merchantId) {
        return merchantService.getDishesByMerchantId(merchantId);
    }

    // ---------- 商家注册登录与店铺信息管理 ----------
    // 商家注册接口
    @PostMapping("/register")
    public String registerMerchant(@RequestBody Merchant merchant) {
        return merchantService.registerMerchant(merchant);
    }

    // 商家登录接口
    // 商家登录接口
    @PostMapping("/login")
    public MerchantLoginResponse loginMerchant(@RequestParam String account, @RequestParam String password) {
        // 调用 service 获取返回对象
        return merchantService.loginMerchant(account, password);
    }

    // 更新店铺信息接口
    @PutMapping("/update")
    public String updateMerchant(@RequestBody Merchant merchant) {
        return merchantService.updateMerchantInfo(merchant);
    }

    // ---------- 菜品管理 ----------
    // 添加新菜品接口
    @PostMapping("/dish/add")
    public String addDish(@RequestBody Dish dish) {
        return merchantService.addDish(dish);
    }

    // 修改菜品信息接口
    @PutMapping("/dish/update")
    public String updateDish(@RequestBody Dish dish) {
        return merchantService.updateDish(dish);
    }

    // 修改菜品状态接口（上架/下架）
    @PutMapping("/dish/status")
    public String changeDishStatus(@RequestParam Long dishId, @RequestParam Integer status) {
        return merchantService.changeDishStatus(dishId, status);
    }

    // 删除菜品接口
    @DeleteMapping("/dish/delete")
    public String deleteDish(@RequestParam Long dishId) {
        return merchantService.deleteDish(dishId);
    }
}
