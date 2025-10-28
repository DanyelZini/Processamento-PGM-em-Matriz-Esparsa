package matriz_esparsa;

import java.util.Objects;

public class MatrizEsparsa<T> implements InterfaceMatrizEsparsa<T>{

    // Celula basica de uma matriz esparsa encadeada por linhas e colunas
    // right aponta para o proximo elemento na mesma linha
    // down aponta para o proximo elemento na mesma coluna
    private static class Cell<T> {
        int row, col;
        T val;
        Cell<T> right;
        Cell<T> down;

        Cell(int r, int c, T v) {
            row = r;
            col = c;
            val = v;
        }
    }

    private final int rows;
    private final int cols;
    private final Cell<T>[] rowHeads; // cabecas de linhas para varrer pra direita
    private final Cell<T>[] colHeads; // cabecas de colunas para varrer pra baixo
    private int quant; // quantidade de celulas nao nulas

    @SuppressWarnings("unchecked")
    public MatrizEsparsa(int rows, int cols) {
        // valida dimensoes minimas
        if (rows <= 0 || cols <= 0)
            throw new IllegalArgumentException("Dimensoes invalidas!!!");

        this.rows = rows;
        this.cols = cols;

        // cria vetores de sentinelas para linhas e colunas
        this.rowHeads = (Cell<T>[]) new Cell<?>[rows];
        this.colHeads = (Cell<T>[]) new Cell<?>[cols];

        // cada linha/coluna tem um sentinela com col/row = -1
        for (int r = 0; r < rows; r++)
            rowHeads[r] = new Cell<>(r, -1, null);
        for (int c = 0; c < cols; c++)
            colHeads[c] = new Cell<>(-1, c, null);
    }

    public int linhas() {
        return rows;
    }

    public int colunas() {
        return cols;
    }

    public int quant() {
        return quant;
    }

    // Verifica se (r, c) esta dentro dos limites
    private void checkBordas(int r, int c) {
        if (r < 0 || r >= rows || c < 0 || c >= cols)
            throw new IndexOutOfBoundsException("Posicao invalida (" + r + ", " + c + ")");
    }

    // Busca valor em (r, c) caminhando so pelos nao nulos da linha r
    public T get(int r, int c) {
        checkBordas(r, c);
        Cell<T> cur = rowHeads[r].right;

        // Como a linha esta ordenada por col, avanca ate col >= c
        while (cur != null && cur.col < c)
            cur = cur.right;

        // Retorna valor se achou a coluna exata, ou null (que significa zero)
        return (cur != null && cur.col == c) ? cur.val : null;
    }

    // insere/atualiza valor em (r, c)
    // Se v == null, remove a celula (vira zero)
    public void set(int r, int c, T v) {
        checkBordas(r, c);

        // null significa que quer zerar/remover a celula
        if (v == null) {
            remove(r, c);
            return;
        }

        // Varre a lista da linha r ate a posicao correta
        Cell<T> prevRow = rowHeads[r], curRow = prevRow.right;
        while (curRow != null && curRow.col < c) {
            prevRow = curRow;
            curRow = curRow.right;
        }

        // Se ja existe a celula nessa coluna, apenas atualiza
        if (curRow != null && curRow.col == c) {
            curRow.val = v;
            return;
        }

        // Senao, cria uma nova celula e insere na linha
        Cell<T> node = new Cell<>(r, c, v);
        node.right = curRow;
        prevRow.right = node;

        // Agora insere no encadeamento da coluna correspondente
        Cell<T> prevCol = colHeads[c], curCol = prevCol.down;
        while (curCol != null && curCol.row < r) {
            prevCol = curCol;
            curCol = curCol.down;
        }
        node.down = curCol;
        prevCol.down = node;

        quant++;
    }

    // Remove a celula em (r, c) se existir e retorna o valor antigo, se nao,
    // retorna null
    public T remove(int r, int c) {
        checkBordas(r, c);

        // Desconecta da lista da linha
        Cell<T> prevRow = rowHeads[r], curRow = prevRow.right;
        while (curRow != null && curRow.col < c) {
            prevRow = curRow;
            curRow = curRow.right;
        }
        if (curRow == null || curRow.col != c) // nao achou
            return null;

        T old = curRow.val;
        prevRow.right = curRow.right;

        // Desconecta da lista da coluna
        Cell<T> prevCol = colHeads[c], curCol = prevCol.down;
        while (curCol != null && curCol.row < r) {
            prevCol = curCol;
            curCol = curCol.down;
        }
        if (curCol != null && curCol.row == r) {
            prevCol.down = curCol.down;
        }

        quant--;
        return old;
    }

    // Materializa uma matriz densa int[][] para debug/visualizacao
    // Valores null viram 0 e valores T devem ser Integer
    public int[][] matrizComum() {
        int[][] matriz = new int[linhas()][colunas()];

        for (int r = 0; r < rows; r++) {
            Cell<T> cur = rowHeads[r].right;
            for (int c = 0; c < cols; c++) {
                if (cur != null && cur.col == c) {
                    matriz[r][c] = (int) (cur.val);
                    cur = cur.right;
                } else {
                    matriz[r][c] = 0;
                }
            }
        }

        return matriz;
    }

    // Inverte escala de cinza: v -> 255 - v
    // Se o novo valor for 0, sera removido a celula
    public void inverterCores() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                T tv = get(r, c);
                int v = (tv == null) ? 0 : (int) tv;
                int nv = 255 - v;

                if (nv == 0) {
                    remove(r, c);
                } else {
                    @SuppressWarnings("unchecked")
                    T boxed = (T) Integer.valueOf(nv);
                    set(r, c, boxed);
                }
            }
        }
    }

    // Rotacao 90 graus horario
    // Mapeamento: (r, c) -> (c, rows-1-r)
    public MatrizEsparsa<T> girar90Horario() {
        MatrizEsparsa<T> rotated = new MatrizEsparsa<>(this.cols, this.rows);

        for (int r = 0; r < rows; r++) {
            Cell<T> cur = rowHeads[r].right;
            while (cur != null) {
                int c = cur.col;

                int nr = c;
                int nc = (rows - 1) - r;

                int v = (int) cur.val;
                if (v != 0) {
                    @SuppressWarnings("unchecked")
                    T boxed = (T) Integer.valueOf(v);
                    rotated.set(nr, nc, boxed);
                }

                cur = cur.right;
            }
        }

        return rotated;
    }

    // Rotacao 90 graus anti horario
    // Mapeamento: (r, c) -> (cols-1-c, r)
    public MatrizEsparsa<T> girar90AntiHorario() {
        MatrizEsparsa<T> rotated = new MatrizEsparsa<>(this.cols, this.rows);

        for (int r = 0; r < rows; r++) {
            Cell<T> cur = rowHeads[r].right;
            while (cur != null) {
                int c = cur.col;

                int nr = (cols - 1) - c;
                int nc = r;

                int v = (int) cur.val;
                if (v != 0) {
                    @SuppressWarnings("unchecked")
                    T boxed = (T) Integer.valueOf(v);
                    rotated.set(nr, nc, boxed);
                }

                cur = cur.right;
            }
        }

        return rotated;
    }

    // Insere uma moldura branca (valor 255) de 3 px ao redor
    // Usa set para sobrescrever valores existentes e criar novos nos
    public void inserirBordaBranca3px() {
        final int B = 3;
        if (rows < B * 2 || cols < B * 2) {
            System.out.println("Matriz muito pequena.");
        }

        // Topo
        for (int r = 0; r < Math.min(B, rows); r++) {
            for (int c = 0; c < cols; c++) {
                set(r, c, cor255());
            }
        }
        // Base
        for (int r = Math.max(0, rows - B); r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                set(r, c, cor255());
            }
        }

        // Esquerda
        for (int c = 0; c < Math.min(B, cols); c++) {
            for (int r = 0; r < rows; r++) {
                set(r, c, cor255());
            }
        }
        // Direita
        for (int c = Math.max(0, cols - B); c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                set(r, c, cor255());
            }
        }
    }

    // Borda branca quando T == Integer
    @SuppressWarnings("unchecked")
    private T cor255() {
        return (T) Integer.valueOf(255);
    }

    // Imprime a forma densa, mas percorrendo apenas nos nao nulos por linha
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int r = 0; r < rows; r++) {
            Cell<T> cur = rowHeads[r].right;
            sb.append("[");
            for (int c = 0; c < cols; c++) {
                if (cur != null && cur.col == c) {
                    sb.append(Objects.toString(cur.val));
                    cur = cur.right;
                } else {
                    sb.append("0");
                }
                if (c + 1 < cols)
                    sb.append(", ");
            }
            sb.append("]\n");
        }
        return sb.toString();
    }

    // Teste mais robusto:
    // - cria um padrao simples
    // - imprime
    // - inverte cores
    // - insere borda branca 3px
    // - gira horario e anti horario
    public static void main(String[] args) {
        MatrizEsparsa<Integer> m = new MatrizEsparsa<>(8, 10);

        // Linha central horizontal em 255
        int midR = 8 / 2;
        for (int c = 0; c < m.colunas(); c++)
            m.set(midR, c, 255);

        // Coluna central vertical em 255
        int midC = 10 / 2;
        for (int r = 0; r < m.linhas(); r++)
            m.set(r, midC, 255);

        // Pequeno bloco 3x3 com 128 no canto superior esquerdo
        for (int r = 1; r <= 3; r++) {
            for (int c = 1; c <= 3; c++) {
                m.set(r, c, 128);
            }
        }

        System.out.println("Original:");
        System.out.println(m);

        // Inverter cores
        m.inverterCores();
        System.out.println("Depois de inverter cores:");
        System.out.println(m);
        m.inverterCores();

        // Girar 90 horario
        MatrizEsparsa<Integer> mh = m.girar90Horario();
        System.out.println("Girada 90 graus horario:");
        System.out.println(mh);

        // Girar 90 anti horario a partir da original invertida+bordada para comparar
        MatrizEsparsa<Integer> mah = m.girar90AntiHorario();
        System.out.println("Girada 90 graus anti horario:");
        System.out.println(mah);

        // So para validar: get num ponto e remocao
        System.out.println("Valor em (0,0): " + m.get(0, 0));
        System.out.println("Removido (0,0): " + m.remove(0, 0));
        System.out.println("Quant de nos: " + m.quant());

        // Inserir borda branca 3px
        m.inserirBordaBranca3px();
        System.out.println("Depois de inserir borda branca 3px:");
        System.out.println(m);
    }
}
