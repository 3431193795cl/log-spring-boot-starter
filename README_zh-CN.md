# log-spring-boot-starter

[English](./README.md) | 简体中文

## 这是什么?

这是一个能帮你无需再作日志记录的start组件

## 快速开始

在你的 pom.xml 文件中加入以下依赖：

```xml
<dependency>
    <groupId>io.github.3431193795cl</groupId>
    <artifactId>log-spring-boot-starter</artifactId>
    <version>1.0.1</version>
</dependency>
```

在你的数据库中加入一下数据表:
```sql
create table sys_log
(
    id             varchar(32)   not null comment '主键'
        primary key,
    title          varchar(32)   null comment '模块标题',
    content        varchar(225)  null comment '日志内容',
    method         varchar(100)  null comment '方法名称',
    request_method varchar(10)   null comment '请求方式',
    oper_name      varchar(50)   null comment '操作人员',
    request_url    varchar(255)  null comment '请求URL',
    oper_ip        varchar(128)  null comment '操作IP',
    ip_location    varchar(255)  null comment 'IP属地',
    request_param  varchar(2000) null comment '请求参数',
    response_param varchar(2000) null comment '响应参数',
    status         int           null comment '状态(0正常 1异常)',
    error_msg      varchar(2000) null comment '错误消息',
    oper_time      datetime      null comment '操作时间',
    take_time      bigint        null comment '方法执行耗时（单位：毫秒）',
    oper_id        varchar(50)   null comment '操作人员id'
)
    charset = utf8;
```
这就是你需要做的全部事情！

## 我们做了什么？

我们发现在日志记录的过程中，需要程序员去做切面等的操作去程序员自己去编写,

当然，你也可以通过自己编写一些代码来做到这一点。

## 配置

对于本地开发自测这种频繁，你可以通过在 application.yml，目前只支持Mysql数据库，设置以下配置，可在前端cookie或session中存储${user-info-key}(自定义)，需通过后端提供的对称加密算法进行加密 。

```yaml
log:
  driver-class-name: com.mysql.cj.jdbc.Driver
  jdbc-url: jdbc:mysql://localhost:3306/text_db?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
  username: localhost
  password: localhost
  table-name: sys_log
  is-get-user-info: true
  address: cookie
  encrypt-type: des3
  key: 111036369260679051122113
  user-info-key: userInfo
```
用户信息实例：
```json
{
  "user_id": "",
  "user_name": ""
}
```

## 联系我
邮箱: 3431193795@qq.com




