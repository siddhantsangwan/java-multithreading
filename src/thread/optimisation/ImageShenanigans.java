package thread.optimisation;

import java.awt.image.BufferedImage;
import java.sql.BatchUpdateException;

public class ImageShenanigans {
    public static void recolorImage(BufferedImage originalImage, BufferedImage resultImage, int leftCorner, int bottomCorner, int height, int width) {
        for(int x = leftCorner; x < leftCorner + width && x < originalImage.getWidth(); x++) {
            for(int y = bottomCorner; y < bottomCorner + height && y < originalImage.getHeight(); y++) {
                recolorPixel(originalImage, resultImage, x, y);
            }
        }
    }
    public static void recolorPixel(BufferedImage originalImage, BufferedImage resultImage, int x, int y) {
        int rgb = originalImage.getRGB(x, y);
        int red = getRed(rgb);
        int green = getGreen(rgb);
        int blue = getBlue(rgb);

        int newRed, newGreen, newBlue;
        if(isShadeOfGrey(red, green, blue)) {
            newRed = Math.min(255, red + 10);
            newGreen = Math.max(0, green - 80);
            newBlue = Math.max(0, blue - 20);
        }
        else {
            newRed = red;
            newGreen = green;
            newBlue = blue;
        }
        int newRGB = createRGBFromColors(newRed, newGreen, newBlue);
        setRGB(resultImage, x, y, newRGB);
    }
    public static boolean isShadeOfGrey(int red, int green, int blue) {
        return (Math.abs(red - green) < 30 && Math.abs(red - blue) < 30 && Math.abs(green - blue) < 30);
    }
    public static void setRGB(BufferedImage resultImage, int x, int y, int newRGB) {
        resultImage.getRaster().setDataElements(x, y, resultImage.getColorModel().getDataElements(newRGB, null));
    }
    public static int createRGBFromColors(int red, int green, int blue) {
        int rgb = 0;
        rgb |= blue;
        rgb |= green << 8;
        rgb |= red << 16;

        // set opacity to fully opaque
        rgb |= 0xFF000000;
        return rgb;
    }
    public static int getRed(int rgb) {
        return (rgb & 0x00FF0000) >> 16;
    }
    public static int getGreen(int rgb) {
        return (rgb & 0x0000FF00) >> 8;
    }
    public static int getBlue(int rgb) {
        return (rgb & 0x000000FF);
    }
}
