package org.spantus.extractor.segments.likelihood;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.math.MatrixUtils;
import org.spantus.math.VectorUtils;

import com.google.common.collect.Lists;

public class PartialLikelihoodFunctionCalculation {

	public void calcaulate(FrameValues samples, FrameVectorValues feature,
			List<Double> gain) {
//		List<LinkedList<Double>> partialLikelihoodFunctions = Lists
//				.newLinkedList();

		List<Double> coef = null;
		if (gain == null) {
			coef = MatrixUtils.fill(feature.getDimention(), 1D);
		} else {
			coef = new LinkedList<Double>();
			for (Double float1 : gain) {
				coef.add(float1 * float1);
			}
		}
	}
		

	/**
	 * 
	 * @param samples
	 * @param sequence
	 * @param order
	 * @param numberOfSelectedFrames
	 */
	public List<LinkedList<Double>> processPartialLikelihoodFunctionCalculation(
			FrameValues samples, FrameVectorValues feature, int order,
			int numberOfSelectedFrames, List<Double> gain) {

		List<LinkedList<Double>> partialLikelihoodFunctions = Lists
				.newLinkedList();

		List<Double> coef = null;
		if (gain == null) {
			coef = MatrixUtils.fill(order, 1D);
		} else {
			coef = new LinkedList<Double>();
			for (Double float1 : gain) {
				coef.add(float1 * float1);
			}
		}

		ListIterator<Double> coefIter = coef.listIterator();
		ListIterator<List<Double>> featureIter = feature.listIterator();
		Double coefNext = coefIter.next();
		List<Double> fetureNext = featureIter.next();

		for (int i = 0; i < numberOfSelectedFrames - 1; i++) {

			Double coefCurrent = coefNext;
			coefNext = coefIter.next();

			List<Double> fetureCurrent = fetureNext;
			fetureNext = featureIter.next();

			LinkedList<Double> currentLikelihood = new LinkedList<Double>();
			currentLikelihood.addAll(MatrixUtils.zeros(order));
			for (int k = currentLikelihood.size(); k < samples.size(); k++) {
				Double previous = currentLikelihood.getLast();
				// Pirmas demuo
				Double calculate = previous;
				// Antras demuo
				calculate -=  Math.log10(Math.sqrt(coefCurrent));
				// Trecias demuo
				calculate += Math.log10(Math.sqrt(coefNext));

				// Ketvirtas demuo
				Double dTemp1 = 0D;
				ListIterator<Double> sampleIterator = samples.listIterator(k-order);
				for (ListIterator<Double> iterator = fetureCurrent.listIterator(); iterator
						.hasNext();) {
					Double sampleVal = (Double) iterator.next();
//					if (iterator.previousIndex() < k + order) {
//						break;
//					}
					dTemp1 += sampleIterator.next() * sampleVal;
					// dTemp1 += *(CAlgorithm::m_pdLpcParameters + i *
					// nLpcAnalysisOrder + j) * (*(pdWaveSamples + k - j));
				}

				calculate -= (dTemp1 * dTemp1) / (2 * coefCurrent);

				// Penktas demuo
				dTemp1 = 0D;
				 sampleIterator = samples.listIterator(k-order);
				for (ListIterator<Double> iterator = fetureNext.listIterator(); iterator
						.hasNext();) {
					Double sampleVal = (Double) iterator.next();
					dTemp1 += sampleIterator.next() * sampleVal;
					// dTemp1 += *(CAlgorithm::m_pdLpcParameters + i *
					// nLpcAnalysisOrder + j) * (*(pdWaveSamples + k - j));
				}

				calculate += (dTemp1 * dTemp1) / (2 * coefNext);
				currentLikelihood.add(calculate);

			}
			partialLikelihoodFunctions.add(currentLikelihood);
		}
		return partialLikelihoodFunctions;

	}

	/**
	 * 
	 * @param samples
	 * @param order
	 * @param numberOfPhonemes
	 * @param partialLikelihoodFunctions
	 * @return
	 */
	public List<LinkedList<Double>> processBellmanFunctionCalculation(
			FrameValues samples, int order, int numberOfPhonemes,
			List<LinkedList<Double>> partialLikelihoodFunctions) {
		List<LinkedList<Double>> bellmanFunctions = Lists.newLinkedList();
		ListIterator<LinkedList<Double>> partialLikelihoodIter = partialLikelihoodFunctions
				.listIterator();
		for (int i = 0; i < numberOfPhonemes - 1; i++) {
			// Isskiriamas masyvas eilinei Bellman'o funkciju aibei
			LinkedList<Double> currentBellmans = new LinkedList<Double>();

			LinkedList<Double> currentLikelihood = partialLikelihoodIter.next();

			// Pirmos p+2 Bellman'o funkciju reiksmes prilyginamos nuliui
			currentBellmans.addAll(MatrixUtils.zeros(order + i + 1));

			Double dTemp1 = 0D, dTemp2 = 0D;
			// Skaiciuojamos sekancios Bellman'o funkcijos reiksmes
			for (int j = (order + i + 1); j < samples.size(); j++) {
				if (i == 0) {
					// Nustatoma buvusioji Bellman'o funkcijos reiksme
					if (j < (order + 2)) {
						dTemp1 = currentLikelihood.getLast();
						// dTemp1 =
						// *(CAlgorithm::m_pdPartialLikelihoodFunctions[i] +
						// nLpcAnalysisOrder);
					} else {
						dTemp1 = currentBellmans.getLast();
						// dTemp1 = *(CAlgorithm::m_pdBellmanFunctions[i] + j -
						// 1);
					}
					// Nustatoma dabartine dalines tiketinumo funkcijos
					// reiksme
					dTemp2 = currentLikelihood.get(j - 1);
					// dTemp2 =
					// *(CAlgorithm::m_pdPartialLikelihoodFunctions[i] + j -
					// 1);
				} else {
					if (j < (order + i + 2)) {
						// dTemp1 =
						// *(CAlgorithm::m_pdPartialLikelihoodFunctions[i] +
						// nLpcAnalysisOrder + i) +
						// *(CAlgorithm::m_pdPartialLikelihoodFunctions[i - 1] +
						// nLpcAnalysisOrder + i);
					} else {
						dTemp1 = currentBellmans.getLast();
						// dTemp1 = *(CAlgorithm::m_pdBellmanFunctions[i] + j -
						// 1);
					}
					dTemp2 = bellmanFunctions.get(i - 1).get(j - 1)
							+ currentLikelihood.get(j - 1);
					// dTemp2 = *(CAlgorithm::m_pdBellmanFunctions[i - 1] +
					// j - 1) +
					// *(CAlgorithm::m_pdPartialLikelihoodFunctions[i] + j -
					// 1);
				}
				if (dTemp1 <= dTemp2) {
					currentBellmans.add(dTemp2);
					// *(CAlgorithm::m_pdBellmanFunctions[i] + j) = dTemp2;
				} else {
					currentBellmans.add(dTemp1);
					// *(CAlgorithm::m_pdBellmanFunctions[i] + j) = dTemp1;
				}

			}
			bellmanFunctions.add(currentBellmans);
		}
		return bellmanFunctions;
	}

	/**
	 * 
	 * @param partialLikelihoodFunctions
	 * @param bellmanFunctions
	 * @param numberOfSelectedFrames
	 * @param numberOfPhonemes
	 */
	public void processDetermineFunctionsMaxMin(
			List<LinkedList<Double>> partialLikelihoodFunctions,
			List<LinkedList<Double>> bellmanFunctions,
			int numberOfSelectedFrames, int numberOfPhonemes) {
		// Pirmiausiai randami tiketinumo ir Bellman'o funkciju moduliu
		// maksimumai
		// Isskiriami masyvai
		LinkedList<Double> partialLikelihoodFunctionsMaxModulus = Lists
				.newLinkedList();
		LinkedList<Double> bellmanFunctionsMaxModulus = Lists.newLinkedList();
		// Ieskoma maksimaliu reiksmiu
		for (int i = 0; i < numberOfPhonemes - 1; i++) {
			// Maksimalios daliniu tiketinumo funkciju reiksmes
			Double dTemp = VectorUtils.max(partialLikelihoodFunctions.get(i));
			partialLikelihoodFunctionsMaxModulus.set(i, dTemp);
			// Maksimalios Bellman'o funkciju reiksmes
			dTemp = VectorUtils.max(bellmanFunctions.get(i));
			bellmanFunctionsMaxModulus.set(i, dTemp);
		}

		// Dabar ieskomos tiketinumo funkciju minimalios vertes
		// Isskiriami masyvai
		LinkedList<Double> partialLikelihoodFunctionsMinModulus = Lists
				.newLinkedList();
		LinkedList<Double> bellmanFunctionsMinModulus = Lists.newLinkedList();
		for (int i = 0; i < numberOfPhonemes - 1; i++) {
			// Maksimalios daliniu tiketinumo funkciju reiksmes
			Double dTemp = VectorUtils.min(partialLikelihoodFunctions.get(i));
			partialLikelihoodFunctionsMinModulus.set(i, dTemp);
			// Maksimalios Bellman'o funkciju reiksmes
			dTemp = VectorUtils.min(bellmanFunctions.get(i));
			bellmanFunctionsMinModulus.set(i, dTemp);
		}
	}

	/**
	 * 
	 * @param numberOfCalculationIterations
	 * @param numberOfSelectedFrames
	 * @param bellmanFunctions
	 * @param sampleSize
	 */
	@SuppressWarnings("unused")
	public void processChangePointsDetection(int numberOfCalculationIterations,
			int numberOfSelectedFrames,
			List<LinkedList<Double>> bellmanFunctions, int sampleSize) {
		LinkedList<Boolean> changePoints = Lists.newLinkedList();
		for (int i = numberOfSelectedFrames - 2; i >= 0; i--) {
			Double dTemp = bellmanFunctions.get(i).get(sampleSize - 1);
			int minArg = 0;
			for (int j = sampleSize - 1; j >= 0; j--) {
				if (bellmanFunctions.get(i).get(j) < dTemp) {
					// Pridedama 2 nes indeksacija nuo nulio (+1) ir fiksuojamas
					// nepakites taskas
					// Keiciant strcPHONEMES.m_pnChangePoints dydi, (...+ i + 1)
					// pakeista (...+ i + 1)
					minArg = j + 1;
					// *(CAlgorithm::strcPHONEMES.m_pnChangePoints[CAlgorithm::m_nNumberOfCalculationIterations]
					// + i + 2) = (j + 1);
					break;
				}
			}

		}
	}
}
