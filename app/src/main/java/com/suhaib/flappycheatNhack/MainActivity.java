package com.suhaib.flappycheatNhack;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.suhaib.flappycheatNhack.R;

public class MainActivity extends ActionBarActivity {
	private NodeList nodelist;
	private TextView textScore;
	private EditText editScore;
	private Element element;
	private String filepath1;
	private String filepath2;
	private File file;
	private Document doc;
	private Transformer transformer;
	private AdView adView;
	private String MY_AD_UNIT_ID;

	private DialogFragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_in, 0);
		setContentView(R.layout.activity_main);
		textScore = (TextView) findViewById(R.id.textScore);
		editScore = (EditText) findViewById(R.id.editScore);
		Typeface font = Typeface.createFromAsset(getAssets(),
				"fonts/GildaDisplay-Regular.ttf");
		textScore.setTypeface(font);
		File dir = new File(Environment.getExternalStorageDirectory(),
				"/FlappyBirdCheats/");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		filepath1 = "/data/data/com.dotgears.flappybird/shared_prefs/FlappyBird.xml";
		filepath2 = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/FlappyBirdCheats/FlappyBird.xml";

		// Create the adView.
		MY_AD_UNIT_ID = "a153062bad3fead";
		adView = new AdView(this);
		adView.setAdUnitId(MY_AD_UNIT_ID);
		adView.setAdSize(AdSize.BANNER);

		LinearLayout layout = (LinearLayout) findViewById(R.id.adLayout);

		layout.addView(adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);

		new MyAsyncTask().execute();
		if (!new File(filepath1).exists()) {
			fragment = CustomDialogFragment.newInstance(getResources()
					.getString(R.string.install_message), true);
			fragment.setCancelable(false);
			fragment.show(getSupportFragmentManager(), "dialog");
		} else {
			copyToSd(filepath1, filepath2);
			showScore();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		MenuItem shareItem = menu.findItem(R.id.menu_share);
		ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat
				.getActionProvider(shareItem);
		mShareActionProvider.setShareIntent(createShareIntent());

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_about) {
			fragment = CustomDialogFragment.newInstance(getResources()
					.getString(R.string.about_message), false);
			fragment.show(getSupportFragmentManager(), "dialog");
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(0, R.anim.slide_out);
	}

	public Intent createShareIntent() {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Flappy Bird Cheats");
		shareIntent
				.putExtra(Intent.EXTRA_TEXT,
						"https://play.google.com/store/apps/details?id=com.suhaib.flappybirdcheats");
		return shareIntent;
	}

	public void setScore(View v) {
		if (editScore.getText().toString().length() != 0
				&& Integer.parseInt(editScore.getText().toString()) < 1000000000) {
			changeScore(editScore.getText().toString());
		} else if (editScore.getText().toString().length() == 0) {
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			editScore.startAnimation(shake);
		} else {
			showToast(this,
					"Whoa, it'll take 32 years to get that score legitimately. Keep it down.");
		}

	}

	public void setRandScore(View v) {
		int rand = new Random().nextInt(1000);
		changeScore(Integer.toString(rand));
	}

	public void changeScore(String score) {

		element.setAttribute("value", score);
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		// initialize StreamResult with File object to save to file
		StreamResult result = new StreamResult(file);
		DOMSource source = new DOMSource(doc);
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		copyToSystem(filepath2, filepath1);
		showScore();
		ActivityManager am = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
		am.killBackgroundProcesses("com.dotgears.flappybird");
		showToast(this, "Success. Check the game for new High Score.");

	}

	public void copyToSd(String path1, String path2) {
		Process rootProcess;
		try {
			rootProcess = Runtime.getRuntime().exec("su");
			if (rootProcess != null) {
				DataOutputStream dos = new DataOutputStream(
						rootProcess.getOutputStream());
				dos.writeBytes("mount -o rw,remount -t yaffs2 /dev/block/mtdblock4 /data\n");
				dos.writeBytes("cp -f -R " + path1 + " " + path2 + "\n");
				dos.writeBytes("exit\n");
				dos.flush();
				rootProcess.waitFor();
			} else {
				showToast(this, "Cant get root access");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void copyToSystem(String path1, String path2) {
		Process rootProcess;
		try {
			rootProcess = Runtime.getRuntime().exec("su");

			DataOutputStream dos = new DataOutputStream(
					rootProcess.getOutputStream());
			dos.writeBytes("mount -o rw,remount -t yaffs2 /dev/block/mtdblock4 /data\n");
			// dos.writeBytes("mv -f " + path2 + " " + path2 + ".bak");
			dos.writeBytes("cp -f " + path1 + " " + path2 + "\n");
			dos.writeBytes("chmod 755 " + path2 + "\n");
			dos.writeBytes("exit\n");
			dos.flush();
			rootProcess.waitFor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void showScore() {
		file = new File(Environment.getExternalStorageDirectory(),
				"/FlappyBirdCheats/FlappyBird.xml");
		parseXmlFile(file);
		for (int i = 0; i < nodelist.getLength(); i++) {
			if (nodelist.item(i) instanceof Element) {
				element = (Element) nodelist.item(i);
			}

		}

		textScore.setText(element.getAttribute("value"));
	}

	public void parseXmlFile(File file) {
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(file);
			nodelist = doc.getDocumentElement().getChildNodes();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void showToast(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	public class MyAsyncTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {

			return new Root().isDeviceRooted();

		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			if (!result) {
				fragment = CustomDialogFragment.newInstance(getResources()
						.getString(R.string.root_message), true);
				fragment.setCancelable(false);
				fragment.show(getSupportFragmentManager(), "dialog");
			}

		}

	}
}
