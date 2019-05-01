public class SimConstantFinder {

    public static void main(String[] args) {
        final long mask = (1L << 48) - 1;
        final long base = (0x5deece66dL * 0x5deece66dL) & mask;
        final long baseAddend = (0x5deece66dL * 0xbL + 0xbL) & mask;
        long multiplier = base;
        long addend = baseAddend;
        int power = 1;
        int minBitCount = 49;

        while (true) {
            int bitCount = Long.bitCount(multiplier);
            if (bitCount < minBitCount) {
                minBitCount = bitCount;
                System.out.println(power + " (" + (power / 4) + " items): " + Long.toBinaryString(multiplier) + " plus " + Long.toHexString(addend));
            }
            multiplier = (multiplier * base) & mask;
            addend = (base * addend + baseAddend) & mask;
            power++;
        }
    }

}
