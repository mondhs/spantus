/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.externals.recognition.bean;

import java.awt.Point;
import java.util.List;

/**
 *
 * @author mondhs
 */
public class RecognitionResultDetails extends RecognitionResult {

    private List<Point> path;

    public void setPath(List<Point> path) {
        this.path = path;
    }

    public List<Point> getPath() {
        return path;
    }
}
