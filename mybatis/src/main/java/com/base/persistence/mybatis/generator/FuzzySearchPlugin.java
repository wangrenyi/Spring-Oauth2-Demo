package com.base.persistence.mybatis.generator;

import java.util.List;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;

public class FuzzySearchPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> arg0) {
        return true;
    }

    /**
     *
     * add new method:
     *
     * <pre>
     * {@code
     * public Criteria andAnyLike(Map<String,String> params) {
     *     Set<Entry<String,String>>entrySet=map.entrySet();
     *     entrySet.forEach(entry->addCriterion(entry.getKey()+" like",entry.getValue(),entry.getKey()));
     *     return this;
     * }
     * </pre>
     *
     */
    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        List<InnerClass> innerClasses = topLevelClass.getInnerClasses();
        InnerClass innerClass = innerClasses.stream()
            .filter(clazz -> "Criteria".equals(clazz.getType().getShortNameWithoutTypeArguments())).findFirst()
            .orElse(null);

        topLevelClass.addImportedType("java.util.Set");
        topLevelClass.addImportedType("java.util.Map.Entry");
        Method andAnyLikeMethod = createAndAnyLikeMethod(innerClass.getType());
        innerClass.addMethod(andAnyLikeMethod);

        return true;
    }

    private Method createAndAnyLikeMethod(FullyQualifiedJavaType returnType) {
        Method andAnyLikeMethod = new Method();
        andAnyLikeMethod.setVisibility(JavaVisibility.PUBLIC);
        andAnyLikeMethod.setReturnType(returnType);
        andAnyLikeMethod.setName("andAnyLike");
        FullyQualifiedJavaType mapType = FullyQualifiedJavaType.getNewMapInstance();
        mapType.addTypeArgument(FullyQualifiedJavaType.getStringInstance());
        mapType.addTypeArgument(FullyQualifiedJavaType.getStringInstance());
        andAnyLikeMethod.addParameter(new Parameter(mapType, "params"));
        andAnyLikeMethod.addBodyLine("Set<Entry<String, String>> entrySet = params.entrySet();");
        andAnyLikeMethod.addBodyLine(
            "entrySet.forEach(entry -> addCriterion(entry.getKey() + \" like\", entry.getValue(), entry.getKey()));");
        andAnyLikeMethod.addBodyLine("return this;");

        return andAnyLikeMethod;
    }

}
