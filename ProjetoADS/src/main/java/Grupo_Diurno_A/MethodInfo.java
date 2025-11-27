package Grupo_Diurno_A;

/**
 * A classe {@code MethodInfo} representa a informacao basica de um metodo,
 * incluindo suas metricas associadas.
 *
 * <p>Utilizada para armazenar detalhes dos metodos durante a analise
 * de metricas de codigo fonte.</p>
 *
 */
public class MethodInfo {
    /**
     * Nome do metodo.
     */
    private String name;

    /**
     * Numero da linha inicial do metodo no ficheiro fonte.
     */
    private int startLine;

    /**
     * Numero da linha final do metodo no ficheiro fonte.
     */
    private int endLine;

    //metricas do metodo
    /**
     * Linhas de codigo (LOC) do metodo.
     */
    private int loc; //linhas de codigo

    /**
     * Complexidade ciclomatica do metodo.
     */
    private int cyclomaticComplexity; //complexidade ciclomatica

    /**
     * Numero de parametros do metodo.
     */
    private int numeroDeParametros; //numero de parametros

    //construtor

    /**
     * Construtor da classe MethodInfo.
     * @param name nome do metodo
     * @param startLine numero da linha inicial do metodo
     * @param endLine numero da linha final do metodo
     * @param numeroDeParametros numero de parametros do metodo
     */
    public MethodInfo(String name, int startLine, int endLine, int numeroDeParametros) {
        this.name = name;
        this.startLine = startLine;
        this.endLine = endLine;
        this.numeroDeParametros = numeroDeParametros;
        this.loc = endLine - startLine + 1;
        this.cyclomaticComplexity = 1; //inicializa com 1 - valor base da complexidade ciclomatica
    }

    //getters

    /**
     * Retorna o nome do metodo.
     * @return nome do metodo
     */
    public String getName() {
        return name;
    }

    /**
     * Retorna o numero da linha inicial do metodo.
     * @return linha inicial do metodo
     */
    public int getStartLine() {
        return startLine;
    }

    /**
     * Retorna o numero da linha final do metodo.
     * @return linha final do metodo
     */
    public int getEndLine() {
        return endLine;
    }

    /**
     * Retorna as linhas de codigo (LOC) do metodo.
     * @return linhas de codigo do metodo
     */
    public int getLoc() {
        return loc;
    }

    /**
     * Retorna a complexidade ciclomatica do metodo.
     * @return complexidade ciclomatica do metodo
     */
    public int getCyclomaticComplexity() {
        return cyclomaticComplexity;
    }

    /**
     * Retorna o numero de parametros do metodo.
     * @return numero de parametros do metodo
     */
    public int getNumeroDeParametros() {
        return numeroDeParametros;
    }

    //setters
    /**
     * Define a complexidade ciclomatica do metodo.
     * @param cyclomaticComplexity complexidade ciclomatica a ser definida
     */
    public void setCyclomaticComplexity(int cyclomaticComplexity) {
        this.cyclomaticComplexity = cyclomaticComplexity;
    }

}
