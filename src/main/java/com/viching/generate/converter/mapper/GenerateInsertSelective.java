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
import com.viching.generate.source.GeneratedKey;

@Component
@InjectionOrder(22)
public class GenerateInsertSelective extends GenerateMethod {
	
	@Autowired
	private ICommentGenerator commentGenerator;
	
	@Override
	public void addInterfaceElements(Interface interfaze,
			DBTableJavaBean dbTable) {
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
        Method method = new Method();

        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName(METHOD_INSERT_SELECTIVE);

        FullyQualifiedJavaType parameterType = new FullyQualifiedJavaType(dbTable.getBaseRecordType());

        importedTypes.add(parameterType);
        method.addParameter(new Parameter(parameterType, "record")); 

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
        sb.append("@InsertProvider(type="); 
        sb.append(fqjt.getShortName());
        sb.append(".class, method=\""); 
        sb.append(METHOD_INSERT_SELECTIVE);
        sb.append("\")"); 

        method.addAnnotation(sb.toString());

        GeneratedKey gk = dbTable.getGeneratedKey();
        if (gk != null) {
            addGeneratedKeyAnnotation(method, gk);
        }
    }

	@Override
	public void addExtraImports(Interface interfaze, DBTableJavaBean dbTable) {
		interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.InsertProvider")); 
	}
}
