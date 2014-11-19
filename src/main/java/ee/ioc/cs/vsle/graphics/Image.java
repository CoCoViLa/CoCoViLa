/**
 * 
 */
package ee.ioc.cs.vsle.graphics;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

import javax.swing.*;

/**
 * @author pavelg
 *
 */
public class Image extends Shape {

    private static final long serialVersionUID = 1L;
    
    private BufferedImage image;// = Toolkit.getDefaultToolkit().getImage( "SingleSeriesGraph.gif" );
    //path relative to the package
    private String path;

    /**
     * @param x
     * @param y
     * @param width
     * @param height
     * @param name
     */
    public Image( int x, int y, BufferedImage image, String path, boolean fixed ) {
        super( x, y, image.getWidth(), image.getHeight() );
        this.image = image;
        this.path = path;
        setFixed( fixed );
    }

    public Image( int x, int y, java.awt.Image image, String path, boolean fixed ) {
        this( x, y, toBufferedImage( image ), path, fixed );
    }
    
    public Image( int x, int y, String image, String path, boolean fixed ) {
        this( x, y, Toolkit.getDefaultToolkit().getImage( image ), path, fixed );
    }
    
 // This method returns a buffered image with the contents of an image
    public static BufferedImage toBufferedImage(java.awt.Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage)image;
        }
    
        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();
    
        // Determine if the image has transparent pixels; for this method's
        // implementation, see e661 Determining If an Image Has Transparent Pixels
        boolean hasAlpha = hasAlpha(image);
    
        // Create a buffered image with a format that's compatible with the screen
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            // Determine the type of transparency of the new buffered image
            int transparency = Transparency.OPAQUE;
            if (hasAlpha) {
                transparency = Transparency.TRANSLUCENT;
            }
    
            // Create the buffered image
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(
                image.getWidth(null), image.getHeight(null), transparency);
        } catch (HeadlessException e) {
            // The system does not have a screen
        }
    
        if (bimage == null) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            if (hasAlpha) {
                type = BufferedImage.TYPE_INT_ARGB;
            }
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }
    
        // Copy image to buffered image
        Graphics g = bimage.createGraphics();
    
        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();
    
        return bimage;
    }
    
 // This method returns true if the specified image has transparent pixels
    public static boolean hasAlpha(java.awt.Image image) {
        // If buffered image, the color model is readily available
        if (image instanceof BufferedImage) {
            BufferedImage bimage = (BufferedImage)image;
            return bimage.getColorModel().hasAlpha();
        }
    
        // Use a pixel grabber to retrieve the image's color model;
        // grabbing a single pixel is usually sufficient
        PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            if (pg.grabPixels()) {
                // Get the image's color model
                ColorModel cm = pg.getColorModel();
                if (cm != null) {
                    return cm.hasAlpha();
                }
            }
        } catch (InterruptedException e) {
            // ignore
        }
        return false;
    }

    @Override
    public int getWidth() {
        return image.getWidth();
    }

    @Override
    public int getHeight() {
        return image.getHeight();
    }

    @Override
    public Shape getCopy() {
        return new Image( getX(), getY(), image, path, isFixed() );
    }

    @Override
    public void draw( int xModifier, int yModifier, float Xsize, float Ysize, Graphics2D g ) {
        int a = xModifier + (int) ( Xsize * getX() );
        int b = yModifier + (int) ( Ysize * getY() );
        
        AffineTransformOp op = null;
        
        if( !isFixed() ) {
            AffineTransform tx = new AffineTransform();
            tx.scale( Xsize, Ysize );
            op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        }
        
        g.drawImage( image, op, a, b );
        
        if( isSelected() ) {
            drawSelection( g );
        }
    }

    @Override
    public void resize( int deltaW, int deltaH, int cornerClicked ) {

    }

    @Override
    protected void drawSelection( Graphics2D g2 ) {
        if( isFixed() ) {
            g2.setColor( Color.black );
            g2.setStroke( new BasicStroke( 1.0f ) );
            g2.drawRect( getX(), getY(), getWidth(), getHeight() );
        } else {
            super.drawSelection( g2 );
        }
    }
    
    @Override
    public String toFile( int boundingboxX, int boundingboxY ) {
        return "<image x=\"" + ( getX() - boundingboxX ) + "\" y=\"" + ( getY() - boundingboxY ) //
                + "\" width=\"" + getWidth() + "\" height=\"" + getHeight() //
                + "\" path=\"" + path + "\" fixed=\"" + isFixed() + "\" />\n";//
    }

    @Override
    public String toText() {
        return "";
    }
}
