
import tntlutgen.Detector;
import tntlutgen.LUT;
import tntlutgen.TntSimulator;
import tntlutgen.MessMath;

import java.math.BigInteger;
import java.util.*;

public class ReversalTest {

    public static void main(String[] args) {
        TntSimulator.initialHeight = 26;
        TntSimulator.launchTntCount = 7;
        Detector.generateDetectorCircle();
        long[] lut = new long[Detector.detectors.size()];
        for (int i = 0; i < lut.length; i++)
            lut[i] = LUT.getUpperSeedBound(i);

        final long a = 0x97be9f880aa9L;
        final long b = 0xeac471130bcaL;
        // final long[] bvec = {-27656690254670940L, -8487796951449621L, 6587028370136042L, 15308787416636229L, -19938768618496968L, -11963740464960064L, -33031215471443596L};
        final long[] bvec = {-100614,-30878,23964,55694,-72536,-43523,-120166};
        final long[] m6 = {0xff7392795dd6L, 0x1184f9b300L, 0xff79d1d659aeL, 0xff62f08153d5L, 0xfe64fe5e8622L, 0x24beb7ecc11L, 0x30c38f7adcL};
        final long[][] mm1 = {
                {-26, -81, -16, 50, 14, 22, -76},
                {-117, 4, -42, -45, -20, -61, -32},
                {3, 79, 1, 92, 5, 26, 8},
                {18, 28, 48, 53, -92, -33, -33},
                {43, 63, -68, -10, 33, 18, -57},
                {-21, -6, 91, -19, 56, 51, -12},
                {-44, 25, -6, -1, -44, 65, 34}
        };
        //for (long n:bvec) {
        //    System.out.println(n >> BITS_IGNORED);
        //}

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
                    if ((mm1[k][j] & 0x800000000000L) != 0) { // if it's negative mod 2^48
                        detectorId--;
                        if (detectorId < 0)
                            detectorId += Detector.detectors.size();
                        // v[k] = lut[detectorId] & ~((1L << (BITS_IGNORED))-1);
                        v[k] = lut[detectorId];

                    } else {
                        v[k] = lut[detectorId];
                        //  v[k] = lut[detectorId] & ~((1L << (BITS_IGNORED))-1);
                        if (detectorId == 0) {
                            v[k]+=(1L<<(48-LUT.BITS_IGNORED));
                        }
                    }
                }
                BigInteger val = MessMath.dotBig(v, mm1, j);
                // val = val.subtract(BigInteger.valueOf(bvec[j]).shiftRight(BITS_IGNORED));
                val = val.subtract(BigInteger.valueOf(bvec[j]));
                val = val.shiftRight(48-LUT.BITS_IGNORED);
                penultimateVector[j] = val.longValue();

            }
            //System.out.println(Arrays.toString(bvec));
            //System.out.println(Arrays.toString(penultimateVector));
            long seed = MessMath.dot(m6, penultimateVector);
            for (int j = 0; j < 1; j++) {
                //seed = ((seed - 0xbL) * 0xdfe05bcb1365L) & 0xffffffffffffL;
                seed = (seed * 0x5deece66dL + 0xbL) & 0xffffffffffffL;
            }

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
