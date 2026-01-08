package org.example.models.Repository;

import lombok.extern.slf4j.Slf4j;
import org.example.models.Balance.BalanceSheet;
import org.example.models.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Repository {
    Map<String, User> users;
    Map<String, Group> groupList;
    //list of expenses for a group-id.
    Map<String, List<Expense>> groupExpenseList;
    //for each group id what is the final balance of each user
    Map<String, ExpenseMap> groupFinalBalance;
    //for each group id, - simplified balance
    Map<String, BalanceSheet> balanceSheetMap;
    private volatile ExpenseMap expenseMap;

    public Repository() {
        users = new ConcurrentHashMap<>();
        groupList = new ConcurrentHashMap<>();
        groupExpenseList = new ConcurrentHashMap<>();
        groupFinalBalance = new ConcurrentHashMap<>();
        balanceSheetMap = new ConcurrentHashMap<>();
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public boolean addUser(String userId, String name) {
        if (users.containsKey(userId)) {
            throw new IllegalArgumentException();
        }
        users.put(userId, new User(userId, name));
        return true;
    }

    public void addGroup(String id, String name, List<String> userIds) {
        if (!groupList.containsKey(id)) {
            synchronized (this) {
                if (!groupList.containsKey(id)) {
                    List<User> userList = new ArrayList<>();
                    for (String usersId : userIds) {
                        if (!users.containsKey(usersId)) {
                            //todo - ask for the full details - like name.
                            addUser(usersId, "Random-name");
                        }
                        userList.add(users.get(usersId));
                    }
                    groupList.put(id, new Group(id, name, userList, new Date()));
                }
            }
        }

    }

    public void addExpenseToGroup(String groupId, Expense expense) {
        groupExpenseList.computeIfAbsent(groupId, k -> Collections.synchronizedList(new ArrayList<>())).add(expense);
    }

    public void updateFinalBalanceOfGroup(String groupId, Expense expense) {
        groupFinalBalance.computeIfAbsent(groupId, k -> new ExpenseMap());
        expenseMap = groupFinalBalance.get(groupId);
        for (Split split : expense.getSplits()) {
            String userId = split.getUserId();
            double amount = split.getAmount();
            expenseMap.addExpenseForGroup(userId, amount);
          //  System.out.println("inside update " + Thread.currentThread().getName() + " expenseMap " + expenseMap);
        }
        groupFinalBalance.put(groupId, expenseMap);
    }

    public ExpenseMap getGroupBalance(String groupId) {
        if (!groupFinalBalance.containsKey(groupId)) {
            throw new IllegalArgumentException("no group balance found for the group id " + groupId);
        }
        return groupFinalBalance.get(groupId);
    }

    public BalanceSheet getBalanceSheet(String groupId) {
        return balanceSheetMap.get(groupId);
    }

    public void addSettlement(String groupId, String payer, String reciever, Double settlementAmt) {
        balanceSheetMap.get(groupId).addSettlement(payer, reciever, settlementAmt);
    }

    public void clearBalanceSheet(String groupId) {
        balanceSheetMap.put(groupId, new BalanceSheet());
    }
}
