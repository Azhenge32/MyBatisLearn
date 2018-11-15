package com.azhen.mapper;


import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SaurerRepository {
    void save(@Param("table") String table, @Param("columns") List<String> columns, @Param("valueList") List<List<Object>> list);
}
