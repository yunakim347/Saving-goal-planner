import java.util.Scanner;

public class Main {
    private static final String DATA_FILE = "financeData.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        FinanceManager financeManager = new FinanceManager();
        boolean isRunning = true;

        /*
         * The program keeps showing the menu until the user chooses Exit.
         * Input is validated before each action so the application does not
         * break when the user enters invalid values.
         */
        while (isRunning) {
            printMenu();
            int choice = readInt(scanner, "Enter your choice: ");
            System.out.println();

            switch (choice) {
                case 1:
                    addCategory(scanner, financeManager);
                    break;
                case 2:
                    addTransaction(scanner, financeManager);
                    break;
                case 3:
                    financeManager.displayCategories();
                    break;
                case 4:
                    System.out.println("Total balance: $" + String.format("%.2f", financeManager.getTotalBalance()));
                    break;
                case 5:
                    setSavingGoal(scanner, financeManager);
                    break;
                case 6:
                    financeManager.displaySavingGoals();
                    break;
                case 7:
                    addSubCategory(scanner, financeManager);
                    break;
                case 8:
                    financeManager.saveToFile(DATA_FILE);
                    break;
                case 9:
                    financeManager.loadFromFile(DATA_FILE);
                    break;
                case 10:
                    financeManager.displayRecursiveSummary();
                    break;
                case 11:
                    System.out.println("Exiting program.");
                    isRunning = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

            System.out.println();
        }

        scanner.close();
    }

    /*
     * Displays all available menu options for the user.
     */
    public static void printMenu() {
        System.out.println("===== Saving Goal Planner =====");
        System.out.println("1. Add category");
        System.out.println("2. Add transaction");
        System.out.println("3. View categories");
        System.out.println("4. View total balance");
        System.out.println("5. Set saving goal");
        System.out.println("6. View saving goals");
        System.out.println("7. Add subcategory");
        System.out.println("8. Save to file");
        System.out.println("9. Load from file");
        System.out.println("10. View recursive summary");
        System.out.println("11. Exit");
    }

    public static void addCategory(Scanner scanner, FinanceManager financeManager) {
        String categoryName = readNonEmptyString(scanner, "Enter category name: ");

        if (financeManager.findCategoryByName(categoryName) != null) {
            System.out.println("A category with that name already exists.");
            return;
        }

        financeManager.addCategory(new Category(categoryName));
        System.out.println("Category added successfully.");
    }

    /*
     * Adds a transaction to the category selected by the user.
     * The category is searched first so the transaction can be
     * attached to the correct place in the category tree.
     */
    public static void addTransaction(Scanner scanner, FinanceManager financeManager) {
        String categoryName = readNonEmptyString(scanner, "Enter category name: ");
        Category category = financeManager.findCategoryByName(categoryName);

        if (category == null) {
            System.out.println("Category not found.");
            return;
        }

        String type = readTransactionType(scanner);
        double amount = readDouble(scanner, "Enter amount: ", 0);
        String description = readNonEmptyString(scanner, "Enter description: ");

        category.addTransaction(new Transaction(amount, description, type));
        System.out.println("Transaction added successfully.");
    }

    /*
     * Creates a saving goal and links it to an existing category.
     * This makes the goal more meaningful because it belongs to
     * a specific part of the finance structure.
     */
    public static void setSavingGoal(Scanner scanner, FinanceManager financeManager) {
        String goalName = readNonEmptyString(scanner, "Enter goal name: ");
        double targetAmount = readDouble(scanner, "Enter target amount: ", 0);
        String categoryName = readNonEmptyString(scanner, "Enter category name for this goal: ");

        Category targetCategory = financeManager.findCategoryByName(categoryName);

        if (targetCategory == null) {
            System.out.println("Category not found.");
            return;
        }

        financeManager.addSavingGoal(new SavingGoal(goalName, targetAmount, targetCategory));
        System.out.println("Saving goal added successfully.");
    }

    /*
     * Adds a new subcategory under an existing parent category.
     * This is important because it builds the recursive category tree.
     */
    public static void addSubCategory(Scanner scanner, FinanceManager financeManager) {
        String parentName = readNonEmptyString(scanner, "Enter parent category name: ");
        Category parentCategory = financeManager.findCategoryByName(parentName);

        if (parentCategory == null) {
            System.out.println("Parent category not found.");
            return;
        }

        String subCategoryName = readNonEmptyString(scanner, "Enter subcategory name: ");

        if (parentCategory.findSubCategoryByName(subCategoryName) != null) {
            System.out.println("A subcategory with that name already exists in this category tree.");
            return;
        }

        parentCategory.addSubCategory(new Category(subCategoryName));
        System.out.println("Subcategory added successfully.");
    }

    private static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);
            }
            catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private static double readDouble(Scanner scanner, String prompt, double minimum) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                double value = Double.parseDouble(input);
                if (value > minimum) {
                    return value;
                }
                System.out.println("Please enter a number greater than " + minimum + ".");
            }
            catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static String readNonEmptyString(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (!input.isEmpty()) {
                return input;
            }

            System.out.println("Input cannot be empty.");
        }
    }

    private static String readTransactionType(Scanner scanner) {
        while (true) {
            System.out.print("Enter transaction type (income/expense): ");
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals(Transaction.INCOME) || input.equals(Transaction.EXPENSE)) {
                return input;
            }

            System.out.println("Please enter either income or expense.");
        }
    }
}