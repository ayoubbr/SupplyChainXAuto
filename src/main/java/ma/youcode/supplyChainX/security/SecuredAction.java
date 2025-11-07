package ma.youcode.supplyChainX.security;

import ma.youcode.supplyChainX.shared.enums.Role;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SecuredAction {
    Role[] roles() default {};
}
