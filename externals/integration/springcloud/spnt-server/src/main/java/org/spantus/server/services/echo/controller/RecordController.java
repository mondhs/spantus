package org.spantus.server.services.echo.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sound.sampled.AudioFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spantus.server.servlet.service.SpntEchoRepository;
import org.spantus.server.servlet.service.SpntServletConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.mit.csail.sls.wami.util.ContentType;

@Controller
public class RecordController {
	private static Logger LOG = LoggerFactory.getLogger(RecordController.class);
	public static final AudioFormat playFormat = new AudioFormat(
			AudioFormat.Encoding.PCM_SIGNED, 16000, 16, 1, 2, 16000, false);

	@Autowired
	SpntEchoRepository spntEchoRepository;
	private SpntServletConfigService configService;

	@RequestMapping(method = RequestMethod.GET, value = "/record")
	public void pingRecord(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		LOG.debug("PING");
	}

	@RequestMapping(method = RequestMethod.POST, value = "/record")
	public void postRecord(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		LOG.debug(
				"[doPost] request: {}",
				getConfigService().reconstructRequestURLandParams(
						request.getRequestURL().toString(),
						request.getParameterMap()));
		LOG.debug("[doPost] Handling portal recognize() post on session: {}",
				request.getSession().getId());

		AudioFormat audioFormat = getAudioFormatFromParams(request,
				"recordAudioFormat", "recordSampleRate", "recordIsLittleEndian");

		LOG.debug("[doPost]  audioFormat={}", audioFormat);
		spntEchoRepository.store(request.getInputStream(),audioFormat, getRecognizerRequiredAudioFormat());

	}

	/**
	 * 
	 * @param request
	 * @param formatParam
	 * @param sampleRateParam
	 * @param isLittleEndianParam
	 * @return
	 */
	private AudioFormat getAudioFormatFromParams(HttpServletRequest request,
			String formatParam, String sampleRateParam,
			String isLittleEndianParam) {
		LOG.debug("Record Content-Type {} ", request.getContentType());
		ContentType contentType = ContentType.parse(request.getContentType());
		String contentMajor = contentType.getMajor();
		String contentMinor = contentType.getMinor();

		// If Content-Type is valid, go with it
		if ("AUDIO".equals(contentMajor)) {
			if (contentMinor.equals("L16")) {
				// Content-Type = AUDIO/L16; CHANNELS=1; RATE=8000; BIG=false
				int rate = contentType.getIntParameter("RATE", 8000);
				int channels = contentType.getIntParameter("CHANNELS", 1);
				boolean big = contentType.getBooleanParameter("BIG", true);
				return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, rate,
						16, channels, 2, rate, big);
			}
		}

		// One of the clients that does not specify ContentType, or
		// sets it to something bogus
		String audioFormatStr = request.getParameter(formatParam);
		int sampleRate = Integer
				.parseInt(request.getParameter(sampleRateParam));
		boolean isLittleEndian = Boolean.parseBoolean(request
				.getParameter(isLittleEndianParam));

		if ("MULAW".equals(audioFormatStr)) {
			return new AudioFormat(AudioFormat.Encoding.ULAW, sampleRate, 8, 1,
					2, 8000, !isLittleEndian);
		} else if ("LIN16".equals(audioFormatStr)) {
			return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sampleRate,
					16, 1, 2, sampleRate, !isLittleEndian);
		}
		throw new UnsupportedOperationException("Unsupported audio format: '"
				+ audioFormatStr + "'");
	}

	/**
	 * 
	 * @return
	 */
	private AudioFormat getRecognizerRequiredAudioFormat() {
		return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 8000, 16, 1, 2,
				8000, false);
	}

	/**
	 * 
	 * @return
	 */
	public SpntServletConfigService getConfigService() {
		if (configService == null) {
			configService = new SpntServletConfigService();
		}
		return configService;
	}
}
