package org.example.service;

import org.example.models.*;

import java.util.*;

public class SplitwiseService {
    private static SplitwiseService splitWiseService;
    Map<String, User> users;
    Map<String, Group> groupList;
    //list of expenses for a group-id.
    Map<String, List<Expense>> groupExpenseList;
    //for each group id what is the final balance of each user
    Map<String, ExpenseMap> groupBalance;

    private SplitwiseService() {
        users = new HashMap<>();
        groupList = new HashMap<>();
        groupBalance = new HashMap<>();
    }

    public SplitwiseService getInstance() {
        if (splitWiseService == null) {
            splitWiseService = new SplitwiseService();
        }
        return splitWiseService;
    }

    public void createUser(String userId, String name, String email) {
        //todo -check if user already exists dont create
        users.put(userId, new User(userId, name, email));
    }

    public void createGroup(String id, String name, List<String> userIds) {
        List<User> userList = new ArrayList<>();
        for (String usersId : userIds) {
            if (users.containsKey(usersId)) {
                userList.add(users.get(usersId));
            }
        }
        groupList.put(id, new Group(id, name, userList, new Date()));
    }

    public void addExpense(String name, String id, double amount, String paidByUserId, String groupId, SplitType splitType, Map<String, Double> splitData) throws IllegalStateException {
        //split data logic calculation - and store the final split it each expense which is a map
        List<String> userList = new ArrayList<>(splitData.keySet());
        List<Split> finalSplit = SplitCalculator.calculateUserSplit(userList, amount, paidByUserId, splitData, splitType);
        SplitCalculator.validateSplit(finalSplit);
        Expense expense = new Expense(id, name, amount, paidByUserId, groupId, finalSplit);
        groupExpenseList.computeIfAbsent(groupId, k -> new ArrayList<>()).add(expense);

        updateGroupExpense(groupId, expense);
    }

    private void updateGroupExpense(String groupId, Expense expense) {
        groupBalance.computeIfAbsent(groupId, k -> new ExpenseMap());
        ExpenseMap expenseMap = groupBalance.get(groupId);
        for (Split split : expense.getSplits()) {
            String userId = split.getUserId();
            double amount = split.getAmount();
            expenseMap.getExpenseMap().put(userId, expenseMap.getExpenseMap().getOrDefault(userId, 0.0) + amount);
        }
        groupBalance.put(groupId, expenseMap);
    }

}
