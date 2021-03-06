package cz.mzk.recordmanager.server.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.google.common.base.Preconditions;


@Entity
@Table(name=HarvestedRecord.TABLE_NAME)
public class HarvestedRecord extends AbstractDomainObject {

	public static final String TABLE_NAME = "harvested_record";
	
	@Embeddable
	public static class HarvestedRecordUniqueId implements Serializable {

		private static final long serialVersionUID = 1L;
		
		@Column(name="import_conf_id")
		private Long harvestedFromId;

		@Column(name="record_id")
		private String recordId;

		// for hibernate
		private HarvestedRecordUniqueId() {
		}
		
		public HarvestedRecordUniqueId(ImportConfiguration harvestedFrom,
				String recordId) {
			super();
			Preconditions.checkNotNull(harvestedFrom, "harvestedFrom");
			Preconditions.checkNotNull(recordId, "recordId");
			this.harvestedFromId = harvestedFrom.getId();
			this.recordId = recordId;
		}
		
		public HarvestedRecordUniqueId(Long harvestedFromId,
				String recordId) {
			super();
			Preconditions.checkNotNull(harvestedFromId, "harvestedFromId");
			Preconditions.checkNotNull(recordId, "recordId");
			this.harvestedFromId = harvestedFromId;
			this.recordId = recordId;
		}

		public Long getHarvestedFromId() {
			return harvestedFromId;
		}

		public String getRecordId() {
			return recordId;
		}

		@Override
		public int hashCode() {
			return Objects.hash(harvestedFromId, recordId);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}	
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			HarvestedRecordUniqueId other = (HarvestedRecordUniqueId) obj;
			return Objects.equals(this.getHarvestedFromId(), other.getHarvestedFromId())
					&& Objects.equals(this.getRecordId(), other.getRecordId());
		}

		@Override
		public String toString() {
			return String.format("%s[harvestedFromId=%s, recordId=%s]",
					getClass().getSimpleName(), harvestedFromId, recordId);
		}

	}
	
	@Embedded
	private HarvestedRecordUniqueId uniqueId;
	
	@Column(name="raw_001_id")
	private String raw001Id;
	
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	@JoinColumn(name="import_conf_id", nullable=false, updatable=false, insertable=false)
	private ImportConfiguration harvestedFrom;

	@Column(name="harvested")
	@Temporal(TemporalType.TIMESTAMP)
	private Date harvested = new Date();
	
	@Column(name="updated")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updated = harvested;
	
	@Column(name="deleted")
	@Temporal(TemporalType.TIMESTAMP)
	private Date deleted;
	
	@Column(name="format")
	private String format;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name="harvested_record_id", referencedColumnName="id", nullable=false)
	private List<Isbn> isbns = new ArrayList<Isbn>();
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name="harvested_record_id", referencedColumnName="id", nullable=false)
	private List<Issn> issns = new ArrayList<Issn>();
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name="harvested_record_id", referencedColumnName="id", nullable=false)
	private List<Cnb> cnb = new ArrayList<Cnb>();
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name="harvested_record_id", referencedColumnName="id", nullable=false)
	private List<Title> titles = new ArrayList<Title>();
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name="harvested_record_id", referencedColumnName="id", nullable=false)
	private List<Oclc> oclcs = new ArrayList<Oclc>();

	@OneToMany(mappedBy="id.harvestedRecordId", cascade = CascadeType.ALL)
	@MapKey(name="id.langStr")
	private Map<String, Language> languages = new HashMap<>();

	// TODO consider moving dedup keys to separate table
	@Column(name="author_auth_key")
	private String authorAuthKey;
	
	@Column(name="author_string")
	private String authorString;
	
	@Column(name="issn_series")
	private String issnSeries;
	
	@Column(name="issn_series_order")
	private String issnSeriesOrder;
	
	@Column(name="uuid")
	private String uuid;
	
	@Column(name="scale")
	private Long scale;
	
	@Column(name="publication_year")
	private Long publicationYear;

	@OneToMany
	@JoinTable(
	   name = "harvested_record_format_link", 
	   joinColumns = @JoinColumn(name = "harvested_record_id "), 
	   inverseJoinColumns = @JoinColumn(name = "harvested_record_format_id "))
	private List<HarvestedRecordFormat> physicalFormats = new ArrayList<>();
	
	@Basic(fetch = FetchType.LAZY)
	@Column(name="raw_record") 
	private byte[] rawRecord;
	
	@ManyToOne(optional=true, fetch=FetchType.LAZY)
	@JoinColumn(name="dedup_record_id", nullable=true)
	private DedupRecord dedupRecord;
	
	@Column(name="weight")
	private Long weight;
	
	@Column(name="cluster_id")
	private String clusterId;
	
	@Column(name="pages")
	private Long pages;
	
	/**
	 * indicator variable used for filtering reasons
	 */
	@Transient
	private boolean shouldBeProcessed = true;
	
	
	private HarvestedRecord() {
	}
	
	public HarvestedRecord(HarvestedRecordUniqueId id) {
		super();
		this.uniqueId = id;
	}

	public HarvestedRecordUniqueId getUniqueId() {
		return uniqueId;
	}

	public void setId(HarvestedRecordUniqueId id) {
		this.uniqueId = id;
	}

	public ImportConfiguration getHarvestedFrom() {
		return harvestedFrom;
	}

	public void setHarvestedFrom(ImportConfiguration harvestedFrom) {
		this.harvestedFrom = harvestedFrom;
	}

	public Date getHarvested() {
		return harvested;
	}

	public void setHarvested(Date harvested) {
		this.harvested = harvested;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public Date getDeleted() {
		return deleted;
	}

	public void setDeleted(Date deleted) {
		this.deleted = deleted;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}
	
	public List<Isbn> getIsbns() {
		return isbns;
	}

	public void setIsbns(List<Isbn> isbns) {
		this.isbns = isbns;
	}

	public List<Title> getTitles() {
		return titles;
	}

	public void setTitles(List<Title> titles) {
		this.titles = titles;
	}
	
	public Long getPublicationYear() {
		return publicationYear;
	}

	public void setPublicationYear(Long publicationYear) {
		this.publicationYear = publicationYear;
	}

	public byte[] getRawRecord() {
		return rawRecord;
	}

	public void setRawRecord(byte[] rawRecord) {
		this.rawRecord = rawRecord;
	}

	public DedupRecord getDedupRecord() {
		return dedupRecord;
	}

	public void setDedupRecord(DedupRecord dedupRecord) {
		this.dedupRecord = dedupRecord;
	}

	public List<HarvestedRecordFormat> getPhysicalFormats() {
		return physicalFormats;
	}

	public void setPhysicalFormats(List<HarvestedRecordFormat> physicalFormats) {
		this.physicalFormats = physicalFormats;
	}

	public Long getWeight() {
		return weight;
	}

	public void setWeight(Long weight) {
		this.weight = weight;
	}

	public String getAuthorAuthKey() {
		return authorAuthKey;
	}

	public void setAuthorAuthKey(String authorAuthKey) {
		this.authorAuthKey = authorAuthKey;
	}

	public String getAuthorString() {
		return authorString;
	}

	public void setAuthorString(String authorString) {
		this.authorString = authorString;
	}

	public String getIssnSeries() {
		return issnSeries;
	}

	public void setIssnSeries(String issnSeries) {
		this.issnSeries = issnSeries;
	}

	public String getIssnSeriesOrder() {
		return issnSeriesOrder;
	}

	public void setIssnSeriesOrder(String issnSeriesOrder) {
		this.issnSeriesOrder = issnSeriesOrder;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Long getScale() {
		return scale;
	}

	public void setScale(Long scale) {
		this.scale = scale;
	}

	public List<Issn> getIssns() {
		return issns;
	}

	public void setIssns(List<Issn> issns) {
		this.issns = issns;
	}

	public List<Cnb> getCnb() {
		return cnb;
	}

	public void setCnb(List<Cnb> cnb) {
		this.cnb = cnb;
	}

	public String getClusterId() {
		return clusterId;
	}

	public void setClusterId(String clusterId) {
		this.clusterId = clusterId;
	}

	public Long getPages() {
		return pages;
	}

	public void setPages(Long pages) {
		this.pages = pages;
	}

	public List<Oclc> getOclcs() {
		return oclcs;
	}

	public void setOclcs(List<Oclc> oclcs) {
		this.oclcs = oclcs;
	}

	public Set<String> getLanguages() {
		return languages.keySet();
	}
	
	public void setLanguages(List<String> newLangs) {
		for (String newLang : newLangs) {
			if (!languages.containsKey(newLang)) {
				languages.put(newLang, new Language(this, newLang));
			}
		}
		Iterator<String> iter = languages.keySet().iterator();
		while (iter.hasNext()) {
			String lang = iter.next();
			if (!newLangs.contains(lang)) {
				iter.remove();
			}
		}
	}

	public boolean getShouldBeProcessed() {
		return shouldBeProcessed;
	}

	public void setShouldBeProcessed(boolean shouldBeProcessed) {
		this.shouldBeProcessed = shouldBeProcessed;
	}

	public String getRaw001Id() {
		return raw001Id;
	}

	public void setRaw001Id(String raw001Id) {
		this.raw001Id = raw001Id;
	}

	@Override
	public String toString() {
		return String.format("HarvestedRecord[id=%s, uniqueId=%s]", getId(), getUniqueId());
	}

}
