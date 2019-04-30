package tntlutgen;

public class NBitNumber {

    public final long val;
    public final int bits;

    public NBitNumber(long val, int bits) {
        this.val = val & ((1L << bits) - 1);
        this.bits = bits;
    }

    public NBitNumber add(NBitNumber other, int outBits) {
        return new NBitNumber(val + other.val, outBits);
    }

    public NBitNumber sub(NBitNumber other, int outBits) {
        return new NBitNumber(val - other.val, outBits);
    }

    public NBitNumber mul(NBitNumber other, int outBits) {
        return new NBitNumber(val * other.val, outBits);
    }

    public NBitNumber shiftRight(int amt, int outBits) {
        return new NBitNumber(val >>> amt, outBits);
    }

    public static NBitNumber[] vec(int bits, long... vals) {
        NBitNumber[] ret = new NBitNumber[vals.length];
        for (int i = 0; i < vals.length; i++)
            ret[i] = new NBitNumber(vals[i], bits);
        return ret;
    }

    public static NBitNumber[][] mat(int bits, long[][] mat) {
        NBitNumber[][] ret = new NBitNumber[mat.length][mat[0].length];
        for (int i = 0; i < mat.length; i++)
            for (int j = 0; j < mat[0].length; j++)
                ret[i][j] = new NBitNumber(mat[i][j], bits);
        return ret;
    }

}
