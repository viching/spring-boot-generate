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
@InjectionOrder(32)
public class GenerateUpdateByPrimaryKeySelective extends GenerateMethod {
	
	@Autowired
	private ICommentGenerator commentGenerator;
	
	@Override
	public void addInterfaceElements(Interface interfaze,
			DBTableJavaBean dbTable) {
		Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
        FullyQualifiedJavaType parameterType;
        
        parameterType = new FullyQualifiedJavaType(dbTable.getBaseRecordType());

        importedTypes.add(parameterType);

        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.setName(METHOD_UPDATE_PRIMARYKEY_SELECTIVE);
        method.addParameter(new Parameter(parameterType, "record")); //$NON-NLS-1$

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
        sb.append("@UpdateProvider(type="); //$NON-NLS-1$
        sb.append(fqjt.getShortName());
        sb.append(".class, method=\""); //$NON-NLS-1$
        sb.append(METHOD_UPDATE_PRIMARYKEY_SELECTIVE);
        sb.append("\")"); //$NON-NLS-1$

        method.addAnnotation(sb.toString());
    }
	
	@Override
    public void addExtraImports(Interface interfaze, DBTableJavaBean dbTable) {
		interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.UpdateProvider")); //$NON-NLS-1$
	}
}
