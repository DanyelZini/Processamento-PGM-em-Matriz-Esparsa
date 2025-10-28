import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;

public class TelaLeitura extends JFrame {
    private final ImagePanel panel;

    public TelaLeitura(String titulo, int[][] conteudo) {
        setTitle(titulo);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton btnInverter = new JButton("Inverter cores");
        JButton btnGirarHorario = new JButton("Girar imagem - Direita");
        JButton btnGirarAntiHorario = new JButton("Girar imagem - Esquerda");
        JButton btnBorda = new JButton("Borda na imagem");
        JButton btnExportar = new JButton("Exportar Matriz");

        toolBar.add(btnInverter);
        toolBar.add(btnGirarHorario);
        toolBar.add(btnGirarAntiHorario);
        toolBar.add(btnBorda);
        toolBar.add(btnExportar);

        BufferedImage img = criarImagemCinza(conteudo);
        panel = new ImagePanel(img);

        JPanel root = new JPanel(new BorderLayout());
        root.add(toolBar, BorderLayout.NORTH);
        root.add(panel, BorderLayout.CENTER);
        setContentPane(root);
        setLocationRelativeTo(null);
        setVisible(true);

        btnInverter.addActionListener(e -> {
            // 1) Atualiza o modelo
            TelaEntrada.getPgm().inverterCores();
            // 2) Recria a imagem a partir da nova matriz
            int[][] m = TelaEntrada.getPgm().matrizComum();
            BufferedImage nova = criarImagemCinza(m);
            // 3) Atualiza a view e repinta
            panel.setImage(nova);
        });

        btnGirarHorario.addActionListener(e -> {
            // 1) Atualiza o modelo
            TelaEntrada.getPgm().girar90Horario();
            // 2) Recria a imagem a partir da nova matriz
            int[][] m = TelaEntrada.getPgm().matrizComum();
            BufferedImage nova = criarImagemCinza(m);
            // 3) Atualiza a view e repinta
            panel.setImage(nova);
        });

        btnGirarAntiHorario.addActionListener(e -> {
            TelaEntrada.getPgm().girar90AntiHorario();
            // 2) Recria a imagem a partir da nova matriz
            int[][] m = TelaEntrada.getPgm().matrizComum();
            BufferedImage nova = criarImagemCinza(m);
            // 3) Atualiza a view e repinta
            panel.setImage(nova);
        });

        btnBorda.addActionListener(e -> {
            // 1) Atualiza o modelo
            TelaEntrada.getPgm().inserirBordaBranca3px();
            // 2) Recria a imagem a partir da nova matriz
            int[][] m = TelaEntrada.getPgm().matrizComum();
            BufferedImage nova = criarImagemCinza(m);
            // 3) Atualiza a view e repinta
            panel.setImage(nova);
        });

        btnExportar.addActionListener(e -> {
            String pgmTxt = TelaEntrada.getPgm().gerarPGM();
            System.out.println(pgmTxt);

            javax.swing.JTextArea area = new javax.swing.JTextArea(pgmTxt, 25, 80);
            area.setEditable(false);
            area.setFont(new java.awt.Font(java.awt.Font.MONOSPACED, java.awt.Font.PLAIN, 13));
            area.setLineWrap(false);
            area.setWrapStyleWord(false);

            javax.swing.JScrollPane sp = new javax.swing.JScrollPane(area);
            javax.swing.JOptionPane.showMessageDialog(
                    this, sp, "PGM Resultante", javax.swing.JOptionPane.PLAIN_MESSAGE);
        });

    }

    private static BufferedImage criarImagemCinza(int[][] m) {
        int h = m.length;
        
        if (h == 0)
            throw new IllegalArgumentException("Matriz vazia");

        int w = m[0].length;

        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster = bi.getRaster();

        for (int y = 0; y < h; y++) {
            if (m[y].length != w)
                throw new IllegalArgumentException("Linhas com larguras diferentes");
            for (int x = 0; x < w; x++) {
                int v = m[y][x];
                if (v < 0)
                    v = 0;
                if (v > 255)
                    v = 255;
                raster.setSample(x, y, 0, v);
            }
        }
        return bi;
    }

}
