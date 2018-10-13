package com.viching.generate.converter.mapper;

import static com.viching.generate.db.FormattingUtilities.getEscapedColumnName;
import static com.viching.generate.db.FormattingUtilities.getParameterClause;
import static com.viching.generate.db.util.StringUtility.escapeStringForJava;
import static com.viching.generate.elements.OutputUtilities.javaIndent;

import java.util.Iterator;
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
import com.viching.generate.source.DBColumnJavaBean;
import com.viching.generate.source.DBTableJavaBean;

@Component
@InjectionOrder(31)
public class GenerateUpdateByPrimaryKey extends GenerateMethod {
	
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
		method.setName(METHOD_UPDATE_PRIMARYKEY);
		method.addParameter(new Parameter(parameterType, "record")); 

		commentGenerator.addGeneralMethodComment(method,
				dbTable);

		addMapperAnnotations(method, dbTable);

		addExtraImports(interfaze, dbTable);
		interfaze.addImportedTypes(importedTypes);
		interfaze.addMethod(method);
	}

	@Override
	public void addMapperAnnotations(Method method, DBTableJavaBean dbTable) {
		method.addAnnotation("@Update({"); 

        StringBuilder sb = new StringBuilder();
        javaIndent(sb, 1);
        sb.append("\"update "); 
        sb.append(escapeStringForJava(dbTable.getTableName()));
        sb.append("\","); 
        method.addAnnotation(sb.toString());

        // set up for first column
        sb.setLength(0);
        javaIndent(sb, 1);
        sb.append("\"set "); 

        Iterator<DBColumnJavaBean> iter;
        iter = dbTable.getBaseColumns().iterator();

        while (iter.hasNext()) {
            DBColumnJavaBean dbColumn = iter.next();

            sb.append(escapeStringForJava(getEscapedColumnName(dbColumn)));
            sb.append(" = "); 
            sb.append(getParameterClause(dbColumn));

            if (iter.hasNext()) {
                sb.append(',');
            }

            sb.append("\","); 
            method.addAnnotation(sb.toString());

            // set up for the next column
            if (iter.hasNext()) {
                sb.setLength(0);
                javaIndent(sb, 1);
                sb.append("  \""); 
            }
        }

        boolean and = false;
        iter = dbTable.getPrimaryKeyColumns().iterator();
        while (iter.hasNext()) {
            sb.setLength(0);
            javaIndent(sb, 1);
            if (and) {
                sb.append("  \"and "); 
            } else {
                sb.append("\"where "); 
                and = true;
            }

            DBColumnJavaBean dbColumn = iter.next();
            sb.append(escapeStringForJava(getEscapedColumnName(dbColumn)));
            sb.append(" = "); 
            sb.append(getParameterClause(dbColumn));
            sb.append('\"');
            if (iter.hasNext()) {
                sb.append(',');
            }
            method.addAnnotation(sb.toString());
        }

        method.addAnnotation("})");
	}

	@Override
	public void addExtraImports(Interface interfaze, DBTableJavaBean dbTable) {
        interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Update"));
	}
}
