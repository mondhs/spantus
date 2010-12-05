/*
 	Copyright (c) 2009 Mindaugas Greibus (spantus@gmail.com)
 	Part of program for analyze speech signal
 	http://spantus.sourceforge.net

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
*/
package org.spantus.work.ui.cmd;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.spantus.core.IValues;
import org.spantus.core.marker.Marker;
import org.spantus.externals.recognition.bean.RecognitionResultDetails;
import org.spantus.externals.recognition.corpus.CorpusRepositoryFileImpl;
import org.spantus.externals.recognition.services.CorpusServiceBaseImpl;
import org.spantus.logger.Logger;
import org.spantus.work.services.ExtractorReaderService;
import org.spantus.work.services.WorkServiceFactory;
import org.spantus.externals.recognition.ui.RecognizeDetailDialog;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.math.dtw.DtwServiceJavaMLImpl;
import org.spantus.math.dtw.DtwServiceJavaMLImpl.JavaMLSearchWindow;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.i18n.I18nFactory;

/**
 *
 * @author mondhs
 */
public class RecognizeCmd extends AbsrtactCmd {

    private static Logger log = Logger.getLogger(RecognizeCmd.class);
    private RecognizeDetailDialog info;
    private ExtractorReaderService extractorReaderService;
    private CorpusServiceBaseImpl corpusService;
    private DtwServiceJavaMLImpl dtwService;
    private CorpusRepositoryFileImpl corpusRepo;

    
    
    public RecognizeCmd(CommandExecutionFacade executionFacade) {
        super(executionFacade);
    }
    
    @Override
    public String execute(SpantusWorkInfo ctx) {
        
       
        update(ctx); 
        
        Marker marker = ((Marker) getCurrentEvent().getValue());

        Map<String, IValues> fvv = getExtractorReaderService().findAllVectorValuesForMarker(
                getReader(),
                marker);

        List<RecognitionResultDetails> results = getCorpusService().findMultipleMatch(
                fvv);
        if(results!=null && results.size()>0){
            marker.setLabel(results.get(0).getInfo().getName());
        }
        info = null;
        getInfoPnl().setTargetWavURL(ctx.getProject().getSample().getCurrentFile());
        getInfoPnl().setTargetMarker(marker);
        getInfoPnl().updateCtx(results);
	getInfoPnl().setVisible(true);
        return GlobalCommands.sample.reloadMarkers.name();
    }

    public Set<String> getExpectedActions() {
        return createExpectedActions(
                GlobalCommands.tool.recognize.name());
    }
    
    /**
     * Update configuration
     * @param ctx
     */
    protected void update(SpantusWorkInfo ctx){
        corpusService = null;
        String corpusPath = ctx.getProject().getRecognitionConfig().getRepositoryPath();
        String searchWindowStr= ctx.getProject().getRecognitionConfig().getDtwWindow();
        JavaMLSearchWindow searchWindow= JavaMLSearchWindow.valueOf(searchWindowStr);
        int radius = ctx.getProject().getRecognitionConfig().getRadius();
        
        File corpusDir =  new File(corpusPath);
        if (!corpusDir.equals(getCorpusRepository().getRepoDir().getAbsoluteFile())) {
            getCorpusRepository().setRepositoryPath(corpusPath);
            getCorpusRepository().flush();
        }
        if(searchWindow != null){
            getDtwService().setSearchWindow(searchWindow);
        }
        getDtwService().setSearchRadius(radius);
    }

    private RecognizeDetailDialog getInfoPnl() {
        if (info == null) {
            info = new RecognizeDetailDialog(null,I18nFactory.createI18n());
            info.setModal(true);
        }
        return info;
    }
    public CorpusServiceBaseImpl getCorpusService() {
        if (corpusService == null) {
            CorpusServiceBaseImpl corpusServiceimpl = new CorpusServiceBaseImpl();
            corpusServiceimpl.setDtwService(getDtwService());
            corpusServiceimpl.setIncludeFeatures(new HashSet<String>());
            corpusServiceimpl.getIncludeFeatures().add(ExtractorEnum.MFCC_EXTRACTOR.name());
            corpusServiceimpl.getIncludeFeatures().add(ExtractorEnum.LPC_EXTRACTOR.name());
            corpusServiceimpl.getIncludeFeatures().add(ExtractorEnum.FFT_EXTRACTOR.name());
//            corpusServiceimpl.getIncludeFeatures().add(ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR.name());
            corpusServiceimpl.setCorpus(getCorpusRepository());
            corpusService = corpusServiceimpl;
//            corpusService = RecognitionServiceFactory.createCorpusService();
        }
        return corpusService;
    }
    
    
    public DtwServiceJavaMLImpl getDtwService(){
        if(dtwService == null){
            dtwService = new DtwServiceJavaMLImpl();
//            dtwService.setSearchWindow(DtwServiceJavaMLImpl.JavaMLSearchWindow.ExpandedResWindow);
//            dtwService.setSearchRadius(15);
        }
        return dtwService;
    }
            

    public void setCorpusService(CorpusServiceBaseImpl corpusService) {
        this.corpusService = corpusService;
    }
    public CorpusRepositoryFileImpl getCorpusRepository() {
        if(corpusRepo == null){
            corpusRepo =  new CorpusRepositoryFileImpl();
        }
        return corpusRepo;
    }
    
    public ExtractorReaderService getExtractorReaderService() {
        if(extractorReaderService == null){
            extractorReaderService = WorkServiceFactory.createExtractorReaderService();
        }
        return extractorReaderService;
    }

    public void setExtractorReaderService(ExtractorReaderService extractorReaderService) {
        this.extractorReaderService = extractorReaderService;
    }


}
