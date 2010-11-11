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
    public float getTime();
    public float getSampleRate();
    public void setSampleRate(float sampleRate);
    public float toTime(int i);
    public int toIndex(float f);
    public int getDmention();
}
