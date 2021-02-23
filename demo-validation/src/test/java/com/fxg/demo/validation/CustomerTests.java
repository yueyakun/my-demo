package com.fxg.demo.validation;

import com.fxg.demo.validation.annotation.group.Custom;
import com.fxg.demo.validation.annotation.group.New;
import com.fxg.demo.validation.annotation.group.NewAndUpdate;
import com.fxg.demo.validation.annotation.group.Update;
import com.fxg.demo.validation.domain.Address;
import com.fxg.demo.validation.domain.Customer;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

class CustomerTests {

	private static Validator validator;

	static {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	/**
	 * 测试notBlank注解
	 */
	@Test
	public void nameNotBlank() {
		Customer customer = new Customer();
		customer.setAge(19);
		Set<ConstraintViolation<Customer>> result = validator.validate(customer, Default.class);
		System.out.println("****************测试notBlank注解****************");
		this.printError(result);
	}

	/**
	 * 测试group作用1--实现部分校验（分组校验）
	 */
	@Test
	public void testValidationGroup1() {
		Customer customer = new Customer();
		Set<ConstraintViolation<Customer>> result = validator.validate(customer, New.class);
		System.out.println("****************测试group作用1--实现部分校验（分组校验）****************");
		this.printError(result);
	}

	/**
	 * 测试group作用2--控制校验顺序
	 */
	@Test
	public void testValidationGroup2() {
		Customer customer = new Customer();
		Set<ConstraintViolation<Customer>> result = validator.validate(customer, NewAndUpdate.class);
		System.out.println("****************测试group作用2--控制校验顺序****************");
		this.printError(result);
	}

	/**
	 * 测试级联校验1-对象
	 */
	@Test
	public void testCascadeValidation1() {
		Customer customer = new Customer();
		customer.setNickname("xiaohuihui");
		customer.setAddress(new Address());
		Set<ConstraintViolation<Customer>> result = validator.validate(customer, New.class);
		System.out.println("****************测试级联校验1-对象****************");
		this.printError(result);
	}

	/**
	 * 测试级联校验2-List
	 */
	@Test
	public void testCascadeValidation2() {
		ArrayList<Address> addressList = new ArrayList<>();
		addressList.add(new Address());
		Customer customer = new Customer();
		customer.setMemberNo("AAA");
		customer.setHistoryAddressList(addressList);
		Set<ConstraintViolation<Customer>> result = validator.validate(customer, Custom.class);
		System.out.println("****************测试级联校验2-List****************");
		this.printError(result);
	}

	/**
	 * 测试约束条件组合
	 */
	@Test
	public void testUnionValidation() {
		Customer customer = new Customer();
		customer.setId(0);
		Set<ConstraintViolation<Customer>> result = validator.validate(customer, Update.class);
		System.out.println("****************测试约束条件组合****************");
		this.printError(result);
	}

	/**
	 * 测试自定义注解@CheckCase
	 */
	@Test
	public void testCustomAnnotation() {
		Customer customer = new Customer();
		customer.setMemberNo("aaa");
		Set<ConstraintViolation<Customer>> result = validator.validate(customer, Custom.class);
		System.out.println("****************测试自定义注解@CheckCase****************");
		this.printError(result);
	}

	public void printError(Set<ConstraintViolation<Customer>> result) {
		System.out.println("错误数量为：" + result.size());
		if (!result.isEmpty()) {
			System.out.println("错误信息如下：");
			Iterator<ConstraintViolation<Customer>> iterator = result.iterator();
			while (iterator.hasNext()) {
				ConstraintViolation<Customer> next = iterator.next();
				System.out.println(
						"属性名：" + next.getPropertyPath() + ",校验结果：" + next.getMessage() + ",传入值：" + next.getInvalidValue());
			}
		}
	}
}
