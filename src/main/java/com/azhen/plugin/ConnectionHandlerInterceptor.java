package com.azhen.plugin;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Intercepts({@Signature(type=StatementHandler.class,method="prepare",args={Connection.class, Integer.class})})
public class ConnectionHandlerInterceptor implements Interceptor {
	private int limit;
	private String dbType;
	private static final String LMT_TABLE_NAME = "limit_Table_Name_xxx";

	private static ThreadLocal<SimpleDateFormat> dateTimeFormatterThreadLocal = new ThreadLocal<SimpleDateFormat>(){
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};

	private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;

	public Object intercept(Invocation invocation) throws Throwable {
		StatementHandler stmtHandler = (StatementHandler) invocation.getTarget();
		MetaObject metaStmtHandler = SystemMetaObject.forObject(stmtHandler);
		
		String sql = (String) metaStmtHandler.getValue("delegate.boundSql.sql");
		List<ParameterMapping> parameterMappings = (List<ParameterMapping>)metaStmtHandler.getValue("delegate.boundSql.parameterMappings");
		Map<String, Object> parameterObjects = (Map<String, Object> )metaStmtHandler.getValue("delegate.boundSql.parameterObject");

		List<String> parameters = new ArrayList<String>();
		for (ParameterMapping parameterMapping : parameterMappings) {
			Object parameter = findParameterObject(parameterMapping.getProperty(), parameterObjects);
			parameters.add(String.valueOf(parameter));
		}

		for (String value : parameters) {
			sql = sql.substring(0, sql.indexOf("?") + 1).replace("?", value) + sql.substring(sql.indexOf("?") + 1);
		}
		System.out.println(sql);
		return invocation.proceed();
	}

	private String findParameterObject(String property, Object parameterObject) throws Throwable {
		String[] names = property.split("\\.");
		for (int i = 0; i < names.length; i ++) {
			String name = names[i];
			if (Map.class.isAssignableFrom(parameterObject.getClass())) {
				Map<String, Object> paramMap = (Map<String, Object>) parameterObject;
				parameterObject = paramMap.get(name);
			} else {
				Class<?> paramClass = parameterObject.getClass();
				String getMethodName = "get" + name.substring(0,1).toUpperCase() + name.substring(1);
				Method getMethod = paramClass.getMethods()[0];
				parameterObject = getMethod.invoke(parameterObject);
			}
		}


		String result = parameterObject.toString();
		if (Date.class.isAssignableFrom(parameterObject.getClass())) {
			result = dateTimeFormatterThreadLocal.get().format((Date) parameterObject);
		}

		if (!Number.class.isAssignableFrom(parameterObject.getClass())) {
			result = "'" + result + "'";
		}
		return result;
	}

	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	public void setProperties(Properties properties) {
		String strLimit = (String) properties.getProperty("limit", "50");
		this.limit = Integer.parseInt(strLimit);
		this.dbType = (String) properties.getProperty("dbType", "mysql");
	}
}
