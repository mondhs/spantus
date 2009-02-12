package org.spantus.work.ui.services;

import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.io.ProcessedFrameLinstener;
import org.spantus.utils.ExtractorParamUtils;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.dto.SpantusWorkProjectInfo;
import org.spantus.work.ui.dto.WorkSample;
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
	public static IExtractorInputReader constructReader(SpantusWorkInfo ctx, ProcessedFrameLinstener processedFrameLinstener){
		setThreshold(ctx.getProject());
		WorkSample workSample = ctx.getProject().getCurrentSample();
		workSample.setFormat(createReaderService().getFormat(workSample.getCurrentFile()));
		return createReaderService().getReader(
				workSample.getCurrentFile(), 
				ctx.getProject().getFeatureReader(),
				processedFrameLinstener);
	}
	protected static void setThreshold(SpantusWorkProjectInfo project){
		ProjectTypeEnum projectType = ProjectTypeEnum.valueOf(project.getCurrentType());
		switch (projectType) {
		case fileThreshold:
			for (String extractorKey : project.getFeatureReader().getExtractors()) {
				ExtractorParam param = ExtractorParamUtils.getSafeParam(
						project.getFeatureReader().getParameters(),
						extractorKey);
				ExtractorParamUtils.setBoolean(param,
						ExtractorParamUtils.commonParam.isThreashold.name(),
						Boolean.TRUE);
				ExtractorParamUtils.<Float>setValue(param,
						ExtractorParamUtils.commonParam.threasholdCoef.name(),
						Float.valueOf(1.5f));

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
