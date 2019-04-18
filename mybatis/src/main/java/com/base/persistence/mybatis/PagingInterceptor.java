package com.base.persistence.mybatis;

import java.util.Map;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

@Intercepts({@Signature(type = Executor.class, method = "query",
    args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
@Component
public class PagingInterceptor extends AbstractInterceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement)invocation.getArgs()[0];
        if (SqlCommandType.SELECT != mappedStatement.getSqlCommandType()) {
            return invocation.proceed();
        }

        Object parameter = invocation.getArgs()[1];

        BoundSql boundSql = null;
        Integer pageIndex = null;
        Integer pageSize = null;
        Object example = null;
        Map<String, Object> paramsMap = null;
        if (parameter != null && parameter instanceof Map) {
            pageIndex = safeGet((Map<?, ?>)parameter, BasePersistenceMybatisConst.BASEDAO_PAGING_INDEX);
            pageSize = safeGet((Map<?, ?>)parameter, BasePersistenceMybatisConst.BASEDAO_PAGING_SIZE);
            example = safeGet((Map<?, ?>)parameter, BasePersistenceMybatisConst.BASEDAO_PAGING_EXAMPLE);
            paramsMap = safeGet((Map<?, ?>)parameter, BasePersistenceMybatisConst.BASEDAO_PAGING_CRITERIA);
        }

        if (pageIndex != null && pageSize != null) {
            boundSql = mappedStatement.getBoundSql(example == null ? parameter : example);
            String originalSql = boundSql.getSql().trim();

            StringBuffer sb = new StringBuffer();
            int offset = pageIndex * pageSize;

            String newSql
                = sb.append(originalSql).append(" limit ").append(offset).append(",").append(pageSize).toString();
            MappedStatement newMs = createNewMappedStatement(mappedStatement, boundSql, newSql);
            invocation.getArgs()[0] = newMs;
        }

        if (example != null || paramsMap != null) {
            invocation.getArgs()[1] = example == null ? paramsMap : example;
        }

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        }

        return target;
    }

}