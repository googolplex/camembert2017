package biz.lcompras.util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

public class Database {
	static Logger logger = Logger.getLogger(Database.class);
	private static DataSource ds = null;	
	public static DataSource getDataSource() {
		logger.info("Inside Database() static method... ");
		Context context = null;
		try {
			context = new InitialContext();
			ds = (DataSource)context.lookup("java:comp/env/jdbc/terere");
		} catch (NamingException e) {
			logger.error("Unable to get Datasource...");
			e.printStackTrace();
		}
		return ds;
	}
}
