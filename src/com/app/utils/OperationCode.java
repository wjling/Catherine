package com.app.utils;

public class OperationCode {
    
    public static final int REGISTER = 0;   // 注册
    public static final int LOGIN = 1;      // 登陆
    public static final int UPLOAD_CONTACT_BOOK = 2;  // 上传通讯录
    public static final int FRIEND_RECOMMEND = 3;     // 好友推荐
    public static final int ADD_FRIEND = 4;           // 添加好友
    public static final int CHANGE_SETTINGS = 5;      // 更改设置
    public static final int LAUNCH_EVENT = 6;         // 发起活动
    public static final int GET_EVENTS = 7;           // 获取活动
    public static final int GET_EVENT_CONTENT = 8;    // 获取活动具体信息
    public static final int GET_EVENT_POST = 9;       // 获取活动帖子
    public static final int GET_POST_CONTENT = 10;    // 获取帖子具体内容
    public static final int ADD_POST = 11;            // 发起一个帖子
    public static final int DELETE_POST = 12;     // 删除一个帖子
    public static final int GET_POST_COMMENT = 13;      // 获取帖子的评论
    public static final int ADD_COMMENT = 14;    // 在帖子上评论
    public static final int DELETE_COMMENT = 15;  // 删除一条评论
    public static final int SEARCH_EVENT = 16;         // 查找活动
    public static final int GET_SALT_VALUE = 17;      // 获取盐值
    public static final int SYNCHRONIZE = 18;         // 同步好友列表
    public static final int SEARCH_FRIEND = 19;		// 搜索好友
    public static final int PARTICIPATE_EVENT = 20;   // 参加活动（请求、同意或拒绝）
    public static final int INVITE_FRIENDS = 21;             // 活动发起者邀请好友参加活动
    public static final int NOTE = 22;                                 // 记事本功能
    public static final int UPLOAD_AVATAR = 23;        // 上传头像
    public static final int GET_AVATAR = 24;                 // 获取头像
    public static final int LOGOUT = 25;                              // 登出
}
