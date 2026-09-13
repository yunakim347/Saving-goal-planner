import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.io.File;

public class CategoryTest {

    // Tests balance calculation using only transactions in one category.
    @Test
    public void testDirectBalance() {
        Category food = new Category("Food");
        food.addTransaction(new Transaction(100, "salary", "income"));
        food.addTransaction(new Transaction(30, "lunch", "expense"));

        assertEquals(70.0, food.getCategoryBalance(), 0.001);
    }

    // Tests recursive balance calculation through nested subcategories.
    @Test
    public void testRecursiveBalance() {
        Category savings = new Category("Savings");
        Category travel = new Category("Travel");
        Category flights = new Category("Flights");

        flights.addTransaction(new Transaction(200, "deposit", "income"));
        flights.addTransaction(new Transaction(50, "ticket", "expense"));
        travel.addSubCategory(flights);
        savings.addSubCategory(travel);

        assertEquals(150.0, savings.getCategoryBalance(), 0.001);
    }

    // Tests recursive search for a deeply nested subcategory.
    @Test
    public void testFindSubCategoryByName() {
        Category savings = new Category("Savings");
        Category travel = new Category("Travel");
        Category flights = new Category("Flights");

        travel.addSubCategory(flights);
        savings.addSubCategory(travel);

        Category result = savings.findSubCategoryByName("Flights");

        assertNotNull(result);
        assertEquals("Flights", result.getName());
    }

    // Tests that a category with no transactions returns a zero balance.
    @Test
    public void testEmptyCategoryBalance() {
        Category empty = new Category("Empty");
        assertEquals(0.0, empty.getCategoryBalance(), 0.001);
    }

    // Tests total balance across multiple top-level categories.
    @Test
    public void testTotalBalanceInFinanceManager() {
        FinanceManager manager = new FinanceManager();

        Category food = new Category("Food");
        food.addTransaction(new Transaction(100, "salary", "income"));
        food.addTransaction(new Transaction(40, "dinner", "expense"));

        Category savings = new Category("Savings");
        Category travel = new Category("Travel");
        travel.addTransaction(new Transaction(300, "deposit", "income"));
        savings.addSubCategory(travel);

        manager.addCategory(food);
        manager.addCategory(savings);

        assertEquals(360.0, manager.getTotalBalance(), 0.001);
    }

    // Tests the edge case where a category name does not exist.
    @Test
    public void testFindCategoryByNameNotFound() {
        FinanceManager manager = new FinanceManager();
        manager.addCategory(new Category("Food"));

        Category result = manager.findCategoryByName("Travel");

        assertNull(result);
    }

    // Tests saving goal progress calculation and achievement status.
    @Test
    public void testSavingGoalProgress() {
        Category travel = new Category("Travel");
        travel.addTransaction(new Transaction(300, "deposit", "income"));

        SavingGoal goal = new SavingGoal("Trip Fund", 1000, travel);

        assertEquals(30.0, goal.getProgressPercentage(), 0.001);
        assertFalse(goal.isAchieved());
    }

    // Tests whether saved data can be loaded back correctly.
    @Test
    public void testSaveAndLoad() {
        FinanceManager manager = new FinanceManager();

        Category savings = new Category("Savings");
        Category travel = new Category("Travel");
        savings.addSubCategory(travel);
        travel.addTransaction(new Transaction(300, "deposit", "income"));

        manager.addCategory(savings);
        manager.addSavingGoal(new SavingGoal("Trip Fund", 1000, travel));

        String fileName = "testData.txt";
        manager.saveToFile(fileName);

        FinanceManager loadedManager = new FinanceManager();
        loadedManager.loadFromFile(fileName);

        assertEquals(300.0, loadedManager.getTotalBalance(), 0.001);
        assertNotNull(loadedManager.findCategoryByName("Travel"));

        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
    }
}