package thread.optimisation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.BatchUpdateException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static final String SOURCE_FILE = "./resources/many-flowers.jpg";
    public static final String DESTINATION_FILE = "./out/many-flowers.jpg";

    public static void main(String[] args) throws IOException {
        // read the original image from the source path
        BufferedImage originalImage = ImageIO.read(new File(SOURCE_FILE));
        BufferedImage resultImage =  new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);

        // benchmarking performance for single thread
        long startTime = System.currentTimeMillis();
        recolorSingleThreaded(originalImage, resultImage);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("Time taken by a single thread is: " + duration);

        // benchmarking performance for multi threads
        for(int i = 1; i <= 8; i++) {
            startTime = System.currentTimeMillis();
            recolorMultiThreaded(originalImage, resultImage, i);
            endTime = System.currentTimeMillis();
            duration = endTime - startTime;
            System.out.println("Time taken by " + i + " threads is: " + duration);
        }

        // write this image to a file
        File outputFile = new File(DESTINATION_FILE);
        ImageIO.write(resultImage, "jpg", outputFile);
    }

    public static void recolorMultiThreaded(BufferedImage originalImage, BufferedImage resultantImage, int numThreads) {
        List<Thread> threads = new ArrayList<>();
        for(int i = 0; i < numThreads; i++) {
            final int bottomCornerMultiplier = i;
            Thread thread = new Thread(() -> {
                int height = originalImage.getHeight() / numThreads;
                ImageShenanigans.recolorImage(originalImage, resultantImage, 0, bottomCornerMultiplier * height, height, originalImage.getWidth());
            });
            threads.add(thread);
        }
        for(Thread thread : threads) {
            thread.start();
        }
        for(Thread thread : threads) {
            try {
                thread.join(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void recolorSingleThreaded(BufferedImage originalImage, BufferedImage resultImage) {
        ImageShenanigans.recolorImage(originalImage, resultImage, 0, 0, originalImage.getHeight(), originalImage.getWidth());
    }
}
