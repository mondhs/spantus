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
    public Long getTime();
    public Double getSampleRate();
    public void setSampleRate(Double sampleRate);
    public Long toTime(int i);
    public int toIndex(Long f);
    public int getDimention();
    public int size();
    public <T extends IValues> T subList(int fromIndex, int toIndex);
    public <T extends IValues> void addValues(T values);
}
