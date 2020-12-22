package com.fxg.archetype.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fxg.archetype.domain.User;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 用户信息表 Mapper 接口
 * </p>
 *
 * @author yueyakun
 * @since 2020-04-24
 */
public interface UserMapper extends BaseMapper<User> {

	User loadUserByUsername(@Param("username") String username);
}
