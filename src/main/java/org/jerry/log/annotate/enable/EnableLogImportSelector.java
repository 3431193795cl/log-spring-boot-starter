package org.jerry.log.annotate.enable;

import org.jerry.log.annotate.annotation.EnableSaveLog;
import org.jerry.log.config.LogAutoConfigure;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;
import java.util.function.Predicate;

public class EnableLogImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        //获取注解上的属性
        Map<String, Object> annotationAttributes = annotationMetadata.getAnnotationAttributes(EnableSaveLog.class.getName());
        Boolean value = (Boolean) annotationAttributes.get("value");
        return new String[]{value ? LogAutoConfigure.class.getName() : null};
    }

    @Override

    public Predicate<String> getExclusionFilter() {
        return ImportSelector.super.getExclusionFilter();
    }

}
