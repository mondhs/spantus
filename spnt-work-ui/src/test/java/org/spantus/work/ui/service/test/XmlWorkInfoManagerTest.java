package org.spantus.work.ui.service.test;

import java.io.File;
import java.util.Locale;

import junit.framework.TestCase;

import org.spantus.work.WorkReadersEnum;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.dto.SpantusWorkProjectInfo;
import org.spantus.work.ui.dto.WorkUIExtractorConfig;
import org.spantus.work.ui.dto.SpantusWorkProjectInfo.ProjectTypeEnum;
import org.spantus.work.ui.services.XmlWorkInfoManager;

public class XmlWorkInfoManagerTest extends TestCase {

	XmlWorkInfoManager workInfoManager;
	
	static final String PATH = "./target/";
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		workInfoManager = new XmlWorkInfoManager();
		workInfoManager.setConfigPath("./target/");
	}
	
	public void testEnivroment(){
		SpantusWorkInfo info = new SpantusWorkInfo();
		info.setLocale(Locale.getDefault());
		info.setProject(createProject());
		workInfoManager.saveWorkInfo(info);
		info = workInfoManager.openWorkInfo();
		assertNotNull(info);
		assertNotNull(info.getProject().getFeatureReader().getReaderPerspective());

	}
	public void testProject(){
		String fileName = PATH+"project.xml";
		SpantusWorkProjectInfo project = createProject();
		project.setCurrentType(ProjectTypeEnum.record.name());
		workInfoManager.saveProject(project, fileName);
		SpantusWorkProjectInfo loadedProject = workInfoManager.openProject(fileName);
		assertNotNull(loadedProject);
		assertEquals(project.getCurrentType(),loadedProject.getCurrentType());
	}
	
	protected SpantusWorkProjectInfo createProject(){
		SpantusWorkProjectInfo project = new SpantusWorkProjectInfo();
		project.getFeatureReader().setReaderPerspective(WorkReadersEnum.multiFeature);
		project.getFeatureReader().setWorkConfig(new WorkUIExtractorConfig());
		project.getFeatureReader().getExtractors().add("test1");
		project.getFeatureReader().getExtractors().add("test2");
		project.setWorkingDir(new File("."));
		return project;
	}
}
