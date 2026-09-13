public class Transaction {
    public static final String INCOME = "income";
    public static final String EXPENSE = "expense";

    private final double amount;
    private final String description;
    private final String type;

    /*
     * A transaction stores one money movement.
     * The type is normalised to either "income" or "expense".
     */
    public Transaction(double amount, String description, String type) {
        String normalisedType = type == null ? "" : type.trim().toLowerCase();

        if (!normalisedType.equals(INCOME) && !normalisedType.equals(EXPENSE)) {
            throw new IllegalArgumentException("Transaction type must be income or expense.");
        }

        this.amount = amount;
        this.description = description == null ? "" : description.trim();
        this.type = normalisedType;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    /*
     * Displays transaction details in a readable format
     * for the terminal-based application.
     */
    public void displayTransaction() {
        System.out.println(
            "Type: " + type +
            ", Amount: $" + String.format("%.2f", amount) +
            ", Description: " + description
        );
    }
}