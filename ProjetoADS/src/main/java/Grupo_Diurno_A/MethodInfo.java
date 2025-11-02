package Grupo_Diurno_A;

public class MethodInfo {
    private String name;
    private int startLine;
    private int endLine;

    private int loc; //linhas de codigo
    private int cyclomaticComplexity; //complexidade ciclomatica
    private int numeroDeParametros; //numero de parametros

    //construtor
    public MethodInfo(String name, int startLine, int endLine, int numeroDeParametros) {
        this.name = name;
        this.startLine = startLine;
        this.endLine = endLine;
        this.numeroDeParametros = numeroDeParametros;
        this.loc = endLine - startLine + 1;
        this.cyclomaticComplexity = 1; //inicializa com 1 - valor base da complexidade ciclomatica
    }

    //getters
    public String getName() {
        return name;
    }

    public int getStartLine() {
        return startLine;
    }

    public int getEndLine() {
        return endLine;
    }
    public int getLoc() {
        return loc;
    }
    public int getCyclomaticComplexity() {
        return cyclomaticComplexity;
    }
    public int getNumeroDeParametros() {
        return numeroDeParametros;
    }

    //setters
    public void setCyclomaticComplexity(int cyclomaticComplexity) {
        this.cyclomaticComplexity = cyclomaticComplexity;
    }

}
