package org.no_ip.payan.metro3.clases;

import android.app.Activity;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NfcA;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;
import java.util.Arrays;

/* renamed from: org.no_ip.payan.metro3.clases.util */
public class C0372util extends Application {
    private static final String LOG_TAG = C0372util.class.getSimpleName();
    private static Context mAppContext;
    private static SparseArray<byte[][]> mKeyMap = null;
    private static int mKeyMapFrom = -1;
    private static NfcAdapter mNfcAdapter;
    private static float mScale;
    private static Tag mTag = null;
    private static byte[] mUID = null;
    private static String mVersionCode;

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[(len / 2)];
        int i = 0;
        while (i < len) {
            try {
                data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
                i += 2;
            } catch (Exception e) {
                Log.d(LOG_TAG, "Argument(s) for hexStringToByteArray(String s)was not a hex string");
            }
        }
        return data;
    }

    public void onCreate() {
        super.onCreate();
        mAppContext = getApplicationContext();
        mScale = getResources().getDisplayMetrics().density;
        try {
            mVersionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(LOG_TAG, "Version not found.");
        }
    }

    public static SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(mAppContext);
    }

    public static void setNfcAdapter(NfcAdapter nfcAdapter) {
        mNfcAdapter = nfcAdapter;
    }

    public static NfcAdapter getNfcAdapter() {
        return mNfcAdapter;
    }

    public static void disableNfcForegroundDispatch(Activity targetActivity) {
        if (mNfcAdapter != null && mNfcAdapter.isEnabled()) {
            mNfcAdapter.disableForegroundDispatch(targetActivity);
        }
    }

    public static void enableNfcForegroundDispatch(Activity targetActivity) {
        if (mNfcAdapter != null && mNfcAdapter.isEnabled()) {
            PendingIntent pendingIntent = PendingIntent.getActivity(targetActivity, 0, new Intent(targetActivity, targetActivity.getClass()).addFlags(536870912), 0);
            mNfcAdapter.enableForegroundDispatch(targetActivity, pendingIntent, (IntentFilter[]) null, new String[][]{new String[]{NfcA.class.getName()}});
        }
    }

    public static void setTag(Tag tag) {
        mTag = tag;
        mUID = tag.getId();
    }

    public static Tag getTag() {
        return mTag;
    }

    public static String byte2HexString(byte[] bytes) {
        String ret = "";
        if (bytes != null) {
            for (byte valueOf : bytes) {
                Byte b = Byte.valueOf(valueOf);
                ret = ret + String.format("%02X", new Object[]{Integer.valueOf(b.intValue() & 255)});
            }
        }
        return ret;
    }

    public static int checkMifareClassicSupport(Tag tag, Context context) {
        byte sak;
        if (tag == null || context == null) {
            return -3;
        }
        if (Arrays.asList(tag.getTechList()).contains(MifareClassic.class.getName())) {
            return 0;
        }
        NfcA nfca = NfcA.get(tag);
        byte[] atqa = nfca.getAtqa();
        if (atqa[1] == 0 && ((atqa[0] == 4 || atqa[0] == 68 || atqa[0] == 2 || atqa[0] == 66) && ((sak = (byte) nfca.getSak()) == 8 || sak == 9 || sak == 24 || sak == -120 || sak == 40))) {
            return -1;
        }
        return -2;
    }

    public static int treatAsNewTag(Intent intent, Context context) {
        if (!"android.nfc.action.TECH_DISCOVERED".equals(intent.getAction())) {
            return -4;
        }
        Tag tag = MCReader.patchTag((Tag) intent.getParcelableExtra("android.nfc.extra.TAG"));
        setTag(tag);
        Toast.makeText(context, ("(UID: " + byte2HexString(tag.getId())) + ")", 1).show();
        return checkMifareClassicSupport(tag, context);
    }
}
