package com.fxg.demo.validation.annotation;


import com.fxg.demo.validation.validator.CheckCaseValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//校验是否全为大写或小写
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckCaseValidator.class)
public @interface CheckCase {
	String message() default "{custom.constraints.checkCase.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	CaseMode mode() default CaseMode.UPPER;

	enum CaseMode {
		UPPER("UPPER", (v) -> v == null ? false : v.equals(v.toUpperCase())),
		LOWER("LOWER", (v) -> v == null ? false : v.equals(v.toLowerCase()));

		private String code;
		private CaseModeAdapter adapter;

		CaseMode(String code, CaseModeAdapter adapter) {
			this.code = code;
			this.adapter = adapter;
		}

		public String getCode() {
			return code;
		}

		public CaseModeAdapter getAdapter() {
			return adapter;
		}

		public interface CaseModeAdapter {
			Boolean adaptive(String v);
		}
	}
}

