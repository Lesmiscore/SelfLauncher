package com.nao20010128nao.Self.Launcher;

import android.content.*;
import android.content.pm.*;
import android.net.*;
import android.os.*;
import android.support.v4.view.*;
import android.support.v7.app.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.util.*;
import android.text.*;
import android.support.design.widget.*;

public class MainActivity extends AppCompatActivity implements ExpandableListView.OnChildClickListener {
	String action=Intent.ACTION_MAIN,category=Intent.CATEGORY_LAUNCHER,data=null;
	final int editorResult=20;
	ExpandableListView elv;
	FrameLayout loading;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main3);
		elv = (ExpandableListView)findViewById(R.id.lnList);
		loading=(FrameLayout)findViewById(R.id.loading);
		updateList();
		elv.setOnChildClickListener(this);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item=menu.add(Menu.NONE,0,0,R.string.editFilter);
		MenuItemCompat.setShowAsAction(item,MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case 0:
				openEditor();
				return true;
		}
		return false;
	}

	public List<ResolveInfo> getLauncheableActivityList(PackageManager packageManager, Intent intent) {
		if (intent == null) {
			intent = new Intent(action, null);
			intent.addCategory(category);
			if (data != null)intent.setData(Uri.parse(data));
		}
		List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(intent, 0);
		Log.d("DEBUG", "resolveInfoList:" + resolveInfoList.size());
		return resolveInfoList;
	}
	public List<ResolveInfo> sort(List<ResolveInfo> resolveInfo, PackageManager pm) {
		Collections.sort(resolveInfo, new ResolveInfo.DisplayNameComparator(pm));
		return resolveInfo;
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View view,
								int groupPosition, int childPosition, long id) {
		Log.d("DEBUG", "groupPosition:" + groupPosition);
		Log.d("DEBUG", "childPosition:" + childPosition);
		ExpandableListAdapter adapter = parent.getExpandableListAdapter();
		switch(childPosition){
			case 0:
				Map<String, String> itemData = (Map<String, String>)adapter.getChild(groupPosition, 0);
				String packageName=itemData.get("pkgName");
				String activityClass=itemData.get("actClas");
				try {
					Intent i=new Intent(action);
					i.setClassName(packageName, activityClass);
					i.addCategory(category);
					if (data != null)i.setData(Uri.parse(data));
					i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
					return true;
				} catch (Throwable ex) {
					ex.printStackTrace();
					Snackbar.make(findViewById(android.R.id.content),R.string.startFailed,Snackbar.LENGTH_SHORT).show();
					return false;
				}
			default:
				return false;
		}
    }
	public void updateList() {
		elv.setVisibility(View.GONE);
		loading.setVisibility(View.VISIBLE);
		new AsyncTask<Void,Void,Map.Entry<List<Map<String, String>>,List<List<Map<String, String>>>>>(){
			public Map.Entry<List<Map<String, String>>,List<List<Map<String, String>>>> doInBackground(Void... a){
				List<Map<String, String>> groupList = new ArrayList<Map<String,String>>();
				List<List<Map<String, String>>> childList = new ArrayList<List<Map<String,String>>>();
				for (ResolveInfo i:sort(getLauncheableActivityList(getPackageManager(), null), getPackageManager())) {
					Map<String, String> groupElement = new HashMap<String, String>();
					groupElement.put("GROUP_TITLE", "" + i.loadLabel(getPackageManager()));
					groupList.add(groupElement);
					List<Map<String, String>> childElements = new ArrayList<Map<String, String>>();
					for (String j:new String[]{getResources().getString(R.string.openThis),
						i.activityInfo.packageName,
						i.activityInfo.name}) {
						Map<String, String> child = new HashMap<String, String>();
						child.put("CHILD_TITLE", j);
						childElements.add(child);
					}
					childElements.get(0).put("pkgName",i.activityInfo.packageName);
					childElements.get(0).put("actClas",i.activityInfo.name);
					childList.add(childElements);
				}
				Map<List<Map<String, String>>,List<List<Map<String, String>>>> resultGen=new HashMap<>();
				resultGen.put(groupList,childList);
				return new ArrayList<Map.Entry<List<Map<String, String>>,List<List<Map<String, String>>>>>(resultGen.entrySet()).get(0);
			}
			public void onPostExecute(Map.Entry<List<Map<String, String>>,List<List<Map<String, String>>>> d){
				List<Map<String, String>> groupList = d.getKey();
				List<List<Map<String, String>>> childList = d.getValue();
				SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(
					MainActivity.this,
					groupList,
					R.layout.simple_expandable_list_item_1_custom,
					new String []{"GROUP_TITLE"},
					new int []{android.R.id.text1},
					childList,
					R.layout.simple_expandable_list_item_2_custom,
					new String []{"CHILD_TITLE"},
					new int []{android.R.id.text1}
				);
				elv.setAdapter(adapter);
				loading.setVisibility(View.GONE);
				elv.setVisibility(View.VISIBLE);
			}
		}.execute();
	}
	public void openEditor() {
		Intent editor=new Intent();
		editor.putExtra("ACTION", action);
		editor.putExtra("CATEGORY", category);
		editor.putExtra("DATA", data);
		editor.setClass(this, IntentEditActivity.class);
		startActivityForResult(editor, editorResult);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent activityResult) {
		// TODO: Implement this method
		super.onActivityResult(requestCode, resultCode, activityResult);
		if (resultCode != RESULT_OK)return;
		switch (requestCode) {
			case editorResult:
				action = activityResult.getStringExtra("ACTION");
				if (TextUtils.isEmpty(action))action = null;//action=Intent.ACTION_MAIN;
				category = activityResult.getStringExtra("CATEGORY");
				if (TextUtils.isEmpty(category))category = Intent.CATEGORY_LAUNCHER;
				data = activityResult.getStringExtra("DATA");
				if (TextUtils.isEmpty(data))data = null;
				Log.d("DEBUG", "action:" + action + "/category:" + category + "/data:" + data);
				Log.d("DEBUG", "action:" + (action == null) + "/category:" + (category == null) + "/data:" + (data == null));
				//Log.d("DEBUG","action:"+action.length()+"/category:"+category.length()+"/data:"+data.length());
				updateList();
				break;
		}
	}
}
