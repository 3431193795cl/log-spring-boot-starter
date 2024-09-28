package org.jerry.log.config;

import lombok.Data;
import org.jerry.log.toolkit.enums.EncryptEnum;
import org.jerry.log.toolkit.enums.StorageAddress;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "log")
public class LogProperties {

    /**
     * 是否开启
     */
    private Boolean enable = false;

//    /**
//     * 数据库地址
//     */
//    private String jdbcUrl;
//
//    /**
//     * 数据库驱动
//     */
//    private String driverClassName;
//
//    /**
//     * 数据库账号
//     */
//    private String username;
//
//    /**
//     * 密码
//     */
//    private String password;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 是否获取用户信息
     * 用户信息实例：
     * {
     *     "user_id":"xxxxxxxxxx",
     *     "user_name":"xxx"
     * }
     */
    private Boolean isGetUserInfo;

    /**
     * 用户信息key
     */
    private String userInfoKey;

    /**
     * 用户信息存放地址 默认cookie
     */
    private StorageAddress address = StorageAddress.COOKIE;

    /**
     * 加密类型 默认AES
     */
    private EncryptEnum encryptType = EncryptEnum.AES;

    /**
     * 密钥
     */
    private String key;

}
