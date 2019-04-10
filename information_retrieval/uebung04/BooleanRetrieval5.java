/*
 * 	for dblp.xml you need more main memory:
 * 
 *  java -mx3800M ...
 */

/*




 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
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


public class BooleanRetrieval5 {

	private Map<String, Set<Integer>> tmpInvertedFile;
	private Map<String, int[]> invertedFile;
	private List<String> intToDblpKey;

	private long tokenCount = 0;
	private int documentCount = 0;
    private int totalBitsDelta = 0;
    private int totalBitsGamma = 0;

	private void enterTermsIntoInvertedFile(String DocumentPart) {
        Integer DocumentID = new Integer(documentCount);
        // System.out.println(DocumentID + " : " + DocumentPart);
        StringTokenizer st = new StringTokenizer(DocumentPart,
                " \t\n\r\f,.?!-&:|\"");
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            tokenCount++;
            Set<Integer> IdSet = tmpInvertedFile.get(token);
            if (IdSet == null)
                tmpInvertedFile.put(token, IdSet = new TreeSet<Integer>());
            IdSet.add(DocumentID);
        }
    }

    private String toGammaNotation(int i, boolean helper){

        String result = "";

        if(i == 1) return "0";

        // Unärcode, wie viele Bits folgen
        String binString = Integer.toBinaryString(i);
        for(int j = 0; j < binString.length()-1; j++){
            result += "1";
        }

        // Trennzeichen
        result += "0";

        // Anzahl hinzufügen
        try {
            result += binString.substring(1);
        }catch (Exception e){}

        if(!helper) totalBitsGamma += result.length();

        return result;
    }

    private String toDeltaNotation(int i){
        String result = "";

        if(i == 1) return "0";

        // Gamma-Code, wie viele Bits folgen
        String binString = Integer.toBinaryString(i);
        result += toGammaNotation(binString.length(), true);

        // Anzahl hinzufügen
        try {
            result += binString.substring(1);
        }catch (Exception e){}

        totalBitsDelta += result.length();

        return result;
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
		outStream
				.println("total # of postings : "
						+ invertedFile.values().stream()
								.mapToLong(s -> s.length).sum());
		outStream.println("# of singleton posting lists : "
				+ invertedFile.values().stream().filter(s -> s.length == 1)
						.count());
		outStream.println();
		outStream.println("inverted file (posting list length / %):");
		String wordArray[] = new String[invertedFile.size()];
		int i = 0;
		for (Map.Entry<String, int[]> e : invertedFile.entrySet()) {
			int l = e.getValue().length;

			float p = ((float) l) / ((float) documentCount) * 100;
			wordArray[i++] = String.format("Gamma: %s Delta: %s %7d %8.5f  %s", toGammaNotation(l, false) , toDeltaNotation(l) ,l, p, e.getKey());
		}
		Arrays.sort(wordArray);
		for (String s : wordArray) {
			outStream.println(s);
		}

		System.out.println("Total Bits Gamma Notation: " + totalBitsGamma + "\n" +
                            "Total Bits Delta Notation: " + totalBitsDelta);

		outStream.close();
	}

	private class ConfigHandler extends DefaultHandler {

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
					if (s != null && !s.startsWith("homepages/")) {
						documentCount++;
						intToDblpKey.add(s);
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
				enterTermsIntoInvertedFile(Value);
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

	class query {

		private int lineNo;
		private String query;
		private StringTokenizer tokens;
		private String nextToken;

		private void getNext() {
			do {
				if (!tokens.hasMoreTokens()) {
					nextToken = null;
					return;
				}
				nextToken = tokens.nextToken();
				if (nextToken.equals("%")) {
					nextToken = null;
					return;
				}
			} while (nextToken.equals(" "));
		}

		/*
		 * queryDisjunction -> queryConjunction qDloop
		 * 
		 * qDloop -> '|' queryConjunction qDloop |
		 * 
		 * queryConjunction -> simpleQuery qCloop
		 * 
		 * qCloop -> '&' simpleQuery qCloop |
		 * 
		 * simpleQuery -> ( queryDisjunction ) | term
		 */

		private void syntaxError(String message) {
			System.out.println("Syntax error : " + message);
		}



            /*
             * !A
             */
        private int[] queryNegation(){
            int[] result1 = invertedFile.get(nextToken);

            int[] result3 = new int[documentCount - result1.length];



            for (String str: invertedFile.keySet()) {
                if (!str.equals(nextToken)){
                    int[] result2 = invertedFile.get(str);
                    int i1=0, i2=0, n=0;
                    while (n<result3.length && i1<result1.length && i2<result2.length) {
                        if (result1[i1] == result2[i2]) {
                            i1++; i2++;
                        } else if (result1[i1] < result2[i2]) {
                            result3[n++] = result2[i2];
                            i1++;
                        } else {
                            result3[n++] = result2[i2];
                            i2++;
                        }
                    }
                }
            }
            result1 = result3;
            System.out.println(lineNo + ":: for '!" + nextToken + "' : " + result1.length + " hits");
            getNext();

            return result1;
        }


		/*
		 * A | B | C | ...
		 */
		private int[] queryDisjunction() {
			int[] result1 = queryConjunction();
			while (nextToken != null && nextToken.equals("|")) {
				getNext();
				int[] result2 = queryConjunction();
				
				// ...

                /*
                 * first pass: calculate the array size
                 */
                int i1=0, i2=0, n=0;
                while (i1<result1.length && i2<result2.length) {
                    if (result1[i1] == result2[i2]) {
                        n++; i1++; i2++;
                    } else if (result1[i1] < result2[i2]) {
                        n++;
                        i1++;
                    } else {
                        n++;
                        i2++;
                    }
                }

                /*
                 * create the new array
                 */
                int[] result3 = new int[n];

                /*
                 * second pass: store the Union into the new array
                 */
                i1=0; i2=0; n=0;
                while (n<result3.length && i1<result1.length && i2<result2.length) {
                    if (result1[i1] == result2[i2]) {
                        result3[n++] = result1[i1];
                        i1++; i2++;
                    } else if (result1[i1] < result2[i2]) {
                        result3[n++] = result1[i1];
                        i1++;
                    } else {
                        result3[n++] = result2[i2];
                        i2++;
                    }
                }
                result1 = result3;


			}
			return result1;
		}

		/*
		 * A & B & C & ...
		 */
		private int[] queryConjunction() {
			int[] result1 = simpleQuery();
			while (nextToken != null && nextToken.equals("&")) {
				getNext();
				int[] result2 = simpleQuery();
				
				/*
				 * first pass: calculate the array size
				 */
				int i1=0, i2=0, n=0;
				while (i1<result1.length && i2<result2.length) {
					if (result1[i1] == result2[i2]) {
						n++; i1++; i2++;
					} else if (result1[i1] < result2[i2]) {
						i1++;
					} else {
						i2++;
					}
				}

				/*
				 * create the new array
				 */
				int[] result3 = new int[n];

				/*
				 * second pass: store the intersection into the new array
				 */
				i1=0; i2=0; n=0;
                while (n<result3.length && i1<result1.length && i2<result2.length) {
                    if (result1[i1] == result2[i2]) {
                        result3[n++] = result1[i1];
                        i1++; i2++;
                    } else if (result1[i1] < result2[i2]) {
                        i1++;
                    } else {
                        i2++;
                    }
                }
                result1 = result3;
			}
			return result1;
		}

		/*
		 * ( A )    or    term
		 */
		private int[] simpleQuery() {
			if (nextToken == null) {
				syntaxError("term or (...) expected");
				return new int[0];
			}
            if (nextToken.equals("!")) {
                getNext();
                if (nextToken == null || nextToken.equals(" ")) {
                    syntaxError("literal expected");
                    return new int[0];
                }
                int[] result = queryNegation();
                return result;
            }
			if (nextToken.equals("(")) {
				getNext();
				int[] result = queryDisjunction();
				if (nextToken == null || !nextToken.equals(")")) {
					syntaxError(") expected");
					return new int[0];
				}
				getNext();
				return result;
			}
			// consume term ...
			int[] result = invertedFile.get(nextToken);
			if (result == null)
				result = new int[0];
			System.out.println(lineNo + ":: for '" + nextToken + "' : " + result.length + " hits");
			
			getNext();
			return result;
		}

		private void analyzeQuery() {
			getNext();
			if (nextToken == null) {
				return;
			}
			int[] result = queryDisjunction();
			if (nextToken != null) {
				System.out.println(lineNo + ":: extra tokens : " + nextToken + " ...");
			}
			System.out.print(lineNo + "::: Total hits: " + result.length + " at ");
			for (int d: result) {
				// System.out.print(" " + d);     // numbers ...
				System.out.print(" " + intToDblpKey.get(d-1));  // ... or keys
			}
			System.out.println();
			System.out.println();
		}

		public query(String query, int lineNo) {
			this.lineNo = lineNo;
			this.query = query;
			System.out.println(lineNo + ": " + query);
			tokens = new StringTokenizer(query, " |&!()%", true);
			analyzeQuery();
		}
	}

	private void processTxtFile(String fname) {

		File file = new File(fname);
		int lineCount = 0;

		if (!file.canRead() || !file.isFile())
			System.exit(0);

		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(fname));
			String line = null;
			while ((line = in.readLine()) != null) {
				lineCount++;
				if (line.equals("quit!") || line.equals("exit!"))
					break;
				new query(line, lineCount);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
				}
		}
	}

	public BooleanRetrieval5(String dblpXmlFileName) {
		tmpInvertedFile = new HashMap<String, Set<Integer>>();
		intToDblpKey = new ArrayList<String>();
		try {
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			SAXParser parser = parserFactory.newSAXParser();
			ConfigHandler handler = new ConfigHandler();
			parser.getXMLReader().setFeature(
					"http://xml.org/sax/features/validation", true);
			if (dblpXmlFileName.endsWith(".gz"))
				parser.parse(new GZIPInputStream(new FileInputStream(
						dblpXmlFileName)), handler);
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
		invertedFile = new TreeMap<String, int[]>();

		for (Map.Entry<String, Set<Integer>> e : tmpInvertedFile.entrySet()) {
			int a[] = new int[e.getValue().size()], j = 0;
			for (Integer i : e.getValue()) {
				a[j++] = i.intValue();
			}
			invertedFile.put(e.getKey(), a);
		}
		tmpInvertedFile = null;
		invertedFileStatistics();
		processTxtFile("queries.txt");
	}

	public static void main(String[] args) {
		System.setProperty("entityExpansionLimit", "2500000");
		if (args.length < 1) {
			System.out
					.println("Usage: java BooleanRetrieval5 [dblpXmlFileName]");
			System.exit(0);
		}
		new BooleanRetrieval5(args[0]);
	}

}
