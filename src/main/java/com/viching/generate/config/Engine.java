package com.viching.generate.config;

import java.io.Serializable;
import java.util.Properties;

public class Engine implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 数据库类型:mysql,oracle
	 */
	private String db;
	/**
	 * 驱动包位置
	 */
	private String jarpath;
	/**
	 * 驱动类
	 */
	private String driverClass;
	/**
	 * 数据库链接地址
	 */
	private String connectionURL;
	/**
	 * 用户名称
	 */
	private String user;
	/**
	 * 密码
	 */
	private String password;
	/**
	 * 项目名称
	 */
	private String projectName;
	/*
	 * 项目分组
	 */
	private String groupId;
	/**
	 * 项目唯一标识
	 */
	private String artifactId;
	/**
	 * 版本
	 */
	private String version;
	/**
	 * 相关数据表
	 */
	private String tables;

	/**
	 * 是否生成mapper扩展类
	 */
	private boolean ext;
	/**
	 * 是否将byte类型转换枚举类型
	 */
	private boolean needEnum;
	/**
	 * 是否生成service
	 */
	private boolean service;
	/**
	 * 是否生成controller
	 */
	private boolean controller;
	/**
	 * 生成文件相对位置
	 */
	private String root;
	/**
	 * 文件编码格式
	 */
	private String charCode = "UTF-8";
	/**
	 * 枚举类型对应接口
	 */
	private String enumRoot;
	/**
	 * 枚举类型解析handler
	 */
	private String enumTypeHandler;
	/**
	 * 分页组件中间对象
	 */
	private String paging;
	/**
	 * 分页语句组装类
	 */
	private String pagingProvider;
	/**
	 * ID生成器
	 */
	private String idGenerator;
	/**
	 * 分页插件
	 */
	private String pagingInterceptor;
	/**
	 * 超级service观察者接口
	 */
	private String superIService;
	/**
	 * 超级service观察者实现类
	 */
	private String superService;
	/**
	 * 排除数据表
	 */
	private String filter;

	public Engine() {
		super();
	}

	public Properties getJDBCTarget() {
		Properties target = new Properties();
		target.put("user", this.user);
		target.put("password", this.password);
		target.put("connectionURL", this.connectionURL);
		return target;
	}

	public String getDb() {
		return db;
	}

	public void setDb(String db) {
		this.db = db;
	}

	public String getJarpath() {
		return jarpath;
	}

	public void setJarpath(String jarpath) {
		this.jarpath = jarpath;
	}

	public String getDriverClass() {
		return driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public String getConnectionURL() {
		return connectionURL;
	}

	public void setConnectionURL(String connectionURL) {
		this.connectionURL = connectionURL;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getTables() {
		return tables;
	}

	public void setTables(String tables) {
		this.tables = tables;
	}

	public boolean isExt() {
		return ext;
	}

	public void setExt(boolean ext) {
		this.ext = ext;
	}

	public boolean isNeedEnum() {
		return needEnum;
	}

	public void setNeedEnum(boolean needEnum) {
		this.needEnum = needEnum;
	}

	public boolean isService() {
		return service;
	}

	public void setService(boolean service) {
		this.service = service;
	}

	public boolean isController() {
		return controller;
	}

	public void setController(boolean controller) {
		this.controller = controller;
	}

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public String getCharCode() {
		return charCode;
	}

	public void setCharCode(String charCode) {
		this.charCode = charCode;
	}

	public String getEnumRoot() {
		return enumRoot;
	}

	public void setEnumRoot(String enumRoot) {
		this.enumRoot = enumRoot;
	}

	public String getEnumTypeHandler() {
		return enumTypeHandler;
	}

	public void setEnumTypeHandler(String enumTypeHandler) {
		this.enumTypeHandler = enumTypeHandler;
	}

	public String getPaging() {
		return paging;
	}

	public void setPaging(String paging) {
		this.paging = paging;
	}

	public String getPagingProvider() {
		return pagingProvider;
	}

	public void setPagingProvider(String pagingProvider) {
		this.pagingProvider = pagingProvider;
	}

	public String getIdGenerator() {
		return idGenerator;
	}

	public void setIdGenerator(String idGenerator) {
		this.idGenerator = idGenerator;
	}

	public String getPagingInterceptor() {
		return pagingInterceptor;
	}

	public void setPagingInterceptor(String pagingInterceptor) {
		this.pagingInterceptor = pagingInterceptor;
	}

	public String getSuperIService() {
		return superIService;
	}

	public void setSuperIService(String superIService) {
		this.superIService = superIService;
	}

	public String getSuperService() {
		return superService;
	}

	public void setSuperService(String superService) {
		this.superService = superService;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}
}
