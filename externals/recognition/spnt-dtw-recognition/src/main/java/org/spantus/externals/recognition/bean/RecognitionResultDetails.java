/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.externals.recognition.bean;

import java.awt.Point;
import java.util.List;
import java.util.Map;

/**
 *
 * @author mondhs
 */
public class RecognitionResultDetails extends RecognitionResult {

    private Map<String,List<Point>> path;

    public void setPath(Map<String, List<Point>> path) {
        this.path = path;
    }

    public Map<String,List<Point>> getPath() {
        return path;
    }
}
