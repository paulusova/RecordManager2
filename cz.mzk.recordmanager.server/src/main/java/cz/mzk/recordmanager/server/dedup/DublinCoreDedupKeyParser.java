package cz.mzk.recordmanager.server.dedup;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import cz.mzk.recordmanager.server.dc.InvalidDcException;
import cz.mzk.recordmanager.server.metadata.MetadataRecord;
import cz.mzk.recordmanager.server.metadata.MetadataRecordFactory;
import cz.mzk.recordmanager.server.model.HarvestedRecord;
import cz.mzk.recordmanager.server.model.Title;
import cz.mzk.recordmanager.server.model.HarvestedRecordFormat.HarvestedRecordFormatEnum;
import cz.mzk.recordmanager.server.oai.dao.HarvestedRecordFormatDAO;
import cz.mzk.recordmanager.server.util.MetadataUtils;

@Component
public class DublinCoreDedupKeyParser implements DedupKeysParser {

	private final static String FORMAT = "dublinCore";
	
	private final static int EFFECTIVE_TITLE_LENGTH = 255;
	
	@Autowired 
	private MetadataRecordFactory metadataFactory;
	
	@Autowired 
	private HarvestedRecordFormatDAO harvestedRecordFormatDAO;
	
	@Override
	public List<String> getSupportedFormats() {
		return Collections.singletonList(FORMAT);
	}

	@Override
	public HarvestedRecord parse(HarvestedRecord record) {
		Preconditions.checkArgument(FORMAT.equals(record.getFormat()));
		try {		
			MetadataRecord metadata = metadataFactory.getMetadataRecord(record);
			return parse(record, metadata);
		} catch (InvalidDcException idce) {
			throw new DedupKeyParserException(idce.getMessage(), idce);
		}
	}

	@Override
	public HarvestedRecord parse(HarvestedRecord record,
			MetadataRecord metadataRecord) throws DedupKeyParserException {
		
		record.setIsbns(metadataRecord.getISBNs());
		List<Title> existingTitles = record.getTitles();
		for (Title title: metadataRecord.getTitle()) {
			title.setTitleStr(MetadataUtils.normalizeAndShorten(
						title.getTitleStr(),
						EFFECTIVE_TITLE_LENGTH));
			if (!existingTitles.contains(title)) {
				existingTitles.add(title);
			}
		}
		record.setTitles(existingTitles);
		
		record.setIssns(metadataRecord.getISSNs());
		record.setCnb(metadataRecord.getCNBs());
		if(record.getHarvestedFrom() != null) record.setWeight(metadataRecord.getWeight(record.getHarvestedFrom().getBaseWeight()));
		record.setPublicationYear(metadataRecord.getPublicationYear());
		List<HarvestedRecordFormatEnum> formatEnums = metadataRecord.getDetectedFormatList();
		record.setPhysicalFormats(harvestedRecordFormatDAO.getFormatsFromEnums(formatEnums));
		record.setAuthorAuthKey(metadataRecord.getAuthorAuthKey());
		record.setAuthorString(MetadataUtils.normalize(metadataRecord.getAuthorString()));
		record.setScale(metadataRecord.getScale());
		record.setUuid(metadataRecord.getUUId());
		record.setIssnSeries(MetadataUtils.normalize(metadataRecord.getISSNSeries()));
		record.setIssnSeriesOrder(MetadataUtils.normalize(metadataRecord.getISSNSeriesOrder()));
		return record;
	}
}
