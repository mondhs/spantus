package org.spantus.exp.recognition.dao;

import org.spantus.exp.recognition.domain.QSegmentExp;

public interface QSegmentExpDao {
	public void init();
	public QSegmentExp save(QSegmentExp exp);
	public void destroy();
}
