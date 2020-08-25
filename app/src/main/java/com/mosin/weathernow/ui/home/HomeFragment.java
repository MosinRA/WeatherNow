package com.mosin.weathernow.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.gson.Gson;
import com.mosin.weathernow.R;
import com.mosin.weathernow.model.WeatherRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class HomeFragment extends Fragment {
    private static final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather?q=";
    private static final String API_KEY = "762ee61f52313fbd10a4eb54ae4d4de2";
    private String cityChoice = "Сургут";
    private TextView showCity, showTempView, showWindSpeed, showPressure, showHumidity;
    private ImageView icoWeather;
    private String temperatureValue, pressureText, humidityStr, windSpeedStr;

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        createWeatherJsonParam();
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView cityNameView = root.findViewById(R.id.cityNameView);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                cityNameView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findView(view);
    }

    public void findView(View view) {
        showCity = view.findViewById(R.id.cityNameView);
        showTempView = view.findViewById(R.id.showTempViewFragmentShowCityInfo);
        showWindSpeed = view.findViewById(R.id.windSpeedView);
        showPressure = view.findViewById(R.id.pressureView);
        showHumidity = view.findViewById(R.id.humidityView);
        icoWeather = view.findViewById(R.id.weatherIcoView);
    }

    private void createWeatherJsonParam() {
        try {
            final URL uri = new URL(WEATHER_URL + cityChoice + "&units=metric&appid=" + API_KEY);
            final Handler handler = new Handler(Looper.myLooper()); // Запоминаем основной поток
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
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                displayWeather(weatherRequest);
                            }
                        });
                    } catch (Exception e) {
                        Log.e("D", "Fail connection", e);
                        e.printStackTrace();
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
        }
    }

    private String getLines(BufferedReader reader) {
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

    private void displayWeather(WeatherRequest weatherRequest) {
        temperatureValue = String.format(Locale.getDefault(), "%.0f", weatherRequest.getMain().getTemp());
        pressureText = String.format(Locale.getDefault(), "%d", weatherRequest.getMain().getPressure());
        humidityStr = String.format(Locale.getDefault(), "%d", weatherRequest.getMain().getHumidity());
        windSpeedStr = String.format(Locale.getDefault(), "%.0f", weatherRequest.getWind().getSpeed());
        String icoView = weatherRequest.getWeather()[0].getIcon();
        showTempView.setText(String.format("%s °", temperatureValue));
        showWindSpeed.setText(String.format("%s м/с", windSpeedStr));
        showPressure.setText(String.format("%s мб", pressureText));
        showHumidity.setText(String.format("%s %%", humidityStr));
        setIcoViewImage(weatherRequest);
    }

    private void setIcoViewImage(WeatherRequest weatherRequest) {
        String icoView = weatherRequest.getWeather()[0].getIcon();
        if (icoView.equals("01d")) {
            icoWeather.setImageResource(R.drawable.clear_sky_d);
        } else if (icoView.equals("01n")) {
            icoWeather.setImageResource(R.drawable.clear_sky_n);
        } else if (icoView.equals("02d") || icoView.equals("03d") || icoView.equals("04d")) {
            icoWeather.setImageResource(R.drawable.few_clouds_d);
        } else if (icoView.equals("02n") || icoView.equals("03n") || icoView.equals("04n")) {
            icoWeather.setImageResource(R.drawable.few_clouds_n);
        } else if (icoView.equals("09d") || icoView.equals("10d")) {
            icoWeather.setImageResource(R.drawable.rain_d);
        } else if (icoView.equals("09n") || icoView.equals("10n")) {
            icoWeather.setImageResource(R.drawable.rain_n);
        } else if (icoView.equals("13n") || icoView.equals("13d")) {
            icoWeather.setImageResource(R.drawable.snow);
        } else if (icoView.equals("50n") || icoView.equals("50d")) {
            icoWeather.setImageResource(R.drawable.mist);
        }
    }
}