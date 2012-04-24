package org.spantus.server.services.echo.controller;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.transform.stream.StreamResult;

import org.spantus.exception.ProcessingException;
import org.spantus.server.dto.CorporaEntry;
import org.spantus.server.dto.CorporaEntryList;
import org.spantus.server.servlet.service.SpntEchoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.mongodb.gridfs.GridFSDBFile;

@Controller
public class CoporaController {
	public static final String CORPORA_XML_VIEW_KEY = "CORPORA_XML_VIEW_KEY";

	@Autowired
	private SpntEchoRepository spntEchoRepository;

	@Autowired
	private Jaxb2Marshaller jaxb2Marshaller;

	/**
	 * 
	 * @param id
	 * @param outputStream
	 * @throws IOException
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/corpora/entry/{id}")
	public void getEntry(@PathVariable String id, OutputStream outputStream)
			throws IOException {
		GridFSDBFile audioForOutput = spntEchoRepository.findOutputById(id);
		if (audioForOutput != null) {
			audioForOutput.writeTo(outputStream);
		} else {
			throw new ProcessingException("File not found");
		}
	}

	/**
	 * @param id
	 * 
	 * @param outputStream
	 * 
	 * @throws IOException
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/corpora/entry/{id}")
	public void updateEntry(@PathVariable String id, String description)
			throws IOException {
		spntEchoRepository.update(id, description);

	}

	/**
	 * 
	 * @param name
	 * @param description
	 * @param sample
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/corpora")
	public ModelAndView postEntry(@RequestParam MultipartFile sample)
			throws IOException {
		CorporaEntry entry = spntEchoRepository.store(sample.getInputStream());
		return newMAV(entry);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/corpora/entry/{id}")
	public void deleteEntry(@PathVariable String id) throws IOException {
		spntEchoRepository.delete(id);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/corpora")
	public ModelAndView findAllCorpora() {
		CorporaEntryList corporaEntryList = spntEchoRepository.findAll();
		return newMAV(corporaEntryList);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/corpora/export.zip")
	public void findAllCorporaZiped(OutputStream outputStream)
			throws IOException {

		ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(
				outputStream));
		CorporaEntryList corporaEntryList = spntEchoRepository.findAll();
		for (CorporaEntry entry : corporaEntryList) {
			GridFSDBFile audioForOutput = spntEchoRepository
					.findOutputById(entry.getObjectId().toString());
			if (audioForOutput != null) {
				zos.putNextEntry(new ZipEntry(entry.getFileName()));
				audioForOutput.writeTo(zos);
				zos.putNextEntry(new ZipEntry(entry.get_id() + ".xml"));
				jaxb2Marshaller.marshal(entry, new StreamResult(zos));
			}
		}
		zos.close();
	}

	private ModelAndView newMAV(Object obj) {
		ModelAndView mav = new ModelAndView(CORPORA_XML_VIEW_KEY);
		mav.addObject(obj);
		return mav;
	}

}
