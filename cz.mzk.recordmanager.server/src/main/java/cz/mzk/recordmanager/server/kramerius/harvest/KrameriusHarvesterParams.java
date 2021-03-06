package cz.mzk.recordmanager.server.kramerius.harvest;

import java.util.Date;

import cz.mzk.recordmanager.server.model.OAIGranularity;

public class KrameriusHarvesterParams {

	private String url;

	private String metadataStream;

	private String model;
	
	private Long queryRows;

	private Date from;

	private Date until;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMetadataStream() {
		return metadataStream;
	}

	public void setMetadataStream(String metadataStream) {
		this.metadataStream = metadataStream;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}
	
	public Date getFrom() {
		return from;
	}

	public void setFrom(Date from) {
		this.from = from;
	}

	public Date getUntil() {
		return until;
	}

	public void setUntil(Date until) {
		this.until = until;
	}

	public Long getQueryRows() {
		return this.queryRows;
	}
	
	public void setQueryRows(Long queryRows) {
		this.queryRows=queryRows;
	}
	
	@Override
	public String toString() {
		return "KrameriusHarvesterParams [url=" + url + ", metadataStream="
				+ metadataStream + ", model=" + model + ", queryRows="
				+ queryRows + ", from=" + from + ", until=" + until + "]";
	}
}
