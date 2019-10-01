
import tntlutgen.*;

import java.util.*;

/**
 * Tests whether the (code version of) {@link Reverser} is working properly
 */
public class ReversalTest {

    public static void main(String[] args) {
        // Initialize our LUT and constants
        Detector.generateDetectorCircle();

        NBitNumber[] lut = new NBitNumber[Detector.detectors.size()];
        for (int i = 0; i < lut.length; i++)
            lut[i] = new NBitNumber(LUT.getLUTValue(i), 10);

        int failcount = 0;
        for (int i = 0;i<10000000; i++) {
            // Give ourselvevs something to reverse
            Random rand = new Random();
            long seed = Reverser.reverse(rand, lut, false);

            if (MessMath.circularDiff(seed, MessMath.getSeed(rand)) != 0) {
                failcount+=1;
                System.out.println("Expected: " + MessMath.getSeed(rand) + ", actual: " + seed);
                System.out.println("Diff: " + MessMath.circularDiff(seed, MessMath.getSeed(rand)));
                System.out.println(i);
            }
            if (i%100000==0) {
                System.out.println(i);
            }

        }
        System.out.println("Fail count: " + failcount);

    }

}
