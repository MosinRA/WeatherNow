package com.mosin.weathernow;

public class ViewInfo {
    private static String showTempView, showWindSpeed, showPressure, showHumidity;
    static Request mThing;

    public static void createInfo() {
        mThing = new Request();
        Thread myThready = new Thread(mThing);    //Создание потока "myThready"
        myThready.start();
        showTempView = (String.format("%s °C", Request.getTemperatureValue()));
        showWindSpeed = (String.format("%s м/с", Request.getWindSpeedStr()));
        showPressure = (String.format("%s мб", Request.getPressureText()));
        showHumidity = (String.format("%s %%", Request.getHumidityStr()));
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

