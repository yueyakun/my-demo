package com.fxg.demo.validation.annotation.group;



import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//校验是否符合主键id要求，不为null，大于等于1
@Min(1)
@NotNull
@ReportAsSingleViolation
@Constraint(validatedBy = {})
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
public @interface KeyId {
	String message() default "{custom.constraints.KeyId.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}

