package com.viching.generate.converter.mapper;

import static com.viching.generate.db.FormattingUtilities.getEscapedColumnName;
import static com.viching.generate.db.FormattingUtilities.getParameterClause;
import static com.viching.generate.db.util.StringUtility.escapeStringForJava;
import static com.viching.generate.elements.OutputUtilities.javaIndent;

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
@InjectionOrder(43)
public class GenerateDeleteBatch extends GenerateMethod {
	
	@Autowired
	private ICommentGenerator commentGenerator;

	@Override
	public void addInterfaceElements(Interface interfaze,
			DBTableJavaBean dbTable) {
		Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
		Method method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnType(FullyQualifiedJavaType.getIntInstance());
		method.setName(METHOD_DELETE_BATCH);

		if (dbTable.hasPrimaryKeyColumns()) {
			FullyQualifiedJavaType type = FullyQualifiedJavaType
					.getNewListInstance();
			type.addTypeArgument(new FullyQualifiedJavaType(dbTable
					.getPrimaryKeyType()));
			importedTypes.add(type);
			Parameter parameter = new Parameter(type, "keys");
			StringBuilder sb = new StringBuilder();
			sb.setLength(0);
			sb.append("@Param(\"");
			sb.append("keys");
			sb.append("\")");
			parameter.addAnnotation(sb.toString());
			method.addParameter(parameter);
		} else {
			List<DBColumnJavaBean> dbColumns = dbTable.getPrimaryKeyColumns();
			if (dbColumns.size() > 1) {
				return;
			}
			DBColumnJavaBean dbColumn = dbColumns.get(0);
			FullyQualifiedJavaType type = new FullyQualifiedJavaType(
					dbTable.getPrimaryKeyType());
			type.addTypeArgument(dbColumn.getFullyQualifiedJavaType());
			importedTypes.add(type);
			Parameter parameter = new Parameter(type, "keys");
			StringBuilder sb = new StringBuilder();
			sb.setLength(0);
			sb.append("@Param(\"");
			sb.append("keys");
			sb.append("\")");
			parameter.addAnnotation(sb.toString());
			method.addParameter(parameter);
		}

		commentGenerator.addGeneralMethodComment(method, dbTable);

		addMapperAnnotations(method, dbTable);

		addExtraImports(interfaze, dbTable);
		interfaze.addImportedTypes(importedTypes);
		interfaze.addMethod(method);
	}

	@Override
	public void addMapperAnnotations(Method method, DBTableJavaBean dbTable) {

		method.addAnnotation("@Delete({");

		StringBuilder sb = new StringBuilder();
		javaIndent(sb, 1);
		sb.append("\"<script>\",");
		method.addAnnotation(sb.toString());

		sb.setLength(0);
		javaIndent(sb, 1);
		sb.append("\"delete from ");
		sb.append(escapeStringForJava(dbTable.getTableName()));
		sb.append(" where ");
		sb.append("\",");
		method.addAnnotation(sb.toString());
		
		DBColumnJavaBean dbColumn = null;
		if (!dbTable.needGeneratePrimaryKey()) {
			dbColumn = dbTable.getPrimaryKeyColumns().get(0);
			sb.setLength(0);
			javaIndent(sb, 1);
			sb.append("\"");
			sb.append(escapeStringForJava(getEscapedColumnName(dbColumn)));
			sb.append(" in ");
			sb.append("\",");
			method.addAnnotation(sb.toString());

			sb.setLength(0);
			javaIndent(sb, 1);
			sb.append("\"<foreach collection='keys' index='index' item='"
					+ dbColumn.getJavaProperty()
					+ "' open='(' separator=',' close=')'>\",");
			method.addAnnotation(sb.toString());

			sb.setLength(0);
			javaIndent(sb, 1);
			sb.append("\"");
			sb.append(getParameterClause(dbColumn));
			sb.append("\",");
			method.addAnnotation(sb.toString());

			sb.setLength(0);
			javaIndent(sb, 1);
			sb.append("\"</foreach>\",");
			method.addAnnotation(sb.toString());

			
		}else{
			sb.setLength(0);
			javaIndent(sb, 1);
			sb.append("\"<foreach collection='keys' index='index' item='item' open='(' separator='or' close=')'>\",");
			method.addAnnotation(sb.toString());
			
			sb.setLength(0);
			javaIndent(sb, 1);
			sb.append("\"");
			for (int i = 0; i < dbTable.getPrimaryKeyColumns().size(); i++) {
				
				if(i > 0){
					sb.append(" and ");
				}
				dbColumn = dbTable.getPrimaryKeyColumns().get(i);

				sb.append(dbColumn.getColumnName());
				sb.append(" = ");
				sb.append(getParameterClause(dbColumn));
			}
			sb.append(",\" ");
			method.addAnnotation(sb.toString());
			
			sb.setLength(0);
			javaIndent(sb, 1);
			sb.append("\"</foreach>\",");
			method.addAnnotation(sb.toString());
		}
		sb.setLength(0);
		javaIndent(sb, 1);
		sb.append("\"</script>\"");
		method.addAnnotation(sb.toString());

		method.addAnnotation("})");
	}

	@Override
	public void addExtraImports(Interface interfaze, DBTableJavaBean dbTable) {
		interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Delete"));
		interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Param"));
	}
}
