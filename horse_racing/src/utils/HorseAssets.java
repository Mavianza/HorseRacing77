package utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HorseAssets {
    
    private static BufferedImage horseBaseImage = null;
    private static String gifPath = null;
    private static boolean gifLoaded = false;
    private static int gifWidth = 64;
    private static int gifHeight = 48;
    
    private static List<BufferedImage> preRenderedFrames = null;
    private static int preRenderedWidth = -1;
    private static int preRenderedHeight = -1;
    private static boolean framesPreRendered = false;
    
    public static boolean preRenderFrames(int targetWidth, int targetHeight) {
        if (framesPreRendered && 
            preRenderedWidth == targetWidth && 
            preRenderedHeight == targetHeight && 
            preRenderedFrames != null && 
            !preRenderedFrames.isEmpty()) {
            return true;
        }
        
        loadHorseGif();
        
        if (gifPath == null) {
            System.err.println("Cannot pre-render: GIF not loaded");
            return false;
        }
        
        preRenderedFrames = new ArrayList<>();
        preRenderedWidth = targetWidth;
        preRenderedHeight = targetHeight;
        
        try {
            File gifFile = new File(gifPath);
            if (!gifFile.exists()) {
                System.err.println("GIF file not found: " + gifPath);
                return false;
            }
            
            System.out.println("Pre-rendering GIF frames...");
            
            ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
            ImageInputStream stream = ImageIO.createImageInputStream(gifFile);
            reader.setInput(stream);
            
            int frameCount = reader.getNumImages(true);
            System.out.println("Total frames to pre-render: " + frameCount);
            
            for (int i = 0; i < frameCount; i++) {
                BufferedImage originalFrame = reader.read(i);
                
                int origWidth = originalFrame.getWidth();
                int origHeight = originalFrame.getHeight();
                
                double scaleX = (double) targetWidth / origWidth;
                double scaleY = (double) targetHeight / origHeight;
                double scale = Math.min(scaleX, scaleY);
                
                int scaledWidth = (int) (origWidth * scale);
                int scaledHeight = (int) (origHeight * scale);
                
                BufferedImage scaledFrame = new BufferedImage(
                    scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB
                );
                
                Graphics2D g2d = scaledFrame.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
                                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, 
                                    RenderingHints.VALUE_RENDER_QUALITY);
                
                g2d.drawImage(originalFrame, 0, 0, scaledWidth, scaledHeight, null);
                g2d.dispose();
                
                preRenderedFrames.add(scaledFrame);
                
                if ((i + 1) % 5 == 0 || i == frameCount - 1) {
                    System.out.println("Pre-rendered " + (i + 1) + "/" + frameCount + " frames");
                }
            }
            
            reader.dispose();
            stream.close();
            
            framesPreRendered = true;

            return true;
            
        } catch (Exception e) {
            System.err.println("Error pre-rendering GIF frames: " + e.getMessage());
            e.printStackTrace();
            preRenderedFrames = null;
            framesPreRendered = false;
            return false;
        }
    }
    
    public static BufferedImage getPreRenderedFrame(int frameIndex) {
        if (preRenderedFrames == null || preRenderedFrames.isEmpty()) {
            return null;
        }
        
        int index = frameIndex % preRenderedFrames.size();
        return preRenderedFrames.get(index);
    }
    
    public static int getPreRenderedFrameCount() {
        return preRenderedFrames != null ? preRenderedFrames.size() : 0;
    }
    
    public static boolean hasPreRenderedFrames() {
        return framesPreRendered && preRenderedFrames != null && !preRenderedFrames.isEmpty();
    }
    
    public static Dimension getPreRenderedDimensions() {
        if (preRenderedFrames != null && !preRenderedFrames.isEmpty()) {
            BufferedImage first = preRenderedFrames.get(0);
            return new Dimension(first.getWidth(), first.getHeight());
        }
        return new Dimension(preRenderedWidth, preRenderedHeight);
    }
    
    public static ImageIcon getHorseGifIcon() {
        loadHorseGif();
        
        if (gifPath != null) {
            try {
                File gifFile = new File(gifPath);
                if (gifFile.exists()) {
                    return new ImageIcon(gifFile.getAbsolutePath());
                }
            } catch (Exception e) {
                System.err.println("Error creating GIF icon: " + e.getMessage());
            }
        }
        
        return null;
    }
    
    public static ImageIcon getScaledHorseGifIcon(int maxWidth, int maxHeight) {
        if (hasPreRenderedFrames()) {
            BufferedImage firstFrame = getPreRenderedFrame(0);
            if (firstFrame != null) {
                return new ImageIcon(firstFrame);
            }
        }
        
        loadHorseGif();
        
        if (gifPath != null) {
            try {
                File gifFile = new File(gifPath);
                if (!gifFile.exists()) {
                    return null;
                }
                
                ImageIcon originalIcon = new ImageIcon(gifFile.getAbsolutePath());
                
                int origWidth = originalIcon.getIconWidth();
                int origHeight = originalIcon.getIconHeight();
                
                double scaleX = (double) maxWidth / origWidth;
                double scaleY = (double) maxHeight / origHeight;
                double scale = Math.min(scaleX, scaleY);
                
                int newWidth = (int) (origWidth * scale);
                int newHeight = (int) (origHeight * scale);
                
                int scaleType = (origWidth > 200 || origHeight > 200) ? 
                    Image.SCALE_REPLICATE : Image.SCALE_FAST;
                
                Image scaledImage = originalIcon.getImage().getScaledInstance(
                    newWidth, newHeight, scaleType
                );
                
                return new ImageIcon(scaledImage);
                
            } catch (Exception e) {
                System.err.println("Error creating scaled GIF: " + e.getMessage());
            }
        }
        
        return null;
    }
    
    public static int getGifWidth() {
        loadHorseGif();
        return gifWidth;
    }
    
    public static int getGifHeight() {
        loadHorseGif();
        return gifHeight;
    }
    
    private static void loadHorseGif() {
        if (gifLoaded) return;
        gifLoaded = true;
        
        try {
            File gifFile = new File("assets/horse.gif");
            
            if (gifFile.exists()) {
                gifPath = gifFile.getAbsolutePath();
                
                try (ImageInputStream iis = ImageIO.createImageInputStream(gifFile)) {
                    Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
                    if (readers.hasNext()) {
                        ImageReader reader = readers.next();
                        reader.setInput(iis);
                        gifWidth = reader.getWidth(0);
                        gifHeight = reader.getHeight(0);
                        
                        int frameCount = reader.getNumImages(true);
                        System.out.println("Horse GIF loaded successfully from assets/horse.gif");
                        System.out.println("GIF dimensions: " + gifWidth + "x" + gifHeight);
                        System.out.println("GIF frames: " + frameCount);
                        
                        reader.dispose();
                    }
                }
                
            } else {
                System.out.println("Warning: assets/horse.gif not found");
                gifPath = null;
            }
        } catch (Exception e) {
            System.err.println("Error loading horse GIF: " + e.getMessage());
            e.printStackTrace();
            gifPath = null;
        }
    }
    
    public static boolean hasAnimatedGif() {
        if (!gifLoaded) {
            loadHorseGif();
        }
        return gifPath != null;
    }
    
    private static BufferedImage loadHorseImage() {
        if (horseBaseImage != null) {
            return horseBaseImage;
        }
        
        try {
            File gifFile = new File("assets/horse.gif");
            if (gifFile.exists()) {
                horseBaseImage = ImageIO.read(gifFile);
            } else {
                horseBaseImage = createFallbackHorseImage();
            }
        } catch (IOException e) {
            horseBaseImage = createFallbackHorseImage();
        }
        
        return horseBaseImage;
    }
    
    public static BufferedImage createHorseImage(int width, int height) {
        BufferedImage baseImage = loadHorseImage();
        
        BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaledImage.createGraphics();
        
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        g2d.drawImage(baseImage, 0, 0, width, height, null);
        g2d.dispose();
        
        return scaledImage;
    }
    
    private static BufferedImage createFallbackHorseImage() {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(new Color(139, 69, 19));
        
        g2d.fillOval(20, 30, 60, 40);
        g2d.fillOval(65, 35, 30, 30);
        
        int[] neckX = {50, 65, 65, 45};
        int[] neckY = {35, 35, 45, 45};
        g2d.fillPolygon(neckX, neckY, 4);
        
        g2d.setStroke(new BasicStroke(8));
        g2d.drawLine(30, 70, 30, 95);
        g2d.drawLine(45, 70, 45, 95);
        g2d.drawLine(55, 70, 55, 95);
        g2d.drawLine(70, 70, 70, 95);
        
        g2d.setStroke(new BasicStroke(6));
        int[] tailX = {25, 15, 10};
        int[] tailY = {55, 65, 75};
        g2d.drawPolyline(tailX, tailY, 3);
        
        g2d.setStroke(new BasicStroke(4));
        g2d.drawArc(55, 25, 20, 25, 30, 120);
        
        int[] earX = {75, 80, 85};
        int[] earY = {35, 25, 35};
        g2d.fillPolygon(earX, earY, 3);
        
        g2d.dispose();
        return image;
    }
    
}