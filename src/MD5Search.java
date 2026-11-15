
public class MD5Search {

    public static void main(String[] args) {

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
