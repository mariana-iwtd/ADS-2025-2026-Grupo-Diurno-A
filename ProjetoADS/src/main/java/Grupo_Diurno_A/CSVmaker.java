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
    public CSVmaker(String filePath, List<ClassInfo> data) throws IOException {
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
    private void export(List<ClassInfo> data) throws IOException {

        // cria a ficheiro no caminho dado pelo user
        File file = new File(filePath + "/resultado_da_analise.csv");
        //cria um FileWriter que nos permite escrever no ficheiro criado
        FileWriter fileWriter = new FileWriter(file);

        //cria CSVWriter que nos permite escrever no ficheiro em formato CSV
        // mudei o delimitador para ';', para formatar de forma correta quando abrimos o ficheiro
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

}
