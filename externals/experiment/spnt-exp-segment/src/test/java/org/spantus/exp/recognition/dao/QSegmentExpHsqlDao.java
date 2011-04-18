package org.spantus.exp.recognition.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;

import org.spantus.exception.ProcessingException;
import org.spantus.exp.recognition.domain.QSegmentExp;
import org.spantus.exp.segment.domain.ExperimentResult;
import org.spantus.exp.segment.domain.ExperimentResultTia;

public class QSegmentExpHsqlDao implements QSegmentExpDao {
	Connection connection = null;
	Statement statement = null;
	boolean recreate = false;

	public void init() {
		try {
			Class.forName("org.hsqldb.jdbcDriver").newInstance();

			String url = "jdbc:hsqldb:hsql://localhost/spnt-exp";
			connection = DriverManager.getConnection(url, "sa", "");
			connection.setAutoCommit(true);

			statement = connection.createStatement();
			if (recreate) {
				String query = "DROP TABLE QSegmentExp IF EXISTS;"
						+

						"CREATE CACHED TABLE QSegmentExp("
						+ "id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 0) NOT NULL PRIMARY KEY,"
						+ "length BIGINT,"
						+ "wavFilePath VARCHAR(255) NOT NULL,"
						+ "markerLabel VARCHAR(255) NOT NULL,"
						+ "corpusEntryName VARCHAR(255) NOT NULL,"
						+ "manualName VARCHAR(255) NOT NULL,"
						+ "proceessTime BIGINT," + "loudness FLOAT,"
						+ "spectralFlux FLOAT," + "plp FLOAT," + "lpc FLOAT,"
						+ "mfcc FLOAT," + "signalEntropy FLOAT," 
						+ "timeStamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP"+ ");"
				// + "CREATE INDEX feature_idx on QSegmentExp(TOTALRESULT)"
				;
				statement.executeUpdate(query);
				connection.commit();
			}

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

	protected String getInsertExperimentResulQuery() {
		String insertQuery = "INSERT INTO QSegmentExp"
				+ "(wavFilePath, length, markerLabel, corpusEntryName, manualName ,proceessTime,loudness,spectralFlux,plp, lpc, mfcc, signalEntropy) VALUES"
				+ "(''{0}'', {1,number,#},''{2}'', ''{3}'', ''{4}'', {5,number,#}, {6,number,#.#}, {7,number,#.#}, {8,number,#.#}, {9,number,#.#}, {10,number,#.#}, {11,number,#.#}" +
						");";
		return insertQuery;
	}

	public QSegmentExp save(QSegmentExp exp) {
		String query = MessageFormat.format(
				getInsertExperimentResulQuery(),
				exp.getWavFilePath(),exp.getLength(),
				exp.getMarkerLabel(), exp.getCorpusEntryName(),
				exp.getManualName(), exp.getProceessTime(),
				exp.getLoudness(), exp.getSpectralFlux(),
				exp.getPlp(), exp.getLpc(), exp.getMfcc(), exp.getSignalEntropy(), exp.getTimeStamp());
		try {
			statement = connection.createStatement();
			statement.executeUpdate(query);
			connection.commit();
		} catch (SQLException e) {
			destroy();
			throw new ProcessingException(e);
		}
		return exp;
	}

	public void destroy() {
		try {
			if(!connection.isClosed()){
				connection.commit();
			}
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

	public boolean isRecreate() {
		return recreate;
	}

	public void setRecreate(boolean recreate) {
		this.recreate = recreate;
	}

}
