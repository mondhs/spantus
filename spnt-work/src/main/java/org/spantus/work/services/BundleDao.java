package org.spantus.work.services;

import java.io.File;

import org.spantus.work.SpantusBundle;

public interface BundleDao {
	public SpantusBundle read(File zipFile);
	public void write(SpantusBundle bundle, File zipFile);
}
