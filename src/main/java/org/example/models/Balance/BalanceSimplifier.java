package org.example.models.Balance;

import org.example.models.ExpenseMap;
import org.example.models.Repository.Repository;

import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;

import static org.example.models.Balance.BalanceSheet.epsilon;

public class BalanceSimplifier {
    private Repository repository;
    private Map<String, Object> locks = new ConcurrentHashMap<>();

    public BalanceSimplifier(Repository repository) {
        this.repository = repository;
    }

    public synchronized void simplifyTheExpense(String groupId) {
        //locks according to group
        Object lock = locks.computeIfAbsent(groupId, k -> new Object());
        synchronized (lock) {
            BalanceSheet balanceSheet = new BalanceSheet();
            ExpenseMap groupExpenseMap = repository.getGroupBalance(groupId);
            Comparator<Map.Entry<String, Double>> comparator = Comparator.comparingDouble(Map.Entry::getValue);
            PriorityQueue<Map.Entry<String, Double>> minHeap = new PriorityQueue<>(comparator);
            PriorityQueue<Map.Entry<String, Double>> maxHeap = new PriorityQueue<>(comparator.reversed());
            groupExpenseMap.getExpenseMap().entrySet().forEach(e -> {
                if (e.getValue() >= 0) {
                    maxHeap.add(e);
                } else {
                    minHeap.add(e);
                }
            });
            while (!minHeap.isEmpty() && !maxHeap.isEmpty()) {
                var maxNegative = minHeap.poll();
                var maxPostive = maxHeap.poll();
                Double settlementAmt = Math.min(maxPostive.getValue(), Math.abs(maxNegative.getValue()));
                Double amtPostiveRem = maxPostive.getValue() - settlementAmt;
                Double amtNegativeRem = maxNegative.getValue() + settlementAmt;
                if (Math.abs(amtNegativeRem) > epsilon) {
                    minHeap.add(Map.entry(maxNegative.getKey(), amtNegativeRem));
                } else if (amtPostiveRem > epsilon) {
                    maxHeap.add(Map.entry(maxPostive.getKey(), amtPostiveRem));
                }
                balanceSheet.addSettlement(maxNegative.getKey(), maxPostive.getKey(), settlementAmt);
            }
            repository.updateBalanceSheet(groupId, balanceSheet);
        }
    }
}
