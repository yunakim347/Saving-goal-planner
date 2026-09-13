public class SavingGoal {
    private final String goalName;
    private final double targetAmount;
    private final Category targetCategory;

    /*
     * A saving goal stores the goal name, target amount,
     * and the category it is linked to.
     */
    public SavingGoal(String goalName, double targetAmount, Category targetCategory) {
        String cleanedGoalName = goalName == null ? "" : goalName.trim();

        if (cleanedGoalName.isEmpty()) {
            throw new IllegalArgumentException("Goal name cannot be empty.");
        }
        if (targetAmount <= 0) {
            throw new IllegalArgumentException("Target amount must be greater than 0.");
        }
        if (targetCategory == null) {
            throw new IllegalArgumentException("Target category cannot be null.");
        }

        this.goalName = cleanedGoalName;
        this.targetAmount = targetAmount;
        this.targetCategory = targetCategory;
    }

    public String getGoalName() {
        return goalName;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public Category getTargetCategory() {
        return targetCategory;
    }

    /*
     * Returns the current saved amount by using the linked
     * category balance.
     */
    public double getCurrentSavedAmount() {
        return targetCategory.getCategoryBalance();
    }

    /*
     * Calculates progress as a percentage of the target amount.
     */
    public double getProgressPercentage() {
        return (getCurrentSavedAmount() / targetAmount) * 100;
    }

    /*
     * Checks whether the current saved amount has reached
     * or passed the target amount.
     */
    public boolean isAchieved() {
        return getCurrentSavedAmount() >= targetAmount;
    }

    /*
     * Displays the saving goal together with its current progress.
     */
    public void displayGoal() {
        double currentSaved = getCurrentSavedAmount();
        double progress = getProgressPercentage();
        String status = isAchieved() ? "Achieved" : "In Progress";

        System.out.println("----- Saving Goal: " + goalName + " -----");
        System.out.println("Category : " + targetCategory.getName());
        System.out.println("Saved    : $" + String.format("%.2f", currentSaved)
                + " / $" + String.format("%.2f", targetAmount));
        System.out.println("Progress : " + String.format("%.2f", progress) + "%");
        System.out.println("Status   : " + status);
    }
}