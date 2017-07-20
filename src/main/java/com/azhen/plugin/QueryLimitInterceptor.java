package com.azhen.plugin;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;

import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.sql.Connection;
import java.util.Properties;

@Intercepts({ @Signature(type = Executor.class, method = "get",
args = { Connection.class}) })
public class QueryLimitInterceptor implements Interceptor {
	private int limit;
	private String dbType;
	private static final String LMT_TABLE_NAME = "limit_Table_Name_xxx";

	public Object intercept(Invocation invocation) throws Throwable {
		StatementHandler stmtHandler = (StatementHandler) invocation.getTarget();
		MetaObject metaStmtHandler = SystemMetaObject.forObject(stmtHandler);
		
		while (metaStmtHandler.hasGetter("h")) {
			Object object = metaStmtHandler.getValue("h");
			metaStmtHandler = SystemMetaObject.forObject(object);
		}
		
		while (metaStmtHandler.hasGetter("target")) {
			Object object = metaStmtHandler.getValue("target");
			metaStmtHandler = SystemMetaObject.forObject(object);
		}
		
		String sql = (String) metaStmtHandler.getValue("delegate.boundSql.sql");
		System.out.println(sql);
		String limitSql;
		if ("mysql".equals(this.dbType)
				&& sql.indexOf("LMT_TABLE_NAME") == -1) {
			sql = sql.trim();
			limitSql = "select * from ( " + sql + " ) " + LMT_TABLE_NAME + " limit " + limit;
			metaStmtHandler.setValue("delegate.boundSql.sql", limitSql);
		}
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
