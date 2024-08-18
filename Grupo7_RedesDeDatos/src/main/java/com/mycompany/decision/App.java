package com.mycompany.decision;

import com.fxgraph.cells.HBoxCell;
import com.fxgraph.edges.Edge;
import com.fxgraph.graph.Graph;
import com.fxgraph.graph.ICell;
import com.fxgraph.graph.Model;
import com.fxgraph.layout.ForceDirectedLayout;
import dispositivos.Cable;
import dispositivos.Host;
import dispositivos.Router;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class App extends Application {
        com.mycompany.decision.Graph<Escenario> grafo = null;
        Escenario escenario = null;
        Host PC1;
        Host PC2;
        Router router;
        Cable cable1;
        Cable cable2;
        Thread threadCable;
        
        ProgressBar progressBarTransferencia;
        Label labelProgresoTransferencia;
        long totalBytes = 0;

	@Override
	public void start(Stage stage) throws Exception {
                iniciarDispositivos();
                

		Graph graph = new Graph();
		// Add content to graph
		populateGraph(graph);

		// Layout nodes
		//AbegoTreeLayout layout = new AbegoTreeLayout(200, 200, Location.Top);
		//graph.layout(layout);
                //graph.layout(new RandomLayout());
                graph.layout(new ForceDirectedLayout());
		// Configure interaction buttons and behavior
		graph.getViewportGestures().setPanButton(MouseButton.SECONDARY);
		graph.getNodeGestures().setDragButton(MouseButton.PRIMARY);
                BorderPane bP = new BorderPane(graph.getCanvas());
                bP.setMinSize(800, 600);
                bP.setPrefSize(800,600);
                Scene scene = new Scene(bP);
               
		// Display the graph
		stage.setScene(scene);
		stage.show();
	}
        
        public static void main(String[] args) {
            launch();
        }
        
        public void iniciarDispositivos(){
            PC1 = new Host("ABCDEF");
            PC2 = new Host("12AF53");
            router = new Router("A6548E");
            cable1 = new Cable();
            cable2 = new Cable();
            
            router.conectCable(cable1);
            router.conectCable(cable2);

            PC1.connectToCable(cable1);
            PC2.connectToCable(cable2);
            if(this.threadCable != null){
                this.threadCable.stop();
            }
            threadCable = new Thread(router);
            this.threadCable.start();
        }

	private void populateGraph(Graph graph) {
            final Model model = graph.getModel();
            graph.beginUpdate();
            ICell pc1 = new HBoxCell(dibujarEscenario(new Escenario(PC1)));
            model.addCell(pc1);
            ICell pc2 = new HBoxCell(dibujarEscenario(new Escenario(PC2)));
            model.addCell(pc2);
            ICell r1 = new HBoxCell(dibujarEscenario(new Escenario(router)));
            model.addCell(r1);
            
            Edge edge1 = new Edge(pc1, r1, false, Color.color(0, 1, 0));
            Edge edge2 = new Edge(pc2, r1, false, Color.color(0, 1, 0));
            model.addEdge(edge1);
            model.addEdge(edge2);
            
            graph.endUpdate();
	}
        


    public HBox dibujarEscenario(Escenario escenario) {
        
        HBox hBoxEscenario = new HBox();
        hBoxEscenario.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getClickCount() == 2) {
                mostrarSimuladorComputadora(escenario);
            }
        });
        VBox datosE = new VBox();
        Label ip = new Label("IP: " + escenario.getDispositivo().getIp());
        Label MAC = new Label("MAC: " + escenario.getDispositivo().getMAC());
        VBox vBoxDispositivo = new VBox(new ImageView(new Image(escenario.getTipo() + ".png", 50, 50, true, true)));
        vBoxDispositivo.setMinWidth(50);
        datosE.getChildren().addAll(ip, MAC);
        hBoxEscenario.getChildren().addAll(datosE, vBoxDispositivo);

        String estilo = tipoEscenario(0);
        hBoxEscenario.setStyle(estilo);
        
        
        return hBoxEscenario;
    }

    private void mostrarSimuladorComputadora(Escenario escenario) {
        escenario.getDispositivo().openApplication("mail");
        escenario.getDispositivo().openApplication("radio");
        
        TextArea textAreaRecepcion;
        Stage stage = new Stage();
        stage.setTitle("Simulador de Computadora");

        VBox vBoxSimulador = new VBox(15);
        vBoxSimulador.setPadding(new Insets(20));
        vBoxSimulador.setAlignment(Pos.CENTER);
        vBoxSimulador.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        Button titulo = new Button("Simulador de Computadora");
        titulo.setFont(Font.font("Arial", 20));
        titulo.setStyle("-fx-background-color: transparent; -fx-text-fill: #333333; -fx-font-weight: bold;");
        titulo.setDisable(true);

        Button btnEnviarArchivo = new Button("Enviar Archivo de Texto", new ImageView(new Image("file_icon.png", 24, 24, true, true)));
        btnEnviarArchivo.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnEnviarArchivo.setOnAction(e -> {
            enviarArchivoTexto(stage, escenario);});

        progressBarTransferencia = new ProgressBar(0);
        progressBarTransferencia.setMinWidth(250);

        labelProgresoTransferencia = new Label("Progreso de Transferencia: 0%");
        labelProgresoTransferencia.setFont(Font.font("Arial", 14));
        textAreaRecepcion = new TextArea();                           
        actualizarRecepcion(textAreaRecepcion, escenario);
        
        textAreaRecepcion.setMinHeight(100);
        textAreaRecepcion.setEditable(false);

        VBox vboxTransferencia = new VBox(10, btnEnviarArchivo, progressBarTransferencia, labelProgresoTransferencia, textAreaRecepcion);
        vboxTransferencia.setAlignment(Pos.CENTER);

        Button btnEmitirAudio = new Button("Emitir Audio", new ImageView(new Image("audio_icon.png", 24, 24, true, true)));
        btnEmitirAudio.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        btnEmitirAudio.setOnAction(e -> emitirAudio());

        VBox vBoxMain = new VBox(15, titulo, vboxTransferencia, btnEmitirAudio);
        vBoxMain.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vBoxMain, 400, 400);
        stage.setScene(scene);
        stage.show();
    }
    
    private void actualizarRecepcion(TextArea textAreaRecepcion, Escenario escenario) {
    new Thread(() -> {
        Thread t = escenario.getDispositivo().getApp(0).receiveDataFile(textAreaRecepcion);
    }).start();
    }

    private void enviarArchivoTexto(Stage stage, Escenario escenario) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Archivo de Texto");
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                totalBytes = file.length(); // Obtiene el tamaño total del archivo en bytes
                // Envía el archivo y actualiza el progreso de envío
                escenario.getDispositivo().getApp(0).sendFile(file.toString());

                // Supongamos que progresoEnvio se actualiza durante el envío
                actualizarProgresoEnvio(escenario);

                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Archivo Enviado");
                alert.setHeaderText(null);
                alert.setContentText("Archivo enviado: " + file.getName());
                alert.showAndWait();
            } catch (IOException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void actualizarProgresoEnvio(Escenario escenario) {
        // Este método se encarga de actualizar la barra de progreso usando la variable progresoEnvio
    // Actualiza la barra de progreso y la etiqueta
            Platform.runLater(() -> {
                double progreso = (double) escenario.getDispositivo().getApp(0).getDatosEnviados() / totalBytes;
                progressBarTransferencia.setProgress(progreso);
                labelProgresoTransferencia.setText(String.format("Progreso de Transferencia: %.2f%%", progreso * 100));
            });
            
            Platform.runLater(() -> {
                double progreso = (double)1;
                progressBarTransferencia.setProgress(progreso);
                labelProgresoTransferencia.setText(String.format("Progreso de Transferencia: %.2f%%", progreso * 100));
            });

        // Aquí puedes también actualizar la visualización del contenido recibido si lo deseas
        //Platform.runLater(() -> textAreaRecepcion.appendText("Datos recibidos...\n"));
    }

    private void emitirAudio() {
    }
    
    public String tipoEscenario(int tipo){
        String stilo = "-fx-border-width: 2 ; \n" +
"    -fx-border-style: segments(10, 15, 15, 15)  line-cap round ;\n";;
        switch(tipo){
            case 1:
                stilo += "-fx-border-color: yellow ;";
                break;
            case 2:
                stilo += "-fx-border-color: green ;";
                break;
            case 3:
                stilo += "-fx-border-color: red ;";
                break;    
            default:
                stilo += "-fx-border-color: blue ;";
                break;
        }
        return stilo;
    }

}