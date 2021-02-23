package com.fxg.demo.validation.controller;

import com.fxg.demo.validation.domain.Address;
import com.fxg.demo.validation.service.AddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/testAOP")
public class TestAOPValidationController {

	@Autowired
	private AddressService addressService;

	//service 校验不过，报ConstraintViolationException
	@GetMapping(value = "/test/notEmpty")
	public String testNotEmpty() {
		addressService.testNotEmpty("");
		return "success";
	}

	//service 校验不过，报ConstraintViolationException
	@GetMapping(value = "/test/object")
	public String testObject() {
		addressService.testObject(new Address());
		return "success";
	}

	//由于基于AOP实现，所以类内部调用是不会触发validation的
	@GetMapping(value = "/test/aop")
	public String testAOP() {
		addressService.testAOP("");
		return "success";
	}
}
