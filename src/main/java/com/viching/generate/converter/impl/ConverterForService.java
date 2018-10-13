package com.viching.generate.converter.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.viching.generate.config.Engine;
import com.viching.generate.converter.ConverterDriver;
import com.viching.generate.db.util.JavaBeansUtil;
import com.viching.generate.elements.java.CompilationUnit;
import com.viching.generate.elements.java.Field;
import com.viching.generate.elements.java.FullyQualifiedJavaType;
import com.viching.generate.elements.java.Interface;
import com.viching.generate.elements.java.JavaVisibility;
import com.viching.generate.elements.java.Method;
import com.viching.generate.elements.java.Parameter;
import com.viching.generate.elements.java.TopLevelClass;
import com.viching.generate.source.DBColumnJavaBean;
import com.viching.generate.source.DBTableJavaBean;

//@Component
public class ConverterForService extends ConverterDriver {
	
	@Autowired
	private Engine engine;

	@Override
	public List<CompilationUnit> getCompilationUnits(DBTableJavaBean dbTable) {

		FullyQualifiedJavaType type = new FullyQualifiedJavaType(dbTable.getServiceType());
		Interface topInterface = new Interface(type);
		topInterface.setVisibility(JavaVisibility.PUBLIC);
		commentGenerator.addJavaFileComment(topInterface);

		FullyQualifiedJavaType paramType = new FullyQualifiedJavaType(
				dbTable.getBaseRecordType());
		topInterface.addImportedType(paramType);

		FullyQualifiedJavaType list = FullyQualifiedJavaType
				.getNewListInstance();
		list.addTypeArgument(paramType);
		topInterface.addImportedType(list);

		FullyQualifiedJavaType paging = new FullyQualifiedJavaType(engine.getPaging());
		topInterface.addImportedType(paging);

		List<DBColumnJavaBean> keys = dbTable
				.getPrimaryKeyColumns();

		// add
		Method method = new Method("add");
		method.setReturnType(new FullyQualifiedJavaType("void"));
		method.addParameter(new Parameter(paramType, "record")); //$NON-NLS-1$
		method.addException(new FullyQualifiedJavaType("java.lang.Exception"));
		commentGenerator.addGeneralMethodComment(method,
				dbTable);
		topInterface.addMethod(method);

		// update
		method = new Method("update");
		method.setReturnType(new FullyQualifiedJavaType("void"));
		method.addParameter(new Parameter(paramType, "record")); //$NON-NLS-1$
		method.addException(new FullyQualifiedJavaType("java.lang.Exception"));
		commentGenerator.addGeneralMethodComment(method,
				dbTable);
		topInterface.addMethod(method);

		// addOrUpdate
		method = new Method("addOrUpdate");
		method.setReturnType(new FullyQualifiedJavaType("void"));
		method.addParameter(new Parameter(paramType, "record")); //$NON-NLS-1$
		method.addException(new FullyQualifiedJavaType("java.lang.Exception"));
		commentGenerator.addGeneralMethodComment(method,
				dbTable);
		topInterface.addMethod(method);

		// selectById
		method = new Method("selectByPrimaryKey");
		method.setReturnType(paramType);
		for (DBColumnJavaBean c : keys) {
			method.addParameter(new Parameter(c.getFullyQualifiedJavaType(), c
					.getJavaProperty())); //$NON-NLS-1$
		}
		method.addException(new FullyQualifiedJavaType("java.lang.Exception"));
		commentGenerator.addGeneralMethodComment(method,
				dbTable);
		topInterface.addMethod(method);

		// selectBySelective
		method = new Method("selectBySelective");
		method.setReturnType(list);
		method.addParameter(new Parameter(paramType, "record")); //$NON-NLS-1$
		method.addException(new FullyQualifiedJavaType("java.lang.Exception"));
		commentGenerator.addGeneralMethodComment(method,
				dbTable);
		topInterface.addMethod(method);

		// countBySelective
		method = new Method("countBySelective");
		method.setReturnType(FullyQualifiedJavaType.getIntInstance());
		method.addParameter(new Parameter(paramType, "record")); //$NON-NLS-1$
		method.addException(new FullyQualifiedJavaType("java.lang.Exception"));
		commentGenerator.addGeneralMethodComment(method,
				dbTable);
		topInterface.addMethod(method);

		// delById
		method = new Method("delByPrimaryKey");
		method.setReturnType(new FullyQualifiedJavaType("void"));
		for (DBColumnJavaBean c : keys) {
			method.addParameter(new Parameter(c.getFullyQualifiedJavaType(), c
					.getJavaProperty())); //$NON-NLS-1$
		}
		method.addException(new FullyQualifiedJavaType("java.lang.Exception"));
		commentGenerator.addGeneralMethodComment(method,
				dbTable);
		topInterface.addMethod(method);

		// delBySelective
		method = new Method("delBySelective");
		method.setReturnType(new FullyQualifiedJavaType("void"));
		method.addParameter(new Parameter(paramType, "record")); //$NON-NLS-1$
		method.addException(new FullyQualifiedJavaType("java.lang.Exception"));
		commentGenerator.addGeneralMethodComment(method,
				dbTable);
		topInterface.addMethod(method);

		// paging
		method = new Method("paging");
		method.setReturnType(new FullyQualifiedJavaType("void"));
		method.addParameter(new Parameter(paging, "paging")); //$NON-NLS-1$
		method.addException(new FullyQualifiedJavaType("java.lang.Exception"));
		commentGenerator.addGeneralMethodComment(method,
				dbTable);
		topInterface.addMethod(method);

		// addBatch
		method = new Method("addBatch");
		method.setReturnType(new FullyQualifiedJavaType("void"));
		method.addParameter(new Parameter(list, "list")); //$NON-NLS-1$
		method.addException(new FullyQualifiedJavaType("java.lang.Exception"));
		commentGenerator.addGeneralMethodComment(method,
				dbTable);
		topInterface.addMethod(method);

		// updateBatch
		method = new Method("updateBatch");
		method.setReturnType(new FullyQualifiedJavaType("void"));
		method.addParameter(new Parameter(list, "list")); //$NON-NLS-1$
		method.addException(new FullyQualifiedJavaType("java.lang.Exception"));
		commentGenerator.addGeneralMethodComment(method,
				dbTable);
		topInterface.addMethod(method);

		// checkDelEnable
		method = new Method("checkDelEnable");
		method.setReturnType(FullyQualifiedJavaType
				.getBooleanPrimitiveInstance());
		for (DBColumnJavaBean c : keys) {
			method.addParameter(new Parameter(c.getFullyQualifiedJavaType(), c
					.getJavaProperty())); //$NON-NLS-1$
		}
		method.addException(new FullyQualifiedJavaType("java.lang.Exception"));
		commentGenerator.addGeneralMethodComment(method,
				dbTable);
		topInterface.addMethod(method);

		// deleteBatch
		if (keys.size() == 1) {
			method = new Method("deleteBatch");
			method.setReturnType(new FullyQualifiedJavaType("void"));
			FullyQualifiedJavaType ptype = FullyQualifiedJavaType
					.getNewListInstance();
			ptype.addTypeArgument(new FullyQualifiedJavaType(dbTable.getPrimaryKeyType()));
			method.addParameter(new Parameter(ptype, "list")); //$NON-NLS-1$
			method.addException(new FullyQualifiedJavaType(
					"java.lang.Exception"));
			commentGenerator.addGeneralMethodComment(method,
					dbTable);
			topInterface.addMethod(method);
		}

		List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
		answer.add(topInterface);
		
		List<CompilationUnit> extraCompilationUnits = getExtraCompilationUnits(dbTable);
        if (extraCompilationUnits != null) {
            answer.addAll(extraCompilationUnits);
        }
        
		return answer;
	}

	public List<CompilationUnit> getExtraCompilationUnits(DBTableJavaBean dbTable) {

        FullyQualifiedJavaType superInterface = new FullyQualifiedJavaType(dbTable.getServiceType());
        FullyQualifiedJavaType type = new FullyQualifiedJavaType(dbTable.getServiceImplType());
        TopLevelClass topLevelClass = new TopLevelClass(type);
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(topLevelClass);

        topLevelClass.addSuperInterface(superInterface);
        topLevelClass.addImportedType(superInterface);
        topLevelClass.addAnnotation("@Service");
        topLevelClass.addImportedType(new FullyQualifiedJavaType("org.springframework.stereotype.Service"));

        FullyQualifiedJavaType paramType = new FullyQualifiedJavaType(dbTable.getBaseRecordType());
        topLevelClass.addImportedType(paramType);

        FullyQualifiedJavaType list = FullyQualifiedJavaType.getNewListInstance();
        list.addTypeArgument(paramType);
        topLevelClass.addImportedType(list);

        FullyQualifiedJavaType arraylist = FullyQualifiedJavaType.getNewArrayListInstance();
        arraylist.addTypeArgument(paramType);
        topLevelClass.addImportedType(arraylist);

        FullyQualifiedJavaType paging = new FullyQualifiedJavaType(engine.getPaging());
        topLevelClass.addImportedType(paging);

        topLevelClass.addImportedType(new FullyQualifiedJavaType("org.apache.commons.lang3.StringUtils"));
        topLevelClass.addImportedType(new FullyQualifiedJavaType("org.springframework.transaction.annotation.Transactional"));
        
        Field log = new Field();
        log.setVisibility(JavaVisibility.PRIVATE);
        log.setFinal(true);
        log.setName("logger");
        FullyQualifiedJavaType logType = new FullyQualifiedJavaType("org.slf4j.Logger");
        log.setType(logType);
        log.addAnnotation("@SuppressWarnings(\"unused\")");
        log.setInitializationString("LoggerFactory.getLogger(this.getClass())");
        topLevelClass.addField(log);
        FullyQualifiedJavaType logFactoryType = new FullyQualifiedJavaType("org.slf4j.LoggerFactory");
        topLevelClass.addImportedType(logType);
        topLevelClass.addImportedType(logFactoryType);
        
        // 添加默认属性
        Field field = new Field();
        field.setVisibility(JavaVisibility.PRIVATE);
        field.setType(new FullyQualifiedJavaType(dbTable.getExtMapperType()));
        String mapperName = dbTable.getMapperName();
        field.setName(mapperName);
        topLevelClass.addImportedType(field.getType());
        topLevelClass.addImportedType(new FullyQualifiedJavaType("org.springframework.beans.factory.annotation.Autowired"));
        field.addAnnotation("@Autowired");
        topLevelClass.addField(field);

        /**
         * spring 事务的一些规则
         * PROPAGATION_REQUIRED -- 支持当前事务，如果当前没有事务，就新建一个事务。这是最常见的选择。 
         * PROPAGATION_SUPPORTS -- 支持当前事务，如果当前没有事务，就以非事务方式执行。 
         * PROPAGATION_MANDATORY -- 支持当前事务，如果当前没有事务，就抛出异常。 
         * PROPAGATION_REQUIRES_NEW -- 新建事务，如果当前存在事务，把当前事务挂起。 
         * PROPAGATION_NOT_SUPPORTED -- 以非事务方式执行操作，如果当前存在事务，就把当前事务挂起。 
         * PROPAGATION_NEVER -- 以非事务方式执行，如果当前存在事务，则抛出异常。 
         * PROPAGATION_NESTED -- 如果当前存在事务，则在嵌套事务内执行。如果当前没有事务，则进行与PROPAGATION_REQUIRED类似的操作。
         * 
         * Transactional中propagation的默认值是：Propagation.REQUIRED，，即当一个事务中被其它事务调用时，该事务会遵循调用者开启的事务，而不会重新开启事务
         * 原码如下：
         * Propagation propagation() default Propagation.REQUIRED;
         */
        List<DBColumnJavaBean> keys = dbTable.getPrimaryKeyColumns();
        List<String> keyParams = new ArrayList<String>();
        for (DBColumnJavaBean c : keys) {
            keyParams.add(c.getJavaProperty());
        }

        //add
        Method method = new Method("add");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addException(new FullyQualifiedJavaType("java.lang.Exception"));
        method.addAnnotation("@Override");
        method.addAnnotation("@Transactional(rollbackFor = Exception.class)");
        method.setReturnType(new FullyQualifiedJavaType("void"));
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addParameter(new Parameter(paramType, "record")); //$NON-NLS-1$
        method.addBodyLine("");
        method.addBodyLine("// TODO");
        generatePrimaryKey(method, keys, topLevelClass, paramType, false, true);
        method.addBodyLine(String.format("%s.insertSelective(%s);", mapperName, "record"));
        method.addBodyLine("");
        topLevelClass.addMethod(method);

        //update
        method = new Method("update");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addException(new FullyQualifiedJavaType("java.lang.Exception"));
        method.addAnnotation("@Override");
        method.addAnnotation("@Transactional(rollbackFor = Exception.class)");
        method.setReturnType(new FullyQualifiedJavaType("void"));
        method.addParameter(new Parameter(paramType, "record")); //$NON-NLS-1$
        commentGenerator.addGeneralMethodComment(method, dbTable);
        method.addBodyLine("");
        method.addBodyLine("// TODO");
        method.addBodyLine(String.format("%s.updateByPrimaryKey(%s);", mapperName, "record"));
        method.addBodyLine("");
        topLevelClass.addMethod(method);

        //addOrUpdate
        method = new Method("addOrUpdate");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType("void"));
        method.addAnnotation("@Override");
        method.addAnnotation("@Transactional(rollbackFor = Exception.class)");
        method.addParameter(new Parameter(paramType, "record")); //$NON-NLS-1$
        method.addException(new FullyQualifiedJavaType("java.lang.Exception"));
        commentGenerator.addGeneralMethodComment(method, dbTable);
        method.addBodyLine("");
        List<String> conditions = new ArrayList<String>();
        for (DBColumnJavaBean c : keys) {
            conditions.add(String.format("StringUtils.isBlank(record.%s())", JavaBeansUtil.getGetterMethodName(c.getJavaProperty(), c.getFullyQualifiedJavaType())));
        }
        method.addBodyLine(String.format("if(%s){", StringUtils.join(conditions, "&&")));
        generatePrimaryKey(method, keys, topLevelClass, paramType, false, false);
        method.addBodyLine(String.format("%s.insertSelective(%s);", mapperName, "record"));
        method.addBodyLine("}else{");
        method.addBodyLine(String.format("%s.updateByPrimaryKeySelective(%s);", mapperName, "record"));
        method.addBodyLine("}");
        method.addBodyLine("");
        topLevelClass.addMethod(method);

        //selectById
        method = new Method("selectByPrimaryKey");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addException(new FullyQualifiedJavaType("java.lang.Exception"));
        method.addAnnotation("@Override");
        method.addAnnotation("@Transactional(readOnly = true)");
        method.setReturnType(paramType);
        for (DBColumnJavaBean c : keys) {
            method.addParameter(new Parameter(c.getFullyQualifiedJavaType(), c.getJavaProperty())); //$NON-NLS-1$
        }
        commentGenerator.addGeneralMethodComment(method, dbTable);
        method.addBodyLine("");
        method.addBodyLine("// TODO");
        method.addBodyLine(String.format("return %s.selectByPrimaryKey(%s);", mapperName, StringUtils.join(keyParams, ".")));
        method.addBodyLine("");
        topLevelClass.addMethod(method);

        //selectBySelective
        method = new Method("selectBySelective");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addException(new FullyQualifiedJavaType("java.lang.Exception"));
        method.addAnnotation("@Override");
        method.addAnnotation("@Transactional(readOnly = true)");
        method.setReturnType(list);
        method.addParameter(new Parameter(paramType, "record")); //$NON-NLS-1$
        commentGenerator.addGeneralMethodComment(method, dbTable);
        method.addBodyLine("");
        method.addBodyLine(String.format("%s data = %s.selectBySelective(%s);", list.getShortName(), mapperName, "record"));
        method.addBodyLine("if(data == null){");
        method.addBodyLine(String.format("data = new %s();", arraylist.getShortName()));
        method.addBodyLine("}");
        method.addBodyLine("return data;");
        method.addBodyLine("");
        topLevelClass.addMethod(method);
        
        //countBySelective
        method = new Method("countBySelective");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addException(new FullyQualifiedJavaType("java.lang.Exception"));
        method.addAnnotation("@Override");
        method.addAnnotation("@Transactional(readOnly = true)");
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.addParameter(new Parameter(paramType, "record")); //$NON-NLS-1$
        commentGenerator.addGeneralMethodComment(method, dbTable);
        method.addBodyLine("");
        method.addBodyLine(String.format("return %s.countBySelective(%s);", mapperName, "record"));
        method.addBodyLine("");
        topLevelClass.addMethod(method);

        //delById
        method = new Method("delByPrimaryKey");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addException(new FullyQualifiedJavaType("java.lang.Exception"));
        method.addAnnotation("@Override");
        method.addAnnotation("@Transactional(rollbackFor = Exception.class)");
        method.setReturnType(new FullyQualifiedJavaType("void"));
        for (DBColumnJavaBean c : keys) {
            method.addParameter(new Parameter(c.getFullyQualifiedJavaType(), c.getJavaProperty())); //$NON-NLS-1$
        }
        commentGenerator.addGeneralMethodComment(method, dbTable);
        method.addBodyLine("");
        method.addBodyLine("// TODO");
        method.addBodyLine(String.format("%s.deleteByPrimaryKey(%s);", mapperName, StringUtils.join(keyParams, ".")));
        method.addBodyLine("");
        topLevelClass.addMethod(method);

        //delBySelective
        method = new Method("delBySelective");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addException(new FullyQualifiedJavaType("java.lang.Exception"));
        method.addAnnotation("@Override");
        method.addAnnotation("@Transactional(rollbackFor = Exception.class)");
        method.setReturnType(new FullyQualifiedJavaType("void"));
        method.addParameter(new Parameter(paramType, "record")); //$NON-NLS-1$
        commentGenerator.addGeneralMethodComment(method, dbTable);
        method.addBodyLine("");
        method.addBodyLine("// TODO");
        method.addBodyLine(String.format("%s.deleteBySelective(%s);", mapperName, "record"));
        method.addBodyLine("");
        topLevelClass.addMethod(method);

        //paging
        method = new Method("paging");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addException(new FullyQualifiedJavaType("java.lang.Exception"));
        method.addAnnotation("@Override");
        method.addAnnotation("@Transactional(readOnly = true)");
        method.setReturnType(new FullyQualifiedJavaType("void"));
        method.addParameter(new Parameter(paging, "paging")); //$NON-NLS-1$
        commentGenerator.addGeneralMethodComment(method, dbTable);
        method.addBodyLine("");
        method.addBodyLine("// TODO");
        method.addBodyLine(String.format("%s data = %s.paging(%s);", list.getShortName(), mapperName, "paging"));
        method.addBodyLine("if(data == null){");
        method.addBodyLine(String.format("data = new %s();", arraylist.getShortName()));
        method.addBodyLine("}");
        method.addBodyLine("paging.setData(data);");
        method.addBodyLine("");
        topLevelClass.addMethod(method);

        //addBatch
        method = new Method("addBatch");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addException(new FullyQualifiedJavaType("java.lang.Exception"));
        method.addAnnotation("@Override");
        method.addAnnotation("@Transactional(rollbackFor = Exception.class)");
        method.setReturnType(new FullyQualifiedJavaType("void"));
        method.addParameter(new Parameter(list, "list")); //$NON-NLS-1$
        commentGenerator.addGeneralMethodComment(method, dbTable);
        method.addBodyLine("");
        method.addBodyLine("// TODO");
        method.addBodyLine("if(list == null || list.size() == 0){");
        method.addBodyLine("return;");
        method.addBodyLine("}");
        generatePrimaryKey(method, keys, topLevelClass, paramType, true, true);
        method.addBodyLine(String.format("%s.insertBatch(%s);", mapperName, "list"));
        method.addBodyLine("");
        topLevelClass.addMethod(method);

        //updateBatch
        method = new Method("updateBatch");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addException(new FullyQualifiedJavaType("java.lang.Exception"));
        method.addAnnotation("@Override");
        method.addAnnotation("@Transactional(rollbackFor = Exception.class)");
        method.setReturnType(new FullyQualifiedJavaType("void"));
        method.addParameter(new Parameter(list, "list")); //$NON-NLS-1$
        commentGenerator.addGeneralMethodComment(method, dbTable);
        method.addBodyLine("");
        method.addBodyLine("// TODO");
        method.addBodyLine("if(list == null || list.size() == 0){");
        method.addBodyLine("return;");
        method.addBodyLine("}");
        method.addBodyLine(String.format("%s.updateBatch(%s);", mapperName, "list"));
        method.addBodyLine("");
        topLevelClass.addMethod(method);

        //checkDelEnable
        method = new Method("checkDelEnable");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addException(new FullyQualifiedJavaType("java.lang.Exception"));
        method.addAnnotation("@Override");
        method.addAnnotation("@Transactional(readOnly = true)");
        method.setReturnType(FullyQualifiedJavaType.getBooleanPrimitiveInstance());
        for (DBColumnJavaBean c : keys) {
            method.addParameter(new Parameter(c.getFullyQualifiedJavaType(), c.getJavaProperty())); //$NON-NLS-1$
        }
        commentGenerator.addGeneralMethodComment(method, dbTable);
        method.addBodyLine("");
        method.addBodyLine("// TODO");
        method.addBodyLine("return false;");
        method.addBodyLine("");
        topLevelClass.addMethod(method);

        //deleteBatch
        if (keys.size() == 1) {
            method = new Method("deleteBatch");
            method.setVisibility(JavaVisibility.PUBLIC);
            method.setReturnType(new FullyQualifiedJavaType("void"));
            FullyQualifiedJavaType ptype = FullyQualifiedJavaType.getNewListInstance();
            ptype.addTypeArgument(new FullyQualifiedJavaType(dbTable.getPrimaryKeyType()));
            method.addParameter(new Parameter(ptype, "list")); //$NON-NLS-1$
            method.addException(new FullyQualifiedJavaType("java.lang.Exception"));
            method.addAnnotation("@Override");
            method.addAnnotation("@Transactional(rollbackFor = Exception.class)");
            commentGenerator.addGeneralMethodComment(method, dbTable);
            method.addBodyLine("");
            method.addBodyLine("// TODO");
            method.addBodyLine("if(list == null || list.size() == 0){");
            method.addBodyLine("return;");
            method.addBodyLine("}");
            method.addBodyLine(String.format("%s.deleteBatch(%s);", mapperName, "list"));
            method.addBodyLine("");
            topLevelClass.addMethod(method);
        }

        List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
        answer.add(topLevelClass);

        return answer;
    }
	
	private void generatePrimaryKey(Method method, List<DBColumnJavaBean> keys, TopLevelClass topLevelClass, FullyQualifiedJavaType paramType, boolean isBatch, boolean onlyAdd){
        if(keys == null || keys.size() > 1){
            return;
        }
        DBColumnJavaBean c = keys.get(0);
        if(c.isStringColumn()){
            topLevelClass.addImportedType(new FullyQualifiedJavaType("org.apache.commons.lang3.StringUtils"));
            topLevelClass.addImportedType(new FullyQualifiedJavaType(engine.getIdGenerator()));
            if(isBatch){
                method.addBodyLine(String.format("for(%s record: list){", paramType.getShortName()));
                method.addBodyLine(String.format("if(StringUtils.isBlank(record.%s())){", JavaBeansUtil.getGetterMethodName(c.getJavaProperty(), c.getFullyQualifiedJavaType())));
                method.addBodyLine(String.format("record.%s(IDGenerator.generate());", JavaBeansUtil.getSetterMethodName(c.getJavaProperty())));
                method.addBodyLine("}");
                method.addBodyLine("}");
            }else{
                if(onlyAdd){
                    method.addBodyLine(String.format("if(StringUtils.isBlank(record.%s())){", JavaBeansUtil.getGetterMethodName(c.getJavaProperty(), c.getFullyQualifiedJavaType())));
                    method.addBodyLine(String.format("record.%s(IDGenerator.generate());", JavaBeansUtil.getSetterMethodName(c.getJavaProperty())));
                    method.addBodyLine("}"); 
                }else{
                    method.addBodyLine(String.format("record.%s(IDGenerator.generate());", JavaBeansUtil.getSetterMethodName(c.getJavaProperty())));
                }
                
            }
        }
    }
	
	/*private String getUtilType(String className) {
    	String type = "com.viching.bootstrap";
        String strs[] = type.split("\\.");
        List<String> list = new ArrayList<String>();
        list.addAll(Arrays.asList(strs));
        list.add("plugin");
        list.add(className);
        return StringUtils.join(list, ".");
    }*/
}
