package com.viching.generate.converter.mapper;

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
import com.viching.generate.db.FormattingUtilities;
import com.viching.generate.db.util.StringUtility;
import com.viching.generate.elements.java.FullyQualifiedJavaType;
import com.viching.generate.elements.java.Interface;
import com.viching.generate.elements.java.JavaVisibility;
import com.viching.generate.elements.java.Method;
import com.viching.generate.elements.java.Parameter;
import com.viching.generate.source.DBColumnJavaBean;
import com.viching.generate.source.DBTableJavaBean;

@Component
@InjectionOrder(11)
public class GenerateSelectByPrimaryKey extends GenerateMethod {
	
	@Autowired
	private ICommentGenerator commentGenerator;

	@Override
	public void addInterfaceElements(Interface interfaze, DBTableJavaBean dbTable) {
		Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);

        FullyQualifiedJavaType returnType = new FullyQualifiedJavaType(dbTable.getBaseRecordType());
        method.setReturnType(returnType);
        importedTypes.add(returnType);

        method.setName(METHOD_SELECT_PRIMARYKEY);

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
                        "org.apache.ibatis.annotations.Param")); //$NON-NLS-1$
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
                    sb.append("@Param(\""); //$NON-NLS-1$
                    sb.append(dbColumn.getJavaProperty());
                    sb.append("\")"); //$NON-NLS-1$
                    parameter.addAnnotation(sb.toString());
                }
                method.addParameter(parameter);
            }
        }

        addMapperAnnotations(method, dbTable);
        
        addAnnotatedResults(interfaze, method, dbTable);

        commentGenerator.addGeneralMethodComment(method,
                dbTable);

        addExtraImports(interfaze, dbTable);
        interfaze.addImportedTypes(importedTypes);
        interfaze.addMethod(method);
	}
	
	@Override
    public void addMapperAnnotations(Method method, DBTableJavaBean dbTable) {

        StringBuilder sb = new StringBuilder();
        method.addAnnotation("@Select({"); //$NON-NLS-1$
        javaIndent(sb, 1);
        sb.append("\"select\","); //$NON-NLS-1$
        method.addAnnotation(sb.toString());
        
        sb.setLength(0);
        javaIndent(sb, 1);
        sb.append('"');
        boolean hasColumns = false;
        Iterator<DBColumnJavaBean> iter = dbTable.getAllColumns().iterator();
        while (iter.hasNext()) {
            sb.append(StringUtility.escapeStringForJava(FormattingUtilities.getSelectListPhrase(iter.next())));
            hasColumns = true;

            if (iter.hasNext()) {
                sb.append(", "); //$NON-NLS-1$
            }

            if (sb.length() > 80) {
                sb.append("\","); //$NON-NLS-1$
                method.addAnnotation(sb.toString());

                sb.setLength(0);
                javaIndent(sb, 1);
                sb.append('"');
                hasColumns = false;
            }
        }

        if (hasColumns) {
            sb.append("\","); //$NON-NLS-1$
            method.addAnnotation(sb.toString());
        }

        sb.setLength(0);
        javaIndent(sb, 1);
        sb.append("\"from "); //$NON-NLS-1$
        sb.append(StringUtility.escapeStringForJava(dbTable.getTableName()));
        sb.append("\","); //$NON-NLS-1$
        method.addAnnotation(sb.toString());

        boolean and = false;
        iter = dbTable.getPrimaryKeyColumns().iterator();
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
            sb.append(StringUtility.escapeStringForJava(FormattingUtilities.getAliasedEscapedColumnName(dbColumn)));
            sb.append(" = "); //$NON-NLS-1$
            sb.append(FormattingUtilities.getParameterClause(dbColumn));
            sb.append('\"');
            if (iter.hasNext()) {
                sb.append(',');
            }
            method.addAnnotation(sb.toString());
        }

        method.addAnnotation("})"); //$NON-NLS-1$
    }

    /*private void addResultMapAnnotation(Method method, DBTableJavaBean dbTable) {

        String annotation = String.format("@ResultMap(\"%s.%s\")", //$NON-NLS-1$
                dbTable.getMapperType(),"BaseResultMap");
        method.addAnnotation(annotation);
    }*/

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

    @Override
    public void addExtraImports(Interface interfaze, DBTableJavaBean dbTable) {
        interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Select")); //$NON-NLS-1$

        addAnnotationImports(interfaze, dbTable);
    }

    private void addAnnotationImports(Interface interfaze, DBTableJavaBean dbTable) {
        interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.type.JdbcType")); //$NON-NLS-1$

        if (dbTable.isConstructorBased()) {
            interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Arg")); //$NON-NLS-1$
            interfaze.addImportedType(
                    new FullyQualifiedJavaType("org.apache.ibatis.annotations.ConstructorArgs")); //$NON-NLS-1$
        } else {
            interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Result")); //$NON-NLS-1$
            interfaze.addImportedType(
                    new FullyQualifiedJavaType("org.apache.ibatis.annotations.Results")); //$NON-NLS-1$
        }
    }

}
