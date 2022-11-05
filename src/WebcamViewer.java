​//This is an example of using the Webcam Capture library that includes drawing on the image
package src;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Color;

import javax.swing.JFrame;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

public class WebcamViewer extends JFrame {

    private Webcam webcam;

    WebcamViewer()
    {
        webcam = Webcam.getDefault();
        webcam.setViewSize(WebcamResolution.VGA.getSize());

        DrawableWebcamPanel panel = new DrawableWebcamPanel(webcam);
        panel.setImageSizeDisplayed(true);
        panel.setMirrored(true); // optionally flip the camera horizontally

        this.getContentPane().add(panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setVisible(true);
    }
    
    BufferedImage getImage()
    {
        return webcam.getImage();
    }
}

class DrawableWebcamPanel extends WebcamPanel
{
    DrawableWebcamPanel(Webcam webcam)
    {
        super(webcam);
        setPainter(new DrawableWebcamPanelPainter(this));
    }
}

class DrawableWebcamPanelPainter implements WebcamPanel.Painter
{
    private WebcamPanel.Painter basePainter;

    DrawableWebcamPanelPainter(DrawableWebcamPanel drawableWebcamPanel)
    {
        basePainter = drawableWebcamPanel.getPainter();
    }

    public void paintImage(WebcamPanel panel, BufferedImage image, Graphics2D g2)
    {
        basePainter.paintImage(panel,  image,  g2);
        g2.setColor(Color.RED);
        g2.drawRect(ImageLearner.cameraCropLeftX, ImageLearner.cameraCropTopY, ImageLearner.cameraCropWidth, ImageLearner.cameraCropHeight);
    }

    public void paintPanel(WebcamPanel panel, Graphics2D g2)
    {
        basePainter.paintPanel(panel, g2);
    }
}