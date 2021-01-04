package com.fxg.encrypt.annotation;

import com.fxg.configs.SecretKeyConfig;
import com.fxg.encrypt.advice.EncryptRequestBodyAdvice;
import com.fxg.encrypt.advice.EncryptResponseBodyAdvice;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Author:Bobby
 * DateTime:2019/4/9 16:44
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import({SecretKeyConfig.class,
        EncryptResponseBodyAdvice.class,
        EncryptRequestBodyAdvice.class})
public @interface EnableSecurity{

}
