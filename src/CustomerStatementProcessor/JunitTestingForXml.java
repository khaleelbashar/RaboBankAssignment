package CustomerStatementProcessor;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(value = Parameterized.class)
public class JunitTestingForXml {

	private String referencenumber;
	private String accountnumber;
	private String description;
	private String startbalance;
	private String mutation;
	private String endbalance;

	public JunitTestingForXml(String referencenumber, String accountnumber, String description, String startbalance,
			String mutation, String endbalance) {
		super();
		this.referencenumber = referencenumber;
		this.accountnumber = accountnumber;
		this.description = description;
		this.startbalance = startbalance;
		this.mutation = mutation;
		this.endbalance = endbalance;
	}

	public String getReferencenumber() {
		return referencenumber;
	}

	public void setReferencenumber(String referencenumber) {
		this.referencenumber = referencenumber;
	}

	public String getAccountnumber() {
		return accountnumber;
	}

	public void setAccountnumber(String accountnumber) {
		this.accountnumber = accountnumber;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStartbalance() {
		return startbalance;
	}

	public void setStartbalance(String startbalance) {
		this.startbalance = startbalance;
	}

	public String getMutation() {
		return mutation;
	}

	public void setMutation(String mutation) {
		this.mutation = mutation;
	}

	public String getEndbalance() {
		return endbalance;
	}

	public void setEndbalance(String endbalance) {
		this.endbalance = endbalance;
	}

	@Parameters
	public static Collection testXML() throws IOException, SAXException, ParserConfigurationException {
		return fetchDataFromXml("C:/Basha/records.xml");
	}

	public static Collection<String[]> fetchDataFromXml(String xmlfileName)
			throws IOException, SAXException, ParserConfigurationException {
		List<String[]> listOfcustomerStatements = new ArrayList<String[]>();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new File(xmlfileName));
		doc.getDocumentElement().normalize();
		NodeList nodeList = doc.getElementsByTagName("record");

		for (int itr = 0; itr < nodeList.getLength(); itr++) {
			Node node = nodeList.item(itr);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) node;

				String[] customerStatements = { eElement.getAttribute("reference").toString(),
						eElement.getElementsByTagName("accountNumber").item(0).getTextContent().toString(),
						eElement.getElementsByTagName("description").item(0).getTextContent().toString(),
						eElement.getElementsByTagName("startBalance").item(0).getTextContent().toString(),
						eElement.getElementsByTagName("mutation").item(0).getTextContent().toString(),
						eElement.getElementsByTagName("endBalance").item(0).getTextContent().toString() };

				listOfcustomerStatements.add(customerStatements);
			}
		}

		return listOfcustomerStatements;
	}

	@Test
	public void testForXMLFile() throws Exception {
		JunitTestingForXml test = new JunitTestingForXml(referencenumber, accountnumber, description, startbalance,
				mutation, endbalance);

		if ("".equals(referencenumber)) {
			System.out.println("Please enter reference number");
			assertFalse("referencenumber", true);
		} else if ("".equals(accountnumber)) {
			System.out.println("Please enter accountnumber");
			assertFalse("accountnumber", true);
		} else if ("".equals(description)) {
			System.out.println("Please enter  description");
			assertFalse("description", true);
		} else if ("".equals(startbalance)) {
			System.out.println("Please enter startbalance");
			assertFalse("startbalance", true);
		} else if ("".equals(mutation)) {
			System.out.println("mutation is null");
			assertFalse("mutation", true);
		} else if ("".equals(endbalance)) {
			System.out.println("endbalance no is null");
			assertFalse("endbalance", true);
		}
		boolean isNumericVlaue = isNumeric(test.getStartbalance()) && isNumeric(test.getMutation());
		boolean isNumericEndBalance = isNumeric(test.getEndbalance());

		BigDecimal sumofBalaneAndMutation = BigDecimal.ZERO;
		if (isNumericVlaue) {
			if (!"".equals(test.getStartbalance()) && !"".equals(test.getMutation())) {
				sumofBalaneAndMutation = new BigDecimal(test.getStartbalance()).add(new BigDecimal(test.getMutation()));
			}
		}

		if (sumofBalaneAndMutation == BigDecimal.ZERO) {
			assertFalse("endbalance", true);
		}

		if (isNumericEndBalance) {
			BigDecimal endbalance = new BigDecimal(test.getEndbalance());
			if (sumofBalaneAndMutation != BigDecimal.ZERO && !endbalance.equals(sumofBalaneAndMutation)) {
				assertTrue("endbalance", true);
				System.out.println(
						"end balance is not macthing with the sum of the Startbalance and mutation and failed Reference No="
								+ referencenumber);
			}
		}
	}

	public static boolean isNumeric(String strNum) {
		Pattern pattern = Pattern.compile("[+-]?\\d*[,.]?\\d*");
		if (strNum == null || "".equals(strNum)) {
			return false;
		}
		return pattern.matcher(strNum).matches();
	}

}
