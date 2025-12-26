package org.example.models;

import java.util.HashMap;
import java.util.Map;

public class ExpenseMap {
    Map<String,Double> expenseMap;

    public ExpenseMap() {
        this.expenseMap = new HashMap<>();
    }

    public Map<String, Double> getExpenseMap() {
        return expenseMap;
    }

    public void put(String key, Double value) {
        expenseMap.put(key,value);
    }

    public boolean isEmpty() {
        return expenseMap.isEmpty();
    }
}
