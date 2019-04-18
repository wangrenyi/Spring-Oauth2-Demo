package com.base.persistence.mybatis;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;

import com.base.common.PagingResult;

/**
 * https://blog.csdn.net/xiaokang123456kao/article/details/76228684 <br/>
 */
public class BaseDAO<Entity, EntityExample, EntityMapper> extends SqlSessionDaoSupport {

    private static final String prefix_placeholder = "p";
    private Class<Entity> entityClass;
    private Class<EntityExample> entityExampleClass;

    private Class<EntityMapper> entityMapperClass;

    @SuppressWarnings("unchecked")
    public BaseDAO() {
        java.lang.reflect.Type[] parameterizedType = getParameterizedTypes();
        entityClass = (Class<Entity>)parameterizedType[0];
        entityExampleClass = (Class<EntityExample>)parameterizedType[1];
        entityMapperClass = (Class<EntityMapper>)parameterizedType[2];
    }

    public void appendUserAndTimeStamp(Object model, String user) {
        Method getId = ReflectionUtils.findMethod(model.getClass(), "getId");
        if (getId != null) {
            Object id = ReflectionUtils.invokeMethod(getId, model);
            appendUserAndTimeStamp(model, user, id == null || (id instanceof Integer && (Integer)id <= 0));
        }
    }

    public void appendUserAndTimeStamp(Object model, String user, boolean creating) {
        Date now = new Date();

        Method setUpdateUser = ReflectionUtils.findMethod(model.getClass(), "setUpdateUser", String.class);
        if (setUpdateUser != null) {
            ReflectionUtils.invokeMethod(setUpdateUser, model, user);
        }

        Method setUpdateTime = ReflectionUtils.findMethod(model.getClass(), "setUpdateTime", Date.class);
        if (setUpdateTime != null) {
            ReflectionUtils.invokeMethod(setUpdateTime, model, now);
        }

        if (creating) {
            Method setCreateUser = ReflectionUtils.findMethod(model.getClass(), "setCreateUser", String.class);
            if (setCreateUser != null) {
                ReflectionUtils.invokeMethod(setCreateUser, model, user);
            }

            Method setCreateTime = ReflectionUtils.findMethod(model.getClass(), "setCreateTime", Date.class);
            if (setCreateTime != null) {
                ReflectionUtils.invokeMethod(setCreateTime, model, now);
            }
        }
    }

    public long countByExample(EntityExample example) {
        EntityMapper entityMapper = getEntityMapperProxy();

        Method method = ReflectionUtils.findMethod(entityMapperClass, "countByExample", entityExampleClass);
        return (long)ReflectionUtils.invokeMethod(method, entityMapper, example);
    }

    public <T> Long countBySql(String sql, Object... value) {
        String msId = generateStatement(SqlCommandType.SELECT, sql, Long.class, value);
        Object paramMap = mappingParameters(sql, value);

        return this.getSqlSession().selectOne(msId, paramMap);
    }

    private void createMappedStatement(SqlCommandType sqlCommandType, String msId, String sql, Class<?> resultType,
        Object... value) {
        Configuration configuration = this.getSqlSession().getConfiguration();
        if (configuration.hasStatement(msId, false)) {
            return;
        }

        Class<?> parameterType = value.getClass();
        if (sql.contains("?")) {
            sql = replacePlaceholder(sql, value);
        }

        LanguageDriver languageDriver = configuration.getDefaultScriptingLanguageInstance();
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, parameterType);
        List<ResultMap> resultMapList = new ArrayList<ResultMap>();
        resultMapList
            .add(new ResultMap.Builder(configuration, "defaultResultMap", resultType, new ArrayList<ResultMapping>(0))
                .build());
        MappedStatement ms = new MappedStatement.Builder(configuration, msId, sqlSource, sqlCommandType)
            .resultMaps(resultMapList).build();

        configuration.addMappedStatement(ms);
    }

    public int deleteByExample(EntityExample example) {
        EntityMapper entityMapper = getEntityMapperProxy();

        Method method = ReflectionUtils.findMethod(entityMapperClass, "deleteByExample", entityExampleClass);
        return (int)ReflectionUtils.invokeMethod(method, entityMapper, example);
    }

    public <T> int deleteByPrimaryKey(T id) {
        EntityMapper entityMapper = getEntityMapperProxy();

        Method method = ReflectionUtils.findMethod(entityMapperClass, "deleteByPrimaryKey", id.getClass());
        return (int)ReflectionUtils.invokeMethod(method, entityMapper, id);
    }

    public <T> int deleteBySql(String sql, Object... value) {
        String msId = generateStatement(SqlCommandType.DELETE, sql, Integer.class, value);
        Object paramMap = mappingParameters(sql, value);

        return this.getSqlSession().delete(msId, paramMap);
    }

    public List<BatchResult> flush() {
        return this.getSqlSession().flushStatements();
    }

    private String generateMsId(SqlCommandType sqlCommandType, String sql, Class<?> resultType, Object... value) {
        return StringUtils.join(new Object[] {this.getClass().getName(), sqlCommandType, sql.hashCode(),
            resultType.getSimpleName(), Optional.ofNullable(value).orElse(new Object[0]).length}, '.');
    }

    private String generateStatement(SqlCommandType sqlCommandType, String sql, Class<?> resultType, Object... value) {
        String msId = generateMsId(sqlCommandType, sql, resultType, value);
        createMappedStatement(sqlCommandType, msId, sql, resultType, value);

        return msId;
    }

    private EntityMapper getEntityMapperProxy() {
        return this.getSqlSession().getMapper(entityMapperClass);
    }

    private java.lang.reflect.Type[] getParameterizedTypes() {
        Class<?> c = getClass();

        java.lang.reflect.Type type = c.getGenericSuperclass();
        java.lang.reflect.Type[] parameterizedType = ((ParameterizedType)type).getActualTypeArguments();
        return parameterizedType;
    }

    public int insert(Entity record) {
        EntityMapper entityMapper = getEntityMapperProxy();

        Method method = ReflectionUtils.findMethod(entityMapperClass, "insert", entityClass);
        return (int)ReflectionUtils.invokeMethod(method, entityMapper, record);
    }

    public <T> int insertBySql(String sql, Object... value) {
        String msId = generateStatement(SqlCommandType.INSERT, sql, Integer.class, value);
        Object paramMap = mappingParameters(sql, value);

        return this.getSqlSession().insert(msId, paramMap);
    }

    public int insertSelective(Entity record) {
        EntityMapper entityMapper = getEntityMapperProxy();

        Method method = ReflectionUtils.findMethod(entityMapperClass, "insertSelective", entityClass);
        return (int)ReflectionUtils.invokeMethod(method, entityMapper, record);
    }

    private Object mappingParameters(String sql, Object... value) {
        if (value == null || value.length < 1) {
            return value;
        }

        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (ArrayUtils.isNotEmpty(value)) {
            for (int i = 0; i < value.length; i++) {
                paramMap.put(prefix_placeholder + String.valueOf(i), value[i]);
            }
        }

        return paramMap;
    }

    public PagingResult pagingByExample(EntityExample entityExample, Integer pageIndex, Integer pageSize) {
        MappedStatement mappedStatement = this.getSqlSession().getConfiguration()
            .getMappedStatement(entityMapperClass.getName() + ".selectByExample");

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put(BasePersistenceMybatisConst.BASEDAO_PAGING_EXAMPLE, entityExample);
        paramMap.put(BasePersistenceMybatisConst.BASEDAO_PAGING_INDEX, pageIndex);
        paramMap.put(BasePersistenceMybatisConst.BASEDAO_PAGING_SIZE, pageSize);

        Long count = this.countByExample(entityExample);
        List<?> details = this.getSqlSession().selectList(mappedStatement.getId(), paramMap);

        PagingResult pagingResult = new PagingResult();
        pagingResult.setCount(count);
        pagingResult.setDetails(details);

        return pagingResult;
    }

    public PagingResult pagingBysql(String sql, Class<?> resultType, Integer pageIndex, Integer pageSize,
        Object... values) {
        String msId = generateStatement(SqlCommandType.SELECT, sql, resultType, values);
        Object params = mappingParameters(sql, values);

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put(BasePersistenceMybatisConst.BASEDAO_PAGING_INDEX, pageIndex);
        paramMap.put(BasePersistenceMybatisConst.BASEDAO_PAGING_SIZE, pageSize);
        paramMap.put(BasePersistenceMybatisConst.BASEDAO_PAGING_CRITERIA, params);

        Long count = this.uniqueBySql("select count(*) from (" + sql + ") baseDaoPagingInternal", Long.class, values);
        List<?> details = this.getSqlSession().selectList(msId, paramMap);

        PagingResult pagingResult = new PagingResult();
        pagingResult.setCount(count);
        pagingResult.setDetails(details);

        return pagingResult;
    }

    private String replacePlaceholder(String sql, Object[] param) {
        for (int i = 0; i < param.length; i++) {
            sql = sql.replaceFirst("(?<!\\\\)\\?", "#{" + prefix_placeholder + String.valueOf(i) + "}");
        }

        sql = sql.replaceAll("\\\\\\?", "?");

        return sql;
    }

    @SuppressWarnings("unchecked")
    public List<Entity> selectByExample(EntityExample example) {
        EntityMapper entityMapper = getEntityMapperProxy();

        Method method = ReflectionUtils.findMethod(entityMapperClass, "selectByExample", entityExampleClass);
        return (List<Entity>)ReflectionUtils.invokeMethod(method, entityMapper, example);
    }

    @SuppressWarnings("unchecked")
    public <T> Entity selectByPrimaryKey(T id) {
        EntityMapper entityMapper = getEntityMapperProxy();

        Method method = ReflectionUtils.findMethod(entityMapperClass, "selectByPrimaryKey", id.getClass());
        return (Entity)ReflectionUtils.invokeMethod(method, entityMapper, id);
    }

    public <T> List<T> selectBySql(String sql, Class<T> resultType, Object... value) {
        String msId = generateStatement(SqlCommandType.SELECT, sql, resultType, value);
        Object paramMap = mappingParameters(sql, value);

        return this.getSqlSession().selectList(msId, paramMap);
    }

    @Override
    @Autowired
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    public <T> T uniqueBySql(String sql, Class<T> resultType, Object... value) {
        String msId = generateStatement(SqlCommandType.SELECT, sql, resultType, value);
        Object paramMap = mappingParameters(sql, value);

        return this.getSqlSession().selectOne(msId, paramMap);
    }

    public Entity uniqueByExample(EntityExample example) {
        List<Entity> result = this.selectByExample(example);
        if (CollectionUtils.isNotEmpty(result)) {
            return result.get(0);
        }

        return null;
    }

    public int updateByExample(Entity record, EntityExample example) {
        EntityMapper entityMapper = getEntityMapperProxy();

        Method method
            = ReflectionUtils.findMethod(entityMapperClass, "updateByExample", entityClass, entityExampleClass);
        return (int)ReflectionUtils.invokeMethod(method, entityMapper, record, example);
    }

    public int updateByExampleSelective(Entity record, EntityExample example) {
        EntityMapper entityMapper = getEntityMapperProxy();

        Method method = ReflectionUtils.findMethod(entityMapperClass, "updateByExampleSelective", entityClass,
            entityExampleClass);
        return (int)ReflectionUtils.invokeMethod(method, entityMapper, record, example);
    }

    public int updateByPrimaryKey(Entity record) {
        EntityMapper entityMapper = getEntityMapperProxy();

        Method method = ReflectionUtils.findMethod(entityMapperClass, "updateByPrimaryKey", entityClass);
        return (int)ReflectionUtils.invokeMethod(method, entityMapper, record);
    }

    public int updateByPrimaryKeySelective(Entity record) {
        EntityMapper entityMapper = getEntityMapperProxy();

        Method method = ReflectionUtils.findMethod(entityMapperClass, "updateByPrimaryKeySelective", entityClass);
        return (int)ReflectionUtils.invokeMethod(method, entityMapper, record);
    }

    public <T> int updateBySql(String sql, Object... value) {
        String msId = generateStatement(SqlCommandType.UPDATE, sql, Integer.class, value);
        Object paramMap = mappingParameters(sql, value);

        return this.getSqlSession().update(msId, paramMap);
    }

}
