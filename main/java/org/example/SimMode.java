package org.example;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.geometry.Multipoint;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ArcGISVectorTiledLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.fazecast.jSerialComm.SerialPort;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;

public class SimMode extends Scene {
    static CSVfile csv;
    long t0;
    long t;
    ComboBox sp_combo = new ComboBox();
    SerialPort PuertoSerial;
    static clockGS cronus;
    public static  Text TM = new Text("T-00:13:20");
    static boolean startThread = true;
    static boolean runThread = false;
    static int hr = 0, min = 0;
    static int seg = 0;
    //    boolean startThread = true;
//    boolean runThread = false;
    String line = "";
    ObservableList<String> listPort = FXCollections.observableArrayList();
    //Textos
    Text Voltage,Temperature,Acceleration,Altitude,Pressure,Air_speed,PackageC;
    Text GPS_Altitude,GPS_Latitude, GPS_Longitude,GPS_Time,GPS_Sats;
    Text Tilt_X,Tilt_Y,Rot_Z,PoE;
    Cylinder cilindro;
    //Variables
    double dataBattery,dataTemp,dataAcce,dataAlt,dataPress,dataAir,dataPoE,dataMS,dataLat,dataLon;
    //Barras
    Rectangle R1,R2,R3;
    //Gauges
    Arc arc2,arc4,arc6;
    //Misssion Status
    Circle ms_circle,ms_circle1,ms_circle2,ms_circle3,ms_circle4,ms_circle5;
    Line ms_line,ms_line1,ms_line2,ms_line3,ms_line4;
    double dataWRect;
    PointCollection stateCapitalsPST;
    Multipoint multipoint;
    Graphic multipointGraphic;
    GraphicsOverlay graphicsOverlay;
    Point point1;
    Graphic pointGraphic;
    static XYChart.Series GRF1 = new XYChart.Series();
    // ACELEROMETRO
    static XYChart.Series GRF2 = new XYChart.Series();
    // ALTITUD
    static XYChart.Series GRF3 = new XYChart.Series();
    static XYChart.Series GRF4 = new XYChart.Series();
    ImageView pointing = new ImageView(new Image("/images/pointing1.png"));
    Button porT = new Button("CONNECT");
    Button send = new Button("SEND");
    static ObservableList<String> listComm =
            FXCollections.observableArrayList
                    (
                            "CMD,2012,CX,ON",
                            "CMD,2012,CX,OFF",
                            "CMD,2012,FLY,ACTIVATE",
                            "CMD,2012,ST,GPS",
                            "CMD,2012,STHHmmss",
                            "CMD,2012,SIM,ENABLE",
                            "CMD,2012,SIM,ACTIVATE",
                            "CMD,2012,SIMP",
                            "CMD,2012,CAL",
                            "CMD,2012,BCN,ON",
                            "CMD,2012,BCN,OFF",
                            "CMD,2012,CALIBRATION",
                            "CMD,2012,WIPE,EEPROM",
                            "CMD,2012,EXIT"
                    );
    static ComboBox command_combo = new ComboBox(listComm);
    public SimMode(StackPane root1, int x, int y) {
        super(root1,x,y);
        dataWRect=410;
        Image imageBackground = new Image("/images/GS_CC2024.png");
        Region background = new Region();
        background.setBackground(new Background(new BackgroundImage(
                imageBackground,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(1, 1, true, true, false, false)
        )));

        // GRAFICAS
        // VIBRACIÓN
        HBox Container_Grf1;
        // ACELERACIÓN
        HBox Container_Grf2;
        // PRESIÓN
        HBox Container_Grf3;
        // ALTITUD
        HBox Container_Grf4;


        Font.loadFont(getClass().getResourceAsStream("/fonts/HKGrotesk-SemiBold.ttf"), 15);
        Font.loadFont(getClass().getResourceAsStream("/fonts/HKGrotesk-Medium.ttf"), 15);
        GridPane root = new GridPane();
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(33);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(34);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(33);
        root.getColumnConstraints().addAll(col1, col2, col3);
        RowConstraints row = new RowConstraints();
        row.setPercentHeight(100);
        root.getRowConstraints().add(row);
        root.setGridLinesVisible(false);

        //Grid Izquierdo
        GridPane LeftGrid = new GridPane();
        LeftGrid.getStyleClass().add("mygridStyle");
        ColumnConstraints colum = new ColumnConstraints();
        colum.setPercentWidth(93);
        LeftGrid.getColumnConstraints().add(colum);

        RowConstraints graf1 = new RowConstraints();
        graf1.setPercentHeight(19);
        LeftGrid.getRowConstraints().add(graf1);
        RowConstraints graf2 = new RowConstraints();
        graf2.setPercentHeight(19);
        LeftGrid.getRowConstraints().add(graf2);
        RowConstraints graf3 = new RowConstraints();
        graf3.setPercentHeight(19);
        LeftGrid.getRowConstraints().add(graf3);
        RowConstraints graf4 = new RowConstraints();
        graf4.setPercentHeight(19);
        LeftGrid.getRowConstraints().add(graf4);
        RowConstraints row6 = new RowConstraints();
        row6.setPercentHeight(22);
        LeftGrid.getRowConstraints().add(row6);
        LeftGrid.setGridLinesVisible(false);
        LeftGrid.setAlignment(Pos.BOTTOM_RIGHT);
        root.getStyleClass().add("mygridStyle");
        root.add(LeftGrid, 0, 0);

        GRF1 = new XYChart.Series();
        Container_Grf1 = new HBox();
        final NumberAxis x1 = new NumberAxis();
        final NumberAxis y1 = new NumberAxis();
        final LineChart LineChart1 = new LineChart(x1, y1);

        LineChart1.getData().addAll(GRF1);
        LineChart1.setCreateSymbols(false);
        LineChart1.setLegendVisible(false);
        x1.setAutoRanging(true);
        x1.setForceZeroInRange(false);
        y1.setAutoRanging(true);
        y1.setForceZeroInRange(false);
        LineChart1.setScaleY(0.95);
        LineChart1.setScaleX(0.9);
        LineChart1.getYAxis().setTickLabelsVisible(true);
        LineChart1.getXAxis().setTickLabelsVisible(true);
        x1.setTickLabelFill(Color.BLACK);
        y1.setTickLabelFill(Color.BLACK);
        LineChart1.getYAxis().setOpacity(1);
        LineChart1.getXAxis().setOpacity(1);
        Container_Grf1.getChildren().add(LineChart1);
        LeftGrid.add(Container_Grf1, 0, 0);
        Container_Grf1.setPadding(new Insets(10, 0, 0, 0));

//        GRF1.getData().add(new XYChart.Data(789, 30.08));
//        GRF1.getData().add(new XYChart.Data(790, 30.08));
//        GRF1.getData().add(new XYChart.Data(791, 30.08));
//        GRF1.getData().add(new XYChart.Data(792, 30.08));
//        GRF1.getData().add(new XYChart.Data(793, 30.08));
//        GRF1.getData().add(new XYChart.Data(794, 30.08));
//        GRF1.getData().add(new XYChart.Data(795, 30.08));
//        GRF1.getData().add(new XYChart.Data(796, 30.09));
//        GRF1.getData().add(new XYChart.Data(797, 30.09));
//        GRF1.getData().add(new XYChart.Data(798, 30.09));
//        GRF1.getData().add(new XYChart.Data(799, 30.08));
//        GRF1.getData().add(new XYChart.Data(800, 30.08));

        GRF2 = new XYChart.Series();
        Container_Grf2 = new HBox();
        final NumberAxis x2 = new NumberAxis();
        final NumberAxis y2 = new NumberAxis();
        final LineChart LineChart2 = new LineChart(x2, y2);

        LineChart2.getData().addAll(GRF2);
        LineChart2.setCreateSymbols(false);
        LineChart2.setLegendVisible(false);
        x2.setAutoRanging(true);
        x2.setForceZeroInRange(false);
        y2.setAutoRanging(true);
        y2.setForceZeroInRange(false);
        LineChart2.setScaleY(0.95);
        LineChart2.setScaleX(0.9);
        LineChart2.getYAxis().setTickLabelsVisible(true);
        LineChart2.getXAxis().setTickLabelsVisible(true);
        x2.setTickLabelFill(Color.BLACK);
        y2.setTickLabelFill(Color.BLACK);
        LineChart2.getYAxis().setOpacity(1);
        LineChart2.getXAxis().setOpacity(1);
        Container_Grf2.getChildren().add(LineChart2);
        LeftGrid.add(Container_Grf2, 0, 1);
        Container_Grf2.setPadding(new Insets(10, 0, 0, 0));

//        GRF2.getData().add(new XYChart.Data(789, 23));
//        GRF2.getData().add(new XYChart.Data(790, 14));
//        GRF2.getData().add(new XYChart.Data(791, 15));
//        GRF2.getData().add(new XYChart.Data(792, 24));
//        GRF2.getData().add(new XYChart.Data(793, 34));
//        GRF2.getData().add(new XYChart.Data(794, 36));
//        GRF2.getData().add(new XYChart.Data(795, 22));
//        GRF2.getData().add(new XYChart.Data(796, 45));
//        GRF2.getData().add(new XYChart.Data(797, 43));
//        GRF2.getData().add(new XYChart.Data(798, 17));
//        GRF2.getData().add(new XYChart.Data(799, 29));
//        GRF2.getData().add(new XYChart.Data(800, 25));

        GRF3 = new XYChart.Series();
        Container_Grf3 = new HBox();
        final NumberAxis x3 = new NumberAxis();
        final NumberAxis y3 = new NumberAxis();
        final LineChart LineChart3 = new LineChart(x3, y3);

        LineChart3.getData().addAll(GRF3);
        LineChart3.setCreateSymbols(false);
        LineChart3.setLegendVisible(false);
        x3.setAutoRanging(true);
        x3.setForceZeroInRange(false);
        y3.setAutoRanging(true);
        y3.setForceZeroInRange(false);
        LineChart3.setScaleY(0.95);
        LineChart3.setScaleX(0.9);
        LineChart3.getYAxis().setTickLabelsVisible(true);
        LineChart3.getXAxis().setTickLabelsVisible(true);
        x3.setTickLabelFill(Color.BLACK);
        y3.setTickLabelFill(Color.BLACK);
        LineChart3.getYAxis().setOpacity(1);
        LineChart3.getXAxis().setOpacity(1);
        Container_Grf3.getChildren().add(LineChart3);
        LeftGrid.add(Container_Grf3, 0, 2);
        Container_Grf3.setPadding(new Insets(10, 0, 0, 0));

//        GRF3.getData().add(new XYChart.Data(789, 95.28));
//        GRF3.getData().add(new XYChart.Data(790, 95.28));
//        GRF3.getData().add(new XYChart.Data(791, 95.28));
//        GRF3.getData().add(new XYChart.Data(792, 95.28));
//        GRF3.getData().add(new XYChart.Data(793, 95.28));
//        GRF3.getData().add(new XYChart.Data(794, 95.28));
//        GRF3.getData().add(new XYChart.Data(795, 95.28));
//        GRF3.getData().add(new XYChart.Data(796, 95.28));
//        GRF3.getData().add(new XYChart.Data(797, 95.28));
//        GRF3.getData().add(new XYChart.Data(798, 95.28));
//        GRF3.getData().add(new XYChart.Data(799, 95.28));
//        GRF3.getData().add(new XYChart.Data(800, 95.17));

        GRF4 = new XYChart.Series();
        Container_Grf4 = new HBox();
        final NumberAxis x4 = new NumberAxis();
        final NumberAxis y4 = new NumberAxis();
        final LineChart LineChart4 = new LineChart(x4, y4);

        LineChart4.getData().addAll(GRF4);
        LineChart4.setCreateSymbols(false);
        LineChart4.setLegendVisible(false);
        x4.setAutoRanging(true);
        x4.setForceZeroInRange(false);
        y4.setAutoRanging(true);
        y4.setForceZeroInRange(false);
        LineChart4.setScaleY(0.95);
        LineChart4.setScaleX(0.9);
        LineChart4.getYAxis().setTickLabelsVisible(true);
        LineChart4.getXAxis().setTickLabelsVisible(true);
        x4.setTickLabelFill(Color.BLACK);
        y4.setTickLabelFill(Color.BLACK);
        LineChart4.getYAxis().setOpacity(1);
        LineChart4.getXAxis().setOpacity(1);
        Container_Grf4.getChildren().add(LineChart4);
        LeftGrid.add(Container_Grf4, 0, 3);
        Container_Grf4.setPadding(new Insets(10, 0, 0, 0));

//        GRF4.getData().add(new XYChart.Data(789, 0.08));
//        GRF4.getData().add(new XYChart.Data(790, 0.10));
//        GRF4.getData().add(new XYChart.Data(791, 0.15));
//        GRF4.getData().add(new XYChart.Data(792, 0.18));
//        GRF4.getData().add(new XYChart.Data(793, 0.24));
//        GRF4.getData().add(new XYChart.Data(794, 0.27));
//        GRF4.getData().add(new XYChart.Data(795, 0.28));
//        GRF4.getData().add(new XYChart.Data(796, 0.3));
//        GRF4.getData().add(new XYChart.Data(797, 0.35));
//        GRF4.getData().add(new XYChart.Data(798, 0.4));
//        GRF4.getData().add(new XYChart.Data(799, 9.56));
//        GRF4.getData().add(new XYChart.Data(800, 109.25));

        GridPane ButtonsGrid = new GridPane();
        ButtonsGrid.getStyleClass().add("mygridStyle");
        for (int i = 0; i < 2; i++) {
            ColumnConstraints colum1 = new ColumnConstraints();
            colum1.setPercentWidth(40);
            ButtonsGrid.getColumnConstraints().add(colum1);
        }
        for (int i = 0; i < 2; i++) {
            RowConstraints row2 = new RowConstraints();
            row2.setPercentHeight(25);
            ButtonsGrid.getRowConstraints().add(row2);
        }
        ButtonsGrid.setAlignment(Pos.CENTER_LEFT);
        ButtonsGrid.setGridLinesVisible(false);
        ButtonsGrid.setPadding(new Insets(10, 0, 0, 0));
        LeftGrid.add(ButtonsGrid, 0, 4);


//        SerialPort/Commands
        porT.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Conexion establecida");
                if ((porT.getText() == "CONNECT")) {
                    t0 = (System.currentTimeMillis());
                    porT.setText("DISCONNECT");
                    try {
                        csv = new CSVfile();
                        Lectura();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        System.out.println("El error esta en el boton" + e);
                    }

                } else {
                    System.out.println("FINISH THREAD SERIALPORT");

                    csv.close();
                    runThread = false;
                    startThread = false;
                    PuertoSerial.closePort();

                    GRF1.getData().clear();
                    GRF2.getData().clear();
                    GRF3.getData().clear();
                    GRF4.getData().clear();

                    porT.setText("CONNECT");

                }
            }
        });
        // VBox SP_container = new VBox();
        porT.setPrefWidth(150);
        sp_combo.setPrefWidth(150);
//                SP_container.setSpacing(7.5);
//                SP_container.setAlignment(Pos.CENTER_LEFT);
        // SP_container.getChildren().addAll(sp_combo,porT);
        //   HBox buttons_container = new HBox(10.0,command_combo,send);
        command_combo.setPrefWidth(150);
        send.setPrefWidth(150);
        send.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String cmd = (String) command_combo.getValue();
                switch (cmd) {
                    case "CMD,2012,CX,ON":
                        System.out.println(cmd + "-> SEND");
                        byte[] CXON = "CXON\n".getBytes();
                        PuertoSerial.writeBytes(CXON, CXON.length);
                        break;

                    case "CMD,2012,CX,OFF":
                        System.out.println(cmd + "-> SEND");
                        byte[] CXOFF = "CXOFF\n".getBytes();
                        PuertoSerial.writeBytes(CXOFF, CXOFF.length);
                        break;

                    case "CMD,2012,FLY,ACTIVATE":
                        System.out.println(cmd + "-> SEND");
                        byte[] F = "F\n".getBytes();
                        PuertoSerial.writeBytes(F, F.length);
                        break;

                    case "CMD,2012,FLY,ENABLE":
                        System.out.println(cmd + "-> SEND");
                        byte[] S = "S".getBytes();
                        PuertoSerial.writeBytes(S, S.length);
                        break;

                    case "CMD,2012,ST,GPS":
                        System.out.println(cmd + "-> SEND");
                        byte[] STG = "STG\n".getBytes();
                        PuertoSerial.writeBytes(STG, STG.length);
                        break;

                    case "CMD,2012,STHHmmss":
                        System.out.println(cmd + "-> SEND");
                        SimpleDateFormat dateFormat = new SimpleDateFormat("HHmmss");
                        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                        String timeStamp = dateFormat.format(new Date());
                        String UTC = "ST"+timeStamp+"\n";
                        System.out.println(UTC);
                        byte[] ST = UTC.getBytes();
                        PuertoSerial.writeBytes(ST, ST.length);
                        break;

                    case "CMD,2012,SIM,ENABLE":
                        System.out.println(cmd + "-> SEND");
                        byte[] SE = "SE\n".getBytes();
                        PuertoSerial.writeBytes(SE, SE.length);
                        break;

                    case "CMD,2012,SIM,ACTIVATE":
                        System.out.println(cmd + "-> SEND");
                        byte[] SA = "SA\n".getBytes();
                        PuertoSerial.writeBytes(SA, SA.length);
                        break;

                    case "CMD,2012,SIM,DISABLE":
                        System.out.println(cmd + "-> SEND");
                        byte[] SD = "SD\n".getBytes();
                        PuertoSerial.writeBytes(SD, SD.length);
                        break;

                    case "CMD,2012,SIMP":
                        System.out.println(cmd + "-> SEND");
                        byte[] SI = "SI\n".getBytes();
                        PuertoSerial.writeBytes(SI, SI.length);
                        break;

                    case "CMD,2012,CAL":
                        System.out.println(cmd + "-> SEND");
                        byte[] CAL = "CAL\n".getBytes();
                        PuertoSerial.writeBytes(CAL, CAL.length);
                        break;

                    case "CMD,2012,BCN,ON":
                        System.out.println(cmd + "-> SEND");
                        byte[] Z = "Z".getBytes();
                        PuertoSerial.writeBytes(Z, Z.length);
                        break;

                    case "CMD,2012,BCN,OFF":
                        System.out.println(cmd + "-> SEND");
                        byte[] z = "z".getBytes();
                        PuertoSerial.writeBytes(z, z.length);
                        break;

                    case "CMD,2012,WIPE,EEPROM":
                        System.out.println(cmd + "-> SEND");
                        byte[] W = "W\n".getBytes();
                        PuertoSerial.writeBytes(W, W.length);
                        break;

                    case "CMD,2012,EXIT":
                        Platform.exit();
                        System.exit(0);
                        break;

                    default:
                        break;
                }
            }
        });
        ButtonsGrid.add(sp_combo, 0, 0);
        GridPane.setHalignment(sp_combo, HPos.CENTER);
        ButtonsGrid.add(command_combo, 1, 0);
        ButtonsGrid.setHalignment(command_combo, HPos.CENTER);
        ButtonsGrid.add(porT, 0, 1);
        ButtonsGrid.setHalignment(porT, HPos.CENTER);
        ButtonsGrid.add(send, 1, 1);
        ButtonsGrid.setHalignment(send, HPos.CENTER);
        //  VBox commands_container = new VBox(20.0,buttons_container);
        // VBox container_b = new VBox(10.0,commands_container);
        // container_b.setAlignment(Pos.CENTER);
        // HBox bottom_container = new HBox(20.0,SP_container,container_b);
        //  bottom_container.setPadding(new Insets(35, 10, 0, 0));
//                bottom_container.setPrefWidth(x/3);
//                bottom_container.setAlignment(Pos.CENTER);
//                LeftGrid.add(bottom_container, 0, 4);


        //CenterGrid
        GridPane CenterGrid = new GridPane();
        CenterGrid.getStyleClass().add("mygridStyle");
        ColumnConstraints colum12 = new ColumnConstraints();
        colum12.setPercentWidth(100);
        CenterGrid.getColumnConstraints().add(colum12);

        RowConstraints gps_container = new RowConstraints();
        gps_container.setPercentHeight(37);
        CenterGrid.getRowConstraints().add(gps_container);
        RowConstraints tilt_container = new RowConstraints();
        tilt_container.setPercentHeight(32.5);
        CenterGrid.getRowConstraints().add(tilt_container);
        RowConstraints tm_container = new RowConstraints();
        tm_container.setPercentHeight(22);
        CenterGrid.getRowConstraints().add(tm_container);

        CenterGrid.setGridLinesVisible(false);
        CenterGrid.setAlignment(Pos.BOTTOM_CENTER);
        root.getStyleClass().add("mygridStyle");
        root.add(CenterGrid, 1, 0);

        GridPane GPSGrid = new GridPane();
        GPSGrid.getStyleClass().add("mygridStyle");
        ColumnConstraints colum13 = new ColumnConstraints();
        colum13.setPercentWidth(85);
        GPSGrid.getColumnConstraints().add(colum13);
        for (int i = 0; i < 2; i++) {
            RowConstraints row15 = new RowConstraints();
            row15.setPercentHeight(8);
            GPSGrid.getRowConstraints().add(row15);
        }
        RowConstraints row16 = new RowConstraints();
        row16.setPercentHeight(68);
        GPSGrid.getRowConstraints().add(row16);
        for (int i = 0; i < 2; i++) {
            RowConstraints row17 = new RowConstraints();
            row17.setPercentHeight(8);
            GPSGrid.getRowConstraints().add(row17);
        }
        GPSGrid.setAlignment(Pos.CENTER);
        GPSGrid.setGridLinesVisible(false);
        CenterGrid.add(GPSGrid, 0, 0);

        GridPane GPSData = new GridPane();
        GPSData.getStyleClass().add("mygridStyle");
        for (int i = 0; i < 5; i++) {
            ColumnConstraints colum17 = new ColumnConstraints();
            colum17.setPercentWidth(100 / 5);
            GPSData.getColumnConstraints().add(colum17);
        }
        RowConstraints row20 = new RowConstraints();
        row20.setPercentHeight(100);
        GPSData.getRowConstraints().add(row20);
        GPSData.setAlignment(Pos.CENTER);
        GPSData.setGridLinesVisible(false);
        GPSGrid.add(GPSData, 0, 1);

        GridPane GPSData1 = new GridPane();
        GPSData1.getStyleClass().add("mygridStyle");
        for (int i = 0; i < 4; i++) {
            ColumnConstraints colum18 = new ColumnConstraints();
            colum18.setPercentWidth(25);
            GPSData1.getColumnConstraints().add(colum18);
        }
        RowConstraints row21 = new RowConstraints();
        row21.setPercentHeight(100);
        GPSData1.getRowConstraints().add(row21);
        GPSData1.setAlignment(Pos.CENTER);
        GPSData1.setGridLinesVisible(false);
        GPSGrid.add(GPSData1, 0, 3);

        String yourApiKey = "AAPKe5e83248457c4ebb92422e2c11574cc19crEy44M6RMc2yfo5txDy2KJ6QgyhgbpM8M-be6wtwAOJ-baqDDVMqoFE_6fqWNd";
        ArcGISRuntimeEnvironment.setApiKey(yourApiKey);
        String licenseKey = "runtimelite,1000,rud9970126455,none,6PB3LNBHPDADL50JT179";
        ArcGISRuntimeEnvironment.setLicense(licenseKey);


        Portal portal = new Portal("https://www.arcgis.com", false);
        // Conexión ID con argis para extraer la tesela vectorial
        String itemId = "1cda05c8deb2429fbfdbf20c9294e4df";
        PortalItem portalItem = new PortalItem(portal,itemId);
        // Asignación del diseño como una capa
        ArcGISVectorTiledLayer vectorTiledLayer = new ArcGISVectorTiledLayer(portalItem);
        // Creación del mapa
        ArcGISMap map = new ArcGISMap();
        //Agregar tesela vectorial al mapa
        map.getOperationalLayers().add(vectorTiledLayer);
        MapView mapView = new MapView();
        mapView.setMap(map);
//        // create a JavaFX scene with a stack pane as the root node, and add it to the scene
//        // create a map view to display the map and add it to the stack pane
//        MapView mapView = new MapView();
//        ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_NAVIGATION);
//        // set the map on the map view
//        mapView.setMap(map);

        // create a graphics overlay and add it to the map view
        graphicsOverlay = new GraphicsOverlay();
        mapView.getGraphicsOverlays().add(graphicsOverlay);

        mapView.setViewpoint(new Viewpoint(19.5093036, -99.1339391, 90000));

        // create a point geometry with a location and spatial reference
        point1 = new Point(dataLon, dataLat, SpatialReferences.getWgs84());
//        // create an opaque orange point symbol with a opaque blue outline symbol
        SimpleMarkerSymbol simpleMarkerSymbol =
                new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.ORANGE, 10);
        SimpleLineSymbol blueOutlineSymbol =
                new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 2);

        simpleMarkerSymbol.setOutline(blueOutlineSymbol);
        // create a graphic with the point geometry and symbol
        Graphic pointGraphic = new Graphic(point1, simpleMarkerSymbol);

        // add the point graphic to the graphics overlay
        graphicsOverlay.getGraphics().add(pointGraphic);

////        Point Collection
//        stateCapitalsPST = new PointCollection(SpatialReferences.getWgs84());
//        stateCapitalsPST.add(-99.1339, 19.5093); // Sacramento, CA
//        stateCapitalsPST.add(-99.1341, 19.5095); // Olympia, WA
//        stateCapitalsPST.add(-99.1343, 19.5097); // Salem, OR
//        stateCapitalsPST.add(-99.1344, 19.5099); // Carson City, NV
//        multipoint = new Multipoint(stateCapitalsPST);
//        multipointGraphic = new Graphic(multipoint, new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE,
//                Color.RED, 12));
//
//        graphicsOverlay.getGraphics().addAll(Arrays.asList(multipointGraphic));


        GPSGrid.add(mapView, 0, 2);
        GPS_Altitude = new Text("289.8m");
//        Altitude.setFont(Font.font("HKGrotesk", FontWeight.SEMI_BOLD,18));
        GPS_Altitude.setId("dataGPS");
        GPS_Altitude.setFill(Color.BLACK);
        GPSData.add(GPS_Altitude, 0, 0);
        GPS_Latitude = new Text("19.5091°");
//        Latitude.setFont(Font.font("HKGrotesk", FontWeight.SEMI_BOLD,18));
        GPS_Latitude.setId("dataGPS");
        GPS_Latitude.setFill(Color.BLACK);
        GPSData.add(GPS_Latitude, 2, 0);
        GPS_Longitude = new Text("-99.1326°");
//        Longitude.setFont(Font.font("HKGrotesk", FontWeight.SEMI_BOLD,18));
        GPS_Longitude.setId("dataGPS");
        GPS_Longitude.setFill(Color.BLACK);
        GPSData.add(GPS_Longitude, 4, 0);

        GPS_Time = new Text("00:13:20");
//        GPS_TM.setFont(Font.font("HKGrotesk", FontWeight.MEDIUM,18));
        GPS_Time.setId("data");
        GPS_Time.setFill(Color.BLACK);
        GridPane.setHalignment(GPS_Time, HPos.CENTER);
        GPSData1.add(GPS_Time, 1, 0);
        GPS_Sats = new Text("7");
//        Sats.setFont(Font.font("HKGrotesk", FontWeight.SEMI_BOLD,18));
        GPS_Sats.setId("data");
        GPS_Sats.setFill(Color.BLACK);
        GPSData1.add(GPS_Sats, 3, 0);
        GridPane.setHalignment(GPS_Sats, HPos.RIGHT);

        GridPane TiltGrid = new GridPane();
        TiltGrid.getStyleClass().add("mygridStyle");
        ColumnConstraints colum14 = new ColumnConstraints();
        colum14.setPercentWidth(60);
        TiltGrid.getColumnConstraints().add(colum14);
        ColumnConstraints colum15 = new ColumnConstraints();
        colum15.setPercentWidth(40);
        TiltGrid.getColumnConstraints().add(colum15);
        RowConstraints row18 = new RowConstraints();
        row18.setPercentHeight(90);
        TiltGrid.getRowConstraints().add(row18);
        TiltGrid.setAlignment(Pos.CENTER);
        TiltGrid.setGridLinesVisible(false);
        CenterGrid.add(TiltGrid, 0, 1);

        GridPane TiltData = new GridPane();
        TiltData.getStyleClass().add("mygridStyle");
        ColumnConstraints colum18 = new ColumnConstraints();
        colum18.setPercentWidth(23);
        TiltData.getColumnConstraints().add(colum18);
        ColumnConstraints colum19 = new ColumnConstraints();
        colum19.setPercentWidth(20);
        TiltData.getColumnConstraints().add(colum19);
        for (int i = 0; i < 3; i++) {
            RowConstraints row22 = new RowConstraints();
            row22.setPercentHeight(85 / 3);
            TiltData.getRowConstraints().add(row22);
        }
        TiltData.setAlignment(Pos.BOTTOM_LEFT);
        TiltData.setGridLinesVisible(false);
        TiltGrid.add(TiltData, 0, 0);

        //Cilindro
        Rotate xRotate = new Rotate(0, Rotate.X_AXIS);
        Rotate yRotate = new Rotate(0, Rotate.Y_AXIS);
        Rotate zRotate = new Rotate(0, Rotate.Z_AXIS);

//        int min = 1;
//        int max = 10;
//
//        for (int i = 1; i <= 180; i++) {
//            int getRandomValue = ThreadLocalRandom.current().nextInt(min, max) + min;
//            xRotate.setAngle(xRotate.getAngle() + getRandomValue); // Rotación en el eje X
//            yRotate.setAngle(yRotate.getAngle() + getRandomValue); // Rotación en el eje Y
//            zRotate.setAngle(zRotate.getAngle() + getRandomValue); // Rotación en el eje Z
//        }

        Rot_Z = new Text("0.0000°");
//		RotZ.setFont(Font.font("Brocades Sans"));
        Rot_Z.setId("tilt");
        Rot_Z.setFill(Color.BLACK);
        TiltData.add(Rot_Z, 1, 0);
        GridPane.setHalignment(Rot_Z, HPos.CENTER);
        Tilt_X = new Text("0.0000°");
//				TiltX.setFont(Font.font("Brocades Sans"));
        Tilt_X.setId("tilt");
        Tilt_X.setFill(Color.BLACK);
        TiltData.add(Tilt_X, 1, 1);
        GridPane.setHalignment(Tilt_X, HPos.CENTER);
        Tilt_Y = new Text("0.0000°");
//				TiltY.setFont(Font.font("Brocades Sans"));
        Tilt_Y.setId("tilt");
        Tilt_Y.setFill(Color.BLACK);
        TiltData.add(Tilt_Y, 1, 2);
        GridPane.setHalignment(Tilt_Y, HPos.CENTER);
//        Text TiltZ = new Text("0.0000°");
//				TiltZ.setFont(Font.font("Brocades Sans"));
//        TiltZ.setId("tilt");
//        TiltZ.setFill(Color.BLACK);
//        TiltData.add(TiltZ, 1, 3);
//        GridPane.setHalignment(TiltZ, HPos.CENTER);

        GridPane TiltGraf = new GridPane();
        TiltGraf.getStyleClass().add("mygridStyle");
        ColumnConstraints colum22 = new ColumnConstraints();
        colum22.setPercentWidth(55);
        TiltGraf.getColumnConstraints().add(colum22);
        RowConstraints row23 = new RowConstraints();
        row23.setPercentHeight(85);
        TiltGraf.getRowConstraints().add(row23);
        TiltGraf.setAlignment(Pos.BOTTOM_RIGHT);
        TiltGraf.setGridLinesVisible(false);
        TiltGrid.add(TiltGraf, 0, 0);

        // Crear un cilindro
        DropShadow dpCilindro = new DropShadow();
        dpCilindro.setColor(Color.WHITE);
        dpCilindro.setRadius(10);

        cilindro = new Cylinder(20, 100);
        cilindro.setTranslateX(100);
        cilindro.setTranslateY(100);
        cilindro.setMaterial(new javafx.scene.paint.PhongMaterial(Color.LIGHTGRAY));
        // Crear rotaciones en los ejes X, Y y Z
        cilindro.getTransforms().addAll(xRotate, yRotate, zRotate);


        Group Modelo3D = new Group(cilindro);
        Modelo3D.setEffect(dpCilindro);
//        Modelo3D.setTranslateX(400);
//        Modelo3D.setTranslateY(300);

        TiltGraf.getChildren().add(Modelo3D);
        TiltGraf.setHalignment(Modelo3D, HPos.CENTER);

        //Pointing error
        GridPane PEGraf = new GridPane();
        PEGraf.getStyleClass().add("mygridStyle");
        ColumnConstraints colum25 = new ColumnConstraints();
        colum25.setPercentWidth(100);
        PEGraf.getColumnConstraints().add(colum25);
        RowConstraints row26 = new RowConstraints();
        row26.setPercentHeight(64);
        PEGraf.getRowConstraints().add(row26);
        RowConstraints row27 = new RowConstraints();
        row27.setPercentHeight(22.5);
        PEGraf.getRowConstraints().add(row27);
        PEGraf.setAlignment(Pos.BOTTOM_RIGHT);
        PEGraf.setGridLinesVisible(false);
        TiltGrid.add(PEGraf, 1, 0);

        PoE = new Text("15.5°");
//				PE.setFont(Font.font("Brocades Sans"));
        PoE.setId("data1");
        PoE.setFill(Color.BLACK);
        PEGraf.add(PoE, 0, 1);
        GridPane.setHalignment(PoE, HPos.CENTER);

        pointing.setX(0);
        pointing.setY(0);
        pointing.setFitWidth(140);
        pointing.setPreserveRatio(true);
        PEGraf.add(pointing, 0, 0);
        PEGraf.setHalignment(pointing, HPos.CENTER);
        PEGraf.setValignment(pointing, VPos.CENTER);

        RotateTransition rt = new RotateTransition(Duration.INDEFINITE, pointing);
        rt.setByAngle(0);
        rt.setFromAngle(15.5);
        rt.setCycleCount(Timeline.INDEFINITE);
        rt.setAutoReverse(true);
        rt.play();

        GridPane TMGrid = new GridPane();
        TMGrid.getStyleClass().add("mygridStyle");
        ColumnConstraints colum16 = new ColumnConstraints();
        colum16.setPercentWidth(80);
        TMGrid.getColumnConstraints().add(colum16);
        ColumnConstraints colum17 = new ColumnConstraints();
        colum17.setPercentWidth(15);
        TMGrid.getColumnConstraints().add(colum17);
        RowConstraints row19 = new RowConstraints();
        row19.setPercentHeight(85);
        TMGrid.getRowConstraints().add(row19);
        TMGrid.setAlignment(Pos.CENTER_RIGHT);
        TMGrid.setGridLinesVisible(false);
        CenterGrid.add(TMGrid, 0, 2);

        GridPane TMContainer = new GridPane();
        TMContainer.getStyleClass().add("mygridStyle");
        ColumnConstraints colum21 = new ColumnConstraints();
        colum21.setPercentWidth(100);
        TMContainer.getColumnConstraints().add(colum21);
        RowConstraints row22 = new RowConstraints();
        row22.setPercentHeight(40);
        TMContainer.getRowConstraints().add(row22);
        TMContainer.setAlignment(Pos.TOP_CENTER);
        TMContainer.setGridLinesVisible(false);
        TMGrid.add(TMContainer, 0, 0);
        TM.setId("TimeMision");
//		TM.setFont(Font.font("HKGrotesk", FontWeight.BOLD,40));
        TM.setFill(Color.BLACK);
        TMContainer.add(TM, 0, 0);
        GridPane.setHalignment(TM, HPos.CENTER);
        GridPane.setValignment(TM, VPos.BOTTOM);

        GridPane PCContainer = new GridPane();
        PCContainer.getStyleClass().add("mygridStyle");
        ColumnConstraints colum23 = new ColumnConstraints();
        colum23.setPercentWidth(100);
        PCContainer.getColumnConstraints().add(colum23);
        RowConstraints row24 = new RowConstraints();
        row24.setPercentHeight(29);
        PCContainer.getRowConstraints().add(row24);
        RowConstraints row25 = new RowConstraints();
        row25.setPercentHeight(16);
        PCContainer.getRowConstraints().add(row25);
        PCContainer.setAlignment(Pos.BOTTOM_CENTER);
        PCContainer.setGridLinesVisible(false);
        TMGrid.add(PCContainer, 1, 0);

        PackageC = new Text("1286");
        PackageC.setId("count");
//		PC.setFont(Font.font("Figtree", FontWeight.SEMI_BOLD,25));
        PackageC.setFill(Color.BLACK);
        PCContainer.add(PackageC, 0, 0);
        GridPane.setHalignment(PackageC, HPos.CENTER);

        //RightGrid
        GridPane RightGrid = new GridPane();
        RightGrid.getStyleClass().add("mygridStyle");
        ColumnConstraints colum1 = new ColumnConstraints();
        colum1.setPercentWidth(94);
        RightGrid.getColumnConstraints().add(colum1);

        RowConstraints ms_container = new RowConstraints();
        ms_container.setPercentHeight(30);
        RightGrid.getRowConstraints().add(ms_container);
        RowConstraints graf20 = new RowConstraints();
        graf20.setPercentHeight(15);
        RightGrid.getRowConstraints().add(graf20);
        RowConstraints gauges_container = new RowConstraints();
        gauges_container.setPercentHeight(12);
        RightGrid.getRowConstraints().add(gauges_container);
        RowConstraints bar_container = new RowConstraints();
        bar_container.setPercentHeight(19);
        RightGrid.getRowConstraints().add(bar_container);
        RowConstraints buttons_container = new RowConstraints();
        buttons_container.setPercentHeight(22);
        RightGrid.getRowConstraints().add(buttons_container);
        RightGrid.setGridLinesVisible(false);
        RightGrid.setAlignment(Pos.BOTTOM_LEFT);
        root.getStyleClass().add("mygridStyle");
        root.add(RightGrid, 2, 0);

        GridPane GaugesGrid = new GridPane();
        GaugesGrid.getStyleClass().add("mygridStyle");
        for (int i = 0; i < 3; i++) {
            ColumnConstraints colum2 = new ColumnConstraints();
            colum2.setPercentWidth(32.5);
            GaugesGrid.getColumnConstraints().add(colum2);
        }
        RowConstraints row3 = new RowConstraints();
        row3.setPercentHeight(100);
        GaugesGrid.getRowConstraints().add(row3);
        GaugesGrid.setAlignment(Pos.CENTER_RIGHT);
        GaugesGrid.setGridLinesVisible(false);
        RightGrid.add(GaugesGrid, 0, 2);

        RadialGradient gradient1 = new RadialGradient(0,
                .1,
                100,
                100,
                20,
                false,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.RED),
                new Stop(1, Color.BLACK));


//                Stop[] stops = new Stop[] { new Stop(0, Color.rgb(93,224,230)), new Stop(1, Color.rgb(0,77,173))};
//                LinearGradient lg2 = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);

        //Air speed gauge
        Arc arc1 = new Arc();
        arc2 = new Arc();
        Group Gp1 = new Group();
        HBox HBgp1 = new HBox();
        Air_speed = new Text("135.47\nm/s");
//		Air_speed.setFont(Font.font("Brocades Sans"));
        Air_speed.setFill(Color.BLACK);
        Air_speed.setId("gauges");
        Air_speed.setTextAlignment(TextAlignment.CENTER);
        arc1.setFill(Color.TRANSPARENT);
        arc1.setStroke(Color.rgb(244, 246, 252));
        arc1.setStrokeWidth(13);
        arc1.setCenterX(x / 2);
        arc1.setCenterY(y / 2);
        arc1.setRadiusX(55);
        arc1.setRadiusY(55);
        arc1.setStartAngle(0);
        arc1.setLength(180);
        arc2.setFill(Color.TRANSPARENT);
        arc2.setStroke(Color.rgb(24, 147, 252));
        arc2.setStrokeWidth(13);
        arc2.setCenterX(x / 2);
        arc2.setCenterY(y / 2);
        arc2.setRadiusX(55);
        arc2.setRadiusY(55);
        arc2.setStartAngle((-1) * 180);
        arc2.setLength((-1) * 120);
        Gp1.getChildren().addAll(arc1, arc2);
        HBgp1.getChildren().add(Gp1);
        HBgp1.setAlignment(Pos.BOTTOM_CENTER);
        GaugesGrid.add(HBgp1, 0, 0);
        StackPane BPT1 = new StackPane();
        BPT1.getChildren().add(Air_speed);
        BPT1.setAlignment(Pos.BOTTOM_CENTER);
        GaugesGrid.add(BPT1, 0, 0);
        //Altitude gauge
        Arc arc3 = new Arc();
        arc4 = new Arc();
        Group Gp2 = new Group();
        HBox HBgp2 = new HBox();
        Altitude = new Text("109.25\nm");
//		Altitude.setFont(Font.font("Brocades Sans"));
        Altitude.setFill(Color.BLACK);
        Altitude.setId("gauges");
        Altitude.setTextAlignment(TextAlignment.CENTER);
        arc3.setFill(Color.TRANSPARENT);
        arc3.setStroke(Color.rgb(244, 246, 252));
        arc3.setStrokeWidth(13);
        arc3.setCenterX(x / 2);
        arc3.setCenterY(y / 2);
        arc3.setRadiusX(55);
        arc3.setRadiusY(55);
        arc3.setStartAngle(0);
        arc3.setLength(180);
        arc4.setFill(Color.TRANSPARENT);
        arc4.setStroke(Color.rgb(24, 147, 252));
        arc4.setStrokeWidth(13);
        arc4.setCenterX(x / 2);
        arc4.setCenterY(y / 2);
        arc4.setRadiusX(55);
        arc4.setRadiusY(55);
        arc4.setStartAngle((-1) * 180);
        arc4.setLength((-1) * 19.6596);
        Gp2.getChildren().addAll(arc3, arc4);
        HBgp2.getChildren().add(Gp2);
        HBgp2.setAlignment(Pos.BOTTOM_CENTER);
        GaugesGrid.add(HBgp2, 1, 0);
        StackPane BPT2 = new StackPane();
        BPT2.getChildren().add(Altitude);
        BPT2.setAlignment(Pos.BOTTOM_CENTER);
        GaugesGrid.add(BPT2, 1, 0);
        //Pressure gauge
        Arc arc5 = new Arc();
        arc6 = new Arc();
        Group Gp3 = new Group();
        HBox HBgp3 = new HBox();
        Pressure = new Text("94.04\nkPa");
        Pressure.setFill(Color.BLACK);
        Pressure.setId("gauges");
        Pressure.setTextAlignment(TextAlignment.CENTER);
        arc5.setFill(Color.TRANSPARENT);
        arc5.setStroke(Color.rgb(244, 246, 252));
        arc5.setStrokeWidth(13);
        arc5.setCenterX(x / 2);
        arc5.setCenterY(y / 2);
        arc5.setRadiusX(55);
        arc5.setRadiusY(55);
        arc5.setStartAngle(0);
        arc5.setLength(180);
        arc6.setFill(Color.TRANSPARENT);
        arc6.setStroke(Color.rgb(24, 147, 252));
        arc6.setStrokeWidth(13);
        arc6.setCenterX(x / 2);
        arc6.setCenterY(y / 2);
        arc6.setRadiusX(55);
        arc6.setRadiusY(55);
        arc6.setStartAngle((-1) * 180);
        arc6.setLength((-1) * 45);
        Gp3.getChildren().addAll(arc5, arc6);
        HBgp3.getChildren().add(Gp3);
        HBgp3.setAlignment(Pos.BOTTOM_CENTER);
        GaugesGrid.add(HBgp3, 2, 0);
        StackPane BPT3 = new StackPane();
        BPT3.getChildren().add(Pressure);
        BPT3.setAlignment(Pos.BOTTOM_CENTER);
        GaugesGrid.add(BPT3, 2, 0);

        GridPane BarGrid = new GridPane();
        BarGrid.getStyleClass().add("mygridStyle");
        ColumnConstraints colum3 = new ColumnConstraints();
        colum3.setPercentWidth(92);
        BarGrid.getColumnConstraints().add(colum3);
        for (int i = 0; i < 6; i++) {
            RowConstraints row4 = new RowConstraints();
            row4.setPercentHeight(15);
            BarGrid.getRowConstraints().add(row4);
        }
        BarGrid.setAlignment(Pos.CENTER);
        BarGrid.setGridLinesVisible(false);
        RightGrid.add(BarGrid, 0, 3);

        Stop[] stops1 = new Stop[]{new Stop(0, Color.rgb(93, 224, 230)), new Stop(1, Color.rgb(0, 77, 173))};
        LinearGradient lg1 = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops1);

        Temperature = new Text("30.08 °C");
//				Temperature.setFont(Font.font("Brocades Sans"));
        Temperature.setFill(Color.BLACK);
        Temperature.setId("gauges1");
        R1 = new Rectangle();
        R1.setHeight(13);
        R1.setWidth(214.1977);
        R1.setArcHeight(20d);
        R1.setArcWidth(15d);
        R1.setFill(lg1);
        R1.setStrokeLineCap(StrokeLineCap.ROUND);
        BarGrid.add(Temperature, 0, 0);
        BarGrid.add(R1, 0, 1);
        GridPane.setHalignment(Temperature, HPos.RIGHT);
        GridPane.setHalignment(R1, HPos.LEFT);

        Acceleration = new Text("22.46 m/s^2");
//				Acceleration.setFont(Font.font("Brocades Sans"));
        Acceleration.setFill(Color.BLACK);
        Acceleration.setId("gauges1");
        R2 = new Rectangle();
        R2.setHeight(13);
        R2.setWidth(102.5);
        R2.setArcHeight(20d);
        R2.setArcWidth(15d);
        R2.setFill(lg1);
        R2.setStrokeLineCap(StrokeLineCap.ROUND);
        BarGrid.add(Acceleration, 0, 2);
        BarGrid.add(R2, 0, 3);
        GridPane.setHalignment(Acceleration, HPos.RIGHT);
        GridPane.setHalignment(R2, HPos.LEFT);


        Voltage = new Text("7.98 V");
//				Voltage.setFont(Font.font("Brocades Sans"));
        Voltage.setFill(Color.BLACK);
        Voltage.setId("gauges1");
        R3 = new Rectangle();
        R3.setHeight(13);
        R3.setWidth(363.5333);
        R3.setArcHeight(20d);
        R3.setArcWidth(15d);
        R3.setFill(lg1);
        R3.setStrokeLineCap(StrokeLineCap.ROUND);
        BarGrid.add(Voltage, 0, 4);
        BarGrid.add(R3, 0, 5);
        GridPane.setHalignment(Voltage, HPos.RIGHT);
        GridPane.setHalignment(R3, HPos.LEFT);

        GridPane StatesGrid = new GridPane();
        StatesGrid.getStyleClass().add("mygridStyle");
        ColumnConstraints colum4 = new ColumnConstraints();
        colum4.setPercentWidth(34);
        StatesGrid.getColumnConstraints().add(colum4);
        ColumnConstraints colum5 = new ColumnConstraints();
        colum5.setPercentWidth(15);
        StatesGrid.getColumnConstraints().add(colum5);
        ColumnConstraints colum6 = new ColumnConstraints();
        colum6.setPercentWidth(34);
        StatesGrid.getColumnConstraints().add(colum6);
        ColumnConstraints colum7 = new ColumnConstraints();
        colum7.setPercentWidth(15);
        StatesGrid.getColumnConstraints().add(colum7);
        for (int i = 0; i < 2; i++) {
            RowConstraints row4 = new RowConstraints();
            row4.setPercentHeight(84 / 2);
            StatesGrid.getRowConstraints().add(row4);
        }
        StatesGrid.setAlignment(Pos.CENTER_RIGHT);
        StatesGrid.setGridLinesVisible(false);
        RightGrid.add(StatesGrid, 0, 1);

        Circle parachute_circle = new Circle(20);
        parachute_circle.setCenterX(0);
        parachute_circle.setCenterY(0);
        parachute_circle.setFill(Color.rgb(235, 253, 255));
//        parachute_circle.setFill(Color.rgb(13, 211, 254));
        parachute_circle.setStroke(Color.rgb(0, 0, 0, 1));
        parachute_circle.setStrokeWidth(2);
        StatesGrid.add(parachute_circle, 1, 0);
        StatesGrid.setHalignment(parachute_circle, HPos.CENTER);

        Circle aerobreaking_circle = new Circle(20);
        aerobreaking_circle.setCenterX(0);
        aerobreaking_circle.setCenterY(0);
        aerobreaking_circle.setFill(Color.rgb(235, 253, 255));
//        aerobreaking_circle.setFill(Color.rgb(13, 211, 254));
        aerobreaking_circle.setStroke(Color.rgb(0, 0, 0, 1));
        aerobreaking_circle.setStrokeWidth(2);
        StatesGrid.add(aerobreaking_circle, 1, 1);
        StatesGrid.setHalignment(aerobreaking_circle, HPos.CENTER);

        Circle camera_circle = new Circle(20);
        camera_circle.setCenterX(0);
        camera_circle.setCenterY(0);
//        camera_circle.setFill(Color.rgb(235, 253, 255));
        camera_circle.setFill(Color.rgb(13, 211, 254));
        camera_circle.setStroke(Color.rgb(0, 0, 0, 1));
        camera_circle.setStrokeWidth(2);
        StatesGrid.add(camera_circle, 3, 0);
        StatesGrid.setHalignment(camera_circle, HPos.CENTER);

        Circle system_circle = new Circle(20);
        system_circle.setCenterX(0);
        system_circle.setCenterY(0);
        system_circle.setFill(Color.rgb(235, 253, 255));
        system_circle.setStroke(Color.rgb(0, 0, 0, 1));
        system_circle.setStrokeWidth(2);
        StatesGrid.add(system_circle, 3, 1);
        StatesGrid.setHalignment(system_circle, HPos.CENTER);

        GridPane MSGrid = new GridPane();
        MSGrid.getStyleClass().add("mygridStyle");
        ColumnConstraints colum8 = new ColumnConstraints();
        colum8.setPercentWidth(30);
        MSGrid.getColumnConstraints().add(colum8);
        for (int i = 0; i < 5; i++) {
            RowConstraints row5 = new RowConstraints();
            row5.setPercentHeight(7);
            MSGrid.getRowConstraints().add(row5);
            RowConstraints row7 = new RowConstraints();
            row7.setPercentHeight(6);
            MSGrid.getRowConstraints().add(row7);
        }
        RowConstraints row8 = new RowConstraints();
        row8.setPercentHeight(7);
        MSGrid.getRowConstraints().add(row8);
        RowConstraints row9 = new RowConstraints();
        row9.setPercentHeight(5);
        MSGrid.getRowConstraints().add(row9);
        MSGrid.setAlignment(Pos.BOTTOM_LEFT);
        MSGrid.setGridLinesVisible(false);
        RightGrid.add(MSGrid, 0, 0);

        ms_circle = new Circle(8);
        ms_circle.setCenterX(0);
        ms_circle.setCenterY(0);
        ms_circle.setFill(Color.rgb(235, 253, 255));
        ms_circle.setStroke(Color.rgb(0, 0, 0, 1));
        ms_circle.setStrokeWidth(2);
        MSGrid.add(ms_circle, 0, 0);
        StatesGrid.setHalignment(ms_circle, HPos.CENTER);
        ms_line = new Line();
        ms_line.setStartY(0);
        ms_line.setEndY(12);
        ms_line.setStrokeWidth(3);
//		ms_line.setEffect(shadow);
        ms_line.setStroke(Color.rgb(0, 0, 0));
        MSGrid.add(ms_line, 0, 1);
        StatesGrid.setHalignment(ms_line, HPos.CENTER);

        ms_circle1 = new Circle(8);
        ms_circle1.setCenterX(0);
        ms_circle1.setCenterY(0);
        ms_circle1.setFill(Color.rgb(235, 253, 255));
        ms_circle1.setStroke(Color.rgb(0, 0, 0, 1));
        ms_circle1.setStrokeWidth(2);
        MSGrid.add(ms_circle1, 0, 2);
        StatesGrid.setHalignment(ms_circle1, HPos.CENTER);
        ms_line1 = new Line();
        ms_line1.setStartY(0);
        ms_line1.setEndY(12);
        ms_line1.setStrokeWidth(3);
//		ms_line.setEffect(shadow);
        ms_line1.setStroke(Color.rgb(0, 0, 0));
        MSGrid.add(ms_line1, 0, 3);
        StatesGrid.setHalignment(ms_line1, HPos.CENTER);

        ms_circle2 = new Circle(8);
        ms_circle2.setCenterX(0);
        ms_circle2.setCenterY(0);
        ms_circle2.setFill(Color.rgb(235, 253, 255));
        ms_circle2.setStroke(Color.rgb(0, 0, 0, 1));
        ms_circle2.setStrokeWidth(2);
        MSGrid.add(ms_circle2, 0, 4);
        StatesGrid.setHalignment(ms_circle2, HPos.CENTER);
        ms_line2 = new Line();
        ms_line2.setStartY(0);
        ms_line2.setEndY(12);
        ms_line2.setStrokeWidth(3);
//		ms_line.setEffect(shadow);
        ms_line2.setStroke(Color.rgb(0, 0, 0));
        MSGrid.add(ms_line2, 0, 5);
        StatesGrid.setHalignment(ms_line2, HPos.CENTER);

        ms_circle3 = new Circle(8);
        ms_circle3.setCenterX(0);
        ms_circle3.setCenterY(0);
        ms_circle3.setFill(Color.rgb(235, 253, 255));
        ms_circle3.setStroke(Color.rgb(0, 0, 0, 1));
        ms_circle3.setStrokeWidth(2);
        MSGrid.add(ms_circle3, 0, 6);
        StatesGrid.setHalignment(ms_circle3, HPos.CENTER);
        ms_line3 = new Line();
        ms_line3.setStartY(0);
        ms_line3.setEndY(12);
        ms_line3.setStrokeWidth(3);
//		ms_line.setEffect(shadow);
        ms_line3.setStroke(Color.rgb(0, 0, 0));
        MSGrid.add(ms_line3, 0, 7);
        StatesGrid.setHalignment(ms_line3, HPos.CENTER);

        ms_circle4 = new Circle(8);
        ms_circle4.setCenterX(0);
        ms_circle4.setCenterY(0);
        ms_circle4.setFill(Color.rgb(13, 211, 254));
        ms_circle4.setStroke(Color.rgb(0, 0, 0, 1));
        ms_circle4.setStrokeWidth(2);
        MSGrid.add(ms_circle4, 0, 8);
        StatesGrid.setHalignment(ms_circle4, HPos.CENTER);
        ms_line4 = new Line();
        ms_line4.setStartY(0);
        ms_line4.setEndY(12);
        ms_line4.setStrokeWidth(3);
//		ms_line.setEffect(shadow);
        ms_line4.setStroke(Color.rgb(0, 0, 0));
        MSGrid.add(ms_line4, 0, 9);
        StatesGrid.setHalignment(ms_line4, HPos.CENTER);

        ms_circle5 = new Circle(8);
        ms_circle5.setCenterX(0);
        ms_circle5.setCenterY(0);
        ms_circle5.setFill(Color.rgb(13, 211, 254));
        ms_circle5.setStroke(Color.rgb(0, 0, 0, 1));
        ms_circle5.setStrokeWidth(2);
        MSGrid.add(ms_circle5, 0, 10);
        StatesGrid.setHalignment(ms_circle5, HPos.CENTER);

        GridPane CommandsContainer = new GridPane();
        CommandsContainer.getStyleClass().add("mygridStyle");
        ColumnConstraints colum9 = new ColumnConstraints();
        colum9.setPercentWidth(90);
        CommandsContainer.getColumnConstraints().add(colum9);
        RowConstraints row10 = new RowConstraints();
        row10.setPercentHeight(35);
        CommandsContainer.getRowConstraints().add(row10);
        RowConstraints row11 = new RowConstraints();
        row11.setPercentHeight(20);
        CommandsContainer.getRowConstraints().add(row11);
        RowConstraints row12 = new RowConstraints();
        row12.setPercentHeight(33);
        CommandsContainer.getRowConstraints().add(row12);
        CommandsContainer.setAlignment(Pos.CENTER_RIGHT);
        CommandsContainer.setGridLinesVisible(false);
        RightGrid.add(CommandsContainer, 0, 4);

        GridPane CommandsGrid = new GridPane();
        CommandsGrid.getStyleClass().add("mygridStyle");
        for (int i = 0; i < 4; i++) {
            ColumnConstraints colum10 = new ColumnConstraints();
            colum10.setPercentWidth(25);
            CommandsGrid.getColumnConstraints().add(colum10);
        }
        RowConstraints row13 = new RowConstraints();
        row13.setPercentHeight(100);
        CommandsGrid.getRowConstraints().add(row13);
        CommandsGrid.setAlignment(Pos.CENTER_LEFT);
        CommandsGrid.setGridLinesVisible(true);
        CommandsContainer.add(CommandsGrid, 0, 0);

        GridPane CommandsGrid1 = new GridPane();
        CommandsGrid1.getStyleClass().add("mygridStyle");
        for (int i = 0; i < 2; i++) {
            ColumnConstraints colum11 = new ColumnConstraints();
            colum11.setPercentWidth(50);
            CommandsGrid1.getColumnConstraints().add(colum11);
        }
        RowConstraints row14 = new RowConstraints();
        row14.setPercentHeight(100);
        CommandsGrid1.getRowConstraints().add(row14);
        CommandsGrid1.setAlignment(Pos.CENTER_LEFT);
        CommandsGrid1.setGridLinesVisible(false);
        CommandsContainer.add(CommandsGrid1, 0, 2);

        Button refr = new Button("REFRESH PORTS");
        refr.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                sp_combo.getItems().clear();
                SPort();
            }
        });
        Button mode = new Button("CHANGE MODE");
        mode.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Main.setEscenaDos();
            }
        });
        refr.setPrefWidth(180);
        mode.setPrefWidth(180);
        CommandsGrid1.add(refr, 0, 0);
        CommandsGrid1.setHalignment(refr, HPos.CENTER);
        CommandsGrid1.add(mode, 1, 0);
        CommandsGrid1.setHalignment(mode, HPos.CENTER);

        ImageView charts_button = new ImageView(new Image("/images/1.png"));
        charts_button.setX(0);
        charts_button.setY(0);
        charts_button.setFitWidth(y * 0.06);
        charts_button.setPreserveRatio(true);
        charts_button.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("REFRESH CHARTS!");
                GRF1.getData().clear();
                GRF2.getData().clear();
                GRF3.getData().clear();
                GRF4.getData().clear();
                event.consume();
            }
        });

        CommandsGrid.add(charts_button, 0, 0);
        CommandsGrid.setHalignment(charts_button, HPos.CENTER);
        ImageView buzzer_button = new ImageView(new Image("/images/2.png"));
        buzzer_button.setX(0);
        buzzer_button.setY(0);
        buzzer_button.setFitWidth(y * 0.06);
        buzzer_button.setPreserveRatio(true);
        CommandsGrid.add(buzzer_button, 1, 0);
        CommandsGrid.setHalignment(buzzer_button, HPos.CENTER);
        ImageView cali_button = new ImageView(new Image("/images/3.png"));
        cali_button.setX(0);
        cali_button.setY(0);
        cali_button.setFitWidth(y * 0.06);
        cali_button.setPreserveRatio(true);
        CommandsGrid.add(cali_button, 2, 0);
        CommandsGrid.setHalignment(cali_button, HPos.CENTER);
        SPort();
        root1.getChildren().addAll(background, root);
    }

    private void SPort() {
//        System.out.println("Puerto Serial");
        SerialPort[] puertos = SerialPort.getCommPorts();
        for (SerialPort puerto : puertos) {
            System.out.println(puerto.getSystemPortName());
            sp_combo.getItems().add(puerto.getSystemPortName());
        }
    }

    public void Lectura() {
        PuertoSerial = SerialPort.getCommPort(sp_combo.getValue().toString());
        PuertoSerial.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);

        if(PuertoSerial.openPort()){
            try {
                if (runThread == false) {
                    startThread = true;
                    runThread = true;
                    startThrearCronus();
                } }catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("Error en el metodo openport" + e);
            }

            Task tarea = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try (Scanner scanner = new Scanner(PuertoSerial.getInputStream())) {
                        while (scanner.hasNextLine()) {
                            line = (scanner.nextLine());
                            String[] tel = line.split(",");
                            System.out.println(line);

                            try {
                                if (tel.length >= 20 && tel.length <= 23) {
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            t = (System.currentTimeMillis() - t0) / 1000;
//
                                            GRF1.getData().add(new XYChart.Data(t, Float.parseFloat(tel[9])));// Temperature
                                            GRF2.getData().add(new XYChart.Data(t,Float.parseFloat(tel[21])));//Acceleation
                                            GRF3.getData().add(new XYChart.Data(t, Float.parseFloat(tel[10])));// Pressure
                                            GRF4.getData().add(new XYChart.Data(t, Float.parseFloat(tel[5])));// Height

                                            GPS_Altitude.setText(tel[13] + "m");
                                            GPS_Latitude.setText(tel[14] + "°N");
                                            dataLat=Double.parseDouble(tel[14]);
                                            GPS_Longitude.setText(tel[15] + " °W");
                                            dataLon=Double.parseDouble(tel[15]);
                                            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                                            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                                            String timeStamp = dateFormat.format(new Date());
                                            GPS_Time.setText(String.valueOf(timeStamp ));
                                            GPS_Sats.setText(tel[16]);
//                                            Anim_GPS(dataLat,dataLon,stateCapitalsPST,multipoint,multipointGraphic,graphicsOverlay);
                                            Anim_GPS(dataLat,dataLon,point1);

                                            Tilt_X.setText(tel[17] + "°");
                                            Tilt_Y.setText(tel[18] + "°");
                                            Rot_Z.setText(tel[19]+"°");

                                            PoE.setText(tel[22] + "°");
                                            dataPoE=Double.parseDouble(tel[22]);
                                            RotateTransition rt = new RotateTransition(Duration.INDEFINITE, pointing);
                                            rt.setByAngle(0);
                                            rt.setFromAngle(dataPoE);
                                            rt.setCycleCount(Timeline.INDEFINITE);
                                            rt.setAutoReverse(true);
                                            rt.play();
//
                                            Air_speed.setText(tel[6] + "\nm/s");
                                            dataAir = Double.parseDouble(tel[6]);
                                            Anim_Speed(dataAir,arc2);

                                            Altitude.setText(tel[5] + "\nm");
//                                            dataAlt = Double.parseDouble(tel[5]);
//                                            Anim_Alt(dataAlt,arc4);

                                            Pressure.setText(tel[10] + "\nKPa");
                                            dataPress = Double.parseDouble(tel[10]);
                                            Anim_Press(dataPress,arc6);
//

                                            Temperature.setText(tel[9] + "°C");
                                            dataTemp = Double.parseDouble(tel[9]);
                                            Anim_Temp(dataTemp, R1);

                                            Acceleration.setText(tel[21] + " m/s^2");
                                            dataAcce = Double.parseDouble(tel[21]);
                                            Anim_Acce(dataAcce,R2);

                                            Voltage.setText(tel[11] + "V");
                                            dataBattery = Double.parseDouble(tel[11]);
                                            Anim_Voltage(dataBattery, R3, dataWRect);

                                            PackageC.setText(tel[2]);

                                            dataMS = Double.parseDouble(tel[4]);
                                            Anim_MS(dataMS,ms_circle,ms_circle1,ms_circle2,ms_circle3,ms_circle4,ms_circle5);
                                        }
                                    });

                                }
                                csv.save(line);

                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                System.out.println("Error en la linea de elementos" + e);

                            }

                        }

                    }

                    catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        System.out.println("Error en el metodo general" + e);

                    }

                    return null;
                }

            };

            Thread thread1 = new Thread(tarea);
            thread1.setDaemon(true);
            thread1.start();

        } else {

            if (runThread == false) {
                startThread = true;
                runThread = true;
                startThrearCronus();
            }
            System.out.println("FINISH THREAD CLOCK");
        }
    }
    private static void startThrearCronus() {
        if (startThread = true) {
            System.out.println("START THREAD");
            cronus = new clockGS(TM);
            try {
                cronus.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
//					Thread threadClock = new Thread(cronus);
//					threadClock.setDaemon(true);
//					threadClock.start();
        }
    }
    public void Anim_Speed(double speed, Arc arc2) {
        arc2.setLength(-((0.5454*speed)+16.3636));
    }
    public void Anim_Alt(double alt, Arc arc4) {
        arc4.setLength(-(0.18*alt));
    }
    public void Anim_Press(double press, Arc arc6) {
        arc6.setLength(-(0.45*press));
    }
    public void Anim_Temp(double temp, Rectangle R1) {
        R1.setWidth((3.5652*temp)+106.9565);
    }
    public void Anim_Acce(double acce, Rectangle R2) {
        R2.setWidth(0.36*acce);
    }
    public void Anim_Voltage(double batt, Rectangle R3, double WR) {
        R3.setWidth((batt*(WR))/9);
    }
    public void Anim_GPS(double alt, double lon, Point po) {
        po = new Point(lon, alt, SpatialReferences.getWgs84());
    }
    //    public void Anim_GPS(double alt, double lon,PointCollection stateCapitalsPST) {
//        stateCapitalsPST.add(lon, alt);
//    }
    public static void Anim_MS(double dataMS,Circle ms_circle,Circle ms_circle1,Circle ms_circle2,Circle ms_circle3,Circle ms_circle4,Circle ms_circle5) {
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.WHITE);
        shadow.setRadius(40);
        if(dataMS==1) {
            ms_circle.setFill(Color.rgb(13, 211, 254, 0.5));
            ms_circle.setEffect(shadow);
            ms_circle1.setFill(Color.rgb(200, 200, 255, 0.1));
            ms_circle2.setFill(Color.rgb(200, 200, 255, 0.1));
            ms_circle3.setFill(Color.rgb(200, 200, 255, 0.1));
            ms_circle4.setFill(Color.rgb(200, 200, 255, 0.1));
            ms_circle5.setFill(Color.rgb(235, 253, 255));
        }
        else if(dataMS==2) {
//			ms_line.setEffect(shadow);
            ms_circle.setFill(Color.rgb(13, 211, 254, 0.5));
            ms_circle.setEffect(shadow);
            ms_circle1.setFill(Color.rgb(13, 211, 254, 0.5));
            ms_circle1.setEffect(shadow);
            ms_circle2.setFill(Color.rgb(200, 200, 255, 0.1));
            ms_circle3.setFill(Color.rgb(200, 200, 255, 0.1));
            ms_circle4.setFill(Color.rgb(200, 200, 255, 0.1));
            ms_circle5.setFill(Color.rgb(200, 200, 255, 0.1));
        }
        else if(dataMS==3) {
            ms_circle.setFill(Color.rgb(13, 211, 254, 0.5));
            ms_circle.setEffect(shadow);
            ms_circle1.setFill(Color.rgb(13, 211, 254, 0.5));
            ms_circle1.setEffect(shadow);
            ms_circle2.setFill(Color.rgb(13, 211, 254, 0.5));
            ms_circle2.setEffect(shadow);
            ms_circle3.setFill(Color.rgb(200, 200, 255, 0.1));
            ms_circle4.setFill(Color.rgb(200, 200, 255, 0.1));
            ms_circle5.setFill(Color.rgb(200, 200, 255, 0.1));
        }
        else if(dataMS==4) {
            ms_circle.setFill(Color.rgb(13, 211, 254, 0.5));
            ms_circle.setEffect(shadow);
            ms_circle1.setFill(Color.rgb(13, 211, 254, 0.5));
            ms_circle1.setEffect(shadow);
            ms_circle2.setFill(Color.rgb(13, 211, 254, 0.5));
            ms_circle2.setEffect(shadow);
            ms_circle3.setFill(Color.rgb(13, 211, 254, 0.5));
            ms_circle3.setEffect(shadow);
            ms_circle4.setFill(Color.rgb(200, 200, 255, 0.1));
            ms_circle5.setFill(Color.rgb(200, 200, 255, 0.1));
        }
        else if(dataMS==5) {
            ms_circle.setFill(Color.rgb(13, 211, 254, 0.5));
            ms_circle.setEffect(shadow);
            ms_circle1.setFill(Color.rgb(13, 211, 254, 0.5));
            ms_circle1.setEffect(shadow);
            ms_circle2.setFill(Color.rgb(13, 211, 254, 0.5));
            ms_circle2.setEffect(shadow);
            ms_circle3.setFill(Color.rgb(13, 211, 254, 0.5));
            ms_circle3.setEffect(shadow);
            ms_circle4.setFill(Color.rgb(13, 211, 254, 0.5));
            ms_circle4.setEffect(shadow);
            ms_circle5.setFill(Color.rgb(200, 200, 255, 0.1));
        }
        else if(dataMS==6) {
            ms_circle.setFill(Color.rgb(13, 211, 254, 0.5));
            ms_circle.setEffect(shadow);
            ms_circle1.setFill(Color.rgb(13, 211, 254, 0.5));
            ms_circle1.setEffect(shadow);
            ms_circle2.setFill(Color.rgb(13, 211, 254, 0.5));
            ms_circle2.setEffect(shadow);
            ms_circle3.setFill(Color.rgb(13, 211, 254, 0.5));
            ms_circle3.setEffect(shadow);
            ms_circle4.setFill(Color.rgb(13, 211, 254, 0.5));
            ms_circle4.setEffect(shadow);
            ms_circle5.setFill(Color.rgb(13, 211, 254, 0.5));
            ms_circle5.setEffect(shadow);
        }
    }
}