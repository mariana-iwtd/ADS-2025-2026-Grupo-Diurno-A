package Grupo_Diurno_A;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe {@code Controller} e' responsavel por gerir a interacao
 * do utilizador com a interface grafica definida no ficheiro FXML.
 * Contem os metodos ligados aos eventos dos botoes e elementos visuais.
 */
public class Controller {

    //Botoes
    /**
     * Botao para escolher a pasta do projeto a analisar.
     */
    @FXML
    private Button botaoEscolhaProjeto;

    /**
     * Botao para iniciar a analise do projeto selecionado.
     */
    @FXML
    private Button botaoAnalisar;

    /**
     * Botao para voltar ao ecra inicial, de escolha de projeto.
     */
    @FXML
    private Button botaoVoltar;

    /**
     * Botao para mostrar a tabela de resultados da analise.
     */
    @FXML
    private Button botaoTabela;

    /**
     * Botao para mostrar o grafico de Linhas de Codigo por Classe(LOC).
     */
    @FXML
    private Button botaoLOC;

    /**
     * Botao para mostrar o grafico de Numero de Metodos por Classe(NOM).
     */
    @FXML
    private Button botaoNOM;

    /**
     * Botao para mostrar o grafico de Numero de Atributos por Classe(NOA).
     */
    @FXML
    private Button botaoNOA;

    /**
     * Botao para mostrar o grafico de Profundidade da Arvore de Heranca por Classe(DIT).
     */
    @FXML
    private Button botaoDIT;

    /**
     * Botao para mostrar o grafico de Numero de Filhos de cada Classe(NOC).
     */
    @FXML
    private Button botaoNOC;

    /**
     * Botao para mostrar o grafico de Acoplamento entre Objetos por Classe(CBO).
     */
    @FXML
    private Button botaoCBO;

    /**
     * Botao para mostrar o grafico de Complexidade Ciclomatica por Classe(CC).
     */
    @FXML
    private Button botaoCC;

    /**
     * Botao para mostrar o grafico de Numero Medio de Parametros dos Metodos de Cada Classe(NPRM).
     */
    @FXML
    private Button botaoNPRM;

    /**
     * Botao para mostrar o grafico de Media do Numero de Linhas por Classe(AvgLOCMethod).
     */
    @FXML
    private Button botaoAvgLOCMethod;

    /**
     * Botao para exportar os resultados da analise para um ficheiro CSV.
     */
    @FXML
    private Button botaoExportar;


    //Labels
    /**
     * Label que mostra o nome da pasta escolhida pelo utilizador.
     */
    @FXML
    private Label pastaEscolhida;   //tera o nome da pasta que foi escolhida pelo user

    /**
     * Label que diz "Pasta Escolhida:" antes do nome da pasta escolhida.
     */
    @FXML
    private Label labelPE; //label que diz "Pasta Escolhida:"


    //Logotipo
    /**
     * ImageView que contem o logotipo da aplicacao CodeMetrics Analyzer.
     */
    @FXML
    private ImageView logotipo;


    //Estruturas de cada ecra
    /**
     * VBox que contem os elementos do ecra inicial (selecao de pasta e botao analisar).
     */
    @FXML
    private VBox vBoxInicial;

    /**
     * BorderPane que contem os elementos do ecra de resultados (tabela de resultados, grafico e botoes).
     */
    @FXML
    private BorderPane paneResultados;


    //Tabela
    /**
     * TableView (tabela) que exibe os resultados da analise em formato tabular.
     */
    @FXML
    private TableView tabela;

    /**
     * Coluna da tabela que exibe o nome das classes analisadas.
     * Cada uma das suas colunas e' associada a uma metrica.
     * Cada linha da tabela corresponde a uma classe analisada.
     */
    @FXML
    private TableColumn<ClassInfo, String> colNome;

    /**
     * Coluna da tabela que exibe as Linhas de Codigo (LOC) de cada classe.
     */
    @FXML
    private TableColumn<ClassInfo, Integer> colLOC;

    /**
     * Coluna da tabela que exibe o Numero de Metodos (NOM) de cada classe.
     */
    @FXML
    private TableColumn<ClassInfo, Integer> colNOM;

    /**
     * Coluna da tabela que exibe o Numero de Atributos (NOA) de cada classe.
     */
    @FXML
    private TableColumn<ClassInfo, Integer> colNOA;

    /**
     * Coluna da tabela que exibe a Profundidade da Arvore de Heranca (DIT) de cada classe.
     */
    @FXML
    private TableColumn<ClassInfo, Integer> colDIT;

    /**
     * Coluna da tabela que exibe o Numero de Filhos (NOC) de cada classe.
     */
    @FXML
    private TableColumn<ClassInfo, Integer> colNOC;

    /**
     * Coluna da tabela que exibe o Acoplamento entre Objetos (CBO) de cada classe.
     */
    @FXML
    private TableColumn<ClassInfo, Integer> colCBO;

    /**
     * Coluna da tabela que exibe a Complexidade Ciclomatica (CC) de cada classe.
     */
    @FXML
    private TableColumn<ClassInfo, Integer> colCC; // CiclomaticComplexity

    /**
     * Coluna da tabela que exibe o Numero Medio de Parametros dos Metodos (NPRM) de cada classe.
     */
    @FXML
    private TableColumn<ClassInfo, Double> colNPRM;

    /**
     * Coluna da tabela que exibe a Media do Numero de Linhas por Metodo (AvgLOCMethod) de cada classe.
     */
    @FXML
    private TableColumn<ClassInfo, Double> colAvgLOC;


    //Grafico
    /**
     * BarChart que exibe os resultados da analise em formato grafico.
     * Os seus dados sao preenchidos dinamicamente conforme o botao selecionado.
     */
    @FXML
    private BarChart<String, Number> grafico;


    /**
     * O diretorio selecionado pelo utilizador para analise.
     */
    private File selectedDir;

    /**
     * O objeto ProjectAnalyser que realiza a analise do projeto selecionado.
     */
    private ProjectAnalyser analyser;

    /**
     * Indica se o grafico e' o primeiro a ser exibido (para controlar a animacao).
     */
    private boolean primeiroGrafico = true;


    /**
     * Metodo de inicializacao chamado automaticamente apos o carregamento do FXML.
     * Configura o estado inicial dos elementos visuais da interface.
     * Atribui a imagem do logotipo, esconde elementos da tabela e grafico,
     * e configura as colunas da tabela para exibirem os dados corretamente.
     *
     * Mostra apenas o ecra inicial com os botoes de escolha de pasta e analise.
     */
    @FXML
    public void initialize() {
        logotipo.setImage(new Image("/Logotipo.png"));
        labelPE.setOpacity(0);
        pastaEscolhida.setOpacity(0);

        //janela da tabela
        paneResultados.setOpacity(0);
        paneResultados.setDisable(true);
        paneResultados.setManaged(false);
        botaoVoltar.setDisable(true);
        botaoVoltar.setOpacity(0);
        botaoExportar.setOpacity(0);
        botaoExportar.setDisable(true);
        tabela.setOpacity(0);
        tabela.setDisable(true);
        grafico.setOpacity(0);
        grafico.setDisable(true);

        //botoes da janela da tabela
        botaoLOC.setOpacity(0);
        botaoLOC.setDisable(true);
        botaoNOM.setOpacity(0);
        botaoNOM.setDisable(true);
        botaoNOA.setOpacity(0);
        botaoNOA.setDisable(true);
        botaoDIT.setOpacity(0);
        botaoDIT.setDisable(true);
        botaoNOC.setOpacity(0);
        botaoNOC.setDisable(true);
        botaoCBO.setOpacity(0);
        botaoCBO.setDisable(true);
        botaoCC.setOpacity(0);
        botaoCC.setDisable(true);
        botaoNPRM.setOpacity(0);
        botaoNPRM.setDisable(true);
        botaoAvgLOCMethod.setOpacity(0);
        botaoAvgLOCMethod.setDisable(true);

        configurarTabela();

    }

    /**
     * Configura as colunas da tabela para exibirem os dados corretos
     * associando cada coluna a uma propriedade da classe ClassInfo.
     */
    private void configurarTabela(){
        colNome.setCellValueFactory(new PropertyValueFactory<>("name"));
        colLOC.setCellValueFactory(new PropertyValueFactory<>("loc"));
        colNOM.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colNOA.setCellValueFactory(new PropertyValueFactory<>("noa"));
        colDIT.setCellValueFactory(new PropertyValueFactory<>("dit"));
        colNOC.setCellValueFactory(new PropertyValueFactory<>("noc"));
        colCBO.setCellValueFactory(new PropertyValueFactory<>("CBO"));
        colCC.setCellValueFactory(new PropertyValueFactory<>("ciclomaticComplexity"));
        colNPRM.setCellValueFactory(new PropertyValueFactory<>("NPRM"));
        colAvgLOC.setCellValueFactory(new PropertyValueFactory<>("avgLocMethod"));
    }

    /**
     * Metodo chamado quando o utilizador clica no botao de escolher pasta.
     * Abre um dialogo para selecionar um diretorio e atualiza a interface
     * com o nome da pasta escolhida.
     * Com base no diretorio selecionado, atualiza a label correspondente e e' atualizado o selectedDir.
     */
    @FXML
    private void onSelecionarPasta() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Selecione a pasta do projeto que quer analisar:");

        // Abrir o dialogo e guardar a pasta escolhida
        selectedDir = chooser.showDialog(null);

        if (selectedDir != null) {
            pastaEscolhida.setText(selectedDir.getName());
            pastaEscolhida.setOpacity(1);
            labelPE.setOpacity(1);
        }
        else{
            //Poe labels invisiveis caso nao tenha selecionado nada
            pastaEscolhida.setOpacity(0);
            labelPE.setOpacity(0);
        }
    }

    /**
     * Metodo chamado quando o utilizador clica no botao de analisar o projeto.
     *
     * Inicia a analise do projeto selecionado atualizando o objeto ProjectAnalyser
     * e atualiza a interface para exibir os resultados (tabela e graficos).
     *
     * Se nenhum diretorio foi selecionado, ou a pasta nao for do tipo esperado, mostra um alerta de erro.
     *
     * Ao iniciar a analise, o ecra inicial e' desativado bem como os seus elementos visuais,
     * e o ecra de resultados e' ativado, mostrando a tabela de resultados.
     */
    @FXML
    private void onAnalisar(){

        if(selectedDir==null){
            mostrarAlerta("Erro", "Nao selecionou nenhum ficheiro para analisar!");
            return;
        }
        try {
            //cria project analyser com o caminho do diretorio
            analyser = new ProjectAnalyser(selectedDir.toPath());

            if(analyser.getResults().isEmpty()){
                mostrarAlerta("Erro", "O projeto selecionado nao contem classes Java validas para analisar.");
                return;
            }

            //desativa e faz com que botoes desaparecam
            vBoxInicial.setOpacity(0);
            vBoxInicial.setDisable(true);

            botaoAnalisar.setOpacity(0);
            botaoAnalisar.setDisable(true);

            botaoEscolhaProjeto.setOpacity(0);
            botaoEscolhaProjeto.setDisable(true);

            labelPE.setOpacity(0);
            pastaEscolhida.setOpacity(0);

            logotipo.setOpacity(0);
            logotipo.setDisable(true);

            //novo ecra
            paneResultados.setOpacity(1);
            paneResultados.setDisable(false);
            paneResultados.setManaged(true);

            preencheTabela();
            tabela.setOpacity(1);
            tabela.setDisable(false);

            botaoVoltar.setDisable(false);
            botaoVoltar.setOpacity(1);

            botaoExportar.setDisable(false);
            botaoExportar.setOpacity(1);

            botaoTabela.getStyleClass().add("button-selecionado");
            botaoTabela.setDisable(false);
            botaoTabela.setOpacity(1);
            botaoLOC.setDisable(false);
            botaoLOC.setOpacity(1);
            botaoNOM.setDisable(false);
            botaoNOM.setOpacity(1);
            botaoNOA.setDisable(false);
            botaoNOA.setOpacity(1);
            botaoDIT.setDisable(false);
            botaoDIT.setOpacity(1);
            botaoNOC.setDisable(false);
            botaoNOC.setOpacity(1);
            botaoCBO.setDisable(false);
            botaoCBO.setOpacity(1);
            botaoCC.setDisable(false);
            botaoCC.setOpacity(1);
            botaoNPRM.setDisable(false);
            botaoNPRM.setOpacity(1);
            botaoAvgLOCMethod.setDisable(false);
            botaoAvgLOCMethod.setOpacity(1);

            Stage stage = (Stage) botaoAnalisar.getScene().getWindow();
            stage.setWidth(850);
            stage.setHeight(500);
        }catch (RuntimeException e){
            mostrarAlerta("Erro", "Ocorreu um erro ao analisar o projeto selecionado.\nCertifique-se que o projeto e' valido.");
        }
    }


    /**
     * Preenche a tabela de resultados com os dados obtidos pela analise do projeto.
     * Cada linha da tabela corresponde a uma classe analisada,
     * e cada coluna exibe uma metrica diferente.
     */
    public void preencheTabela(){
        //limpa dados antigos da tabela
        tabela.getItems().clear();

        List<ClassInfo> classes = analyser.getResults();

        //percorre a lista de classes
        for(ClassInfo classe: classes){
            tabela.getItems().add(classe);
        }

    }

    /**
     * Metodo chamado quando o utilizador clica no botao de voltar ao ecra inicial.
     * Restaura a interface para o estado inicial, escondendo a tabela e graficos,
     * e exibindo os botoes de escolha de pasta e analise.
     */
    @FXML
    public void onVoltar(){
        //desaparece a tabela, grid e botao voltar
        limparSelecaoBotoes();
        tabela.setOpacity(0);
        tabela.setDisable(true);
        grafico.setOpacity(0);
        grafico.setDisable(true);
        paneResultados.setOpacity(0);
        paneResultados.setDisable(true);
        paneResultados.setManaged(false);
        botaoVoltar.setOpacity(0);
        botaoVoltar.setDisable(true);
        botaoExportar.setOpacity(0);
        botaoExportar.setDisable(true);


        //voltam a aparecer os botoes e grid
        vBoxInicial.setOpacity(1);
        vBoxInicial.setDisable(false);

        botaoAnalisar.setOpacity(1);
        botaoAnalisar.setDisable(false);

        botaoEscolhaProjeto.setOpacity(1);
        botaoEscolhaProjeto.setDisable(false);

        labelPE.setOpacity(1);
        pastaEscolhida.setOpacity(1);

        logotipo.setOpacity(1);
        logotipo.setDisable(false);

        Stage stage = (Stage) botaoVoltar.getScene().getWindow();
        stage.setWidth(400);
        stage.setHeight(405);
    }

    /**
     * Limpa a selecao visual dos botoes de metrica, removendo a classe CSS
     * que indica o botao atualmente selecionado.
     */
    private void limparSelecaoBotoes() {
        botaoTabela.getStyleClass().remove("button-selecionado");
        botaoLOC.getStyleClass().remove("button-selecionado");
        botaoNOM.getStyleClass().remove("button-selecionado");
        botaoNOA.getStyleClass().remove("button-selecionado");
        botaoDIT.getStyleClass().remove("button-selecionado");
        botaoNOC.getStyleClass().remove("button-selecionado");
        botaoCBO.getStyleClass().remove("button-selecionado");
        botaoCC.getStyleClass().remove("button-selecionado");
        botaoNPRM.getStyleClass().remove("button-selecionado");
        botaoAvgLOCMethod.getStyleClass().remove("button-selecionado");
    }

    /**
     * Metodo chamado quando o utilizador clica no botao de mostrar a tabela de resultados.
     * Atualiza a interface para exibir a tabela e esconder o grafico.
     * Tambem atualiza a selecao visual dos botoes.
     */
    public void onBotaoTabela(){
        changeSelectedButtonColor(botaoTabela);
        //ativa a tabela e desativa o grafico se ainda nao estiverem
        if(!grafico.isDisable()) {
            grafico.setOpacity(0);
            grafico.setDisable(true);
            tabela.setOpacity(1);
            tabela.setDisable(false);
            botaoExportar.setOpacity(1);
            botaoExportar.setDisable(false);
        }
    }

    /**
     * Metodo chamado quando o utilizador clica no botao de mostrar o grafico de Linhas de Codigo por Classe (LOC).
     * Atualiza a interface para exibir o grafico correspondente.
     * Tambem atualiza a selecao visual dos botoes.
     */
    public void onLOC(){
        changeSelectedButtonColor(botaoLOC);

        List<Integer>locs=new ArrayList();

        for(ClassInfo classe:analyser.getResults())
            locs.add(classe.getLoc());

        activateGraph();
        preencherGrafico("Linhas de Codigo por Classe", locs);

    }

    /**
     * Metodo chamado quando o utilizador clica no botao de mostrar o grafico de Numero de Metodos por Classe (NOM).
     * Atualiza a interface para exibir o grafico correspondente.
     * Tambem atualiza a selecao visual dos botoes.
     */
    public void onNOM(){
        changeSelectedButtonColor(botaoNOM);

        List<Integer>noms=new ArrayList();

        for(ClassInfo classe:analyser.getResults())
            noms.add(classe.getNom());

        activateGraph();
        preencherGrafico("Numero de Metodos por Classe", noms);

    }

    /**
     * Metodo chamado quando o utilizador clica no botao de mostrar o grafico de Numero de Atributos por Classe (NOA).
     * Atualiza a interface para exibir o grafico correspondente.
     * Tambem atualiza a selecao visual dos botoes.
     */
    public void onNOA(){
        changeSelectedButtonColor(botaoNOA);
        List<Integer>noas=new ArrayList();

        for(ClassInfo classe:analyser.getResults())
            noas.add(classe.getNoa());

        activateGraph();
        preencherGrafico("Numero de Atributos por Classe", noas);
    }

    /**
     * Metodo chamado quando o utilizador clica no botao de mostrar o grafico de Profundidade da Arvore de Heranca por Classe (DIT).
     * Atualiza a interface para exibir o grafico correspondente.
     * Tambem atualiza a selecao visual dos botoes.
     */
    public void onDIT(){
        changeSelectedButtonColor(botaoDIT);

        List<Integer>dits=new ArrayList();

        for(ClassInfo classe:analyser.getResults())
            dits.add(classe.getDit());

        activateGraph();
        preencherGrafico("Profundidade da Arvore de Heranca por Classe", dits);
    }

    /**
     * Metodo chamado quando o utilizador clica no botao de mostrar o grafico de Numero de Filhos de cada Classe (NOC).
     * Atualiza a interface para exibir o grafico correspondente.
     * Tambem atualiza a selecao visual dos botoes.
     */
    public void onNOC(){
        changeSelectedButtonColor(botaoNOC);
        List<Integer>nocs=new ArrayList();

        for(ClassInfo classe:analyser.getResults())
            nocs.add(classe.getNoc());

        activateGraph();
        preencherGrafico("Numero de Filhos de cada Classe", nocs);
    }

    /**
     * Metodo chamado quando o utilizador clica no botao de mostrar o grafico de Acoplamento entre Objetos por Classe (CBO).
     * Atualiza a interface para exibir o grafico correspondente.
     * Tambem atualiza a selecao visual dos botoes.
     */
    public void onCBO(){
        changeSelectedButtonColor(botaoCBO);

        List<Integer>cbos=new ArrayList();

        for(ClassInfo classe:analyser.getResults())
            cbos.add(classe.getCBO());

        activateGraph();
        preencherGrafico("Acoplamento entre Objetos por Classe", cbos);
    }

    /**
     * Metodo chamado quando o utilizador clica no botao de mostrar o grafico de Complexidade Ciclomatica por Classe (CC).
     * Atualiza a interface para exibir o grafico correspondente.
     * Tambem atualiza a selecao visual dos botoes.
     */
    public void onCC(){
        changeSelectedButtonColor(botaoCC);

        List<Integer>ccs=new ArrayList();

        for(ClassInfo classe:analyser.getResults())
            ccs.add(classe.getCiclomaticComplexity());

        activateGraph();
        preencherGrafico("Complexidade Ciclomatica por Classe", ccs);
    }

    /**
     * Metodo chamado quando o utilizador clica no botao de mostrar o grafico de Numero Medio de Parametros dos Metodos de Cada Classe (NPRM).
     * Atualiza a interface para exibir o grafico correspondente.
     * Tambem atualiza a selecao visual dos botoes.
     */
    public void onNPRM(){
        changeSelectedButtonColor(botaoNPRM);
        List<Double>nprms=new ArrayList();

        for(ClassInfo classe:analyser.getResults())
            nprms.add(classe.getNPRM());

        activateGraph();
        preencherGrafico("Numero Medio de Parametros dos Metodos de Cada Classe", nprms);
    }

    /**
     * Metodo chamado quando o utilizador clica no botao de mostrar o grafico de Media do Numero de Linhas por Classe (AvgLOCMethod).
     * Atualiza a interface para exibir o grafico correspondente.
     * Tambem atualiza a selecao visual dos botoes.
     */
    public void onAvgLOC(){
        changeSelectedButtonColor(botaoAvgLOCMethod);

        List<Double>avgLocs=new ArrayList();

        for(ClassInfo classe:analyser.getResults())
            avgLocs.add(classe.getAvgLocMethod());

        activateGraph();
        preencherGrafico("Media do Numero de Linhas por Classe", avgLocs);
    }

    /**
     * Metodo chamado quando o utilizador clica no botao de exportar os resultados para um ficheiro CSV.
     * Abre um dialogo para selecionar o diretorio onde o ficheiro sera guardado
     * e chama o metodo de exportacao do ProjectAnalyser.
     * Mostra uma mensagem de sucesso ou erro conforme o resultado da operacao.
     */
    public void onExportar(){
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Selecione onde deseja guardar o ficheiro: ");

        // Abrir o dialogo e guardar a pasta escolhida
        File pasta = chooser.showDialog(null);

        if(pasta != null) {
            try {
                analyser.exportResults(pasta.getAbsolutePath());

                //mostra que transferiu o ficheiro com sucesso
                Alert alertaSucesso = new Alert(Alert.AlertType.INFORMATION);
                alertaSucesso.setTitle("Sucesso");
                alertaSucesso.setHeaderText(null);
                alertaSucesso.setContentText("O ficheiro CSV foi transferido com sucesso!");
                alertaSucesso.showAndWait();

            } catch (Exception e) {
                //caso ocorra um erro
                mostrarAlerta("Nao foi possivel exportar", "Ocorreu um erro: " + e.getMessage());
            }
        }
    }


    /**
     * Altera a cor do botao selecionado, indicando visualmente qual metrica esta ativa.
     * Remove a selecao dos outros botoes.
     * @param botao O botao que foi selecionado.
     */
    private void changeSelectedButtonColor(Button botao){
        limparSelecaoBotoes();
        botao.getStyleClass().add("button-selecionado");
    }

    /**
     * Ativa o grafico, tornando-o visivel e desativando a tabela.
     * Utilizado quando o utilizador seleciona um botao de metrica para exibir o grafico correspondente.
     * Caso o grafico ja esteja ativo, nao faz nada.
     */
    private void activateGraph(){
        if(grafico.isDisable()) {
            grafico.setOpacity(1);
            grafico.setDisable(false);
            tabela.setOpacity(0);
            tabela.setDisable(true);
            botaoExportar.setOpacity(0);
            botaoExportar.setDisable(true);
        }
    }

    /**
     * Preenche o grafico com os dados fornecidos.
     * Cria uma nova serie de dados com o nome especificado e adiciona os pontos correspondentes
     * a cada classe e seu valor de metrica.
     * @param nomeGrafico O nome da metrica a ser exibida no grafico.
     * @param results A lista de valores da metrica para cada classe.
     */
    private void preencherGrafico(String nomeGrafico, List results){

        grafico.setAnimated(!primeiroGrafico);

        grafico.getData().clear(); // limpa dados antigos

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName(nomeGrafico);

        List<ClassInfo> classes =  analyser.getResults();
        for(int i=0;i<results.size();i++) {

            serie.getData().add(new XYChart.Data<>(classes.get(i).getName(), (Number)results.get(i)));
        }

        grafico.getData().add(serie);
        if (primeiroGrafico)
            primeiroGrafico=false;
    }

    /**
     * Mostra um alerta de erro com o titulo e mensagem fornecidos.
     * @param titulo O titulo do alerta.
     * @param msg A mensagem do alerta.
     */
    private void mostrarAlerta(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(titulo);
        alert.setContentText(msg);
        alert.setResizable(true);
        alert.getDialogPane().setMinWidth(500);
        alert.showAndWait();
    }
}
