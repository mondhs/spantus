package org.spantus.work.ui.cmd;

import org.spantus.work.ui.container.chart.SampleChart;
import org.spantus.work.ui.dto.SpantusWorkInfo;

public class ReloadSampleChartCmd extends AbsrtactCmd {
	
	
	private SampleChart chart;
	
	public ReloadSampleChartCmd(SampleChart chart) {
		this.chart = chart;
	}

	
	public String execute(SpantusWorkInfo ctx) {
		if(ctx.getProject().getCurrentSample().getCurrentFile()==null){
			chart.setReader(null);
		}
		chart.updateContent();
		return null;
	}

}
