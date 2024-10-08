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
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
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
		// Definición de dispositivos
		Host PC1;
		Host PC2;
		Router router;
		Cable cable1;
		Cable cable2;
		Stage stage;

		// Variables para llevar el conteo de bytes y paquetes
		long totalBytes = 0;
		Pane bP;
		ICell[] iDispositivos;
		Graph graph;

		// VBox principal
		private VBox vBoxMain;

		// Variables para llevar el conteo de paquetes perdidos y corrompidos
		private int totalPerdidos;
		private int totalCorrompidos;

		// Variables para llevar el conteo de paquetes enviados y recibidos
		private int totalEnviados;
		private int totalRecibidos;

		// Probabilidad de pérdida de paquetes
		private double pPerdida;

		@Override
		public void start(Stage stage) throws Exception {
			// Inicialización de variables
			pPerdida = 0.1;
			this.totalPerdidos = 0;
			this.totalCorrompidos = 0;
			this.totalEnviados = 0;
			this.totalRecibidos = 0;
			vBoxMain = new VBox(15);

			// Creación de etiquetas para mostrar datos
			Label labelDatos = new Label("Datos total enviados: " + this.totalEnviados +
										 " | Datos total recibidos: " + this.totalRecibidos/128 +
										 " | Datos total recibidos: " + this.totalRecibidos +
										 " | Perdidos: " + this.totalPerdidos +
										 " | Corrompidos: " + this.totalCorrompidos);
			Label labelDatosP = new Label("Datos total enviados: " + 0 + "% | Datos total recibidos: " + 0 + "% | Perdidos: " + 0 + "% | Corrompidos: " + 0 +"%");
			labelDatos.setMinHeight(25);

			// Configuración del escenario principal
			this.stage = stage;
			iniciarDispositivos();
			graph = new Graph();
			bP = new BorderPane();
			iDispositivos = populateGraph(graph);

			// Configuración del diseño del gráfico
			graph.layout(new ForceDirectedLayout());
			graph.getViewportGestures().setPanButton(MouseButton.SECONDARY);
			graph.getNodeGestures().setDragButton(MouseButton.PRIMARY);
			bP.getChildren().add(graph.getCanvas());
			vBoxMain.getChildren().add(bP);

			// Creación de HBox para contener botones y campos de texto
			HBox hBoxBottom = new HBox(15);

			// Botón para actualizar los datos
			Button refreshButton = new Button("Refresh");
			refreshButton.setOnAction(eh -> {
				labelDatos.setText("Datos total enviados: " + this.totalEnviados +
								   " | Datos total recibidos: " + this.totalRecibidos +
								   " | Perdidos: " + this.totalPerdidos +
								   " | Corrompidos: " + this.totalCorrompidos);
				labelDatosP.setText("Datos total enviados: " + (this.totalEnviados*100)/this.totalEnviados + "% | Datos total recibidos: " + ((float)(this.totalRecibidos*100))/((float)this.totalEnviados) + "% | Perdidos: " + (float)(this.totalPerdidos*100)/(float)((float)this.totalEnviados/(float)128) + "% | Corrompidos: " + (float)(this.totalCorrompidos*100)/((float)this.totalEnviados/(float)128) +"%");
			});

			// Botón para mostrar el gráfico
			Button mostrarGraficoButton = new Button("Ver grafico");
			mostrarGraficoButton.setOnAction(eh -> {
				mostrarGrafico((((float)this.totalRecibidos)/128), (((float)this.totalEnviados)/128));
			});

			// Campo de texto para cambiar la probabilidad de pérdida
			TextField probabilidadPerdida = new TextField();
			probabilidadPerdida.setTooltip(new Tooltip("Ingrese el porcentaje de perdida"));

			probabilidadPerdida.setOnAction(eh -> {
				if (!probabilidadPerdida.getText().isEmpty()) {
					pPerdida = Double.parseDouble(probabilidadPerdida.getText());
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Cambiado");
					alert.setHeaderText(null);
					alert.setContentText("Probabilidad de pérdida cambiada a: " + pPerdida * 100);
					alert.showAndWait();
					this.totalPerdidos = 0;
					this.totalCorrompidos = 0;
					this.totalEnviados = 0;
					this.totalRecibidos = 0;
					labelDatos.setText("Datos total enviados: " + this.totalEnviados +
									   " | Datos total recibidos: " + this.totalRecibidos +
									   " | Perdidos: " + this.totalPerdidos +
									   " | Corrompidos: " + this.totalCorrompidos);
					labelDatosP.setText("Datos total enviados: " + 0 + "% | Datos total recibidos: " + 0 + "% | Perdidos: " + 0 + "% | Corrompidos: " + 0 + "%");
				}
			});

			// Añadir elementos al HBox inferior
			hBoxBottom.getChildren().addAll(labelDatos, refreshButton, mostrarGraficoButton, probabilidadPerdida);
			vBoxMain.getChildren().addAll(hBoxBottom, labelDatosP);

			// Configuración del tamaño y el fondo del BorderPane
			bP.setMinSize(800, 600);
			bP.setPrefSize(800, 600);
			bP.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

			// Creación de la escena principal
			Scene scene = new Scene(vBoxMain);

			// Configuración del escenario principal
			stage.setScene(scene);
			stage.show();

			// Acción al cerrar el escenario
			stage.setOnCloseRequest(eh -> {
				router.shutDown();
			});
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
            ICell pc2 = new HBoxCell(dibujarEscenario(new Escenario(PC2)));
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
    
    private void mostrarGrafico(float datosRecibidos, float datosEnviados) {
        Stage stage = new Stage();
        stage.initModality(Modality.NONE);
        stage.initOwner(this.stage);
        stage.initStyle(StageStyle.UTILITY);

        stage.setTitle("Comparación de datos perdidos");

        // Ejes X y Y
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Comparaciones");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Datos perdidos");

        // Crear el gráfico de barras
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Comparación de datos perdidos");

        // Configurar las propiedades del gráfico
        barChart.setBarGap(-5); // Reducir el espacio entre barras

        // Crear los datos para el gráfico
        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.setName("Valor teorico");
        double p = Math.pow((1-pPerdida), 10);
        series1.getData().add(new XYChart.Data<>("Datos", datosEnviados*p));

        XYChart.Series<String, Number> series2 = new XYChart.Series<>();
        series2.setName("Valor practico");
        series2.getData().add(new XYChart.Data<>("Datos", datosRecibidos));

        // Añadir las series al gráfico
        barChart.getData().addAll(series1, series2);
        VBox vbox = new VBox();
        vbox.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        // Crear un AnchorPane para añadir el gráfico y las etiquetas
        AnchorPane root = new AnchorPane();
        Label labelDatos = new Label("Datos total enviados: " + this.totalEnviados +
                                     " | Datos total recibidos: " + this.totalRecibidos +
                                     " | Perdidos: " + this.totalPerdidos +
                                     " | Corrompidos: " + this.totalCorrompidos);
        labelDatos.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        AnchorPane.setLeftAnchor(labelDatos, 10.0);
        AnchorPane.setTopAnchor(labelDatos, 10.0);

        float enviados = this.totalEnviados > 0 ? this.totalEnviados : 1; // Evitar división por cero
        float recibidos = this.totalRecibidos; // Evitar división por cero
        float enviadosDiv128 = this.totalEnviados > 0 ? this.totalEnviados / 128f : 1; // Evitar división por cero


        Label labelDatosP = new Label("Datos total enviados: " + (this.totalEnviados > 0 ? 100 + "%" : "N/A") + " | " +
                                      "Datos total recibidos: " + ((enviados > 0 ? (recibidos * 100) / enviados : 0) + "%") + " | " +
                                      "Perdidos: " + ((enviadosDiv128 > 0 ? (this.totalPerdidos * 100) / enviadosDiv128 : 0) + "%") + " | " +
                                      "Corrompidos: " + ((enviadosDiv128 > 0 ? (this.totalCorrompidos * 100) / enviadosDiv128 : 0) + "%"));
        labelDatosP.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
        AnchorPane.setLeftAnchor(labelDatosP, 10.0);
        AnchorPane.setTopAnchor(labelDatosP, 40.0); // Ajustar según la posición deseada


        VBox infoBox = new VBox(labelDatos, labelDatosP);
        infoBox.setSpacing(5); // Espacio entre las etiquetas
        infoBox.setPadding(new Insets(10)); // Padding alrededor de las etiquetas

        // Añadir el gráfico y el contenedor de información a un VBox
        root.getChildren().addAll(barChart);
        VBox.setVgrow(root, Priority.ALWAYS);
        vbox.getChildren().addAll(infoBox, root);
        // Añadir etiquetas a las barras
        for (XYChart.Series<String, Number> series : barChart.getData()) {
            for (XYChart.Data<String, Number> data : series.getData()) {
                Label label = new Label(String.valueOf(data.getYValue()));
                label.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");

                Label label2 = new Label(String.valueOf(((data.getYValue().floatValue()*100)/(datosEnviados == 0 ? 1: datosEnviados))) + "%");
                label2.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");

                data.getNode().layoutXProperty().addListener((observable, oldValue, newValue) -> {
                    double barWidth = data.getNode().getBoundsInParent().getWidth();
                    double xPos = newValue.doubleValue();
                    double barX = xPos + barWidth / 2 - label.getWidth() / 2; // Centrar la etiqueta horizontalmente

                    AnchorPane.setLeftAnchor(label, barX);
                    AnchorPane.setLeftAnchor(label2, barX);
                });

                data.getNode().layoutYProperty().addListener((observable, oldValue, newValue) -> {
                    double yPos = newValue.doubleValue();
                    double chartHeight = barChart.getHeight();

                    double labelY = yPos; // Posición de la etiqueta
                    double label2Y = yPos-15; // Posición de la etiqueta % 

                    // Ajustar la posición si la etiqueta está por encima del gráfico
                    if (labelY < 0) {
                        labelY = 0;
                        label2Y = 0;
                    }


                    AnchorPane.setTopAnchor(label, labelY);
                    AnchorPane.setTopAnchor(label2, label2Y);
                });

                root.getChildren().addAll(label, label2);
            }
        }

        AnchorPane.setTopAnchor(barChart, 0.0);
        AnchorPane.setBottomAnchor(barChart, 0.0);
        AnchorPane.setLeftAnchor(barChart, 0.0);
        AnchorPane.setRightAnchor(barChart, 0.0);

        // Crear la escena y mostrarla
        Scene scene = new Scene(vbox, 800, 800);
        stage.setScene(scene);
        stage.show();
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
        vBoxSimulador.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
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
		// Definición de variables
		ProgressBar progressBarTransferencia;
		Label labelProgresoTransferencia;
		Button actualizarConexionButton = new Button("Volver a conectar");
		Button cerrarApp = new Button("Cerrar aplicacion");

		// Abrir la aplicación en el dispositivo
		escenario.getDispositivo().openApplication("mail", sinPerdida, this.pPerdida);
		// application.Application app2 = escenario.getDispositivo().openApplication("radio");

		// Variables adicionales
		boolean envio = false;
		TextArea textAreaRecepcion;
		Stage stage = new Stage();

		// Configuración del nuevo escenario
		stage.initModality(Modality.NONE);
		stage.initOwner(this.stage);
		stage.initStyle(StageStyle.UTILITY);

		// Configuración del título del escenario
		stage.setTitle("Simulador de Computadora de IP: " + escenario.getDispositivo().getIp());

		// Obtener el primer dígito de la MAC del dispositivo
		int in1 = Integer.parseInt(escenario.getDispositivo().getMAC().substring(0, 1));

		// Creación del VBox principal
		VBox vBoxSimulador = new VBox(15);
		vBoxSimulador.setPadding(new Insets(20));
		vBoxSimulador.setAlignment(Pos.CENTER);
		vBoxSimulador.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

		// Creación del botón de título
		Button titulo = new Button("Simulador de Computadora de IP: " + escenario.getDispositivo().getIp());
		titulo.setFont(Font.font("Arial", 15));
		titulo.setStyle("-fx-background-color: transparent; -fx-text-fill: #030303; -fx-font-weight: bold;");
		titulo.setDisable(true);

		// Creación de etiquetas para mostrar datos
		Label datosEnviados = new Label("Datos enviados: "+escenario.getDispositivo().getApp(0).getDatosRecibidos() + "\nPaquetes Recibidos: 0");
		datosEnviados.setPrefHeight(35);
		Label datosRecibidos = new Label("Datos recibidos: "+escenario.getDispositivo().getApp(0).getDatosRecibidos() + "\nPaquetes Recibidos: 0");
		datosRecibidos.setPrefHeight(35);
		Label datosPerdidos = new Label("Paquetes Perdidos: " + escenario.getDispositivo().getApp(0).getdPerdida());
		Label datosCorrompidos = new Label("Paquetes Corrompidos: " + escenario.getDispositivo().getApp(0).getdCorrupccion());

		// Creación de la barra de progreso
		progressBarTransferencia = new ProgressBar(0);
		progressBarTransferencia.setMinWidth(250);

		// Creación de la etiqueta de progreso de transferencia
		labelProgresoTransferencia = new Label("Progreso de Transferencia: 0%");
		labelProgresoTransferencia.setFont(Font.font("Arial", 14));

		// Creación del botón para enviar archivo de texto
		Button btnEnviarArchivo = new Button("Enviar Archivo de Texto", new ImageView(new Image("file_icon.png", 24, 24, true, true)));
		btnEnviarArchivo.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
		btnEnviarArchivo.setOnAction(e -> {
			enviarArchivoTexto(stage, escenario, datosEnviados, progressBarTransferencia, labelProgresoTransferencia);
		});

		// Creación del botón para enviar archivo de texto 100 veces
		Button btnEnivarArchivoN = new Button("Enviar Archivo de Texto 100 veces", new ImageView(new Image("file_icon.png", 24, 24, true, true)));
		btnEnivarArchivoN.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
		btnEnivarArchivoN.setOnAction(e -> {
			enviarArchivoTextoNVeces(stage, escenario, datosEnviados, progressBarTransferencia, labelProgresoTransferencia, 100);
		});

		// Creación del área de texto para la recepción
		textAreaRecepcion = new TextArea();

		// Configuración del área de texto para arrastrar y soltar archivos
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

		// Configuración del área de texto para recibir datos
		escenario.getDispositivo().getApp(0).receiveDataFile(textAreaRecepcion, datosRecibidos);

		// Inicio de un hilo para la transferencia de datos
		new Thread (()->{
			if(escenario.getDispositivo().hasApps()){
				while(escenario.getDispositivo().hasApps() && escenario.getDispositivo().getApp(0).getRecibidorThread().isAlive()){
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
			}
		}).start();

		// Configuración del botón para actualizar la conexión
		actualizarConexionButton.setOnAction(e -> {
			if(escenario.getDispositivo().hasApps()){
				if(!escenario.getDispositivo().getApp(0).getRecibidorThread().isAlive()){
					escenario.getDispositivo().getApp(0).receiveDataFile(textAreaRecepcion, datosRecibidos);
					new Thread (()->{
						while(escenario.getDispositivo().hasApps() && escenario.getDispositivo().getApp(0).getRecibidorThread().isAlive()){
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
						}).start();
					}
				}
			});

			// Configuración del área de texto para la recepción
			textAreaRecepcion.setMinHeight(100);
			textAreaRecepcion.setEditable(false);

			// Creación del VBox para la transferencia de archivos
			VBox vboxTransferencia = new VBox(10, btnEnviarArchivo, btnEnivarArchivoN, progressBarTransferencia, labelProgresoTransferencia, textAreaRecepcion);
			vboxTransferencia.setAlignment(Pos.CENTER);

			// Creación del botón para guardar el archivo
			Button saveText = new Button("Guardar Archivo", new ImageView(new Image("save_file.png", 24, 24, true, true)));
			saveText.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
			saveText.setOnAction(e -> guardarArchivo(stage, textAreaRecepcion));

			// Añadir elementos al VBox principal
			vBoxSimulador.getChildren().addAll(titulo, datosEnviados, datosRecibidos, datosPerdidos, datosCorrompidos, vboxTransferencia, saveText, actualizarConexionButton, cerrarApp);
			vBoxSimulador.setAlignment(Pos.CENTER);

			// Creación de la escena principal
			Scene scene = new Scene(vBoxSimulador, 500, 700);
			stage.setScene(scene);

			// Configuración de la posición del escenario al mostrarse
			stage.setOnShown(event -> {
				if(in1 == 1){
					stage.setX(this.stage.getX() + this.stage.getWidth());
				} else {
					stage.setX(this.stage.getX() - stage.getWidth());
				}
				stage.setY(this.stage.getY());
			});

			// Acción al cerrar el escenario
			stage.setOnCloseRequest(eh -> {
				if(escenario.getDispositivo().hasApps()){
					int perdida = escenario.getDispositivo().getApp(0).getdPerdida();
					int corrupcion = escenario.getDispositivo().getApp(0).getdCorrupccion();
					this.totalPerdidos += perdida;
					this.totalCorrompidos += corrupcion;
				}
				escenario.getDispositivo().shutDown();
			});

			// Configuración del botón para cerrar la aplicación
			cerrarApp.setOnAction(eh -> {
				int perdida = 0;
				int corrupcion = 0;
				if(escenario.getDispositivo().hasApps()){
					escenario.getDispositivo().getApp(0).close();
					try {
						Thread.sleep(10);
					} catch (InterruptedException ex) {
						Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
					}
					perdida = escenario.getDispositivo().getApp(0).getdPerdida();
					corrupcion = escenario.getDispositivo().getApp(0).getdCorrupccion();
					datosPerdidos.setText("Paquetes Perdidos: " + perdida);
					datosCorrompidos.setText("Paquetes Corrompidos: " + corrupcion);

					this.totalPerdidos += perdida;
					this.totalCorrompidos += corrupcion;
					this.totalEnviados += escenario.getDispositivo().getApp(0).getDatosEnviados();
					this.totalRecibidos += escenario.getDispositivo().getApp(0).getDatosRecibidos();

					escenario.getDispositivo().removeApp(escenario.getDispositivo().getApp(0));
					textAreaRecepcion.setDisable(true);
					btnEnviarArchivo.setDisable(true);
					btnEnivarArchivoN.setDisable(true);
					actualizarConexionButton.setDisable(true);
					cerrarApp.setText("Abrir app");
				} else {
					cerrarApp.setText("Cerrar aplicacion");
					escenario.getDispositivo().openApplication("mail", sinPerdida, this.pPerdida);
					escenario.getDispositivo().getApp(0).receiveDataFile(textAreaRecepcion, datosRecibidos);
					textAreaRecepcion.clear();
					textAreaRecepcion.setDisable(false);
					btnEnviarArchivo.setDisable(false);
					btnEnivarArchivoN.setDisable(false);
					actualizarConexionButton.setDisable(false);
				}
			});

			// Mostrar el escenario
			stage.show();

			return stage;
		}

    
    private void enviarArchivoTextoNVeces(Stage stage, Escenario escenario, Label datosEnviados, ProgressBar progressBarTransferencia, Label labelProgresoTransferencia, int n){
        File file = seleccionarArchivo(stage);
        totalBytes = file.length()*n;
        if(file != null){
            for(int i = 0; i<n; i++){
                enviarArchivo(file, escenario, datosEnviados, progressBarTransferencia, labelProgresoTransferencia, false);
                //actualizarProgresoEnvio(escenario, datosEnviados, progressBarTransferencia, labelProgresoTransferencia);
            }
        }
    }
    
    private void enviarArchivoTexto(Stage stage, Escenario escenario, Label datosEnviados, ProgressBar progressBarTransferencia, Label labelProgresoTransferencia){
        File file = seleccionarArchivo(stage);
        if(file != null){
            totalBytes = file.length();
            enviarArchivo(file, escenario, datosEnviados, progressBarTransferencia, labelProgresoTransferencia, true);
        }
    }
    
    private void enviarArchivo(File file, Escenario escenario, Label datosEnviados, ProgressBar progressBarTransferencia, Label labelProgresoTransferencia, boolean showAlert) {
        if (file != null) {
            try {
                if(escenario.getDispositivo().hasApps()){
                    escenario.getDispositivo().getApp(0).sendFile(file.toString());
                    if(progressBarTransferencia != null){
                        new Thread (()->{
                            while(escenario.getDispositivo().hasApps() && escenario.getDispositivo().getApp(0).getEnviadorThread().isAlive()){
                                try {
                                  Thread.sleep(600);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                actualizarProgresoEnvio(escenario, datosEnviados, progressBarTransferencia, labelProgresoTransferencia);
                            }

                        }).start();
                        }
                    if(showAlert){
                        mostrarAlertaDeEnvio(file);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void mostrarAlertaDeEnvio(File file){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Archivo Enviado");
        alert.setHeaderText(null);
        alert.setContentText("Archivo enviado: " + file.getName());
        alert.showAndWait();
    }
    
    private File seleccionarArchivo(Stage stage){
        File currentDirectory = new File(System.getProperty("user.dir"));
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(currentDirectory);
        fileChooser.setTitle("Seleccionar Archivo de Texto");
        File file = fileChooser.showOpenDialog(stage);
        return file;
    }

    private void actualizarProgresoEnvio(Escenario escenario, Label datosEnviados, ProgressBar progressBarTransferencia, Label labelProgresoTransferencia) {
            Platform.runLater(() -> {
                int dEnviados = escenario.getDispositivo().getApp(0).getDatosEnviados();
                double progreso = ((double)  (dEnviados / totalBytes)) > 100 ? (double)100:(double) (dEnviados/ totalBytes);
                progressBarTransferencia.setProgress(progreso);
                labelProgresoTransferencia.setText(String.format("Progreso de Transferencia: %.2f%%", progreso * 100));
                datosEnviados.setText("Datos enviados: "+dEnviados  + "\nPaquetes Enviados: " + (int)dEnviados/128);
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