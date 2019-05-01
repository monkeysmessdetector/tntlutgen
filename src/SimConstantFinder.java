public class SimConstantFinder {

    public static void main(String[] args) {
        final long mask = (1L << 48) - 1;
        final long base = (0x5deece66dL * 0x5deece66dL) & mask;
        long constant = base;
        int power = 1;
        int minBitCount = 49;

        while (true) {
            int bitCount = Long.bitCount(constant);
            if (bitCount < minBitCount) {
                minBitCount = bitCount;
                System.out.println(power + " (" + (power / 4) + " items): " + Long.toBinaryString(constant));
            }
            constant = (constant * base) & mask;
            power++;
        }
    }

}
