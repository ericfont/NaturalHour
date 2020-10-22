// SPDX-License-Identifier: GPL-3.0-or-later
/*
    Copyright (C) 2020 Forrest Guice
    This file is part of Natural Hour.

    Natural Hour is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Natural Hour is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Natural Hour.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.forrestguice.suntimes.naturalhour.ui.widget;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;

import com.forrestguice.suntimes.naturalhour.R;
import com.forrestguice.suntimes.naturalhour.ui.clockview.NaturalHourClockBitmap;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class WidgetPreferenceFragment extends PreferenceFragment
{
    /**
     * @return appWidgetId (apply as widget settings), or 0 if unset (apply as global settings)
     */
    public int getAppWidgetId() {
        return getArguments().getInt("appWidgetId");
    }
    public void setAppWidgetId(int appWidgetId) {
        getArguments().putInt("appWidgetId", appWidgetId);
        initWidgetDefaults();
    }

    public WidgetPreferenceFragment()
    {
        super();
        setArguments(new Bundle());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_widget);
        initWidgetDefaults();
        setHasOptionsMenu(false);
    }

    protected void initWidgetDefaults()
    {
        Context context = getActivity();
        int appWidgetId = getAppWidgetId();
        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID && context != null)
        {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = prefs.edit();
            for (String key : NaturalHourClockBitmap.FLAGS) {                     // copy flags from widget_0 to widget_i
                String prefKey = "widget_0_" + key;
                String widgetKey = "widget_" + appWidgetId + "_" + key;
                editor.putBoolean(widgetKey, prefs.getBoolean(prefKey, NaturalHourClockBitmap.getDefaultFlag(context, key)));
            }
            for (String key : NaturalHourClockBitmap.VALUES) {                    // copy values from widget_0 to widget_i
                String prefKey = "widget_0_" + key;
                String widgetKey = "widget_" + appWidgetId + "_" + key;
                editor.putInt(widgetKey, prefs.getInt(prefKey, NaturalHourClockBitmap.getDefaultValue(context, key)));
            }
            editor.apply();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(onWidgetPrefChanged);
    }

    @Override
    public void onPause()
    {
        PreferenceManager.getDefaultSharedPreferences(getActivity()).unregisterOnSharedPreferenceChangeListener(onWidgetPrefChanged);
        super.onPause();
    }

    private SharedPreferences.OnSharedPreferenceChangeListener onWidgetPrefChanged = new SharedPreferences.OnSharedPreferenceChangeListener()
    {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String prefKey)
        {
            int appWidgetId = getAppWidgetId();
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID)
            {
                if (prefKey.startsWith("widget_0_"))
                {
                    String key = prefKey.replace("widget_0_", "");
                    String widgetKey = "widget_" + appWidgetId + "_" + key;

                    Context context = getActivity();
                    SharedPreferences.Editor editor = prefs.edit();

                    for (String boolPref : NaturalHourClockBitmap.FLAGS)
                    {
                        if (boolPref.equals(key)) {
                            editor.putBoolean(widgetKey, prefs.getBoolean(prefKey, NaturalHourClockBitmap.getDefaultFlag(context, key)));
                            editor.apply();
                            return;
                        }
                    }

                    for (String intPref : NaturalHourClockBitmap.VALUES)
                    {
                        if (intPref.equals(key)) {
                            editor.putInt(widgetKey, prefs.getInt(prefKey, NaturalHourClockBitmap.getDefaultValue(context, key)));
                            editor.apply();
                            return;
                        }
                    }
                }

            } else Log.e("onWidgetPrefChanged", "AppWidgetID is unset! ignoring change to " + prefKey);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

}
