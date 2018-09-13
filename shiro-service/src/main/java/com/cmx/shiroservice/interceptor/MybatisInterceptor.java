package com.cmx.shiroservice.interceptor;

import java.sql.Connection;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import com.cmx.shiroservice.util.ReflectUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;

@Slf4j
@Intercepts({@Signature(type=StatementHandler.class,method="prepare",args={Connection.class, Integer.class}) })
public class MybatisInterceptor implements Interceptor {

    @SuppressWarnings("unused")
    private Properties properties;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
//        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
//        Object parameter = null;
//        if (invocation.getArgs().length > 1) {
//            parameter = invocation.getArgs()[1];
//        }
//        String sqlId = mappedStatement.getId();
//        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
//        Configuration configuration = mappedStatement.getConfiguration();
//        Object returnValue = null;
//        long start = System.currentTimeMillis();
//        returnValue = invocation.proceed();
//        long end = System.currentTimeMillis();
//        long time = (end - start);
//        String sql = getSql(configuration, boundSql, sqlId, time);
//        log.info(sql);
//        return returnValue;
        Object result = null;

        if(invocation.getTarget() instanceof RoutingStatementHandler){
            RoutingStatementHandler statementHandler = (RoutingStatementHandler)invocation.getTarget();
            StatementHandler delegate = (StatementHandler) ReflectUtils.getFieldValue(statementHandler, "delegate");
            BoundSql boundSql = delegate.getBoundSql();
            Object obj = boundSql.getParameterObject();
            //通过反射获取delegate父类BaseStatementHandler的mappedStatement属性
            MappedStatement mappedStatement = (MappedStatement)ReflectUtils.getFieldValue(delegate, "mappedStatement");
            String sqlId = mappedStatement.getId();
            Configuration configuration = mappedStatement.getConfiguration();
            //获取当前要执行的Sql语句，也就是我们直接在Mapper映射语句中写的Sql语句
            long start = System.currentTimeMillis();
            result =  invocation.proceed();
            long end = System.currentTimeMillis();
            long time = (end - start);
            String sql = getSql(configuration, boundSql, sqlId, time);
            log.info(sql);
        }
        return result == null? result : invocation.proceed();

    }

    public static String getSql(Configuration configuration, BoundSql boundSql, String sqlId, long time) {
        String sql = showSql(configuration, boundSql);
        StringBuilder str = new StringBuilder();
        str.append(sqlId);
        str.append(":");
        str.append(sql);
        str.append(": 执行耗时");
        str.append(time);
        str.append("ms");
        return str.toString();
    }

    private static String getParameterValue(Object obj) {
        String value = null;
        if (obj instanceof String) {
            value = "'" + obj.toString() + "'";
        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            value = "'" + formatter.format(new Date()) + "'";
        } else {
            if (obj != null) {
                value = obj.toString();
            } else {
                value = "";
            }

        }
        return value;
    }

    public static String showSql(Configuration configuration, BoundSql boundSql) {
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        if (parameterMappings.size() > 0 && parameterObject != null) {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst("\\?", getParameterValue(parameterObject));

            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    }
                }
            }
        }
        return sql;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    @Override
    public void setProperties(Properties properties0) {
        this.properties = properties0;
    }
}
