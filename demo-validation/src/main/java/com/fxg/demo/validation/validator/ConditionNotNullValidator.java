package com.fxg.demo.validation.validator;

import com.fxg.demo.validation.annotation.ConditionNotNull;
import org.apache.commons.beanutils.PropertyUtils;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

/**
 * @ConditionNotNull 注解对应的校验器
 */
public class ConditionNotNullValidator implements ConstraintValidator<ConditionNotNull, Object> {

	private String targetFileName;
	private String dependFileNames[];
	private ConditionNotNull.ConditionLevel conditionLevel;

	@Override
	public void initialize(ConditionNotNull annotation) {
		this.targetFileName = annotation.targetFileName();
		this.dependFileNames = annotation.dependFileNames();
		this.conditionLevel = annotation.conditionLevel();
	}

	@Override
	public boolean isValid(Object o, ConstraintValidatorContext context) {
		int nullCount = 0;
		int notNullCount = 0;
		Object targetFile = null;
		try {
			targetFile = PropertyUtils.getProperty(o, this.targetFileName);
			for (String dependFileName : dependFileNames) {
				Object dependFile = PropertyUtils.getProperty(o, dependFileName);
				if (Objects.isNull(dependFile)){
					nullCount++;
				}else {
					notNullCount++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		switch (this.conditionLevel) {

			case ANY:
				if (notNullCount > 0) {
					return Objects.nonNull(targetFile);
				}
				break;
			case ALL:
				if (nullCount == 0) {
					return Objects.nonNull(targetFile);
				}
				break;
		}
		return true;
	}
}
