package cz.mzk.recordmanager.server.kramerius.harvest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import cz.mzk.recordmanager.server.model.HarvestedRecord;
import cz.mzk.recordmanager.server.model.OAIHarvestConfiguration;
import cz.mzk.recordmanager.server.oai.dao.HarvestedRecordDAO;
import cz.mzk.recordmanager.server.oai.dao.OAIHarvestConfigurationDAO;
import cz.mzk.recordmanager.server.oai.harvest.OAIFormatResolver;
import cz.mzk.recordmanager.server.util.HibernateSessionSynchronizer;
import cz.mzk.recordmanager.server.util.HibernateSessionSynchronizer.SessionBinder;

public class KrameriusItemProcessor implements ItemProcessor<List<HarvestedRecord>, List<HarvestedRecord>>, StepExecutionListener {

	@Autowired
	protected HarvestedRecordDAO recordDao;

	@Autowired
	protected OAIHarvestConfigurationDAO configDao;

	@Autowired
	protected OAIFormatResolver formatResolver;

	@Autowired
	private HibernateSessionSynchronizer sync;

	private String format;
	
	private OAIHarvestConfiguration configuration;


	@Override
	public List<HarvestedRecord> process(List<HarvestedRecord> arg0) throws Exception {
		List<HarvestedRecord> result = new ArrayList<>();
		for (HarvestedRecord hrIncomplete: arg0) {
			result.add(completeHarvestedRecord(hrIncomplete));
		}
		return result;
	}

	private HarvestedRecord completeHarvestedRecord(
			HarvestedRecord hrIncomplete) {
		String recordId = hrIncomplete.getUniqueId().getRecordId(); //TODO check it! 
		HarvestedRecord rec = recordDao.findByIdAndHarvestConfiguration(recordId, configuration);
		if (rec == null) {
	//		HarvestedRecordUniqueId id = new HarvestedRecordUniqueId(configuration, recordId);
	//		rec = new HarvestedRecord(id);
			rec=hrIncomplete;
			rec.setHarvestedFrom(configuration);
			rec.setFormat(format);
		}
		rec.setRawRecord(hrIncomplete.getRawRecord());
		return rec;
	}

	
	@Override
	public void beforeStep(StepExecution stepExecution) {
		try (SessionBinder session = sync.register()) {
			Long confId = stepExecution.getJobParameters().getLong(
					"configurationId");
			configuration = configDao.get(confId);
			format = formatResolver.resolve("oai_dc"); //TODO - formats should be taken from configuration
		}	
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		return null;
	}


}
