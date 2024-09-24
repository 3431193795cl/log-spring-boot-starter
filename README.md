# log-spring-boot-starter

English | [简体中文](./README_zh-CN.md)

## What is this?

This is a start component that can help you stop logging.

## Quick Start

Add the following dependency to your `pom.xml` file:

```xml
<dependency>
    <groupId>io.github.3431193795cl</groupId>
    <artifactId>log-spring-boot-starter</artifactId>
    <version>1.0.1</version>
</dependency>
```
Add a data table to your database:
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

That's all you need to do!

## What have we done?

We found that in the process of logging, programmers need to do operations such as cutting planes and write them themselves.

Of course, you can also do this by writing some code yourself.

## configure

For the frequent self-test of local development, you can set the following configuration in application.yml, which currently only supports Mysql database. You can store ${user-info-key} (user-defined) in the front-end cookie or session, and you need to encrypt it through the symmetric encryption algorithm provided by the back-end.
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
Example of user information:
```json
{
  "user_id": "",
  "user_name": ""
}
```

## benefaction
<img height="600px" src="static_file/alibaba.jpg" width="300px"/>

<img height="600px" src="static_file/wechat.jpg" width="300px"/>

## Contact me
talphone/wechat: 15117286104