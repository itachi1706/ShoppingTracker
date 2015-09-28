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
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Toast;

import com.itachi1706.shoppingtracker.AsyncTasks.AppUpdateChecker;
import com.itachi1706.shoppingtracker.Database.ListDB;
import com.itachi1706.shoppingtracker.utility.StaticMethods;
import com.itachi1706.shoppingtracker.utility.ToastHelper;

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

        SharedPreferences sp;

        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);

            sp = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

            if (sp.contains("debug")) sp.edit().remove("debug").apply();    //Remove debug code

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
            Preference appVersion = findPreference("view_app_version");
            appVersion.setSummary(version + "-b" + versionCode);
            enable_developer_testing_mode(appVersion);
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
                    return true;
                }
            });

            findPreference("view_cart_json").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    String json = sp.getString("cart_json", "There is no Cart JSON stored");
                    new AlertDialog.Builder(getActivity()).setMessage(json)
                            .setPositiveButton("Close", null).show();
                    return true;
                }
            });

            findPreference("remove_cart_json").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    final String json = sp.getString("cart_json", "carti");
                    sp.edit().remove("cart_json").apply();
                    Snackbar.make(getActivity().findViewById(android.R.id.content), "Removed Cart History", Snackbar.LENGTH_SHORT)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (json.equals("carti")) return;

                                    sp.edit().putString("cart_json", json).apply();
                                }
                            }).show();
                    return true;
                }
            });

            findPreference("tax_value").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (newValue.toString().length() > 0 && isIntegerOrDouble(newValue.toString()))
                        return true;
                    else {
                        ToastHelper.createShortToast(getActivity().getApplicationContext(), "A value is required");
                        return false;
                    }
                }
            });

            findPreference("remove_db").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ListDB db = new ListDB(getActivity());
                    db.dropEverythingAndRebuild();
                    Snackbar.make(getActivity().findViewById(android.R.id.content), "Cleared Database of its data", Snackbar.LENGTH_SHORT).show();
                    return true;
                }
            });

            findPreference("storageLocation").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ToastHelper.createShortToast(getActivity().getApplicationContext(), "Exit and enter the application after changing this option to reflect your new changes");
                    return true;
                }
            });

            if (sp.contains("enable_testing_views") && sp.getBoolean("enable_testing_views", false)){
                addPreferencesFromResource(R.xml.pref_testing);
                refreshTestingPreference();
            }
        }

        @SuppressWarnings("ResultOfMethodCallIgnored")
        public boolean isIntegerOrDouble(String check){
            try {
                Double.parseDouble(check);
            } catch (NumberFormatException e){
                return false;
            } catch (NullPointerException e){
                return false;
            }
            return true;
        }

        private void refreshTestingPreference(){
            if (findPreference("force_crash") != null){
                findPreference("force_crash").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        throw new RuntimeException("Test Crashing deliberately");
                    }
                });
            }

            if (findPreference("debug_locate") != null){
                findPreference("debug_locate").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        if (Build.VERSION.SDK_INT < 19){
                            //Cannot run
                            ToastHelper.createShortToast(getActivity().getApplicationContext(), "Android Version incompatible with test");
                            return true;
                        }
                        String magic = StaticMethods.debugStorageLocation(getActivity().getApplicationContext());
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Storage Locations: ")
                                .setMessage(Html.fromHtml(magic))
                                .setPositiveButton(android.R.string.ok, null).show();
                        return true;
                    }
                });
            }
        }

        private int clickTimes = 0;
        private Toast toasty;

        private void enable_developer_testing_mode(Preference preference){
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    boolean isDeveloper = false;
                    if (sp.contains("enable_testing_views")) isDeveloper = sp.getBoolean("enable_testing_views", false);

                    if (isDeveloper){
                        Snackbar.make(getActivity().findViewById(android.R.id.content), "You are already a developer", Snackbar.LENGTH_SHORT)
                                .setAction("REVOKE", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        sp.edit().putBoolean("enable_testing_views", false).apply();
                                        Snackbar.make(getActivity().findViewById(android.R.id.content), "You are no longer a developer, reboot to update", Snackbar.LENGTH_SHORT).show();
                                    }
                                }).show();
                        return true;
                    }

                    if (clickTimes >= 10){
                        //Enable developer mode
                        sp.edit().putBoolean("enable_testing_views", true).apply();
                        addPreferencesFromResource(R.xml.pref_testing);
                        refreshTestingPreference();
                        Snackbar.make(getActivity().findViewById(android.R.id.content), "You are now a developer!", Snackbar.LENGTH_SHORT).show();
                        return true;
                    }

                    switch (clickTimes){
                        case 5: prompt(5); break;
                        case 6: prompt(4); break;
                        case 7: prompt(3); break;
                        case 8: prompt(2); break;
                        case 9: prompt(1); break;
                    }

                    clickTimes++;

                    return true;
                }
            });
        }

        private void prompt(int left){
            if (toasty != null){
                toasty.cancel();
            }
            if (left > 1)
                toasty = Toast.makeText(getActivity(), left + " more clicks to be a developer!", Toast.LENGTH_SHORT);
            else
                toasty = Toast.makeText(getActivity(), left + " more click to be a developer!", Toast.LENGTH_SHORT);
            toasty.show();
        }

    }



}
