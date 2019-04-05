package tntlutgen;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Detector {

    public static List<Detector> detectors;

    public final int x, z;

    public Detector(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public static void generateDetectorCircle() {
        detectors = new ArrayList<>();

        double radius = -TntSimulator.simulateTntLaunch(0).z;
        int x = (int) (radius + 0.49 - 0.001);
        int z = 0;
        while (x >= z) {
            double dx = x - 0.99 - 0.001;
            double dz = z + 0.01 + 0.001;
            double dx2 = radius * radius - dz * dz;
            if (dx2 < dx * dx)
                x--;
            detectors.add(new Detector(x, z));
            detectors.add(new Detector(-z, x));
            detectors.add(new Detector(-x, -z));
            detectors.add(new Detector(z, -x));
            if (z != 0 && z != x) {
                detectors.add(new Detector(z, x));
                detectors.add(new Detector(-x, z));
                detectors.add(new Detector(-z, -x));
                detectors.add(new Detector(x, -z));
            }
            z++;
        }

        detectors.sort(Comparator.comparingDouble(detector -> {
            double angle = Math.atan2(-detector.x, -detector.z);
            if (angle < 0) angle += Math.PI * 2;
            return angle;
        }));
    }

    public static void renderDetectors() {
        final int CELL_SIZE = 16;
        int radius = -detectors.get(0).z;
        BufferedImage image = new BufferedImage((2 * radius + 1) * CELL_SIZE, (2 * radius + 1) * CELL_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());

        g.setColor(Color.GREEN);
        for (Detector detector : detectors) {
            g.fillRect((detector.x + radius) * CELL_SIZE, (detector.z + radius) * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }

        g.setColor(Color.BLUE);
        g.fillRect(radius * CELL_SIZE, radius * CELL_SIZE, CELL_SIZE, CELL_SIZE);

        g.setColor(Color.BLACK);
        for (int i = 0; i < 2 * radius; i++) {
            g.drawLine((i + 1) * CELL_SIZE, 0, (i + 1) * CELL_SIZE, image.getHeight());
            g.drawLine(0, (i + 1) * CELL_SIZE, image.getWidth(), (i + 1) * CELL_SIZE);
        }

        g.setColor(Color.RED);
        double preciseRadius = -TntSimulator.simulateTntLaunch(0).z;
        g.drawOval((int) ((0.01 - preciseRadius + radius) * CELL_SIZE), (int) ((0.01 - preciseRadius + radius) * CELL_SIZE), (int) ((2 * preciseRadius) * CELL_SIZE), (int) ((2 * preciseRadius) * CELL_SIZE));
        g.drawOval((int) ((0.01 - preciseRadius + radius) * CELL_SIZE), (int) ((0.99 - preciseRadius + radius) * CELL_SIZE), (int) ((2 * preciseRadius) * CELL_SIZE), (int) ((2 * preciseRadius) * CELL_SIZE));
        g.drawOval((int) ((0.99 - preciseRadius + radius) * CELL_SIZE), (int) ((0.01 - preciseRadius + radius) * CELL_SIZE), (int) ((2 * preciseRadius) * CELL_SIZE), (int) ((2 * preciseRadius) * CELL_SIZE));
        g.drawOval((int) ((0.99 - preciseRadius + radius) * CELL_SIZE), (int) ((0.99 - preciseRadius + radius) * CELL_SIZE), (int) ((2 * preciseRadius) * CELL_SIZE), (int) ((2 * preciseRadius) * CELL_SIZE));
        g.dispose();

        try {
            ImageIO.write(image, "PNG", new File("circle.png"));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        try {
            Desktop.getDesktop().open(new File("circle.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
