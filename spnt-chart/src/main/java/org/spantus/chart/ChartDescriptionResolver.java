package org.spantus.chart;

public interface ChartDescriptionResolver {
	ChartDescriptionInfo resolve(Long time, float value);
}
