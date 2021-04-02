package com.site.common.constant;

public class Constant {

    public static final int BYTE_BUFFER = 1024;

    public static final int BUFFER_MULTIPLE = 10;

    //验证码过期时间
    public static final Long PASS_TIME =  50000 * 60 *1000L;

    //根菜单节点
    public static final String ROOT_MENU = "0";

    //菜单类型，1：菜单  2：按钮操作
    public static final int TYPE_MENU = 1;

    //菜单类型，1：菜单  2：按钮操作
    public static final int TYPE_BUTTON = 2;

    //用户名登录
    public static final int LOGIN_USERNAME = 0;
    //手机登录
    public static final int LOGIN_MOBILE = 1;
    //邮箱登录
    public static final int LOGIN_EMAIL = 2;

    //启用
    public static final int ENABLE = 1;
    //禁用
    public static final int DISABLE = 0;

    public static class RoleType{
        //超级管理员
        public static final String SYS_ASMIN_ROLE= "sysadmin";
        //管理员
        public static final String ADMIN= "admin";
        //普通用户
        public static final String USER= "user";
    }

    public static class User{

        /**
         * user缓存key
         */
        public static final String USER_KEY = "user::";

        /**
         * user token 缓存key
         */
        public static final String USER_TOKEN_KEY = "user::token::";

        /**
         * user缓存key时长
         */
        public static final long USER_KEY_EXPIRE = 86400L;// 缓存一天

        /**
         * 权限缓存key
         */
        public static final String PERMISSION_KEY = "permission::";

        /**
         * token key
         */
        public static final String ACCESS_TOKEN = "authorization";

        /**
         * 用于从 token 中获取用户id
         */
        public static final String JWT_USER_ID = "jwt-user-id-key";
    }


    public static class FilePostFix{
        public static final String ZIP_FILE =".zip";

        public static final String[] IMAGES ={"jpg", "jpeg", "JPG", "JPEG", "gif", "GIF", "bmp", "BMP", "png"};
        public static final String[] ZIP ={"ZIP","zip","rar","RAR"};
        public static final String[] VIDEO ={"mp4","MP4","mpg","mpe","mpa","m15","m1v", "mp2","rmvb"};
        public static final String[] APK ={"apk","exe"};
        public static final String[] OFFICE ={"xls","xlsx","docx","doc","ppt","pptx"};

    }
    public class FileType{
        public static final int FILE_IMG = 1;
        public static final int FILE_ZIP = 2;
        public static final int FILE_VEDIO= 3;
        public static final int FILE_APK = 4;
        public static final int FIVE_OFFICE = 5;
        public static final String FILE_IMG_DIR= "/img/";
        public static final String FILE_ZIP_DIR= "/zip/";
        public static final String FILE_VEDIO_DIR= "/video/";
        public static final String FILE_APK_DIR= "/apk/";
        public static final String FIVE_OFFICE_DIR= "/office/";
    }

}
