/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.exp.synthesis;

import java.util.ArrayList;
import java.util.List;
import org.spantus.core.marker.Marker;
import scikit.util.Pair;

/**
 *
 * @author as
 */
public class MarkerMbrola {

    Marker marker;

    public MarkerMbrola(Marker marker) {
        this.marker = marker;
    }
    /**
     * pitchValue(%), pitchValue(hz)
     */
    List<Pair<Integer, Integer>> pitches = new ArrayList<>();

    /**
     * pitchValue(%), pitchValue(hz)
     */
    public List<Pair<Integer, Integer>> getPitches() {
        return pitches;
    }

    /**
     * pitchValue(%), pitchValue(hz)
     */
    public void setPitches(List<Pair<Integer, Integer>> pitches) {
        this.pitches = pitches;
    }

    public String toMbrolaString() {
        StringBuilder phonemeBuilder = new StringBuilder();
        phonemeBuilder.append(getMarker().getLabel()).append(" ").append(getMarker().getLength());
        for (Pair<Integer, Integer> durationAndValue : this.getPitches()) {
            phonemeBuilder.append("\t").append(durationAndValue.fst())
                    .append(" ").append(durationAndValue.snd());
        }
        phonemeBuilder.append("\n");
        return phonemeBuilder.toString();
    }

    public Marker getMarker() {
        return marker;
    }
}
