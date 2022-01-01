package com.sustart.shdsystem;

import android.app.Application;

import com.sustart.shdsystem.entity.User;

/**
 * 安卓的核心应用类。
 * 这里的作用是用来保存当前登录用户的信息，供其他模块获取当前用户的相关信息
 */
public class SHDSystemApplication extends Application {

    private static SHDSystemApplication instance;
    //当前登录用户
    public User loginUser;


    public static SHDSystemApplication getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
