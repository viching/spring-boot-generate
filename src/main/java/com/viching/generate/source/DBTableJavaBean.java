package com.viching.generate.source;

import static com.viching.generate.db.util.JavaBeansUtil.getCamelCaseString;

import java.util.ArrayList;
import java.util.List;

import com.viching.generate.db.util.JavaBeansUtil;

public class DBTableJavaBean extends DBTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<DBColumnJavaBean> primaryKeyColumns;

	private List<DBColumnJavaBean> baseColumns;
	
	private String packBase;

	public DBTableJavaBean(String packBase) {
		super();
		primaryKeyColumns = new ArrayList<DBColumnJavaBean>();
		baseColumns = new ArrayList<DBColumnJavaBean>();
		this.packBase = packBase;
	}

	public List<DBColumnJavaBean> getPrimaryKeyColumns() {
		return primaryKeyColumns;
	}

	public void setPrimaryKeyColumns(List<DBColumnJavaBean> primaryKeyColumns) {
		this.primaryKeyColumns = primaryKeyColumns;
	}

	public List<DBColumnJavaBean> getBaseColumns() {
		return baseColumns;
	}

	public void setBaseColumns(List<DBColumnJavaBean> baseColumns) {
		this.baseColumns = baseColumns;
	}

	public List<DBColumnJavaBean> getAllColumns() {
		List<DBColumnJavaBean> answer = new ArrayList<DBColumnJavaBean>();
		answer.addAll(primaryKeyColumns);
		answer.addAll(baseColumns);

		return answer;
	}

	public void addPrimaryKeyColumn(DBColumnJavaBean ic) {
		primaryKeyColumns.add(ic);
	}

	public void addColumn(DBColumnJavaBean ic) {
		baseColumns.add(ic);
	}

	public boolean hasBaseColumns() {
		return baseColumns.size() > 0;
	}

	public boolean hasJDBCDateColumns() {
		boolean rc = false;

		for (DBColumnJavaBean dbColumn : primaryKeyColumns) {
			if (dbColumn.isJDBCDateColumn()) {
				rc = true;
				break;
			}
		}

		if (!rc) {
			for (DBColumnJavaBean dbColumn : baseColumns) {
				if (dbColumn.isJDBCDateColumn()) {
					rc = true;
					break;
				}
			}
		}

		return rc;
	}

	public boolean hasJDBCTimeColumns() {
		boolean rc = false;

		for (DBColumnJavaBean dbColumn : primaryKeyColumns) {
			if (dbColumn.isJDBCTimeColumn()) {
				rc = true;
				break;
			}
		}

		if (!rc) {
			for (DBColumnJavaBean dbColumn : baseColumns) {
				if (dbColumn.isJDBCTimeColumn()) {
					rc = true;
					break;
				}
			}
		}

		return rc;
	}

	public boolean hasPrimaryKeyColumns() {
		return primaryKeyColumns.size() > 0;
	}
	
	public boolean needGeneratePrimaryKey() {
		return primaryKeyColumns.size() > 1;
	}
	
	public String getDomainObjectName() {
		return getCamelCaseString(this.getTableName(), true);
    }

	public String getPrimaryKeyType() {
		if(primaryKeyColumns.size() == 1){
			return primaryKeyColumns.get(0).getFullyQualifiedJavaType().getFullyQualifiedName();
		}
		StringBuilder sb = new StringBuilder();
        sb.append(packBase);
        sb.append('.');
        sb.append("entity");
        sb.append('.');
        sb.append(getDomainObjectName());
        sb.append("Key"); 
        return sb.toString();
	}

	public String getBaseRecordType() {
		StringBuilder sb = new StringBuilder();
        sb.append(packBase);
        sb.append('.');
        sb.append("entity");
        sb.append('.');
        sb.append(getDomainObjectName());
        return sb.toString();
	}

	public String getEnumType(String columnName) {
		StringBuilder sb = new StringBuilder();
        sb.append(packBase);
        sb.append('.');
        sb.append("entity");
        sb.append('.');
        sb.append("etype");
        sb.append('.');
        sb.append(JavaBeansUtil.getCamelCaseString(columnName, true));
        sb.append("Enum"); 
        return sb.toString();
	}

	public String getDtoType() {
		StringBuilder sb = new StringBuilder();
        sb.append(packBase);
        sb.append('.');
        sb.append("dto");
        sb.append('.');
        sb.append(getDomainObjectName());
        sb.append("DTO"); 
        return sb.toString();
	}

	public String getSearchType() {
		StringBuilder sb = new StringBuilder();
        sb.append(packBase);
        sb.append('.');
        sb.append("search");
        sb.append('.');
        sb.append(getDomainObjectName());
        sb.append("Search"); 
        return sb.toString();
	}

	public String getMapperType() {
		StringBuilder sb = new StringBuilder();
        sb.append(packBase);
        sb.append('.');
        sb.append("mapper");
        sb.append('.');
        sb.append(getDomainObjectName());
        sb.append("Mapper"); 
        return sb.toString();
	}
	
	public String getProviderType() {
		StringBuilder sb = new StringBuilder();
        sb.append(packBase);
        sb.append('.');
        sb.append("mapper");
        sb.append('.');
        sb.append(getDomainObjectName());
        sb.append("Provider"); 
        return sb.toString();
	}

	public String getExtMapperType() {
		StringBuilder sb = new StringBuilder();
        sb.append(packBase);
        sb.append('.');
        sb.append("mapper");
        sb.append('.');
        sb.append("ext");
        sb.append('.');
        sb.append("Ext");
        sb.append(getDomainObjectName());
        sb.append("Mapper"); 
        return sb.toString();
	}

	public String getServiceType() {
		StringBuilder sb = new StringBuilder();
        sb.append(packBase);
        sb.append('.');
        sb.append("service");
        sb.append('.');
        sb.append(getDomainObjectName());
        sb.append("Service"); 
        return sb.toString();
	}

	public String getServiceImplType() {
		StringBuilder sb = new StringBuilder();
        sb.append(packBase);
        sb.append('.');
        sb.append("service");
        sb.append('.');
        sb.append("impl");
        sb.append('.');
        sb.append(getDomainObjectName());
        sb.append("ServiceImpl"); 
        return sb.toString();
	}
	
	public String getCommonControllerType() {
		StringBuilder sb = new StringBuilder();
		sb.append(packBase);
        sb.append('.');
        sb.append("controller");
        sb.append('.');
        sb.append("CommonController"); 
        return sb.toString();
	}

	public String getControllerType() {
		StringBuilder sb = new StringBuilder();
        sb.append(packBase);
        sb.append('.');
        sb.append("controller");
        sb.append('.');
        sb.append(getDomainObjectName());
        sb.append("Controller"); 
        return sb.toString();
	}

	public boolean isConstructorBased() {
		return false;
	}

	public GeneratedKey getGeneratedKey() {
		if(getPrimaryKeyColumns().size() ==1){
        	DBColumnJavaBean dbColumn = getPrimaryKeyColumns().get(0);
        	GeneratedKey gk = new GeneratedKey(dbColumn, dbColumn.getColumnName(), dbColumn.isIdentity(), dbColumn.getFullyQualifiedJavaType().getFullyQualifiedName());
            return gk;
        } 
		return null;
	}

	public String getPrimaryKey() {
		if(primaryKeyColumns.size() == 1){
			return primaryKeyColumns.get(0).getJavaProperty();
		}
		StringBuilder sb = new StringBuilder();
        sb.append(getDomainObjectName());
        sb.append("_Key"); 
        return JavaBeansUtil.getCamelCaseString(sb.toString(), false);
	}

	public String getMapperName() {
		StringBuilder sb = new StringBuilder();
        sb.append(getDomainObjectName());
        sb.append("_Mapper"); 
        return JavaBeansUtil.getCamelCaseString(sb.toString(), false);
	}
	
	public String getServiceName() {
		StringBuilder sb = new StringBuilder();
        sb.append(getDomainObjectName());
        sb.append("_Service"); 
        return JavaBeansUtil.getCamelCaseString(sb.toString(), false);
	}
}
