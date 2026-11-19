/* Represents a contiguous, non-overlapping sub-range [startIndex, endIndex) of the total search space, assigned to a single worker thread.
 */
public class SearchRange {

    private final int threadId;
    private final long startIndex; // inclusive
    private final long endIndex;   // exclusive

    public SearchRange(int threadId, long startIndex, long endIndex) {
        this.threadId = threadId;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public int getThreadId() {
        return threadId;
    }

    public long getStartIndex() {
        return startIndex;
    }

    public long getEndIndex() {
        return endIndex;
    }

    public long size() {
        return endIndex - startIndex;
    }

    /* dividing total search space into contiguous sub-ranges for each thread to figure out the baseline number of passwords every thread should be assigned */
     
    public static SearchRange[] partition(long totalSpace, int numThreads) {
        SearchRange[] ranges = new SearchRange[numThreads];
        long baseChunk = totalSpace / numThreads;
        long remainder = totalSpace % numThreads;

        long start = 0;
        for (int i = 0; i < numThreads; i++) {
            long size = baseChunk + (i < remainder ? 1 : 0); 
            long end = start + size;
            ranges[i] = new SearchRange(i, start, end);
            start = end;
        }
        return ranges;
    }

    @Override
    public String toString() {
        return "Thread " + threadId + ": [" + startIndex + ", " + endIndex + ") size=" + size();
    }
}
