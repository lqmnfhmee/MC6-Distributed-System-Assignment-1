<<<<<<< HEAD
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * CLI entry point for the multithreaded MD5 brute-force search.
 *
 * Usage:
 *   java MD5Search <hash> <threads> <passwordLength>
 *
 * Example:
 *   java MD5Search a7897b68a0645cd54c92fcf496934eb9 4 2
 */
=======

>>>>>>> 1e0a8bfbb3740333075b29388cc639b99a62ae2e
public class MD5Search {

    public static void main(String[] args) {

<<<<<<< HEAD
        // ---- 1. Input validation ----
        if (args.length != 3) {
            System.err.println("Usage: java MD5Search <hash> <threads> <passwordLength>");
            return;
        }

        String targetHash = args[0].trim().toLowerCase();
        int numThreads;
        int passwordLength;

        if (!targetHash.matches("[0-9a-f]{32}")) {
            System.err.println("Error: hash value must be a 32-character hexadecimal MD5 hash.");
            return;
        }

        try {
            numThreads = Integer.parseInt(args[1].trim());
        } catch (NumberFormatException e) {
            System.err.println("Error: number of threads must be an integer.");
            return;
        }
        if (numThreads < 1 || numThreads > 10) {
            System.err.println("Error: number of threads must be between 1 and 10.");
            return;
        }

        try {
            passwordLength = Integer.parseInt(args[2].trim());
        } catch (NumberFormatException e) {
            System.err.println("Error: password length must be an integer.");
            return;
        }
        if (passwordLength < 1 || passwordLength > 6) {
            System.err.println("Error: password length must be between 1 and 6 for this assignment.");
            return;
        }

        // ---- 2. Partition the search space ----
        long totalSpace = MD5Crack.totalSearchSpace(passwordLength);
        // Never use more threads than there are candidates (edge case for tiny spaces).
        int effectiveThreads = (int) Math.min(numThreads, totalSpace);
        SearchRange[] ranges = SearchRange.partition(totalSpace, effectiveThreads);

        System.out.println("Target hash       : " + targetHash);
        System.out.println("Password length   : " + passwordLength);
        System.out.println("Threads requested : " + numThreads);
        System.out.println("Search space size : " + totalSpace);
        for (SearchRange r : ranges) {
            System.out.println("  " + r);
        }

        // ---- 3. Shared coordination state ----
        AtomicBoolean found = new AtomicBoolean(false);
        AtomicReference<String> foundPassword = new AtomicReference<>(null);
        AtomicInteger foundThreadId = new AtomicInteger(-1);

        Thread[] threads = new Thread[ranges.length];
        SearchWorker[] workers = new SearchWorker[ranges.length];

        // ---- 4. Launch worker threads ----
        long startTime = System.nanoTime();

        for (int i = 0; i < ranges.length; i++) {
            workers[i] = new SearchWorker(ranges[i], targetHash, passwordLength,
                    found, foundPassword, foundThreadId);
            threads[i] = new Thread(workers[i], "Worker-" + i);
            threads[i].start();
        }

        // ---- 5. Wait for all worker threads to finish before reporting time ----
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Main thread interrupted while waiting for workers.");
            }
        }

        long endTime = System.nanoTime();
        double elapsedSeconds = (endTime - startTime) / 1_000_000_000.0;

        // ---- 6. Report results ----
        if (found.get()) {
            System.out.println("Password found     : " + foundPassword.get());
            System.out.println("Found by thread ID : " + foundThreadId.get());
        } else {
            System.out.println("Password NOT found within the given search space.");
        }
        System.out.printf("Total execution time: %.4f seconds%n", elapsedSeconds);
    }
}
=======
        // Step 1: Check number of arguments
        if (args.length != 3) { //argument validation (three arguments)
            System.out.println("Error: Invalid number of arguments.");
            System.out.println("Usage: java MD5Search <hash> <threads> <passwordLength>");
            return;
        }

        // Step 2: Get user inputs
        String targetHash = args[0];
        String threadInput = args[1];
        String lengthInput = args[2];

        // Step 3: Validate MD5 hash
        if (!targetHash.matches("[a-fA-F0-9]{32}")) { //MD5 validation (check the hash contains 32 hexadecimal chars)
            System.out.println("Error: Invalid MD5 hash.");
            return;
        }

        // Step 4: Validate thread count
        int numberOfThreads;
        try {
            numberOfThreads = Integer.parseInt(threadInput);
        } catch (NumberFormatException e) {
            System.out.println("Error: Thread count must be an integer.");
            return;
        }

        if (numberOfThreads < 1 || numberOfThreads > 10) { //support 1-10 threads
            System.out.println("Error: Thread count must be between 1 and 10.");
            return;
        }

        // Step 5: Validate password length
        int passwordLength;
        try {
            passwordLength = Integer.parseInt(lengthInput);
        } catch (NumberFormatException e) {
            System.out.println("Error: Password length must be an integer.");
            return;
        }

        if (passwordLength < 2 || passwordLength > 6) {
            System.out.println("Error: Password length must be between 2 and 6.");
            return;
        }

        // Step 6: Display inputs
        System.out.println("========== MD5 PASSWORD SEARCH ==========");
        System.out.println("Target Hash      : " + targetHash.toLowerCase());
        System.out.println("Number of Threads: " + numberOfThreads);
        System.out.println("Password Length  : " + passwordLength);
        System.out.println("=========================================");

    }
}
>>>>>>> 1e0a8bfbb3740333075b29388cc639b99a62ae2e
