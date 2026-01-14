package org.example.models;

import lombok.Getter;

import java.util.Iterator;
import java.util.List;

public class Expense  implements Iterable<Split>{
    private String id;
    private String name;
    private Double amount;
    private String paidByUserId;
    private String groupId;
    @Getter
    private List<Split> splits;


    public Expense(String id, String name, Double amount, String paidByUserId, String groupId, List<Split> splits) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.paidByUserId = paidByUserId;
        this.groupId = groupId;
        this.splits = splits;
    }

    @Override
    public Iterator<Split> iterator() {
        return splits.iterator();
    }
}
