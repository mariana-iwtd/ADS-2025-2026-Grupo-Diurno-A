package Grupo_Diurno_A;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.*;
import java.util.stream.Stream;
//bibiliotecas JDT para AST
import org.eclipse.jdt.core.dom.*;




/**
 * A classe {@code FileScanner} e' responsavel por percorrer uma pasta de projeto,
 * identificar todos os ficheiros Java e gerar as respetivas arvores de sintaxe
 * abstrata (AST) utilizando a API JDT do eclipse.
 *
 * <p>Ponto inicial do processo de analise de metricas de codigo,
 * fornecendo uma lista de {@link File} e de {@link CompilationUnit} representando
 * a estrutura sintatica de cada classe analisada.</p>
 *
 * <p>Compativel com Java 9 ou superior (utiliza o diretorio {@code /jmods}).</p>
 *
 * @author Grupo
 * @version 1.0
 */
public class FileScanner {
    /** Lista de ficheiros .java encontrados no projeto. */
    private List<File> files;

    /** Caminho base do projeto, pasta onde se encontram os ficheiros. */
    private String projectPath;

    /** Lista de unidades de compilacao (ASTs) geradas de cada um dos ficheiros da lista {@link #files}. */
    private List<CompilationUnit> compilationUnits;


    //CONSTRUTOR:
    /**
     * Construtor principal. Recebe o caminho base do projeto,
     * procura todos os ficheiros .java e gera as respetivas ASTs.
     *
     * @param path Caminho da pasta base do projeto a ser analisado.
     */
    public FileScanner(Path path) {
        this.projectPath = path.toString();
        this.files = getJavaFiles(path);
        this.compilationUnits = parseFilesAST(this.files, this.projectPath);
    }

    //GETTERS:
    /**
     * Retorna a lista de ficheiros Java identificados.
     *
     * @return Lista de ficheiros {@link File} com extensao .java encontrados na pasta base do projeto.
     */
    public List<File> getFiles() {
        return files;
    }

    /**
     * Retorna a lista de unidades de compilacao (ASTs) geradas.
     *
     * @return Lista de objetos {@link CompilationUnit} correspondentes a cada ficheiro Java.
     */
    public List<CompilationUnit>  getCompilationUnits() {
        return compilationUnits;
    }



    /**
     * Percorre recursivamente o diretório especificado e identifica todos os ficheiros com
     * a extensao .java.
     *
     * @param path Caminho da pasta base do projeto a ser analisado.
     * @return Lista de ficheiros {@link File} com extensao .java encontrados.
     * @throws RuntimeException Se ocorrer algum erro ao percorrer os ficheiros.
     */
    private List<File> getJavaFiles(Path path) {
        List<File> listaFicheiros = new ArrayList<>(); //lista que guardara os ficheiros .java

        //Files walk, percorre todas as pastas e ficheiros dentro de "path"
        try(Stream<Path> ficheiros = Files.walk(path)){
            ficheiros.forEach(ficheiro -> {
                //verifica se e' um ficheiro ou uma pasta, se for um ficheiro verifica se tem extensao .java e adiciona a lista
                if (Files.isRegularFile(ficheiro) && ficheiro.toString().endsWith(".java")) {

                    //to file - transforma path em ficheiro
                    listaFicheiros.add(ficheiro.toFile());
                }
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return listaFicheiros;
    }

    /**
     * Gera uma lista de arvores AST correspondentes aos ficheiros Java fornecidos.
     *
     * @param files       Lista de ficheiros .java a analisar.
     * @param projectPath Caminho base do projeto onde se encontram os ficheiros-fonte - necessario para resolucao de bindings.
     * @return Lista de objetos {@link CompilationUnit} gerados a partir dos ficheiros.
     */
    //funcao com AST parser para analisar o conteudo dos ficheiros .java
    private List<CompilationUnit> parseFilesAST(List<File> files,String projectPath){
        List<CompilationUnit> asts = new ArrayList<>();
        for (File file : files) {
            asts.add (parseClassFile(file, projectPath));
        }
        return asts;
    }

    /**
     * Analisa um ficheiro Java individual e devolve a sua arvore de sintaxe abstrata (AST).
     *
     * <p>Configura um {@link ASTParser) e ativa a resolucao de bindings,
     * permitindo identificar referencias a tipos, variaveis e metodos.</p>
     *
     * @param file        Ficheiro .java a ser analisado.
     * @param projectPath Caminho onde se encontra o ficheiro.
     * @return AST {@link CompilationUnit} correspondente ao ficheiro analisado.
     * @throws RuntimeException Se ocorrer algum erro ao ler o ficheiro ou criar a AST.
     */
    //funcao que retorna compuilation unit (AST) de um ficheiro .java
    private CompilationUnit parseClassFile(File file, String projectPath) {
        //Cria um JDT AST parser para Java 17
        ASTParser parser = ASTParser.newParser(AST.JLS17);

        // Define o conteudo do ficheiro a ser analisado - ficheiro .java completo - uma unidade de compilação
        parser.setKind(ASTParser.K_COMPILATION_UNIT);

        try {
            //le o conteudo do ficheiro e define como codigo fonte para o parser - o parser le um array de caracteres
            String code = new String(Files.readAllBytes(file.toPath()), java.nio.charset.StandardCharsets.UTF_8);
            parser.setSource(code.toCharArray());

            //parser precisa de saber o nome do ficheiro que esta a ser analisado, para o poder ligar ao resto do ~projeto
            parser.setUnitName(file.getName());

            //Ativa bindings para conseguimos obter mais informacoes sobre os elementos do codigo - referencias, tipos, etc
            parser.setResolveBindings(true);


            // Configurar ambiente do projeto (onde procurar outras classes e bibliotecas)
            String[] sources = { projectPath + "/src"}; // diretório com os ficheiros .java (ex: "src/main/java")
            String javaHome = System.getProperty("java.home"); // pasta do java instalado no sistema
            String[] classpath = { javaHome + "/jmods"  }; //para Java 9 e superior

            parser.setEnvironment(classpath, sources, new String[] { "UTF-8" }, true);


            //Cria a AST (compilation unit) a partir do conteudo do ficheiro - monitor a null porque nao estamos a usar eclipse ide
            CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);
            return compilationUnit;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
