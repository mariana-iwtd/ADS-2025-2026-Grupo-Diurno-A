package Grupo_Diurno_A;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de testes para a classe {@link CSVmaker}.
 * Verifica a correta criacao do ficheiro CSV e os dados nele contidos.
 */
class CSVmakerTest {

    /**
     * Pasta temporaria para os testes. E' criada automaticamente pelo JUnit antes dos testes
     * e eliminada apos a execucao dos mesmos.
     * Vai ser usada para guardar o ficheiro CSV gerado durante os testes.
     */
    @TempDir
    static Path pastaTeste;

    // listas de classes para testes
    /**
     * Lista com uma classe (para testar CSV com uma classe).
     */
    private static List<ClassInfo> classes1 = new ArrayList<>();

    /**
     * Lista com tres classes (para testar CSV com varias classes).
     */
    private static List<ClassInfo> classes2 = new ArrayList<>();

    /**
     * Lista vazia (para testar CSV vazio).
     */
    private static List<ClassInfo> classes3 = new ArrayList<>(); // lista vazia (para testar CSV vazio)

    /**
     * Configuracao inicial dos testes.
     * Cria varias instancias de {@link ClassInfo} com diferentes caracteristicas
     * para serem usadas nos diferentes testes. Adiciona as instancias 'as listas de teste.
     * Esta funcao e' executada uma vez antes de todos os testes.
     */
    @BeforeAll
    static void setUpAll() {
        // Classe 1 - tem um metodo e um atributo
        ClassInfo class1 = new ClassInfo("TestClass1", 0, 10);
        class1.addAtribute(new AtributeInfo("attr1", "int"));
        class1.addMethod(new MethodInfo("method1", 0, 5, 2));
        class1.setCBO(1);
        class1.setNPRM(2.0);
        class1.setNoc(0);
        class1.setAvgLocMethod(3.0);
        class1.setCiclomaticComplexity(3);
        class1.setDit(1);

        // Classe 2 - nao tem metodos nem atributos, tem apenas o nome
        ClassInfo class2 = new ClassInfo("TestClass2", 0, 1);

        // Classe 3 - tem dois metodos e dois atributos
        ClassInfo class3 = new ClassInfo("TestClass3", 0, 20);
        class3.addAtribute(new AtributeInfo("attr1", "String"));
        class3.addAtribute(new AtributeInfo("attr2", "double"));
        class3.addMethod(new MethodInfo("method1", 0, 10, 5));
        class3.addMethod(new MethodInfo("method2", 11, 20, 4));
        class3.setCBO(2);
        class3.setNPRM(2);
        class3.setNoc(2);
        class3.setAvgLocMethod(2);
        class3.setCiclomaticComplexity(2);
        class3.setDit(2);

        // adicionar as classes às listas de teste
        classes1.add(class1);

        // lista com as 3 classes
        classes2.add(class3);
        classes2.add(class2);
        classes2.add(class1);
    }

    /**
     * Teste para verificar a criacao de um ficheiro CSV vazio (apenas com o cabecalho).
     * Garante que o ficheiro e' criado corretamente e que o cabecalho esta conforme o esperado.
     * @throws IOException
     */
    @Test
    void testEmptyCSV() throws IOException {
        //adiciona o caminho do ficheiro CSV na pasta de teste - com a funcao resolve, para testar se depois estq correto
        Path csvFile = pastaTeste.resolve("resultado_da_analise.csv");

        // Gera CSV com lista vazia
        new CSVmaker(pastaTeste.toString(), classes3);

        assertTrue(Files.exists(csvFile), "O ficheiro nao foi criado.");

        //le as linhas do ficheiro
        List<String> lines = Files.readAllLines(csvFile);

        // verifica que so existe uma linha - so existe o cabecalho
        assertEquals(1, lines.size(), "Para uma lista vazia, era esperado apenas o cabecalho.");

        //verifica se o cabecalho esta correto de acorqdo com o esperado
        String expectedHeader = "Classe;LOC;NOM;NOA;DIT;NOC;CBO;CC;NPRM;Avg LOC por metodo";// cabecalho esperado
        assertEquals(expectedHeader, lines.get(0), "O cabeçalho do CSV nao corresponde ao esperado.");
    }

    /**
     * Teste para verificar a criacao de um ficheiro CSV com uma unica classe.
     * Garante que o ficheiro e' criado corretamente e que os dados da classe estao corretos.
     * Garante que o numero de colunas e' o esperado e que o nome da classe esta correto no CSV.
     * @throws IOException
     */
    @Test
    void testCSVComUmaClasse() throws IOException {
        //adiciona o caminho do ficheiro CSV na pasta de teste - com a funcao resolve, para testar se depois foi criado
        Path csvFile = pastaTeste.resolve("resultado_da_analise.csv");

        // Gera CSV com uma classe
        new CSVmaker(pastaTeste.toString(), classes1);

        assertTrue(Files.exists(csvFile), "O ficheiro nao foi criado.");

        List<String> lines = Files.readAllLines(csvFile);

        // verifica que existem 2 linhas - cabecalho + 1 linha
        assertEquals(2, lines.size(), "Era esperado o cabecalho e uma linha de dados.");

        String dataLine = lines.get(1);//linha dos dadosda classe
        String[] cols = dataLine.split(";");
        //verifica que existem 10 colunas e que o nome da classe esta correto
        assertEquals(10, cols.length, "Cada linha de dados deve ter 10 colunas.");

        //verifica o nome da classe na primeira coluna
        assertEquals("TestClass1", cols[0], "O nome da classe na primeira coluna nao esta correto.");
    }

    /**
     * Teste para verificar a criacao de um ficheiro CSV com varias classes.
     * Garante que o ficheiro e' criado corretamente e que os dados das classes estao corretos.
     * Garante que o numero de linhas e' o esperado, que os nomes das classes estao corretos
     * no CSV e que estao na ordem correta.
     * @throws IOException
     */
    @Test
    void testCSVComVariasClasses() throws IOException {
        Path csvFile = pastaTeste.resolve("resultado_da_analise.csv");

        // Gera CSV com 3 classes
        new CSVmaker(pastaTeste.toString(), classes2);

        // verifica que o ficheiro foi criado
        assertTrue(Files.exists(csvFile), "O ficheiro CSV nao foi criado.");

        List<String> lines = Files.readAllLines(csvFile);

        // verifica que existem 4 linhas - cabecalho + 3 linhas de dados
        assertEquals(4, lines.size(), "Devia existir o cabeçalho + 3 linhas de dados.");

        String[] line1 = lines.get(1).split(";");
        String[] line2 = lines.get(2).split(";");
        String[] line3 = lines.get(3).split(";");

        // verifica se os nomes das classes estao corretos e na ordem correta
        assertEquals("TestClass3", line1[0]);
        assertEquals("TestClass2", line2[0]);
        assertEquals("TestClass1", line3[0]);
    }
}
