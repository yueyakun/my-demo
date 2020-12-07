package com.fxg.springboot.redis.domain;

import java.io.Serializable;

public class User implements Serializable {

	private static final long serialVersionUID = -2357919541317066490L;

	private Integer id;

	private String name;

	public User() {
	}

	public User(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "User{" + "id=" + id + ", name='" + name + '\'' + '}';
	}
}
