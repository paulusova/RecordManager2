package cz.mzk.recordmanager.server.metadata.institutions;

import java.util.regex.Matcher;

import cz.mzk.recordmanager.server.marc.MarcRecord;
import cz.mzk.recordmanager.server.metadata.MetadataMarcRecord;

public class SkatMarcMetadataRecord extends MetadataMarcRecord {

	public SkatMarcMetadataRecord(MarcRecord underlayingMarc) {
		super(underlayingMarc);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String getUUId() {
		String baseStr = underlayingMarc.getField("911", 'u');
		if (baseStr == null) {
			return null;
		}

		Matcher matcher = UUID_PATTERN.matcher(baseStr);
		if (matcher.find()) {
			String uuidStr = matcher.group(0);
			if (uuidStr != null && uuidStr.length() > 5) {
				return uuidStr.substring(5);
			}
		}
		return null;
	}
	
}
