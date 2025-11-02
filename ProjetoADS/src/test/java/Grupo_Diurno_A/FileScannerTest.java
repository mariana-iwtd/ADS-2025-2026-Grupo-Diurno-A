package Grupo_Diurno_A;

import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;
import java.io.File;
import java.util.List;
import org.eclipse.jdt.core.dom.*;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * Testes unitarios para a classe {@link FileScanner}.
 * <p>
 * Este conjunto de testes valida:
 * <ul>
 *   <li>Selecao correta de ficheiros <code>.java</code> no diretório alvo;</li>
 *   <li>Geracao das {@link CompilationUnit} (AST) para os ficheiros Java;</li>
 *   <li>Verificacao da correcao das AST: Detetando metodos via {@link ASTVisitor} e resolvendo bindings (ex.: {@link IMethodBinding}) nas ASTs.</li>
 * </ul>
 *
 * Os testes utilizam diretorios temporarios criados em {@link #setUpAll()} e removidos em {@link #tearDown()}.
 * @see FileScanner
 */

public class FileScannerTest {

    /**
     * Caminho da pasta temporaria criada para os testes.
     */
    private static Path path;


    /**
     * Instancia do scanner a testar.
     */
    private static FileScanner scanner;

    /**
     * Metodo executado uma vez antes de todos os testes.
     * Cria um diretorio temporario e tres ficheiros de teste:
     * <ul>
     *   <li><code>A.java</code> com um metodo vazio;</li>
     *   <li><code>B.java</code> com um campo e um metodo simples;</li>
     *   <li><code>C.txt</code> (nao-Java) para validar filtragem de extensoes.</li>
     * </ul>
     * @throws IOException se ocorrer erro na criacao de diretorios/ficheiros
     */
    @BeforeAll
    static void setUpAll() throws IOException {
        // Criar diretorio temporario
        path = Files.createTempDirectory("projectTest");

        // Criar ficheiros de teste (.java e .outro, para teste)
        Files.write(path.resolve("A.java"), "public class A { void funcaoVazia() {} }".getBytes());
        Files.write(path.resolve("B.java"), "public class B { int soma; int sum(int a, int b){ return a+b; } }".getBytes());
        Files.write(path.resolve("C.txt"), "isto não é java".getBytes());

        // Criar instância do FileScanner
        scanner = new FileScanner(path);
    }

    /**
     * Metodo executado uma vez apos todos os testes.
     * Limpa o diretorio temporario criado para os testes.
     * @throws IOException se ocorrer erro na remocao dos ficheiros/diretorios
     */
    @AfterAll
    static void tearDown() throws IOException {
        // Apaga o diretório temporário após cada teste
        Files.walk(path)
                .sorted(Comparator.reverseOrder())
                .forEach(file -> {
                    try {
                        Files.delete(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }


    /**
     * Verifica que apenas ficheiros com extensao <code>.java</code> sao
     * identificados e que o numero de ficheiros detetados e' o esperado.
     * <ul>
     *   <li>Espera encontrar exatamente dois ficheiros Java;</li>
     *   <li>Garante que todos os ficheiros listados terminam em <code>.java</code>.</li>
     * </ul>
     */
    @Test
    void testGetJavaFiles() {
        //Verifica se o numero de ficheiros esta correto
        assertEquals(2, scanner.getFiles().size(), "Deveria encontrar 2 ficheiros .java, mas so encontra " + scanner.getFiles().size());

        //Verifica se todos os ficheiros tem extensao .java
        assertTrue(scanner.getFiles().stream().allMatch(f -> f.getName().endsWith(".java")), "Existem ficheiros sem a extensao .java a serem processados");
    }


    /**
     * Garante que o {@link FileScanner} lida corretamente com uma pasta sem
     * ficheiros Java, devolvendo uma lista vazia de ficheiros.
     *
     * @throws IOException se ocorrer erro na criacao/remocao da pasta temporaria
     */
    @Test
    void testNoJavaFiles() throws  IOException {
        // Criar diretorio temporario sem ficheiros .java
        Path emptyPath = Files.createTempDirectory("projetoVazio");

        FileScanner emptyScanner = new FileScanner(emptyPath);

        // Verifica se a lista de ficheiros esta vazia
        assertEquals(0, emptyScanner.getFiles().size(), "Deveria encontrar 0 ficheiros .java, mas encontra " + emptyScanner.getFiles().size());

        // Apaga o diretOrio temporario
        Files.delete(emptyPath);
    }


    /**
     * Valida a criacao das {@link CompilationUnit} (AST) para os ficheiros Java.
     * <ul>
     *   <li>Confirma que existem tantas ASTs quanto ficheiros Java;</li>
     *   <li>Garante que nenhuma unidade de compilacao e' nula.</li>
     * </ul>
     */
    @Test
    void testParseFilesAST() {
        List<CompilationUnit> compilationUnits = scanner.getCompilationUnits();

        //verifica se o numero de unidades de compilacao e' igual ao numero de ficheiros java
        assertEquals(2, compilationUnits.size(), "O numero de unidades de compilacao deveria ser 2 mas apenas foram criados " + compilationUnits.size());

        //verifica se as unidades de compilacao foram criadas
        for (CompilationUnit unit : scanner.getCompilationUnits()) {
            assertNotNull(unit, "Unidade de compilacao nula encontrada: " + unit);
        }
    }

    /**
     * Verifica se os metodos são detetados nas ASTs.
     * <p>
     * Para cada {@link CompilationUnit}, conta as ocorrencias de
     * {@link MethodDeclaration} e espera exatamente 1 metodo (de acordo com os
     * ficheiros de teste criados em {@link #setUpAll()}).
     */
    @Test
    void testMethodDetection() {
        List<CompilationUnit> compilationUnits = scanner.getCompilationUnits();

        for (CompilationUnit unit : compilationUnits) {
            final int[] count = {0};
            unit.accept(new ASTVisitor() {
                @Override
                public boolean visit(MethodDeclaration node) {
                    count[0]++;
                    String methodName = node.getName().getIdentifier();
                    assertNotNull(methodName, "Metodo sem nome encontrado: " + node);
                    return true;
                }
            });
            assertEquals(1, count[0], "Foi encontrado mais do que um metodo: " + unit);
        }
    }

    /**
     * Teste simples de resolucao de <em>bindings</em> para metodos.
     * <p>
     * Percorre as {@link CompilationUnit} e assegura que, para cada
     * {@link MethodDeclaration}, o {@link IMethodBinding} nao e' nulo.
     * <p>
     */
    @Test
    void testBindings() {
        List<CompilationUnit> compilationUnits = scanner.getCompilationUnits();

        for (CompilationUnit unit : compilationUnits) {
            unit.accept(new ASTVisitor() {
                @Override
                public boolean visit(MethodDeclaration node) {
                    IMethodBinding binding = node.resolveBinding();
                    assertNotNull(binding, "Binding nulo encontrado para o metodo: " + node.getName().getIdentifier());
                    return true;
                }
            });
        }
    }
}
