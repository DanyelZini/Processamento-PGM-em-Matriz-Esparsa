package matriz_esparsa;

public interface InterfaceMatrizEsparsa<T> {
    int linhas(); // numero de linhas
    int colunas(); // numero de colunas
    int quant(); // nao nulos

    T get(int r, int c); // null == zero
    void set(int r, int c, T v); // v == null -> remove
    T remove(int r, int c);

    int[][] matrizComum(); // materializa para debug (assume T == Integer)

    void inverterCores(); // v -> 255 - v (assume T == Integer)

    void inserirBordaBranca3px(); // borda 255 (assume T == Integer)

    InterfaceMatrizEsparsa<T> girar90Horario();
    InterfaceMatrizEsparsa<T> girar90AntiHorario();
}
