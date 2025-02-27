package cn.edu.jlu.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 管理员服务启动程序
 */
@SpringBootApplication
@EnableDiscoveryClient  // 开启服务注册功能
public class AdminServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminServiceApplication.class, args);
    }
}
