package org.spnt.recognition.corpus;

import org.spnt.recognition.bean.RecognitionResult;

public interface CorpusMatchListener {
	public void matched(RecognitionResult result);
}
