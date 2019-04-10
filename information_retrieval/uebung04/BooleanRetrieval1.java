import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class BooleanRetrieval1 {

	private Map<String, Set<String>> invertedFile;
	
	private long tokenCount = 0;
	private int documentCount = 0;

	private void enterTermsIntoInvertedFile(String DocumentID,
			String DocumentPart) {
		if (DocumentID == null)
			return;
		// System.out.println(DocumentID + " : " + DocumentPart);
		StringTokenizer st = new StringTokenizer(DocumentPart,
				" \t\n\r\f,.?!-&:|\"");
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			tokenCount++;
			Set<String> IdSet = invertedFile.get(token);
			if (IdSet == null)
				invertedFile.put(token, IdSet = new TreeSet<String>());
			IdSet.add(DocumentID);
		}
	}
	
	private void invertedFileStatistics() {
		PrintStream outStream;
		try {
			outStream = new PrintStream(new FileOutputStream("invFileStat.txt"));
		} catch (IOException e) {
			System.err.println("cannot write to invFileStat.txt");
			return;
		}        
		outStream.println("# of documents : " + documentCount);
		outStream.println("# of terms : " + invertedFile.size());
		outStream.println("# of tokens : " + tokenCount);
		outStream.println("total # of postings : " + invertedFile.values().stream().mapToLong(s -> s.size()).sum());
		outStream.println("# of singleton posting lists : " + invertedFile.values().stream().filter(s -> s.size()==1).count());
		outStream.close();
	}

	private class ConfigHandler extends DefaultHandler {

		private String key = null;
		private String Value = "";
		private boolean insideInterestingField = false;
		private int level = 0;

		public void setDocumentLocator(Locator locator) {
		}

		public void startElement(String namespaceURI, String localName,
				String rawName, Attributes atts) throws SAXException {

			level++;
			if (level == 2) {
				if (atts.getLength() > 0) {
					String s = atts.getValue("key");
					key = null;
					if (s != null && !s.startsWith("homepages/")) {
						key = s;
						documentCount++;
						if (documentCount % 100000 == 0)
							System.err.println(documentCount + " documents");
					}
				}
				return;
			}
			if (level == 3) {
				Value = "";
				insideInterestingField = rawName.equals("title");
			}
		}

		public void endElement(String namespaceURI, String localName,
				String rawName) throws SAXException {
			level--;
			if (level == 2 && insideInterestingField && Value.length() > 0)
				enterTermsIntoInvertedFile(key, Value);
		}

		public void characters(char[] ch, int start, int length)
				throws SAXException {
			if (insideInterestingField)
				Value += new String(ch, start, length);
		}

		private void Message(String mode, SAXParseException exception) {
			System.out.println(mode + " Line: " + exception.getLineNumber()
					+ " URI: " + exception.getSystemId() + "\n" + " Message: "
					+ exception.getMessage());
		}

		public void warning(SAXParseException exception) throws SAXException {

			Message("**Parsing Warning**\n", exception);
			throw new SAXException("Warning encountered");
		}

		public void error(SAXParseException exception) throws SAXException {

			Message("**Parsing Error**\n", exception);
			throw new SAXException("Error encountered");
		}

		public void fatalError(SAXParseException exception) throws SAXException {

			Message("**Parsing Fatal Error**\n", exception);
			throw new SAXException("Fatal Error encountered");
		}
	}

	public BooleanRetrieval1(String dblpXmlFileName) {
		invertedFile = new HashMap<String, Set<String>>();
		try {
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			SAXParser parser = parserFactory.newSAXParser();
			ConfigHandler handler = new ConfigHandler();
			parser.getXMLReader().setFeature(
					"http://xml.org/sax/features/validation", true);
			if (dblpXmlFileName.endsWith(".gz"))
				parser.parse(new GZIPInputStream(new FileInputStream(dblpXmlFileName)), handler);
			else
				parser.parse(new File(dblpXmlFileName), handler);
		} catch (IOException e) {
			System.out.println("Error reading URI: " + e.getMessage());
		} catch (SAXException e) {
			System.out.println("Error in parsing: " + e.getMessage());
		} catch (ParserConfigurationException e) {
			System.out.println("Error in XML parser configuration: "
					+ e.getMessage());
		}
		invertedFileStatistics();
	}

	public static void main(String[] args) {
		System.setProperty("entityExpansionLimit","2500000");
		if (args.length < 1) {
			System.out
					.println("Usage: java BooleanRetrieval1 [dblpXmlFileName]");
			System.exit(0);
		}
		new BooleanRetrieval1(args[0]);
	}

}
