import java.util.Scanner;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.regex.Pattern;
import java.util.List;
import java.time.LocalDate;

class Transaction {
    LocalDate date;
    String description;
    double debit;
    double credit;
    double balance;

    public Transaction(LocalDate date, String description, double debit, double credit, double balance) {
        this.date = date;
        this.description = description;
        this.debit = debit;
        this.credit = credit;
        this.balance = balance;
    }

    @Override
    public String toString() {
        return String.format("Date: %s  Description: %-20s  Debit: $%.2f  Credit: $%.2f  Balance: $%.2f",
                date, description, debit, credit, balance);
    }
}

public class Main {
    static final double TRANSACTION_LIMIT = 10000.0;
    static final int DESCRIPTION_LIMIT = 50;

    static HashMap<String, String> loginData = new HashMap<>(); // To store email and password
    static HashMap<String, String> registerData = new HashMap<>(); // To store email and name
    static ArrayList<Transaction> transactions = new ArrayList<>();
    static double balance = 0;
    static double savings = 0;
    static double loan = 0;
    static double savingsPercentage = 0;
    static LocalDate lastTransferDate = LocalDate.now();
    static int loanTerm = 0;


    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.println("== Ledger System ==");

        while (true) {
            System.out.println("Login or Register: ");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.print("> ");

            try {
                int number = input.nextInt();
                input.nextLine();

                switch (number) {
                    case 1:
                        login(input);
                        break;
                    case 2:
                        register(input);
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number (1 or 2).");
                input.nextLine();
            }
        }
    }

    private static void login(Scanner input) {

        System.out.println("\n== Please enter your email and password ==");

        System.out.print("Email: ");
        String email = input.nextLine().toLowerCase(); // Convert email to lowercase
        System.out.print("Password: ");
        String password = input.nextLine();

        if (loginData.containsKey(email) && loginData.get(email).equals(password)) {
            System.out.println("\nLogin Successful!!!");
            dashboard (input, email);
        } else {
            System.out.println("Invalid email or password. If you don't have an account, you should register first.");
            System.out.print("Would you like to register? (Y/N)");
            String choice = input.nextLine().toLowerCase();

            if (choice.equals("y")) {
                register(input); // If user wants to register, prompt for registration
            } else {
                System.out.println("Exiting the program.");
                System.exit(0); // Exit the program if user doesn't want to register
            }
        }
    }

    private static void register(Scanner input) {
        System.out.println("\n== Please fill in the form ==");

        System.out.print("Name: ");
        String name = input.nextLine();
        if (!Pattern.matches("^[a-zA-Z0-9 ]+$", name)) {
            System.out.println("Name must be alphanumeric and cannot contain special characters.");
            return;
        }

        System.out.print("Email: ");
        String email = input.nextLine().toLowerCase();
        if (!Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", email)) {
            System.out.println("Invalid email format.");
            return;
        }

        if (loginData.containsKey(email)) {
            System.out.println("Email already registered.");
            return;
        }

        System.out.print("Password: ");
        String password = input.nextLine();
        if (password.length() < 6 || !password.matches(".*[!@#$%^&*()].*")) {
            System.out.println("Password must be at least 6 characters long and contain at least one special character.");
            return;
        }

        System.out.print("Confirm Password: ");
        String confirmPassword = input.nextLine();
        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match.");
            return;
        }

        loginData.put(email, password);
        registerData.put(email, name);

        System.out.println("\nRegister Successful!!!");
        login(input);
    }

    private static void dashboard(Scanner input, String email) {
        while (true) {
            System.out.printf("== Welcome, %s ==\n", registerData.get(email)); // Print the welcome message with the user's name by formatting the string with their registered name from the HashMap
            System.out.printf("Balance: $%.2f\n", balance);
            System.out.printf("Savings: $%.2f\n", savings);
            System.out.printf("Loan: $%.2f\n", loan);
            System.out.println("\n== Transaction ==");
            System.out.println("1. Debit");
            System.out.println("2. Credit");
            System.out.println("3. History");
            System.out.println("4. Savings");
            System.out.println("5. Credit Loan");
            System.out.println("6. Deposit Interest Predictor");
            System.out.println("7. Logout");

            System.out.print("> ");
            int option = input.nextInt();
            input.nextLine(); // Clears the newline left in the buffer by nextInt() to prevent skipping the next user input

            switch (option) {
                case 1:
                    debit(input);
                    break;
                case 2:
                    credit(input);
                    break;
                case 3:
                    history(input);
                    accountBalance();
                    break;
                case 4:
                    activeSavings(input);
                    break;
                case 5:
                    creditLoan(input);
                    break;
                case 6:
                    depositInterestPredictor(input);
                    break;
                case 7:
                    System.out.println("Thank you for using Ledger System.");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please, try again.");
            }
        }
    }


    private static void debit(Scanner input) {
        System.out.println("== Debit ==");
        System.out.print("Enter amount: ");
        double amount = input.nextDouble();
        input.nextLine();
        System.out.print("Enter description: ");
        String description = input.nextLine();

        if (amount <= 0 || amount > TRANSACTION_LIMIT) {
            System.out.println("Invalid amount. Must be positive and not exceed $" + TRANSACTION_LIMIT);
            return;
        }

        if (description.length() > DESCRIPTION_LIMIT) {
            System.out.println("Description too long. Maximum " + DESCRIPTION_LIMIT + " characters allowed.");
            return;
        }

        if (LocalDate.now().isAfter(LocalDate.now())) {
            System.out.println("Invalid date. Transaction date cannot be in the future.");
            return;
        }

        double savedAmount = (amount * savingsPercentage) / 100;
        balance += (amount - savedAmount);
        savings += savedAmount;
        Transaction txn = new Transaction(LocalDate.now(), description, amount, 0, balance);
        transactions.add(txn);
        transferSavings();
        System.out.println("Debit Successfully Recorded.\n");
    }

    private static void credit(Scanner input) {
        System.out.println("== Credit ==");
        System.out.print("Enter amount: ");
        double amount = input.nextDouble();
        input.nextLine();
        System.out.print("Enter description: ");
        String description = input.nextLine();

        if (amount <= 0 || amount > TRANSACTION_LIMIT) {
            System.out.println("Invalid amount. Must be positive and not exceed $" + TRANSACTION_LIMIT);
            return;
        }
        if (amount > balance) {
            System.out.println("Insufficient balance! You cannot credit more than the available balance.");
            return;
        }
        if (description.length() > DESCRIPTION_LIMIT) {
            System.out.println("Description too long. Maximum " + DESCRIPTION_LIMIT + " characters allowed.");
            return;
        }

        balance -= amount;
        Transaction txn = new Transaction(LocalDate.now(), description, 0, amount, balance);
        transactions.add(txn);
        transferSavings();
        System.out.println("Credit Successfully Recorded.\n");
    }


    private static void transferSavings() {
        if (LocalDate.now().getMonth() != lastTransferDate.getMonth()) {
            balance += savings;
            savings = 0;
            lastTransferDate = LocalDate.now();
            System.out.println("Monthly Savings Transferred to Balance.");
        }
    }

    private static void activeSavings(Scanner input) {
        System.out.print("Are your sure you want to active it? (Y/N): ");
        String response = input.nextLine().toUpperCase();

        if (response.equals("Y")) {
            System.out.print("Enter the percentage you wish to debut from the next debit: ");
            savingsPercentage = input.nextDouble();
            input.nextLine();
            System.out.println("Savings Activated!\n");
        }
    }

    private static void accountBalance() {
        LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);
        double monthlyBalance = 0;

        for (Transaction t : transactions) {
            if (!t.date.isBefore(currentMonth)) {
                monthlyBalance += (t.debit - t.credit);
            }
        }

        System.out.printf("Current Monthly Balance: $%.2f\n", monthlyBalance);
    }

    private static void history(Scanner input) {
        System.out.println("== Transaction History ==");
    }


    private static void depositInterestPredictor(Scanner input) {
        System.out.println("\n== Interest Calculator ==");

        // Prompt for bank and interest rate
        System.out.print("Select bank (RHB/MayBank/HongLeong/Alliance/AmBank/StandardChartered): ");
        String bank = input.nextLine().toLowerCase();

        double interestRate;
        switch (bank) {
            case "rhb":
                interestRate = 2.6;
                break;
            case "maybank":
                interestRate = 2.5;
                break;
            case "hongleong":
                interestRate = 2.3;
                break;
            case "alliance":
                interestRate = 2.85;
                break;
            case "ambank":
                interestRate = 2.55;
                break;
            case "standardchartered":
                interestRate = 2.65;
                break;
            default:
                System.out.println("Invalid bank. Using default rate of 2.5%.");
                interestRate = 2.5;
        }

        System.out.print("Calculate interest for (daily/monthly/annually): ");
        String period = input.nextLine().toLowerCase();


        System.out.print("Enter duration (number of periods): ");
        int duration = input.nextInt();
        input.nextLine();

        double rate = interestRate / 100;
        double totalInterest;

        switch (period) {
            case "daily" -> totalInterest = (balance * rate / 365) * duration;
            case "monthly" -> totalInterest = (balance * rate / 12) * duration;
            case "annually" -> totalInterest = (balance * rate) * duration;
            default -> {
                System.out.println("Invalid period. Calculating monthly by default.");
                totalInterest = (balance * rate / 12) * duration;
            }
        }

        System.out.printf("Estimated Interest over %d %s: $%.2f\n", duration, period, totalInterest);
    }


    private static void creditLoan(Scanner input) {
        System.out.println("\n== Credit Loan ==");

        // Check if the loan is overdue
        if (loan > 0 && LocalDate.now().isAfter(lastTransferDate.plusMonths(loanTerm))) {
            System.out.println("Loan overdue! Debit and credit are blocked until repayment.");
            return;
        }

        System.out.println("1. Apply for Loan");
        System.out.println("2. Repay Loan");
        System.out.print("> ");
        int choice = input.nextInt();
        input.nextLine();

        switch (choice) {
            case 1 -> applyLoan(input);
            case 2 -> repayLoan(input);
            default -> System.out.println("Invalid option. Please try again.");
        }
    }

    private static void applyLoan(Scanner input) {
        System.out.print("Enter loan amount: ");
        double principal = input.nextDouble();
        input.nextLine();

        System.out.print("Enter interest rate (as %): ");
        double interestRate = input.nextDouble() / 100;
        input.nextLine();

        System.out.print("Enter repayment period (in months): ");
        int repaymentPeriod = input.nextInt();
        input.nextLine();

        double totalRepayment = principal + (principal * interestRate * repaymentPeriod / 12);
        double monthlyInstallment = totalRepayment / repaymentPeriod;

        loan += totalRepayment;
        balance += principal;
        loanTerm = repaymentPeriod;
        lastTransferDate = LocalDate.now();

        System.out.printf("Loan of $%.2f approved.\nTotal repayment: $%.2f over %d months.\nMonthly installment: $%.2f\n\n",
                principal, totalRepayment, repaymentPeriod, monthlyInstallment);
    }

    private static void repayLoan(Scanner input) {
        if (loan <= 0) {
            System.out.println("No active loan to repay.");
            return;
        }

        System.out.print("Enter repayment amount: ");
        double repayment = input.nextDouble();
        input.nextLine();

        if (repayment > balance) {
            System.out.println("Insufficient balance for repayment.");
            return;
        }

        loan -= repayment;
        balance -= repayment;

        System.out.printf("Repayment of $%.2f successful.\nRemaining loan: $%.2f\n", repayment, loan);

        if (loan <= 0) {
            System.out.println("Loan fully repaid. Debit and credit unblocked.");
        }
    }
}