package parameterized.test;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		System.out.println("Hello World!");
	}

	public LoanResponse requestLoan(float loanAmount, float downPayment, float availableFunds) {
		LoanResponse response = new LoanResponse();

		if (availableFunds < downPayment) {
			response.setApproved(false);
			response.setMessage("error.insufficient.funds.for.down.payment");
		} 
		else if (downPayment / loanAmount < 0.1) {
			response.setApproved(false);
			response.setMessage("error.insufficient.down.payment");
		} 
		else {
			response.setApproved(true);
		}

		return response;
	}
}
