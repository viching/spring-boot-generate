package com.viching.generate.config;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.viching.generate.elements.java.CompilationUnit;
import com.viching.generate.elements.java.Field;
import com.viching.generate.elements.java.FullyQualifiedJavaType;
import com.viching.generate.elements.java.JavaVisibility;
import com.viching.generate.elements.java.Method;
import com.viching.generate.elements.java.Parameter;
import com.viching.generate.elements.java.TopLevelClass;
import com.viching.generate.source.DBTableJavaBean;

@Component
public class MybatisConfig{

	@Autowired
	private Engine engine;
	
	@Autowired
	protected ICommentGenerator commentGenerator;
	
	
	public List<CompilationUnit> getCompilationUnits(List<DBTableJavaBean> dbTables) {
		List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
		
		if(engine.isNeedEnum()) {
			FullyQualifiedJavaType type = new FullyQualifiedJavaType(getLocalEnumTypeHandler());
			TopLevelClass typeHandler = new TopLevelClass(type);
			typeHandler.setVisibility(JavaVisibility.PUBLIC);
			
			FullyQualifiedJavaType supper = new FullyQualifiedJavaType(engine.getEnumTypeHandler());
			typeHandler.setSuperClass(supper);
			typeHandler.addImportedType(supper);
			
			Method structor = new Method(getHandlerName());
			structor.setVisibility(JavaVisibility.PUBLIC);
			structor.setConstructor(true);
			FullyQualifiedJavaType enumType = new FullyQualifiedJavaType("java.lang.Class");
			FullyQualifiedJavaType root = new FullyQualifiedJavaType(engine.getEnumRoot());
			enumType.addTypeArgument(root);
			typeHandler.addImportedType(root);
			Parameter p = new Parameter(enumType, "type");
			structor.addParameter(p);
			
			structor.addBodyLine("super(type);");
			typeHandler.addMethod(structor);
			
			StringBuilder sb = new StringBuilder();
			sb.append("@MappedTypes({");
			dbTables.stream().forEach(dbTable ->{
				dbTable.getBaseColumns().stream().forEach(c->{
					if (c.getJdbcType() == Types.TINYINT && engine.isNeedEnum()) {
						FullyQualifiedJavaType fq = new FullyQualifiedJavaType(dbTable.getEnumType(dbTable.getTableName()+"_"+c.getColumnName()));
		            	typeHandler.addImportedType(fq);
		            	sb.append(fq.getShortName() +".class, ");
		            }
				});
			});
	    	
			sb.append("})");
			typeHandler.addImportedType("org.apache.ibatis.type.MappedTypes");
			typeHandler.addAnnotation(sb.toString());
		
			answer.add(typeHandler);
		}
		
		answer.addAll(getCompilationUnits());
		
		return answer;
	}

	public List<CompilationUnit> getCompilationUnits() {
		
		FullyQualifiedJavaType type = new FullyQualifiedJavaType(getEnumConfigType());
		TopLevelClass config = new TopLevelClass(type);

		config.setVisibility(JavaVisibility.PUBLIC);
		commentGenerator.addJavaFileComment(config);
		config.addAnnotation("@Configuration");
		config.addAnnotation("@AutoConfigureAfter(DataSourceAutoConfiguration.class)");
		config.addAnnotation("@MapperScan(basePackages = \""+ getExtMapperRoot() +"\")");
		config.addAnnotation("@EnableTransactionManagement");
		config.addSuperInterface(new FullyQualifiedJavaType("org.springframework.transaction.annotation.TransactionManagementConfigurer"));
		
		Field dataSource = new Field("dataSource", new FullyQualifiedJavaType("javax.sql.DataSource"));
		dataSource.addAnnotation("@Autowired");
		dataSource.setVisibility(JavaVisibility.PRIVATE);
		config.addField(dataSource);

		Method annotationDrivenTransactionManager = new Method("annotationDrivenTransactionManager");
		annotationDrivenTransactionManager.addAnnotation("@Override");
		annotationDrivenTransactionManager.setVisibility(JavaVisibility.PUBLIC);
		annotationDrivenTransactionManager.setReturnType(new FullyQualifiedJavaType("org.springframework.transaction.PlatformTransactionManager"));
		annotationDrivenTransactionManager.addBodyLine("return new DataSourceTransactionManager(dataSource);");
		config.addMethod(annotationDrivenTransactionManager);
		
		Method sqlSessionFactoryBean = new Method("sqlSessionFactoryBean");
		sqlSessionFactoryBean.addAnnotation("@Bean(name = \"sqlSessionFactory\")");
		sqlSessionFactoryBean.addAnnotation("@ConditionalOnMissingBean");
		sqlSessionFactoryBean.setVisibility(JavaVisibility.PUBLIC);
		sqlSessionFactoryBean.setReturnType(new FullyQualifiedJavaType("org.apache.ibatis.session.SqlSessionFactory"));
		//---------------------
		sqlSessionFactoryBean.addBodyLine("SqlSessionFactoryBean bean = new SqlSessionFactoryBean();");
		sqlSessionFactoryBean.addBodyLine("bean.setDataSource(dataSource);");
		sqlSessionFactoryBean.addBodyLine("//spring boot这种运行方式的文件系统与之前的不一样，所以mybatis需要设置spring boot vsf。");
		sqlSessionFactoryBean.addBodyLine("bean.setVfs(SpringBootVFS.class);");
		sqlSessionFactoryBean.addBodyLine("");
		sqlSessionFactoryBean.addBodyLine("Properties sqlSessionFactoryProperties = new Properties();");
		sqlSessionFactoryBean.addBodyLine("sqlSessionFactoryProperties.setProperty(\"dialect\", \""+engine.getDb()+"\");");
		sqlSessionFactoryBean.addBodyLine("bean.setConfigurationProperties(sqlSessionFactoryProperties);");
		sqlSessionFactoryBean.addBodyLine("");
		sqlSessionFactoryBean.addBodyLine("// 分页插件 ");
		sqlSessionFactoryBean.addBodyLine("PageInterceptor pageHelper = new PageInterceptor();");
		sqlSessionFactoryBean.addBodyLine("Properties properties = new Properties();");
		sqlSessionFactoryBean.addBodyLine("properties.setProperty(\"databaseType\", \""+engine.getDb()+"\");");
		sqlSessionFactoryBean.addBodyLine("pageHelper.setProperties(properties);");
		sqlSessionFactoryBean.addBodyLine("bean.setPlugins(new Interceptor[] { pageHelper });");
		sqlSessionFactoryBean.addBodyLine("");
		if(engine.isNeedEnum()) {
			sqlSessionFactoryBean.addBodyLine("//类型handler");
			sqlSessionFactoryBean.addBodyLine("bean.setTypeHandlersPackage(\""+getEnumRoot()+"\");");
		}
		sqlSessionFactoryBean.addBodyLine("try {");
		sqlSessionFactoryBean.addBodyLine("return bean.getObject();");
		sqlSessionFactoryBean.addBodyLine("} catch (Exception e) {");
		sqlSessionFactoryBean.addBodyLine("e.printStackTrace();");
		sqlSessionFactoryBean.addBodyLine("throw new RuntimeException(e);");
		sqlSessionFactoryBean.addBodyLine("}");
		//---------------------
		config.addMethod(sqlSessionFactoryBean);
		
		Method sqlSessionTemplate = new Method("sqlSessionTemplate");
		sqlSessionTemplate.addAnnotation("@Bean");
		sqlSessionTemplate.setVisibility(JavaVisibility.PUBLIC);
		Parameter sqlSessionFactory = new Parameter(new FullyQualifiedJavaType("org.apache.ibatis.session.SqlSessionFactory"), "sqlSessionFactory");
		sqlSessionTemplate.addParameter(sqlSessionFactory);
		sqlSessionTemplate.setReturnType(new FullyQualifiedJavaType("org.mybatis.spring.SqlSessionTemplate"));
		sqlSessionTemplate.addBodyLine("return new SqlSessionTemplate(sqlSessionFactory);");
		config.addMethod(sqlSessionTemplate);
		
		config.addImportedType("org.springframework.context.annotation.Configuration");
		config.addImportedType("org.springframework.boot.autoconfigure.EnableAutoConfiguration");
		config.addImportedType("org.springframework.boot.autoconfigure.AutoConfigureAfter");
		config.addImportedType("org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration");
		config.addImportedType("org.mybatis.spring.annotation.MapperScan");
		config.addImportedType("org.springframework.transaction.annotation.EnableTransactionManagement");
		config.addImportedType("org.springframework.transaction.annotation.TransactionManagementConfigurer");
		
		config.addImportedType("org.springframework.beans.factory.annotation.Autowired");
		config.addImportedType("javax.sql.DataSource");
		config.addImportedType("org.springframework.transaction.PlatformTransactionManager");
		config.addImportedType("org.springframework.jdbc.datasource.DataSourceTransactionManager");
		config.addImportedType("org.springframework.context.annotation.Bean");
		
		config.addImportedType("org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean");
		config.addImportedType("org.mybatis.spring.SqlSessionFactoryBean");
		config.addImportedType("org.mybatis.spring.boot.autoconfigure.SpringBootVFS");
		config.addImportedType("java.util.Properties");
		config.addImportedType(engine.getPagingInterceptor());
		config.addImportedType("org.mybatis.spring.SqlSessionTemplate");
		config.addImportedType("org.apache.ibatis.session.SqlSessionFactory");
		config.addImportedType("org.apache.ibatis.plugin.Interceptor");
		
		List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
		answer.add(config);
		return answer;
	}
	
	private String getEnumRoot() {
		StringBuilder sb = new StringBuilder();
        sb.append(engine.getGroupId());
        sb.append('.');
        sb.append("entity");
        sb.append('.');
        sb.append("etype");
        return sb.toString();
	}
	
	private String getHandlerName(){
		return "LocalEnumTypeHandler";
	}
	
	private String getLocalEnumTypeHandler() {
		StringBuilder sb = new StringBuilder();
        sb.append(getEnumRoot());
        sb.append('.');
        sb.append(getHandlerName());
        return sb.toString();
	}
	
	private String getEnumConfigType() {
		StringBuilder sb = new StringBuilder();
        sb.append(engine.getGroupId());
        sb.append('.');
        sb.append("config");
        sb.append('.');
        sb.append("MybatisConfig");
        return sb.toString();
	}
	
	public String getExtMapperRoot() {
		StringBuilder sb = new StringBuilder();
        sb.append(engine.getGroupId());
        sb.append('.');
        sb.append("mapper");
        sb.append('.');
        sb.append("ext");
        return sb.toString();
	}
}
