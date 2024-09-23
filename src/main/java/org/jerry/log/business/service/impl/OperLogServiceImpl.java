package org.jerry.log.business.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jerry.log.config.LogProperties;
import org.jerry.log.business.dao.SysLogDao;
import org.jerry.log.business.entity.SysLog;
import org.jerry.log.toolkit.exception.TableNotExistException;
import org.jerry.log.business.service.IOperLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class OperLogServiceImpl implements IOperLogService {

    @Resource
    private SysLogDao  sysLogDao;

    @Resource
    private LogProperties conf;


    @Override
    public void save(SysLog sysLog) {
        try {
            if (sysLogDao.isTableExist(conf.getTableName())) {
                if (sysLogDao.save(sysLog)) {
                    log.info("日志记录成功！");
                } else {
                    log.error("日志记录失败！");
                }
            } else {
                log.error("日志表不存在存在！并开始创建！");
                throw new TableNotExistException("日志表不存在存在！并开始创建！");
            }
        } catch (Exception e) {
            log.error("Error occurred while checking table existence", e);
        }
    }

}
