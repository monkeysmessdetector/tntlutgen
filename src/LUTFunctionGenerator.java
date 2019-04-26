import tntlutgen.Detector;
import tntlutgen.LUT;

public class LUTFunctionGenerator {

    public static void main(String[] args) {
        Detector.generateDetectorCircle();

        for (int detectorId = 0; detectorId < Detector.detectors.size(); detectorId++) {
            Detector detector = Detector.detectors.get(detectorId);
            int x, z, dx, dz;
            if (Math.abs(detector.x) > Math.abs(detector.z)) {
                dx = detector.x < 0 ? -1 : 1;
                dz = 0;
                x = 147 * dx;
                z = detector.z;
            } else {
                dx = 0;
                dz = detector.z < 0 ? -1 : 1;
                x = detector.x;
                z = 147 * dz;
            }
            placeValue(x, 40, z, dx, dz, LUT.getLUTValue(detectorId == 0 ? Detector.detectors.size() - 1 : detectorId - 1));
        }
    }

    private static void placeValue(int x, int y, int z, int dx, int dz, long lut) {
        for (int i = 0; i < 10; i++) {
            System.out.printf("setblock %d %d %d %s%n", x, y, z, (lut & 1) != 0 ? "observer facing=down" : "sandstone");
            lut >>= 1;
            x += dx * 2;
            z += dz * 2;
        }
    }

}
