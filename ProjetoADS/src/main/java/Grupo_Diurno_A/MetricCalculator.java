package Grupo_Diurno_A;

import org.eclipse.jdt.core.dom.*;

import java.lang.instrument.ClassDefinition;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * A classe {@code MetricCalculator} e' responsavel por calcular as metricas das
 * diferentes classes e metodos, a partir das arvores de sintaxe abstrata (AST)
 * previamente geradas pela classe {@link FileScanner}.
 *
 * <p>Analisa cada unidade de compilacao para extrair informacoes relevantes sobre
 * a estrutura e complexidade do codigo, permitindo a avaliacao de qualidade e
 * manutencao de software.</p>
 *
 * <p>Compativel com as estruturas AST produzidas pela API JDT do Eclipse.</p>
 */
public class MetricCalculator {

    /** Lista de informacoes de classes analisadas. */
    private List<ClassInfo> classes;

    /**
     * Construtor principal. Recebe a lista de ASTs e inicia o processo
     *
     * @param asts lista de {@link CompilationUnit} correspondentes 'as ASTs dos ficheiros a analisar
     */
    public MetricCalculator(List<CompilationUnit> asts){
        classes = extractClassInfo(asts);
    }

    //getter

    /**
     * Retorna a lista de informacoes de classes analisadas.
     *
     * @return Lista de informacoes de classes ({@link ClassInfo}) calculadas
     */
    public List<ClassInfo> getClasses() {
        return classes;
    }


    /**
     * Extrai informacoes de classes a partir das arvores de sintaxe abstrata (AST)
     * Calcula as metricas para cada classe encontrada e junta todas as informacoes numa lista de ClassInfo
     *
     * @param asts lista de {@link CompilationUnit} correspondentes 'as ASTs dos ficheiros a analisar
     * @return Lista de informacoes de classes ({@link ClassInfo}) extraidas das ASTs fornecidas
     */
    private List<ClassInfo> extractClassInfo(List<CompilationUnit> asts){
        final List <ClassInfo> classInfos = new ArrayList<>();
        for (final CompilationUnit ast : asts){
            ast.accept(new ASTVisitor() {
                //sempre que encontrar um no' do tipo TypeDeclaration (uma classe ou interface) esta funcao sera chamada
                @Override
                public boolean visit(TypeDeclaration no){
                    String name= no.getName().toString();
                    int startLine = ast.getLineNumber(no.getStartPosition());
                    int endLine = ast.getLineNumber(no.getStartPosition() + no.getLength() - 1);

                    ClassInfo classInfo = new ClassInfo(name, startLine, endLine);

                    classInfo.setMethods(getMethods(no, ast)); //preenche a lista de metodos da classe
                    classInfo.setAtributes(getAtributes(no)); //preenche a lista de atributos da classe

                    classInfo.setDit(calculateDIT(no));//preenche o DIT da classe
                    classInfo.setNoc(calculateNOC(asts, no));//preenche o NOC da classe
                    classInfo.setCBO(calculateCBO(no));
                    classInfo.setCiclomaticComplexity(calculateMCC(classInfo)); //preenche a complexidade ciclomatica total da classe
                    classInfo.setNPRM(calculateNPRM(classInfo));


                    //--------------------CALCULAR MEDIA LOC POR METODO-----------------------
                    //AvgLOCMethod = LOC / NOM ou seja o numero de linhas da classe dividido pelo numero de metodos da mesma
                    if(classInfo.getNom()!=0)
                        classInfo.setAvgLocMethod((double)(classInfo.getLoc()/classInfo.getNom()));
                    else
                        classInfo.setAvgLocMethod(0);
                    classInfos.add(classInfo);
                    return true;

                }
            });
        }
        return classInfos;
    }

    /**
     * Preeenche a lista de metodos de uma classe com base no TypeDeclaration.
     * Preenche tambem a complexidade ciclomatica de cada metodo, as linhas de codigo e o numero de parametros.
     *
     * @param type no' {@link TypeDeclaration} correspondente 'a classe a analisar
     * @param ast no' {@link CompilationUnit} correspondente 'a AST do ficheiro a analisar
     * @return
     */
    private List<MethodInfo> getMethods(TypeDeclaration type, CompilationUnit ast){
        List<MethodInfo> methods = new ArrayList<>();

        MethodDeclaration[] methodDeclarations = type.getMethods();

        for(MethodDeclaration m : methodDeclarations){
            String name = m.getName().toString();
            int startLine = ast.getLineNumber(m.getStartPosition());
            int endLine = ast.getLineNumber(m.getStartPosition() + m.getLength())-1;
            int numeroDeParametros = m.parameters().size();
            MethodInfo methodInfo = new MethodInfo(name, startLine, endLine, numeroDeParametros);
            methodInfo.setCyclomaticComplexity(calculateCC_method(m)); //preenche a complexidade ciclomatica do metodo

            methods.add(methodInfo);
        }

        return methods;
    }

    /**
     * Preeenche a lista de atributos de uma classe com base no TypeDeclaration.
     *
     * @param type no' {@link TypeDeclaration} correspondente 'a classe a analisar
     * @return Lista de atributos ({@link AtributeInfo}) da classe analisada
     */
    private List<AtributeInfo> getAtributes(TypeDeclaration type){
        List<AtributeInfo> atributes = new ArrayList<>();

        FieldDeclaration[] fieldDeclarations = type.getFields();

        for (FieldDeclaration f : fieldDeclarations){
            String type_data = f.getType().toString();
            //pode haver mais do que um atributo declarado na mesma linha - no.fragments() devolve uma lista com todos os atributos declarados na linha
            //elementos da lista sao do tipo VariableDeclarationFragment
            //exemplo: int x, y; sao dois fragments
            List<VariableDeclarationFragment> fragments = f.fragments();
            for(VariableDeclarationFragment fragment : fragments){
                String name = fragment.getName().toString();
                AtributeInfo atributeInfo = new AtributeInfo(name, type_data);
                atributes.add(atributeInfo);
            }
        }

        return atributes;
    }

    /**
     * Calcula a complexidade ciclomatica (CC) de um metodo especifico.
     * @param method no' {@link MethodDeclaration} correspondente ao metodo a analisar
     * @return Complexidade ciclomatica do metodo analisado
     */
    private int calculateCC_method(MethodDeclaration method){
        final int[] CC = {1}; //complexidade ciclomatica inicia em 1

        method.accept(new ASTVisitor() {

            // Verificamos se o metodo tem statements que aumentem a sua complexidade
            // Verifica se tem ifs -> if (x > 0)
            @Override
            public boolean visit(IfStatement node) {
                CC[0]++;
                return true;
            }

            // Verifica se tem fors -> for(...)
            @Override
            public boolean visit(ForStatement node) {
                CC[0]++;
                return true;
            }

            // Verifica se tem forEachs -> for(Type x : list)
            @Override
            public boolean visit(EnhancedForStatement node) {
                CC[0]++;
                return true;
            }

            // Verifica se tem whiles -> while(...)
            @Override
            public boolean visit(WhileStatement node) {
                CC[0]++;
                return true;
            }

            // Verifica se tem dos -> do {...} while(...)
            @Override
            public boolean visit(DoStatement node) {
                CC[0]++;
                return true;
            }

            // Verifica se existem switchs
            // se houver, retorna true para permitir avaliar o numero de cases
            @Override
            public boolean visit(SwitchStatement node) {
                return true;
            }

            // Verifica se existem cases -> case:
            @Override
            public boolean visit(SwitchCase node) {
                CC[0]++;
                return true;
            }

            // Verifica se tem caths -> catch(Exception e)
            @Override
            public boolean visit(CatchClause node) {
                CC[0]++;
                return true;
            }

            // Verifica se tem condicoes -> condition ? x : y
            @Override
            public boolean visit(ConditionalExpression node) {
                CC[0]++;
                return true;
            }
        });

        return CC[0];
    }



    //--------------- NPRM: Number of Parameters per Method --------------------------
    /**
     * Calcula a metrica NPRM (Number of Parameters per Method) para uma classe.
     * Esta metrica representa o numero medio de parametros por metodo declarado
     * numa determinada classe.
     *
     * @param classInfo Informacao da classe a analisar ({@link ClassInfo})
     * @return O valor NPRM da classe.
     *         Caso a classe nao tenha metodos, retorna 0 para evitar divisao por zero.
     */
    private double calculateNPRM(ClassInfo classInfo){
        List<MethodInfo> metodos = classInfo.getMethods();

        int n_parametros = 0;
        int n_metodos = metodos.size();

        for(MethodInfo m : metodos){
            n_parametros += m.getNumeroDeParametros();
        }

        if(n_metodos == 0) return 0;

        return (double) n_parametros /n_metodos;
    }


    //--------------- DIT: Depth of Inheritance Tree --------------------------

    /**
     * Calcula o numero de niveis de heranca entre a classe analisada e
     * a classe raiz da hierarquia (normalmente {@code java.lang.Object}).
     *
     * @param type no' {@link TypeDeclaration} correspondente 'a classe a analisar
     * @return DIT da classe analisada.
     */
    public int calculateDIT(TypeDeclaration type){

        //Variavel que vai guardar as informacoes completas da classe (nome, herancas, ...)
        // Foi feita desta maneira para poder chama-la dentro da funcao visit
        final ITypeBinding[] binding = new ITypeBinding[1];
        // variavel que guarda o numero herancas da classe
        int DIT = 0;

        if (!type.isInterface()) {
            binding[0] = type.resolveBinding();
        }

        // enquanto houver classes herdadas para visitar (superclasses), percorremos as varias classes
        // B - herda de C - herda de A - herda de Object - null -> DIT = 3
        if(binding[0] == null){
            return DIT;
        }

        while(binding[0].getSuperclass() != null){
            // guarda o nome da classe anterior para testes
            String temp = binding[0].getName();
            // vai buscar a classe de que herda
            binding[0] = binding[0].getSuperclass();

            // aumenta o contador
            DIT++;
        }

        return DIT;
    }

    //--------------- MCC: McCabe (Complexidade Ciclomatica) --------------------------

    /**
     * Calcula a metrica de complexidade ciclomatica (MCC) de uma classe.
     *
     * @param classInfo Informacao da classe a analisar ({@link ClassInfo}).
     * @return A complexidade ciclomatica total da classe.
     */
    public int calculateMCC(ClassInfo classInfo){
        List <MethodInfo> metodos = classInfo.getMethods();
        int complexidadeCiclomaticaTotal = 0;
        for(MethodInfo metodo : metodos){
            complexidadeCiclomaticaTotal += metodo.getCyclomaticComplexity();
        }

        return complexidadeCiclomaticaTotal;
    }



    //--------------- NOC: Number of Children --------------------------

    /**
     * <p>Verifica se o no' recebido representa uma classe filha de uma classe
     * com o nome dado.</p>
     *
     * @param parentName    nome da classe que se pretende verificar se e' classe mae
     * @param no            no' {@link TypeDeclaration} correspondente 'a classe a analisar
     * @return              {@code true} se o no' representar uma subclasse de {@code parentName}, caso contrario {@code false}
     */
    private static boolean isSon(String parentName, TypeDeclaration no){
        if (no.getSuperclassType() != null)
            return !no.isInterface() && no.getSuperclassType().toString().equals(parentName);
        else return false;
    }

    /**
     * <p>Calcula a metrica NOC (<i>Number of Children</i>) para cada classe
     * presente nas arvores de sintaxe abstrata (AST) fornecidas.</p>
     *
     * @param asts  lista de {@link CompilationUnit} correspondentes 'as ASTs dos ficheiros a analisar
     * @return      o valor NOC da classe analisada
     */
    public static int calculateNOC(List<CompilationUnit> asts, TypeDeclaration type){
        int[] NOC = {0};
        String nomeClasse = type.getName().toString();//getMainClassName(ast);

        //percorremos todos os ast
        for(CompilationUnit astAnalisar: asts){
            //percorre recursivamente a arvore
            astAnalisar.accept(new ASTVisitor() {
                @Override
                //a funcao e' chamada sempre que encontrar uma classe/interface
                public boolean visit(TypeDeclaration no) {
                    //se for filho da classe a analisar, incrementa o NOC
                    if(!no.getName().toString().equals(nomeClasse))
                        if (isSon(nomeClasse, no))
                            NOC[0]++;

                    return super.visit(no);
                }
            });
        }
        return NOC[0];
    }

    //--------------- CBO: Coupling Between Objects --------------------------
    /**
     * <p>Calcula a metrica CBO (<i>Coupling Between Objects</i>) para uma classe
     *
     * Conta o numero de classes distintas com as quais a classe analisada, atraves de heranca,
     * interfaces implementadas, tipos de campos, tipos de retorno de metodos,
     * @param type no' {@link TypeDeclaration} correspondente 'a classe a analisar
     * @return o valor CBO da classe analisada
     */
    public int calculateCBO(TypeDeclaration type){
        List<String> types = new ArrayList<>();
        // Heranca
        if (type.getSuperclassType() != null)
            types = addType(type.getSuperclassType(), types);

        // Campos (tipo + inicializadores)
        for (FieldDeclaration fd : type.getFields()) {
            types = addType(fd.getType(), types);
        }

        //para os metodos ver retorno parametros e excecoes
        for (MethodDeclaration m : type.getMethods()) {
            //retorno
            types = addType(m.getReturnType2(), types);
            //parametros
            List<SingleVariableDeclaration> vars = m.parameters();
            for (SingleVariableDeclaration s : vars) {
                types = addType(s.getType(), types);
            }
            for (Object excecoes : m.thrownExceptionTypes()) {
                types = addType((Type) excecoes, types);
            }
        }

        // nao contar a propria classe
        types.remove(type.getName().getIdentifier());

        return types.size();

    }

    /**
     * Adiciona o nome do tipo 'a lista de tipos distintos,
     * se nao for um tipo primitivo, e se ainda nao estiver na lista.
     * @param type tipo a adicionar
     * @param types lista de tipos distintos
     * @return lista atualizada de tipos distintos
     */
    private static List<String> addType(Type type, List<String> types) {
        if (type == null) return types;

        // para extrair o tipo do array
        if (type.isArrayType()) return addType(((ArrayType) type).getElementType(), types);

        // saber o tipo dos genericos List<E>->List
        if (type.isParameterizedType()) type = ((ParameterizedType) type).getType();

        //nome do tipo
        String typeName = type.toString();

        //tipos que nao contam - tipos primitivos e String e void
        if (typeName.equals("int") || typeName.equals("double") || typeName.equals("float") ||
            typeName.equals("long") || typeName.equals("char") || typeName.equals("boolean") ||
            typeName.equals("byte") || typeName.equals("short") || typeName.equals("void") ||
            typeName.equals("String"))
            return types;

        if (!types.contains(typeName)) {
            types.add(typeName);
        }
        return types;
    }

}
