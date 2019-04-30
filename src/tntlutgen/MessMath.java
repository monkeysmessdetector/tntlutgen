package tntlutgen;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class MessMath {

    public static NBitNumber dotColumn(NBitNumber[] v, NBitNumber[][] mat, int col, int bits) {
        NBitNumber result = new NBitNumber(0, bits);
        for (int i = 0; i < v.length; i++) {
            result = result.add(v[i], bits).mul(mat[i][col], bits);
        }
        return result;
    }

    public static NBitNumber dot(NBitNumber[] a, NBitNumber[] b, int outBits) {
        NBitNumber result = new NBitNumber(0, outBits);
        for (int i = 0; i < a.length; i++)
            result = result.add(a[i].mul(b[i], outBits), outBits);
        return result;
    }

    public static long getSeed(Random rand) {
        try {
            Field field = Random.class.getDeclaredField("seed");
            field.setAccessible(true);
            return ((AtomicLong) field.get(rand)).get();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String toBinaryString(long l) {
        StringBuilder str = new StringBuilder(Long.toBinaryString(l));
        while (str.length() < 10)
            str.insert(0, "0");
        return str.toString();
    }

    public static long circularDiff(long a, long b) {
        long da = (b - a) % (1L << 48);
        long diff = 2 * da % (1L << 48) - da;
        return diff;
    }

}
