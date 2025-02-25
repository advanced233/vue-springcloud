// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router';

// 导入组件
import WelcomePage from '../views/WelcomePage.vue'; // 欢迎页面
// import UserLogin from '../views/UserLogin.vue';     // 用户登录页面
import UserRegister from '../views/UserRegister.vue';
import UserLogin from "../views/UserLogin.vue";
import UserHomePage from "../views/UserHomePage.vue"; // 用户注册页面
// import MerchantLogin from '../views/MerchantLogin.vue'; // 商家登录页面
// import MerchantRegister from '../views/MerchantRegister.vue'; // 商家注册页面

const routes = [
    {
        path: '/',              // 根路径
        name: 'Welcome',
        component: WelcomePage, // 欢迎页面组件
    },
    {
        path: '/user/login',    // 用户登录路径
        name: 'UserLogin',
        component: UserLogin,
    },
    {
        path: '/user/register', // 用户注册路径
        name: 'UserRegister',
        component: UserRegister,
    },
    {
        path: '/user/homepage',
        name: '/UserHomePage',
        component: UserHomePage
    },
    // {
    //     path: '/merchant/login', // 商家登录路径
    //     name: 'MerchantLogin',
    //     component: MerchantLogin,
    // },
    // {
    //     path: '/merchant/register', // 商家注册路径
    //     name: 'MerchantRegister',
    //     component: MerchantRegister,
    // },
];

const router = createRouter({
    history: createWebHistory(), // 使用 HTML5 历史模式
    routes,
});

export default router;