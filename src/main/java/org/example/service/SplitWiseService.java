package org.example.service;

import org.example.models.*;
import org.example.models.Balance.BalanceSheet;
import org.example.models.Balance.BalanceSimplifier;
import org.example.models.Repository.Repository;

import java.util.*;

// convert to repository + service - but what to make singleton etc.
public class SplitWiseService {
    private volatile static SplitWiseService splitWiseService = null;
    private BalanceSimplifier balanceSimplifier;
    private Repository repository;

    private SplitWiseService() {
        repository = new Repository();
        balanceSimplifier = new BalanceSimplifier(repository);
    }

    public static SplitWiseService getInstance() {
        if (splitWiseService == null) {
            synchronized (SplitWiseService.class) {
                if (splitWiseService == null) {
                    splitWiseService = new SplitWiseService();
                }
            }
        }
        return splitWiseService;
    }

    public Map<String, User> getUsers() {
        return repository.getUsers();
    }

    public void createUser(String userId, String name) {
        //todo -check if user already exists dont create
        repository.addUser(userId, name);
    }

    public void createGroup(String id, String name, List<String> userIds) {
        repository.addGroup(id, name, userIds);
    }

    public Boolean addExpense(String name, String id, double amount, String paidByUserId, String groupId, SplitType splitType, ExpenseMap splitData) throws IllegalStateException {
        //split data logic calculation - and store the final split it each expense which is a map
        List<String> userList = new ArrayList<>(splitData.getExpenseMap().keySet());
        if (!repository.isGroupPresent(groupId)) {
            createGroup(id, name, userList);
        }

        List<Split> finalSplit = SplitCalculator.calculateUserSplit(userList, amount, paidByUserId, splitData, splitType);
        SplitCalculator.validateSplit(finalSplit);
        Expense expense = new Expense(id, name, amount, paidByUserId, groupId, finalSplit);
        repository.addExpenseToGroup(groupId, expense);
        updateGroupExpense(groupId, expense);
        return true;
    }

    private void updateGroupExpense(String groupId, Expense expense) {
        repository.updateFinalBalanceOfGroup(groupId, expense);
        balanceSimplifier.simplifyTheExpense(groupId);
    }

    public void settleBalance(String payer, String  receiver, Double amount, String groupId){
        ExpenseMap splitData = new ExpenseMap();
        splitData.addExpense(payer,-amount);
        splitData.addExpense(receiver,amount);
        splitWiseService.addExpense("settling","settle-balance",0.0,payer,groupId, SplitType.EXACT,splitData);
        System.out.println("Balance is settled successfully!");
        //todo - error handling what if there no settlement amount between two.
        balanceSimplifier.simplifyTheExpense(groupId);
    }
    public void printInvoice(String groupId){
        BalanceSheet balanceSheet = repository.getBalanceSheet(groupId);
        for(var balance : balanceSheet){
            for(var expenseMap : balance.getValue()){
                System.out.println(balance.getKey() + " needs to give " + expenseMap.getKey() + " amt " + expenseMap.getValue());
            }
        }
    }

    public BalanceSheet getBalanceSheet(String groupId) {
        return repository.getBalanceSheet(groupId);
    }
}
