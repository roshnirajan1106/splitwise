package org.example.service;

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
}
