<template>
    <div class="home">
        <div class="element-icon">
            <div class="system">
                <el-alert title="介绍" type="success" show-icon :closable="false"></el-alert>
                <h3>基于Spring Boot、mybatis、Vue、Element实现的Java快速开发平台</h3>
                <div class="content" style="padding-left:3%">
                    <div class="left" style="display: inline-block">
                        <h4>开发环境</h4>
                        <ul>
                            <li>IDE : eclipse 4.6.x。     JDK : JDK 1.8.x。</li>
                            <li>Maven : Maven 3.5.x。   MySQL: MySQL 5.7.x。</li>
                            <li>IDE : VS Code 1.27。    Webpack：webpack 3.2.x。</li>
                            <li>NODE: Node 8.9.x。      NPM : NPM 6.4.x。</li>
                            <li>IDE : eclipse 4.6.x。     JDK : JDK 1.8.x。</li>
                        </ul>
                    </div>
                    <div class="right" style="display:inline-block;padding-left:6%">
                        <h4>技术选型</h4>
                        <ul>
                            <li>核心框架：Spring Boot 2.x。  服务治理：Spring Cloud。</li>
                            <li>视图框架：Spring MVC 5.x。   持久层框架：MyBatis 3.x。</li>
                            <li>数据库连接池：Druid 1.x。    安全框架：Shiro 1.4.x。</li>
                            <li>前端框架：Vue.js 2.x。       页面组件：Element 2.x。</li>
                            <li>状态管理：Vuex.js 2.x。      后台交互：axios 0.18.x。</li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
        <el-alert title="介绍" type="success" show-icon :closable="false"></el-alert>
        <el-alert title="介绍" type="warning" show-icon :closable="false"></el-alert>
        <el-alert title="介绍" type="info" show-icon :closable="false"></el-alert>
        <el-alert title="介绍" type="error" show-icon :closable="false"></el-alert>
    </div>
</template>

<script>
export default {
    name:'home-index'
}
</script>

<style lang="css" scoped>
.home .icon-fa{
    position: relative;
    margin-top: 10px;
    padding-top:20%;
    padding-bottom:20%;
    width:100%;
    font-size:12px;
    overflow: hidden;
}
.home .iframe{
    position: absolute;
    left: 0;
    right: 0;
    top: 0;
    bottom: 0;
    width: 100%;
    height: 100%;
    background: #fff;
    overflow-y: hidden;
}
.home .prople .text {
    font-size: 14px;
}

.home .prople .item {
    margin-bottom: 18px;
}

.home .prople .clearfix:before,.home .prople .clearfix:after {
    display: table;
    content: "";
}
.home .prople .clearfix:after {
    clear: both
}

.home .prople .box-card {
    width: 30%;
}
</style>