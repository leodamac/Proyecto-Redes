package com.fxgraph.cells;

import com.fxgraph.graph.Graph;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;



public class HBoxCell extends AbstractCell {
    private HBox hBox = new HBox();
    public HBoxCell(HBox hBox){
        this.hBox = hBox;
        this.hBox.setPadding(new Insets(5,5,5,5));
    }
    
    @Override
    public Region getGraphic(Graph graph) {
            final Pane pane = new Pane(hBox);
            hBox.prefWidthProperty().bind(pane.prefWidthProperty());
            hBox.prefHeightProperty().bind(pane.prefHeightProperty());
            
            CellGestures.makeResizable(graph, pane);
            return pane;
    }

}