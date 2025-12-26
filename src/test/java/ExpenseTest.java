import org.example.models.BalanceSheet;
import org.example.models.ExpenseMap;
import org.example.models.SplitType;
import org.example.service.SplitwiseService;
import org.junit.Assert;
import org.junit.Test;
import java.util.Map;

public class ExpenseTest {

    private SplitwiseService splitwiseService;
    private BalanceSheet balanceSheet;
    private ExpenseMap getExpenseMapForExact(){
        ExpenseMap expenseMap = new ExpenseMap();
        expenseMap.put("A",90D);
        expenseMap.put("B",20D);
        expenseMap.put("C",10D);
        return expenseMap;
    }
    private ExpenseMap getExpenseMapForPercentage(){
        ExpenseMap expenseMap = new ExpenseMap();
        expenseMap.put("A",30D);
        expenseMap.put("B",30D);
        expenseMap.put("C",40D);
        return expenseMap;
    }

    private ExpenseMap getExpenseMapForEqual(){
        ExpenseMap expenseMap = new ExpenseMap();
        expenseMap.put("A",null);
        expenseMap.put("B",null);
        expenseMap.put("C",null);
        return expenseMap;
    }

    public ExpenseTest(){
        splitwiseService = SplitwiseService.getInstance();
        balanceSheet = new BalanceSheet();
        splitwiseService.addExpense("trip","exp1",1000,"A","123", SplitType.EQUAL,getExpenseMapForEqual());
        splitwiseService.addExpense("food","exp2",120.0,"B","123", SplitType.EXACT,getExpenseMapForExact());
        splitwiseService.addExpense("travel","exp3",1000.0,"C","123", SplitType.PERCENTAGE,getExpenseMapForPercentage());
    }
    @Test
    public void createUser(){
        splitwiseService = SplitwiseService.getInstance();
        splitwiseService.createUser("D","random");
        splitwiseService.createUser("E","random");
        Assert.assertEquals(5, splitwiseService.getUsers().size());
    }

    @Test
    public void balanceTest(){
        Map<String,ExpenseMap> resGroupBalance = splitwiseService.getGroupBalance();
        Assert.assertEquals(276.666666667, resGroupBalance.get("123").getExpenseMap().get("A"),1e-6);
        Assert.assertEquals(-533.33333333, resGroupBalance.get("123").getExpenseMap().get("B"),1e-6);
        Assert.assertEquals(256.666666666, resGroupBalance.get("123").getExpenseMap().get("C"),1e-6);

    }
    @Test
    public void printInvoiceTest(){
        balanceSheet.printInvoice("123");
    }

}
