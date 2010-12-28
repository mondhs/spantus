package org.spantus.extractor.segments.online.rule;

import org.spantus.extractor.segments.offline.ExtremeSegment;
import org.spantus.extractor.segments.online.ExtremeOnClassifierServiceFactory;
import org.spantus.extractor.segments.online.ExtremeSegmentsOnlineCtx;
import org.spantus.extractor.segments.online.cluster.ExtremeOnlineClusterService;
import org.spantus.logger.Logger;

/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.2
 * Created Mar 16, 2010
 *
 */
public class ClassifierRuleBaseServiceImpl implements ClassifierRuleBaseService {

	Logger log = Logger.getLogger(getClass());

	private ExtremeOnlineClusterService clusterService;
	/**
	 * test on rule base
	 */
	public String testOnRuleBase(ExtremeSegmentsOnlineCtx ctx) {
		ClassifierRuleBaseEnum.action actionVal = testOnRuleBase(ctx,
				Boolean.TRUE);
		log.debug("[testOnRuleBase] {0}", actionVal.name());
		return actionVal.name();
	}

	@SuppressWarnings("unused")
	public ClassifierRuleBaseEnum.action testOnRuleBase(
			ExtremeSegmentsOnlineCtx ctx, Boolean isEnum) {

		ExtremeSegment currentSegment = null;
		ExtremeSegment lastSegment = null;
//		boolean segmentEnd = ctx.getFoundEndSegment();
//		boolean segmentStart = ctx.getFoundStartSegment();
		
		boolean segmentPeak = ctx.getFoundPeakSegment();
		boolean noiseClass = true;

		Double currentArea = null;
		Integer currentPeak = null;
		Long currentLength = null;
		Double lastArea = null;
		Integer lastPeak = null;
		Long lastLength = null;
		boolean isIncrease = false;
		boolean isDecrease = false;
		int lastSizeValues = 0;
		int currentSizeValues = 0;
		String className = "";
		
		if(ctx.getCurrentSegment() != null){
			currentSegment = ctx.getCurrentSegment();
			currentArea = currentSegment.getCalculatedArea();
			currentPeak = currentSegment.getPeakEntries().size();
			currentLength = currentSegment.getCalculatedLength();
			currentSizeValues = currentSegment.getValues().size();
		}
		
		if (ctx.getExtremeSegments().size() > 0) {
			lastSegment = ctx.getExtremeSegments().getLast();
			lastArea = lastSegment.getCalculatedArea();
			lastPeak = lastSegment.getPeakEntries().size();
			lastLength = lastSegment.getCalculatedLength();
			lastSizeValues = lastSegment.getValues().size();

			
			if(currentSegment.getPeakEntry() != null){
				isIncrease = currentSegment.isIncrease(lastSegment);
				isDecrease = currentSegment.isDecrease(lastSegment);
				className = getClusterService().getClassName(lastSegment, ctx);

				
				log.debug("[testOnRuleBase] Similar: {0}, Increase: {1}; Decrease: {2}; className:{3}",
						currentSegment.isSimilar(lastSegment),
						isIncrease,
						isDecrease,
						className
						);
			}
		}
		
		if(currentSegment == null &&  ctx.isFeatureInMin()){
			log.debug("Current not initialized. This first segment");
			return ClassifierRuleBaseEnum.action.changePointCurrentApproved;	
		} else if(lastSegment == null &&  ctx.isFeatureInMin()){
			log.debug("Previous not initialized. this is second segment");
			return ClassifierRuleBaseEnum.action.changePointCurrentApproved;	
		} else if(lastSegment == null &&  ctx.isFeatureInMax()){
			//do nothing
			log.debug("Previous not initialized");
			return ClassifierRuleBaseEnum.action.processSignal;	
		}else if(ctx.isFeatureInMin()){
			log.debug("Found min. Possible change point");
			return ClassifierRuleBaseEnum.action.changePoint;	
		}else 	if(ctx.isFeatureInMax() && isIncrease){
			log.debug("Found max. join as increase");
			return ClassifierRuleBaseEnum.action.join;
		}else if(ctx.isFeatureInMax() && currentLength<40 && lastLength > 100){
			log.debug("too small gap for new segment {0}<40, {1}>100", currentLength, lastLength);
			return ClassifierRuleBaseEnum.action.join;
		}else 	if(ctx.isFeatureInMax() && isDecrease && lastLength < 100){
			log.debug("Found max. join as decrease {0}", currentSegment.isDecrease(lastSegment));
			return ClassifierRuleBaseEnum.action.join;
		}else if(ctx.isFeatureInMax() && "0".equals(className)){
			log.debug("Found max. delete segment as noise");
			return ClassifierRuleBaseEnum.action.delete;
		}else if(ctx.isFeatureInMax()){
			log.debug("Found max. approve previous change point");
			return ClassifierRuleBaseEnum.action.changePointLastApproved;	
		}

		log.debug("[testOnRuleBase]  not handled area and length {0}, {1}",
				currentArea, currentLength);

		return ClassifierRuleBaseEnum.action.processSignal;
	}

	public ExtremeOnlineClusterService getClusterService() {
		if (clusterService == null) {
			clusterService = ExtremeOnClassifierServiceFactory
					.createClusterService();

		}
		return clusterService;
	}

	public void setClusterService(ExtremeOnlineClusterService clusterService) {
		this.clusterService = clusterService;
	}
}
