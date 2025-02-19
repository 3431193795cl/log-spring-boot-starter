package org.jerry.log.business.dao;

import org.jerry.log.business.entity.SysLog;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class SysLogDao {
    @Resource
    private JdbcTemplate jdbcTemplate;

    public Boolean save(SysLog sysLog) {
        String sql = "INSERT INTO `sys_log` (" +
                "    `id`, `title`, `content`, `method`, `request_method`, `oper_name`, " +
                "    `request_url`, `oper_ip`, `ip_location`, `request_param`, " +
                "    `response_param`, `status`, `error_msg`, `oper_time`, `take_time`, `oper_id`" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        // 2 调用方法实现
        Object[] args = {sysLog.getId(),
                sysLog.getTitle(), sysLog.getContent(), sysLog.getMethod(), sysLog.getRequestMethod(),
                sysLog.getOperName(), sysLog.getRequestUrl(), sysLog.getOperIp(), sysLog.getIpLocation(),
                sysLog.getRequestParam(), sysLog.getResponseParam(), sysLog.getStatus(), sysLog.getErrorMsg(),
                sysLog.getOperTime(), sysLog.getTakeTime(), sysLog.getOperId()};
        return jdbcTemplate.update(sql, args) >
                0;
    }

    /**
     * 检查指定名称的表是否存在
     *
     * @param tableName 要检查的表名称
     * @return 如果表存在返回true，否则返回false
     */
    public Boolean isTableExist(String tableName) {
        // 定义查询语句，用于查询指定名称的表是否存在
        String sql = "SHOW TABLES LIKE ?";
        try {
            // 使用JdbcTemplate执行查询，并通过ResultSetExtractor提取结果
            return jdbcTemplate.query(sql, new Object[]{tableName}, new ResultSetExtractor<Boolean>() {
                @Override
                public Boolean extractData(ResultSet rs) throws SQLException, DataAccessException {
                    // 如果查询结果集有下一行，说明表存在，返回true，否则返回false
                    return rs.next();
                }
            });
        } catch (Exception e) {
            // 记录异常信息
            System.err.println("Error checking table existence: " + e.getMessage());
            throw e; // 或者可以根据具体情况进行处理
        }
    }



    public void generateTable(String tableName) {
        // 初始化DDL语句构建器
        jdbcTemplate.execute("create table "+tableName+"\n" +
                "(\n" +
                "    id             int  not null comment '主键'\n" +
                "        primary key,\n" +
                "    title          varchar(32)   null comment '模块标题',\n" +
                "    content        varchar(225)  null comment '日志内容',\n" +
                "    method         varchar(100)  null comment '方法名称',\n" +
                "    request_method varchar(10)   null comment '请求方式',\n" +
                "    oper_name      varchar(50)   null comment '操作人员',\n" +
                "    request_url    varchar(255)  null comment '请求URL',\n" +
                "    oper_ip        varchar(128)  null comment '操作IP',\n" +
                "    ip_location    varchar(255)  null comment 'IP属地',\n" +
                "    request_param  varchar(2000) null comment '请求参数',\n" +
                "    response_param varchar(2000) null comment '响应参数',\n" +
                "    status         int           null comment '状态(0正常 1异常)',\n" +
                "    error_msg      varchar(2000) null comment '错误消息',\n" +
                "    oper_time      datetime      null comment '操作时间',\n" +
                "    take_time      bigint        null comment '方法执行耗时（单位：毫秒）',\n" +
                "    oper_id        varchar(50)   null comment '操作人员id'\n" +
                ")\n" +
                "    charset = utf8;");
    }
}
