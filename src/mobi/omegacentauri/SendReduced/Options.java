package mobi.omegacentauri.SendReduced;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

public class Options extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	public static final String PREF_RESOLUTION = "resolution";
	public static final String PREF_QUALITY = "quality";
	public static final String PREF_NAME = "output";
	public static final String OPT_NAME_DATE_TIME = "date and time";
	public static final String OPT_NAME_RANDOM = "random";
	public static final String OPT_NAME_SEQUENTIAL = "sequential";
	public static final String OPT_NAME_PRESERVE = "preserve";
	public static final String PREF_EXIF_LOCATION = "exifLocation";
	public static final String PREF_EXIF_MAKE_MODEL = "exifMake";
	public static final String PREF_EXIF_DATETIME = "exifDateTime";
	
	public static final String[] proKeys = { PREF_NAME, PREF_EXIF_LOCATION, PREF_EXIF_MAKE_MODEL, PREF_EXIF_DATETIME, "outputPrivacy" };
	
	private static String[] summaryKeys = { PREF_RESOLUTION, PREF_QUALITY, PREF_NAME };
	private static int[] summaryEntryValues = { R.array.resolutions, R.array.qualities, R.array.outputs };
	private static int[] summaryEntries = { R.array.resolutions, R.array.qualities, R.array.outputs }; 
	private static String[] summaryDefaults = { "1024", "85", "random" };

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		addPreferencesFromResource(R.xml.options);
		Utils.cleanCache(this, System.currentTimeMillis());
	}
	@Override
	public void onResume() {
		super.onResume();

		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		customizeDisplay();
	}
	
	@Override
	public void onStop() {
		super.onStop();
	}

	
	@Override
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences options, String key) {
		setSummary(key);
	}

	public static String getString(SharedPreferences options, String key) {
		for (int i=0; i<summaryKeys.length; i++)
			if (summaryKeys[i].equals(key)) 
				return options.getString(key, summaryDefaults[i]);
		
		return options.getString(key, "");
	}
	
	public void customizeDisplay() {
		for (int i=0; i<summaryKeys.length; i++) {
			setSummary(i);
		}

		PreferenceScreen upgrade = (PreferenceScreen) findPreference("upgrade");
		if (SendReduced.pro(this)) {
			getPreferenceScreen().removePreference(upgrade);
			return;
		}
		Intent intent = new Intent(Intent.ACTION_VIEW);
    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	if (MarketDetector.detect(this) == MarketDetector.APPSTORE) {
            // string split up to fool switcher.sh
      		intent.setData(Uri.parse("http://www.amazon.com/gp/mas/dl/android?p=mobi.omegacentauri.Send"+"Reduced_"+"pro"));
      	}
      	else {
            // string split up to fool switcher.sh
      		intent.setData(Uri.parse("market://details?id=mobi.omegacentauri.Send" +"Reduced_"+"pro"));
      	}
    	
		upgrade.setIntent(intent);
		
		for (String p : proKeys) {
			Preference pref = findPreference(p);
			if (pref != null) {
				String title = pref.getTitle().toString();
				if (! title.endsWith("[pro]")) 
					pref.setTitle(title + " [pro]");
			}
		}

		PreferenceScreen ps = getPreferenceScreen();
		int n = ps.getPreferenceCount();
		for (int i = 0 ; i < n ; i++) {
			Preference p = ps.getPreference(i);
			String title = p.getTitle().toString();
			if (title != null && title.contains(" [pro]")) {
				p.setTitle(title.replace(" [pro]", ""));
			}
		}
	}
	
	public void setSummary(String key) {		
		for (int i=0; i<summaryKeys.length; i++) {
			if (summaryKeys[i].equals(key)) {
				setSummary(i);
				return;
			}
		}
	}
	
	public void setSummary(int i) {
		SharedPreferences options = PreferenceManager.getDefaultSharedPreferences(this);
		Resources res = getResources();
		
		Preference pref = findPreference(summaryKeys[i]);
		String value = options.getString(summaryKeys[i], summaryDefaults[i]);
		
		String[] valueArray = res.getStringArray(summaryEntryValues[i]);
		String[] entryArray = res.getStringArray(summaryEntries[i]);
		
		for (int j=0; j<valueArray.length; j++) 
			if (valueArray[j].equals(value)) {
				pref.setSummary(entryArray[j]);
				return;
			}
	}

}

