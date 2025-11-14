package Grupo_Diurno_A;

import java.nio.file.Path;
import java.util.List;

public class ProjectAnalyser {

    private FileScanner fileScanner;
    private MetricCalculator metricCalculator;
    //private CSVmaker csvMaker;
    private List<ClassInfo> classInfos;

    // Imaginei que quando o user mete o caminho:
    // 1. Na GUI, chamamos o construtor do projectAnalyser, que faz a analise completa do ficheiro
    // 2. Na GUI, depois de completar a analise, chamamos a funcao getResults
    // 3. Se o user quiser exportar, ou criamos logo em analyseProject
    //      ou criamos e enviamos apenas quando chamamos a funcao exportResults
    public ProjectAnalyser(Path path){
        analyseProject(path);
    }

    // Nao sei se faz sentido ter esta funcao
    // podemos fazer logo no construtor
    private void analyseProject(Path path){
        fileScanner = new FileScanner(path);
        metricCalculator = new MetricCalculator(fileScanner.getCompilationUnits());
        classInfos = metricCalculator.getClasses();
    }

    public List<ClassInfo> getResults(){
        return classInfos;
    }

    // TODO Completar quando fizermos a class CSVmaker
    public void exportResults(Path path){

    }
}
