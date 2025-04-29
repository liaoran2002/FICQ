package org.lc.ficq.annotation;

import org.lc.ficq.enums.ListenerType;
import org.springframework.stereotype.Component;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Listener {
    ListenerType type();
}
