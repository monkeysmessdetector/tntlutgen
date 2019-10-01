package tntlutgen;

import java.util.Random;

public class Reverser {

    public static final long A = 0x97be9f880aa9L;
    public static final long B = 0xeac471130bcaL;
    public static final NBitNumber[] BVEC = NBitNumber.vec(24,
            0x01_198a,
            0xff_822e,
            0xff_c780,
            0xff_4bfc,
            0x00_5c33,
            0xff_7b71,
            0x01_c5bf);
    public static final NBitNumber[] M6 = NBitNumber.vec(48,
            0xb63e_7d43_381eL,
            0x6358_f923_3700L,
            0x4d2a_7d3e_8316L,
            0x2249_96be_0fb1L,
            0x23a8_5e19_a87aL,
            0x3a17_0e00_293dL,
            0xff92_3748_f7acL);
    public static final NBitNumber[][] MM1 = NBitNumber.mat(16, new long[][] {
            {0x1a, 0x51, 0x10, 0x32, 0x0e, 0x16, 0x4c},
            {0x75, 0x04, 0x2a, 0x2d, 0x14, 0x3d, 0x20},
            {0x03, 0x4f, 0x01, 0x5c, 0x05, 0x1a, 0x08},
            {0x12, 0x1c, 0x30, 0x35, 0x5c, 0x21, 0x21},
            {0x2b, 0x3f, 0x44, 0x0a, 0x21, 0x12, 0x39},
            {0x15, 0x06, 0x5b, 0x13, 0x38, 0x33, 0x0c},
            {0x2c, 0x19, 0x06, 0x01, 0x2c, 0x41, 0x22}
    });
    public static final int[][] MM1_SIGNS = {
            {-1, -1, -1, +1, +1, +1, -1},
            {-1, +1, -1, -1, -1, -1, -1},
            {+1, +1, +1, +1, +1, +1, +1},
            {+1, +1, +1, +1, -1, -1, -1},
            {+1, +1, -1, -1, +1, +1, -1},
            {-1, -1, +1, -1, +1, +1, -1},
            {-1, +1, -1, -1, -1, +1, +1}
    };


    public static long reverse(Random rand, NBitNumber[] lut, boolean log) {
        final long initialSeed = MessMath.getSeed(rand);

        if (log) System.out.println("Angles:");
        int[] detectorArray = new int[7];
        for (int j = 0; j < 7; j++) {
            rand.setSeed((((((MessMath.getSeed(rand)*A+B)- 0xbL) * 0xdfe05bcb1365L)- 0xbL) * 0xdfe05bcb1365L)^0x5deece66dL);
            double angle = rand.nextDouble() * 2 * Math.PI;
            if (log) System.out.println("   " + angle);
            detectorArray[j] = TntSimulator.getDetectorOutput(angle);
        }
        NBitNumber[] lutOut = new NBitNumber[7];
        for (int k = 0; k < 7; k++) {
            int detectorId = detectorArray[k];
            int lastDetectorId = detectorId == 0 ? Detector.detectors.size() - 1 : detectorId - 1;
            lutOut[k] = lut[lastDetectorId];
        }

        NBitNumber[] penultimateVector = new NBitNumber[7];
        if (log) System.out.println("Before right-shift:");
        for (int j = 0; j < 7; j++) {
            // initialize to bvec
            NBitNumber val = BVEC[j];
            // add to val the dot product of lutOut with column j of mm1
            for (int k = 0; k < 7; k++) {
                NBitNumber product = lutOut[k].mul(MM1[k][j], 24);
                if (MM1_SIGNS[k][j] < 0) // handle signed product when increasing bit count; only mm1 is signed
                    product = new NBitNumber(0xffffff, 24).sub(product, 24);
                val = val.add(product, 24);
            }
            if (log) System.out.println("   " + Long.toHexString(val.val));
            val = val.shiftRight(48 - LUT.BITS_IGNORED, 48);
            // In reality val is currently 14-bit
            // Although we're using 24-bit val before the shift, in reality it's 21-bit which means we can
            // test the whole of the top hex digit after the shift for negativity.
            if ((val.val & 0xf000) != 0) // is val negative?
                val = new NBitNumber(0xffff_ffff_f000L | val.val, 48); // handle signed right shift

            penultimateVector[j] = val;
        }

        if (log) {
            System.out.println("Initial seed: " + Long.toHexString(initialSeed));
            System.out.print("LUT: ");
            for (NBitNumber n : lutOut)
                System.out.print(lutToBinary(n) + ", ");
            System.out.println();
            System.out.print("Penultimate: ");
            for (NBitNumber n : penultimateVector)
                System.out.print(Long.toHexString(n.val) + ", ");
            System.out.println();
        }
        NBitNumber seed = MessMath.dot(M6, penultimateVector, 48);
        if (log) System.out.println("Calculated Seed: " + Long.toHexString(seed.val));

        return seed.val;
    }

    private static String lutToBinary(NBitNumber lut) {
        StringBuilder sb = new StringBuilder(Long.toBinaryString(lut.val));
        while (sb.length() < 10)
            sb.insert(0, '0');
        return sb.toString();
    }

}
