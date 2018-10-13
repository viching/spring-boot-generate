package com.viching.generate.converter.mapper;

import static com.viching.generate.elements.OutputUtilities.javaIndent;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.viching.generate.config.Engine;
import com.viching.generate.config.ICommentGenerator;
import com.viching.generate.converter.GenerateMethod;
import com.viching.generate.converter.InjectionOrder;
import com.viching.generate.elements.java.FullyQualifiedJavaType;
import com.viching.generate.elements.java.Interface;
import com.viching.generate.elements.java.JavaVisibility;
import com.viching.generate.elements.java.Method;
import com.viching.generate.elements.java.Parameter;
import com.viching.generate.source.DBColumnJavaBean;
import com.viching.generate.source.DBTableJavaBean;

@Component
@InjectionOrder(14)
public class GeneratePaging extends GenerateMethod {
	
	@Autowired
	private Engine engine;
	@Autowired
	private ICommentGenerator commentGenerator;

	@Override
	public void addInterfaceElements(Interface interfaze, DBTableJavaBean dbTable) {
		Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
        FullyQualifiedJavaType parameterType;
        
        parameterType = new FullyQualifiedJavaType(engine.getPaging());
        importedTypes.add(parameterType);

        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        
        FullyQualifiedJavaType returnType = FullyQualifiedJavaType.getNewListInstance();
        returnType.addTypeArgument(new FullyQualifiedJavaType(dbTable.getBaseRecordType()));
        method.setReturnType(returnType);
        
        method.setName(METHOD_SELECT_PAGING);
        method.addParameter(new Parameter(parameterType, "paging"));

        commentGenerator.addGeneralMethodComment(method, dbTable);

        addMapperAnnotations(method, dbTable);
        
        addAnnotatedResults(interfaze, method, dbTable);

        addExtraImports(interfaze, dbTable);
        interfaze.addImportedTypes(importedTypes);
        interfaze.addMethod(method);
	}
	
	@Override
    public void addMapperAnnotations(Method method, DBTableJavaBean dbTable) {
		FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(
				dbTable.getProviderType());

		StringBuilder sb = new StringBuilder();
		sb.append("@SelectProvider(type=");
		sb.append(fqjt.getShortName());
		sb.append(".class, method=\"");
		sb.append(METHOD_SELECT_PAGING);
		sb.append("\")");

		method.addAnnotation(sb.toString());
	}

    @Override
    public void addExtraImports(Interface interfaze, DBTableJavaBean dbTable) {
        interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.SelectProvider")); //$NON-NLS-1$
    }
    
    private void addAnnotatedResults(Interface interfaze, Method method, DBTableJavaBean dbTable) {

        if (dbTable.isConstructorBased()) {
            method.addAnnotation("@ConstructorArgs({"); //$NON-NLS-1$
        } else {
            method.addAnnotation("@Results({"); //$NON-NLS-1$
        }

        StringBuilder sb = new StringBuilder();

        Iterator<DBColumnJavaBean> iterPk = dbTable.getAllColumns().iterator();
        while (iterPk.hasNext()) {
            DBColumnJavaBean dbColumn = iterPk.next();
            sb.setLength(0);
            javaIndent(sb, 1);
            sb.append(getResultAnnotation(interfaze, dbColumn, dbColumn.isIdentity(),
                    dbTable.isConstructorBased()));
            
            if (iterPk.hasNext()) {
                sb.append(',');
            }

            method.addAnnotation(sb.toString());
        }

        method.addAnnotation("})"); //$NON-NLS-1$
    }

}
