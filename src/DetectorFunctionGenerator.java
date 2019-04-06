import tntlutgen.Detector;
import tntlutgen.LUT;
import tntlutgen.TntSimulator;

public class DetectorFunctionGenerator {

    public static void main(String[] args) {
        final int y = 54 - (int) TntSimulator.initialHeight;

        Detector.generateDetectorCircle();

        for (int i = 0; i < Detector.detectors.size(); i++) {
            Detector detector = Detector.detectors.get(i);
            System.out.println("# Detector " + i);
            System.out.printf("fill 0 %d %d %d %d %d ender_chest%n", y, detector.z + (int)Math.signum(detector.z), detector.x, y, (int)Math.signum(detector.z));
            System.out.printf("fill %d %d %d %d %d %d ender_chest%n", detector.x - 1, y, detector.z - 1, detector.x + 1, y, detector.z + 1);
            System.out.printf("setblock %d %d %d ender_chest%n", detector.x, y, detector.z);
            System.out.printf("setblock %d %d %d tripwire%n", detector.x, y + 1, detector.z);
            System.out.printf("setblock %d %d %d observer facing=down%n", detector.x, y + 2, detector.z);
            System.out.printf("setblock %d %d %d sandstone%n", detector.x, y + 3, detector.z);
            long upperBound = LUT.getLUTValue(i);
            System.out.printf("fill %d %d %d %d %d %d stone%n", detector.x, y + 4, detector.z, detector.x, y + 10, detector.z);
            System.out.printf("setblock %d %d %d command_block facing=up replace {Command:\"testfor @e[tag=detectorLock,score_detectorId%s=%d]\"}%n", detector.x, y + 4, detector.z, i == Detector.detectors.size() - 1 ? "" : "_min", (i+1) % Detector.detectors.size());
            System.out.printf("setblock %d %d %d chain_command_block facing=up replace {auto:1b,Command:\"testforblock ~ ~-1 ~ command_block * {SuccessCount:0}\"}%n", detector.x, y + 5, detector.z);
            System.out.printf("setblock %d %d %d chain_command_block facing=up,conditional=true replace {auto:1b,Command:\"testfor @e[tag=detectorsActive]\"}%n", detector.x, y + 6, detector.z);
            System.out.printf("setblock %d %d %d chain_command_block facing=up,conditional=true replace {auto:1b,Command:\"scoreboard players set lut messConstants %d\"}%n", detector.x, y + 7, detector.z, upperBound);
            System.out.printf("setblock %d %d %d chain_command_block facing=up,conditional=true replace {auto:1b,Command:\"scoreboard players set lutLast messConstants %d\"}%n", detector.x, y + 8, detector.z, LUT.getLUTValue((i==0 ? Detector.detectors.size()-1 : i-1)));
            System.out.printf("setblock %d %d %d chain_command_block facing=up,conditional=true replace {auto:1b,Command:\"summon area_effect_cloud ~ ~ ~ {Tags:[\\\"detectorLock\\\"]}\",Duration:2147483647}%n", detector.x, y + 9, detector.z);
            System.out.printf("setblock %d %d %d chain_command_block facing=up,conditional=true replace {auto:1b,Command:\"scoreboard players set @e[tag=detectorLock] detectorId %d\"}%n", detector.x, y + 10, detector.z, i);
        }

        Detector.detectors.clear();
        TntSimulator.launchTntCount--;
        Detector.generateDetectorCircle();

        System.out.println("# NO LAG PLZ!!!");
        for (Detector detector : Detector.detectors) {
            System.out.printf("fill 0 %d %d %d %d %d air%n", y, detector.z + (int) Math.signum(detector.z), detector.x, y, (int) Math.signum(detector.z));
        }
    }

}
