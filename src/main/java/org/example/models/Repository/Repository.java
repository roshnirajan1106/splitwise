package org.example.models.Repository;

import org.example.models.*;
import org.example.models.Balance.BalanceSheet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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

    public Repository(){
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
        if(users.containsKey(userId)){
            throw new IllegalArgumentException();
        }
        users.put(userId,new User(userId,name));
        return true;
    }

    public void addGroup(String id, String name, List<String> userIds) {
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

    public boolean isGroupPresent(String groupId) {
        return groupList.containsKey(groupId);
    }

    public void addExpenseToGroup(String groupId, Expense expense) {
        groupExpenseList.computeIfAbsent(groupId, k -> new ArrayList<>()).add(expense);

    }

    public void updateFinalBalanceOfGroup(String groupId, Expense expense) {
        groupFinalBalance.computeIfAbsent(groupId, k -> new ExpenseMap());
        ExpenseMap expenseMap = groupFinalBalance.get(groupId);
        for (Split split : expense.getSplits()) {
            String userId = split.getUserId();
            double amount = split.getAmount();
            expenseMap.addExpense(userId, expenseMap.getExpenseMap().getOrDefault(userId, 0.0) + amount);
        }
        groupFinalBalance.put(groupId, expenseMap);
    }

    public ExpenseMap getGroupBalance(String groupId) {
        if(!groupFinalBalance.containsKey(groupId)){
            throw new IllegalArgumentException("no group balance found for the group id " + groupId);
        }
        return groupFinalBalance.get(groupId);
    }

    public BalanceSheet getBalanceSheet(String groupId) {
        return balanceSheetMap.get(groupId);
    }

    public void addSettlement(String groupId, String payer, String reciever, Double settlementAmt) {
        balanceSheetMap.get(groupId).addSettlement(payer,reciever,settlementAmt);
    }

    public void clearBalanceSheet(String groupId) {
        balanceSheetMap.put(groupId,new BalanceSheet());
    }
}
