package com.fxg.demo.validation.controller;

import com.fxg.demo.validation.annotation.group.New;
import com.fxg.demo.validation.domain.Address;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/address")
public class AddressController {

	//request param 校验 错误信息直接向外抛出，报500
	@GetMapping(value = "/test/notNull")
	public String testNotNull(@NotNull Integer id) {
		log.info("id is:{}", id);
		return "success";
	}

	//request param 校验 错误信息直接向外抛出，报500
	@GetMapping(value = "/test/notEmpty")
	public String testNotEmpty(@NotEmpty String name) {
		log.info("name is:{}", name);
		return "success";
	}

	//request body 校验，错误信息封装入bindingResult
	@PostMapping(value = "/test/body", consumes = "application/json;charset=UTF-8")
	public String testBody(@RequestBody @Validated(New.class) Address address, BindingResult bindingResult) {
		log.info("address is:{}", address);
		int errorCount = bindingResult.getErrorCount();
		log.info("errorCount is:{}", errorCount);
		return "success";
	}

	//request body 校验，错误信息直接抛出，报400
	@PostMapping(value = "/test/body2", consumes = "application/json;charset=UTF-8")
	public String testBody2(@RequestBody @Valid Address address) {
		log.info("address is:{}", address);
		return "success";
	}
}
