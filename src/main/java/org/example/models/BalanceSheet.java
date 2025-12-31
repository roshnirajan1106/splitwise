package org.example.models;

import org.example.service.SplitWiseService;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class BalanceSheet {
    private final Map<String,ExpenseMap> balanceMap;

    private static final double epsilon = 1e-6;
    public BalanceSheet() {
        this.balanceMap = new HashMap<>();
    }

    public void settleBalance(String payer, String  receiver, Double amount, String groupId){

        ExpenseMap splitData = new ExpenseMap();
        splitData.put(payer,-amount);
        splitData.put(receiver,amount);
        SplitWiseService.SPLITWISESERVICE.addExpense("settling","settle-balance",0.0,payer,groupId,SplitType.EXACT,splitData);
        System.out.println("Balance is settled successfully!");
        //todo - error handling what if there no settlement amount between two.
        simplifyTheExpense(groupId);
    }

    private void simplifyTheExpense(String groupId){
        ExpenseMap groupExpenseMap = SplitWiseService.SPLITWISESERVICE.getGroupBalance().get(groupId);
        Comparator<Map.Entry<String,Double>> comparator = Comparator.comparingDouble(Map.Entry::getValue);
        PriorityQueue<Map.Entry<String,Double>> minHeap = new PriorityQueue<>(comparator);
        PriorityQueue<Map.Entry<String,Double>> maxHeap = new PriorityQueue<>(comparator.reversed());

        groupExpenseMap.getExpenseMap().entrySet().forEach(e -> {
            if(e.getValue() >= 0){
                maxHeap.add(e);
            }else{
                minHeap.add(e);
            }
        });
        while(!minHeap.isEmpty() && !maxHeap.isEmpty()){
            var maxNegative = minHeap.poll();
            var maxPostive = maxHeap.poll();
            Double settlementAmt  = Math.min(maxPostive.getValue(),Math.abs(maxNegative.getValue()));
            Double amtPostiveRem = maxPostive.getValue() - settlementAmt;
            Double amtNegativeRem = maxNegative.getValue() + settlementAmt;
            if(Math.abs(amtNegativeRem) > epsilon){
                minHeap.add(Map.entry(maxNegative.getKey(),amtNegativeRem));
            }else if(amtPostiveRem > epsilon){
                maxHeap.add(Map.entry(maxPostive.getKey(),amtPostiveRem));
            }
            ExpenseMap expenseMap = balanceMap.computeIfAbsent(maxNegative.getKey(),k->new ExpenseMap());
            expenseMap.getExpenseMap().put(maxPostive.getKey(),settlementAmt);
        }

    }

    public void printInvoice(String groupId){
        simplifyTheExpense(groupId);
        for(var balance : balanceMap.entrySet()){
            for(var settlement : balance.getValue().getExpenseMap().entrySet()){
                System.out.println("From "+ balance.getKey() + " to " +settlement.getKey() +" the amount pending is "+settlement.getValue());
            }
        }
    }

}
