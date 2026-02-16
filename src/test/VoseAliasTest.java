package test;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;
import data.TableResult;
import utils.CardSelector;

public class VoseAliasTest {
    private CardSelector cardSelector = new CardSelector();

    @Test
    public void testSetupAliasTablesExtremeBias() {
        // TEST INPUT: Extreme bias 
        // card index 1 dominates completely, whilst 0 and 2 are nearly impossible to draw naturally.
        List<Float> weights = Arrays.asList(0.0001f, 10000.0f, 0.0001f);
        Float totalWeight = 10000.0002f;
        Integer N = 3;

        // table setup
        TableResult result = cardSelector.setupAliasTables(weights, totalWeight, N);
        Float[] probs = result.getProbTable();
        Integer[] aliases = result.getAliasTable();

        // expected outcome: in this extreme case, the alias method should crush the probabilities of the tiny weights (0 and 2) to near zero, and alias them to the dominant index (1).
        
        assertEquals(1, aliases[0], "Bucket 0 must alias to the dominant weight (index 1).");
        assertTrue(probs[0] < 0.001f, "Bucket 0 probability should be crushed to near zero.");

        assertEquals(1, aliases[2], "Bucket 2 must alias to the dominant weight (index 1).");
        assertTrue(probs[2] < 0.001f, "Bucket 2 probability should be crushed to near zero.");

        // the dominant bucket (1) should absorb the remaining probability and stand at exactly 1.0f
        assertEquals(1.0f, probs[1], 0.0001f, "Dominant bucket probability must equal exactly 1.0.");
    }

    @Test
    public void testSetupAliasTablesHeavySkew() {
        // TEST INPUT: Heavy Skew (N=4), large distribution of small/medium weights.
        // card 3 dominates completely. It will be repeatedly drawn from the 'large' stack, subtracted from, and re-added to the stack until it is depleted.
        List<Float> weights = Arrays.asList(10.0f, 20.0f, 30.0f, 140.0f);
        Float totalWeight = 200.0f;
        Integer N = 4;

        // table setup
        TableResult result = cardSelector.setupAliasTables(weights, totalWeight, N);
        Float[] probs = result.getProbTable();
        Integer[] aliases = result.getAliasTable();

        // expected outcome: card 3 should dominate and be aliased to all other buckets, but the smaller weights should still retain their original probabilities (0.2, 0.4, and 0.6 respectively) until the dominant bucket is fully depleted to 1.0 after subtractions.
        
        // scaled probabilities = [0.2, 0.4, 0.6, 2.8]
        // index 3 (2.8) must be sliced 3 times to 'top up' indices 0, 1, and 2 to 1.0.
        // 2.8 - 0.8 (for bucket 0) - 0.6 (for bucket 1) - 0.4 (for bucket 2) = exactly 1.0 remaining.
        
        // Assert Bucket 0
        assertEquals(0.2f, probs[0], 0.0001f, "Bucket 0 probability should be exactly 0.2.");
        assertEquals(3, aliases[0], "Bucket 0 must alias to the dominant index (3).");

        // Assert Bucket 1
        assertEquals(0.4f, probs[1], 0.0001f, "Bucket 1 probability should be exactly 0.4.");
        assertEquals(3, aliases[1], "Bucket 1 must alias to the dominant index (3).");

        // Assert Bucket 2
        assertEquals(0.6f, probs[2], 0.0001f, "Bucket 2 probability should be exactly 0.6.");
        assertEquals(3, aliases[2], "Bucket 2 must alias to the dominant index (3).");

        // Assert Bucket 3 (The remainder)
        assertEquals(1.0f, probs[3], 0.0001f, "Bucket 3 should be perfectly depleted to exactly 1.0 after re-sorting.");
    }

    @Test
    public void testSetupAliasTablesUniformWeights() {
        // TEST INPUT: 4 cards with identical weights
        List<Float> weights = Arrays.asList(10.0f, 10.0f, 10.0f, 10.0f);
        Float totalWeight = 40.0f;
        Integer N = 4;

        TableResult result = cardSelector.setupAliasTables(weights, totalWeight, N);
        Float[] probs = result.getProbTable();

        // expected outcome: if all weights are equal, every bucket in the probability table should be exactly 1.0
        for (int i = 0; i < N; i++) {
            assertEquals(1.0f, probs[i], 0.0001f, 
                "Uniform weights should yield a scaled probability of exactly 1.0 for all buckets.");
            assertEquals(null, result.getAliasTable()[i], 
                "Uniform weights should yield an alias table where each bucket aliases to itself.");
        }
    }
}