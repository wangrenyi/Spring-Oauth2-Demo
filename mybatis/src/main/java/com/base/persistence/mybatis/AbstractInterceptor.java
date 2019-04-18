package com.base.persistence.mybatis;

import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.MappedStatement.Builder;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;

public abstract class AbstractInterceptor implements Interceptor {

    protected class BoundSqlSqlSource implements SqlSource {
        BoundSql boundSql;

        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        @Override
        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

    protected BoundSql copyFromBoundSql(MappedStatement ms, BoundSql boundSql, String sql) {
        BoundSql newBoundSql
            = new BoundSql(ms.getConfiguration(), sql, boundSql.getParameterMappings(), boundSql.getParameterObject());
        for (ParameterMapping mapping : boundSql.getParameterMappings()) {
            String prop = mapping.getProperty();
            if (boundSql.hasAdditionalParameter(prop)) {
                newBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));
            }
        }
        return newBoundSql;
    }

    protected MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        Builder builder = new Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());

        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        builder.keyProperty(StringUtils.join(ms.getKeyProperties(), ","));
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());

        return builder.build();
    }

    protected MappedStatement createNewMappedStatement(MappedStatement mappedStatement, BoundSql boundSql, String newSql) {
        BoundSql newBoundSql = copyFromBoundSql(mappedStatement, boundSql, newSql);
        MappedStatement newMs = copyFromMappedStatement(mappedStatement, new BoundSqlSqlSource(newBoundSql));
        return newMs;
    }

    @SuppressWarnings("unchecked")
    protected <T> T safeGet(Map<?, ?> map, String key) {
        try {
            return (T)map.get(key);
        } catch (BindingException ex) {
            return null;
        }
    }

}
