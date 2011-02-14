package org.spantus.extractor.segments.online.rule;

import org.spantus.extractor.segments.offline.ExtremeSegment;
import org.spantus.extractor.segments.online.ExtremeSegmentsOnlineCtx;


public interface ClassifierRuleBaseService {
	public String testOnRuleBase(ExtremeSegmentsOnlineCtx ctx);

	public void learn(ExtremeSegment currentSegment,
			ExtremeSegmentsOnlineCtx ctx);
}
