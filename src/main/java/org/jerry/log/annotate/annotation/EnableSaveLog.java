package org.jerry.log.annotate.annotation;

import org.jerry.log.annotate.enable.EnableLogImportSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({EnableLogImportSelector.class})
public @interface EnableSaveLog {

    boolean value() default false;

}
