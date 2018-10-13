package com.viching.generate.config;

import java.util.List;

import com.viching.generate.source.DBTableJavaBean;

public interface Investigator {
	
	List<DBTableJavaBean> introspectTables() throws Exception;
	
}
