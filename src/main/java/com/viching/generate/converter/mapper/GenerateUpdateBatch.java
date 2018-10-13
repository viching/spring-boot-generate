package com.viching.generate.converter.mapper;

import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.viching.generate.config.ICommentGenerator;
import com.viching.generate.converter.GenerateMethod;
import com.viching.generate.converter.InjectionOrder;
import com.viching.generate.elements.java.FullyQualifiedJavaType;
import com.viching.generate.elements.java.Interface;
import com.viching.generate.elements.java.JavaVisibility;
import com.viching.generate.elements.java.Method;
import com.viching.generate.elements.java.Parameter;
import com.viching.generate.source.DBTableJavaBean;

@Component
@InjectionOrder(33)
public class GenerateUpdateBatch extends GenerateMethod {
	
	@Autowired
	private ICommentGenerator commentGenerator;
	
	@Override
	public void addInterfaceElements(Interface interfaze,
			DBTableJavaBean dbTable) {
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
        FullyQualifiedJavaType parameterType;
        
        parameterType = FullyQualifiedJavaType.getNewListInstance();
        parameterType.addTypeArgument(new FullyQualifiedJavaType(dbTable.getBaseRecordType()));

        importedTypes.add(parameterType);

        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.setName(METHOD_UPDATE_BATCH);
        
        Parameter parameter = new Parameter(parameterType, "records");
        StringBuilder sb = new StringBuilder();
        sb.setLength(0);
        sb.append("@Param(\"");
        sb.append("list");
        sb.append("\")");
        parameter.addAnnotation(sb.toString());
        method.addParameter(parameter); 

        commentGenerator.addGeneralMethodComment(method, dbTable);

        addMapperAnnotations(method, dbTable);
        
        addExtraImports(interfaze, dbTable);
        interfaze.addImportedTypes(importedTypes);
        interfaze.addMethod(method);
    }

	@Override
	public void addMapperAnnotations(Method method, DBTableJavaBean dbTable) {
        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(dbTable.getProviderType());
        
        StringBuilder sb = new StringBuilder();
        sb.append("@UpdateProvider(type="); 
        sb.append(fqjt.getShortName());
        sb.append(".class, method=\""); 
        sb.append(METHOD_UPDATE_BATCH);
        sb.append("\")"); 

        method.addAnnotation(sb.toString());
    }

	@Override
	public void addExtraImports(Interface interfaze, DBTableJavaBean dbTable) {
		interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.UpdateProvider"));
		interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Param"));
	}

}
