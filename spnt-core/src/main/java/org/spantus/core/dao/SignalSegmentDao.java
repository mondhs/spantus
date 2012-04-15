package org.spantus.core.dao;

import java.io.File;
import java.io.OutputStream;

import org.spantus.core.beans.SignalSegment;

public interface SignalSegmentDao {

	public abstract void write(SignalSegment segment, File file);

	public abstract void write(SignalSegment segment, OutputStream outputStream);

}