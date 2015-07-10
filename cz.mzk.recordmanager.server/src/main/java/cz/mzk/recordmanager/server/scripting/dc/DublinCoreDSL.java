package cz.mzk.recordmanager.server.scripting.dc;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import cz.mzk.recordmanager.server.dc.DublinCoreRecord;
import cz.mzk.recordmanager.server.metadata.MetadataDublinCoreRecord;
import cz.mzk.recordmanager.server.scripting.Mapping;
import cz.mzk.recordmanager.server.scripting.MappingResolver;
import cz.mzk.recordmanager.server.scripting.function.RecordFunction;

public class DublinCoreDSL {
	
	private final DublinCoreRecord record;
	private MetadataDublinCoreRecord dcMetadataRecord;
	
	private final MappingResolver propertyResolver;
	
	private final Map<String, RecordFunction<DublinCoreRecord>> functions;

	public DublinCoreDSL(DublinCoreRecord record,
			MappingResolver propertyResolver,
			Map<String, RecordFunction<DublinCoreRecord>> functions) {
		super();
		this.record = record;
		this.propertyResolver = propertyResolver;
		this.functions = functions;
		this.dcMetadataRecord = new MetadataDublinCoreRecord(record);
	}
	
	public String getFirstTitle() {
		return record.getFirstTitle();
	}
	
	public List<String> getOtherTitles() {
		List<String> titles = record.getTitles();
		if (!titles.isEmpty()) {
			titles.remove(0); //removes first title which goes to different field
		}
		return titles;
	}
	
	public String getFirstCreator() {
		return record.getFirstCreator();
	}
	
	public List<String> getOtherCreators() {
		List<String> creators = record.getCreators();
		List<String> contributors = record.getContributors();
		if (!creators.isEmpty()) {
			creators.remove(0); //removes first creator who goes to different field
		}
		if (!contributors.isEmpty()) {
			creators.addAll(contributors); //adds all contributors to other creators
		} 
		return creators;
	}
	
	public String getFirstDate() {
		return record.getFirstDate();
	}
	
	public List <String> getPublishers() {
		return record.getPublishers();
	}
	
	public List <String> getSubjects() {
		return record.getSubjects();
	}
	
	public String getAllFields() {
		String result ="";
		if (!record.getCreators().isEmpty()) {result = result + record.getCreators().toString();}
		if (!record.getContributors().isEmpty()) {result = result + record.getContributors().toString();}
		if (!record.getLanguages().isEmpty()) {result = result + record.getLanguages().toString();}
		if (!record.getSubjects().isEmpty()) {result = result + record.getSubjects().toString();}
		if (!record.getTitles().isEmpty()) {result = result + record.getTitles().toString();}
		if (!record.getPublishers().isEmpty()) {result = result + record.getPublishers().toString();}
		if (!record.getDates().isEmpty()) {result = result + record.getDates().toString();}
		/* more to come..*/
		System.out.println("getAllFields: " + result);
		return result;
	}
	
	public String getFullrecord() {
		//TODO <MJ.> decide how should export work
		return getAllFields();		
	}
	
	
	public String translate(String file, String input, String defaultValue)
			throws IOException {
		Mapping mapping = propertyResolver.resolve(file);
		String result = (String) mapping.get(input);
		if (result == null) {
			result = defaultValue;
		}
		return result;
	}
	
	public Object methodMissing(String methodName, Object args) {
		RecordFunction<DublinCoreRecord> func = functions.get(methodName);
		if (func == null) {
			throw new IllegalArgumentException(String.format("missing function: %s", methodName));
		}
		return func.apply(record, args);
	}

}
