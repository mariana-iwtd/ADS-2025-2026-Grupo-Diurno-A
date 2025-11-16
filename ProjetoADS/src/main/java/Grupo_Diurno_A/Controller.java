package Grupo_Diurno_A;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.eclipse.core.internal.resources.Project;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;


public class Controller {

    //Butoes
    @FXML
    private Button botaoEscolhaProjeto;

    @FXML
    private Button botaoAnalisar;

    @FXML
    private Button botaoVoltar;

    @FXML
    private Button botaoTabela;

    @FXML
    private Button botaoLOC;

    @FXML
    private Button botaoNOM;

    @FXML
    private Button botaoNOA;

    @FXML
    private Button botaoDIT;

    @FXML
    private Button botaoNOC;

    @FXML
    private Button botaoCBO;

    @FXML
    private Button botaoCC;

    @FXML
    private Button botaoNPRM;

    @FXML
    private Button botaoAvgLOCMethod;


    //Labels
    @FXML
    private Label pastaEscolhida;   //tera o nome da pasta que foi escolhida pelo user

    @FXML
    private Label labelPE; //label que diz "Pasta Escolhida:"


    //Logotipo
    @FXML
    private ImageView logotipo;


    //Grids
    @FXML
    private GridPane gridPaneInicial;

    @FXML
    private GridPane gridPaneTabela;

    //Tabela
    @FXML
    private TableView tabela;

    @FXML
    private TableColumn<ClassInfo, String> colNome;

    @FXML
    private TableColumn<ClassInfo, Integer> colLOC;

    @FXML
    private TableColumn<ClassInfo, Integer> colNOM;

    @FXML
    private TableColumn<ClassInfo, Integer> colNOA;

    @FXML
    private TableColumn<ClassInfo, Integer> colDIT;

    @FXML
    private TableColumn<ClassInfo, Integer> colNOC;

    @FXML
    private TableColumn<ClassInfo, Integer> colCBO;

    @FXML
    private TableColumn<ClassInfo, Integer> colCC; // CiclomaticComplexity

    @FXML
    private TableColumn<ClassInfo, Double> colNPRM;

    @FXML
    private TableColumn<ClassInfo, Double> colAvgLOC;


    //Grafico
    @FXML
    private BarChart<String, Number> grafico;


    private File selectedDir;
    private ProjectAnalyser analyser;

    private boolean primeiroGrafico = true;


    // corre quando a interface aparece
    public void initialize() {
        logotipo.setImage(new Image("/Logotipo.png"));
        labelPE.setOpacity(0);
        pastaEscolhida.setOpacity(0);

        //janela da tabela
        gridPaneTabela.setOpacity(0);
        gridPaneTabela.setDisable(true);
        botaoVoltar.setDisable(true);
        botaoVoltar.setOpacity(0);
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

    //configura as colunas para irem buscar valores automaticamente
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
    }

    @FXML
    private void onAnalisar(){

        if(selectedDir==null){
            mostrarAlerta("Erro", "Nao selecionou nenhum ficheiro para analisar!");
            return;
        }

        //cria project analyser com o caminho do diretorio
        analyser = new ProjectAnalyser(selectedDir.toPath());

        //desativa e faz com que botoes desaparecam
        gridPaneInicial.setOpacity(0);
        gridPaneInicial.setDisable(true);

        botaoAnalisar.setOpacity(0);
        botaoAnalisar.setDisable(true);

        botaoEscolhaProjeto.setOpacity(0);
        botaoEscolhaProjeto.setDisable(true);

        labelPE.setOpacity(0);
        pastaEscolhida.setOpacity(0);

        logotipo.setOpacity(0);
        logotipo.setDisable(true);

        //novo ecra
        gridPaneTabela.setOpacity(1);
        gridPaneTabela.setDisable(false);

        preencheTabela();
        tabela.setOpacity(1);
        tabela.setDisable(false);

        botaoVoltar.setDisable(false);
        botaoVoltar.setOpacity(1);

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
    }


    public void preencheTabela(){
        //limpa dados antigos da tabela
        tabela.getItems().clear();

        List<ClassInfo> classes = analyser.getResults();

        //percorre a lista de classes
        for(ClassInfo classe: classes){
            tabela.getItems().add(classe);
        }

    }

    @FXML
    public void onVoltar(){
        //desaparece a tabela, grid e botao voltar
        limparSelecaoBotoes();
        tabela.setOpacity(0);
        tabela.setDisable(true);
        grafico.setOpacity(0);
        grafico.setDisable(true);
        gridPaneTabela.setOpacity(0);
        gridPaneTabela.setDisable(true);
        botaoVoltar.setOpacity(0);
        botaoVoltar.setDisable(true);


        //voltam a aparecer os botoes e grid
        gridPaneInicial.setOpacity(1);
        gridPaneInicial.setDisable(false);

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
        stage.setHeight(400);
    }

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

    public void onBotaoTabela(){
        changeSelectedButtonColor(botaoTabela);
        //ativa a tabela e desativa o grafico se ainda nao estiverem
        if(!grafico.isDisable()) {
            grafico.setOpacity(0);
            grafico.setDisable(true);
            tabela.setOpacity(1);
            tabela.setDisable(false);
        }
    }

    public void onLOC(){
        changeSelectedButtonColor(botaoLOC);

        List<Integer>locs=new ArrayList();

        for(ClassInfo classe:analyser.getResults())
            locs.add(classe.getLoc());

        activateGraph();
        preencherGrafico("Linhas de Codigo por Classe", locs);

    }

    public void onNOM(){
        changeSelectedButtonColor(botaoNOM);

        List<Integer>noms=new ArrayList();

        for(ClassInfo classe:analyser.getResults())
            noms.add(classe.getNom());

        activateGraph();
        preencherGrafico("Numero de Metodos por Classe", noms);

    }

    public void onNOA(){
        changeSelectedButtonColor(botaoNOA);
        List<Integer>noas=new ArrayList();

        for(ClassInfo classe:analyser.getResults())
            noas.add(classe.getNoa());

        activateGraph();
        preencherGrafico("Numero de Atributos por Classe", noas);
    }

    public void onDIT(){
        changeSelectedButtonColor(botaoDIT);

        List<Integer>dits=new ArrayList();

        for(ClassInfo classe:analyser.getResults())
            dits.add(classe.getNom());

        activateGraph();
        preencherGrafico("Profundidade da Arvore de Heranca por Classe", dits);
    }

    public void onNOC(){
        changeSelectedButtonColor(botaoNOC);
        List<Integer>nocs=new ArrayList();

        for(ClassInfo classe:analyser.getResults())
            nocs.add(classe.getNoc());

        activateGraph();
        preencherGrafico("Numero de Filhos de cada Classe", nocs);
    }

    public void onCBO(){
        changeSelectedButtonColor(botaoCBO);

        List<Integer>cbos=new ArrayList();

        for(ClassInfo classe:analyser.getResults())
            cbos.add(classe.getNom());

        activateGraph();
        preencherGrafico("Acoplamento entre Objetos por Classe", cbos);
    }

    public void onCC(){
        changeSelectedButtonColor(botaoCC);

        List<Integer>ccs=new ArrayList();

        for(ClassInfo classe:analyser.getResults())
            ccs.add(classe.getNom());

        activateGraph();
        preencherGrafico("Complexidade Ciclomatica por Classe", ccs);
    }

    public void onNPRM(){
        changeSelectedButtonColor(botaoNPRM);
        List<Integer>nprms=new ArrayList();

        for(ClassInfo classe:analyser.getResults())
            nprms.add(classe.getNom());

        activateGraph();
        preencherGrafico("Numero Medio de Parametros dos Metodos de Cada Classe", nprms);
    }

    public void onAvgLOC(){
        changeSelectedButtonColor(botaoAvgLOCMethod);

        List<Integer>avgLocs=new ArrayList();

        for(ClassInfo classe:analyser.getResults())
            avgLocs.add(classe.getNom());

        activateGraph();
        preencherGrafico("Media do Numero de Linhas por Classe", avgLocs);
    }

    private void changeSelectedButtonColor(Button botao){
        limparSelecaoBotoes();
        botao.getStyleClass().add("button-selecionado");
    }

    //ativa o grafico e desativa a tabela se ainda nao estiverem
    private void activateGraph(){
        if(grafico.isDisable()) {
            grafico.setOpacity(1);
            grafico.setDisable(false);
            tabela.setOpacity(0);
            tabela.setDisable(true);
        }
    }

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

    private void mostrarAlerta(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(titulo);
        alert.setContentText(msg);
        alert.showAndWait();
    }







}
