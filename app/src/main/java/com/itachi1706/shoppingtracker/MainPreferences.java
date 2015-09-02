package com.itachi1706.shoppingtracker;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;

import com.itachi1706.shoppingtracker.AsyncTasks.AppUpdateChecker;
import com.itachi1706.shoppingtracker.utility.StaticMethods;

import java.util.ArrayList;
import java.util.Collections;

public class MainPreferences extends AppCompatActivity {

    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new GeneralPreferenceFragment())
                .commit();
    }


    /**
     * General Preference Fragment
     */
    public static class GeneralPreferenceFragment extends PreferenceFragment{
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);

            final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

            //Get General Information
            String version = "NULL", packageName = "NULL";
            int versionCode = 0;
            PackageInfo packageInfo;
            try {
                packageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                version = packageInfo.versionName;
                packageName = packageInfo.packageName;
                versionCode = packageInfo.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            //General Information
            findPreference("view_app_version").setSummary(version + "-b" + versionCode);
            findPreference("view_app_name").setSummary(packageName);
            findPreference("view_sdk_version").setSummary(Build.VERSION.RELEASE);

            //Update Information and Settings
            findPreference("launch_updater").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new AppUpdateChecker(getActivity(), sp).execute();
                    return true;
                }
            });

            findPreference("android_changelog").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    String changelog = sp.getString("version-changelog", "l");
                    if (changelog.equals("l")) {
                        //Not available
                        new AlertDialog.Builder(getActivity()).setTitle("No Changelog")
                                .setMessage("No changelog was found. Please check if you can connect to the server")
                                .setPositiveButton(android.R.string.ok, null).show();
                    } else {
                        String[] changelogArr = changelog.split("\n");
                        String body = StaticMethods.getChangelogFromArray(changelogArr);
                        new AlertDialog.Builder(getActivity()).setTitle("Changelog")
                                .setMessage(Html.fromHtml(body)).setPositiveButton("Close", null).show();
                    }
                    return true;
                }
            });

            findPreference("get_releases").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(getResources().getString(R.string.update_link)));
                    startActivity(i);
                    return false;
                }
            });
        }
    }

}
