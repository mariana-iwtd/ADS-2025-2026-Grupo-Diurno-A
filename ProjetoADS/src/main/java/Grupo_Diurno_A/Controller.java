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


import java.io.File;
import java.util.List;
import java.nio.file.Path;


public class Controller {

    @FXML
    private Button botaoEscolhaProjeto;

    @FXML
    private Button botaoAnalisar;

    @FXML
    private Button botaoVoltar;

    @FXML
    private Label pastaEscolhida;   //tera o nome da pasta que foi escolhida pelo user

    @FXML
    private Label labelPE; //label que diz "Pasta Escolhida:"

    @FXML
    private ImageView logotipo;

    @FXML
    private GridPane gridPaneInicial;

    @FXML
    private GridPane gridPaneTabela;

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




    private File selectedDir;
    private ProjectAnalyser analyser;

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
        tabela.setOpacity(0);
        tabela.setDisable(true);
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

    private void mostrarAlerta(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(titulo);
        alert.setContentText(msg);
        alert.showAndWait();
    }







}
