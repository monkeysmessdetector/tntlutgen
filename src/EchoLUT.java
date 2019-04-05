import tntlutgen.Detector;
import tntlutgen.LUT;
import tntlutgen.MessMath;

public class EchoLUT {

    public static void main(String[] args) {
        Detector.generateDetectorCircle();

        long largestDifference = 0;

        long lowerBound = 0;
        for (int detectorId = 0; detectorId < Detector.detectors.size(); detectorId++) {
            long upperBound = LUT.getLUTValue(detectorId);
            System.out.printf("%03d: %s%n", detectorId, MessMath.toBinaryString(upperBound));
            upperBound = (upperBound << LUT.BITS_IGNORED) | ((1L << LUT.BITS_IGNORED) - 1);
            System.out.println(Math.log(upperBound) / Math.log(2) + " " + Math.log(lowerBound) / Math.log(2));
            if (upperBound - lowerBound > largestDifference)
                largestDifference = upperBound - lowerBound;
            lowerBound = upperBound & ~((1L << LUT.BITS_IGNORED) - 1);
        }
        System.out.println("Largest difference: " + largestDifference);
        System.out.println("Log of that: " + Math.log(largestDifference) / Math.log(2));
        System.out.println("Circle radius: " + -Detector.detectors.get(0).z);

        Detector.renderDetectors();
    }

}
