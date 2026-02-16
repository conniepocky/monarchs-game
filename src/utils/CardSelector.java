package utils; 

import java.util.List;
import java.util.Stack;
import data.TableResult;

public class CardSelector {

    public CardSelector() {
        // default constructor, no initialisation needed for this utility class
    }

    public Integer selectNextCardIndex(List<Float> weights) {
        Integer N = weights.size();
        Float totalWeight = 0.0f;
        
        // calculate total weight
        for (Float weight : weights) {
            totalWeight += weight;
        }

        // error check: if total weight is 0 or completely uniform, select uniformly
        if (totalWeight == 0.0f || totalWeight == N.floatValue()) {  
            return (int)(Math.random() * N);
        }

        // proceed with vose alias method
        TableResult tableResult = setupAliasTables(weights, totalWeight, N);
        return drawWeightedCard(tableResult.getProbTable(), tableResult.getAliasTable(), N);
    }

    public TableResult setupAliasTables(List<Float> weights, Float totalWeight, Integer N) {
        Float[] probTable = new Float[N];
        Integer[] aliasTable = new Integer[N];

        double[] scaledProbs = new double[N]; 

        Stack<Integer> small = new Stack<>(); 
        Stack<Integer> large = new Stack<>(); 

        for (int i = 0; i < N; i++) {
            double scaledProb = weights.get(i) * N / totalWeight; 
            scaledProbs[i] = scaledProb;

            if (scaledProb < 1.0) {
                small.push(i); 
            } else {
                large.push(i); 
            }
        }

        while (!small.isEmpty() && !large.isEmpty()) { 
            Integer smallIndex = small.pop();
            Integer largeIndex = large.pop();

            probTable[smallIndex] = (float) scaledProbs[smallIndex];
            aliasTable[smallIndex] = largeIndex;

            double remainingProb = (scaledProbs[largeIndex] + scaledProbs[smallIndex]) - 1.0;
            scaledProbs[largeIndex] = remainingProb;

            if (remainingProb < 1.0) { 
                small.push(largeIndex);
            } else {
                large.push(largeIndex);
            }
        }

        while (!large.isEmpty()) {
            Integer largeIndex = large.pop();
            probTable[largeIndex] = 1.0f;
        }

        while (!small.isEmpty()) {
            Integer smallIndex = small.pop();
            probTable[smallIndex] = 1.0f; 
        }

        return new TableResult(probTable, aliasTable);
    }

    public Integer drawWeightedCard(Float[] Prob, Integer[] Alias, Integer N) {
        Integer randomBucketIndex = (int)(Math.random() * N); 
        Float randomProb = (float)(Math.random());

        if (randomProb < Prob[randomBucketIndex]) {
            return randomBucketIndex; 
        } else {
            return Alias[randomBucketIndex]; 
        }
    }
}