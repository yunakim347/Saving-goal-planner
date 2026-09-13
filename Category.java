import java.util.ArrayList;

public class Category {
    private final String name;
    private final ArrayList<Transaction> transactions;
    private final ArrayList<Category> subCategories;

    /*
     * A category stores its own transactions and can also store
     * nested subcategories. This creates a recursive data structure
     * because a Category contains other Category objects.
     */
    public Category(String name) {
        String cleanedName = name == null ? "" : name.trim();

        if (cleanedName.isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty.");
        }

        this.name = cleanedName;
        this.transactions = new ArrayList<>();
        this.subCategories = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void addTransaction(Transaction transaction) {
        if (transaction != null) {
            transactions.add(transaction);
        }
    }

    public void addSubCategory(Category subCategory) {
        if (subCategory == null || subCategory == this) {
            return;
        }

        for (Category existingCategory : subCategories) {
            if (existingCategory.getName().equalsIgnoreCase(subCategory.getName())) {
                return;
            }
        }

        subCategories.add(subCategory);
    }

    public ArrayList<Category> getSubCategories() {
        return subCategories;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    /*
     * Calculates only the balance of this category's own transactions.
     * Income adds to the total, while expense subtracts from the total.
     */
    public double getDirectBalance() {
        double total = 0;

        for (Transaction transaction : transactions) {
            if (transaction.getType().equals(Transaction.INCOME)) {
                total += transaction.getAmount();
            }
            else if (transaction.getType().equals(Transaction.EXPENSE)) {
                total -= transaction.getAmount();
            }
        }

        return total;
    }

    /*
     * Recursively calculates the total balance for this category tree.
     * It starts with this category's direct balance, then adds the balances
     * of all nested subcategories.
     */
    public double getCategoryBalance() {
        double total = getDirectBalance();

        for (Category subCategory : subCategories) {
            total += subCategory.getCategoryBalance();
        }

        return total;
    }

    /*
     * Recursively searches through this category and all nested subcategories.
     * If the current category name matches, return it.
     * Otherwise, keep searching deeper until a match is found or nothing is left.
     */
    public Category findSubCategoryByName(String categoryName) {
        if (categoryName == null) {
            return null;
        }

        if (name.equalsIgnoreCase(categoryName.trim())) {
            return this;
        }

        for (Category subCategory : subCategories) {
            Category foundCategory = subCategory.findSubCategoryByName(categoryName);
            if (foundCategory != null) {
                return foundCategory;
            }
        }

        return null;
    }

    /*
     * Counts all nested subcategories below the current category.
     */
    public int countAllSubCategories() {
        int total = subCategories.size();

        for (Category subCategory : subCategories) {
            total += subCategory.countAllSubCategories();
        }

        return total;
    }

    /*
     * Counts all transactions in this category tree, including
     * transactions stored in nested subcategories.
     */
    public int countAllTransactions() {
        int total = transactions.size();

        for (Category subCategory : subCategories) {
            total += subCategory.countAllTransactions();
        }

        return total;
    }

    /*
     * Displays the whole category hierarchy in an indented tree structure.
     */
    public void displayCategoryHierarchy(String indent) {
        double directBalance = getDirectBalance();
        double totalBalance = getCategoryBalance();
        int nestedSubcategories = countAllSubCategories();
        int nestedTransactions = countAllTransactions();

        System.out.println(indent + "Category: " + name);
        System.out.println(indent + "Balance: $" + String.format("%.2f", totalBalance)
                + " | Direct: $" + String.format("%.2f", directBalance));
        System.out.println(indent + "Subcategories: " + nestedSubcategories
                + " | Transactions: " + nestedTransactions);

        if (!transactions.isEmpty()) {
            System.out.println(indent + "Transactions:");
            for (Transaction transaction : transactions) {
                System.out.print(indent + "- ");
                transaction.displayTransaction();
            }
        }

        for (Category subCategory : subCategories) {
            System.out.println();
            subCategory.displayCategoryHierarchy(indent + "    ");
        }
    }
}