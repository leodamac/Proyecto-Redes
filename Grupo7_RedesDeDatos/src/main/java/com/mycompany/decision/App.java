package com.mycompany.decision;

import com.fxgraph.cells.HBoxCell;
import com.fxgraph.edges.Edge;
import com.fxgraph.graph.Graph;
import com.fxgraph.graph.ICell;
import com.fxgraph.graph.Model;
import com.fxgraph.layout.ForceDirectedLayout;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class App extends Application {
        com.mycompany.decision.Graph<Escenario> grafo = null;
        Escenario escenario = null;

	@Override
	public void start(Stage stage) throws Exception {
		Graph graph = new Graph();
                inicializarGrafo();
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

	private void populateGraph(Graph graph) {
            
            final Model model = graph.getModel();
            graph.beginUpdate();
            ICell pc1 = new HBoxCell(dibujarEscenario(new Escenario("pc")));
            model.addCell(pc1);
            ICell pc2 = new HBoxCell(dibujarEscenario(new Escenario("pc")));
            model.addCell(pc2);
            ICell router = new HBoxCell(dibujarEscenario(new Escenario("router")));
            model.addCell(router);
            
            Edge edge1 = new Edge(pc1, router, false, Color.color(0, 1, 0));
            Edge edge2 = new Edge(pc2, router, false, Color.color(0, 1, 0));
            model.addEdge(edge1);
            model.addEdge(edge2);

            graph.endUpdate();
	}
        
    
    private void inicializarGrafo(){
        this.grafo = new com.mycompany.decision.Graph();
    }
    
    
    public HBox dibujarEscenario(Escenario e){
        HBox hBoxEscenario = new HBox();
        hBoxEscenario.addEventHandler(MouseEvent.MOUSE_CLICKED, event->{
            if(event.getClickCount() == 2){
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Información");
                alert.setHeaderText("Este es un encabezado de alerta");
                alert.setContentText("Este es el contenido de la alerta. Puedes escribir cualquier mensaje aquí.");

                // Mostrar la alerta y esperar hasta que el usuario la cierre
                alert.showAndWait();
            }
        });
        VBox vBoxDispositivo = new VBox(new ImageView(new Image(e.getTipo()+".png", 50,50, true, true)));
        vBoxDispositivo .setMinWidth(50);
        hBoxEscenario.getChildren().add(vBoxDispositivo );

        String stilo = tipoEscenario(0);
        hBoxEscenario.setStyle(stilo);
        return hBoxEscenario;
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