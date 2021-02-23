package com.fxg.demo.validation.domain;

import com.fxg.demo.validation.annotation.CheckCase;
import com.fxg.demo.validation.annotation.ConditionNotNull;
import com.fxg.demo.validation.annotation.group.*;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Data
@ConditionNotNull(targetFileName = "birthDay",dependFileNames = {"age"},groups = {Custom.class})
public class Customer {

	@KeyId(groups = {Update.class})
	private Integer id;

	@CheckCase(mode = CheckCase.CaseMode.UPPER,groups = {Custom.class})
	private String memberNo;

	@NotEmpty(message = "nickname 不能为空", groups = {New.class})
	@Size.List({
			@Size(min=8,max=20,message = "普通用户昵称长度最少为8，最多为20"),
			@Size(min=4,max=30,groups = {VIP.class},message = "VIP用户昵称长度最少为4，最多为30")})
	private String nickname;

	@Pattern(regexp = "^([1][3,4,5,6,7,8,9])\\d{9}$")
	private String telephoneNo;

	@NotBlank
	private String name;

	private Integer age;

	private LocalDate birthDay;

	@Valid
	private Address address;

	@Valid
	private List<Address> historyAddressList;
}
