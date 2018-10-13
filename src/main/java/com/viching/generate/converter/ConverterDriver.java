package com.viching.generate.converter;

import static com.viching.generate.db.util.JavaBeansUtil.getGetterMethodName;
import static com.viching.generate.db.util.JavaBeansUtil.getSetterMethodName;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.viching.generate.adapter.Adapter;
import com.viching.generate.config.ICommentGenerator;
import com.viching.generate.elements.java.CompilationUnit;
import com.viching.generate.elements.java.Field;
import com.viching.generate.elements.java.FullyQualifiedJavaType;
import com.viching.generate.elements.java.JavaVisibility;
import com.viching.generate.elements.java.Method;
import com.viching.generate.elements.java.Parameter;
import com.viching.generate.elements.java.TopLevelClass;
import com.viching.generate.source.DBColumnJavaBean;
import com.viching.generate.source.DBTableJavaBean;

public abstract class ConverterDriver implements Converter {
	
	@Autowired
	protected ICommentGenerator commentGenerator;
	@Autowired
	protected List<Adapter> adapters;

	@Override
	public Method getGetter(Field field) {
		Method method = new Method();
		method.setName(getGetterMethodName(field.getName(), field.getType()));
		method.setReturnType(field.getType());
		method.setVisibility(JavaVisibility.PUBLIC);
		StringBuilder sb = new StringBuilder();
		sb.append("return "); //$NON-NLS-1$
		sb.append(field.getName());
		sb.append(';');
		method.addBodyLine(sb.toString());
		return method;
	}

	@Override
	public Method getSetter(Field field) {
		Method method = new Method();
		method.setName(getSetterMethodName(field.getName()));
		method.setVisibility(JavaVisibility.PUBLIC);
		StringBuilder sb = new StringBuilder();
		sb.append("this.");
		sb.append(field.getName());
		sb.append(" = ");
		sb.append(field.getName());
		sb.append(';');
		method.addBodyLine(sb.toString());
		return method;
	}

	@Override
	public void addDefaultConstructor(TopLevelClass topLevelClass,
			DBTableJavaBean dbTable) {
		topLevelClass.addMethod(getDefaultConstructor(topLevelClass, dbTable));
	}

	@Override
	public Method getDefaultConstructor(TopLevelClass topLevelClass,
			DBTableJavaBean dbTable) {
		Method method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setConstructor(true);
		method.setName(topLevelClass.getType().getShortName());
		method.addBodyLine("super();"); //$NON-NLS-1$
		commentGenerator.addGeneralMethodComment(method, dbTable);
		return method;
	}
	
	@Override
	public FullyQualifiedJavaType getSuperClass(DBTableJavaBean dbTable) {
		FullyQualifiedJavaType superClass = null;
		if (StringUtils.isNoneBlank(dbTable.getPrimaryKeyType())) {
			superClass = new FullyQualifiedJavaType(dbTable.getPrimaryKeyType());
		}
		return superClass;
	}
	
	@Override
	public void addParameterizedConstructor(TopLevelClass topLevelClass,
			List<DBColumnJavaBean> constructorColumns, DBTableJavaBean dbTable) {
		final Method method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setConstructor(true);
		method.setName(topLevelClass.getType().getShortName());
		commentGenerator.addGeneralMethodComment(method, dbTable);

		/*for (DBColumnJavaBean introspectedColumn : constructorColumns) {
			method.addParameter(new Parameter(introspectedColumn
					.getFullyQualifiedJavaType(), introspectedColumn
					.getJavaProperty()));
			topLevelClass.addImportedType(introspectedColumn
					.getFullyQualifiedJavaType());
		}*/
		constructorColumns.stream().forEach(introspectedColumn->{
			method.addParameter(new Parameter(introspectedColumn
					.getFullyQualifiedJavaType(), introspectedColumn
					.getJavaProperty()));
			topLevelClass.addImportedType(introspectedColumn
					.getFullyQualifiedJavaType());
		});

		StringBuilder sb = new StringBuilder();
		List<String> superColumns = new LinkedList<String>();
		if (dbTable.needGeneratePrimaryKey()) {
			//boolean comma = false;
			sb.append("super("); //$NON-NLS-1$
			/*for (DBColumnJavaBean introspectedColumn : dbTable
					.getPrimaryKeyColumns()) {
				if (comma) {
					sb.append(", "); //$NON-NLS-1$
				} else {
					comma = true;
				}
				sb.append(introspectedColumn.getJavaProperty());
				superColumns.add(introspectedColumn.getColumnName());
			}*/
			sb.append(dbTable.getPrimaryKeyColumns().stream().map(DBColumnJavaBean::getJavaProperty).collect(Collectors.joining(",")));
			dbTable.getPrimaryKeyColumns().stream().forEach(introspectedColumn->{
				superColumns.add(introspectedColumn.getColumnName());
			});
			sb.append(");"); //$NON-NLS-1$
			method.addBodyLine(sb.toString());
		}

		/*for (DBColumnJavaBean introspectedColumn : constructorColumns) {
			if (!superColumns.contains(introspectedColumn.getColumnName())) {
				sb.setLength(0);
				sb.append("this."); //$NON-NLS-1$
				sb.append(introspectedColumn.getJavaProperty());
				sb.append(" = "); //$NON-NLS-1$
				sb.append(introspectedColumn.getJavaProperty());
				sb.append(';');
				method.addBodyLine(sb.toString());
			}
		}*/
		
		constructorColumns.stream().filter(introspectedColumn ->!superColumns.contains(introspectedColumn.getColumnName())).forEach(introspectedColumn->{
			sb.setLength(0);
			sb.append("this."); //$NON-NLS-1$
			sb.append(introspectedColumn.getJavaProperty());
			sb.append(" = "); //$NON-NLS-1$
			sb.append(introspectedColumn.getJavaProperty());
			sb.append(';');
			method.addBodyLine(sb.toString());
		});

		topLevelClass.addMethod(method);
	}

	@Override
	public List<DBColumnJavaBean> getColumnsInThisClass(DBTableJavaBean dbTable) {
		List<DBColumnJavaBean> introspectedColumns;
		if (dbTable.hasPrimaryKeyColumns()) {
			introspectedColumns = dbTable.getAllColumns();
		} else {
			introspectedColumns = dbTable.getBaseColumns();
		}

		return introspectedColumns;
	}

	@Override
	public abstract List<CompilationUnit> getCompilationUnits(DBTableJavaBean dbTable);

}
