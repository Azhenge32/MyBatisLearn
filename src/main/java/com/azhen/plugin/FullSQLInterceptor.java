package com.azhen.plugin;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Component
@Intercepts({@Signature(type=StatementHandler.class,method="prepare",args={Connection.class, Integer.class})})
public class FullSQLInterceptor implements Interceptor {
	private static final Logger LOGGER = LoggerFactory.getLogger(FullSQLInterceptor.class);
	private Configuration configuration;
	private TypeHandlerRegistry typeHandlerRegistry;
	private static ThreadLocal<SimpleDateFormat> dateTimeFormatter = new ThreadLocal<SimpleDateFormat>(){
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};

	public Object intercept(Invocation invocation) throws Throwable {
		StatementHandler stmtHandler = (StatementHandler) invocation.getTarget();
		MetaObject metaStmtHandler = SystemMetaObject.forObject(stmtHandler);
		BoundSql boundSql = (BoundSql) metaStmtHandler.getValue("delegate.boundSql");
		String sql = boundSql.getSql();
		List<String> parameters = new ArrayList<>();
		Object parameterObject = metaStmtHandler.getValue("delegate.boundSql.parameterObject");
		List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
		if (parameterMappings != null) {
			MetaObject metaObject = parameterObject == null ? null : configuration.newMetaObject(parameterObject);
			for (int i = 0; i < parameterMappings.size(); i++) {
				ParameterMapping parameterMapping = parameterMappings.get(i);
				if (parameterMapping.getMode() != ParameterMode.OUT) {
					//  参数值
					Object value;
					String propertyName = parameterMapping.getProperty();
					//  获取参数名称
					if (boundSql.hasAdditionalParameter(propertyName)) {
						// 获取参数值
						value = boundSql.getAdditionalParameter(propertyName);
					} else if (parameterObject == null) {
						value = null;
					} else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
						// 如果是单个值则直接赋值
						value = parameterObject;
					} else {
						value = metaObject == null ? null : metaObject.getValue(propertyName);
					}

					if (value instanceof Number) {
						parameters.add(String.valueOf(value));
					} else {
						StringBuilder builder = new StringBuilder();
						builder.append("'");
						if (value instanceof Date) {
							builder.append(dateTimeFormatter.get().format((Date) value));
						} else if (value instanceof String) {
							builder.append(value);
						}
						builder.append("'");
						parameters.add(builder.toString());
					}
				}
			}
		}

		for (String value : parameters) {
			sql = sql.replaceFirst("\\?", value);
		}
		LOGGER.info(sql);
		return invocation.proceed();
	}

	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	public void setProperties(Properties properties) {
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public void setTypeHandlerRegistry(TypeHandlerRegistry typeHandlerRegistry) {
		this.typeHandlerRegistry = typeHandlerRegistry;
	}
}
