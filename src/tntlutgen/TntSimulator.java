package tntlutgen;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class TntSimulator {

    public static double initialHeight = 18;
    public static int launchTntCount = 7;
    private static final double FLOOR_HEIGHT = 0.875 - 1;

    public static int getDetectorOutput(double angle) {
        Set<Detector> activatedDetectors = new HashSet<>();
        TntEntity finalTnt = simulateTntLaunch(angle, tnt -> {
            double minX = tnt.x - 0.49 + 0.001, maxX = tnt.x + 0.49 - 0.001;
            double minZ = tnt.z - 0.49 + 0.001, maxZ = tnt.z + 0.49 - 0.001;
            for (Detector detector : Detector.detectors) {
                boolean intersects = minX < detector.x + 1 && maxX >= detector.x && minZ < detector.z + 1 && maxZ >= detector.z;
                if (intersects)
                    activatedDetectors.add(detector);
            }
        });
        double minX = finalTnt.x - 0.49 + 0.001, maxX = finalTnt.x + 0.49 - 0.001;
        double minZ = finalTnt.z - 0.49 + 0.001, maxZ = finalTnt.z + 0.49 - 0.001;
        for (Detector detector : Detector.detectors) {
            boolean intersects = minX < detector.x + 1 && maxX >= detector.x && minZ < detector.z + 1 && maxZ >= detector.z;
            if (intersects)
                activatedDetectors.add(detector);
        }

        int detectorId = 0;
        boolean foundDetector = false;
        while (true) {
            if (activatedDetectors.contains(Detector.detectors.get(detectorId)))
                foundDetector = true;
            else if (foundDetector)
                return detectorId == 0 ? Detector.detectors.size() - 1 : detectorId - 1;
            detectorId = (detectorId + 1) % Detector.detectors.size();
        }
    }

    // Simulates TNT being fired upwards from a dispenser at (0.5, initialHeight, 0.5) and being blasted by launchTntCount TNT
    public static TntEntity simulateTntLaunch(double initialAngle) {
        return simulateTntLaunch(initialAngle, tnt -> {});
    }

    public static TntEntity simulateTntLaunch(double initialAngle, Consumer<TntEntity> tickHandler) {
        TntEntity tnt = new TntEntity();
        tnt.x = 0.5;
        tnt.y = initialHeight;
        tnt.z = 0.5;
        tnt.vx = -Math.sin(initialAngle) * 0.02;
        tnt.vy = 0.2;
        tnt.vz = -Math.cos(initialAngle) * 0.02;

        for (int i = 0; i < 29; i++) {
            tickHandler.accept(tnt);
            tnt.update(initialHeight);
        }

        double dx = tnt.x - 0.5;
        double dy = -0.98/16;
        double dz = tnt.z - 0.5;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double multiplier = (1 - (distance / 8)) / distance;
        tnt.vx += dx * multiplier * launchTntCount;
        tnt.vy += dy * multiplier * launchTntCount;
        tnt.vz += dz * multiplier * launchTntCount;

        for (int i = 0; i < 80 - 29 - 1; i++) {
            double minX = tnt.x - 0.49, maxX = tnt.x + 0.49;
            double minZ = tnt.z - 0.49, maxZ = tnt.z + 0.49;
            boolean offDispenser = maxX < 0 || minX > 1 || maxZ < 0 || minZ > 1;
            tickHandler.accept(tnt);
            tnt.update(offDispenser ? FLOOR_HEIGHT : initialHeight);
        }

        return tnt;
    }

    public static class TntEntity {
        public double x, y, z, vx, vy, vz;

        void update(double floorHeight) {
            vy -= 0.04;

            double dx = vx, dy = vy, dz = vz;
            double prevVy = vy;
            if (y + dy < floorHeight)
                dy = floorHeight - y;
            y += dy;
            x += dx;
            z += dz;
            boolean onGround = dy != prevVy && prevVy < 0;
            if (dy != prevVy)
                vy = 0;

            vx *= 0.98;
            vy *= 0.98;
            vz *= 0.98;
            if (onGround) {
                vx *= 0.7;
                vz *= 0.7;
                vy *= -0.5;
            }
        }
    }

}
