package Grupo_Diurno_A;

import org.eclipse.jdt.core.dom.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    //--------------- NOM: Number of Methods --------------------------

    /**
     * <p>O método {@code calculateNOM} calcula o número de métodos da
     * AST fornecida (NOM — <i>Number of Methods</i>). </p>
     *
     * @param ast   árvore de sintaxe abstrata ({@link CompilationUnit}) correspondente ao ficheiro a analisar
     * @return      número total de métodos na AST fornecida
     */
    public static int calculateNOM(CompilationUnit ast){
        //a variavel necessita de ser um array pois e' a unica forma de podermos alterar o valor dentro da funcao visit
        int[] NOM = {0};

        //percorremos todos os ast
        ast.accept(new ASTVisitor() {
            //sempre que encontrar um no' do tipo MethodDeclaration (um metodo) esta funcao sera chamada
            @Override
            public boolean visit(MethodDeclaration no){
                NOM[0]++;
                return super.visit(no);
            }
        });

        return NOM[0];
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

    //TODO JAVADOC

    // Função para calcular o valor de DIT
    public int calculateDIT(CompilationUnit ast){

        //Variavel que vai guardar as informacoes completas da classe (nome, herencas, ...)
        // Foi feita desta maneira para poder chama-la dentro da funcao visit
        final ITypeBinding[] binding = new ITypeBinding[1];
        // variavel que guarda o numero herenças da classe
        int DIT = 0;

        // Vamos percorrer a AST para encontrar o binding da classe
        ast.accept(new ASTVisitor() {
            @Override
            public boolean visit(TypeDeclaration node) {
                // Esta metrica e' para classes, interfaces seguem outras metricas
                if (!node.isInterface()) {
                    binding[0] = node.resolveBinding();
                }
                return true;
            }
        });

        // enquanto houver classes herdadas para visitar (superclasses), percorremos as varias classes
        // B - herda de C - herda de A - herda de Object - null -> DIT = 3
        while(!binding[0].getName().equals("Object")) {

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

    //TODO JavaDoc
    
    // funcao que calcula a complexidade de uma classe
    public int calculateMCC(CompilationUnit ast){

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
    public static int calculateNOC(List<CompilationUnit> asts, CompilationUnit ast){
        int[] NOC = {0};
        String nomeClasse = getMainClassName(ast);

        //percorremos todos os ast
        for(CompilationUnit astAnalisar: asts){
            //percorre recursivamente a arvore
            astAnalisar.accept(new ASTVisitor() {
                @Override
                //a funcao e' chamada sempre que encontrar uma classe/interface
                public boolean visit(TypeDeclaration no) {
                    //se for filho da classe a analisar, incrementa o NOC
                    if (isSon(nomeClasse, no))
                        NOC[0]++;

                    return super.visit(no);
                }
            });
        }

        return NOC[0];
    }

    //todo tirar o main quando ja nao for preciso
    public static void main(String[] args){
        //Caminho para o diretorio com codigo fonte a analisar
        //Ema
        //Path projectPath = Paths.get("C:/Users/emasi/OneDrive/Ambiente de Trabalho/licenciatura/2ano/workspace/Sokoban_EmaReis110847_MarianaCapela104715");
        //Mariana - 1º Grande 2º Mais Pequeno
        Path projectPath = Paths.get("C:/Users/maria/Documents/Universidade/2Ano/1º Semestre/Programação Orientada por Objetos/Projeto/Sokoban_EmaReis110847_MarianaCapela104715");
        //Path projectPath = Paths.get("C:/Users/maria/Documents/Universidade/2Ano/1º Semestre/Programação Orientada por Objetos/Workspace/Exercicio_S2");
        //Nadia
        //Path projectPath =

        // Criar instancia do FileScanner
        FileScanner scanner = new FileScanner(projectPath);
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

        }


    }

}
