import tntlutgen.Detector;
import tntlutgen.LUT;
import tntlutgen.TntSimulator;

public class DetectorFunctionGenerator {

    public static void main(String[] args) {
        Detector.generateDetectorCircle();

        for (int i = 0; i < Detector.detectors.size(); i++) {
            Detector detector = Detector.detectors.get(i);
            System.out.println("# Detector " + i);
            System.out.printf("fill 0 28 %d %d 28 %d ender_chest%n", detector.z + (int)Math.signum(detector.z), detector.x, (int)Math.signum(detector.z));
            System.out.printf("fill %d 28 %d %d 28 %d ender_chest%n", detector.x - 1, detector.z - 1, detector.x + 1, detector.z + 1);
            System.out.printf("setblock %d 29 %d tripwire%n", detector.x, detector.z);
            System.out.printf("setblock %d 30 %d observer facing=down%n", detector.x, detector.z);
            System.out.printf("setblock %d 31 %d sandstone%n", detector.x, detector.z);
            long upperBound = LUT.getUpperSeedBound(i);
            System.out.printf("fill %d 32 %d %d 38 %d stone%n", detector.x, detector.z, detector.x, detector.z);
            System.out.printf("setblock %d 32 %d command_block facing=up replace {Command:\"testfor @e[tag=detectorLock,score_detectorId%s=%d]\"}%n", detector.x, detector.z, i == Detector.detectors.size() - 1 ? "" : "_min", (i+1) % Detector.detectors.size());
            System.out.printf("setblock %d 33 %d chain_command_block facing=up replace {auto:1b,Command:\"testforblock ~ ~-1 ~ command_block * {SuccessCount:0}\"}%n", detector.x, detector.z);
            System.out.printf("setblock %d 34 %d chain_command_block facing=up,conditional=true replace {auto:1b,Command:\"testfor @e[tag=detectorsActive]\"}%n", detector.x, detector.z);
            System.out.printf("setblock %d 35 %d chain_command_block facing=up,conditional=true replace {auto:1b,Command:\"scoreboard players set lut messConstants %d\"}%n", detector.x, detector.z, upperBound);
            System.out.printf("setblock %d 36 %d chain_command_block facing=up,conditional=true replace {auto:1b,Command:\"scoreboard players set lutLast messConstants %d\"}%n", detector.x, detector.z, LUT.getUpperSeedBound((i==0 ? Detector.detectors.size()-1 : i-1)));
            System.out.printf("setblock %d 37 %d chain_command_block facing=up,conditional=true replace {auto:1b,Command:\"summon area_effect_cloud ~ ~ ~ {Tags:[\\\"detectorLock\\\"]}\",Duration:2147483647}%n", detector.x, detector.z);
            System.out.printf("setblock %d 38 %d chain_command_block facing=up,conditional=true replace {auto:1b,Command:\"scoreboard players set @e[tag=detectorLock] detectorId %d\"}%n", detector.x, detector.z, i);
        }

        Detector.detectors.clear();
        TntSimulator.initialHeight = 17;
        TntSimulator.launchTntCount = 6;
        Detector.generateDetectorCircle();

        System.out.println("# NO LAG PLZ!!!");
        for (Detector detector : Detector.detectors) {
            System.out.printf("fill 0 28 %d %d 28 %d air%n", detector.z + (int) Math.signum(detector.z), detector.x, (int) Math.signum(detector.z));
        }
    }

}
