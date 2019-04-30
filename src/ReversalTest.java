
import tntlutgen.Detector;
import tntlutgen.LUT;
import tntlutgen.TntSimulator;
import tntlutgen.MessMath;

import java.math.BigInteger;
import java.util.*;

public class ReversalTest {

    public static void main(String[] args) {
        Detector.generateDetectorCircle();
        long[] lutLast = new long[Detector.detectors.size()];
        for (int i = 0; i < lutLast.length; i++)
            lutLast[(i+1)%lutLast.length] = LUT.getLUTValue(i);

        final long a = 0x97be9f880aa9L;
        final long b = 0xeac471130bcaL;
        final long[] bvec = {72070, -32212, -14468, -46088, 23600, -33937, 116154};
        final long[] m6 = {200379505784862L, 109233788106496L, 84844885213974L, 37699456995249L, 39206040217722L, 63870693550397L, 281003457836972L};
        final long[][] mm1 = {
                {-26, -81, -16, 50, 14, 22, -76},
                {-117, 4, -42, -45, -20, -61, -32},
                {3, 79, 1, 92, 5, 26, 8},
                {18, 28, 48, 53, -92, -33, -33},
                {43, 63, -68, -10, 33, 18, -57},
                {-21, -6, 91, -19, 56, 51, -12},
                {-44, 25, -6, -1, -44, 65, 34}
        };

        int failcount = 0;
        for (int i = 0;i<10000000; i++) {
            Random rand = new Random();
            int[] detectorArray = new int[7];
            for (int j = 0; j < 7; j++) {
                rand = new Random((((((MessMath.getSeed(rand)*a+b)- 0xbL) * 0xdfe05bcb1365L)- 0xbL) * 0xdfe05bcb1365L)^0x5deece66dL);
                detectorArray[j] = TntSimulator.getDetectorOutput(rand.nextDouble() * 2 * Math.PI);
            }

            long[] penultimateVector = new long[7];
            for (int j = 0; j < 7; j++) {
                long[] v = new long[7];
                for (int k = 0; k < 7; k++) {
                    int detectorId = detectorArray[k];
                    v[k] = lutLast[detectorId];
                }
                BigInteger val = MessMath.dotBig(v, mm1, j);
                val = val.add(BigInteger.valueOf(bvec[j]));
                val = val.shiftRight(48-LUT.BITS_IGNORED);
                penultimateVector[j] = val.longValue();

            }

            long seed = MessMath.dot(m6, penultimateVector);

            if (MessMath.circularDiff(seed, MessMath.getSeed(rand)) != 0) {
                failcount+=1;
                System.out.println("Expected: " + MessMath.getSeed(rand) + ", actual: " + seed);
                System.out.println("Diff: " + MessMath.circularDiff(seed, MessMath.getSeed(rand)));
                System.out.println(i);
                System.out.println(Arrays.toString(detectorArray));
            }
            if (i%100000==0) {
                System.out.println(i);
            }

        }
        System.out.println(failcount);

    }

}
