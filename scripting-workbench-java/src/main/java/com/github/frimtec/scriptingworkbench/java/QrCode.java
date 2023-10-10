package com.github.frimtec.scriptingworkbench.java;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Copied from <a href="https://hollycummins.com/creating-QR-codes/">Creating Custom QR Codes - Blog post</a>.
 */
public class QrCode {

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.err.println(
                    "Wrong number of arguments - should be text to encode, image path, output file" +
                            " path");
            System.exit(1);
        } else {
            String text = args[0];
            String imagePath = args[1];
            String outPath = args[2];
            int size = 640;
            writeQrCode(text, imagePath, outPath, size, size);
        }
    }

    private static void writeQrCode(String text, String imagePath, String outPath, int width, int height) throws Exception {
        Map<EncodeHintType, ErrorCorrectionLevel> hints = new HashMap<>();

        // Specify the error correction, to allow the QR code to tolerate errors, such as
        // a great big picture plunked in the middle
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width,
                height, hints);
        // Load QR image
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix,
                new MatrixToImageConfig(
                        0xFF000000,
                        0xFFFFFFFF));

        // Initialize combined image
        BufferedImage combined = new BufferedImage(qrImage.getHeight(), qrImage.getWidth(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) combined.getGraphics();

        // Write QR code to new image at position 0/0
        g.drawImage(qrImage, 0, 0, null);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        addOverlayImage(g, qrImage, imagePath);

        ImageIO.write(combined, "png", new File(outPath));
        System.out.println("Created QR code at " + outPath);
    }


    private static void addOverlayImage(Graphics2D g, BufferedImage qrImage,
                                                 String imagePath) throws IOException {
        // Load logo image
        BufferedImage overlay = ImageIO.read(new File(imagePath));

        // Calculate the delta height and width between QR code and the logo
        // Note that we don't do any scaling, so the sizes need to kind of
        // work together without obscuring too much logo
        int deltaHeight = qrImage.getHeight() - overlay.getHeight();
        int deltaWidth = qrImage.getWidth() - overlay.getWidth();

        int wOffset = Math.round((float) deltaWidth / 2);
        int hOffset = Math.round((float) deltaHeight / 2);

        // Write the logo into the combined image at position (deltaWidth / 2) and
        // (deltaHeight / 2), so that it's centered
        g.drawImage(overlay, wOffset, hOffset, null);
    }
}