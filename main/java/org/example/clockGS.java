package org.example;

import javafx.scene.text.Text;

public class clockGS extends Thread{
    Text timer;
    public clockGS(Text lb) {
        this.timer = lb;
    }
    @Override
    public void run() {
        try {
            int x = 0;
            while(FlightMode.startThread) {
                Thread.sleep(1000);
                xtnCronus(x);
                x++;
            }

        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    private void xtnCronus(int x) {
        System.out.println(x + Thread.currentThread().getName());
        try {
            FlightMode.seg++;
            if (FlightMode.seg > 59) {
                FlightMode.seg = 0;
                FlightMode.min++;
                if (FlightMode.min > 59) {
                    FlightMode.min = 0;
                    FlightMode.hr++;
                    if (FlightMode.hr > 1) {
                        FlightMode.hr = 0;
                    }
                }
            }
            String noSeg = "", noMin = "", noHr = "";
            if (FlightMode.seg < 10) {
                noSeg = "0" + FlightMode.seg;
            } else {
                noSeg = "" + FlightMode.seg;
            }
            if (FlightMode.min < 10) {
                noMin = "0" + FlightMode.min;
            } else {
                noMin = "" + FlightMode.min;
            }
            if (FlightMode.hr < 10) {
                noHr = "0" + FlightMode.hr;
            } else {
                noHr = "" + FlightMode.hr;
            }

            String clock ="T-"+ noHr + ":" + noMin + ":" + noSeg;

            if (FlightMode.startThread == false) {
                clock = "T-00:00:00";
                FlightMode.seg = 0;
                FlightMode.min = 0;
                FlightMode.hr = 0;
            }
            timer.setText(clock);
        } catch (Exception e) {
            System.out.println("ERROR EN EL HILO");
        }
    }
}
