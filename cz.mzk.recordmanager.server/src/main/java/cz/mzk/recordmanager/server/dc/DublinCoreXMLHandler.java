package cz.mzk.recordmanager.server.dc;

//import java.util.Stack;


import org.apache.commons.lang3.StringEscapeUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DublinCoreXMLHandler extends DefaultHandler {

	private StringBuffer sb;
	private DublinCoreRecord dcRecord;
//	private Stack<String> elementStack = new Stack<String>();
	
	private static final String DC_TITLE="dc:title";
	private static final String DC_CREATOR="dc:creator";
	private static final String DC_IDENTIFIER="dc:identifier";
	private static final String DC_TYPE="dc:type";
	private static final String DC_DATE="dc:date";
	

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
//		this.elementStack.push(qName);

		if ("oai_dc:dc".equals(qName)) {
			// New DC Record instance
			this.dcRecord = new DublinCoreRecordImpl();
		}
		
		sb = new StringBuffer();
		
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		
		if (sb != null) {
			String s = sb.toString();
			StringEscapeUtils.unescapeXml(s);
			
			switch (qName) {
			case DC_TITLE: 			
				this.dcRecord.addTitle(s);
				break;
			case DC_CREATOR:
				this.dcRecord.addCreator(s);
				break;
			case DC_IDENTIFIER: 
				this.dcRecord.addIdentifier(s);
				break;
			case DC_TYPE:
				this.dcRecord.addType(s);
				break;
			case DC_DATE:
				this.dcRecord.addDate(s);
			}
		}
		// Remove last added element
//		this.elementStack.pop();

	}

	/*
	 * checks element name and inputs value to correct array
	 */
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		
		if (sb != null) {
			sb.append(ch, start, length);
		}
		
//		String value = new String(ch, start, length).trim();

//		if (value.length() == 0) {
//			return; // ignore white space
//		}

		// handle the value based on to which element it belongs
//		if ("dc:title".equals(currentElement())) {
//			this.dcRecord.addTitle(value);
//		} else if ("dc:creator".equals(currentElement())) {
//			this.dcRecord.addCreator(value);
//		} else if ("dc:identifier".equals(currentElement())) {
//			this.dcRecord.addIdentifier(value);
//		} else if ("dc:type".equals(currentElement())) {
//			this.dcRecord.addType(value);
//		} else if ("dc:date".equals(currentElement())) {
//			this.dcRecord.addDate(value);
//		}
	}

//	private String currentElement() {
//		return (String) this.elementStack.peek();
//	}

	public DublinCoreRecord getRecord() {
		return this.dcRecord;
	}

}
