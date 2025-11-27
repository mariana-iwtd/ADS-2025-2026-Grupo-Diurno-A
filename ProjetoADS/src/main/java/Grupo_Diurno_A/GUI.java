package Grupo_Diurno_A;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * A classe {@code GUI} e' responsavel por iniciar a aplicacao grafica, chamando o
 * ficheiro FXML com a definicao da interface.
 *
 * <p>Foi utilizado JavaFX para criar a interface grafica.</p>
 *
 */
public class GUI extends Application {

    /**
     * Inicia a aplicacao grafica carregando o ficheiro FXML.
     * @param primaryStage O palco principal da aplicacao.
     * @throws Exception Se ocorrer um erro ao carregar o FXML.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/GUI.fxml")
        );
        StackPane root = loader.load();

        Scene scene = new Scene(root, 400, 400);
        primaryStage.setTitle("CodeMetrics Analyzer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Metodo principal para lancar a aplicacao JavaFX.
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}