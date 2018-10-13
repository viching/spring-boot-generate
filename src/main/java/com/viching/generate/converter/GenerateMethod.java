package com.viching.generate.converter;

import static com.viching.generate.db.util.StringUtility.stringHasValue;

import com.viching.generate.elements.java.FullyQualifiedJavaType;
import com.viching.generate.elements.java.Interface;
import com.viching.generate.elements.java.Method;
import com.viching.generate.source.DBColumnJavaBean;
import com.viching.generate.source.DBTableJavaBean;
import com.viching.generate.source.GeneratedKey;

public abstract class GenerateMethod{
	
	public static final String METHOD_SELECT_PRIMARYKEY = "selectByPrimaryKey";
	public static final String METHOD_SELECT_SELECTIVE = "selectBySelective";
	public static final String METHOD_COUNT_SELECTIVE = "countBySelective";
	public static final String METHOD_SELECT_PAGING = "paging";
	public static final String METHOD_INSERT = "insert";
	public static final String METHOD_INSERT_BATCH = "insertBatch";
	public static final String METHOD_INSERT_SELECTIVE = "insertSelective";
	public static final String METHOD_UPDATE_PRIMARYKEY = "updateByPrimaryKey";
	public static final String METHOD_UPDATE_BATCH = "updateBatch";
	public static final String METHOD_UPDATE_PRIMARYKEY_SELECTIVE = "updateByPrimaryKeySelective";
	public static final String METHOD_DELETE_PRIMARYKEY = "deleteByPrimaryKey";
	public static final String METHOD_DELETE_SELECTIVE = "deleteBySelective";
	public static final String METHOD_DELETE_BATCH = "deleteBatch";
	
	public String getResultAnnotation(Interface interfaze, DBColumnJavaBean dbColumn,
            boolean idColumn, boolean constructorBased) {
        StringBuilder sb = new StringBuilder();
        if (constructorBased) {
            interfaze.addImportedType(dbColumn.getFullyQualifiedJavaType());
            sb.append("@Arg(column=\""); //$NON-NLS-1$
            sb.append(dbColumn.getColumnName());
            sb.append("\", javaType="); //$NON-NLS-1$
            sb.append(dbColumn.getFullyQualifiedJavaType().getShortName());
            sb.append(".class"); //$NON-NLS-1$
        } else {
            sb.append("@Result(column=\""); //$NON-NLS-1$
            sb.append(dbColumn.getColumnName());
            sb.append("\", property=\""); //$NON-NLS-1$
            sb.append(dbColumn.getJavaProperty());
            sb.append('\"');
        }

        if (stringHasValue(dbColumn.getTypeHandler())) {
            FullyQualifiedJavaType fqjt =
                    new FullyQualifiedJavaType(dbColumn.getTypeHandler());
            interfaze.addImportedType(fqjt);
            sb.append(", typeHandler="); //$NON-NLS-1$
            sb.append(fqjt.getShortName());
            sb.append(".class"); //$NON-NLS-1$
        }

        sb.append(", jdbcType=JdbcType."); //$NON-NLS-1$
        sb.append(dbColumn.getJdbcTypeName());
        if (idColumn) {
            sb.append(", id=true"); //$NON-NLS-1$
        }
        sb.append(')');

        return sb.toString();
    }

    public void addGeneratedKeyAnnotation(Method method, GeneratedKey gk) {
        StringBuilder sb = new StringBuilder();
        DBColumnJavaBean dbColumn = gk.getDbColumn();
        if (dbColumn != null) {
            if (gk.isJdbcStandard()) {
                sb.append("@Options(useGeneratedKeys=true,keyProperty=\""); //$NON-NLS-1$
                sb.append(dbColumn.getJavaProperty());
                sb.append("\")"); //$NON-NLS-1$
                method.addAnnotation(sb.toString());
            } else {
                FullyQualifiedJavaType fqjt = dbColumn.getFullyQualifiedJavaType();
                sb.append("@SelectKey(statement=\""); //$NON-NLS-1$
                sb.append(gk.getRuntimeSqlStatement());
                sb.append("\", keyProperty=\""); //$NON-NLS-1$
                sb.append(dbColumn.getJavaProperty());
                sb.append("\", before="); //$NON-NLS-1$
                sb.append(gk.isIdentity() ? "false" : "true"); //$NON-NLS-1$ //$NON-NLS-2$
                sb.append(", resultType="); //$NON-NLS-1$
                sb.append(fqjt.getShortName());
                sb.append(".class)"); //$NON-NLS-1$
                method.addAnnotation(sb.toString());
            }
        }
    }

    public void addGeneratedKeyImports(Interface interfaze, GeneratedKey gk) {
    	DBColumnJavaBean dbColumn = gk.getDbColumn();
        if (dbColumn != null) {
            if (gk.isJdbcStandard()) {
                interfaze.addImportedType(
                        new FullyQualifiedJavaType("org.apache.ibatis.annotations.Options")); //$NON-NLS-1$
            } else {
                interfaze.addImportedType(
                        new FullyQualifiedJavaType("org.apache.ibatis.annotations.SelectKey")); //$NON-NLS-1$
                FullyQualifiedJavaType fqjt = dbColumn.getFullyQualifiedJavaType();
                interfaze.addImportedType(fqjt);
            }
        }
    }

	public abstract void addInterfaceElements(Interface interfaze, DBTableJavaBean dbTable);

	public abstract void addMapperAnnotations(Method method, DBTableJavaBean dbTable);

	public abstract void addExtraImports(Interface interfaze, DBTableJavaBean dbTable);
}
