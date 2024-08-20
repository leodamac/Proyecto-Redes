/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fxgraph.cells;

import com.fxgraph.graph.Graph;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

/**
 *
 * @author Leo
 */
public class ImagenCell  extends AbstractCell {
    private ImageView imagen;
    public ImagenCell(String path) {
        this.imagen = new ImageView(new Image(path, 15, 15, true, true));
    }
    
    public ImagenCell(ImageView image) {
        this.imagen = image;
    }
    
    @Override
    public Region getGraphic(Graph graph) {
        final Pane pane = new Pane(imagen);
        imagen.fitWidthProperty().bind(pane.prefWidthProperty());
        imagen.fitHeightProperty().bind(pane.prefHeightProperty());
            
        CellGestures.makeResizable(graph, pane);
        
        return pane;
    }
    
}
