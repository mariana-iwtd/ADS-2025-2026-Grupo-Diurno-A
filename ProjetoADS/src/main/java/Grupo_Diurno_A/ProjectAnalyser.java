package Grupo_Diurno_A;

import java.nio.file.Path;
import java.util.List;

/**
 * A classe ProjectAnalyser e' o ponto central para iniciar a analise de metricas
 * de codigo fonte em um dado projeto.
 *
 * Esta classe une as operacoes necessarias:
 * <ul>
 *     <li>leitura de ficheiros ({@link FileScanner});</li>
 *     <li>calculo de metricas ({@link MetricCalculator});</li>
 *     <li>exportacao dos resultados ({@link CSVmaker})</li>
 * </ul>
 *
 */
public class ProjectAnalyser {

    /** Objeto da classe {@link FileScanner}. */
    private FileScanner fileScanner;
    /** Objeto da classe {@link MetricCalculator}. */
    private MetricCalculator metricCalculator;
    //private CSVmaker csvMaker;
    /** A lista de informacao das classes ({@link ClassInfo}) resultantes do calculo de metricas. */
    private List<ClassInfo> classInfos;

    /**
     * Construtor da classe ProjectAnalyser.
     *
     * <p>Inicia a analise de metricas para o caminho de projeto fornecido
     * chamando o metodo analyseProject.</p>
     *
     * @param path O caminho para a raiz do projeto a ser analisado.
     */
    public ProjectAnalyser(Path path){
        analyseProject(path);
    }

    /**
     * Executa a sequencia completa de analise do projeto.
     *
     * <p>A sequencia inclui:
     * <ul>
     *     <li>1. Leitura de ficheiros Java no Path dado (FileScanner).</li>
     *     <li>2. Criacao da Arvore de Sintaxe Abstrata (AST).</li>
     *     <li>3. Calculo das metricas (MetricCalculator) para cada classe.</li>
     * </ul>
     * </p>
     *
     * @param path O caminho para a raiz do projeto.
     */
    private void analyseProject(Path path){
        fileScanner = new FileScanner(path);
        metricCalculator = new MetricCalculator(fileScanner.getCompilationUnits());
        classInfos = metricCalculator.getClasses();
    }

    /**
     * Devolve a lista de informacao das classes e metricas obtidas apos a analise.
     *
     * @return A lista de objetos ClassInfo, onde cada objeto contem as metricas de uma classe.
     */
    public List<ClassInfo> getResults(){
        return classInfos;
    }

    /**
     * Exporta os resultados da analise para um ficheiro CSV no caminho especificado.
     *
     * @param path O caminho onde o ficheiro CSV sera salvo.
     */
    public void exportResults(String path){
        new CSVmaker(path, classInfos);
    }
}
