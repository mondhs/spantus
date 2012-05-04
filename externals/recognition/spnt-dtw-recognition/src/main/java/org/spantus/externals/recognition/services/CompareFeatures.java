/*
 	Copyright (c) 2009 Mindaugas Greibus (spantus@gmail.com)
 	Part of program for analyze speech signal 
 	http://spantus.sourceforge.net

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
*/
package org.spantus.externals.recognition.services;

import java.io.File;

import org.spantus.core.FrameVectorValues;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IExtractorVector;
import org.spantus.core.extractor.IGeneralExtractor;
import org.spantus.core.extractor.dao.ReaderDao;
import org.spantus.logger.Logger;
import org.spantus.math.dtw.DtwService;
import org.spantus.math.services.MathServicesFactory;
import org.spantus.work.services.WorkExtractorReaderService;
import org.spantus.work.services.WorkServiceFactory;
import org.spantus.work.services.impl.WorkExtractorReaderServiceImpl;

public class CompareFeatures {

	private String workingExtractor = "LPC";
	private DtwService dtwService;
	private ReaderDao readerDao;
	private WorkExtractorReaderService extractorReaderService;

	@SuppressWarnings("unused")
	private Logger log = Logger.getLogger(getClass());

	public Double compareValues(FrameVectorValues targetValues, File sampleFile) {
		IExtractorInputReader sampleReader = getExtractorInputReader(sampleFile);
		IGeneralExtractor<?> sampleExtractor = getExtractorReaderService()
				.findExtractorByName(getWorkingExtractor(), sampleReader);
		Double distance = calculateDistance(targetValues, sampleExtractor);
		return distance;
	}

	/**
	 * 
	 * @param targetFile
	 * @param sampleFile
	 * @return
	 */
	public Double compareValues(File targetFile, File sampleFile) {
		IExtractorInputReader sampleReader = getExtractorInputReader(sampleFile);
		IExtractorInputReader targetReader = getExtractorInputReader(targetFile);
		IGeneralExtractor<?> sampleExtractor = getExtractorReaderService()
				.findExtractorByName(getWorkingExtractor(), sampleReader);
		IGeneralExtractor<?> targetExtractor = getExtractorReaderService()
				.findExtractorByName(getWorkingExtractor(), targetReader);
		;

		Double distance = calculateDistance(targetExtractor, sampleExtractor);
		return distance;
	}

	/**
	 * 
	 * @param readerFile
	 * @return
	 */
	protected IExtractorInputReader getExtractorInputReader(File readerFile) {
		return getReaderDao().read(readerFile);
	}

	/**
	 * 
	 * @param targetExtractor
	 * @param sampleExtractor
	 * @return
	 */
	public Double calculateDistance(IGeneralExtractor<?> targetExtractor,
			IGeneralExtractor<?> sampleExtractor) {
		if (targetExtractor instanceof IExtractor) {
			return getDtwService().calculateDistance(
					((IExtractor) targetExtractor).getOutputValues(),
					((IExtractor) sampleExtractor).getOutputValues());
		}
		if (targetExtractor instanceof IExtractorVector) {
			return getDtwService().calculateDistanceVector(
					((IExtractorVector) targetExtractor).getOutputValues(),
					((IExtractorVector) sampleExtractor).getOutputValues());
		}
		return null;

	}

	public Double calculateDistance(FrameVectorValues targetValues,
			IGeneralExtractor<?> sampleExtractor) {
		return getDtwService().calculateDistanceVector(targetValues,
				((IExtractorVector) sampleExtractor).getOutputValues());
	}


	public WorkExtractorReaderService getExtractorReaderService() {
		if (extractorReaderService == null) {
			extractorReaderService = new WorkExtractorReaderServiceImpl();
		}
		return extractorReaderService;
	}

	public DtwService getDtwService() {
		if (dtwService == null) {
			dtwService = MathServicesFactory.createDtwService();
		}
		return dtwService;
	}

	public ReaderDao getReaderDao() {
		if (readerDao == null) {
			readerDao = WorkServiceFactory.createReaderDao();
		}
		return readerDao;
	}

	public String getWorkingExtractor() {
		return workingExtractor;
	}

}
