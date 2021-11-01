import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;


public class DebtTracker {
		
	public void trackDebt(int userSelection, Scanner scan) {
		
		int i;
		String grb = scan.nextLine();
		
		String userEnters = "2";	
		String[] creditValues = new String[100]; //!!Store data from user; may move to new class later and then SQL server!!
		String[] loanValues = new String[100];
		int count1 = 0, count2 = 0;
		
		//Credit card balances
		System.out.println("Let's get started with your credit card balances");
		System.out.println("Enter your credit card name and the balance owed below without a dollar sign. You can enter as many as you'd like, and when you are done type Next to continue");
		
		for (i = 0; i < creditValues.length; i++) {
			
			userEnters = scan.nextLine();
			
			if(userEnters.equalsIgnoreCase("next")){
				
				break;
				
			}
			else {
				
				creditValues[i] = userEnters;
				count1 += 1;
			}
			
			
		}
		
		// Loan balances	
		System.out.println("Now for any loans:");
		
		for (i = 0; i < loanValues.length; i++) {
			
			userEnters = scan.nextLine();
			
			if(userEnters.equalsIgnoreCase("next")){
				
				break;
				
			}
			else {
				
				loanValues[i] = userEnters;
				count2 += 1;
			}
			
		}
		
		//Saving entries
		System.out.println("Ok, give me a moment while I process your entries");
		
		newEntry(creditValues, count1, loanValues, count2);
		
		scan.close();
	}	
	
	//Send to storage 
	public void newEntry(String[] cred, int credCount, String[] loan, int loanCount) {
		
		String temp = "";
		double creditBalance = 0;
		double loanBalance = 0;
		double offset = 0;
		
		
		try {
			
			int i = 0; //Increment cred
			int j = 0; //Increment loan
			String credWord = ""; //Save word from cred to be assigned to SQL cred_name
			String loanWord = ""; //Save word from loan to be assigned to SQL loan_name
			int id = 1; //To make sure each component starts at 1 for primary key
			//double avg = ((double)credCount + (double)loanCount) / 2.0;
			double totalCredit; //Send balance to showProgress()
			double totalLoan; //Send balance to showProgress()
			
			
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Maurice", "root", "Calmb4storm!");
			
			Statement myState = conn.createStatement();
			
			//For cred name 
			while (i < credCount) { 
				
				credWord = cred[i];
				credWord = credWord.substring(0, credWord.indexOf(" "));
				
				System.out.println(credWord + " ");

				myState.executeUpdate("INSERT INTO DebtInfo (debt_id, credit_name) VALUES("+id+", '"+credWord+"')");

				id++;
				i++;
			}
			
			id = 1;
			
			//For loan name
			while (j < loanCount) {
				
				loanWord = loan[j];
				loanWord = loanWord.substring(0, loanWord.indexOf(" "));
				
				System.out.println(loanWord + " ");
				
				myState.executeUpdate("UPDATE DebtInfo SET loan_name = '"+loanWord+"' WHERE debt_id = "+id);

				id++;
				j++;
			}
			
			id = 1; //Reset primary key for next component
			
			//Extract # from credit array 
			for(i = 0; i < credCount; i++) {
				temp = cred[i];
				temp = temp.substring(temp.lastIndexOf(' ') + 1);
				offset = Double.parseDouble(temp);
				creditBalance = creditBalance + offset;
				
				myState.executeUpdate("UPDATE DebtInfo SET credit_value = "+temp+" WHERE debt_id = "+id);

				id++;
				
			}
			
			System.out.printf("Total credit card balance: $%.2f\n", creditBalance);
			
			id = 1; //Reset primary key for next component

			//Extract # from loan array
			for(i = 0; i < loanCount; i++) {
				temp = loan[i];
				temp = temp.substring(temp.lastIndexOf(' ') + 1);
				offset = Double.parseDouble(temp);
				loanBalance = loanBalance + offset;	
					
				myState.executeUpdate("UPDATE DebtInfo SET loan_value = "+temp+" WHERE debt_id = "+id);
				
				id ++;

			}
			
			System.out.printf("Total loan balance: $%.2f\n", loanBalance);
			
			
			
		}
		
		catch (Exception excep) {
			excep.printStackTrace();
		}

	}
	
	//Make payments
	public static void makePayment (Scanner scan, String userInput, double totalLoan, double totalCredit) {
		
		double progress = 0.0;
		int pay = 0;
		String grb;
		userInput = "";
		grb = scan.nextLine();//Prevent nextLine & nextInt interaction
		System.out.println("How much would you like to pay?");
		pay = scan.nextInt();
		grb = scan.nextLine();//Prevent nextLine & nextInt interaction
		System.out.println("Got it, and will this be towards a loan or credit card?");
		userInput = scan.nextLine();
		
		if(userInput.equalsIgnoreCase("loan")) {
			progress = totalLoan - pay;
			System.out.println("Here is your current loan balance: $" + progress);
		}
		else {
			progress = totalCredit - pay;
			System.out.println("Here is your current credit balance: $" + progress);
		}
		
	}

	//Show payoff progress
	public void showProgress(Scanner scan, Connection c, Statement s, ResultSet rs) {
		double loanProgress; 
		double creditProgress;	
		String makePayments;
		int i; 
		int k;
		double offset1 = 0.0;
		double offset2 = 0.0;
		double total1 = 0.0;
		double total2 = 0.0;
		double totalCredit = 0.0;
		double totalLoan = 0.0;
		String select = "SELECT COUNT(*) AS 'count' FROM DebtInfo";
		int count = 0;
		
		try {
			
			rs = s.executeQuery(select);
			
			for (int t = 1; t <= 1; t++) {
				rs = s.executeQuery("SELECT credit_value AS 'cv' FROM DebtInfo");
				while(rs.next()) {
					offset1 = rs.getDouble("cv");
					total1 = offset1 + total1;
					totalCredit = total1;
					System.out.println(totalCredit);//Add $ and 2 deci
					
				}
			}
			for (int l = 1; l <= 1; l++) {
		
					rs = s.executeQuery("SELECT loan_value AS 'lv' FROM DebtInfo");
				while(rs.next()) {
					offset2 = rs.getDouble("lv");
					total2 = offset2 + total2;
					totalLoan = total2;
					System.out.println(totalLoan); //Add $ and 2 deci
				
				}
			}
			
			
				
		
			
			rs.close();
			s.close();
			c.close();
			
//			while(rs.next()) {
//				
//				select = "SELECT credit_value AS 'cv' FROM DebtInfo";
//				rs = s.executeQuery(select);
//				offset = rs.getDouble("cv");
//				total = offset + total;
//				totalCredit = total;
//				System.out.println(totalCredit);
//				rs.next();
//			}
				// Sum credit amounts
//			if (rs.next()) {
//				for (i = 1; i <= count; i++) {
//				
//					select = "SELECT credit_value AS 'cv' FROM DebtInfo";
//					rs = s.executeQuery(select);
//					offset = rs.getDouble("cv");
//					total = offset + total;
//					totalCredit = total;
//					System.out.println(total);
//					rs.next();
//				}
//				// Sum loan amounts
//				for (k = 1; k <= count; k++) {
//					
//					select = "SELECT loan_value AS 'lv' FROM DebtInfo";
//					rs = s.executeQuery(select);
//					offset = rs.getDouble("lv");
//					total = offset + total;
//					totalLoan = total;
//					System.out.println(total);
//					rs.next();
//				}
//			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		
		System.out.println("Here are your balances: ");
		System.out.println("	Loan: " + totalLoan);
		System.out.println("	Credit card: " + totalCredit);
		System.out.println("Would you like to make payments?");
		makePayments = scan.nextLine();
		
		if(makePayments.equalsIgnoreCase("yes")) {
			makePayment(scan, makePayments, totalLoan, totalCredit);
			
		}
		
	}
	
	//Driver   
	public static void main(String[] args) {
		
		DebtTracker dt = new DebtTracker();
		StoreDebtInfo sd = new StoreDebtInfo();
		
		double creditTotal;
		double loanTotal;
		int userNum;
		creditTotal = sd.getTotalCredit();
		loanTotal = sd.getTotalLoan();
		
		Scanner scnr = new Scanner(System.in);
		Connection conn = null; 
		Statement state = null;
		ResultSet rs = null;

		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Maurice", "root", "Calmb4storm!");
			state = conn.createStatement();
			rs = state.executeQuery(null);

		}
		catch(Exception e) {
			
		}
		
		//User prompt
		System.out.println("What would you like to do today?");
		System.out.println("	1. Track debt");
		System.out.println("	2. Show debt removal progress");
		userNum = scnr.nextInt();
		
		//Branch to decide which function to call
			
		if (userNum == 1) {
			
			//Branch to track debt and options associated 
			System.out.println("Please select an option below:");
			System.out.println("	1. Start new entry");
			System.out.println("	2. Transfer from someplace else");	//!!Must add transfer!!
			userNum = scnr.nextInt();
			
			if (userNum == 1){
				//Call trackDebt function
				dt.trackDebt(userNum, scnr);
			}
			
			else {
				System.out.println("add transfer");
			}
			
			
		}
		
		//Branch to show progress
		else if (userNum == 2) {
			
			//Show progress
			dt.showProgress(scnr, conn, state, rs);
			//Ask if want to make payment before showing progress 
			dt.makePayment(scnr, "", loanTotal, creditTotal);
			
		}
		
		//Repeat until user makes correct entry
		else {
			
			while (userNum != 1 && userNum != 2) {
			
			System.out.println("Please enter that again");
			userNum = scnr.nextInt();
			
			}
			
			if (userNum == 1) {   
				
				//Branch to track debt and options associated 
				System.out.println("Please select an option below:");
				System.out.println("	1. Start new entry");
				System.out.println("	2. Transfer from someplace else");
				userNum = scnr.nextInt();
				      
				if (userNum == 1) {
					
					//Call trackDebt function
					dt.trackDebt(userNum, scnr);
					
				}
				else {
					
					//Show transfer process again
					System.out.println("add transfer");

				}
			}
			
			else {
				//Show progress again
				dt.showProgress(scnr, conn, state, rs);
				//Ask if want to make payment before showing progress 
				dt.makePayment(scnr, "", loanTotal, creditTotal);
			
			}
			
		}
		
		scnr.close();

	}
}
