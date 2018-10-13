package com.viching.generate.config.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.viching.generate.config.Engine;
import com.viching.generate.config.IConnection;
import com.viching.generate.config.Investigator;
import com.viching.generate.config.JavaTypeResolver;
import com.viching.generate.db.MetaDataConstants;
import com.viching.generate.db.util.JavaBeansUtil;
import com.viching.generate.elements.java.FullyQualifiedJavaType;
import com.viching.generate.source.DBColumnJavaBean;
import com.viching.generate.source.DBTableJavaBean;

public class InvestigatorImpl implements Investigator {
	
	@Autowired
	private Engine engine;
	// 数据库连接
	@Autowired
	private IConnection iConnection;
	@Autowired
	private JavaTypeResolver javaTypeResolver;

	@Override
	public List<DBTableJavaBean> introspectTables() throws Exception {

		List<DBTableJavaBean> introspectedTables = new ArrayList<DBTableJavaBean>();

		Connection connection = null;
		ResultSet rs = null;
		try {
			connection = iConnection.getConnection();
			DatabaseMetaData metaData = connection.getMetaData();
			rs = metaData.getTables(connection.getCatalog(),
					MetaDataConstants.ROOT, null, new String[] { "TABLE" });
			DBTableJavaBean it = null;
			if (engine.getTables().equalsIgnoreCase("ALL")) {
				while (rs.next()) {
					it = new DBTableJavaBean(engine.getGroupId());
					it.setCatalog(rs.getString(MetaDataConstants.TABLE_CAT));
					it.setSchema(rs.getString(MetaDataConstants.TABLE_SCHEM));
					it.setTableName(rs.getString(MetaDataConstants.TABLE_NAME));
					if(StringUtils.isNotBlank(engine.getFilter()) && engine.getFilter().indexOf(it.getTableName().toLowerCase()) != -1){
						continue;
					}
					it.setRemarks(rs.getString(MetaDataConstants.REMARKS));
					introspectedTables.add(it);
				}
				iConnection.closeResultSet(rs);
			}
			DBColumnJavaBean ic = null;
			Statement stmt = null;
			// 获取表的备注信息
			for (DBTableJavaBean table : introspectedTables) {
				// 备注
				stmt = metaData.getConnection().createStatement();
				rs = stmt.executeQuery("SHOW TABLE STATUS LIKE '"
						+ table.getTableName() + "'");
				while (rs.next()) {
					table.setRemarks(rs.getString(MetaDataConstants.COMMENT));
				}
				iConnection.closeResultSet(rs);
				stmt.close();

				// 主键列
				rs = metaData.getPrimaryKeys(table.getCatalog(),
						table.getSchema(), table.getTableName());
				Map<String, Short> keyColumns = new TreeMap<String, Short>();
				while (rs.next()) {
					keyColumns.putAll(getKeyColumn(rs));
				}
				iConnection.closeResultSet(rs);

				// 所有列
				rs = metaData.getColumns(table.getCatalog(), table.getSchema(),
						table.getTableName(), null);
				while (rs.next()) {
					ic = getColumn(rs, table);
					if (keyColumns.containsKey(ic.getColumnName())) {
						ic.setIdentity(true);
						table.addPrimaryKeyColumn(ic);
					} else {
						table.addColumn(ic);
					}
				}
				iConnection.closeResultSet(rs);
			}

		} finally {
			iConnection.closeResultSet(rs);
			iConnection.closeConnection(connection);
		}
		return introspectedTables;
	}

	private DBColumnJavaBean getColumn(ResultSet rs, DBTableJavaBean dbTable) throws SQLException {
		DBColumnJavaBean ic = new DBColumnJavaBean();

		ic.setJdbcType(rs.getInt(MetaDataConstants.DATA_TYPE));
		ic.setLength(rs.getInt(MetaDataConstants.COLUMN_SIZE));
		ic.setColumnName(rs.getString(MetaDataConstants.COLUMN_NAME));
		ic.setNullable(rs.getInt(MetaDataConstants.NULLABLE) == DatabaseMetaData.columnNullable);
		ic.setScale(rs.getInt(MetaDataConstants.DECIMAL_DIGITS));
		ic.setRemarks(rs.getString(MetaDataConstants.REMARKS));
		ic.setDefaultValue(rs.getString(MetaDataConstants.COLUMN_DEF));
		ic.setJavaProperty(JavaBeansUtil.getCamelCaseString(ic.getColumnName(), false));

		FullyQualifiedJavaType fullyQualifiedJavaType = javaTypeResolver.calculateJavaType(ic, dbTable);
		ic.setFullyQualifiedJavaType(fullyQualifiedJavaType);
		ic.setJdbcTypeName(javaTypeResolver.calculateJdbcTypeName(ic));
		return ic;
	}

	private Map<String, Short> getKeyColumn(ResultSet rs) throws SQLException {
		Map<String, Short> keyColumns = new TreeMap<String, Short>();

		keyColumns.put(rs.getString(MetaDataConstants.COLUMN_NAME),
				rs.getShort(MetaDataConstants.KEY_SEQ));

		return keyColumns;
	}

}
