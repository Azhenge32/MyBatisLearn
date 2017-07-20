package com.azhen.mapper;

import com.azhen.domain.Role;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by Azhen on 2017/7/20.
 */
public interface RoleMapper {
    Role getRole(Long id);

    @Select("select * from role")
    List<Role> find();
}
