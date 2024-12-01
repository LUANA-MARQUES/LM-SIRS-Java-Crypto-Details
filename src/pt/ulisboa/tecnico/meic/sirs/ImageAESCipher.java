package pt.ulisboa.tecnico.meic.sirs;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.crypto.Cipher;
import javax.imageio.ImageIO;

/**
 * Encrypts an image with the AES algorithm in multiple modes, with a given,
 * appropriate AES key
 */
public class ImageAESCipher {
            
    public static void xorCipheredImages(String image1Path, String image2Path, String keyFile, String mode, String outputFile) {
        try {
            // Load the images
            BufferedImage image1 = ImageIO.read(new File(image1Path));
            BufferedImage image2 = ImageIO.read(new File(image2Path));

            // Cipher both images
            BufferedImage cipheredImage1 = cipherImage(image1, keyFile, mode);
            BufferedImage cipheredImage2 = cipherImage(image2, keyFile, mode);

            // Ensure both images have the same dimensions
            int width = Math.min(cipheredImage1.getWidth(), cipheredImage2.getWidth());
            int height = Math.min(cipheredImage1.getHeight(), cipheredImage2.getHeight());

            BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            // XOR the pixels of the two ciphered images
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pixel1 = cipheredImage1.getRGB(x, y);
                    int pixel2 = cipheredImage2.getRGB(x, y);

                    int xorPixel = pixel1 ^ pixel2;

                    resultImage.setRGB(x, y, xorPixel);
                }
            }

            // Save the result image
            ImageIO.write(resultImage, "png", new File(outputFile));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage cipherImage(BufferedImage image, String keyFile, String mode) throws Exception {
        // Convert the image to a byte array
        byte[] imageBytes = ImageMixer.imageToByteArray(image);

        // Cipher the byte array
        AESCipherByteArrayMixer cipher = new AESCipherByteArrayMixer(Cipher.ENCRYPT_MODE);
        cipher.setParameters(keyFile, mode);
        byte[] cipheredBytes = cipher.cipher(imageBytes);

        // Convert the ciphered byte array back to an image
        return ImageMixer.byteArrayToImage(cipheredBytes, image.getWidth(), image.getHeight());
    }    
      

    /* public static void printImagePixels(String inputFile) {
        try {
            // Load the image file
            BufferedImage image = ImageIO.read(new File(inputFile));

            // Get image dimensions
            int width = image.getWidth();
            int height = image.getHeight();

            // Iterate through each pixel
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    // Get RGB value of the pixel
                    int pixel = image.getRGB(x, y);

                    // Extract the color components
                    int alpha = (pixel >> 24) & 0xff;
                    int red = (pixel >> 16) & 0xff;
                    int green = (pixel >> 8) & 0xff;
                    int blue = pixel & 0xff;

                    // Print the pixel information
                    System.out.printf("Pixel at (%d, %d): A=%d, R=%d, G=%d, B=%d%n", x, y, alpha, red, green, blue);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    } */

    public static void main(String[] args) throws Exception {

        if (args.length < 4) {
            System.err.println("This program encrypts an image file with AES.");
            System.err.println("Usage: image-aes-cipher [inputFile.png] [AESKeyFile] [ECB|CBC|OFB] [outputFile.png]");
            return;
        }

        final String inputFile = args[0];
        final String keyFile = args[1];
        final String mode = args[2].toUpperCase();
        final String outputFile = args[3];

        if (!(mode.equals("ECB") || mode.equals("CBC") || mode.equals("OFB"))) {
            System.err.println("The modes of operation must be ECB, CBC or OFB.");
            return;
        }

        AESCipherByteArrayMixer cipher = new AESCipherByteArrayMixer(Cipher.ENCRYPT_MODE);
        cipher.setParameters(keyFile, mode);
        ImageMixer.mix(inputFile, outputFile, cipher);
        String outputFile2 = "intro/outputs/xoredOutputFile2.png";
        String image2Path = "intro/inputs/tux-0480.png";
        //printImagePixels(outputFile);
        xorCipheredImages(inputFile, image2Path, keyFile, mode, outputFile2);

    }

}
