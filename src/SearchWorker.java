import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A single worker thread's search task. Each worker scans its assigned
 * SearchRange of candidate indices, converts each index to a password
 * candidate, hashes it, and compares it against the target MD5 hash.
 *
 * Thread coordination / synchronization:
 *  - `found` is a shared AtomicBoolean checked at the start of every loop
 *    iteration. As soon as one worker sets it to true, every other worker
 *    observes it on its next iteration and returns promptly. No locks are
 *    needed because AtomicBoolean gives a safe, non-blocking, visibility-
 *    guaranteed flag shared across threads.
 *  - `found.compareAndSet(false, true)` guarantees that even if two threads
 *    find a match at (almost) the same instant, only one of them "wins" and
 *    records the result, avoiding a race on foundPassword/foundThreadId.
 *  - `foundPassword` / `foundThreadId` are an AtomicReference/AtomicInteger
 *    so the winning thread's result is safely published back to main.
 */
public class SearchWorker implements Runnable {

    private final SearchRange range;
    private final String targetHash;
    private final int passwordLength;
    private final AtomicBoolean found;
    private final AtomicReference<String> foundPassword;
    private final AtomicInteger foundThreadId;
    private long candidatesChecked = 0;

    public SearchWorker(SearchRange range,
                         String targetHash,
                         int passwordLength,
                         AtomicBoolean found,
                         AtomicReference<String> foundPassword,
                         AtomicInteger foundThreadId) {
        this.range = range;
        this.targetHash = targetHash;
        this.passwordLength = passwordLength;
        this.found = found;
        this.foundPassword = foundPassword;
        this.foundThreadId = foundThreadId;
    }

    @Override
    public void run() {
        for (long index = range.getStartIndex(); index < range.getEndIndex(); index++) {
            // Check the shared flag first so other threads stop promptly
            // once ANY thread has found the password.
            if (found.get()) {
                return;
            }

            String candidate = MD5Crack.indexToPassword(index, passwordLength);
            candidatesChecked++;

            if (MD5Crack.getMd5(candidate).equals(targetHash)) {
                if (found.compareAndSet(false, true)) {
                    foundPassword.set(candidate);
                    foundThreadId.set(range.getThreadId());
                }
                return;
            }
        }
    }

    public long getCandidatesChecked() {
        return candidatesChecked;
    }
}