package com.viching.generate.converter.mapper;

import static com.viching.generate.db.FormattingUtilities.getEscapedColumnName;
import static com.viching.generate.db.FormattingUtilities.getParameterClause;
import static com.viching.generate.db.util.StringUtility.escapeStringForJava;
import static com.viching.generate.elements.OutputUtilities.javaIndent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
import com.viching.generate.source.GeneratedKey;

@Component
@InjectionOrder(21)
public class GenerateInsert extends GenerateMethod {
	
	@Autowired
	private ICommentGenerator commentGenerator;
	
	@Override
	public void addInterfaceElements(Interface interfaze,
			DBTableJavaBean dbTable) {
		Method method = new Method();

        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName(METHOD_INSERT);

        FullyQualifiedJavaType parameterType;
        parameterType = new FullyQualifiedJavaType(
                dbTable.getBaseRecordType());

        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
        importedTypes.add(parameterType);
        method.addParameter(new Parameter(parameterType, "record")); //$NON-NLS-1$

        commentGenerator.addGeneralMethodComment(method,
                dbTable);

        addMapperAnnotations(method, dbTable);

        addExtraImports(interfaze, dbTable);
        interfaze.addImportedTypes(importedTypes);
        interfaze.addMethod(method);
	}
	
	@Override
    public void addMapperAnnotations(Method method, DBTableJavaBean dbTable) {

        method.addAnnotation("@Insert({"); //$NON-NLS-1$
        StringBuilder insertClause = new StringBuilder();
        StringBuilder valuesClause = new StringBuilder();

        javaIndent(insertClause, 1);
        javaIndent(valuesClause, 1);

        insertClause.append("\"insert into "); //$NON-NLS-1$
        insertClause.append(escapeStringForJava(dbTable.getTableName()));
        insertClause.append(" ("); //$NON-NLS-1$

        valuesClause.append("\"values ("); //$NON-NLS-1$

        List<String> valuesClauses = new ArrayList<String>();
        Iterator<DBColumnJavaBean> iter = dbTable.getAllColumns().iterator();
        boolean hasFields = false;
        while (iter.hasNext()) {
            DBColumnJavaBean introspectedColumn = iter.next();

            insertClause.append(escapeStringForJava(getEscapedColumnName(introspectedColumn)));
            valuesClause.append(getParameterClause(introspectedColumn));
            hasFields = true;
            if (iter.hasNext()) {
                insertClause.append(", "); //$NON-NLS-1$
                valuesClause.append(", "); //$NON-NLS-1$
            }

            if (valuesClause.length() > 60) {
                if (!iter.hasNext()) {
                    insertClause.append(')');
                    valuesClause.append(')');
                }
                insertClause.append("\","); //$NON-NLS-1$
                valuesClause.append('\"');
                if (iter.hasNext()) {
                    valuesClause.append(',');
                }

                method.addAnnotation(insertClause.toString());
                insertClause.setLength(0);
                javaIndent(insertClause, 1);
                insertClause.append('\"');

                valuesClauses.add(valuesClause.toString());
                valuesClause.setLength(0);
                javaIndent(valuesClause, 1);
                valuesClause.append('\"');
                hasFields = false;
            }
        }

        if (hasFields) {
            insertClause.append(")\","); //$NON-NLS-1$
            method.addAnnotation(insertClause.toString());

            valuesClause.append(")\""); //$NON-NLS-1$
            valuesClauses.add(valuesClause.toString());
        }

        for (String clause : valuesClauses) {
            method.addAnnotation(clause);
        }

        method.addAnnotation("})"); //$NON-NLS-1$

        GeneratedKey gk = dbTable.getGeneratedKey();
        if (gk != null) {
            addGeneratedKeyAnnotation(method, gk);
        }
    }

    @Override
    public void addExtraImports(Interface interfaze, DBTableJavaBean dbTable) {
        GeneratedKey gk = dbTable.getGeneratedKey();
        if (gk != null) {
            addGeneratedKeyImports(interfaze, gk);
        }
        interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Insert")); //$NON-NLS-1$
    }

}
