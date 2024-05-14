package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CSVfile {
    StringBuilder sb;
    String archivo;
    PrintWriter pw;


    String datos = "TEAM_ID, MISSION_TIME, PACKET_COUNT, MODE, STATE, ALTITUDE, AIR_SPEED, HS_DEPLOYED, PC_DEPLOYED, TEMPERATURE, VOLTAGE, PRESSURE, GPS_TIME, GPS_ALTITUDE, GPS_LATITUDE, GPS_LONGITUDE, GPS_SATS, TILT_X, TILT_Y, ROT_Z, CMD_ECHO,ACCELERATION,PO_ERROR";

    String[] Line = {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "","",""};

    public CSVfile() throws FileNotFoundException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss"); // Crear un objeto SimpleDateFormat para dar formato a la fecha
        String timeStamp = dateFormat.format(new Date()); // Crear un objeto Date con la fecha actual
        archivo = "Flight_2012_" + timeStamp + ".csv";  // Crear un archivo CSV con la fecha actual concatenación de la cadena de texto "Telemetria_" y la fecha actual

        pw = new PrintWriter(new File(archivo));    // Crear un objeto PrintWriter con el archivo CSV para escribir en el archivo
        sb = new StringBuilder();   // Crear un objeto StringBuilder para almacenar la cadena de texto
        sb.append(datos);  // Agregar la cadena de texto "datos" al objeto StringBuilder
    }

    public void save(String line) throws FileNotFoundException
    {
        sb.append('\n');
        sb.append(line);
        sb.append(',');
    }

    public void close()
    {
        pw.write(sb.toString());
        pw.close();
    }
}
