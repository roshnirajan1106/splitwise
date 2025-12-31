import org.example.models.BalanceSheet;
import org.example.models.ExpenseMap;
import org.example.models.SplitType;
import org.example.service.SplitWiseService;
import org.junit.Assert;
import org.junit.Test;
import java.util.Map;

public class ExpenseTest {

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
        SplitWiseService.SPLITWISESERVICE.addExpense("trip","exp1",1000,"A","123", SplitType.EQUAL,getExpenseMapForEqual());
        SplitWiseService.SPLITWISESERVICE.addExpense("food","exp2",120.0,"B","123", SplitType.EXACT,getExpenseMapForExact());
        SplitWiseService.SPLITWISESERVICE.addExpense("travel","exp3",1000.0,"C","123", SplitType.PERCENTAGE,getExpenseMapForPercentage());
        balanceSheet = new BalanceSheet();
    }
    @Test
    public void createUser(){
        SplitWiseService.SPLITWISESERVICE.createUser("D","random");
        SplitWiseService.SPLITWISESERVICE.createUser("E","random");
        Assert.assertEquals(5, SplitWiseService.SPLITWISESERVICE.getUsers().size());
    }

    @Test
    public void balanceTest(){
        Map<String,ExpenseMap> resGroupBalance = SplitWiseService.SPLITWISESERVICE.getGroupBalance();
        Assert.assertEquals(276.666666667, resGroupBalance.get("123").getExpenseMap().get("A"),1e-6);
        Assert.assertEquals(-533.33333333, resGroupBalance.get("123").getExpenseMap().get("B"),1e-6);
        Assert.assertEquals(256.666666666, resGroupBalance.get("123").getExpenseMap().get("C"),1e-6);

    }
    @Test
    public void printInvoiceTest(){
        balanceSheet.printInvoice("123");
    }

    @Test
    public void settleBalance(){
        balanceSheet.settleBalance("B","C",100.0,"123");
        printInvoiceTest();
    }


}
