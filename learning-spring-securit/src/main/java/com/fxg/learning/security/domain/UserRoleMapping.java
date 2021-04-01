package com.fxg.learning.security.domain;

import java.io.Serializable;

public class UserRoleMapping implements Serializable {

	private static final long serialVersionUID = 1504516551461775062L;
	private Integer id;

	private Integer userId;

	private Integer roleId;

	private Integer roleName;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public Integer getRoleName() {
		return roleName;
	}

	public void setRoleName(Integer roleName) {
		this.roleName = roleName;
	}
}
