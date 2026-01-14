package org.example.models;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExpenseMap implements Iterable<Map.Entry<String, Double>> {
    Map<String, Double> expenseMap;

    public ExpenseMap() {
        this.expenseMap = new ConcurrentHashMap<>();
    }

    public boolean isEmpty() {
        return this.expenseMap.isEmpty();
    }

    public void addExpense(String receiver, Double settlementAmt) {
        this.expenseMap.merge(receiver, settlementAmt,Double::sum);
    }

    public Map<String, Double> getExpenseMap() {
        return Collections.unmodifiableMap(expenseMap);
    }

    @Override
    public Iterator<Map.Entry<String, Double>> iterator() {
        return expenseMap.entrySet().iterator();
    }

    @Override
    public String toString() {
        return "ExpenseMap{" +
                "expenseMap=" + expenseMap +
                '}';
    }
}
