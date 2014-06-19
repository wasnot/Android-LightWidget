package net.wasnot.android.lightwidget.app;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v4.fragment.OnPreferenceAttachedListener;
import android.support.v4.fragment.PreferenceListFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.widget.Toast;

import jp.freebit.family.appcheck.IgnoreAppActivity;
import jp.freebit.family.gcm.GcmRegister;
import jp.freebit.family.lockalarm.LockAlarmFragment;
import jp.freebit.family.utils.AccessibilityUtil;
import jp.freebit.family.utils.DigestUtil;
import jp.freebit.family.utils.Globals;
import jp.freebit.family.utils.HomeUtil;

public class SettingsActivity extends ActionBarActivity implements OnPreferenceAttachedListener {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new MyPreferenceFragment()).commit();
        PreferenceUtil.setDefaultPreference(getApplicationContext());
        GcmRegister.registerAuto(this, true, null);
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
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
            Preference map = getPreferenceScreen().findPreference(PreferenceUtil.SEE_GPS_LOG);
            if (map != null) {
                map.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        if (getActivity() == null || !isAdded()) {
                            return false;
                        }

                        //Google Play Servicesが使えるかどうかのステータス
                        int status = GooglePlayServicesUtil
                                .isGooglePlayServicesAvailable(getActivity());
                        if (status != ConnectionResult.SUCCESS) {
                            // Google Play Services が使えない場合
                            Toast.makeText(getActivity(), "Google Mapが使えません", Toast.LENGTH_LONG)
                                    .show();
                        } else {
                            // Google Play Services が使える場合
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(android.R.id.content, new MyMapFragment())
                                    .addToBackStack(null).commit();
                        }
                        return true;
                    }
                });
            }
            Preference test = getPreferenceScreen()
                    .findPreference(getString(R.string.settings_ignore_app_key));
            if (test != null) {
                test.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        if (getActivity() == null || !isAdded()) {
                            return false;
                        }
                        if (!PreferenceManager.getDefaultSharedPreferences(getActivity())
                                .getBoolean(PreferenceUtil.ENABLE_OBSERVE_APP, false)) {
                            startActivity(new Intent(getActivity(), MainActivity.class));
                        } else {
                            startActivity(new Intent(getActivity(), IgnoreAppActivity.class));
                        }
                        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        return true;
                    }
                });
            }
            Preference test2 = getPreferenceScreen()
                    .findPreference(getString(R.string.settings_lock_alarm_key));
            if (test2 != null) {
                test2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        if (getActivity() == null || !isAdded()) {
                            return false;
                        }
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(android.R.id.content, new LockAlarmFragment())
                                .addToBackStack(null).commit();
                        return true;
                    }
                });
            }
            passcode = (EditTextPreference) getPreferenceScreen()
                    .findPreference(PreferenceUtil.PASSCODE);
            if (passcode != null) {
                String pass = getPreferenceScreen().getSharedPreferences().getString(
                        PreferenceUtil.PASSCODE, "");
                passcode.setSummary(pass.length() == 0 ? "未設定" : pass
                        .replaceAll(".", "*"));
            }
            email = (EditTextPreference) getPreferenceScreen()
                    .findPreference(PreferenceUtil.NOTIFY_EMAIL);
            if (email != null) {
                String addr = getPreferenceScreen().getSharedPreferences().getString(
                        PreferenceUtil.NOTIFY_EMAIL, "");
                email.setSummary(addr.length() == 0 ? "未設定" : addr);
            }

            checkPass = (CheckBoxPreference) getPreferenceScreen()
                    .findPreference(PreferenceUtil.CHECK_PASS_ADD_CHAREGE);
            if (checkPass != null) {
//                boolean isCheckPass = MyplanPrefsUtil.getCheckPassAddCharge(getActivity());
//                checkPass.setChecked(isCheckPass);
//                getPreferenceManager().getSharedPreferences().edit()
//                        .putBoolean(PreferenceUtil.CHECK_PASS_ADD_CHAREGE, isCheckPass).commit();
            }
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
            if (key.equals(PreferenceUtil.ENABLE_AUTO_GPS)) {
//                try {
//                    getPendingIntent(con).send();
//                } catch (PendingIntent.CanceledException e) {
//                    e.printStackTrace();
//                }
                if (sharedPreferences.getBoolean(key, false)) {
                    GpsChangedReceiver.setGpsOn(con);
                }
            } else if (key.equals(PreferenceUtil.ENABLE_AUTO_HOME)) {
                HomeUtil.clearDefault(con);
                HomeUtil.setPandaDefaultLauncher(con);
                AccessibilityUtil.setPandaHomeAccessibility(con);
            } else if (key.equals(PreferenceUtil.ENABLE_GPS_LOG)) {
                PreferenceUtil.checkLocationPreference(con, sharedPreferences);
            } else if (key.equals(PreferenceUtil.CHECK_TEXT_WHILE_WALK)) {
                PreferenceUtil.checkLocationPreference(con, sharedPreferences);
            } else if (key.equals(PreferenceUtil.NOTIFY_EMAIL)) {
                if (email != null) {
                    String addr = sharedPreferences.getString(key, "");
                    email.setSummary(addr.length() == 0 ? "未設定" : addr);
                }
            } else if (key.equals(PreferenceUtil.PASSCODE)) {
                if (passcode != null) {
                    String pass = sharedPreferences.getString(key, "");
                    passcode.setSummary(pass.length() == 0 ? "未設定" : pass
                            .replaceAll(".", "*"));
                }
            } else if (key.equals(PreferenceUtil.ENABLE_OBSERVE_APP)) {
                PreferenceUtil.checkAppObservePreference(con, sharedPreferences);
            } else if (key.equals(PreferenceUtil.ENABLE_LIMIT_GOOGLEPLAY)) {
                PreferenceUtil.checkAppObservePreference(con, sharedPreferences);
            } else if (key.equals(PreferenceUtil.ENABLE_LIMIT_SETTINGS)) {
                PreferenceUtil.checkAppObservePreference(con, sharedPreferences);
            } else if (key.equals(PreferenceUtil.ENABLE_BATTERY_LOW_NOTIFY)) {
                PreferenceUtil.checkBatteryPreference(con, sharedPreferences);
            } else if (key.equals(PreferenceUtil.CHECK_PASS_ADD_CHAREGE)) {
                String pass = sharedPreferences.getString(PreferenceUtil.PASSCODE, null);
                if (!Globals.isDigestPass) {
                    pass = DigestUtil.build(pass);
                }
                if (pass != null && pass.length() > 0) {
                    MyPlanReceiver.sendChangeBroadcast(con,
                            sharedPreferences
                                    .getBoolean(PreferenceUtil.CHECK_PASS_ADD_CHAREGE, false),
                            pass
                    );
                }
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(
                    this);
            MyPlanReceiver.sendRequestBroadcast(getActivity());
            if (checkPass != null) {
                boolean isCheck = getPreferenceScreen().getSharedPreferences()
                        .getBoolean(PreferenceUtil.CHECK_PASS_ADD_CHAREGE, false);
                checkPass.setChecked(isCheck);
            }
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

    }

}