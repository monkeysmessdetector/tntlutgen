import tntlutgen.*;

import java.util.Random;
import java.util.Scanner;

/**
 * Traces what reversal is supposed to look like for the given seed.
 * Used extensively for debugging the mess detector.
 */
public class ReversalTraceTool {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter starting seed (in hex): ");
        long initialSeed = Long.parseLong(scanner.nextLine(), 16);
        Random rand = new Random(initialSeed ^ 0x5deece66dL);

        Detector.generateDetectorCircle();
        NBitNumber[] lut = new NBitNumber[Detector.detectors.size()];
        for (int i = 0; i < lut.length; i++)
            lut[i] = new NBitNumber(LUT.getLUTValue(i), 10);

        Reverser.reverse(rand, lut, true);
        System.out.println("Actual seed: " + Long.toHexString(MessMath.getSeed(rand)));
    }

}
