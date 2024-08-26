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
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class App extends Application {
        com.mycompany.decision.Graph<Escenario> grafo = null;
        Escenario escenario = null;
        Host PC1;
        Host PC2;
        Router router;
        Cable cable1;
        Cable cable2;
        Thread threadCable;
        Stage stage;
        
        long totalBytes = 0;
        Pane bP;
        ICell[] iDispositivos;
        Graph graph;
	@Override
	public void start(Stage stage) throws Exception {
                this.stage = stage;
                iniciarDispositivos();
		graph = new Graph();
                bP = new BorderPane();
		iDispositivos = populateGraph(graph);
                
                graph.layout(new ForceDirectedLayout());
		graph.getViewportGestures().setPanButton(MouseButton.SECONDARY);
		graph.getNodeGestures().setDragButton(MouseButton.PRIMARY);
  
                bP.getChildren().add(graph.getCanvas());

                bP.setMinSize(800, 600);
                bP.setPrefSize(800,600);
                bP.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
                Scene scene = new Scene(bP);

		stage.setScene(scene);
		stage.show();
                stage.setOnCloseRequest(eh->{router.shutDown();});
	}
        
        public static void main(String[] args) {
            launch();
        }
        
        public void iniciarDispositivos(){
            PC1 = new Host("1BCDEF");
            PC2 = new Host("22AF53");
            router = new Router("A6548E");
            cable1 = new Cable();
            cable2 = new Cable();
            
            router.conectCable(cable1);
            router.conectCable(cable2);

            PC1.connectToCable(cable1);
            PC2.connectToCable(cable2);

            new Thread(router).start();
        }

	private ICell[] populateGraph(Graph graph) {
            ICell[] dispositivos = new ICell[3];
            final Model model = graph.getModel();
            graph.beginUpdate();
            ICell pc1 = new HBoxCell(dibujarEscenario(new Escenario(PC1)));
            dispositivos[0] = pc1;
            model.addCell(pc1);
            ICell pc2 = new HBoxCell(dibujarEscenario(new Escenario(PC2), true));
            dispositivos[1] = pc2;
            model.addCell(pc2);
            ICell r1 = new HBoxCell(dibujarEscenario(new Escenario(router)));
            dispositivos[2] = r1;
            model.addCell(r1);
            
            Edge edge1 = new Edge(pc1, r1, false, Color.color(0, 1, 0));
            Edge edge2 = new Edge(pc2, r1, false, Color.color(0, 1, 0));
            model.addEdge(edge1);
            model.addEdge(edge2);
            graph.endUpdate();
            return dispositivos;
	}
        
     public HBox dibujarEscenario(Escenario escenario) {
            return dibujarEscenario(escenario, false);
    }

    public HBox dibujarEscenario(Escenario escenario, boolean sinPerdida) {
        
        HBox hBoxEscenario = new HBox();
        hBoxEscenario.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getClickCount() == 2) {
                if(escenario.getDispositivo() instanceof Host){
                    mostrarSimuladorComputadora(escenario, sinPerdida);
                }else{
                    mostrarSimuladorRouter(escenario);
                }
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
    
    private void mostrarSimuladorRouter(Escenario escenario) {
        Stage stage = new Stage();
        stage.initModality(Modality.NONE);
        stage.initOwner(this.stage);
        stage.initStyle(StageStyle.UTILITY);
        
        stage.setTitle("Simulador de Router");
        VBox vBoxSimulador = new VBox(15);
        vBoxSimulador.setPadding(new Insets(20));
        vBoxSimulador.setAlignment(Pos.CENTER);
        vBoxSimulador.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        Button titulo = new Button("Simulador de Router");
        titulo.setFont(Font.font("Arial", 20));
        titulo.setStyle("-fx-background-color: transparent; -fx-text-fill: #333333; -fx-font-weight: bold;");
        titulo.setDisable(true);
        
        ListView<String> listView = new ListView<>();
        listView.getItems().addAll(router.getNetworkLayer().getIpTable());
        
        TableView<NetworkEntry> tableView = new TableView<>();
        tableView.setTableMenuButtonVisible(false);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); 

        TableColumn<NetworkEntry, String> ipColumn = new TableColumn<>("IP Address");
        ipColumn.setCellValueFactory(new PropertyValueFactory<>("ip"));

        TableColumn<NetworkEntry, String> macColumn = new TableColumn<>("MAC Address");
        macColumn.setCellValueFactory(new PropertyValueFactory<>("mac"));
        
        TableColumn<NetworkEntry, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        tableView.getColumns().clear();
        tableView.getColumns().addAll(nameColumn, ipColumn, macColumn);

        // Obtener la lista de IPs y MACs y llenarla en la tabla
        ObservableList<NetworkEntry> data = FXCollections.observableArrayList();
        data.add(new NetworkEntry(router.getIp(), router.getMAC(), "Router"));
        for (int i = 0; i < router.getCables().size(); i++) {
            String ip = router.getNetworkLayer().getIpTable().get(i+1);
            String mac = router.getCables().get(i).getMAC();
            data.add(new NetworkEntry(ip, mac, "PC"));
        }
        tableView.setItems(data);
        
        vBoxSimulador.getChildren().addAll(titulo,tableView);
        Scene scene = new Scene(vBoxSimulador, 400, 400);
        stage.setScene(scene);
        stage.show();
    }
    
    private Stage mostrarSimuladorComputadora(Escenario escenario, boolean sinPerdida) {
        ProgressBar progressBarTransferencia;
        Label labelProgresoTransferencia;
        Button actualizarConexionButton = new Button("Volver a conectar");
        
        application.Application app1 = escenario.getDispositivo().openApplication("mail", sinPerdida);
        //application.Application app2 = escenario.getDispositivo().openApplication("radio");
        
        boolean envio = false;
        TextArea textAreaRecepcion;
        Stage stage = new Stage();
        stage.initModality(Modality.NONE);
        stage.initOwner(this.stage);
        stage.initStyle(StageStyle.UTILITY);
        
        stage.setTitle("Simulador de Computadora de IP: " + escenario.getDispositivo().getIp());
        int in1 = Integer.parseInt(escenario.getDispositivo().getMAC().substring(0, 1));
        VBox vBoxSimulador = new VBox(15);
        vBoxSimulador.setPadding(new Insets(20));
        vBoxSimulador.setAlignment(Pos.CENTER);
        vBoxSimulador.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        Button titulo = new Button("Simulador de Computadora de IP: " + escenario.getDispositivo().getIp());
        titulo.setFont(Font.font("Arial", 15));
        titulo.setStyle("-fx-background-color: transparent; -fx-text-fill: #333333; -fx-font-weight: bold;");
        titulo.setDisable(true);
        Label datosEnviados = new Label("Datos enviados: "+app1.getDatosRecibidos());
        Label datosRecibidos = new Label("Datos recibidos: "+app1.getDatosRecibidos());

        progressBarTransferencia = new ProgressBar(0);
        progressBarTransferencia.setMinWidth(250);

        labelProgresoTransferencia = new Label("Progreso de Transferencia: 0%");
        labelProgresoTransferencia.setFont(Font.font("Arial", 14));
        
        Button btnEnviarArchivo = new Button("Enviar Archivo de Texto", new ImageView(new Image("file_icon.png", 24, 24, true, true)));
        btnEnviarArchivo.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnEnviarArchivo.setOnAction(e -> {
            enviarArchivoTexto(stage, escenario, datosEnviados, progressBarTransferencia, labelProgresoTransferencia);
        });
        
        textAreaRecepcion = new TextArea();   
        
                textAreaRecepcion.setOnDragOver(event -> {
            if (event.getGestureSource() != textAreaRecepcion && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        textAreaRecepcion.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                File file = db.getFiles().get(0);
                try {
                    String content = new String(Files.readAllBytes(file.toPath()));
                    textAreaRecepcion.setText(content);
                    success = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
        
        app1.receiveDataFile(textAreaRecepcion, datosRecibidos);
        new Thread (()->{
              while(app1.getRecibidorThread().isAlive()){
                    try {
                        Thread.sleep(550);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    crearSobre(iDispositivos[in1-1], iDispositivos[2]);
                    try {
                        Thread.sleep(550);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    crearSobre(iDispositivos[2], iDispositivos[in1-1]);
              }
        }).start();
        
        actualizarConexionButton.setOnAction(e -> {
            if(!app1.getRecibidorThread().isAlive()){
                app1.receiveDataFile(textAreaRecepcion, datosRecibidos);
                new Thread (()->{
                    while(app1.getRecibidorThread().isAlive()){
                          try {
                              Thread.sleep(550);
                          } catch (InterruptedException ex) {
                              Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                          }
                          crearSobre(iDispositivos[in1-1], iDispositivos[2]);
                          try {
                              Thread.sleep(550);
                          } catch (InterruptedException ex) {
                              Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                          }
                          crearSobre(iDispositivos[2], iDispositivos[in1-1]);
                    }
              }).start();
            }
            });
        textAreaRecepcion.setMinHeight(100);
        textAreaRecepcion.setEditable(false);
        
        VBox vboxTransferencia = new VBox(10, btnEnviarArchivo, progressBarTransferencia, labelProgresoTransferencia, textAreaRecepcion);
        vboxTransferencia.setAlignment(Pos.CENTER);

        Button saveText = new Button("Guardar Archivo", new ImageView(new Image("save_file.png", 24, 24, true, true)));
        saveText.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        saveText.setOnAction(e -> guardarArchivo(stage, textAreaRecepcion));

        vBoxSimulador.getChildren().addAll(titulo, datosEnviados, datosRecibidos, vboxTransferencia, saveText, actualizarConexionButton);
        vBoxSimulador.setAlignment(Pos.CENTER);
        
        stage.setOnCloseRequest(eh->{
            escenario.getDispositivo().shutDown();
        });
        Scene scene = new Scene(vBoxSimulador, 500, 550);
        stage.setScene(scene);
        stage.setOnShown(event -> {
            if(in1 == 1){
                stage.setX(this.stage.getX() + this.stage.getWidth());
            } else {
                stage.setX(this.stage.getX() - stage.getWidth());
            }
            stage.setY(this.stage.getY());
        });
        stage.setOnCloseRequest(eh->{
            escenario.getDispositivo().shutDown();
        });
        
        stage.show();
        
        
        return stage;
    }
    

    private void enviarArchivoTexto(Stage stage, Escenario escenario, Label datosEnviados, ProgressBar progressBarTransferencia, Label labelProgresoTransferencia) {
        // Establece el directorio inicial en el FileChooser
        File currentDirectory = new File(System.getProperty("user.dir"));
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(currentDirectory);
        fileChooser.setTitle("Seleccionar Archivo de Texto");
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                totalBytes = file.length();
                escenario.getDispositivo().getApp(0).sendFile(file.toString());
                new Thread (()->{
                    while(escenario.getDispositivo().getApp(0).getEnviadorThread().isAlive()){
                        actualizarProgresoEnvio(escenario, datosEnviados, progressBarTransferencia, labelProgresoTransferencia);
                    }
                }).start();

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

    private void actualizarProgresoEnvio(Escenario escenario, Label datosEnviados, ProgressBar progressBarTransferencia, Label labelProgresoTransferencia) {
            Platform.runLater(() -> {
                int dEnviados = escenario.getDispositivo().getApp(0).getDatosEnviados();
                double progreso = ((double)  (dEnviados / totalBytes)) > 100 ? (double)100:(double) (dEnviados/ totalBytes);
                progressBarTransferencia.setProgress(progreso);
                labelProgresoTransferencia.setText(String.format("Progreso de Transferencia: %.2f%%", progreso * 100));
                datosEnviados.setText("Datos enviados: "+escenario.getDispositivo().getApp(0).getDatosEnviados()  + "\nPaquetes Enviados: " + (int)(escenario.getDispositivo().getApp(0).getDatosEnviados()/128));
            });
    }

    private void guardarArchivo(Stage stage, TextArea textArea){
        File currentDirectory = new File(System.getProperty("user.dir"));
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(currentDirectory);
        fileChooser.setTitle("Guardar archivo de texto");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos de texto", "*.txt"));

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            saveTextToFile(textArea.getText(), file);
        }
    }
    
    private void saveTextToFile(String content, File file) {
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(content);
        } catch (IOException e) {
            System.out.println("Error al guardar el archivo: " + e.getMessage());
        }
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
    
    public void crearSobre(ICell pc1, ICell pc2){
        Platform.runLater(() -> {
        ImageView sobrecito = new ImageView(new Image("sobre.png", 50, 50, true, true));
        sobrecito.setFitWidth(50);
        sobrecito.setFitHeight(50);

        double startX = graph.getGraphic(pc1).getLayoutX();
        double startY = graph.getGraphic(pc1).getLayoutY();
        double endX = graph.getGraphic(pc2).getLayoutX();
        double endY = graph.getGraphic(pc2).getLayoutY();
        
        Path path = new Path();
        //MoveTo moveToStart = new MoveTo(startX, startY);
        MoveTo moveToStart = new MoveTo(startX, startY);
        LineTo lineTo = new LineTo(endX, endY);
        path.getElements().addAll(moveToStart, lineTo);

        // Crea la transición para mover el sobrecito
        PathTransition pathTransition = new PathTransition();
        pathTransition.setRate(1.0); // Velocidad de movimiento
        pathTransition.setCycleCount(1);
        pathTransition.setPath(path);
        pathTransition.setNode(sobrecito);
        pathTransition.setInterpolator(Interpolator.EASE_BOTH);

        // Animación de desaparición (fade out) al llegar al destino
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), sobrecito);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);

        // Ejecutar las animaciones
        SequentialTransition sequentialTransition = new SequentialTransition(pathTransition, fadeTransition);
        bP.getChildren().add(sobrecito);
        sequentialTransition.play();
        });
    }

}