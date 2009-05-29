package org.spantus.work.ui.container.chart;

import org.spantus.chart.ChartDescriptionInfo;
import org.spantus.chart.ChartDescriptionResolver;
import org.spantus.chart.WrappedChartDescriptionResolver;
import org.spantus.work.ui.i18n.I18nFactory;

public class I18NChartDescriptionResolver extends
		WrappedChartDescriptionResolver {
	
	public static final String SMOOTHED = "SMOOTHED";
	public static final String DELTA = "DELTA";
	
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
		String name = resolved.getName();
		StringBuilder modifiers = new StringBuilder();
		if(name.contains(SMOOTHED)){
			name = name.replaceAll(SMOOTHED+"_", "");	
			modifiers.append(I18nFactory.createI18n().getMessage(SMOOTHED)).append(" ");
		}
		if(name.contains(DELTA)){
			name = name.replaceAll(DELTA+"_", "");
			modifiers.append(I18nFactory.createI18n().getMessage(DELTA)).append(" ");
		}
		modifiers.append(I18nFactory.createI18n().getMessage(name));
		resolved.setName(modifiers.toString());
		return resolved;
	}
}
