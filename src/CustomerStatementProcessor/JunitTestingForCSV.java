package CustomerStatementProcessor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

@RunWith(value = Parameterized.class)
public class JunitTestingForCSV {

	private String referencenumber;
	private String accountnumber;
	private String description;
	private String startbalance;
	private String mutation;
	private String endbalance;

	public JunitTestingForCSV(String referencenumber, String accountnumber, String description, String startbalance,
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
	public static Collection testCSV() throws IOException {
		return fetchDataFromCsv("C:/Basha/records.csv");
	}

	public static Collection<String[]> fetchDataFromCsv(String csvfileName) throws IOException {
		List<String[]> listOfcustomerStatements = new ArrayList<String[]>();
		String record;
		BufferedReader bufferedReader = new BufferedReader(new FileReader(csvfileName));
		bufferedReader.readLine();
		while ((record = bufferedReader.readLine()) != null) {
			String fields[] = record.split(",");

			listOfcustomerStatements.add(fields);
		}
		bufferedReader.close();
		return listOfcustomerStatements;
	}

	@Test
	public void testForCSVFile() throws Exception {

		JunitTestingForCSV test = new JunitTestingForCSV(referencenumber, accountnumber, description, startbalance,
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
			System.out.println("Please enter mutation");
			assertFalse("mutation", true);
		} else if ("".equals(endbalance)) {
			System.out.println("Please enter endbalance");
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
		if(!isNumericEndBalance){
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
