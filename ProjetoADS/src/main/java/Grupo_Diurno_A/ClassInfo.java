package Grupo_Diurno_A;

import java.util.ArrayList;
import java.util.List;

public class ClassInfo {
    private String name;
    private List<MethodInfo> methods;
    private List<AtributeInfo> atributes;
    private int startLine;
    private int endLine;

    //metricas agregadas a classe
    private int loc; //linhas de codigo
    private int nom; //numero de metodos
    private int noa; //numero de atributos
    private int dit; //depth of inheritance tree
    private int noc; //numero de classes filhas - number of children
    private double avgLocMethod; //media de linhas de codigo por metodo
    private int CBO; //coupling between objects
    private int CiclomaticComplexity; //complexidade ciclomatica total da classe
    private double NPRM; //numero medio de parametros por metodo

    //construtor
    public ClassInfo(String name, int startLine, int endLine) {
        this.name = name;
        this.methods = new ArrayList<MethodInfo>();
        this.atributes = new ArrayList<AtributeInfo>();
        this.startLine = startLine;
        this.endLine = endLine;
        this.loc = endLine - startLine + 1;
    }

    //getters
    public String getName() {
        return name;
    }
    public List<MethodInfo> getMethods() {
        return methods;
    }
    public List<AtributeInfo> getAtributes() {
        return atributes;
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
    public int getNom() {
        return methods.size();
    }
    public int getNoa() {
        return atributes.size();
    }
    public int getDit() {
        return dit;
    }
    public int getNoc() {
        return noc;
    }
    public int getCBO() {return CBO;}
    public int getCiclomaticComplexity() {return CiclomaticComplexity;}
    public double getNPRM() {return NPRM;}
    public double getAvgLocMethod() {
        return avgLocMethod;
    }

    //setters
    public void setDit(int dit) {
        this.dit = dit;
    }
    public void setNoc(int noc) {
        this.noc = noc;
    }
    public void setCBO(int CBO) {this.CBO = CBO;}

    public void setCiclomaticComplexity(int CiclomaticComplexity) {this.CiclomaticComplexity = CiclomaticComplexity;}
    public void setNPRM(double NPRM) {this.NPRM = NPRM;}

    public void setAvgLocMethod(double avgLocMethod) {
        this.avgLocMethod = avgLocMethod;
    }


    public void setMethods(List<MethodInfo> methods) {this.methods=methods;}
    public void setAtributes(List<AtributeInfo> atributes) {this.atributes=atributes;}

    //Adicionar metodos e atributos
    public void addMethod(MethodInfo method) {
        methods.add(method);
    }
    public void addAtribute(AtributeInfo atribute) {
        atributes.add(atribute);
    }


}
