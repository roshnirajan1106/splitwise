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
        groupExpenseList = new HashMap<>();
        groupBalance = new HashMap<>();
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public Map<String, Group> getGroupList() {
        return groupList;
    }

    public Map<String, List<Expense>> getGroupExpenseList() {
        return groupExpenseList;
    }

    public Map<String, ExpenseMap> getGroupBalance() {
        return groupBalance;
    }

    public static SplitwiseService getInstance() {
        if (splitWiseService == null) {
            splitWiseService = new SplitwiseService();
        }
        return splitWiseService;
    }

    public void createUser(String userId, String name) {
        //todo -check if user already exists dont create
        if (users.containsKey(userId)) {
            throw new IllegalStateException("the user already exists!");
        }
        users.put(userId, new User(userId, name));
    }

    public void createGroup(String id, String name, List<String> userIds) {
        List<User> userList = new ArrayList<>();
        for (String usersId : userIds) {
            if (!users.containsKey(usersId)) {
                //todo - ask for the full details - like name.
                createUser(usersId, "Random-name");
            }
            userList.add(users.get(usersId));
        }
        groupList.put(id, new Group(id, name, userList, new Date()));
    }

    public Boolean addExpense(String name, String id, double amount, String paidByUserId, String groupId, SplitType splitType, ExpenseMap splitData) throws IllegalStateException {
        //split data logic calculation - and store the final split it each expense which is a map
        List<String> userList = new ArrayList<>(splitData.getExpenseMap().keySet());
        if(!groupList.containsKey(groupId)){
            createGroup(groupId,"Trip",userList);
        }
        List<Split> finalSplit = SplitCalculator.calculateUserSplit(userList, amount, paidByUserId, splitData, splitType);
        SplitCalculator.validateSplit(finalSplit);
        Expense expense = new Expense(id, name, amount, paidByUserId, groupId, finalSplit);
        groupExpenseList.computeIfAbsent(groupId, k -> new ArrayList<>()).add(expense);
        updateGroupExpense(groupId, expense);
        return true;
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
