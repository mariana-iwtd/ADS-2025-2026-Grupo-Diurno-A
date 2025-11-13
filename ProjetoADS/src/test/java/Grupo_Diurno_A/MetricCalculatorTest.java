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

public class MetricCalculatorTest {

    private static MetricCalculator metricas;
    private static Path path;

    private static ClassInfo A;
    private static ClassInfo B;
    private static ClassInfo C;
    private static ClassInfo D;

    @BeforeAll
    static void setUpAll() throws IOException {
        // Criar diretório temporário
        path = Files.createTempDirectory("projectTest");

        // Criar subdiretório "src" que o FileScanner espera
        Path srcPath = Files.createDirectories(path.resolve("src"));

        // Ficheiro com um método que contém um switch-case
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

        // Ficheiro com um atributo e um método simples
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

        // Criar instância do FileScanner e do MetricCalculator
        FileScanner scanner = new FileScanner(path);
        metricas = new MetricCalculator(scanner.getCompilationUnits());


        A = getClass("A");
        B = getClass("B");
        C = getClass("C");
        D = getClass("D");

    }

    @AfterAll
    static void tearDownAll() throws IOException {
        Files.walk(path)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    public static ClassInfo getClass(String name){
        List<ClassInfo> ci = metricas.getClasses();

        for(ClassInfo c : ci){
            if(c.getName().equals(name))
                return c;
        }

        return null;
    }

    @Test
    // Verifica se o objeto MetricCalculator foi criado corretamente
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

    // Verifica se as ClassInfo foram bem criadas;
    // Verifica ao mesmo tempo se as metricas NOA e NOM estao bem
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

    @Test
    // Verifica se calcula a complexidade ciclomatica (MCC) de forma correta
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

    @Test
    // Verifica se calcula o numero de linhas (LOC) corretamente
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

    @Test
    // Verifica se calcula a profundidade da heranca (DIT) corretamente
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

    @Test
    // Verifica se o numero de filhos e calculado de forma correta
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

    @Test
    // Verifica se a media de linhas de codigo por metodo esta correta
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

    @Test
    // Verifica se o calculo de acupolamento?? esta correto
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

    @Test
    // Verifica se calcula o numero medio de parametros por metodo
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
