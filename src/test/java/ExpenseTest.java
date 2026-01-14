import org.example.models.Balance.BalanceSimplifier;
import org.example.models.ExpenseMap;
import org.example.models.Repository.Repository;
import org.example.models.SplitType;
import org.example.service.SplitWiseService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.*;

public class ExpenseTest {

    private SplitWiseService splitWiseService;

    public ExpenseTest() {
        splitWiseService = SplitWiseService.getInstance();
        //splitWiseService.addExpense("trip", "exp1", 1000, "A", "123", SplitType.EQUAL, getExpenseMapForEqual());

    }

    private ExpenseMap getExpenseMapForExact() {

        ExpenseMap expenseMap = new ExpenseMap();
        expenseMap.addExpense("A", 90D);
        expenseMap.addExpense("B", 20D);
        expenseMap.addExpense("C", 10D);
        return expenseMap;
    }

    private ExpenseMap getExpenseMapForPercentage() {
        ExpenseMap expenseMap = new ExpenseMap();
        expenseMap.addExpense("A", 30D);
        expenseMap.addExpense("B", 30D);
        expenseMap.addExpense("C", 40D);
        return expenseMap;
    }

    private ExpenseMap getExpenseMapForEqual() {
        ExpenseMap expenseMap = new ExpenseMap();
        expenseMap.addExpense("A", 0.0);
        expenseMap.addExpense("B", 0.0);
        expenseMap.addExpense("C", 0.0);
        return expenseMap;
    }

    @Test
    public void createUser() {
        splitWiseService.createUser("D", "random");
        splitWiseService.createUser("E", "random");
        Assert.assertEquals(5, splitWiseService.getUsers().size());
    }

    @Test
    public void printInvoiceTest() {

        splitWiseService.printInvoice("123");
    }

    @Test
    public void settleBalance() {
        splitWiseService.settleBalance("B", "C", 100.0, "123");
        printInvoiceTest();
    }

    @Test
    public void concurrentAddExpense() throws Exception {
        int numberOfThreads = 900;
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        Runnable task = () -> {
            try {
                splitWiseService.addExpense("fun", "exp3", 100, "A", "123", SplitType.EQUAL, getExpenseMapForEqual());
                splitWiseService.addExpense("trip", "exp4", 1000, "B", "123", SplitType.PERCENTAGE, getExpenseMapForPercentage());

            } finally {
                latch.countDown();
            }
        };

        ExecutorService executor = Executors.newFixedThreadPool(100);
        for (int i = 0; i < numberOfThreads; i++) {
            executor.execute(task);
        }

        latch.await();          // guarantees completion
        executor.shutdown();
        printInvoiceTest();     // NOW data will appear
    }


}
