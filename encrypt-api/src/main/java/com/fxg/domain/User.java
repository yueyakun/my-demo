package com.fxg.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.fxg.enums.Gender;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户信息
 * </p>
 *
 * @author yueyakun
 * @since 2020-04-24
 */
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;

	private String username;

	/**
	 * 密码
	 */
	private String password;

	/**
	 * 昵称
	 */
	private String nickName;

	/**
	 * 加密手机号密文
	 */
	private String mobile;

	/**
	 * 性别
	 */
	private Gender gender;

	/**
	 * 微信OpenId
	 */
	private String openId;

	/**
	 * 微信UnionID
	 */
	private String unionId;

	/**
	 * 加密邮箱密文
	 */
	private String email;

	/**
	 * 脱敏手机号
	 */
	private String maskedMobile;

	/**
	 * 脱敏邮箱
	 */
	private String maskedEmail;

	/**
	 * 头像存储ID
	 */
	private Integer pictureId;

	/**
	 * 生日
	 */
	private LocalDate birthday;

	@JSONField(serialize = false)
	private Integer version;

	@JSONField(serialize = false)
	private Boolean enabled;

	@JSONField(serialize = false)
	private Integer enabledId;

	private LocalDateTime createTime;

	private LocalDateTime lastTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getUnionId() {
		return unionId;
	}

	public void setUnionId(String unionId) {
		this.unionId = unionId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMaskedMobile() {
		return maskedMobile;
	}

	public void setMaskedMobile(String maskedMobile) {
		this.maskedMobile = maskedMobile;
	}

	public String getMaskedEmail() {
		return maskedEmail;
	}

	public void setMaskedEmail(String maskedEmail) {
		this.maskedEmail = maskedEmail;
	}

	public Integer getPictureId() {
		return pictureId;
	}

	public void setPictureId(Integer pictureId) {
		this.pictureId = pictureId;
	}

	public LocalDate getBirthday() {
		return birthday;
	}

	public void setBirthday(LocalDate birthday) {
		this.birthday = birthday;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Integer getEnabledId() {
		return enabledId;
	}

	public void setEnabledId(Integer enabledId) {
		this.enabledId = enabledId;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}

	public LocalDateTime getLastTime() {
		return lastTime;
	}

	public void setLastTime(LocalDateTime lastTime) {
		this.lastTime = lastTime;
	}
}
