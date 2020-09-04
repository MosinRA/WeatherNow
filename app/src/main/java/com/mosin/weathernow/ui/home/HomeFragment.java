package com.mosin.weathernow.ui.home;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import com.mosin.weathernow.R;
import com.mosin.weathernow.Request;
import com.mosin.weathernow.ViewInfo;
import com.mosin.weathernow.model.WeatherRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.mosin.weathernow.MainActivity.cityChoice;

public class HomeFragment extends Fragment {
    WeatherRequest weatherRequest;
    private boolean wind, pressure, humidity;
    SharedPreferences sharedPreferences;
    private TextView dateNow, showTempView, showWindSpeed, showPressure, showHumidity, cityName, time;
    private ImageView icoWeather;
    private SearchView searchText;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        ViewInfo.createInfo();
        findView(view);
        dateInit();
        sendErInternetAlert();
        sendErUrlAlert();
        initSettingSwitch();
        setParam();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull final Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem search = menu.findItem(R.id.action_search);
        searchText = (SearchView) search.getActionView(); // строка поиска
        searchText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                cityChoice = query;
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("cityName", cityChoice);
                editor.apply();
                cityName.setText(cityChoice);
                ViewInfo.createInfo();
                setParam();
                searchText.onActionViewCollapsed();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
    }

    public void findView(View view) {
        showTempView = view.findViewById(R.id.showTempViewFragmentShowCityInfo);
        showWindSpeed = view.findViewById(R.id.windSpeedView);
        showPressure = view.findViewById(R.id.pressureView);
        showHumidity = view.findViewById(R.id.humidityView);
        icoWeather = view.findViewById(R.id.weatherIcoView);
        dateNow = view.findViewById(R.id.date);
        cityName = view.findViewById(R.id.cityNameView);
        time = view.findViewById(R.id.time);
    }

    public void initSettingSwitch() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        wind = sharedPreferences.getBoolean("Wind", false);
        pressure = sharedPreferences.getBoolean("Pressure", false);
        humidity = sharedPreferences.getBoolean("Humidity", false);
        cityChoice = sharedPreferences.getString("cityName", "cityNameSearch");
        cityName.setText(cityChoice);
    }

    public void setParam() {
        showTempView.setText(ViewInfo.getShowTempView());
        if (wind) {
            showWindSpeed.setText(String.format("%s %s", getResources().getString(R.string.wind_speed), ViewInfo.getShowWindSpeed()));
        } else {
            showWindSpeed.setVisibility(View.GONE);
        }
        if (pressure) {
            showPressure.setText(String.format("%s %s", getResources().getString(R.string.pressure), ViewInfo.getShowPressure()));
        } else {
            showPressure.setVisibility(View.GONE);
        }
        if (humidity) {
            showHumidity.setText(String.format("%s %s", getResources().getString(R.string.humidity), ViewInfo.getShowHumidity()));
        } else {
            showHumidity.setVisibility(View.GONE);
        }
        setIcoViewImage();
    }

    private void setIcoViewImage() {
        if (Request.getIcoId() == 1) {
            icoWeather.setImageResource(R.drawable.clear_sky_d);
        } else if (Request.getIcoId() == 2) {
            icoWeather.setImageResource(R.drawable.clear_sky_n);
        } else if (Request.getIcoId() == 3) {
            icoWeather.setImageResource(R.drawable.few_clouds_d);
        } else if (Request.getIcoId() == 4) {
            icoWeather.setImageResource(R.drawable.few_clouds_n);
        } else if (Request.getIcoId() == 5) {
            icoWeather.setImageResource(R.drawable.rain_d);
        } else if (Request.getIcoId() == 6) {
            icoWeather.setImageResource(R.drawable.rain_n);
        } else if (Request.getIcoId() == 7) {
            icoWeather.setImageResource(R.drawable.snow);
        } else if (Request.getIcoId() == 8) {
            icoWeather.setImageResource(R.drawable.mist);
        }
    }

    private void dateInit() {
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String dateText = dateFormat.format(currentDate);
        DateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String timeText = timeFormat.format(currentDate);
        dateNow.setText(dateText);
        time.setText(timeText);
    }

    private void sendErInternetAlert() {
        if (Request.isErrorStatus()) {
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
        if (Request.isErrorUrlStatus()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.exclamation)
                    .setMessage("Что то пошло не так")
                    .setIcon(R.mipmap.ic_launcher_round)
                    .setPositiveButton(R.string.ok_button, null);
            AlertDialog alert = builder.create();
            alert.show();
        }
    }
}




