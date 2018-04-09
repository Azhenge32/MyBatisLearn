package com.azhen.mapper;

import com.azhen.domain.Role;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * Created by Azhen on 2017/7/20.
 */
public interface RoleMapper {
    Role getRole(Long id);

    @Select("select * from role")
    List<Role> find();

    List<Role> findByMap(@Param("map") Map<String, Object> param);

    int saveRole(Map<String, Object> param);

    int saveRoleByMap(@Param("map") Map<String, Object> param);
}
