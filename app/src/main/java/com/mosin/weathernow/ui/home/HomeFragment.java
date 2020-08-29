package com.mosin.weathernow.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.gson.Gson;
import com.mosin.weathernow.R;
import com.mosin.weathernow.model.WeatherRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

public class HomeFragment extends Fragment {
    SharedPreferences sharedPreferences;
    private static final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather?q=";
    private static final String API_KEY = "762ee61f52313fbd10a4eb54ae4d4de2";
    private static String cityChoice = "Сургут";
    private TextView dateNow, showTempView, showWindSpeed, showPressure, showHumidity, cityName;
    private Button searchCityBtn;
    private ImageView icoWeather;
    boolean errorStatus, errorUrlStatus;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        createWeatherJsonParam();
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findView(view);
        dateInit();
        sendErInternetAlert();
        setOnClickBtn();
        sendErUrlAlert();
    }

    public void findView(View view) {
        showTempView = view.findViewById(R.id.showTempViewFragmentShowCityInfo);
        showWindSpeed = view.findViewById(R.id.windSpeedView);
        showPressure = view.findViewById(R.id.pressureView);
        showHumidity = view.findViewById(R.id.humidityView);
        icoWeather = view.findViewById(R.id.weatherIcoView);
        dateNow = view.findViewById(R.id.date);
        cityName = view.findViewById(R.id.cityNameView);
        searchCityBtn = view.findViewById(R.id.searchBtn);
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
                        }); errorStatus = false;
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
        String temperatureValue, pressureText, humidityStr, windSpeedStr;
        temperatureValue = String.format(Locale.getDefault(), "%.0f", weatherRequest.getMain().getTemp());
        pressureText = String.format(Locale.getDefault(), "%d", weatherRequest.getMain().getPressure());
        humidityStr = String.format(Locale.getDefault(), "%d", weatherRequest.getMain().getHumidity());
        windSpeedStr = String.format(Locale.getDefault(), "%.0f", weatherRequest.getWind().getSpeed());
        cityName.setText(cityChoice);

        boolean wind, pressure, humidity;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        wind = sharedPreferences.getBoolean("Wind", false);
        pressure = sharedPreferences.getBoolean("Pressure", false);
        humidity = sharedPreferences.getBoolean("Humidity", false);

        showTempView.setText(String.format("%s °", temperatureValue));

        if (wind){
            showWindSpeed.setText(String.format("%s м/с", getResources().getString(R.string.wind_speed) + " " + windSpeedStr));
        }else {
            showWindSpeed.setVisibility(View.GONE);
        }
        if (pressure){
            showPressure.setText(String.format("%s мб", getResources().getString(R.string.pressure) + " " + pressureText));
        } else {
            showPressure.setVisibility(View.GONE);
        }
        if (humidity) {
            showHumidity.setText(String.format("%s %%", getResources().getString(R.string.humidity) + " " + humidityStr));
        }else {
            showHumidity.setVisibility(View.GONE);
        }
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

    private void dateInit() {
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String dateText = dateFormat.format(currentDate);
        dateNow.setText(dateText);
    }

    private void sendErInternetAlert() {
        if (errorStatus) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.exclamation)
                    .setMessage(R.string.msg_to_er_internet)
                    .setIcon(R.mipmap.ic_launcher_round)
                    .setPositiveButton(R.string.ok_button, null);
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void sendErUrlAlert() {
        if (errorUrlStatus) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.exclamation)
                    .setMessage(R.string.msg_to_er_internet)
                    .setIcon(R.mipmap.ic_launcher_round)
                    .setPositiveButton(R.string.ok_button, null);
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void setOnClickBtn (){
        searchCityBtn.setOnClickListener(clickAlertDialogView);
    }

    private View.OnClickListener clickAlertDialogView = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Objects.requireNonNull(getContext()));
            final View contentView = getLayoutInflater().inflate(R.layout.alert_dialog, null);
            builder.setTitle(R.string.enter_name)
                    .setView(contentView)
                    .setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            EditText editText = contentView.findViewById(R.id.search_editor);
                            cityChoice = editText.getText().toString();
                            cityName.setText(cityChoice);
                            createWeatherJsonParam();
                        }
                    });
            androidx.appcompat.app.AlertDialog alert = builder.create();
            alert.show();
        }
    };
}




