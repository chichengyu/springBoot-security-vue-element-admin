# vue-element-admin

#### 介绍

基于vue、element做的一个后台管理系统，默认打包出的是异步加载

#### 架构
架构说明

   + [Preview预览](http://xiaochiwz.gitee.io/thinkphp5.1-vue-ivew-admin)
   + [Element官网](https://element.eleme.cn/2.11/#/zh-CN/component/installation)
   + [Vue官网](https://cn.vuejs.org/v2/guide/)
   + [Webpack官网](https://www.webpackjs.com/)
   + [Easy Mock接口API](https://www.easy-mock.com/project/5bf4b1a323557c43607406bc)
   + [Element-component插件](https://www.npmjs.com/package/element-component)

#### 目录结构
目录  
```  
   │──dist            
   │──src             
   │  │──api             接口请求目录  
   │  │──assets          静态资源目录
   │  │──common          公共目录(如：公共函数,可直接修改，不影响打包后的文件)    
   │  │──components      组件目录  
   │  │──config          配置目录  
   │  │──lib             核心库目录  
   │  │──router          路由目录  
   │  │──store           vuex目录  
   │  │──views           视图目录  
   │  │──App.vue         根组件  
   │  │──index.html      模板文件    
   │  │──main.js         入口文件                  
   │  └──settings.js     配置文件
   │    ...
```

#### Getting started
```
# clone the project
git clone https://gitee.com/chichengyu/springBoot-shiro-vue-element-admin.git 

```
   
#### Build
```
# install dependency
npm install

# test development dev
npm run dev

# build
npm run build

# build test
npm run build:test

# watch
npm run watch
```

#### License
[MIT](https://opensource.org/licenses/MIT)
