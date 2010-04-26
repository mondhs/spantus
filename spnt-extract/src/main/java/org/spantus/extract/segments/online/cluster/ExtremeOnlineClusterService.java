package org.spantus.extract.segments.online.cluster;

import org.spantus.extract.segments.offline.ExtremeSegment;
import org.spantus.extract.segments.online.ExtremeSegmentsOnlineCtx;
import org.spantus.extract.segments.online.SegmentInnerData;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.2
 * Created Mar 16, 2010
 *
 */
public interface ExtremeOnlineClusterService {
	/**
	 * class name(Cluster number) of the segment in 
	 * @param segment
	 * @param ctx
	 * @return
	 */
	public String getClassName(ExtremeSegment segment, ExtremeSegmentsOnlineCtx ctx);
	/**
	 * learn segment
	 * @param segment
	 * @param ctx
	 * @return
	 */
	public SegmentInnerData learn(ExtremeSegment segment, ExtremeSegmentsOnlineCtx ctx);
}
