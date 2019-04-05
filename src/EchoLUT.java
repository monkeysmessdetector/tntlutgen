import tntlutgen.Detector;
import tntlutgen.LUT;
import tntlutgen.MessMath;

public class EchoLUT {

    public static void main(String[] args) {
        Detector.generateDetectorCircle();

        long largestDifference = 0;

        long lastSeed = 0;
        for (int detectorId = 0; detectorId < Detector.detectors.size(); detectorId++) {
            long seed = LUT.getUpperSeedBound(detectorId);
            System.out.printf("%03d: %s%n", detectorId, MessMath.toBinaryString(seed));
            if (seed - lastSeed > largestDifference)
                largestDifference = seed - lastSeed;
            lastSeed = seed;
        }
        System.out.println("Largest difference: " + largestDifference);
        System.out.println("Log of that: " + Math.log(largestDifference) / Math.log(2));
        System.out.println("Circle radius: " + -Detector.detectors.get(0).z);

        Detector.renderDetectors();
    }

}
