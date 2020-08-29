package com.mosin.weathernow.ui.setting;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.mosin.weathernow.R;

public class SettingFragment extends Fragment {
    SharedPreferences sharedPreferences;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch windSpeedSwitch, pressureSwitch, humiditySwitch;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_setting, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findView(view);
        initWindSwitchTurn();
        pressureSwitchTurn();
        humiditySwitchTurn();
        loadSwitchesMode();
        saveWindSwitchMode();
        savePressureSwitchMode();
        saveHumiditySwitchMode();
    }

    public void findView(View view) {
        windSpeedSwitch = view.findViewById(R.id.windSpeedSwitch);
        pressureSwitch = view.findViewById(R.id.pressureSwitch);
        humiditySwitch = view.findViewById(R.id.humiditySwitch);
    }

    public void initWindSwitchTurn() {
        if (windSpeedSwitch != null) {
            windSpeedSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    saveWindSwitchMode();
                }
            });
        }
    }

    public void pressureSwitchTurn() {
        if (pressureSwitch != null) {
            pressureSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    savePressureSwitchMode();
                }
            });
        }
    }

    public void humiditySwitchTurn() {
        if (humiditySwitch != null) {
            humiditySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    saveHumiditySwitchMode();
                }
            });
        }
    }

    public void saveWindSwitchMode() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("Wind", windSpeedSwitch.isChecked());
        editor.apply();
    }

    public void savePressureSwitchMode() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("Pressure", pressureSwitch.isChecked());
        editor.apply();
    }

    public void saveHumiditySwitchMode() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("Humidity", humiditySwitch.isChecked());
        editor.apply();
    }
    public void loadSwitchesMode() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        windSpeedSwitch.setChecked(sharedPreferences.getBoolean("Wind", false));
        pressureSwitch.setChecked(sharedPreferences.getBoolean("Pressure", false));
        humiditySwitch.setChecked(sharedPreferences.getBoolean("Humidity", false));
    }
}
