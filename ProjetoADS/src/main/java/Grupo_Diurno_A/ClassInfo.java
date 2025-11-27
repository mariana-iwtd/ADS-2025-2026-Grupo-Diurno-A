package Grupo_Diurno_A;

import java.util.ArrayList;
import java.util.List;

/**
 * A classe {@code ClassInfo} representa a informacao basica de uma classe,
 * incluindo seus metodos, atributos e metricas associadas.
 *
 * <p>Utilizada para armazenar detalhes das classes durante a analise
 * de metricas de codigo fonte.</p>
 *
 */
public class ClassInfo {
    /**
     * Nome da classe.
     */
    private String name;

    /**
     * Lista de metodos da classe.
     */
    private List<MethodInfo> methods;

    /**
     * Lista de atributos da classe.
     */
    private List<AtributeInfo> atributes;

    /**
     * Numero da linha inicial da classe no ficheiro fonte.
     */
    private int startLine;

    /**
     * Numero da linha final da classe no ficheiro fonte.
     */
    private int endLine;

    //metricas agregadas a classe
    /**
     * Linhas de codigo (LOC) da classe.
     */
    private int loc; //linhas de codigo

    /**
     * Numero de metodos (NOM) da classe.
     */
    private int nom; //numero de metodos

    /**
     * Numero de atributos (NOA) da classe.
     */
    private int noa; //numero de atributos

    /**
     * Profundidade da arvore de heranca (DIT) da classe.
     */
    private int dit; //depth of inheritance tree

    /**
     * Numero de classes filhas (NOC) da classe.
     */
    private int noc; //numero de classes filhas - number of children

    /**
     * Media de linhas de codigo por metodo.
     */
    private double avgLocMethod; //media de linhas de codigo por metodo

    /**
     * Coupling between objects (CBO) da classe.
     */
    private int CBO; //coupling between objects

    /**
     * Complexidade ciclomatica total da classe.
     */
    private int CiclomaticComplexity; //complexidade ciclomatica total da classe

    /**
     * Numero medio de parametros por metodo.
     */
    private double NPRM; //numero medio de parametros por metodo

    //construtor
    /**
     * Construtor da classe ClassInfo.
     * @param name nome da classe
     * @param startLine linha inicial da classe no ficheiro fonte
     * @param endLine linha final da classe no ficheiro fonte
     */
    public ClassInfo(String name, int startLine, int endLine) {
        this.name = name;
        this.methods = new ArrayList<MethodInfo>();
        this.atributes = new ArrayList<AtributeInfo>();
        this.startLine = startLine;
        this.endLine = endLine;
        this.loc = endLine - startLine + 1;
    }

    //getters
    /**
     * Retorna o nome da classe.
     * @return nome da classe
     */
    public String getName() {
        return name;
    }

    /**
     * Retorna a lista de metodos da classe.
     * @return lista de metodos
     */
    public List<MethodInfo> getMethods() {
        return methods;
    }

    /**
     * Retorna a lista de atributos da classe.
     * @return lista de atributos
     */
    public List<AtributeInfo> getAtributes() {
        return atributes;
    }

    /**
     * Retorna a linha inicial da classe no ficheiro fonte.
     * @return linha inicial
     */
    public int getStartLine() {
        return startLine;
    }

    /**
     * Retorna a linha final da classe no ficheiro fonte.
     * @return linha final
     */
    public int getEndLine() {
        return endLine;
    }

    /**
     * Retorna as linhas de codigo (LOC) da classe.
     * @return linhas de codigo
     */
    public int getLoc() {
        return loc;
    }

    /**
     * Retorna o numero de metodos (NOM) da classe.
     * @return numero de metodos
     */
    public int getNom() {
        return methods.size();
    }

    /**
     * Retorna o numero de atributos (NOA) da classe.
     * @return numero de atributos
     */
    public int getNoa() {
        return atributes.size();
    }

    /**
     * Retorna a profundidade da arvore de heranca (DIT) da classe.
     * @return DIT
     */
    public int getDit() {
        return dit;
    }

    /**
     * Retorna o numero de classes filhas (NOC) da classe.
     * @return NOC
     */
    public int getNoc() {
        return noc;
    }

    /**
     * Retorna o Coupling between objects (CBO) da classe.
     * @return CBO
     */
    public int getCBO() {return CBO;}

    /**
     * Retorna a complexidade ciclomatica total da classe.
     * @return CiclomaticComplexity
     */
    public int getCiclomaticComplexity() {return CiclomaticComplexity;}

    /**
     * Retorna o numero medio de parametros por metodo.
     * @return NPRM
     */
    public double getNPRM() {return NPRM;}

    /**
     * Retorna a media de linhas de codigo por metodo.
     * @return avgLocMethod
     */
    public double getAvgLocMethod() {
        return avgLocMethod;
    }

    //setters

    /**
     * Define a profundidade da arvore de heranca (DIT) da classe.
     * @param dit profundidade da arvore de heranca
     */
    public void setDit(int dit) {
        this.dit = dit;
    }

    /**
     * Define o numero de classes filhas (NOC) da classe.
     * @param noc numero de classes filhas
     */
    public void setNoc(int noc) {
        this.noc = noc;
    }

    /**
     * Define o Coupling between objects (CBO) da classe.
     * @param CBO CBO da classe
     */
    public void setCBO(int CBO) {this.CBO = CBO;}

    /**
     * Define a complexidade ciclomatica total da classe.
     * @param CiclomaticComplexity complexidade ciclomatica total da classe
     */
    public void setCiclomaticComplexity(int CiclomaticComplexity) {this.CiclomaticComplexity = CiclomaticComplexity;}

    /**
     * Define o numero medio de parametros por metodo.
     * @param NPRM numero medio de parametros por metodo
     */
    public void setNPRM(double NPRM) {this.NPRM = NPRM;}

    /**
     * Define a media de linhas de codigo por metodo.
     * @param avgLocMethod media de linhas de codigo por metodo
     */
    public void setAvgLocMethod(double avgLocMethod) {
        this.avgLocMethod = avgLocMethod;
    }

    /**
     * Define a lista de metodos da classe.
     * @param methods lista de metodos
     */
    public void setMethods(List<MethodInfo> methods) {this.methods=methods;}

    /**
     * Define a lista de atributos da classe.
     * @param atributes lista de atributos
     */
    public void setAtributes(List<AtributeInfo> atributes) {this.atributes=atributes;}

    //Adicionar metodos e atributos
    /**
     * Adiciona um metodo a lista de metodos da classe.
     * @param method metodo a ser adicionado
     */
    public void addMethod(MethodInfo method) {
        methods.add(method);
    }

    /**
     * Adiciona um atributo a lista de atributos da classe.
     * @param atribute atributo a ser adicionado
     */
    public void addAtribute(AtributeInfo atribute) {
        atributes.add(atribute);
    }


}
