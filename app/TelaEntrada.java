import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.UIManager;

public class TelaEntrada extends JFrame {

    private static PGM pgm;
    private final JLabel hint = new JLabel("Arraste um arquivo \".pgm\" aqui ou clique em \"Abrir arquivo\"",
            SwingConstants.CENTER);

    public TelaEntrada() {
        super("Bem-vindo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 260);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JButton btnAbrir = new JButton("Abrir arquivo");
        btnAbrir.addActionListener(this::abrirArquivo);

        JPanel centro = new JPanel(new BorderLayout());
        centro.add(hint, BorderLayout.CENTER);
        centro.setBorder(BorderFactory.createDashedBorder(UIManager.getColor("Component.borderColor")));

        // Habilita Drag-and-Drop de arquivos no painel central
        centro.setTransferHandler(new TransferHandler() {
            @Override
            public boolean canImport(TransferSupport support) {
                boolean ok = support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
                support.setDropAction(COPY);
                return ok;
            }

            @Override
            public boolean importData(TransferSupport support) {
                if (!canImport(support))
                    return false;
                try {
                    @SuppressWarnings("unchecked")
                    List<File> files = (List<File>) support.getTransferable()
                            .getTransferData(DataFlavor.javaFileListFlavor);
                    if (files == null || files.isEmpty())
                        return false;
                    processarArquivo(files.get(0));
                    return true;
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(TelaEntrada.this, "Falha ao importar arquivo: " + ex.getMessage(),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        });

        add(centro, BorderLayout.CENTER);
        add(btnAbrir, BorderLayout.SOUTH);

    }

    private void abrirArquivo(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        int res = chooser.showOpenDialog(this);

        if (res == JFileChooser.APPROVE_OPTION) {
            processarArquivo(chooser.getSelectedFile());
        }
    }

    private void processarArquivo(File arquivo) {
        StringBuilder sb = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            TelaEntrada.pgm = new PGM(arquivo.getPath());

            String linha;
            while ((linha = br.readLine()) != null)
                sb.append(linha).append("\n");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao ler arquivo: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        TelaLeitura tela = new TelaLeitura("Leitura: " + arquivo.getName(), TelaEntrada.pgm.matrizComum());
        tela.setVisible(true);
        dispose();
    }

    public static PGM getPgm() {
        return pgm;
    }

    public JLabel getHint() {
        return hint;
    }
}

