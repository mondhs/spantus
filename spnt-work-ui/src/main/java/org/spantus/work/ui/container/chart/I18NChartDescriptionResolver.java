package org.spantus.work.ui.container.chart;

import org.spantus.chart.ChartDescriptionInfo;
import org.spantus.chart.ChartDescriptionResolver;
import org.spantus.chart.WrappedChartDescriptionResolver;
import org.spantus.extractor.impl.ExtractorModifiersEnum;
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
	public ChartDescriptionInfo resolve(float time, float value) {
		ChartDescriptionInfo resolved = super.resolve(time, value);
		if(resolved == null){
			return null;
		}
		String name = resolved.getName();
		StringBuilder modifiers = new StringBuilder();
		for (ExtractorModifiersEnum modifier : ExtractorModifiersEnum.values()) {
			if(name.contains(modifier.name())){
				name = name.replaceAll(modifier.name()+"_", "");	
				modifiers.append(I18nFactory.createI18n().getMessage(modifier.name())).append(" ");
			}
		}
		modifiers.append(I18nFactory.createI18n().getMessage(name));
		resolved.setName(modifiers.toString());
		return resolved;
	}
}
