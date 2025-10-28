import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public class ImagePanel extends JPanel {

    private BufferedImage image;
    private boolean suavizar = false;
    private boolean ajustarAoPainel = true;

    public ImagePanel() {
        this(null);
    }

    public ImagePanel(BufferedImage image) {
        this.image = image;
        setBackground(Color.DARK_GRAY);
    }

    public void setImage(BufferedImage img) {
        this.image = img;
        revalidate();
        repaint(); // repinta com a nova imagem
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setSuavizar(boolean suavizar) {
        this.suavizar = suavizar;
        repaint();
    }

    public boolean isSuavizar() {
        return suavizar;
    }

    public void setAjustarAoPainel(boolean ajustar) {
        this.ajustarAoPainel = ajustar;
        repaint();
    }

    public boolean isAjustarAoPainel() {
        return ajustarAoPainel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image == null)
            return;

        int pw = getWidth();
        int ph = getHeight();
        int iw = image.getWidth();
        int ih = image.getHeight();
        int dw = iw;
        int dh = ih;

        if (ajustarAoPainel && (iw > pw || ih > ph)) {
            double sx = pw / (double) iw;
            double sy = ph / (double) ih;
            double s = Math.min(sx, sy);
            dw = Math.max(1, (int) Math.round(iw * s));
            dh = Math.max(1, (int) Math.round(ih * s));
        }

        int x = (pw - dw) / 2;
        int y = (ph - dh) / 2;

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    suavizar
                            ? RenderingHints.VALUE_INTERPOLATION_BILINEAR
                            : RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.drawImage(image, x, y, dw, dh, null);
        } finally {
            g2.dispose();
        }
    }
}
