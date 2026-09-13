import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Scanner;

public class FinanceManager {
    private static final String RECORD_CATEGORY = "CATEGORY";
    private static final String RECORD_TRANSACTION = "TRANSACTION";
    private static final String RECORD_GOAL = "GOAL";
    private static final String FIELD_SEPARATOR = "\t";

    private final ArrayList<Category> categories;
    private final ArrayList<SavingGoal> savingGoals;

    /*
     * FinanceManager is responsible for storing and managing
     * all top-level categories and saving goals in the program.
     */
    public FinanceManager() {
        categories = new ArrayList<>();
        savingGoals = new ArrayList<>();
    }

    public void addCategory(Category category) {
        if (category == null) {
            return;
        }

        for (Category existingCategory : categories) {
            if (existingCategory.getName().equalsIgnoreCase(category.getName())) {
                return;
            }
        }

        categories.add(category);
    }

    public void addSavingGoal(SavingGoal savingGoal) {
        if (savingGoal != null) {
            savingGoals.add(savingGoal);
        }
    }

    /*
     * Searches for a category by name across all top-level categories.
     * Each top-level category then performs its own recursive search
     * through the subcategory tree.
     */
    public Category findCategoryByName(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            return null;
        }

        for (Category category : categories) {
            Category foundCategory = category.findSubCategoryByName(categoryName.trim());
            if (foundCategory != null) {
                return foundCategory;
            }
        }

        return null;
    }

    /*
     * Calculates the balance across the whole application by summing
     * the recursive balance of each top-level category.
     */
    public double getTotalBalance() {
        double total = 0;

        for (Category category : categories) {
            total += category.getCategoryBalance();
        }

        return total;
    }

    /*
     * Displays every top-level category together with its full
     * subcategory hierarchy.
     */
    public void displayCategories() {
        if (categories.isEmpty()) {
            System.out.println("No categories found.");
            return;
        }

        for (Category category : categories) {
            category.displayCategoryHierarchy("");
            System.out.println();
        }
    }

    public void displaySavingGoals() {
        if (savingGoals.isEmpty()) {
            System.out.println("No saving goals found.");
            return;
        }

        for (SavingGoal savingGoal : savingGoals) {
            savingGoal.displayGoal();
            System.out.println();
        }
    }

    /*
     * Counts all categories in the program, including nested subcategories.
     * This uses the recursive counting method from each category.
     */
    public int countAllCategories() {
        int total = categories.size();

        for (Category category : categories) {
            total += category.countAllSubCategories();
        }

        return total;
    }

    /*
     * Counts all transactions stored in the whole category tree.
     */
    public int countAllTransactions() {
        int total = 0;

        for (Category category : categories) {
            total += category.countAllTransactions();
        }

        return total;
    }

    /*
     * Displays a summary that makes the recursive structure easier to show:
     * total categories, total transactions, and overall balance.
     */
    public void displayRecursiveSummary() {
        System.out.println("===== Recursive Summary =====");
        System.out.println("Total categories (including nested): " + countAllCategories());
        System.out.println("Total transactions (including nested): " + countAllTransactions());
        System.out.println("Total balance across all categories: $" + String.format("%.2f", getTotalBalance()));
    }

    /*
     * Saves all categories, subcategories, transactions, and saving goals
     * into a text file.
     *
     * Category IDs are written to the file so categories with the same name
     * can still be reconnected correctly during loading.
     */
    public void saveToFile(String fileName) {
        IdentityHashMap<Category, Integer> categoryIds = new IdentityHashMap<>();
        int[] nextId = {1};

        try (PrintWriter writer = new PrintWriter(fileName)) {
            for (Category category : categories) {
                saveCategory(writer, category, 0, categoryIds, nextId);
            }

            for (SavingGoal savingGoal : savingGoals) {
                Integer categoryId = categoryIds.get(savingGoal.getTargetCategory());
                if (categoryId != null) {
                    writer.println(buildRecord(
                        RECORD_GOAL,
                        escapeField(savingGoal.getGoalName()),
                        String.valueOf(savingGoal.getTargetAmount()),
                        String.valueOf(categoryId)
                    ));
                }
            }

            System.out.println("Data saved successfully.");
        }
        catch (FileNotFoundException e) {
            System.out.println("Error saving file.");
        }
    }

    /*
     * Helper method used by saveToFile(). Each category is assigned an ID
     * and saved recursively so nested categories can be restored accurately.
     */
    private void saveCategory(
        PrintWriter writer,
        Category category,
        int parentId,
        IdentityHashMap<Category, Integer> categoryIds,
        int[] nextId
    ) {
        int categoryId = nextId[0]++;
        categoryIds.put(category, categoryId);

        writer.println(buildRecord(
            RECORD_CATEGORY,
            String.valueOf(categoryId),
            escapeField(category.getName()),
            String.valueOf(parentId)
        ));

        for (Transaction transaction : category.getTransactions()) {
            writer.println(buildRecord(
                RECORD_TRANSACTION,
                String.valueOf(categoryId),
                escapeField(transaction.getType()),
                String.valueOf(transaction.getAmount()),
                escapeField(transaction.getDescription())
            ));
        }

        for (Category subCategory : category.getSubCategories()) {
            saveCategory(writer, subCategory, categoryId, categoryIds, nextId);
        }
    }

    /*
     * Loads previously saved data from a text file.
     *
     * The file is read in stages:
     * 1. Rebuild categories and subcategory structure.
     * 2. Add transactions to the correct category.
     * 3. Rebuild saving goals and reconnect them to categories.
     */
    public void loadFromFile(String fileName) {
        categories.clear();
        savingGoals.clear();

        Map<Integer, Category> categoriesById = new HashMap<>();
        ArrayList<String[]> transactionData = new ArrayList<>();
        ArrayList<String[]> goalData = new ArrayList<>();

        try (Scanner fileScanner = new Scanner(new File(fileName))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split(FIELD_SEPARATOR, -1);

                if (parts.length == 0) {
                    continue;
                }

                String recordType = parts[0];

                if (recordType.equals(RECORD_CATEGORY) && parts.length >= 4) {
                    int categoryId = Integer.parseInt(parts[1]);
                    String categoryName = unescapeField(parts[2]);
                    int parentId = Integer.parseInt(parts[3]);

                    Category category = new Category(categoryName);
                    categoriesById.put(categoryId, category);

                    if (parentId == 0) {
                        categories.add(category);
                    }
                    else {
                        Category parentCategory = categoriesById.get(parentId);
                        if (parentCategory != null) {
                            parentCategory.addSubCategory(category);
                        }
                    }
                }
                else if (recordType.equals(RECORD_TRANSACTION) && parts.length >= 5) {
                    transactionData.add(parts);
                }
                else if (recordType.equals(RECORD_GOAL) && parts.length >= 4) {
                    goalData.add(parts);
                }
            }

            for (String[] parts : transactionData) {
                int categoryId = Integer.parseInt(parts[1]);
                String type = unescapeField(parts[2]);
                double amount = Double.parseDouble(parts[3]);
                String description = unescapeField(parts[4]);

                Category category = categoriesById.get(categoryId);
                if (category != null) {
                    category.addTransaction(new Transaction(amount, description, type));
                }
            }

            for (String[] parts : goalData) {
                String goalName = unescapeField(parts[1]);
                double targetAmount = Double.parseDouble(parts[2]);
                int categoryId = Integer.parseInt(parts[3]);

                Category targetCategory = categoriesById.get(categoryId);
                if (targetCategory != null) {
                    savingGoals.add(new SavingGoal(goalName, targetAmount, targetCategory));
                }
            }

            System.out.println("Data loaded successfully.");
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
        catch (IllegalArgumentException e) {
            System.out.println("The saved file contains invalid data.");
        }
    }

    private String buildRecord(String... fields) {
        return String.join(FIELD_SEPARATOR, fields);
    }

    private String escapeField(String value) {
        String safeValue = value == null ? "" : value;
        return safeValue
            .replace("\\", "\\\\")
            .replace("\t", "\\t")
            .replace("\n", "\\n")
            .replace("\r", "\\r");
    }

    private String unescapeField(String value) {
        StringBuilder result = new StringBuilder();
        boolean escaping = false;

        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);

            if (escaping) {
                if (current == 't') {
                    result.append('\t');
                }
                else if (current == 'n') {
                    result.append('\n');
                }
                else if (current == 'r') {
                    result.append('\r');
                }
                else {
                    result.append(current);
                }
                escaping = false;
            }
            else if (current == '\\') {
                escaping = true;
            }
            else {
                result.append(current);
            }
        }

        if (escaping) {
            result.append('\\');
        }

        return result.toString();
    }
}