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
 * A classe {@code MetricCalculator} é responsável por calcular as métricas das
 * diferentes classes e métodos, a partir das árvores de sintaxe abstrata (AST)
 * previamente geradas pela classe {@link FileScanner}.
 *
 * <p>Analisa cada unidade de compilação para extrair informações relevantes sobre
 * a estrutura e complexidade do código, permitindo a avaliação de qualidade e
 * manutenção de software.</p>
 *
 * <p>Compatível com as estruturas AST produzidas pela API JDT do Eclipse.</p>
 */
public class MetricCalculator {

    List<ClassInfo> classes;
    public MetricCalculator(List<CompilationUnit> asts){
        classes = extractClassInfo(asts);
    }

    //getters
    public List<ClassInfo> getClasses() {
        return classes;
    }

    private List<ClassInfo> extractClassInfo(List<CompilationUnit> asts){
        final List <ClassInfo> classInfos = new ArrayList<>();
        for (final CompilationUnit ast : asts){
            ast.accept(new ASTVisitor() {
                //sempre que encontrar um no' do tipo TypeDeclaration (uma classe ou interface) esta funcao sera chamada
                @Override
                public boolean visit(TypeDeclaration no){
                    String name= no.getName().toString();
                    int startLine = ast.getLineNumber(no.getStartPosition());
                    int endLine = ast.getLineNumber(no.getStartPosition() + no.getLength())-1;
                    ClassInfo classInfo = new ClassInfo(name, startLine, endLine);

                    classInfo.setMethods(getMethods(no, ast)); //preenche a lista de metodos da classe
                    classInfo.setAtributes(getAtributes(no)); //preenche a lista de atributos da classe

                    classInfo.setDit(calculateDIT(no));//preenche o DIT da classe
                    classInfo.setNoc(calculateNOC(asts, no));//preenche o NOC da classe
                    //classInfo.setCBO(); //todo completar
                    classInfo.setCiclomaticComplexity(calculateMCC(classInfo)); //preenche a complexidade ciclomatica total da classe
                    classInfo.setNPRM(calculateNPRM());//todo completar


                    //--------------------CALCULAR MEDIA LOC POR METODO-----------------------
                    //AvgLOCMethod = LOC / NOM ou seja o número de linhas da classe dividido pelo nuero de metodos da mesma
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

    private List<MethodInfo> getMethods(TypeDeclaration type, CompilationUnit ast){
        List<MethodInfo> methods = new ArrayList<>();

        type.accept(new ASTVisitor() {
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
        });
        return methods;
    }

    private List<AtributeInfo> getAtributes(TypeDeclaration type){
        List<AtributeInfo> atributes = new ArrayList<>();

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
        });
        return atributes;
    }

    //todo copiei do codigo da nadia, para calcular para cada metodo
    //calcula complexidade ciclomatica por metodo
    private int calculateCC_method(MethodDeclaration method){
        final int[] CC = {1}; //complexidade ciclomática inicia em 1

        method.accept(new ASTVisitor() {

            // Verificamos se o metodo tem statements que aumentem a sua complexidade
            // Verifica se tem ifs -> if (x > 0)
            @Override
            public boolean visit(IfStatement node) {
                // Teste
                System.out.println("Tem If - +1");

                CC[0]++;
                return true;
            }

            // Verifica se tem fors -> for(...)
            @Override
            public boolean visit(ForStatement node) {
                // Teste
                System.out.println("Tem for - +1");

                CC[0]++;
                return true;
            }

            // Verifica se tem forEachs -> for(Type x : list)
            @Override
            public boolean visit(EnhancedForStatement node) {
                // Teste
                System.out.println("Tem forEach - +1");

                CC[0]++;
                return true;
            }

            // Verifica se tem whiles -> while(...)
            @Override
            public boolean visit(WhileStatement node) {
                // Teste
                System.out.println("Tem while - +1");

                CC[0]++;
                return true;
            }

            // Verifica se tem dos -> do {...} while(...)
            @Override
            public boolean visit(DoStatement node) {
                // Teste
                System.out.println("Tem do - +1");

                CC[0]++;
                return true;
            }

            // Verifica se existem switchs
            // se houver, retorna true para permitir avaliar o nº de cases
            @Override
            public boolean visit(SwitchStatement node) {
                // Teste
                System.out.println("Tem switch então vamos verificar o nº de cases");

                return true;
            }

            // Verifica se existem cases -> case:
            @Override
            public boolean visit(SwitchCase node) {
                // Teste
                System.out.println("Tem case - +1");

                CC[0]++;
                return true;
            }

            // Verifica se tem caths -> catch(Exception e)
            @Override
            public boolean visit(CatchClause node) {
                // Teste
                System.out.println("Tem catch - +1");

                CC[0]++;
                return true;
            }

            // Verifica se tem condicoes -> condition ? x : y
            @Override
            public boolean visit(ConditionalExpression node) {
                // Teste
                System.out.println("Tem condicional - +1");

                CC[0]++;
                return true;
            }
        });
        return CC[0];
    }


    //todo calcular NPRM julgo que e' bastante simples so ir buscar ao Class info a lista dos metodos e somar o num de todos os parametros e dividir pelo numero de metodos
    //todo criei a funcao para n dar erro mas n preenchi nada
    private double calculateNPRM(){
        return 0;
    }




    //todo julgo que estas funcoes nom e noa n sao precisas
    //--------------- NOM: Number of Methods --------------------------

    /**
     * <p>O método {@code calculateNOM} calcula o número de métodos da
     * AST fornecida (NOM — <i>Number of Methods</i>). </p>
     *
     * @param ast   árvore de sintaxe abstrata ({@link CompilationUnit}) correspondente ao ficheiro a analisar
     * @return      número total de métodos na AST fornecida
     */
    public static int calculateNOM(TypeDeclaration type){
        //a variavel necessita de ser um array pois e' a unica forma de podermos alterar o valor dentro da funcao visit
        int[] NOM = {0};

        //percorremos todos os ast
        type.accept(new ASTVisitor() {
            //sempre que encontrar um no' do tipo MethodDeclaration (um metodo) esta funcao sera chamada
            @Override
            public boolean visit(MethodDeclaration no){
                NOM[0]++;
                return super.visit(no);
            }
        });

        return NOM[0];
    }

    //--------------- NPRM: Number of Parameters per Method --------------------------

    /**
     * Calcula a métrica NPRM (Number of Parameters per Method) para uma classe.
     *
     * Esta métrica representa o número médio de parâmetros por método declarado
     * numa determinada classe.
     *
     * @param ast A CompilationUnit correspondente ao ficheiro fonte analisado.
     * @return O valor NPRM da classe.
     *         Caso a classe não tenha métodos, retorna 0 para evitar divisão por zero.
     */
    public static int calculateNPRM(CompilationUnit ast){
        final int[] metodos = new int[1];
        final int[] parametros = new int[1];

        // Vamos percurrer a ast
        ast.accept(new ASTVisitor() {

            // Vamos verificar quantos parametros cada metodo recebe
            @Override
            public boolean visit(MethodDeclaration node){
                metodos[0]++;
                parametros[0] += node.parameters().size();

                //teste
                System.out.println("parametros do metodo " + node.getName() + ": " + node.parameters());

                return true;
            }
        });

        // Caso a classe nao tenha metodos
        if(metodos[0] == 0) return 0;

        // retorna o calculo da media de parametros que cada metodo desta classe recebe (NPRM)
        return (int) parametros[0]/metodos[0];
    }

    //--------------- NOA: Number of Attributes --------------------------

    /**
     * <p>O método {@code calculateNOA} calcula o número total de atributos
     * (NOA — <i>Number of Attributes</i>) de um AST fornecido. </p>
     *
     * @param ast   árvore de sintaxe abstrata ({@link CompilationUnit}) correspondente ao ficheiro a analisar
     * @return      número total de atributos declarados na AST fornecida
     */
    public static int calculateNOA(CompilationUnit ast){
        int[] NOA = {0};

        //percorremos todos os ast
        ast.accept(new ASTVisitor() {
            @Override
            //a funcao e' chamada sempre que encontrar uma declaracao de atributo
            public boolean visit(FieldDeclaration no){
                //adiciona o numero de atributos na mesma linha ao NOA total da classe
                //exemplo: int x, y --> 2 atributos na mesma linha
                NOA[0] += no.fragments().size();
                return super.visit(no);
            }
        });

        return NOA[0];
    }

    //--------------- DIT: Depth of Inheritance Tree --------------------------

    /**
     * Calcula o número de níveis de herança entre a classe analisada e
     * a classe raiz da hierarquia (normalmente {@code java.lang.Object}).
     *
     * @param ast A AST (CompilationUnit) correspondente ao código fonte da classe a analisar.
     * @return DIT da classe analisada.
     */
    public int calculateDIT(TypeDeclaration type){

        //Variavel que vai guardar as informacoes completas da classe (nome, herencas, ...)
        // Foi feita desta maneira para poder chama-la dentro da funcao visit
        final ITypeBinding[] binding = new ITypeBinding[1];
        // variavel que guarda o numero herenças da classe
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
            System.out.println("Classe " + temp + " herda da classe " + binding[0].getName());

            // aumenta o contador
            DIT++;
            // Para teste
            System.out.println("Numero de heranças atuais: " + DIT);
        }

        return DIT;
    }

    //--------------- MCC: McCabe (Complexidade Ciclomatica) --------------------------

    /**
     * Calcula a métrica de complexidade ciclomática (MCC) de uma classe.
     *
     * @param ast A AST (CompilationUnit) correspondente ao código fonte da classe a analisar.
     * @return A complexidade ciclomática total da classe.
     */

    /*public int calculateMCC(CompilationUnit ast){
        // variavel que guarda a complexidade da classe
        final int[] MCC_classe = new int[1];

        // Vamos percorrer a AST para encontrar e analisar os metodos da classe
        ast.accept(new ASTVisitor() {

            // guarda a complexidade do metodo que esta a ser analisado
            int MCC = 0;

            // visita os metodos, 1 a 1, permitindo as suas analises
            @Override
            public boolean visit(MethodDeclaration node) {
                // Teste
                System.out.println("Metodo a avaliar: " + node.getName());
                System.out.println("+1");

                MCC++;
                return true;
            }

            // Verificamos se o metodo tem statements que aumentem a sua complexidade

            // Verifica se tem ifs -> if (x > 0)
            @Override
            public boolean visit(IfStatement node){
                // Teste
                System.out.println("Tem If - +1");

                MCC++;
                return true;
            }

            // Verifica se tem fors -> for(...)
            @Override
            public boolean visit(ForStatement node){
                // Teste
                System.out.println("Tem for - +1");

                MCC++;
                return true;
            }

            // Verifica se tem forEachs -> for(Type x : list)
            @Override
            public boolean visit(EnhancedForStatement node){
                // Teste
                System.out.println("Tem forEach - +1");

                MCC++;
                return true;
            }

            // Verifica se tem whiles -> while(...)
            @Override
            public boolean visit(WhileStatement node){
                // Teste
                System.out.println("Tem while - +1");

                MCC++;
                return true;
            }

            // Verifica se tem dos -> do {...} while(...)
            @Override
            public boolean visit(DoStatement node){
                // Teste
                System.out.println("Tem do - +1");

                MCC++;
                return true;
            }

            // Verifica se existem switchs
            // se houver, retorna true para permitir avaliar o nº de cases
            @Override
            public boolean visit(SwitchStatement node){
                // Teste
                System.out.println("Tem switch então vamos verificar o nº de cases");

                return true;
            }

            // Verifica se existem cases -> case:
            @Override
            public boolean visit(SwitchCase node){
                // Teste
                System.out.println("Tem case - +1");

                MCC++;
                return true;
            }

            // Verifica se tem caths -> catch(Exception e)
            @Override
            public boolean visit(CatchClause node){
                // Teste
                System.out.println("Tem catch - +1");

                MCC++;
                return true;
            }

            // Verifica se tem condicoes -> condition ? x : y
            @Override
            public boolean visit(ConditionalExpression node){
                // Teste
                System.out.println("Tem condicional - +1");

                MCC++;
                return true;
            }


            // quando acaba a visita de um metodo, passa automaticamente para o proximo ate nao haver mais
            // apenas e' adicionado pq queremos fazer algo no fim da visita
            @Override
            public void endVisit(MethodDeclaration node){
                // Teste
                System.out.println("Metodo " + node.getName() + " tem complexidade " + MCC);

                // adicionamos ao valor total da complexidade da classe
                MCC_classe[0] += MCC;
                // reiniciamos a variavel MCC
                MCC = 0;
            }
        });


        return MCC_classe[0];
    }*/
    //versao simplificada da funcao acima que ja usa a funcao calculateCC_method
    public int calculateMCC(ClassInfo classInfo){
        List <MethodInfo> metodos = classInfo.getMethods();
        int complexidadeCiclomaticaTotal = 0;
        for(MethodInfo metodo : metodos){
            complexidadeCiclomaticaTotal += metodo.getCyclomaticComplexity();
        }
        return complexidadeCiclomaticaTotal;
    }



    //todo: apagar todos os comentarios desnecessarios antes de entregar!!
    //--------------- NOC: Number of Children --------------------------

    /**
     * <p>Verifica se o nó recebido representa uma classe filha de uma classe
     * com o nome dado.</p>
     *
     * @param parentName    nome da classe que se pretende verificar se é classe mãe
     * @param no            nó {@link TypeDeclaration} correspondente à classe a analisar
     * @return              {@code true} se o nó representar uma subclasse de {@code parentName}, caso contrário {@code false}
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
     * <p>Obtém o nome da classe principal representada pela árvore de sintaxe
     * abstrata (AST) recebida.</p>
     *
     * @param ast   árvore de sintaxe abstrata ({@link CompilationUnit}) correspondente ao ficheiro a analisar
     * @return      nome da classe principal do ficheiro, ou {@code null} caso não exista
     */
    //todo nao apaguei mas ja nao e usada
    public static String getMainClassName(CompilationUnit ast) {
        List types = ast.types(); // lista de todos os tipos (classes, interfaces, enums...) do ast
        //o primeiro em principio e' a classe publica que queremos tirar o nome
        if (!types.isEmpty() && types.get(0) instanceof TypeDeclaration) {
            TypeDeclaration type = (TypeDeclaration) types.get(0);
            return type.getName().toString();
        }
        return null;
    }

    /**
     * <p>Calcula a métrica NOC (<i>Number of Children</i>) para cada classe
     * presente nas árvores de sintaxe abstrata (AST) fornecidas.</p>
     *
     * @param asts  lista de {@link CompilationUnit} correspondentes às ASTs dos ficheiros a analisar
     * @return      mapa que associa o nome de cada classe ao seu número de subclasses diretas
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


    /*public static int CBO(CompilationUnit ast){
        ast.accept(new ASTVisitor() {
            @Override
            public boolean visit(TypeDeclaration typeDeclaration){
                return super.visit(no);
            }
        });
    }_*/

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
        //System.out.println(c.get(0).getMethods().get(4).getName());//da erro pk a classe alvo n tem 5 metodos o que significa que a lista está correta :)
        System.out.println(c.get(8).getNoc()); //tem de dar 9 -no meu deu 10 pk tinha uma classe a mais no projeto e estive uns 20 minutos a tentar perceber o erro
        System.out.println(c.get(1).getDit());

        //teste da nadia para o DIT
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



    }

}
