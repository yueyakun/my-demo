package com.fxg.learning.security.domain;


import java.io.Serializable;

public class Role implements Serializable {

	private static final long serialVersionUID = -134293691228491903L;

	private Integer id;

	private String roleName;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
}
