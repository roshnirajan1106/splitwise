import org.example.models.Balance.BalanceSheet;
import org.example.models.ExpenseMap;
import org.example.models.SplitType;
import org.example.service.SplitWiseService;
import org.junit.Assert;
import org.junit.Test;
import java.util.Map;

public class ExpenseTest {


    private SplitWiseService splitWiseService;
    public ExpenseTest(){
        splitWiseService = SplitWiseService.getInstance();
        splitWiseService.addExpense("trip","exp1",1000,"A","123", SplitType.EQUAL,getExpenseMapForEqual());
        splitWiseService.addExpense("food","exp2",120.0,"B","123", SplitType.EXACT,getExpenseMapForExact());
        splitWiseService.addExpense("travel","exp3",1000.0,"C","123", SplitType.PERCENTAGE,getExpenseMapForPercentage());

    }
    private ExpenseMap getExpenseMapForExact(){

        ExpenseMap expenseMap = new ExpenseMap();
        expenseMap.addExpense("A",90D);
        expenseMap.addExpense("B",20D);
        expenseMap.addExpense("C",10D);
        return expenseMap;
    }

    private ExpenseMap getExpenseMapForPercentage(){
        ExpenseMap expenseMap = new ExpenseMap();
        expenseMap.addExpense("A",30D);
        expenseMap.addExpense("B",30D);
        expenseMap.addExpense("C",40D);
        return expenseMap;
    }

    private ExpenseMap getExpenseMapForEqual(){
        ExpenseMap expenseMap = new ExpenseMap();
        expenseMap.addExpense("A",null);
        expenseMap.addExpense("B",null);
        expenseMap.addExpense("C",null);
        return expenseMap;
    }
    @Test
    public void createUser(){
        splitWiseService.createUser("D","random");
        splitWiseService.createUser("E","random");
        Assert.assertEquals(5, splitWiseService.getUsers().size());
    }

//    @Test
//    public void balanceTest(){
//        BalanceSheet balanceSheet = splitWiseService.getBalanceSheet("123");
//        Assert.assertEquals(276.666666667, balanceSheet.get("123").getExpenseMap().get("A"),1e-6);
//        Assert.assertEquals(-533.33333333, resGroupBalance.get("123").getExpenseMap().get("B"),1e-6);
//        Assert.assertEquals(256.666666666, resGroupBalance.get("123").getExpenseMap().get("C"),1e-6);
//
//    }
    @Test
    public void printInvoiceTest(){
        splitWiseService.printInvoice("123");
    }

    @Test
    public void settleBalance(){
        splitWiseService.settleBalance("B","C",100.0,"123");
        printInvoiceTest();
    }


}
