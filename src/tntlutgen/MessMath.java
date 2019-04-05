package tntlutgen;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class MessMath {

    public static BigInteger toBigInt48(long n) {
        if ((n & 0x800000000000L) != 0)
            return BigInteger.valueOf(n | 0xffff000000000000L);
        else
            return BigInteger.valueOf(n & 0xffffffffffffL);
        //return BigInteger.valueOf(n);
    }

    public static BigInteger dotBig(long[] v, long[][] mat, int col) {
        BigInteger result = BigInteger.ZERO;
        for (int i = 0; i < v.length; i++) {
            result = result.add(BigInteger.valueOf(v[i]).multiply(toBigInt48(mat[i][col])));
        }
        return result;
    }

    public static long dot(long[] a, long[] b) {
        long result = 0;
        for (int i = 0; i < a.length; i++)
            result += a[i] * b[i];
        return result;
    }

    public static long[] matmult(long[] vec, long[][] mat) {
        long[] result = new long[mat.length];
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat.length; j++) {
                result[i] += vec[j] * mat[j][i];
            }
        }
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
        while (str.length() < 48)
            str.insert(0, "0");
        return str.toString();
    }

    public static long circularDiff(long a, long b) {
        long da = (b - a) % (1L << 48);
        long diff = 2 * da % (1L << 48) - da;
        return diff;
    }

}
