package org.jerry.log.config;

import lombok.extern.slf4j.Slf4j;
import org.jerry.log.annotate.aspect.OperLogAspect;
import org.jerry.log.business.dao.SysLogDao;
import org.jerry.log.business.service.IOperLogService;
import org.jerry.log.business.service.impl.OperLogServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Slf4j
@Configuration
@ConditionalOnClass(value = {SysLogDao.class,IOperLogService.class, OperLogServiceImpl.class})
@EnableConfigurationProperties(LogProperties.class)//ioc注入
public class LogAutoConfigure {

    @Resource
    private Environment environment;


    @Bean
    @ConditionalOnMissingBean
    IOperLogService startService() {
        log.info("逻辑操作层注入成功！");
        return new OperLogServiceImpl();
    }

    @Bean
    SysLogDao sysLogDao() {
        log.info("数据操作层注入成功！");
        return new SysLogDao();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    DataSource dataSource() {
        log.info("数据库注入成功！");
        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName(environment.getRequiredProperty("spring.datasource.driver-class-name"));
        dataSourceBuilder.url(environment.getRequiredProperty("spring.datasource.url"));
        dataSourceBuilder.username(environment.getRequiredProperty("spring.datasource.username"));
        dataSourceBuilder.password(environment.getRequiredProperty("spring.datasource.password"));
        return dataSourceBuilder.build();
    }

    @Bean
    OperLogAspect operLogAspect() {
        log.info("操作日志切面注入成功！");
        return new OperLogAspect();
    }
}
