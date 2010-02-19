package org.spantus.work.ui.services;

import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.io.ProcessedFrameLinstener;
import org.spantus.utils.ExtractorParamUtils;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.dto.SpantusWorkProjectInfo;
import org.spantus.work.ui.dto.WorkSample;
import org.spantus.work.ui.dto.WorkUIExtractorConfig;
import org.spantus.work.ui.dto.SpantusWorkProjectInfo.ProjectTypeEnum;

/**
 * 
 * 
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created Jun 11, 2008
 *
 */
public abstract class WorkUIServiceFactory {
	public static ReaderService createReaderService(){
		return new DefaultReaderService();
	}
	public static IExtractorInputReader read(SpantusWorkInfo ctx, ProcessedFrameLinstener processedFrameLinstener){
		setThreshold(ctx.getProject(), ctx.getProject().getFeatureReader().getWorkConfig());
		WorkSample workSample = ctx.getProject().getSample();
//		workSample.setLength(createReaderService().getFormat(workSample.getCurrentFile()));
//		workSample.setFormat(createReaderService().getFormat(workSample.getCurrentFile()));
		ReaderService readerService = createReaderService();
		workSample.setSignalFormat(readerService.getSignalFormat(workSample.getCurrentFile()));
		return readerService.read(
				workSample.getCurrentFile(), 
				ctx.getProject().getFeatureReader(),
				processedFrameLinstener);
	}
	protected static void setThreshold(SpantusWorkProjectInfo project, WorkUIExtractorConfig config){
		ProjectTypeEnum projectType = ProjectTypeEnum.valueOf(project.getType());
		switch (projectType) {
		case segmenation:
			for (String extractorKey : project.getFeatureReader().getExtractors()) {
				ExtractorParam param = ExtractorParamUtils.getSafeParam(
						project.getFeatureReader().getParameters(),
						extractorKey);
				ExtractorParamUtils.setString(param,
						ExtractorParamUtils.commonParam.thresholdType.name(),
						project.getThresholdType());
				ExtractorParamUtils.<Float>setValue(param,
						ExtractorParamUtils.commonParam.threasholdCoef.name(),
						Float.valueOf(config.getThresholdCoef()));
			}
			break;

		default:
			break;
		}
		
	}

	public static WorkInfoManager createInfoManager(){
		return new XmlWorkInfoManager();
	}
	
}
