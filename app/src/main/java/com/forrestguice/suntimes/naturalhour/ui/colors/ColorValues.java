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

package com.forrestguice.suntimes.naturalhour.ui.colors;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;

public abstract class ColorValues implements Parcelable
{
    public abstract String[] getColorKeys();
    public int getFallbackColor() { return Color.WHITE; }

    public ColorValues() {}
    public ColorValues(ColorValues other) {
        loadColorValues(other);
    }
    protected ColorValues(Parcel in) {
        loadColorValues(in);
    }
    public ColorValues(SharedPreferences prefs, String prefix) {
        loadColorValues(prefs, prefix);
    }

    public void loadColorValues(@NonNull Parcel in) {
        setID(in.readString());
        for (String key : getColorKeys()) {
            setColor(key, in.readInt());
        }
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getID());
        for (String key : getColorKeys()) {
            dest.writeInt(values.getAsInteger(key));
        }
    }

    public void loadColorValues(@NonNull ColorValues other)
    {
        setID(other.getID());
        for (String key : other.getColorKeys()) {
            setColor(key, other.getColor(key));
        }
    }

    public void loadColorValues(SharedPreferences prefs, String prefix)
    {
        setID(prefs.getString(prefix + KEY_ID, null));
        for (String key : getColorKeys()) {
            setColor(key, prefs.getInt(prefix + key, getFallbackColor()));
        }
    }
    public void putColors(SharedPreferences.Editor prefs, String prefix)
    {
        prefs.putString(prefix + KEY_ID, getID());
        for (String key : getColorKeys()) {
            prefs.putInt(prefix + key, values.getAsInteger(key));
        }
        prefs.apply();
    }
    public void putColors(ContentValues other) {
        other.putAll(values);
    }

    protected ContentValues values = new ContentValues();
    public ContentValues getContentValues() {
        return new ContentValues(values);
    }

    public static final String KEY_ID = "colorValuesID";
    public void setID( String colorsID ) {
        values.put(KEY_ID, colorsID);
    }
    public String getID() {
        return values.getAsString(KEY_ID);
    }

    public static final String SUFFIX_LABEL = "_LABEL";
    protected void setLabel(String key, String label) {
        values.put(key + SUFFIX_LABEL, label);
    }
    public String getLabel(String key) {
        String label = values.getAsString(key + SUFFIX_LABEL);
        return (label != null ? label : key);
    }

    public void setColor(String key, int color) {
        values.put(key, color);
    }

    public int getColor(String key)
    {
        if (values.containsKey(key)) {
            return values.getAsInteger(key);
        } else return getFallbackColor();
    }

    public ArrayList<Integer> getColors()
    {
        ArrayList<Integer> list = new ArrayList<>();
        for (String key : getColorKeys()) {
            list.add(getColor(key));
        }
        return list;
    }

    public int colorKeyIndex(@NonNull String key)
    {
        String[] keys = getColorKeys();
        if (keys != null) {
            for (int i=0; i < keys.length; i++) {
                if (key.equals(keys[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * @return yaml
     */
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder("--- # ColorValues ");
        String valuesID = getID();
        if (valuesID != null) {
            result.append(valuesID);
        }
        result.append("\n");

        for (String key : getColorKeys())
        {
            result.append("- ");
            result.append(key);
            result.append(": \"#");
            result.append( Integer.toHexString(getColor(key)) );
            result.append("\"\n");
        }
        return result.toString();
    }
}