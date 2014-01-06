package org.spantus.extr.wordspot.dto;

import java.util.HashSet;
import java.util.Set;

import org.spantus.core.marker.Marker;

import com.google.common.collect.BiMap;

public class MatchFoundDto {
	public BiMap<Marker, Marker> matchBi;
	public Set<Marker> duplicates;
	public Set<Marker> notFound;
	
	public MatchFoundDto() {
	}

}
