# springBoot-security-vue-element-admin

[码云](https://gitee.com/chichengyu/springBoot-security-vue-element-admin)

### 后端：springboot-security
使用 `springBoot2.2.6 + security + redis + mysql` 的权限管理系统，账户：` admin `，密码：` 123456 `，如果使用[shiro](https://github.com/chichengyu/springBoot-shiro-vue-element-admin)  
  - security默认验证方式，请看[分支default-AuthenticationToken](https://gitee.com/chichengyu/springBoot-security-vue-element-admin/tree/default-AuthenticationToken)  
  - security多方式登录，详情请看[JwtAuthenticationProvider.java](https://github.com/chichengyu/springBoot-security-vue-element-admin/blob/master/springboot-security/src/main/java/com/site/security/provider/JwtAuthenticationProvider.java)中注释，另外在登录的时候，可以约定账号`username|type`这样便于创建不同的身份令牌

### 前端：vue-element-admin
使用的是 `vue2.6 + element-ui 2.11.1` ，[vue-element-admin下载](https://github.com/chichengyu/vue-element-admin)  
注意：需要打开登录组件开启权限，默认 false 不开启

##### 打包问题
关于前端 ` npm run build `报错 ` ERROR in xxx.js from UglifyJs ` 错误问题，这因为 ` uglifyjs-webpack-plugin版本兼容问题造成的 `，解决方法：运行命令
```
npm uni uglifyjs-webpack-plugin -D

npm i uglifyjs-webpack-plugin@1 -D
```
当前版本是 ` 2.*版本 `，降低到 `1.* 版本`就可以了，再次打包成功

架构说明
   + [Preview预览](http://xiaochiwz.github.io/thinkphp5.1-vue-ivew-admin)
   + [Element官网](https://element.eleme.cn/2.11/#/zh-CN/component/installation)
   + [Element-component插件](https://www.npmjs.com/package/element-component)


##### Getting started
```
# clone the project
git clone https://github.com/chichengyu/springBoot-security-vue-element-admin.git
```
##### Build
```
# install
npm install

# test
npm run build:test

# build
npm run build

# watch
npm run watch

# dev-server
npm run dev
```
##### Preview
![输入图片说明](https://images.gitee.com/uploads/images/2020/0519/161814_3e6bf8d6_1508174.png "login.png")
![输入图片说明](https://images.gitee.com/uploads/images/2020/0806/225307_fc070103_1508174.png "menu.png")
![输入图片说明](https://images.gitee.com/uploads/images/2020/0806/225319_af7245c6_1508174.png "role.png")
![输入图片说明](https://images.gitee.com/uploads/images/2020/0806/225331_96631773_1508174.png "user.png")