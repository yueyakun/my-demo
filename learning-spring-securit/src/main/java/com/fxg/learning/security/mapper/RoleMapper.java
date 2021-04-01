package com.fxg.learning.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fxg.learning.security.domain.Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 角色信息表 Mapper 接口
 * </p>
 *
 * @author yueyakun
 * @since 2020-04-24
 */
public interface RoleMapper extends BaseMapper<Role> {

	List<Role> getRolesByUserId(@Param("userId") Integer userId);

}
