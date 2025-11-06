package Grupo_Diurno_A;

import org.eclipse.jdt.core.dom.*;

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

}
