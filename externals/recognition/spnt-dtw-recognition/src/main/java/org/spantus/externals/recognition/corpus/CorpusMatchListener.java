package org.spantus.externals.recognition.corpus;

import org.spantus.core.beans.RecognitionResult;

public interface CorpusMatchListener {
	public void matched(RecognitionResult result);
}
