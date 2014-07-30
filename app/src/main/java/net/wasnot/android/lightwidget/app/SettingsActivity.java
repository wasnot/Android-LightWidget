package net.wasnot.android.lightwidget.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.PreferenceScreen;
import android.support.v4.fragment.OnPreferenceAttachedListener;
import android.support.v4.fragment.PreferenceListFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;

public class SettingsActivity extends ActionBarActivity implements OnPreferenceAttachedListener {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new MyPreferenceFragment()).commit();
//        PreferenceUtil.setDefaultPreference(getApplicationContext());
//        GcmRegister.registerAuto(this, true, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // AudioManager audioManager = (AudioManager)
        // getSystemService(Context.AUDIO_SERVICE);
        // audioManager.unregisterMediaButtonEventReceiver()
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                finish();
//                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } else {
                getSupportFragmentManager().popBackStack();
            }
            return true;
        }
        return super.onKeyUp(keyCode, keyEvent);
    }

    @Override
    public void onPreferenceAttached(PreferenceScreen root, int xmlId) {
    }

    public static class MyPreferenceFragment extends PreferenceListFragment implements
            SharedPreferences.OnSharedPreferenceChangeListener {

        EditTextPreference passcode;

        EditTextPreference email;
        CheckBoxPreference checkPass;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
//            PreferenceScreen preference = (PreferenceScreen) getPreferenceScreen().findPreference(
//                    PreferenceUtil.ENABLE_AUTO_GPS);
//            Preference map = getPreferenceScreen().findPreference(PreferenceUtil.SEE_GPS_LOG);
//            if (map != null) {
//                map.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//                    @Override
//                    public boolean onPreferenceClick(Preference preference) {
//                        if (getActivity() == null || !isAdded()) {
//                            return false;
//                        }
//
//                        //Google Play Servicesが使えるかどうかのステータス
//                        int status = GooglePlayServicesUtil
//                                .isGooglePlayServicesAvailable(getActivity());
//                        if (status != ConnectionResult.SUCCESS) {
//                            // Google Play Services が使えない場合
//                            Toast.makeText(getActivity(), "Google Mapが使えません", Toast.LENGTH_LONG)
//                                    .show();
//                        } else {
//                            // Google Play Services が使える場合
//                            getActivity().getSupportFragmentManager().beginTransaction()
//                                    .replace(android.R.id.content, new MyMapFragment())
//                                    .addToBackStack(null).commit();
//                        }
//                        return true;
//                    }
//                });
//            }
//            Preference test = getPreferenceScreen()
//                    .findPreference(getString(R.string.settings_ignore_app_key));
//            if (test != null) {
//                test.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//                    @Override
//                    public boolean onPreferenceClick(Preference preference) {
//                        if (getActivity() == null || !isAdded()) {
//                            return false;
//                        }
//                        if (!PreferenceManager.getDefaultSharedPreferences(getActivity())
//                                .getBoolean(PreferenceUtil.ENABLE_OBSERVE_APP, false)) {
//                            startActivity(new Intent(getActivity(), MainActivity.class));
//                        } else {
//                            startActivity(new Intent(getActivity(), IgnoreAppActivity.class));
//                        }
//                        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                        return true;
//                    }
//                });
//            }
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            ((ActionBarActivity) getActivity()).getSupportActionBar()
                    .setDisplayHomeAsUpEnabled(false);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            // LogUtil.d(TAG, "onSharedPreferenceChanged:" + key);
            Context con = getActivity();
            if (con == null || key == null) {
                return;
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(
                    this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

    }

}