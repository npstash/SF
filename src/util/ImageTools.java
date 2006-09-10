package util;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;

import javax.swing.ImageIcon;

public abstract class ImageTools {

    public static ImageIcon makeTransparentIcon(ImageIcon icon,
            final Color color) {
        ImageFilter filter = new RGBImageFilter() {
            // zapamietanie koloru transparencji wraz z ustawionymi alfa-bitami
            private int transparencyColor = (color.getRGB() | 0xFF000000);

            public int filterRGB(int x, int y, int rgb) {
                if ((rgb | 0xFF000000) == transparencyColor) {
                    // to ten kolor - zerowanie alfa-bitow
                    return 0x00FFFFFF & rgb;
                } else {
                    // przepuszczenie innych kolorow
                    return rgb;
                }
            }
        };
        ImageProducer ip = new FilteredImageSource(icon.getImage().getSource(),
                filter);
        return new ImageIcon(Toolkit.getDefaultToolkit().createImage(ip));
    }

}