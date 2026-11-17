import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for the MD5 brute-force search.
 *
 * Provides:
 *  - getMd5(String): compute the MD5 hash of a string as a 32-char hex string.
 *  - indexToPassword(long, int): deterministically map an index
 *    (0 .. 95^length - 1) to the corresponding password candidate, using the
 *    95 printable ASCII characters (decimal 32 to 126).
 *  - totalSearchSpace(int): compute 95^length as a long.
 *
 * Candidate generation order:
 *  Each password position is treated as a "digit" in base 95, where digit 0
 *  corresponds to ASCII 32 (space) and digit 94 corresponds to ASCII 126 ('~').
 *  The LAST character of the password is the least-significant digit, so it
 *  changes fastest as the index increases - this mirrors the innermost loop
 *  in the original sequential brute-force code (Appendix A), generalised to
 *  any password length. This order is applied consistently everywhere, which
 *  is what makes index-based range partitioning safe.
 */
public final class MD5Crack {

    public static final int ASCII_MIN = 32;   // ' '
    public static final int ASCII_MAX = 126;  // '~'
    public static final int CHARSET_SIZE = ASCII_MAX - ASCII_MIN + 1; // 95

    private MD5Crack() {
        // utility class - no instances
    }

    /** Computes the MD5 hash of the given input, as a lowercase, zero-padded 32-char hex string. */
    public static String getMd5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /** Total number of possible passwords of the given length over the 95-character set (95^length). */
    public static long totalSearchSpace(int length) {
        long total = 1L;
        for (int i = 0; i < length; i++) {
            total *= CHARSET_SIZE;
        }
        return total;
    }

    /**
     * Deterministically converts an index in [0, 95^length) into the
     * corresponding password candidate ("base-95 odometer" decoding). This is
     * what lets each worker thread be handed a contiguous sub-range of
     * indices [startIndex, endIndex) instead of generating candidates
     * sequentially from zero.
     */
    public static String indexToPassword(long index, int length) {
        char[] chars = new char[length];
        for (int pos = length - 1; pos >= 0; pos--) {
            int digit = (int) (index % CHARSET_SIZE);
            chars[pos] = (char) (ASCII_MIN + digit);
            index /= CHARSET_SIZE;
        }
        return new String(chars);
    }
}