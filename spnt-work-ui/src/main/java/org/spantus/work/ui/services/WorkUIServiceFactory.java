package org.spantus.work.ui.services;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.SignalFormat;
import org.spantus.core.io.ProcessedFrameLinstener;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.utils.Assert;
import org.spantus.utils.ExtractorParamUtils;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.dto.SpantusWorkProjectInfo;
import org.spantus.work.ui.dto.WorkSample;
import org.spantus.work.ui.dto.WorkUIExtractorConfig;
import org.spantus.work.ui.dto.SpantusWorkProjectInfo.ProjectTypeEnum;
import org.spantus.work.ui.services.impl.DefaultReaderService;
import org.spantus.work.ui.services.impl.XmlWorkInfoManager;
import org.spantus.work.ui.services.impl.YamlWorkInfoManager;

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
		if(workSample.getMarkerSetHolder() == null){
			workSample.setMarkerSetHolder(new MarkerSetHolder());
		}
		ReaderService readerService = createReaderService();
		SignalFormat sfSignal = readerService.getSignalFormat(workSample.getCurrentFile());
		//check if noise and signal frequencies are the same
		if(workSample.getNoiseFile() !=null){
			SignalFormat sfNoise =readerService.getSignalFormat(workSample.getNoiseFile());
			Assert.isTrue(sfSignal.getSampleRate() == sfNoise.getSampleRate(),"Noise and singal sample rate has to be same");
		}
		workSample.setSignalFormat(sfSignal);
		List<URL> urls = new ArrayList<URL>();
		urls.add(workSample.getCurrentFile());
		if(workSample.getNoiseFile() != null){
			urls.add(workSample.getNoiseFile());
		}
		return readerService.read(
				urls, 
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
						project.getClassifierType());
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
		return new YamlWorkInfoManager();
	}
	
}
