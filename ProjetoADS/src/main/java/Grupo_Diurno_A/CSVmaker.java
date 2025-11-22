package Grupo_Diurno_A;

import com.opencsv.CSVWriter;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * A classe CSVmaker e' responsavel por gerar um ficheiro CSV a partir de uma lista de objetos
 * {@code ClassInfo}.
 *
 * O ficheiro gerado utiliza o ponto e virgula (';') como separador de campos,
 * garantindo uma abertura correta em folha de calculo com configuracoes regionais europeias.
 *
 */
public class CSVmaker {

    /** O caminho onde o ficheiro CSV sera guardado. */
    private String filePath;

    /**
     * Construtor da classe CSVmaker.
     *
     * Ao ser instanciado, inicia imediatamente o processo de exportacao
     * dos dados para o ficheiro CSV.
     *
     * @param filePath O caminho do diretorio onde o ficheiro sera criado.
     * @param data A lista de objetos {@code ClassInfo} contendo os dados a exportar.
     */
    public CSVmaker(String filePath, List<ClassInfo> data){
        this.filePath = filePath;
        export(data);
    }

    /**
     * Executa a exportacao dos dados para o ficheiro CSV.
     *
     * Cria o ficheiro {@code resultado_da_analise.csv} no diretorio especificado, escreve o cabeçalho
     * e itera sobre a lista de dados para escrever cada linha de metricas.
     *
     * @param data A lista de objetos {@code ClassInfo} a serem escritos no ficheiro.
     */
    private void export(List<ClassInfo> data){

        // cria a ficheiro no caminho dado pelo user
        File file = new File(filePath + "/resultado_da_analise.csv");
        try{
            //cria um FileWriter que nos permite escrever no ficheiro criadp
            FileWriter fileWriter = new FileWriter(file);

            //cria CSVWriter que nos permite escrever no ficheiro em formato CSV
            // mudei o dilimitador para ';', para formatar de forma correta quando abrimos o ficheiro
            CSVWriter csvWriter = new CSVWriter(fileWriter, ';',
                                                                CSVWriter.NO_QUOTE_CHARACTER,
                                                                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                                                                CSVWriter.DEFAULT_LINE_END);

            // Adiciona a 1 linha do ficheiro, header
            String[] header = {"Classe", "LOC", "NOM", "NOA", "DIT", "NOC", "CBO", "CC", "NPRM", "Avg LOC por metodo"};
            csvWriter.writeNext(header);

            // preenche as linhas com os dados da lista data
            for(ClassInfo c : data){
                String[] line = getNextLine(c);
                csvWriter.writeNext(line);
            }

            csvWriter.close();
            fileWriter.close();

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Prepara a linha de dados para um objeto {@code ClassInfo}.
     *
     * Extrai os valores das metricas do objeto {@code ClassInfo} e converte-os
     * para um array de strings para serem escritos no CSV.
     *
     * @param c O objeto {@code ClassInfo} do qual se extrairao os dados.
     * @return Um array de strings contendo todos os campos de metricas.
     */
    private String[] getNextLine(ClassInfo c){

        String classe = c.getName();
        String LOC = String.valueOf(c.getLoc());
        String NOM = String.valueOf(c.getNom());
        String NOA = String.valueOf(c.getNoa());
        String DIT = String.valueOf(c.getDit());
        String NOC = String.valueOf(c.getNoc());
        String CBO = String.valueOf(c.getCBO());
        String CC = String.valueOf(c.getCiclomaticComplexity());
        String NPRM = String.valueOf(c.getNPRM());
        String AvgLOC = String.valueOf(c.getAvgLocMethod());

        return new String[]{classe, LOC, NOM, NOA, DIT, NOC, CBO, CC, NPRM, AvgLOC};

    }

    //todo tirar o main quando ja nao for preciso
    public static void main(String[] args){
        //Caminho para o diretorio com codigo fonte a analisar
        //Ema
        //Path projectPath = Paths.get("C:/Users/emasi/OneDrive/Ambiente de Trabalho/licenciatura/2ano/workspace/Sokoban_EmaReis110847_MarianaCapela104715");
        //Mariana - 1º Grande 2º Mais Pequeno
        //Path projectPath = Paths.get("C:/Users/maria/Documents/Universidade/2Ano/1º Semestre/Programação Orientada por Objetos/Projeto/Sokoban_EmaReis110847_MarianaCapela104715");
        //Path projectPath = Paths.get("C:/Users/maria/Documents/Universidade/2Ano/1º Semestre/Programação Orientada por Objetos/Workspace/Exercicio_S2");
        //Nadia
        Path projectPath = Paths.get("/Users/nadiagavancha/Documents/6 semestre/ES/projeto/ProjetoES/ES-2024-25-2Sem-Quarta-LEI-A/Projeto_ES");

        FileScanner scanner = new FileScanner(projectPath);
        List<CompilationUnit> asts = scanner.getCompilationUnits();

        MetricCalculator m = new MetricCalculator(asts);
        List<ClassInfo> c= m.getClasses();

        CSVmaker csv = new CSVmaker("/Users/nadiagavancha/Documents/mestrado/ADS/teste", c);

        System.out.println(c.get(5).getMethods().get(0).getName());
        //System.out.println(c.get(0).getMethods().get(4).getName());//da erro pk a classe alvo n tem 5 metodos o que significa que a lista está correta :)
        System.out.println(c.get(8).getNoc()); //tem de dar 9 -no meu deu 10 pk tinha uma classe a mais no projeto e estive uns 20 minutos a tentar perceber o erro
        System.out.println(c.get(1).getDit());

    }
}
