package org.example.models;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ExpenseMap implements Iterable<Map.Entry<String,Double>> {
    Map<String,Double> expenseMap;

    public ExpenseMap() {
        this.expenseMap = new HashMap<>();
    }
    public boolean isEmpty() {
        return this.expenseMap.isEmpty();
    }

    public void addExpense(String receiver, Double settlementAmt) {
        this.expenseMap.put(receiver,settlementAmt);
    }

    public Map<String,Double> getExpenseMap() {
        return Collections.unmodifiableMap(expenseMap);
    }

    @Override
    public Iterator<Map.Entry<String, Double>> iterator() {
        return expenseMap.entrySet().iterator();
    }
}
