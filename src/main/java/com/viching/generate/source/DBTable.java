package com.viching.generate.source;

import static com.viching.generate.db.util.StringUtility.composeFullyQualifiedTableName;

import java.io.Serializable;

public class DBTable implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 目录
	 */
	private String catalog;
	/**
	 * 数据库名称
	 */
    private String schema;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 备注
     */
    private String remarks;

	public String getCatalog() {
		return catalog;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getSource() {
        return composeFullyQualifiedTableName(catalog, schema, tableName, '.');
    }
}
