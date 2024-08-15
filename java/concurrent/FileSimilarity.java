import java.io.*;
import java.util.*;
import java.util.concurrent.Semaphore;

public class FileSimilarity {

    private static Map<String, List<Long>> fileFingerprints = new HashMap<>();;

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: java Sum filepath1 filepath2 filepathN");
            System.exit(1);
        }

        for (int i = 0; i < 5; i++) {
            
        }

        // Calculate the fingerprint for each file
        for (String path : args) {
            // List<Long> fingerprint = fileSum(path);
            FileSumThread fileSumThread = new FileSumThread(path);
            fileSumThread.start();
        }

        

        // Compare each pair of files
        for (int i = 0; i < args.length; i++) {
            for (int j = i + 1; j < args.length; j++) {
                String file1 = args[i];
                String file2 = args[j];
                List<Long> fingerprint1 = fileFingerprints.get(file1);
                List<Long> fingerprint2 = fileFingerprints.get(file2);
                float similarityScore = similarity(fingerprint1, fingerprint2);
                System.out.println("Similarity between " + file1 + " and " + file2 + ": " + (similarityScore * 100) + "%");
            }
        }
    }

    static class FileSumThread extends Thread {

        private String filePath;
        Semaphore mutex = new Semaphore(1);
        Map<String, List<Long>> fingerprints;

        public FileSumThread(String filePath) {
            this.filePath = filePath;
        }

        @Override
        public void run() {
            try {
                this.fileSum();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        private void fileSum() throws IOException, InterruptedException {
            mutex.acquire();
            File file = new File(this.filePath);
            List<Long> chunks = new ArrayList<>();
            try (FileInputStream inputStream = new FileInputStream(file)) {
                byte[] buffer = new byte[100];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    long sum = sum(buffer, bytesRead);
                    chunks.add(sum);
                }
            } catch (Exception e) {            System.out.println(chunks);

                System.out.println(e.getMessage());
            }
            System.out.println(this.filePath);
            // System.out.println(chunks);
            fileFingerprints.put(this.filePath, chunks);
            System.out.println(fileFingerprints.get(this.filePath));
            mutex.release();
        }

        private static long sum(byte[] buffer, int length) {
            long sum = 0;
            for (int i = 0; i < length; i++) {
                sum += Byte.toUnsignedInt(buffer[i]);
            }
            return sum;
        }

    }



    private static float similarity(List<Long> base, List<Long> target) {
        int counter = 0;
        List<Long> targetCopy = new ArrayList<>(target);

        for (Long value : base) {
            if (targetCopy.contains(value)) {
                counter++;
                targetCopy.remove(value);
            }
        }

        return (float) counter / base.size();
    }
}
