public class SimConstantFinder {

    public static void main(String[] args) {
        final long mask = (1L << 48) - 1;
        final long base = (0x5deece66dL * 0x5deece66dL) & mask;
        final long baseAddend = (0x5deece66dL * 0xbL + 0xbL) & mask;
        long multiplier = base;
        long addend = baseAddend;
        int power = 1;
        int minBitCount = 49;

        while (power <= 3000 * 4) {
            int bitCount = Long.bitCount(multiplier);
            //if (bitCount < minBitCount) {
                minBitCount = bitCount;
                System.out.println(power + " " + toString(multiplier));
            //}
            multiplier = (multiplier * base) & mask;
            addend = (base * addend + baseAddend) & mask;
            power++;
        }
    }

    private static String toString(long val) {
        StringBuilder str = new StringBuilder(Long.toBinaryString(val));
        while (str.length() < 48)
            str.insert(0, "0");
        for (int i = str.length() - 12; i > 0; i -= 12)
            str.insert(i, " ");
        return str.toString();
    }

}
