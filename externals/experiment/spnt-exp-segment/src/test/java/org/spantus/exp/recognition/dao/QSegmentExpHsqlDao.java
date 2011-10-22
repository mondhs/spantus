package org.spantus.exp.recognition.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.spantus.exception.ProcessingException;
import org.spantus.exp.recognition.domain.QSegmentExp;
import org.spantus.utils.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class QSegmentExpHsqlDao implements QSegmentExpDao {
	private static final String LPCLABEL = "LPCLABEL";
	private static final String MFCCLABEL = "MFCCLABEL";
	public static final String MANUALNAME = "MANUALNAME";
	private static final String PLPLABEL = "PLPLABEL";
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
						+ "length BIGINT," + "start BIGINT,"
						+ "wavFilePath VARCHAR(255) NOT NULL,"
						+ "markerLabel VARCHAR(255) NOT NULL,"
						+ "corpusEntryName VARCHAR(255) NOT NULL,"
						+ "manualName VARCHAR(255) NOT NULL,"
						+ "proceessTime BIGINT,"
						+ "loudnessLabel VARCHAR(255) NOT NULL,"
						+ "loudness FLOAT,"
						+ "spectralFluxLabel VARCHAR(255) NOT NULL,"
						+ "spectralFlux FLOAT,"
						+ "plpLabel VARCHAR(255) NOT NULL," + "plp FLOAT,"
						+ "lpcLabel VARCHAR(255) NOT NULL," + "lpc FLOAT,"
						+ "mfccLabel VARCHAR(255) NOT NULL," + "mfcc FLOAT,"
						+ "signalEntropyLabel VARCHAR(255) NOT NULL,"
						+ "signalEntropy FLOAT,"
						+ "timeStamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
						+ ");"
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
				+ "(wavFilePath, start, length, markerLabel, corpusEntryName, manualName ,proceessTime,loudnessLabel, loudness, spectralFluxLabel, spectralFlux,plpLabel, plp, lpcLabel, lpc, mfccLabel, mfcc, signalEntropyLabel,  signalEntropy) VALUES"
				+ "(''{0}'',  {1,number,#}, {2,number,#},''{3}'', ''{4}'', ''{5}'', {6,number,#}, "
				+ "''{7}'', {8,number,#.#}," + "''{9}'', {10,number,#.#},"
				+ "''{11}'', {12,number,#.#}, " + "''{13}'',{14,number,#.#}, "
				+ "''{15}'',{16,number,#.#}, " + "''{17}'',{18,number,#.#}"
				+ ");";
		return insertQuery;
	}

	public static final String ALL_SEGMENTS_REPORT_QUERY = "select {0} count(id) mcount from QSEGMENTEXP   {1}";
	public static final String DISTINCT_SEGMENTS_REPORT_QUERY = "select {0}  count(distinct(MANUALNAME)) mcount from QSEGMENTEXP where  not MARKERLABEL like ''D;%''  {1}";
	public static final String CORRECT_SYLABLE_SEGMENTS_REPORT_QUERY = "select {0} count(id) mcount from QSEGMENTEXP where MANUALNAME = ''<SYLLABLE>'' and not MARKERLABEL like ''D;%''  {1}";
	public static final String ERR_JOINED_SEGMENTS_REPORT_QUERY = "select {0} count(id) mcount from QSEGMENTEXP where LENGTH(MANUALNAME) > 2  and not MARKERLABEL like ''D;%''  {1}";
	public static final String ERR_NOIZE_SEGMENTS_REPORT_QUERY = "select {0} count(id) mcount from QSEGMENTEXP where MANUALNAME = '''' and not MARKERLABEL like ''D;%''  {1}";
	public static final String ERR_BREAK_SEGMENTS_REPORT_QUERY = "select {0} count(id) mcount from QSEGMENTEXP where  MARKERLABEL like ''D;%''  {1}";

	public static final String SUCC_RECONITION_SEGMENTS_REPORT_QUERY = "select {0} count(id) mcount from QSEGMENTEXP where "
			+ "MANUALNAME = ''<SYLLABLE_WAS>''  and  MFCCLABEL = ''<SYLLABLE_SAID>'' and MFCC <90 and not  MARKERLABEL like ''D;%''  {1}";

	public static final String SUCC_RECONITION_LIKE_SEGMENTS_REPORT_QUERY = "select {0} count(id) mcount from QSEGMENTEXP where "
			+ "MANUALNAME like ''%<SYLLABLE_WAS>''  and  MFCCLABEL like ''%<SYLLABLE_SAID>'' and MFCC <90 and not  MARKERLABEL like ''D;%''  {1}";

	public static final String ERR_REJECTED_RECONITION_SEGMENTS_REPORT_QUERY = "select {0} count(id) mcount from QSEGMENTEXP where "
			+ " MFCC >90 and not  MARKERLABEL like ''D;%''  {1}";

	/**
	 * 
	 */
	@Override
	public StringBuilder generateReport(String shouldBe) throws SQLException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> reports = new LinkedHashMap<String, String>();
		String[] syllabels = new String[] { "ga", "ma", "me", "na", "ne", "re",
				"ta" };
		String[] vovel = new String[] { "a", "e" };

		for (String string : syllabels) {
			reports.put("Teisingai " + string,
					CORRECT_SYLABLE_SEGMENTS_REPORT_QUERY.replaceAll(
							"<SYLLABLE>", string));
		}
		reports.put("2 skiemenys apjungti ", ERR_JOINED_SEGMENTS_REPORT_QUERY);
		reports.put("aptiktas triukšmas ", ERR_NOIZE_SEGMENTS_REPORT_QUERY);
		reports.put("Skiemenų trūkiai ", ERR_BREAK_SEGMENTS_REPORT_QUERY);
		reports.put("Viso aptikta", ALL_SEGMENTS_REPORT_QUERY);
		reports.put("Skirtingų segmentų tipų", DISTINCT_SEGMENTS_REPORT_QUERY);
		reports.put("turėjo būti", "${shouldBe}");

		reports.put("B2", null);
		// for (String syllableWas : syllabels) {
		// reports.put( syllableWas ,
		// SUCC_RECONITION_SEGMENTS_REPORT_QUERY.replaceAll("<SYLLABLE_WAS>",
		// syllableWas).replaceAll("<SYLLABLE_SAID>", syllableWas));
		// }

		// for (String syllableWas : syllabels) {
		// for (String syllableSaid : syllabels) {
		// if(!syllableWas.equals(syllableSaid)){
		// reports.put( syllableWas + " kaip " + syllableSaid ,
		// SUCC_RECONITION_SEGMENTS_REPORT_QUERY.replaceAll("<SYLLABLE_WAS>",
		// syllableWas).replaceAll("<SYLLABLE_SAID>", syllableSaid));
		// }
		// }
		// }
		// for (String syllableWas : syllabels) {
		// reports.put( "triukšmas kaip " + syllableWas ,
		// SUCC_RECONITION_SEGMENTS_REPORT_QUERY.replaceAll("<SYLLABLE_WAS>",
		// "").replaceAll("<SYLLABLE_SAID>", syllableWas));
		// }
		for (String vovelWas : vovel) {
			reports.put(
					vovelWas,
					SUCC_RECONITION_LIKE_SEGMENTS_REPORT_QUERY.replaceAll(
							"<SYLLABLE_WAS>", vovelWas).replaceAll(
							"<SYLLABLE_SAID>", vovelWas));
		}
		for (String vovelWas : vovel) {
			for (String vovelSaid : vovel) {
				if (!vovelWas.equals(vovelSaid)) {
					reports.put(
							vovelWas + " kaip " + vovelSaid,
							SUCC_RECONITION_LIKE_SEGMENTS_REPORT_QUERY.replaceAll(
									"<SYLLABLE_WAS>", "%"+vovelWas).replaceAll(
									"<SYLLABLE_SAID>", vovelSaid));
				}
			}
		}
		for (String vovelWas : vovel) {
			reports.put(
					"triukšmas kaip " + vovelWas,
					SUCC_RECONITION_LIKE_SEGMENTS_REPORT_QUERY.replaceAll(
							" like ''<SYLLABLE_WAS>", " like ''").replaceAll("<SYLLABLE_SAID>",
									vovelWas));
		}

		reports.put("Atsisakyta", ERR_REJECTED_RECONITION_SEGMENTS_REPORT_QUERY);
		reports.put("Apjungti Skiemenys", ERR_JOINED_SEGMENTS_REPORT_QUERY);
		reports.put("Skiemenų Trūkiai ", ERR_BREAK_SEGMENTS_REPORT_QUERY);
		reports.put("Viso aptikta segmentavime", ALL_SEGMENTS_REPORT_QUERY);

		boolean header = false;
		for (Entry<String, String> entry : reports.entrySet()) {
			if (entry.getValue() == null) {
				sb.append("\n");
				continue;
			}
			if(entry.getValue().contains("${shouldBe}")){
				sb.append(entry.getKey()).append(",").append(shouldBe).append("\n");
				continue;

			}
			Map<Integer, Integer> result = fetchResults(entry.getValue());
			if (!header) {
				sb.append("\n");
				sb.append(",");
//				for (Integer key : result.keySet()) {
//					sb.append(key).append("dB,");
//				}
				sb.append("\n");
				header = true;
			}
			sb.append(entry.getKey()).append(",");
			for (Integer value : result.values()) {
				sb.append(value).append(",");
			}
			sb.append("\n");
		}
		return sb;
	}

	/**
	 * 
	 * @param criteria
	 * @param corpusName
	 * @param results
	 * @param segmentLabel
	 * @throws SQLException
	 */
	private Map<Integer, Integer> fetchResults(String query)
			throws SQLException {
		// String query = REPORT_QUERY + criteria + REPORT_QUERY_GROUPING;
		String pimpedQuery = MessageFormat.format(query,
				"CORPUSENTRYNAME snr,", "GROUP BY CORPUSENTRYNAME");
		Map<Integer, Integer> result = new TreeMap<Integer, Integer>();
		System.out.println(pimpedQuery);
		ResultSet rs = statement.executeQuery(pimpedQuery);
		result.put(0, 0);
		result.put(5, 0);
		result.put(10, 0);
		result.put(15, 0);
		result.put(30, 0);
		while (rs.next()) {
			Integer snr = rs.getInt("snr");
			result.put(snr, rs.getInt("mcount"));
		}

		pimpedQuery = MessageFormat.format(query, "", "");
		System.out.println(pimpedQuery);
		rs = statement.executeQuery(pimpedQuery);
		while (rs.next()) {
			result.put(99999999, rs.getInt("mcount"));
		}

		return result;
	}

	// public static final String
	// REPORT_QUERY="select MANUALNAME, count(id) mcount from QSEGMENTEXP where ";
	// public static final String REPORT_QUERY_GROUPING =
	// " GROUP By MANUALNAME   HAVING count(id) >5 ORDER by COUNT(id) desc";

	/**
	 * 
	 */
	// public void findMatches( String corpusName){
	//
	// StringBuilder criteria = new StringBuilder();
	// String currentRecognitionFeature= MANUALNAME;
	// String separator = " ";
	//
	// criteria.append(separator).append("  {0} = MANUALNAME ");
	// separator = " AND ";
	//
	// // if (StringUtils.hasText(recognitionFeature)) {
	// // currentRecognitionFeature = recognitionFeature;
	// // }
	// if (StringUtils.hasText(corpusName)) {
	// //criteria.append(separator).append("  CORPUSENTRYNAME={1} ");
	// criteria.append(separator).append("  CORPUSENTRYNAME in ({1}) ");
	// separator = " AND ";
	// }
	//
	// String query = REPORT_QUERY + criteria + REPORT_QUERY_GROUPING;
	//
	// Map<String, Map<String, Integer>> results = Maps.newTreeMap();
	// try {
	// statement = connection.createStatement();
	// fetchResults(criteria, corpusName, results, MANUALNAME);
	// fetchResults(criteria, corpusName, results, PLPLABEL);
	// fetchResults(criteria, corpusName, results, MFCCLABEL);
	// fetchResults(criteria, corpusName, results, LPCLABEL);
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// StringBuilder sb = new StringBuilder();
	// sb.append("Label;MANUAL;PLPLABEL; MFCCLABEL; LPCLABEL\n");
	// for (Entry<String, Map<String, Integer>> record : results.entrySet()) {
	// sb.append(record.getKey()).append(";");
	// String[] columns = new String[]{ PLPLABEL, MFCCLABEL, LPCLABEL};
	// int manualInt = record.getValue().get(MANUALNAME);
	// sb.append(manualInt).append(";");
	// for (String column : columns) {
	// Integer val = record.getValue().get(column);
	// if(val == null){
	// sb.append(";");
	// }else{
	// sb.append(val.doubleValue()/manualInt).append(";");
	// }
	//
	// }
	// sb.append("\n");
	//
	// }
	// System.out.println(sb);
	// }

	public QSegmentExp save(QSegmentExp exp) {
		String query = MessageFormat.format(getInsertExperimentResulQuery(),
				exp.getWavFilePath(), exp.getStart(), exp.getLength(),
				exp.getMarkerLabel(), exp.getCorpusEntryName(),
				exp.getManualName(), exp.getProceessTime(),
				exp.getLoudnessLabel(), exp.getLoudness(),
				exp.getSpectralFluxLabel(), exp.getSpectralFlux(),
				exp.getPlpLabel(), exp.getPlp(), exp.getLpcLabel(),
				exp.getLpc(), exp.getMfccLabel(), exp.getMfcc(),
				exp.getSignalEntropyLabel(), exp.getSignalEntropy(),
				exp.getTimeStamp());
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
			if (!connection.isClosed()) {
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
