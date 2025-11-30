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

        // Se fizermos assim, vai buscar as metodos de classes internas tambem, o que nao e' suposto
        /*type.accept(new ASTVisitor() {
            @Override
            public boolean visit(MethodDeclaration no){
                String name = no.getName().toString();
                int startLine = ast.getLineNumber(no.getStartPosition());
                int endLine = ast.getLineNumber(no.getStartPosition() + no.getLength())-1;
                int numeroDeParametros = no.parameters().size();
                MethodInfo methodInfo = new MethodInfo(name, startLine, endLine, numeroDeParametros);
                methodInfo.setCyclomaticComplexity(calculateCC_method(no)); //preenche a complexidade ciclomatica do metodo

                methods.add(methodInfo);

                return true;
            }
        });*/
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

        // Se fizermos assim, conta tambem os atributos de classes internas, o que nao e' suposto
        /*
        type.accept(new ASTVisitor() {
            //vai ver declaracoes de atributos
            @Override
            public boolean visit(FieldDeclaration no){
                String type_data = no.getType().toString();
                //pode haver mais do que um atributo declarado na mesma linha - no.fragments() devolve uma lista com todos os atributos declarados na linha
                //elementos da lista sao do tipo VariableDeclarationFragment
                //exemplo: int x, y; sao dois fragments
                List<VariableDeclarationFragment> fragments = no.fragments();
                for(VariableDeclarationFragment fragment : fragments){
                    String name = fragment.getName().toString();
                    AtributeInfo atributeInfo = new AtributeInfo(name, type_data);
                    atributes.add(atributeInfo);
                }
                return true;
            }
        });*/

        return atributes;
    }

    /**
     * Calcula a complexidade ciclomatica (CC) de um metodo especifico.
     * @param method no' {@link MethodDeclaration} correspondente ao metodo a analisar
     * @return Complexidade ciclomatica do metodo analisado
     */
    private int calculateCC_method(MethodDeclaration method){
        final int[] CC = {1}; //complexidade ciclomatica inicia em 1
        //System.out.println("A analisar metodo " + method.getName());

        method.accept(new ASTVisitor() {

            // Verificamos se o metodo tem statements que aumentem a sua complexidade
            // Verifica se tem ifs -> if (x > 0)
            @Override
            public boolean visit(IfStatement node) {
                // Teste
                //System.out.println("Tem If - +1");

                CC[0]++;
                return true;
            }

            // Verifica se tem fors -> for(...)
            @Override
            public boolean visit(ForStatement node) {
                // Teste
                //System.out.println("Tem for - +1");

                CC[0]++;
                return true;
            }

            // Verifica se tem forEachs -> for(Type x : list)
            @Override
            public boolean visit(EnhancedForStatement node) {
                // Teste
                //System.out.println("Tem forEach - +1");

                CC[0]++;
                return true;
            }

            // Verifica se tem whiles -> while(...)
            @Override
            public boolean visit(WhileStatement node) {
                // Teste
                //System.out.println("Tem while - +1");

                CC[0]++;
                return true;
            }

            // Verifica se tem dos -> do {...} while(...)
            @Override
            public boolean visit(DoStatement node) {
                // Teste
                //System.out.println("Tem do - +1");

                CC[0]++;
                return true;
            }

            // Verifica se existem switchs
            // se houver, retorna true para permitir avaliar o numero de cases
            @Override
            public boolean visit(SwitchStatement node) {
                // Teste
                //System.out.println("Tem switch entao vamos verificar o numero de cases");

                return true;
            }

            // Verifica se existem cases -> case:
            @Override
            public boolean visit(SwitchCase node) {
                // Teste
                //System.out.println("Tem case - +1");

                CC[0]++;
                return true;
            }

            // Verifica se tem caths -> catch(Exception e)
            @Override
            public boolean visit(CatchClause node) {
                // Teste
                //System.out.println("Tem catch - +1");

                CC[0]++;
                return true;
            }

            // Verifica se tem condicoes -> condition ? x : y
            @Override
            public boolean visit(ConditionalExpression node) {
                // Teste
                //System.out.println("Tem condicional - +1");

                CC[0]++;
                return true;
            }
        });

        //System.out.println("O metodo " + method.getName() + " tem MCC = " + CC[0]);
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

        // Vamos percorrer a AST para encontrar o binding da classe
        /*type.accept(new ASTVisitor() {
            @Override
            public boolean visit(TypeDeclaration node) {
                // Esta metrica e' para classes, interfaces seguem outras metricas
                if (!node.isInterface()) {
                    binding[0] = node.resolveBinding();
                }
                return true;

            }
        });*/

        if (!type.isInterface()) {
            binding[0] = type.resolveBinding();
        }

        // enquanto houver classes herdadas para visitar (superclasses), percorremos as varias classes
        // B - herda de C - herda de A - herda de Object - null -> DIT = 3
        if(binding[0] == null){
            return DIT;
        }
        //while (!binding[0].getName().equals("Object")) {
        while(binding[0].getSuperclass() != null){
            // guarda o nome da classe anterior para testes
            String temp = binding[0].getName();
            // vai buscar a classe de que herda
            binding[0] = binding[0].getSuperclass();
            // Para testar
            //System.out.println("Classe " + temp + " herda da classe " + binding[0].getName());

            // aumenta o contador
            DIT++;
            // Para teste
            //System.out.println("Numero de herancas atuais: " + DIT);
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
        //System.out.println(" A analisar MCC da class " + classInfo.getName());
        List <MethodInfo> metodos = classInfo.getMethods();
        int complexidadeCiclomaticaTotal = 0;
        for(MethodInfo metodo : metodos){
            complexidadeCiclomaticaTotal += metodo.getCyclomaticComplexity();
        }
        //System.out.println("A classe " + classInfo.getName() + " tem MCC = " + complexidadeCiclomaticaTotal);
        return complexidadeCiclomaticaTotal;
    }



    //todo: apagar todos os comentarios desnecessarios antes de entregar!!
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
        //Versao para conseguir ver os nomes dos filhos todo tirar tudo quando
//        if (no.getSuperclassType() != null)
//            if(!no.isInterface() && no.getSuperclassType().toString().equals(parentName)) {
//                System.out.println("Filho encontrado! - " + no.getName());
//                return true;}
//            else return false;
//        else return false;
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
        //System.out.println("Tipos da classe: " + types);
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

    //todo tirar o main quando ja nao for preciso
    public static void main(String[] args){
        //Caminho para o diretorio com codigo fonte a analisar
        //Ema
        Path projectPath = Paths.get("C:/Users/emasi/OneDrive/Ambiente de Trabalho/licenciatura/2ano/workspace/Sokoban_EmaReis110847_MarianaCapela104715");
        //Mariana - 1º Grande 2º Mais Pequeno
        //Path projectPath = Paths.get("C:/Users/maria/Documents/Universidade/2Ano/1º Semestre/Programação Orientada por Objetos/Projeto/Sokoban_EmaReis110847_MarianaCapela104715");
        //Path projectPath = Paths.get("C:/Users/maria/Documents/Universidade/2Ano/1º Semestre/Programação Orientada por Objetos/Workspace/Exercicio_S2");
        //Nadia
        //Path projectPath =

        // Criar instancia do FileScanner
        /*FileScanner scanner = new FileScanner(projectPath);
        List<CompilationUnit> asts = scanner.getCompilationUnits();

        System.out.println("Metricas Calculadas para Cada AST");

        for(CompilationUnit ast: asts){
            System.out.println("\nAST - " + getMainClassName(ast));

            //Numero de metodos
            int NOM = MetricCalculator.calculateNOM(ast);
            System.out.println("--> Numero de metodos (NOM): " + NOM);

            //Numero de atributos
            int NOA = MetricCalculator.calculateNOA(ast);
            System.out.println("--> Numero de atributos (NOA): " + NOA);

            //Numero de filhos
            int NOC = MetricCalculator.calculateNOC(asts, ast);
            System.out.println("--> Numero de filhos (NOC): " + NOC);

        }*/

        FileScanner scanner = new FileScanner(projectPath);
        List<CompilationUnit> asts = scanner.getCompilationUnits();

        MetricCalculator m = new MetricCalculator(asts);
        List<ClassInfo> c= m.getClasses();

        System.out.println(c.get(5).getMethods().get(0).getName());
        //System.out.println(c.get(0).getMethods().get(4).getName());//da erro pk a classe alvo n tem 5 metodos o que significa que a lista esta correta :)
        System.out.println(c.get(8).getNoc()); //tem de dar 9 -no meu deu 10 pk tinha uma classe a mais no projeto e estive uns 20 minutos a tentar perceber o erro
        System.out.println(c.get(1).getDit());

        //---------------------- Teste da nadia para o DIT -----------------------------------
        /*String source = "class A {} \n" +
            "class B extends A {} \n" +
            "class C extends B {} \n";

        ASTParser parser = ASTParser.newParser(AST.JLS17);
        parser.setSource(source.toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setResolveBindings(true);
        parser.setBindingsRecovery(true);
        // classpath must include jre
        parser.setUnitName("Test.java");
        parser.setEnvironment(
            new String[] { System.getProperty("java.home") + "/lib/jrt-fs.jar" }, // JDK/JRE classes
            new String[] {}, // no external source folders
            null,
            true
        );

        CompilationUnit ast = (CompilationUnit) parser.createAST(null);
        List<CompilationUnit>l= new ArrayList<>();
        l.add(ast);
        MetricCalculator dit = new MetricCalculator(l);
        System.out.println(dit.getClasses().get(0).getDit());*/

        // ------------------------ TESTE CBO -------------------------
        /*
        String source =
                "package p;\n" +
                        "\n" +
                        "import java.util.List;\n" +
                        "import java.io.IOException;\n" +
                        "\n" +
                        "class A implements Runnable {\n" +
                        "    List<String> nomes;   // conta List<String>\n" +
                        "    B b;                  // conta B\n" +
                        "\n" +
                        "    D[] m(C c) throws IOException { // conta D[] (nao normaliza), C e IOException\n" +
                        "        return null;\n" +
                        "    }\n" +
                        "\n" +
                        "    public void run() {}\n" +
                        "}\n" +
                        "\n" +
                        "class B {}\n" +
                        "class C {}\n" +
                        "class D {}\n";

        // Construir a AST do codigo de teste
        ASTParser parser = ASTParser.newParser(AST.JLS17);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setSource(source.toCharArray());
        parser.setUnitName("Teste.java");
        CompilationUnit cu = (CompilationUnit) parser.createAST(null);

        // Instanciar o MetricCalculator
        List<CompilationUnit> astAnalisar = new ArrayList<>();
        astAnalisar.add(cu);
        MetricCalculator mc = new MetricCalculator(astAnalisar);
        System.out.println(mc.getClasses().get(0).getCBO());

    */



    }

}
