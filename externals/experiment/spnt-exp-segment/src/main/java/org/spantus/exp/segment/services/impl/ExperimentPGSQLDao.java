package org.spantus.exp.segment.services.impl;

import java.sql.DriverManager;
import java.sql.SQLException;

import org.spantus.exception.ProcessingException;

public class ExperimentPGSQLDao extends ExperimentHsqlDao{

	@Override
	public void init(){
		try {
			Class.forName("org.postgresql.Driver").newInstance();
			String url = "jdbc:postgresql://localhost/spnt-exp";
			connection = DriverManager.getConnection(url, "sa", "sa");
			connection.setAutoCommit(true);
		} catch (InstantiationException e) {
			destroy();
			throw new ProcessingException(e);
		} catch (IllegalAccessException e) {
			destroy();
			throw new ProcessingException(e);
		} catch (ClassNotFoundException e) {
			destroy();
			throw new ProcessingException(e);
		} catch (SQLException e) {
			destroy();	
			throw new ProcessingException(e);
		}
	}
}
