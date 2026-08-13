package parameterized.test;

public class LoanResponse {

	private boolean approved;
	private String msg="null";

	public void setApproved(boolean b) {
		approved = b;
	}

	public boolean isApproved() {
		return approved;
	}

	public void setMessage(String string) {
		msg = string;
	}

	public String getMessage() {
		return msg;
	}

}
