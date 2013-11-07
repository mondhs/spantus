/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.extr.wordspot.service;

import java.net.URL;
import org.spantus.extr.wordspot.service.SpottingListener;

/**
 *
 * @author as
 */
public interface SpottingService {

    void wordSpotting(URL urlFile, SpottingListener wordSpottingListener);
    
}
