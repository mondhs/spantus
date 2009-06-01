package org.spantus.chart;

public interface ChartDescriptionResolver {
	ChartDescriptionInfo resolve(float time, float value);
}
