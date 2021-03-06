package cz.mzk.recordmanager.server.imports;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import cz.mzk.recordmanager.server.AbstractTest;
import cz.mzk.recordmanager.server.DBUnitHelper;
import cz.mzk.recordmanager.server.model.HarvestedRecord;
import cz.mzk.recordmanager.server.oai.dao.HarvestedRecordDAO;
import cz.mzk.recordmanager.server.util.Constants;

public class ImportRecordsJobTest extends AbstractTest {
	
	@Autowired
	private JobRegistry jobRegistry;

	@Autowired
	private JobLauncher jobLauncher;
	
	@Autowired
	private 
	HarvestedRecordDAO harvestedRecordDao;
	
	@Autowired
	private DBUnitHelper dbUnitHelper;

	private String testFileXML1;
	private String testFileXML2;
	private String testFileISO1;
	private String testFileISO2;
	private String testFileAleph1;
	private String testFileAleph2;
	private String testFileLine1;
	private String testFileLine2;
	
	@BeforeClass
	public void init() {
		//setup files
		testFileISO1 = this.getClass().getResource("/import/iso2709/MZK01-000000146.mrc").getFile();
		testFileISO2 = this.getClass().getResource("/import/iso2709/MZK-records.mrc").getFile();
		testFileXML1 = this.getClass().getResource("/import/marcxml/NLK-192047.xml").getFile();
		testFileXML2 = this.getClass().getResource("/import/marcxml/KKFB-records.xml").getFile();
		testFileAleph1 = this.getClass().getResource("/import/marcaleph/MZK01-000004171.txt").getFile();
		testFileAleph2 = this.getClass().getResource("/import/marcaleph/MZK-records.txt").getFile();
		testFileLine1 = this.getClass().getResource("/import/marcline/MZK01-000000116.mrc").getFile();
		testFileLine2 = this.getClass().getResource("/import/marcline/MZK-records.mrc").getFile();
	}
	
	@BeforeMethod
	public void initDb() throws Exception {
		dbUnitHelper.init("dbunit/ImportRecords.xml");
	}
	
	@Test
	public void testSimpleImportISO2709() throws Exception {
		Job job = jobRegistry.getJob(Constants.JOB_ID_IMPORT);
		Map<String, JobParameter> params = new HashMap<String, JobParameter>();
		params.put(Constants.JOB_PARAM_CONF_ID, new JobParameter(300L));
		params.put(Constants.JOB_PARAM_IN_FILE, new JobParameter(testFileISO1));
		params.put(Constants.JOB_PARAM_FORMAT, new JobParameter("iso"));
		JobParameters jobParams = new JobParameters(params);
		jobLauncher.run(job, jobParams);

		HarvestedRecord insertedRecord =  harvestedRecordDao.findByIdAndHarvestConfiguration("000000146", 300L);
		Assert.assertNotNull(insertedRecord);
	}
	
	@Test
	public void testSimpleImportMarcXML() throws Exception {
		Job job = jobRegistry.getJob(Constants.JOB_ID_IMPORT);
		Map<String, JobParameter> params = new HashMap<String, JobParameter>();
		params.put(Constants.JOB_PARAM_CONF_ID, new JobParameter(300L));
		params.put(Constants.JOB_PARAM_IN_FILE, new JobParameter(testFileXML1));
		params.put(Constants.JOB_PARAM_FORMAT, new JobParameter("xml"));
		JobParameters jobParams = new JobParameters(params);
		jobLauncher.run(job, jobParams);
		
		HarvestedRecord insertedRecord =  harvestedRecordDao.findByIdAndHarvestConfiguration("19790455", 300L);
		Assert.assertNotNull(insertedRecord);
	}
	
	@Test
	public void testSimpleImportMarcAleph() throws Exception {
		Job job = jobRegistry.getJob(Constants.JOB_ID_IMPORT);
		Map<String, JobParameter> params = new HashMap<String, JobParameter>();
		params.put(Constants.JOB_PARAM_CONF_ID, new JobParameter(300L));
		params.put(Constants.JOB_PARAM_IN_FILE, new JobParameter(testFileAleph1));
		params.put(Constants.JOB_PARAM_FORMAT, new JobParameter("aleph"));
		JobParameters jobParams = new JobParameters(params);
		jobLauncher.run(job, jobParams);
		
		Assert.assertNotNull(harvestedRecordDao.findByIdAndHarvestConfiguration("000004171", 300L));
	}
	
	@Test
	public void testSimpleImportMarcLine() throws Exception {
		Job job = jobRegistry.getJob(Constants.JOB_ID_IMPORT);
		Map<String, JobParameter> params = new HashMap<String, JobParameter>();
		params.put(Constants.JOB_PARAM_CONF_ID, new JobParameter(300L));
		params.put(Constants.JOB_PARAM_IN_FILE, new JobParameter(testFileLine1));
		params.put(Constants.JOB_PARAM_FORMAT, new JobParameter("line"));
		JobParameters jobParams = new JobParameters(params);
		jobLauncher.run(job, jobParams);
		
		Assert.assertNotNull(harvestedRecordDao.findByIdAndHarvestConfiguration("000000116", 300L));
	}
	
	@Test
	public void testMultileImportMarcXML() throws Exception {
		Job job = jobRegistry.getJob(Constants.JOB_ID_IMPORT);
		Map<String, JobParameter> params = new HashMap<String, JobParameter>();
		params.put(Constants.JOB_PARAM_CONF_ID, new JobParameter(300L));
		params.put(Constants.JOB_PARAM_IN_FILE, new JobParameter(testFileXML2));
		params.put(Constants.JOB_PARAM_FORMAT, new JobParameter("xml"));
		JobParameters jobParams = new JobParameters(params);
		jobLauncher.run(job, jobParams);
		
		Assert.assertNotNull(harvestedRecordDao.findByIdAndHarvestConfiguration("kpw0120405", 300L));
		Assert.assertNotNull(harvestedRecordDao.findByIdAndHarvestConfiguration("kpw0120531", 300L));
		Assert.assertNotNull(harvestedRecordDao.findByIdAndHarvestConfiguration("kpw0120435", 300L));
	}
	
	@Test
	public void testMultileImportMarcISO() throws Exception {
		Job job = jobRegistry.getJob(Constants.JOB_ID_IMPORT);
		Map<String, JobParameter> params = new HashMap<String, JobParameter>();
		params.put(Constants.JOB_PARAM_CONF_ID, new JobParameter(300L));
		params.put(Constants.JOB_PARAM_IN_FILE, new JobParameter(testFileISO2));
		params.put(Constants.JOB_PARAM_FORMAT, new JobParameter("iso"));
		JobParameters jobParams = new JobParameters(params);
		jobLauncher.run(job, jobParams);
		
		Assert.assertNotNull(harvestedRecordDao.findByIdAndHarvestConfiguration("000000117", 300L));
		Assert.assertNotNull(harvestedRecordDao.findByIdAndHarvestConfiguration("000000121", 300L));
		Assert.assertNotNull(harvestedRecordDao.findByIdAndHarvestConfiguration("000000120", 300L));
		Assert.assertNotNull(harvestedRecordDao.findByIdAndHarvestConfiguration("000000130", 300L));
		Assert.assertNotNull(harvestedRecordDao.findByIdAndHarvestConfiguration("000000132", 300L));
		Assert.assertNotNull(harvestedRecordDao.findByIdAndHarvestConfiguration("000000134", 300L));
	}
	
	@Test
	public void testMultipleImportMarcAleph() throws Exception {
		Job job = jobRegistry.getJob(Constants.JOB_ID_IMPORT);
		Map<String, JobParameter> params = new HashMap<String, JobParameter>();
		params.put(Constants.JOB_PARAM_CONF_ID, new JobParameter(300L));
		params.put(Constants.JOB_PARAM_IN_FILE, new JobParameter(testFileAleph2));
		params.put(Constants.JOB_PARAM_FORMAT, new JobParameter("aleph"));
		JobParameters jobParams = new JobParameters(params);
		jobLauncher.run(job, jobParams);
		
		Assert.assertNotNull(harvestedRecordDao.findByIdAndHarvestConfiguration("000000116", 300L));
		Assert.assertNotNull(harvestedRecordDao.findByIdAndHarvestConfiguration("000000117", 300L));
		Assert.assertNotNull(harvestedRecordDao.findByIdAndHarvestConfiguration("000000119", 300L));
		Assert.assertNotNull(harvestedRecordDao.findByIdAndHarvestConfiguration("000000120", 300L));
		Assert.assertNotNull(harvestedRecordDao.findByIdAndHarvestConfiguration("000000121", 300L));
	}
	
	@Test
	public void testMultipleImportMarcLine() throws Exception {
		Job job = jobRegistry.getJob(Constants.JOB_ID_IMPORT);
		Map<String, JobParameter> params = new HashMap<String, JobParameter>();
		params.put(Constants.JOB_PARAM_CONF_ID, new JobParameter(300L));
		params.put(Constants.JOB_PARAM_IN_FILE, new JobParameter(testFileLine2));
		params.put(Constants.JOB_PARAM_FORMAT, new JobParameter("line"));
		JobParameters jobParams = new JobParameters(params);
		jobLauncher.run(job, jobParams);
		
		Assert.assertNotNull(harvestedRecordDao.findByIdAndHarvestConfiguration("000000116", 300L));
		Assert.assertNotNull(harvestedRecordDao.findByIdAndHarvestConfiguration("000000117", 300L));
		Assert.assertNotNull(harvestedRecordDao.findByIdAndHarvestConfiguration("000000119", 300L));
		Assert.assertNotNull(harvestedRecordDao.findByIdAndHarvestConfiguration("000000120", 300L));
		Assert.assertNotNull(harvestedRecordDao.findByIdAndHarvestConfiguration("000000121", 300L));
	}
}
