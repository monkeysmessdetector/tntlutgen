package tntlutgen;

public class LUT {

    public static final int BITS_IGNORED = 38; //39 will likely also guarantee 100% success rate. This number must be larger than 22

    public static long getLUTValue(int detectorId) {
        double upperAngle = getUpperAngleBound(detectorId);
        // long upperSeed = (long) (upperAngle / (2 * Math.PI) * 0x1.0p26) << (48 - 26);
        // upperSeed |= ((1L << (BITS_IGNORED)) - 1);
        long upperSeed = (long) (upperAngle / (2 * Math.PI) * 0x1.0p26) >> (-48 + 26 + BITS_IGNORED);
        return upperSeed;
    }

    private static double getUpperAngleBound(int detectorId) {
        final double EPSILON = 0x1.0p-28 * Math.PI;

        /*int lastDetector = 0;
        for (double i = 0;i<2* Math.PI;i+=EPSILON) {
            if (getDetectorOutput(i)< lastDetector) {
                System.out.println(getDetectorOutput(i));
            }
            lastDetector = getDetectorOutput(i);
        }*/

        Detector detector = Detector.detectors.get(detectorId < 2 ? detectorId - 2 + Detector.detectors.size() : detectorId - 2);
        double lower = Math.atan2(-detector.x, -detector.z);

        detector = Detector.detectors.get((detectorId + 2) % Detector.detectors.size());
        double upper = Math.atan2(-detector.x, -detector.z);

        while (Math.abs(upper - lower) > EPSILON) {
            double da = (upper - lower) % (Math.PI * 2);
            double diff = (2 * da) % (Math.PI * 2) - da;
            double mid = lower + diff * 0.5;
            int actualDetector = TntSimulator.getDetectorOutput(mid);
            int dd = (actualDetector - detectorId) % Detector.detectors.size();
            int detectorDiff = 2 * dd % Detector.detectors.size() - dd;
            if (detectorDiff <= 0)
                lower = mid;
            else
                upper = mid;
        }
        if (upper*lower< 0) {
            System.out.println(upper+" "+lower);
        }
        return upper < 0 ? upper + Math.PI * 2 : upper;
    }

}
