package com.mosin.weathernow;

import androidx.appcompat.app.AppCompatActivity;

public class ViewInfo extends Thread {
    private static String showTempView, showWindSpeed, showPressure, showHumidity;

    public static void createInfo() {
        new Thread(new Runnable() {
            public void run() {
                Request.createWeatherJsonParam();
                showTempView = (String.format("%s °C", Request.getTemperatureValue()));
                showWindSpeed = (String.format("%s м/с", Request.getWindSpeedStr()));
                showPressure = (String.format("%s мб", Request.getPressureText()));
                showHumidity = (String.format("%s %%", Request.getHumidityStr()));
            }
        }).start();
    }


            public static String getShowTempView() {
                return showTempView;
            }

            public static String getShowWindSpeed() {
                return showWindSpeed;
            }

            public static String getShowPressure() {
                return showPressure;
            }

            public static String getShowHumidity() {
                return showHumidity;
            }
        }

