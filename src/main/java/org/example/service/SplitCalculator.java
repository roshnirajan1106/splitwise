package org.example.service;

import org.example.models.Split;
import org.example.models.SplitType;
import org.example.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//todo - take care of the currency
public class SplitCalculator {
    public static List<Split> splitEqual(List<String> users,double amt, String payerUserId){
        List<Split> resultSplit = new ArrayList<>();
        double resSplit = amt/users.size();
        for(String userId : users){
            if(userId.equals(payerUserId)){
                resultSplit.add(new Split(userId,amt - resSplit));
            }else{
                resultSplit.add(new Split(userId,-resSplit));
            }
        }
       return resultSplit;
    }
    public static List<Split> splitExact(Map<String,Double> userSplit,double amt,String payerUserId){
        List<Split> resSplit = new ArrayList<>();
        for(var split : userSplit.entrySet()){
            if(split.getKey().equals(payerUserId)){
                resSplit.add(new Split(split.getKey(),amt-split.getValue()));
            }else{
                resSplit.add(new Split(split.getKey(),-split.getValue()));
            }
        }
        return resSplit;
    }
    public static List<Split> splitByPercentage(Map<String,Double>userSplit,double amt,String payerUserId){
        List<Split> resSplit = new ArrayList<>();
        for(var split : userSplit.entrySet()){
            double percentAmt = split.getValue() * 0.01 * amt ;
            if(split.getKey().equals(payerUserId)){
                resSplit.add(new Split(split.getKey(),amt-percentAmt));
            }else{
                resSplit.add(new Split(split.getKey(),-percentAmt));
            }
        }
        return resSplit;
    }

    public static List<Split> calculateUserSplit(List<String> users,double amount, String paidByUserId, Map<String, Double> splitData, SplitType splitType) {
        switch (splitType){
            case EXACT:
                if(splitData == null || splitData.isEmpty()){
                    throw new IllegalArgumentException("amount data per user is required");
                }
                return splitExact(splitData,amount,paidByUserId);
            case PERCENTAGE:
                if(splitData == null || splitData.isEmpty()){
                    throw new IllegalArgumentException("percentage data is required!");
                }
                return splitByPercentage(splitData,amount,paidByUserId);
            default:
                if(users == null || users.isEmpty()){
                    throw new IllegalArgumentException("Users cannot be null.");
                }
                return splitEqual(users,amount, paidByUserId);
        }
    }

    public static void validateSplit(List<Split> finalSplit) {
        double postiveNumber = 0;
        double negativeNumber = 0;
        for(Split split : finalSplit){
            if(split.getAmount() > 0.01){
                postiveNumber += split.getAmount();
            }else{
                negativeNumber += split.getAmount();
            }
        }
        if(postiveNumber != negativeNumber){
            throw new IllegalStateException("Split is not matching, please recheck!");
        }
    }
}

