package parameterized.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class AppTest {

	float loanAmount;
	float downPayment;
	float availableFunds;
	boolean expectApproved;
	String expectedMessage;

	public AppTest(float loanAmount, float downPayment, float availableFunds, boolean expectApproved,
			String expectedMessage) {
		this.loanAmount = loanAmount;
		this.downPayment = downPayment;
		this.availableFunds = availableFunds;
		this.expectApproved = expectApproved;
		this.expectedMessage = expectedMessage;
	}

	// @Parameters(name = "Run {index}: loanAmount={0}, downPayment={1},
	// availableFunds={2}, expectApproved={3}, expectedMessage={4}")
	// public static Iterable<Object[]> data() throws Throwable
	// {
	// return Arrays.asList(new Object[][] {
	// { 1000.0f, 200.0f, 250.0f, true, null },
	// { 1000.0f, 200.0f, 250.0f, true, null },
	// { 1000.0f, 50.0f, 250.0f, false, "error.insufficient.down.payment" },
	// { 1000.0f, 200.0f, 150.0f, false,
	// "error.insufficient.funds.for.down.payment" }
	//
	// });
	// }

	@Parameters
	public static Collection<Object[]> getParameters() {
		Collection<Object[]> retList = new ArrayList<Object[]>();
		try {
			Scanner in = new Scanner(new File("dataFile.txt"));

			// Read as many lines as there are in the file
			while (in.hasNextLine()) {
				String l = in.nextLine();
				
				// split the line using delimiter and then create the test-case object
				String dataArray[] = l.split(",");
				Object[] d = new Object[5];
				d[0] = Float.parseFloat(dataArray[0]);
				d[1] = Float.parseFloat(dataArray[1]);
				d[2] = Float.parseFloat(dataArray[2]);
				d[3] = Boolean.parseBoolean(dataArray[3]);
				d[4] = dataArray[4];

				// add the test data into the arraylist
				retList.add(d);
				
			} // end of while
			in.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		
		// return all the test-cases
		return retList;
	}

	// Checkout https://www.baeldung.com/junit-params for more ways to parameterized test
	@Test  // run this for each test-case in the above collection
	public void testRequestLoan() throws Throwable {
		// Given
		App underTest = new App();

		// When
		LoanResponse result = underTest.requestLoan(loanAmount, downPayment, availableFunds);

		// Then
		assertNotNull(result);
		assertEquals(expectApproved, result.isApproved());
		assertEquals(expectedMessage, result.getMessage());
	}
}
