import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import matriz_esparsa.MatrizEsparsa;

public class PGM {

    private MatrizEsparsa<Integer> matriz;

    private static String proximoToken(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        int ch;

        while (true) {
            ch = in.read();
            if (ch == -1)
                return null;

            if (ch == '#') {
                while (ch != -1 && ch != '\n' && ch != '\r')
                    ch = in.read();
            } else if (Character.isWhitespace(ch)) {
                continue;
            } else {
                break;
            }
        }

        while (ch != -1 && !Character.isWhitespace(ch)) {
            sb.append((char) ch);
            ch = in.read();
        }
        return sb.toString();
    }

    public static MatrizEsparsa<Integer> lerPGM(InputStream in) throws IOException {
        String magic = proximoToken(in);

        if (!"P2".equals(magic))
            throw new IllegalArgumentException("Formato esperado: P2");

        int width = Integer.parseInt(proximoToken(in));
        int height = Integer.parseInt(proximoToken(in));
        int maxVal = Integer.parseInt(proximoToken(in));

        if (width <= 0 || height <= 0 || maxVal <= 0)
            throw new IllegalArgumentException("Dimensoes ou valor maximo invalidos.");

        MatrizEsparsa<Integer> matriz = new MatrizEsparsa<>(height, width);
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                String tok = proximoToken(in);
                if (tok == null)
                    throw new EOFException("Dados insuficientes no arquivo PGM");

                int valor = Integer.parseInt(tok);

                if (valor != 0) {
                    int valorNorm = (maxVal == 255) ? valor : (int) Math.round(valor * 255.0 / maxVal);
                    matriz.set(r, c, valorNorm);
                }
            }
        }

        return matriz;
    }

    public PGM(String caminho) {
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(caminho))) {

            this.matriz = lerPGM(in);

            System.out.println("Matriz carregada:");
            System.out.println("Linhas: " + this.matriz.linhas());
            System.out.println("Colunas: " + this.matriz.colunas());
            System.out.println("Elementos não zero: " + this.matriz.quant());
            // System.out.println();
            // System.out.println(this.matriz);

        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String gerarPGM() {
        StringBuilder sb = new StringBuilder();
        int linhas = matriz.linhas();
        int colunas = matriz.colunas();

        sb.append("P2\n");
        sb.append(colunas).append(' ').append(linhas).append('\n');
        sb.append("255\n"); // maxVal do trabalho

        final int width = 3; // 0..255 -> 3 dígitos

        for (int r = 0; r < linhas; r++) {
            for (int c = 0; c < colunas; c++) {
                Integer v = matriz.get(r, c);
                int px = (v == null) ? 0 : v; // null == 0

                String s = Integer.toString(px);
                for (int k = s.length(); k < width; k++)
                    sb.append(' ');
                sb.append(s);

                if (c + 1 < colunas)
                    sb.append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public int[][] matrizComum() {
        return matriz.matrizComum();
    }

    public void inverterCores() {
        this.matriz.inverterCores();
    }

    public void inserirBordaBranca3px() {
        this.matriz.inserirBordaBranca3px();
    }

    public void girar90Horario() {
        this.matriz = matriz.girar90Horario();
    }

    public void girar90AntiHorario() {
        this.matriz = matriz.girar90AntiHorario();
    }
}
