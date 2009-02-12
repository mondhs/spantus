package org.spantus.work.ui.container.chart;

import org.spantus.chart.ChartDescriptionInfo;
import org.spantus.chart.ChartDescriptionResolver;
import org.spantus.chart.WrappedChartDescriptionResolver;
import org.spantus.work.ui.i18n.I18nFactory;

public class I18NChartDescriptionResolver extends
		WrappedChartDescriptionResolver {
	
	
	@Override
	public ChartDescriptionResolver getInstance(
			ChartDescriptionResolver localResolver) {
		I18NChartDescriptionResolver resolver = new I18NChartDescriptionResolver();
		resolver.setLocalResolver(localResolver);
		return resolver;
	}
	
	@Override
	public ChartDescriptionInfo resolve(float val) {
		ChartDescriptionInfo resolved = super.resolve(val);
		if(resolved == null){
			return null;
		}
		resolved.setName(I18nFactory.createI18n().getMessage(resolved.getName()));
		return resolved;
	}
}
