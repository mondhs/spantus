package org.spantus.exp.recognition.dao;

import java.sql.SQLException;

import org.spantus.exp.recognition.domain.QSegmentExp;

public interface QSegmentExpDao {
	public void init();
	public QSegmentExp save(QSegmentExp exp);
	public void destroy();
	public StringBuilder generateReport(String shouldBe) throws SQLException;
}
