package Grupo_Diurno_A;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.io.File;
import java.util.List;
import org.eclipse.jdt.core.dom.*;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Classe de testes unitarios para a classe {@code MetricCalculator}.
 *
 * Estes testes verificam se as metricas LOC, NOM, NOA, DIT, NOC, CBO, CC, NPRM e Avg LOC/Método
 * sao calculadas corretamente.
 *
 * Os testes utilizam diretorios temporarios criados em {@link #setUpAll()} e removidos em {@link #tearDownAll()}.
 *
 * @see MetricCalculatorTest
 */
public class MetricCalculatorTest {

    /** Instancia do {@code MetricCalculator} a ser testada. */
    private static MetricCalculator metricas;
    /** Caminho temporario criado para simular a raiz do projeto. */
    private static Path path;

    /** Objetos {@code ClassInfo} correspondentes as classes de teste. */
    private static ClassInfo A;
    private static ClassInfo B;
    private static ClassInfo C;
    private static ClassInfo D;

    /**
     * Configuracao inicial (executada uma vez antes de todos os testes).
     *
     * Cria um diretorio temporario e os 3 ficheiros Java:
     * <ul>
     *     <li><code>A.java</code> que contem 1 metodo com switch-case</li>
     *     <li><code>B.java</code> contem 1 atributo e 1 metodo simple</li>
     *     <li><code>C.java</code> contem uma classe aninhada D: contem 2 atributos e 2 metodos com if/else</li>
     * </ul>
     *
     * para construir o AST (Abstract Syntax Tree) e inicializar o {@code MetricCalculator}.</p>
     *
     * @throws IOException Se ocorrer um erro durante a criação dos ficheiros.
     */
    @BeforeAll
    static void setUpAll() throws IOException {
        // Criar diretorio temporario
        path = Files.createTempDirectory("projectTest");

        // Criar subdiretorio "src" que o FileScanner espera
        Path srcPath = Files.createDirectories(path.resolve("src"));

        // Ficheiro com um metodo que contem um switch-case
        String classA =
                "public class A extends B{\n" +
                        "    void exemploSwitch(int x) {\n" +
                        "        switch(x) {\n" +
                        "            case 1:\n" +
                        "                System.out.println(\"um\");\n" +
                        "                break;\n" +
                        "            case 2:\n" +
                        "                System.out.println(\"dois\");\n" +
                        "                break;\n" +
                        "            default:\n" +
                        "                System.out.println(\"outro\");\n" +
                        "        }\n" +
                        "    }\n" +
                        "}\n";

        // Ficheiro com um atributo e um metodo simples
        String classB =
                "public class B extends C{\n" +
                        "    int soma;\n" +
                        "    int sum(int a, int b){\n" +
                        "        return a + b;\n" +
                        "    }\n" +
                        "}\n";

        // Ficheiro com classe aninhada e if/else
        String classC =
                "public class C {\n" +
                        "    private class D {\n" +
                        "        int d1;\n" +
                        "        int d2;\n" +
                        "        boolean test1(int a, int b){\n" +
                        "            if(a > b)\n" +
                        "                while( a > b)\n" +
                        "                   a--;\n" +
                        "            else\n" +
                        "                return false;\n" +
                        "            return true;\n" +
                        "        }\n" +
                        "        boolean test2(int a, int b){\n" +
                        "            if(a > b)\n" +
                        "                while( a > b)\n" +
                        "                   a--;\n" +
                        "            else\n" +
                        "                return false;\n" +
                        "            return true;\n" +
                        "        }\n" +
                        "    }\n" +
                        "}\n";


        Files.write(srcPath.resolve("A.java"), classA.getBytes());
        Files.write(srcPath.resolve("B.java"), classB.getBytes());
        Files.write(srcPath.resolve("C.java"), classC.getBytes());

        // Criar instancia do FileScanner e do MetricCalculator
        FileScanner scanner = new FileScanner(path);
        metricas = new MetricCalculator(scanner.getCompilationUnits());


        A = getClass("A");
        B = getClass("B");
        C = getClass("C");
        D = getClass("D");

    }

    /**
     * Metodo executado uma vez apos todos os testes.
     * Limpa o diretorio temporario criado para os testes.
     *
     * @throws IOException se ocorrer erro na remocao dos ficheiros/diretorios
     */
    @AfterAll
    static void tearDownAll() throws IOException {
        Files.walk(path)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    /**
     * Metodo utilitario para obter um objeto {@code ClassInfo} pelo seu nome.
     *
     * @param name O nome da classe a procurar.
     * @return O objeto {@code ClassInfo} se encontrado, ou {@code null} caso contrario.
     */
    private static ClassInfo getClass(String name){
        List<ClassInfo> ci = metricas.getClasses();

        for(ClassInfo c : ci){
            if(c.getName().equals(name))
                return c;
        }

        return null;
    }

    /**
     * Testa se o objeto {@code MetricCalculator} foi criado corretamente.
     *
     * Verifica se
     * <ul>
     *     <li>a classe foi criadas;</li>
     *     <li>a lista de classe nao e' nula;</li>
     *     <li>o numero de classes encontradas e' o esperado (4: A, B, C, D);</li>
     *     <li>cada classe esperada existe na lista.</li>
     * </ul>
     */
    @Test
    void testMetricCalculatorExists() {
        // Verifica se o Objeto é nulo
        assertNotNull(metricas, "O objeto metricas esta vazio");

        // Verifica se a lista de ClassInfo é nula
        assertNotNull(metricas.getClasses(), "O objeto nao criou classes quando elas existem");

        // Verifica se tem o número correto de classes
        assertEquals(4, metricas.getClasses().size(), "É suposto exitir 4 classes, mas apenas foram criadas " + metricas.getClasses().size());

        // Verifica se as classes foram criadas corretamente; que nao e' nulo
        String[] names = {"A", "B", "C", "D"};

        for(String n : names){
            ClassInfo classInfo = getClass(n);

            assertNotNull(classInfo, "A class " + n + " nao existe na lista de ClassInfo");
        }
    }

    /**
     * Testa a metrica de Numero de Metodos (NOM) e Numero de Atributos (NOA).
     *
     * Verifica a contagem de metodos e atributos de cada classe de teste.
     */
    @Test
    void testClassInfo(){
        int NOM;
        int NOA;

        // Class A
        NOM = A.getNom();
        NOA = A.getNoa();

        assertEquals(1, NOM, "A tem 1 metodo e nao " + NOM);
        assertEquals(0, NOA, "A tem 0 atributos e nao " + NOA);

        // Class B
        NOM = B.getNom();
        NOA = B.getNoa();

        assertEquals(1, NOM, "B tem 1 metodo e nao " + NOM);
        assertEquals(1, NOA, "B tem 1 atributos e nao " + NOA);

        // Class C
        NOM = C.getNom();
        NOA = C.getNoa();

        assertEquals(0, NOM, "C tem 0 metodo e nao " + NOM);
        assertEquals(0, NOA, "C tem 0 atributos e nao " + NOA);

        // Class D
        NOM = D.getNom();
        NOA = D.getNoa();

        assertEquals(2, NOM, "D tem 2 metodo e nao " + NOM);
        assertEquals(2, NOA, "D tem 2 atributos e nao " + NOA);
    }

    /**
     * Testa a metrica de Complexidade Ciclomatica (CC).
     *
     * Verifica o calculo da complexidade esta correto.
     */
    @Test
    void testCalculateMCC() {
        int mcc = 0;

        // Class A
        mcc = A.getCiclomaticComplexity();

        assertEquals(4, mcc, "Complexidade da classe A dever ser 4 (1 base + 3 case) e nao " + mcc);

        // Class B
        mcc = B.getCiclomaticComplexity();

        assertEquals(1, mcc, "Complexidade da classe B dever ser 1 e nao " + mcc);

        // Class C
        mcc = C.getCiclomaticComplexity();

        assertEquals(0, mcc, "Complexidade da classe C dever ser 0 e nao " + mcc);

        // Class D
        mcc = D.getCiclomaticComplexity();

        assertEquals(6, mcc, "Complexidade da classe D dever ser 6 ((1 if + 1 while + 1 base)*2) e nao " + mcc);
    }

    /**
     * Testa a metrica de Linhas de Codigo (LOC).
     *
     * Verifica a contagem de linhas de codigo para cada classe.
     */
    @Test
    void testCalculateLOC() {
        int LOC = 0;
        int esperado = 0;

        // Class A
        LOC = A.getLoc();
        esperado = 49 - 37 + 1; // linha inicial = 37; linha final = 49

        assertEquals(esperado, LOC, "A tem 13 linhas de codigo e nao " + LOC);

        // Class B
        LOC = B.getLoc();
        esperado = 58 - 54 + 1; // linha inicial = 54; linha final = 58

        assertEquals(esperado, LOC, "B tem 5 linhas de codigo e nao " + LOC);

        // Class C
        LOC = C.getLoc();
        esperado = 83 - 63 + 1; // linha inicial = 63; linha final = 83

        assertEquals(esperado, LOC, "C tem 20 linhas de codigo e nao " + LOC);

        // Class D
        LOC = D.getLoc();
        esperado = 82 - 64 + 1; // linha inicial = 82; linha final = 64

        assertEquals(esperado, LOC, "D tem 20 linhas de codigo e nao " + LOC);
    }

    /**
     * Testa a metrica de Profundidade da Arvore de Herança (DIT - Depth of Inheritance Tree).
     *
     * Verifica a profundidade da herança de cada classe (contando a {@code Object} como 1).
     */
    @Test
    void testCalculateDIT(){
        int DIT = 0;

        // Class A
        DIT = A.getDit();

        assertEquals(3, DIT, "A profundidade da herenca de A e 3 (B - C - Object) e nao " + DIT);

        // Class B
        DIT = B.getDit();

        assertEquals(2, DIT, "A profundidade da herenca de B e 2 (C - Object) e nao " + DIT);

        // Class C
        DIT = C.getDit();

        assertEquals(1, DIT, "A profundidade da herenca de C e 1 (Object) e nao " + DIT);

        // Class D
        DIT = D.getDit();

        assertEquals(1, DIT, "A profundidade da herenca de D e 1 (Object) e nao " + DIT);
    }

    /**
     * Testa a metrica de Numero de Filhos (NOC - Number of Children).
     *
     * Verifica quantos filhos (subclasses) cada classe de teste possui.
     */
    @Test
    void testCalculateNOC(){
        int NOC = 0;

        // Class A
        NOC = A.getNoc();

        assertEquals(0, NOC, "A tem 0 filhos e nao " + NOC);

        // Class B
        NOC = B.getNoc();

        assertEquals(1, NOC, "B tem 1 filho (A) e nao " + NOC);

        // Class C
        NOC = C.getNoc();

        assertEquals(1, NOC, "A tem 1 filho (B) e nao " + NOC);

        // Class D
        NOC = D.getNoc();

        assertEquals(0, NOC, "D tem 0 filhos e nao " + NOC);
    }

    /**
     * Testa a metrica de Media de Linhas de Codigo por Metodo (Avg LOC/Metodo).
     *
     * Verifica a razao entre o LOC e o NOM (Numero de Metodos) para cada classe.</p>
     */
    @Test
    void testAvgLocMethod(){
        double Avg = 0;
        double esperado = 0;
        int linhas = 0;

        // Class A
        Avg = A.getAvgLocMethod();
        linhas = 49 - 37 + 1; // LOC
        esperado = linhas/1; // 1 metodo

        assertEquals(esperado, Avg, "A media de linhas de codigo por metodo de A e " + esperado + " e nao " + Avg);

        // Class B
        Avg = B.getAvgLocMethod();
        linhas = 58 - 54 + 1; // LOC
        esperado = linhas/1; // 1 metodo

        assertEquals(esperado, Avg, "A media de linhas de codigo por metodo de B e " + esperado + " e nao " + Avg);

        // Class C
        Avg = C.getAvgLocMethod();
        esperado = 0; // 0 metodo

        assertEquals(esperado, Avg, "A media de linhas de codigo por metodo de C e " + esperado + " e nao " + Avg);

        // Class D
        Avg = D.getAvgLocMethod();
        linhas = 82 - 64 + 1; // LOC
        esperado = linhas/2; // 2 metodos

        assertEquals(esperado, Avg, "A media de linhas de codigo por metodo de D e " + esperado + " e nao " + Avg);
    }

    /**
     * Testa a metrica de Acoplamento entre Objetos (CBO - Coupling Between Objects).
     *
     * Verifica o numero de outras classes a que a classe de teste esta acoplada
     * (e.g., por herança, utilizacao de atributos, ou chamadas de metodo).
     */
    @Test
    void testCalculateCBO(){
        int CBO = 0;
        int esperado = 0;

        // Class A
        CBO = A.getCBO();
        esperado = 1; // (extends B)

        assertEquals(esperado, CBO, "A tem " + esperado + " acupolamento e nao " + CBO);

        // Class B
        CBO = B.getCBO();
        esperado = 1; // (extends C)

        assertEquals(esperado, CBO, "B tem " + esperado + " acupolamento e nao " + CBO);

        // Class C
        CBO = C.getCBO();
        esperado = 0; // nao utiliza outras classes

        assertEquals(esperado, CBO, "C tem " + esperado + " acupolamento e nao " + CBO);

        // Class D
        CBO = D.getCBO();
        esperado = 0; // classes internas nao sao contadas

        assertEquals(esperado, CBO, "D tem " + esperado + " acupolamento e nao " + CBO);
    }

    /**
     * Testa a metrica de Numero Medio de Parametros por Metodo (NPRM - Number of Parameters per Method).
     *
     * Verifica a media do numero de parametros em todos os metodos da classe.
     */
    @Test
    void testCalculateNPRM() {
        double NPRM = 0;
        int esperado = 0;

        // Class A
        NPRM = A.getNPRM();
        esperado = 1; // 1 metodo - 1 parametro

        assertEquals(esperado, NPRM, "A media de parametros por metodo de A e " + esperado + " e nao " + NPRM);

        // Class B
        NPRM = B.getNPRM();
        esperado = 2; // 1 metodo - 2 parametro

        assertEquals(esperado, NPRM, "A media de parametros por metodo de B e " + esperado + " e nao " + NPRM);

        // Class C
        NPRM = C.getNPRM();
        esperado = 0; // 0 metodos

        assertEquals(esperado, NPRM, "A media de parametros por metodo de C e " + esperado + " e nao " + NPRM);

        // Class D
        NPRM = D.getNPRM();
        esperado = 4/2; // 2 metodo - 2 parametros cada

        assertEquals(esperado, NPRM, "A media de parametros por metodo de D e " + esperado + " e nao " + NPRM);
    }


}
