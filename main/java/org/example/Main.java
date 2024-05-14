package org.example;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {
    private static Stage guiStage;
    //Dimensiones de la pantalla
    private int x=(int) ((Screen.getPrimary().getBounds().getWidth())*1);
    private int y=(int) ((Screen.getPrimary().getBounds().getHeight())*1);
    //Definimos dos objetos que serán las escenas en el programa
    static private FlightMode flight;
    static  private SimMode sim;
    public static void main(String[] args) {
        launch(args);
    }

    //Definimos dos metodos globales para poder cambiar la escena desde cualquier parte del programa
    public static void setEscenaUno(){
        guiStage.setScene(sim);
//        guiStage.setMaximized(true);
        guiStage.setFullScreen(true);
    }
    public static void setEscenaDos(){
        guiStage.setScene(flight);
//        guiStage.setMaximized(true);
        guiStage.setFullScreen(true);
    }


    @Override
    public void start(Stage stage) {
        System.out.println(x);
        System.out.println(y);
        //Inicializamos las escenas y añadimos un layout con medidas especificas
        flight = new FlightMode(new StackPane(), x, y);
        sim = new SimMode(new StackPane(), x, y);
        //Inicializamos el escenario.
        guiStage = stage;
        stage.setTitle("GCS Cuahtemoc");
        //Asignamos las hojas css correspondientes a cada escena.
        flight.getStylesheets().add("/Style.css");
        sim.getStylesheets().add("/Style.css");
        flight.setFill(Color.TRANSPARENT);
        sim.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        //Asignamos la escena al escenario principal
        stage.setScene(flight);
        stage.setMaximized(true);
        stage.setFullScreen(true);
//        stage.getIcons().add(new Image("/Icon.png"));
        stage.show();
    }
}