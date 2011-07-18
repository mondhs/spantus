/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.spantus.core;

/**
 *
 * @author mondhs
 */
public interface IValues {
    public Double getTime();
    public Double getSampleRate();
    public void setSampleRate(Double sampleRate);
    public Double toTime(int i);
    public int toIndex(Double f);
    public int getDimention();
}
