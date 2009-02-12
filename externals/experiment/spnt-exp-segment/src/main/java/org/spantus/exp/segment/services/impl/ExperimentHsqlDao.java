package org.spantus.exp.segment.services.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;

import org.spantus.exception.ProcessingException;
import org.spantus.exp.segment.domain.ExperimentResult;
import org.spantus.exp.segment.domain.ExperimentResultTia;

public class ExperimentHsqlDao extends ExperimentStaticDao{
	Connection connection = null;
	Statement statement = null;

	public  static final String insert_query = 
		"INSERT INTO ExperimentResult" +
		"(experimentID, RESOURCE, FEATURES, TOTALRESULT, ONSET, STEADY, OFFSET, DELTAVAF, FEATURENUM) VALUES" +
		"({0,number,#}, ''{1}'', ''{2}'', {3,number}, {4,number},{5,number}, {6,number}, {7,number}, " +
		"{8,number});"; 
	
	public ExperimentHsqlDao() {
		super.setTotalResultThreshold(.05F);
	}
	
	public void init(){
		try {
			Class.forName("org.hsqldb.jdbcDriver").newInstance();

			String url = "jdbc:hsqldb:hsql://localhost/spnt-exp";
			connection = DriverManager.getConnection(url, "sa", "");
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

	public ExperimentResult save(ExperimentResult experimentResult) {
		try {
			ExperimentResultTia result = (ExperimentResultTia)experimentResult;
			int featureNum = result.getFeatures().split(" ").length;
			statement = connection.createStatement();
			String query = 
				MessageFormat.format(
						insert_query,
						result.getExperimentID(),
						result.getResource(),
						result.getFeatures(),
						result.getTotalResult(),
						result.getOnset(),
						result.getSteady(),
						result.getOffset(),
						result.getDeltaVAF(),
						featureNum);
			statement.executeUpdate(query);
			connection.commit();
		} catch (SQLException e) {
			destroy();
			throw new ProcessingException(e);
		}
		return experimentResult;
	}

	public void destroy() {
		try {
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
				} // nothing we can do
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
				} // nothing we can do
			}
		}
	}

}
