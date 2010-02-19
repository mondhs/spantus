package org.spantus.externals.recognition.corpus;

import org.spantus.externals.recognition.bean.RecognitionResult;

public interface CorpusMatchListener {
	public void matched(RecognitionResult result);
}
