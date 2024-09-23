package org.jerry.log.annotate.enable;


import lombok.extern.slf4j.Slf4j;
import org.jerry.log.config.LogProperties;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Map;

@Slf4j
public class EnableLogCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata annotatedTypeMetadata) {
        //获取注解属性
        Map<String, Object> attrs = annotatedTypeMetadata.getAnnotationAttributes(LogProperties.class.getName());
        //获取系统值
        String system = String.valueOf(attrs.get("enable"));
        String currentOs = System.getProperty("os.name");
        boolean result = currentOs.contains(system);
        log.info("********currentOs={}匹配结果是={}", currentOs, result);
        return result;

    }
}
