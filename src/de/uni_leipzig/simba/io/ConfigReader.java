package de.uni_leipzig.simba.io;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.uni_leipzig.simba.data.Restriction;
import de.uni_leipzig.simba.controller.LimesLogger;

public class ConfigReader {

	public int characterLength;
	public int blockingThreshold;
	public String blockedFile;
	public int blockingVerificationThreshold;
	public String blockingVerificationFile;
	public int comparisonThreshold;
	public String comparedFile;
	public int comparisonVerificationThreshold;
	public String comparisonVerificationFile;
	public String hashFunction;
	public KBInfo source;
	public KBInfo target;
	public String algorithm;

	public LimesLogger LimesLogger;
	HashMap<String, String> prefixes;

	public ConfigReader() {
		LimesLogger = LimesLogger.getInstance();
		prefixes = new HashMap<String, String>();
	}

	public boolean validateAndRead(String input){
		DtdChecker dtdChecker = new DtdChecker();
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(true);
			DocumentBuilder builder;
			builder = factory.newDocumentBuilder();
			builder.setErrorHandler(dtdChecker);
			Document xmlDocument = builder.parse(new FileInputStream(input));

			if (dtdChecker.valid) {
				//now extract what we need
				LimesLogger.info("File is valid. Parsing ...");
				source = new KBInfo();
				target = new KBInfo();
				//0. Prefixes
				NodeList list = xmlDocument.getElementsByTagName("PREFIX");
				NodeList children;
				String namespace = "", label = "";
				for (int i = 0; i < list.getLength(); i++) {
					children = list.item(i).getChildNodes();
					for (int j = 0; j < children.getLength(); j++) {
						Node child = children.item(j);
						if (child.getNodeName().equals("NAMESPACE")) {
							namespace = getText(child);
						} else if (child.getNodeName().equals("LABEL")) {
							label = getText(child);
						}
					}
					prefixes.put(label, namespace);
				}
				//1. Source information
				list = xmlDocument.getElementsByTagName("SOURCE");
				children = list.item(0).getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					Node child = children.item(i);
					if (child.getNodeName().equals("ID")) {
						source.id = getText(child);
					} else if (child.getNodeName().equals("ENDPOINT")) {
						source.endpoint = getText(child);
					} else if (child.getNodeName().equals("GRAPH")) {
						source.graph = getText(child);
					} else if (child.getNodeName().equals("RESTRICTION")) {
						source.restrictions.add(Restriction.fromString(getText(child)));
					} else if (child.getNodeName().equals("PROPERTY")) {
						source.properties.add(getText(child));
					} else if (child.getNodeName().equals("PAGESIZE")) {
						source.pageSize = Integer.parseInt(getText(child));
					} else if (child.getNodeName().equals("VAR")) {
						source.var = getText(child);
					}
				}
				source.prefixes = prefixes;
				LimesLogger.info("Source config\n" + source.toString());
				//2. Target information
				list = xmlDocument.getElementsByTagName("TARGET");
				children = list.item(0).getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					Node child = children.item(i);
					if (child.getNodeName().equals("ID")) {
						target.id = getText(child);
					} else if (child.getNodeName().equals("ENDPOINT")) {
						target.endpoint = getText(child);
					} else if (child.getNodeName().equals("GRAPH")) {
						target.graph = getText(child);
					} else if (child.getNodeName().equals("RESTRICTION")) {
						target.restrictions.add(Restriction.fromString(getText(child)));
					} else if (child.getNodeName().equals("PROPERTY")) {
						target.properties.add(getText(child));
					} else if (child.getNodeName().equals("PAGESIZE")) {
						target.pageSize = Integer.parseInt(getText(child));
					} else if (child.getNodeName().equals("VAR")) {
						target.var = getText(child);
					}
				}
				target.prefixes = prefixes;
				LimesLogger.info("Target config\n" + target.toString());

				//5. BLOCKING file and conditions
				list = xmlDocument.getElementsByTagName("BLOCKING");
				children = list.item(0).getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					Node child = children.item(i);
					if (child.getNodeName().equals("THRESHOLD")) {
						blockingThreshold = Integer.valueOf(getText(child));
					} else if (child.getNodeName().equals("FILE")) {
						blockedFile = getText(child);
					} else if (child.getNodeName().equals("RELATION")) {
						//blockingRelation = getText(child);
					}
				}
				LimesLogger.info("Instances with similarity beyond " + blockingThreshold + " "
						+ "will be written in " + blockedFile + " and linked with "/* + blockingRelation*/);

				//5. BLOCKING VERIFICATION file and conditions
				list = xmlDocument.getElementsByTagName("BLOCKING_VERIFICATION");
				children = list.item(0).getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					Node child = children.item(i);
					if (child.getNodeName().equals("THRESHOLD")) {
						blockingVerificationThreshold = Integer.valueOf(getText(child));
					} else if (child.getNodeName().equals("FILE")) {
						blockingVerificationFile = getText(child);
					} else if (child.getNodeName().equals("RELATION")) {
						//blockingRelation = getText(child);
					}
				}
				LimesLogger.info("Instances with similarity beyond " + blockingVerificationThreshold + " "
						+ "will be written in " + blockingVerificationFile + " and linked with "/* + blockingRelation*/);
				
				//5. COMPARISON file and conditions
				list = xmlDocument.getElementsByTagName("COMPARISON");
				children = list.item(0).getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					Node child = children.item(i);
					if (child.getNodeName().equals("THRESHOLD")) {
						comparisonThreshold = Integer.valueOf(getText(child));
					} else if (child.getNodeName().equals("FILE")) {
						comparedFile = getText(child);
					} else if (child.getNodeName().equals("RELATION")) {
						//blockingRelation = getText(child);
					}
				}
				LimesLogger.info("Instances with similarity beyond " + comparisonThreshold + " "
						+ "will be written in " + comparedFile + " and linked with "/* + blockingRelation*/);

				//5. COMPARISON file and conditions
				list = xmlDocument.getElementsByTagName("COMPARISON_VERIFICATION");
				children = list.item(0).getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					Node child = children.item(i);
					if (child.getNodeName().equals("THRESHOLD")) {
						comparisonVerificationThreshold = Integer.valueOf(getText(child));
					} else if (child.getNodeName().equals("FILE")) {
						comparisonVerificationFile = getText(child);
					} else if (child.getNodeName().equals("RELATION")) {
						//blockingRelation = getText(child);
					}
				}
				
				list = xmlDocument.getElementsByTagName("ALGORITHM");
				children = list.item(0).getChildNodes();
				for(int i = 0; i < children.getLength(); i++) {
					Node child = children.item(i);
					if(child.getNodeName().equals("NAME")) {
						algorithm = getText(child);
					}else if(child.getNodeName().equals("CHARACTER_LENGTH")) {
						characterLength = Integer.valueOf(getText(child));
					}
				}
				
				LimesLogger.info("Instances with similarity beyond " + comparisonVerificationThreshold + " "
						+ "will be written in " + comparisonVerificationFile + " and linked with "/* + blockingRelation*/);

			}

		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	/** Returns the content of a node
	 * 
	 * @param node an item of the form <NODE> text </NODE>
	 * @return The text between <NODE> and </NODE>
	 */
	public static String getText(Node node) {

		// We need to retrieve the text from elements, entity
		// references, CDATA sections, and text nodes; but not
		// comments or processing instructions
		int type = node.getNodeType();
		if (type == Node.COMMENT_NODE
				|| type == Node.PROCESSING_INSTRUCTION_NODE) {
			return "";
		}

		StringBuffer text = new StringBuffer();

		String value = node.getNodeValue();
		if (value != null) {
			text.append(value);
		}
		if (node.hasChildNodes()) {
			NodeList children = node.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				text.append(getText(child));
			}
		}

		return text.toString();

	}
	/**
	 * Returns config of source knowledge base
	 * @return
	 */
	public KBInfo getSourceInfo() {
		return source;
	}

	/**
	 * Returns config of target knowledge base
	 * @return
	 */
	public KBInfo getTargetInfo() {
		return target;
	}

	/**
	 * @return
	 */
	public String createTitle()
	{	
		String[] sourceMiddle = source.endpoint.split("/")[2].split("\\.");
		String[] targetMiddle = target.endpoint.split("/")[2].split("\\.");
		return
				sourceMiddle[sourceMiddle.length-2]+'_'+
				targetMiddle[targetMiddle.length-2];

	}
}
