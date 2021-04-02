package com.fxg.learning.security.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fxg.learning.security.domain.Role;
import com.fxg.learning.security.domain.User;
import com.fxg.learning.security.mapper.RoleMapper;
import com.fxg.learning.security.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>
 * 用户信息 service 实现类
 * </p>
 */
@Service
@Validated
public class UserService extends ServiceImpl<UserMapper, User> implements UserDetailsService {

	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	private UserMapper userMapper;
	@Autowired
	private RoleMapper roleMapper;

	@Override
	public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
		User user = userMapper.loadUserByUsername(s);
		if (user == null) {
			//避免返回null，这里返回一个不含有任何值的User对象，在后期的密码比对过程中一样会验证失败
			return new User();
		}
		//查询用户的角色信息，并返回存入user中
		List<Role> roles = roleMapper.getRolesByUserId(user.getId());
		Role userRole = new Role();
		userRole.setId(2);
		userRole.setRoleName("user");
		roles.add(userRole);//只要登录，就有user权限
		user.setRoles(roles);
		return user;
	}

	public User selectById(@NotNull Integer id) {
		return userMapper.selectById(id);
	}

	public User selectByName(@NotBlank String name) {
		return userMapper.loadUserByUsername(name);
	}

	public static void main(String[] args) {
		int[] mgt=new int[]{1,2,3,4,5,6,7,8,9};

		int n = mgt.length;
		int[] ints=new int[n];
		for (int i = 0; i < n; i++) {
			int j= (int)(0+Math.random()*n);
//			mgt[j];
		}
		System.out.println(ints);
	}
}
