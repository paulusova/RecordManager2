package cz.mzk.recordmanager.server.dc;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import cz.mzk.recordmanager.server.AbstractTest;
import cz.mzk.recordmanager.server.marc.MarcRecordFactory;
import cz.mzk.recordmanager.server.marc.MarcRecordImpl;
import cz.mzk.recordmanager.server.marc.MarcXmlParser;
import cz.mzk.recordmanager.server.metadata.MetadataRecord;
import cz.mzk.recordmanager.server.metadata.MetadataRecordFactory;
import cz.mzk.recordmanager.server.model.Cnb;
import cz.mzk.recordmanager.server.model.Issn;
import cz.mzk.recordmanager.server.model.Title;
import cz.mzk.recordmanager.server.oai.dao.HarvestedRecordDAO;

public class DublinCoreRecordImplTest extends AbstractTest {

	@Autowired
	private HarvestedRecordDAO hrdao;

	@Autowired
	private MarcXmlParser parser;

	@Autowired
	private MetadataRecordFactory metadataFactory;

	/* --- DublinCoreRecordImpl Tests --- */

	@Test
	public void getDateTest() {
		DublinCoreRecord rec = new DublinCoreRecordImpl();
		String date = rec.getFirstDate();
		Assert.assertEquals(null, date);
		Assert.assertTrue(rec.getDates().isEmpty());

		String testDate = "1984";
		String testDate2 = "2015";

		rec.addDate(testDate);
		rec.addDate(testDate2);

		date = rec.getFirstDate();
		Assert.assertEquals(testDate, date);

		String dateFromList = rec.getDates().get(0);
		Assert.assertEquals(testDate, dateFromList);

		dateFromList = rec.getDates().get(1);
		Assert.assertEquals(testDate2, dateFromList);
	}

	@Test
	public void getTitleTest() {
		DublinCoreRecord rec = new DublinCoreRecordImpl();

		String title = rec.getFirstTitle();
		Assert.assertEquals(null, title);
		Assert.assertTrue(rec.getTitles().isEmpty());

		String testTitle = "Babička";
		String testTitle2 = "Dědeček hříbeček";

		rec.addTitle(testTitle);
		rec.addTitle(testTitle2);

		title = rec.getFirstTitle();
		Assert.assertEquals(testTitle, title);

		String titleFromList = rec.getTitles().get(0);
		Assert.assertEquals(testTitle, titleFromList);

		titleFromList = rec.getTitles().get(1);
		Assert.assertEquals(testTitle2, titleFromList);
	}

	@Test
	public void getIdentifierTest() {
		DublinCoreRecord rec = new DublinCoreRecordImpl();

		String identifier = rec.getFirstIdentifier();
		Assert.assertEquals(null, identifier);

		String testIdentifier = "isbn:9780091949808";
		String testIdentifier2 = "uuid:ee7a8da0-a925-11e3-a5e2-0800200c9a66";

		rec.addIdentifier(testIdentifier);
		rec.addIdentifier(testIdentifier2);

		String identifierFromList = rec.getIdentifiers().get(0);
		Assert.assertEquals(testIdentifier, identifierFromList);

		identifierFromList = rec.getIdentifiers().get(1);
		Assert.assertEquals(testIdentifier2, identifierFromList);

	}

	/* --- _Metadata_ DublinCoreRecord tests --- */
	@Test
	public void getPublicationYearTest() throws Exception {

		DublinCoreRecord dcr = new DublinCoreRecordImpl();
		MetadataRecord metadataRecord;

		dcr.addDate("1982");
		metadataRecord = metadataFactory.getMetadataRecord(dcr);
		Assert.assertEquals(metadataRecord.getPublicationYear().longValue(),
				1982);
	}

	@Test
	public void getPublicationTitleTest() throws Exception {

		DublinCoreRecord dcr = new DublinCoreRecordImpl();
		MetadataRecord metadataRecord;

		String titleStr = "DesIgnatIo IConographICa OberLeVtensDorfenses PannarIas OffICInas"
				+ " VVLgo FabrICas PenICILLI arbItrIo Representans";
		dcr.addTitle(titleStr);

		metadataRecord = metadataFactory.getMetadataRecord(dcr);
		
		Title title = new Title();
		title.setTitleStr(titleStr);
		title.setOrderInRecord(1L);
		
		Assert.assertTrue(metadataRecord.getTitle().contains(title));
	}

	@Test
	public void getISSNsTest() throws Exception {
		DublinCoreRecord dcr = new DublinCoreRecordImpl();
		MetadataRecord metadataRecord;

		String issn1str = "issn:0322-9580";
		String issn2str = "ISSN:1211-068X";
		String notIssn = "hi:I:am:very:specific:identifier";
		dcr.addIdentifier(issn1str);
		dcr.addIdentifier(notIssn);
		dcr.addIdentifier(issn2str);

		metadataRecord = metadataFactory.getMetadataRecord(dcr);
		List<Issn> issns = metadataRecord.getISSNs();

		Issn issn1 = new Issn();
		issn1.setIssn("0322-9580");
		issn1.setOrderInRecord(1L);
		Assert.assertTrue(issns.contains(issn1));

		Issn issnN = new Issn();
		issnN.setIssn(notIssn);
		Assert.assertFalse(issns.contains(issnN));

		Issn issn2 = new Issn();
		issn2.setIssn("1211-068X");
		issn2.setOrderInRecord(2L);
		Assert.assertTrue(issns.contains(issn2));
	}

	@Test
	public void getCNBsTest() throws Exception {
		DublinCoreRecord dcr = new DublinCoreRecordImpl();
		MetadataRecord metadataRecord;

		// from K4 NDK NK - uuid:2bc5f0f0-987e-11e2-9a08-005056827e52
		String cnb1str = "ccnb:cnb000121063"; 
		//from Kram MZK - uuid:b602bb03-da24-4724-893d-23f9f3344498
		String cnb2str = "ccnb: cnb000790921"; 
		String notCnbStr = "uuid:not:cnb:identifier";
		dcr.addIdentifier(cnb1str);
		dcr.addIdentifier(notCnbStr);
		dcr.addIdentifier(cnb2str);

		metadataRecord = metadataFactory.getMetadataRecord(dcr);
		List<Cnb> cnbs = metadataRecord.getCNBs();

		Cnb cnb1 = new Cnb();
		cnb1.setCnb("cnb000121063");
		Assert.assertTrue(cnbs.contains(cnb1));

		Cnb cnbN = new Cnb();
		cnbN.setCnb(notCnbStr);
		Assert.assertFalse(cnbs.contains(cnbN));

		Cnb cnb2 = new Cnb();
		cnb2.setCnb("cnb000790921");
		Assert.assertTrue(cnbs.contains(cnb2));
	}
	
}
