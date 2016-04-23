/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.simplo_launcher3;

import android.app.Activity;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

/**
 * Settings activity for Launcher. Currently implements the following setting: Allow rotation
 */
public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new LauncherSettingsFragment())
                .commit();
    }

    /**
     * This fragment shows the launcher preferences.
     */
    //TODO 在此加入应用列表设置
    public static class LauncherSettingsFragment extends PreferenceFragment
            implements OnPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.launcher_preferences);

            SwitchPreference pref = (SwitchPreference) findPreference(
                    Utilities.ALLOW_ROTATION_PREFERENCE_KEY);
            ListPreference rowAndColPref = (ListPreference) findPreference(
                    Utilities.ROW_AND_COL_PREFERENCE_KEY);
            pref.setPersistent(false);
            rowAndColPref.setPersistent(false);

            Bundle extras = new Bundle();
            extras.putBoolean(LauncherSettings.Settings.EXTRA_DEFAULT_VALUE, false);
            Bundle value = getActivity().getContentResolver().call(
                    LauncherSettings.Settings.CONTENT_URI,
                    LauncherSettings.Settings.METHOD_GET_BOOLEAN,
                    Utilities.ALLOW_ROTATION_PREFERENCE_KEY, extras);
            pref.setChecked(value.getBoolean(LauncherSettings.Settings.EXTRA_VALUE));

            pref.setOnPreferenceChangeListener(this);

            //2016.4.23 增加设置列表行列数
            Bundle rowColExtras = new Bundle();
            rowColExtras.putString(LauncherSettings.Settings.EXTRA_DEFAULT_VALUE, "0");
            Bundle rowColValue = getActivity().getContentResolver().call(
                    LauncherSettings.Settings.CONTENT_URI,
                    LauncherSettings.Settings.METHOD_GET_STRING,
                    Utilities.ROW_AND_COL_PREFERENCE_KEY, rowColExtras);
            rowAndColPref.setValue(rowColValue.getString(LauncherSettings.Settings.EXTRA_VALUE));
            rowAndColPref.setOnPreferenceChangeListener(this);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            Bundle extras = new Bundle();
            if(preference.getKey().equals(Utilities.ALLOW_ROTATION_PREFERENCE_KEY)) {
                extras.putBoolean(LauncherSettings.Settings.EXTRA_VALUE, (Boolean) newValue);
                getActivity().getContentResolver().call(
                        LauncherSettings.Settings.CONTENT_URI,
                        LauncherSettings.Settings.METHOD_SET_BOOLEAN,
                        preference.getKey(), extras);
            } else if(preference.getKey().equals(Utilities.ROW_AND_COL_PREFERENCE_KEY)) {   //2016.4.23 增加设置列表行列数
                extras.putString(LauncherSettings.Settings.EXTRA_VALUE, (String) newValue);
                getActivity().getContentResolver().call(
                        LauncherSettings.Settings.CONTENT_URI,
                        LauncherSettings.Settings.METHOD_SET_STRING,
                        preference.getKey(), extras);
            }
            return true;
        }
    }
}
