package com.viching.generate.converter.mapper;

import static com.viching.generate.db.FormattingUtilities.getEscapedColumnName;
import static com.viching.generate.db.FormattingUtilities.getParameterClause;
import static com.viching.generate.db.util.StringUtility.escapeStringForJava;
import static com.viching.generate.elements.OutputUtilities.javaIndent;

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

@Component
@InjectionOrder(41)
public class GenerateDeleteByPrimaryKey extends GenerateMethod {
	
	@Autowired
	private ICommentGenerator commentGenerator;
	
	@Override
	public void addInterfaceElements(Interface interfaze,
			DBTableJavaBean dbTable) {
		Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.setName(METHOD_DELETE_PRIMARYKEY);

        if (dbTable.hasPrimaryKeyColumns()) {
            FullyQualifiedJavaType type = new FullyQualifiedJavaType(
                    dbTable.getPrimaryKeyType());
            importedTypes.add(type);
            method.addParameter(new Parameter(type, dbTable.getPrimaryKey())); 
        } else {
            List<DBColumnJavaBean> dbColumns = dbTable
                    .getPrimaryKeyColumns();
            boolean annotate = dbColumns.size() > 1;
            if (annotate) {
                importedTypes.add(new FullyQualifiedJavaType(
                        "org.apache.ibatis.annotations.Param")); 
            }
            StringBuilder sb = new StringBuilder();
            for (DBColumnJavaBean dbColumn : dbColumns) {
                FullyQualifiedJavaType type = dbColumn
                        .getFullyQualifiedJavaType();
                importedTypes.add(type);
                Parameter parameter = new Parameter(type, dbColumn
                        .getJavaProperty());
                if (annotate) {
                    sb.setLength(0);
                    sb.append("@Param(\""); 
                    sb.append(dbColumn.getJavaProperty());
                    sb.append("\")"); 
                    parameter.addAnnotation(sb.toString());
                }
                method.addParameter(parameter);
            }
        }

        commentGenerator.addGeneralMethodComment(method,
                dbTable);

        addMapperAnnotations(method, dbTable);
        
        addExtraImports(interfaze, dbTable);
        interfaze.addImportedTypes(importedTypes);
        interfaze.addMethod(method);
	}
	
	@Override
    public void addMapperAnnotations(Method method, DBTableJavaBean dbTable) {

        method.addAnnotation("@Delete({"); //$NON-NLS-1$

        StringBuilder sb = new StringBuilder();
        javaIndent(sb, 1);
        sb.append("\"delete from "); //$NON-NLS-1$
        sb.append(escapeStringForJava(
                dbTable.getTableName()));
        sb.append("\","); //$NON-NLS-1$
        method.addAnnotation(sb.toString());

        boolean and = false;
        Iterator<DBColumnJavaBean> iter = dbTable.getPrimaryKeyColumns().iterator();
        while (iter.hasNext()) {
            sb.setLength(0);
            javaIndent(sb, 1);
            if (and) {
                sb.append("  \"and "); //$NON-NLS-1$
            } else {
                sb.append("\"where "); //$NON-NLS-1$
                and = true;
            }

            DBColumnJavaBean dbColumn = iter.next();
            sb.append(escapeStringForJava(
                    getEscapedColumnName(dbColumn)));
            sb.append(" = "); //$NON-NLS-1$
            sb.append(getParameterClause(dbColumn));
            sb.append('\"');
            if (iter.hasNext()) {
                sb.append(',');
            }

            method.addAnnotation(sb.toString());
        }

        method.addAnnotation("})"); //$NON-NLS-1$
    }

    @Override
    public void addExtraImports(Interface interfaze, DBTableJavaBean dbTable) {
        interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Delete")); //$NON-NLS-1$
    }
}
