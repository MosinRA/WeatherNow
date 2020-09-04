package com.mosin.weathernow;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.google.gson.Gson;
import com.mosin.weathernow.model.WeatherRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import static com.mosin.weathernow.MainActivity.cityChoice;

public class Request extends Thread {
    private static final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather?q=";
    private static final String API_KEY = "fadb77e5b4ac7ef7017d01ea83fdef18";
    private static boolean errorStatus, errorUrlStatus;
    private static String temperatureValue, pressureText, humidityStr, windSpeedStr, icoView;
    private static int icoId;

    @Override
    public void run()	//Этот метод будет выполнен в побочном потоке
    {
        try {
            final URL uri = new URL(WEATHER_URL + cityChoice + "&units=metric&appid=" + API_KEY);
            // Запоминаем основной поток
            new Thread(new Runnable() {
                public void run() {
                    HttpsURLConnection urlConnection = null;
                    try {
                        urlConnection = (HttpsURLConnection) uri.openConnection();
                        urlConnection.setRequestMethod("GET"); // установка метода получения данных -GET
                        urlConnection.setReadTimeout(10000); // установка таймаута - 10 000 миллисекунд
                        BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream())); // читаем  данные в поток
                        String result = getLines(in);
                        // преобразование данных запроса в модель
                        Gson gson = new Gson();
                        final WeatherRequest weatherRequest = gson.fromJson(result, WeatherRequest.class);
                        // Возвращаемся к основному потоку
                        HandlerThread handlerThread = new HandlerThread("Request");
                        handlerThread.start();
                        final Handler handler = new Handler(handlerThread.getLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                displayWeather(weatherRequest);
                            }
                        });
                        errorStatus = false;
                    } catch (Exception e) {
                        Log.e("D", "Fail connection", e);
                        e.printStackTrace();
                        errorStatus = true;
                    } finally {
                        if (null != urlConnection) {
                            urlConnection.disconnect();
                        }
                    }
                }
            }).start();
        } catch (MalformedURLException e) {
            Log.e("D", "Fail URI", e);
            e.printStackTrace();
            errorUrlStatus = true;
        }
    }

    private static String getLines(BufferedReader reader) {
        StringBuilder rawData = new StringBuilder(1024);
        String tempVariable;

        while (true) {
            try {
                tempVariable = reader.readLine();
                if (tempVariable == null) break;
                rawData.append(tempVariable).append("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rawData.toString();
    }

    private static void displayWeather(WeatherRequest weatherRequest) {
        temperatureValue = String.format(Locale.getDefault(), "%.0f", weatherRequest.getMain().getTemp());
        pressureText = String.format(Locale.getDefault(), "%d", weatherRequest.getMain().getPressure());
        humidityStr = String.format(Locale.getDefault(), "%d", weatherRequest.getMain().getHumidity());
        windSpeedStr = String.format(Locale.getDefault(), "%.0f", weatherRequest.getWind().getSpeed());
        setIcoViewImage(weatherRequest);

    }

    private static void setIcoViewImage(WeatherRequest weatherRequest) {
        icoView = weatherRequest.getWeather()[0].getIcon();
        String icoView = Request.getIconView();
        if (icoView.equals("01d")) {
            icoId = 1;
        } else if (icoView.equals("01n")) {
            icoId = 2;
            ;
        } else if (icoView.equals("02d") || icoView.equals("03d") || icoView.equals("04d")) {
            icoId = 3;
        } else if (icoView.equals("02n") || icoView.equals("03n") || icoView.equals("04n")) {
            icoId = 4;
        } else if (icoView.equals("09d") || icoView.equals("10d")) {
            icoId = 5;
        } else if (icoView.equals("09n") || icoView.equals("10n")) {
            icoId = 6;
        } else if (icoView.equals("13n") || icoView.equals("13d")) {
            icoId = 7;
        } else if (icoView.equals("50n") || icoView.equals("50d")) {
            icoId = 8;
        }
    }


//    public void initCity() {
//         MainActivity activityReference = new MainActivity();
//        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activityReference);
//        cityChoice = sharedPreferences.getString("cityName", "cityNameSearch");
//    }

    public static boolean isErrorStatus() {
        return errorStatus;
    }

    public static int getIcoId() {
        return icoId;
    }

    public static boolean isErrorUrlStatus() {
        return errorUrlStatus;
    }

    public static String getTemperatureValue() {
        return temperatureValue;
    }

    public static String getPressureText() {
        return pressureText;
    }

    public static String getHumidityStr() {
        return humidityStr;
    }

    public static String getWindSpeedStr() {
        return windSpeedStr;
    }

    public static String getIconView() {
        return icoView;
    }
}
