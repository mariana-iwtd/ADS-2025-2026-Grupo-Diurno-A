package Grupo_Diurno_A;

import org.eclipse.jdt.core.dom.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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

    /**
     * <p>O método {@code calculateNOM} calcula o número total de métodos
     * presentes nas diferentes classes analisadas, com base nas árvores
     * de sintaxe abstrata (AST) previamente geradas.</p>
     *
     * @param asts  lista de {@link CompilationUnit} correspondentes às ASTs dos ficheiros a analisar
     * @return      número total de métodos nos ficheiros .java a analisar
     */
    public static int calculateNOM(List<CompilationUnit> asts){
        //a variavel necessita de ser um array pois e' a unica forma de podermos alterar o valor dentro da funcao visit
        int[] NOM = {0};

        //percorremos todos os ast
        for(CompilationUnit ast: asts){
            //este metodo percorre recursivamente a arvore
            ast.accept(new ASTVisitor() {
                //sempre que encontrar um no' do tipo MethodDeclaration (um metodo) esta funcao sera chamada
                @Override
                public boolean visit(MethodDeclaration no){
                    NOM[0]++;
                    return super.visit(no);
                }
            });
        }

        return NOM[0];
    }

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

    public static void main(String[] args){
        //Caminho para o diretorio com codigo fonte a analisar
        //Ema
        //Path projectPath = Paths.get("C:/Users/emasi/OneDrive/Ambiente de Trabalho/licenciatura/2ano/workspace/Sokoban_EmaReis110847_MarianaCapela104715");
        //Mariana
        Path projectPath = Paths.get("C:/Users/maria/Documents/Universidade/2Ano/1º Semestre/Programação Orientada por Objetos/Workspace/Exercicio_S2");
        //Nadia
        //Path projectPath =

        // Criar instncia do FileScanner
        FileScanner scanner = new FileScanner(projectPath);

        //Numero total de metodos
        int NOM = MetricCalculator.calculateNOM(scanner.getCompilationUnits());
        System.out.println("Numero de metodos (NOM): " + NOM);
    }

}
