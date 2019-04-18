package com.base.persistence.mybatis;

import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
@Component
public class OptimisticLockInterceptor extends AbstractInterceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement)args[0];
        if (SqlCommandType.UPDATE != ms.getSqlCommandType()) {
            return invocation.proceed();
        }

        Object param = args[1];

        String methodId = ms.getId();
        String methodName = methodId.substring(methodId.lastIndexOf(".") + 1);

        if (isUpdateByPrimaryKey(methodName)) {
            Method methodGetVersion = ReflectionUtils.findMethod(param.getClass(), "getVersion");
            Integer originalVersion = (Integer)ReflectionUtils.invokeMethod(methodGetVersion, param);

            if (originalVersion == null) {
                return invocation.proceed();
            }

            Method methodSetVersion = ReflectionUtils.findMethod(param.getClass(), "setVersion", Integer.class);
            ReflectionUtils.invokeMethod(methodSetVersion, param, originalVersion + 1);

            BoundSql boundSql = ms.getBoundSql(param);
            String originalSql = boundSql.getSql().trim();
            String newSql = originalSql + " and version=? ";
            boundSql.getParameterMappings()
                .add(new ParameterMapping.Builder(ms.getConfiguration(), "originalVersion", Integer.class).build());
            boundSql.setAdditionalParameter("originalVersion", originalVersion);

            invocation.getArgs()[0] = createNewMappedStatement(ms, boundSql, newSql);
        }

        return invocation.proceed();
    }

    private boolean isUpdateByPrimaryKey(String methodName) {
        return StringUtils.equalsAny(methodName, "updateByPrimaryKeySelective", "updateByPrimaryKey");
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        }

        return target;
    }

}
