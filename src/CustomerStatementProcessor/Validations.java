package CustomerStatementProcessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Validations {

	private static Pattern pattern = Pattern.compile("[+-]?\\d*[,.]?\\d*");

	public static void main(String[] args) {

		Scanner scanner = new Scanner(System.in);
		System.out.println("Please Enter File name");

		String fileName = scanner.nextLine();

		if (fileName.contains(".csv")) {
			readCsvFile(fileName);
		} else if (fileName.contains(".xml")) {
			readXmlFile(fileName);
		} else {
			System.out.println("Please enter either csv or xml file name");
		}

	}

	private static void readCsvFile(String fileName) {
		String line = "";
		String splitBy = ",";
		List<Integer> listOfTransactionRefernces = new ArrayList<Integer>();
		String[] customerStatements = null;
		Integer refNo = 0;
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(new File("").getAbsolutePath()+"/src/CustomerStatementProcessor/" + fileName)));
			while ((line = bufferedReader.readLine()) != null) {
				customerStatements = line.split(splitBy);

				if (!("Start Balance".equalsIgnoreCase(customerStatements[3]))) {

					if ("".equals(customerStatements[0]) || "".equals(customerStatements[1])
							|| "".equals(customerStatements[2]) || "".equals(customerStatements[3])
							|| "".equals(customerStatements[4]) || "".equals(customerStatements[5])) {
						System.out.println("Please provide valid input in the excel sheet for the fields");
					}

					boolean isReferencNoNumeric = isNumeric(customerStatements[0]);

					if ("".equals(customerStatements[0])) {
						++refNo;
					}

					else if (isReferencNoNumeric && !"".equals(customerStatements[0])) {
						refNo = Integer.parseInt(customerStatements[0]);
					}

					if (!listOfTransactionRefernces.contains(refNo)) {

						boolean isBalanceAndMutationNumeric = isNumeric(customerStatements[3])
								&& isNumeric(customerStatements[4]);
						boolean isNumericEndBalance = isNumeric(customerStatements[5]);

						if (!isBalanceAndMutationNumeric) {
							System.out.println(
									"Please correct excel sheet data properly for the Start Balance or Mutation of Reference No= "
											+ refNo);
						} else if (isBalanceAndMutationNumeric) {
							BigDecimal sumOfBalanceAndMutation = new BigDecimal(customerStatements[3])
									.add(new BigDecimal(customerStatements[4]));
							if (isNumericEndBalance) {
								BigDecimal endBalance = new BigDecimal(customerStatements[5]);

								if (!endBalance.equals(sumOfBalanceAndMutation)) {
									System.out.println("[Reference No=" + customerStatements[0] + ", Description="
											+ customerStatements[2] + "] ");
									listOfTransactionRefernces.add(refNo);
								}
							} else {
								System.out.println(
										"Please correct excel sheet data properly for the End Balance of Reference No="
												+ refNo);
							}
						}
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			System.out.println("Please correct excel sheet data properly");
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Please enter the excel sheet data properly for the endbalance with space");
		}

	}

	private static void readXmlFile(String fileName) {
		try {
			Integer refNo = 0;
			List<Integer> listOfTransactionRefernces = new ArrayList<Integer>();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new File(new File("").getAbsolutePath()+"/src/CustomerStatementProcessor/"+ fileName));
			doc.getDocumentElement().normalize();
			NodeList nodeList = doc.getElementsByTagName("record");

			for (int itr = 0; itr < nodeList.getLength(); itr++) {
				Node node = nodeList.item(itr);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) node;

					if ("".equals(eElement.getAttribute("reference").toString())
							|| "".equals(
									eElement.getElementsByTagName("accountNumber").item(0).getTextContent().toString())
							|| "".equals(
									eElement.getElementsByTagName("startBalance").item(0).getTextContent().toString())
							|| "".equals(eElement.getElementsByTagName("mutation").item(0).getTextContent().toString())
							|| "".equals(eElement.getElementsByTagName("description").item(0).getTextContent())
							|| "".equals(eElement.getElementsByTagName("endBalance").item(0).getTextContent())) {
						System.out.println("Please provide valid input in the excel sheet for the fields");
					}

					boolean isReferenceNoNumeric = isNumeric(eElement.getAttribute("reference").toString());

					if ("".equals(eElement.getAttribute("reference").toString())) {
						++refNo;
					} else if (isReferenceNoNumeric && !"".equals(eElement.getAttribute("reference").toString())) {
						refNo = Integer.parseInt(eElement.getAttribute("reference").toString());
					}

					if (!listOfTransactionRefernces.contains(refNo)) {

						boolean isBalanceAndMutationNumeric = isNumeric(
								eElement.getElementsByTagName("startBalance").item(0).getTextContent().toString())
								&& isNumeric(
										eElement.getElementsByTagName("mutation").item(0).getTextContent().toString());

						if (!isBalanceAndMutationNumeric) {
							System.out.println(
									"Please correct excel sheet data properly for the Start Balance or Mutation of Reference No="
											+ eElement.getAttribute("reference").toString());
						} else if (isBalanceAndMutationNumeric) {
							BigDecimal sumOfBalanceAndMutation = new BigDecimal(
									eElement.getElementsByTagName("startBalance").item(0).getTextContent().toString())
											.add(new BigDecimal((eElement.getElementsByTagName("mutation").item(0)
													.getTextContent().toString())));
							boolean isNumericEndBalance = isNumeric(
									eElement.getElementsByTagName("endBalance").item(0).getTextContent());

							if (isNumericEndBalance) {
								BigDecimal endBalance = new BigDecimal(eElement.getElementsByTagName("endBalance")
										.item(0).getTextContent().toString());
								if (!endBalance.equals(sumOfBalanceAndMutation)) {
									System.out.println("[Reference No=" + eElement.getAttribute("reference").toString()
											+ ", Description="
											+ eElement.getElementsByTagName("description").item(0).getTextContent()
											+ "]");
									listOfTransactionRefernces.add(refNo);

								}
							} else {
								System.out.println(
										"Please correct excel sheet data properly for the End Balance of Reference No= "
												+ refNo);
							}
						}
					}
				}
			}
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			System.out.println("Please enter the excel sheet data properly");
		}

	}

	public static boolean isNumeric(String strNum) {
		if (strNum == null || "".equals(strNum)) {
			return false;
		}
		return pattern.matcher(strNum).matches();
	}

}
