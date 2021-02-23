package com.fxg.demo.validation.service;

import com.fxg.demo.validation.domain.Address;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Slf4j
@Service
@Validated
public class AddressService {

	public String testNotEmpty(@NotEmpty String name) {
		log.info("name is:{}", name);
		return "success";
	}


	public String testObject(@Valid Address address) {
		log.info("address is:{}", address);
		return "success";
	}

	public String testAOP(String name) {
		log.info("name is:{}", name);
		this.testNotEmpty(name);
		return "success";
	}
}
