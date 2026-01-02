package org.example.models.Balance;

import org.example.models.ExpenseMap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BalanceSheet implements Iterable<Map.Entry<String,ExpenseMap>> {

     public static final double epsilon = 1e-6;
    //map between how much one person will give to another. (payer, reciver,amt)
    //one balance sheet belongs to one group id.
    private final Map<String, ExpenseMap> balanceSheet = new HashMap<>();

    public void addSettlement(String payer, String receiver, Double settlementAmt) {
        this.balanceSheet.computeIfAbsent(payer,k-> new ExpenseMap());
        this.balanceSheet.get(payer).addExpense(receiver,settlementAmt);
    }


    @Override
    public Iterator<Map.Entry<String,ExpenseMap>> iterator() {
        return balanceSheet.entrySet().iterator();
    }
}
