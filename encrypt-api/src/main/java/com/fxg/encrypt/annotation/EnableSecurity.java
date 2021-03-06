package com.fxg.encrypt.annotation;

import com.fxg.encrypt.SecretKeyConfig;
import com.fxg.encrypt.advice.DecryptRequestBodyAdvice;
import com.fxg.encrypt.advice.EncryptResponseBodyAdvice;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import({SecretKeyConfig.class,
        EncryptResponseBodyAdvice.class,
        DecryptRequestBodyAdvice.class})
public @interface EnableSecurity{

}
