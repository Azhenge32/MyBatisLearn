package com.azhen.plugin;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;

import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.sql.Connection;
import java.util.Properties;

@Intercepts({@Signature(type=StatementHandler.class,method="prepare",args={Connection.class, Integer.class})})
public class ConnectionHandlerInterceptor implements Interceptor {
	private int limit;
	private String dbType;
	private static final String LMT_TABLE_NAME = "limit_Table_Name_xxx";

	public Object intercept(Invocation invocation) throws Throwable {
		StatementHandler stmtHandler = (StatementHandler) invocation.getTarget();
		MetaObject metaStmtHandler = SystemMetaObject.forObject(stmtHandler);
		
		String sql = (String) metaStmtHandler.getValue("delegate.boundSql.sql");

		System.out.println(sql);
		return invocation.proceed();
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
