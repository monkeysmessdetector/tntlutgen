import tntlutgen.Detector;
import tntlutgen.TntSimulator;

import java.util.HashMap;
import java.util.Map;

public class DetectorFunctionGenerator {

    public static void main(String[] args) {
        final int y = 54 - (int) TntSimulator.initialHeight;

        Detector.generateDetectorCircle();

        System.out.println("carpet newLight true");

        for (Detector detector : Detector.detectors) {
            System.out.printf("fill 0 %d %d %d %d %d sandstone%n", y-1, detector.z + (int)Math.signum(detector.z), detector.x, y-1, (int)Math.signum(detector.z));
            System.out.printf("fill 0 %d %d %d %d %d snow_layer layers=8%n", y, detector.z + (int)Math.signum(detector.z), detector.x, y, (int)Math.signum(detector.z));
        }

        for (int i = 0; i < Detector.detectors.size(); i++) {
            Detector detector = Detector.detectors.get(i);
            System.out.println("# Detector " + i);
            System.out.printf("fill %d %d %d %d %d %d ender_chest%n", detector.x - 1, y, detector.z - 1, detector.x + 1, y, detector.z + 1);
            System.out.printf("setblock %d %d %d tripwire%n", detector.x, y + 1, detector.z);
            System.out.printf("setblock %d %d %d observer facing=down%n", detector.x, y + 2, detector.z);
            String facing = getFacing(detector);
            System.out.printf("setblock %d %d %d golden_rail shape=%s%n", detector.x, y + 3, detector.z, facing.equals("north") || facing.equals("south") ? "north_south" : "east_west");
        }

        System.out.println("carpet newLight false");
    }

    private static Map<String, String> oppositeFacing = new HashMap<>();
    static {
        oppositeFacing.put("north", "south");
        oppositeFacing.put("south", "north");
        oppositeFacing.put("west", "east");
        oppositeFacing.put("east", "west");
    }

    private static String getFacing(Detector detector) {
        double angle = Math.atan2(-detector.x, -detector.z);
        if (angle >= 0.25 * Math.PI && angle < 0.75 * Math.PI)
            return "west";
        else if (angle >= 0.75 * Math.PI && angle < 1.25 * Math.PI)
            return "south";
        else if (angle >= 1.25 * Math.PI && angle < 1.75 * Math.PI)
            return "east";
        else
            return "north";
    }

}
