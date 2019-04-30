
import tntlutgen.*;

import java.math.BigInteger;
import java.util.*;

public class ReversalTest {

    public static void main(String[] args) {
        // Initialize our LUT and constants
        Detector.generateDetectorCircle();

        NBitNumber[] lutLast = new NBitNumber[Detector.detectors.size()];
        for (int i = 0; i < lutLast.length; i++)
            lutLast[(i+1)%lutLast.length] = new NBitNumber(LUT.getLUTValue(i), 10);

        final long a = 0x97be9f880aa9L;
        final long b = 0xeac471130bcaL;
        final NBitNumber[] bvec = NBitNumber.vec(24, 0x01_198a, 0xff_822e, 0xff_c780, 0xff_4bfc, 0x00_5c33, 0xff_7b71, 0x01_c5bf);
        final NBitNumber[] m6 = NBitNumber.vec(48,
                0xb63e_7d43_381eL,
                0x6358_f923_3700L,
                0x4d2a_7d3e_8316L,
                0x2249_96be_0fb1L,
                0x23a8_5e19_a87aL,
                0x3a17_0e00_293dL,
                0xff92_3748_f7acL);
        final NBitNumber[][] mm1 = NBitNumber.mat(16, new long[][] {
                {26, 81, 16, 50, 14, 22, 76},
                {117, 4, 42, 45, 20, 61, 32},
                {3, 79, 1, 92, 5, 26, 8},
                {18, 28, 48, 53, 92, 33, 33},
                {43, 63, 68, 10, 33, 18, 57},
                {21, 6, 91, 19, 56, 51, 12},
                {44, 25, 6, 1, 44, 65, 34}
        });
        final int[][] mm1Signs = {
                {-1, -1, -1, +1, +1, +1, -1},
                {-1, +1, -1, -1, -1, -1, -1},
                {+1, +1, +1, +1, +1, +1, +1},
                {+1, +1, +1, +1, -1, -1, -1},
                {+1, +1, -1, -1, +1, +1, -1},
                {-1, -1, +1, -1, +1, +1, -1},
                {-1, +1, -1, -1, -1, +1, +1}
        };

        int failcount = 0;
        for (int i = 0;i<10000000; i++) {
            // Give ourselvevs something to reverse
            Random rand = new Random();
            int[] detectorArray = new int[7];
            for (int j = 0; j < 7; j++) {
                rand = new Random((((((MessMath.getSeed(rand)*a+b)- 0xbL) * 0xdfe05bcb1365L)- 0xbL) * 0xdfe05bcb1365L)^0x5deece66dL);
                detectorArray[j] = TntSimulator.getDetectorOutput(rand.nextDouble() * 2 * Math.PI);
            }
            NBitNumber[] lutOut = new NBitNumber[7];
            for (int k = 0; k < 7; k++) {
                int detectorId = detectorArray[k];
                lutOut[k] = lutLast[detectorId];
            }

            NBitNumber[] penultimateVector = new NBitNumber[7];
            for (int j = 0; j < 7; j++) {
                // initialize to bvec
                NBitNumber val = bvec[j];
                // add to val the dot product of lutOut with column j of mm1
                for (int k = 0; k < 7; k++) {
                    NBitNumber product = lutOut[k].mul(mm1[k][j], 24);
                    if (mm1Signs[k][j] < 0) // handle signed product when increasing bit count; only mm1 is signed
                        product = new NBitNumber(0xffffff, 24).sub(product, 24);
                    val = val.add(product, 24);
                }
                val = val.shiftRight(48 - LUT.BITS_IGNORED, 48);
                // In reality val is currently 14-bit
                // Although we're using 24-bit val before the shift, in reality it's 21-bit which means we can
                // test the whole of the top hex digit after the shift for negativity.
                if ((val.val & 0xf000) != 0) // is val negative?
                    val = new NBitNumber(0xffff_ffff_0000L | val.val, 48); // handle signed right shift

                penultimateVector[j] = val;
            }

            NBitNumber seed = MessMath.dot(m6, penultimateVector, 48);

            if (MessMath.circularDiff(seed.val, MessMath.getSeed(rand)) != 0) {
                failcount+=1;
                System.out.println("Expected: " + MessMath.getSeed(rand) + ", actual: " + seed.val);
                System.out.println("Diff: " + MessMath.circularDiff(seed.val, MessMath.getSeed(rand)));
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
