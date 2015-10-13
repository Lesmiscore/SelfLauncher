package com.nao20010128nao.Self.Launcher;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.util.*;
import android.content.pm.*;
import android.content.*;
import com.nao20010128nao.SpoofBrowser.classes.*;
import android.util.*;
import android.net.*;

public class MainActivity extends Activity implements ExpandableListView.OnChildClickListener,View.OnClickListener{
	String action=Intent.ACTION_MAIN,category=Intent.CATEGORY_LAUNCHER,data=null;
	final int editorResult=20;
	/** Called when the activity is first created. */
	ExpandableListView elv;
	TextView title;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main3);
		elv=(ExpandableListView)findViewById(R.id.lnList);
		updateList();
		elv.setOnChildClickListener(this);
		title=(TextView)findViewById(R.id.title);
		title.setOnClickListener(this);
		try{
			getActionBar().hide();
		}catch(Throwable ex){
			ex.printStackTrace();
		}
    }
	public List<ResolveInfo> getLauncheableActivityList(PackageManager packageManager,Intent intent) {
		if(intent==null){
			intent = new Intent(action, null);
			intent.addCategory(category);
			if(data!=null)intent.setData(Uri.parse(data));
		}
		List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(intent, 0);
		Log.d("DEBUG","resolveInfoList:"+resolveInfoList.size());
		return resolveInfoList;
	}
	public List<ResolveInfo> sort(List<ResolveInfo> resolveInfo,PackageManager pm){
		Collections.sort(resolveInfo, new ResolveInfo.DisplayNameComparator(pm));
		return resolveInfo;
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View view,
								int groupPosition, int childPosition, long id) {
		Log.d("DEBUG","groupPosition:"+groupPosition);
		Log.d("DEBUG","childPosition:"+childPosition);
		ExpandableListAdapter adapter = parent.getExpandableListAdapter();
		Map<String, String> item = (Map<String, String>) adapter.getChild(groupPosition, childPosition);
		if(childPosition!=0)return false;
		else{
			Map<String, String> pn = (Map<String, String>)adapter.getChild(groupPosition,1);
			Map<String, String> cn = (Map<String, String>)adapter.getChild(groupPosition,2);
			String p=pn.get("CHILD_TITLE");
			String c=cn.get("CHILD_TITLE");
			try{
				Intent i=new Intent(action);
				i.setClassName(p,c);
				i.addCategory(category);
				if(data!=null)i.setData(Uri.parse(data));
				i.setFlags(i.getFlags()|i.FLAG_ACTIVITY_BROUGHT_TO_FRONT|i.FLAG_ACTIVITY_NEW_TASK|i.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				return true;
			}catch(Throwable ex){ex.printStackTrace();return false;}
		}
    }
	public void updateList(){
		//elv.setAdapter((ExpandableListAdapter)null);
		List<Map<String, String>> groupList = new ArrayList<Map<String,String>>();
        List<List<Map<String, String>>> childList = new ArrayList<List<Map<String,String>>>();
		for(ResolveInfo i:sort(getLauncheableActivityList(getPackageManager(),null),getPackageManager())){
			Map<String, String> groupElement = new HashMap<String, String>();
			groupElement.put("GROUP_TITLE", ""+i.loadLabel(getPackageManager()));
			groupList.add(groupElement);
			List<Map<String, String>> childElements = new ArrayList<Map<String, String>>();
			for(String j:new String[]{getResources().getString(R.string.openThis),
										i.activityInfo.packageName,
										i.activityInfo.name}){
				Map<String, String> child = new HashMap<String, String>();
				child.put("CHILD_TITLE", j);
				child.put("SUMMARY", "");
				childElements.add(child);
				child = new HashMap<String, String>();
			}
			childList.add(childElements);
		}
		SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(
			this.getApplicationContext(),
			groupList,
			R.layout.simple_expandable_list_item_1_custom,
			new String []{"GROUP_TITLE"},
			new int []{android.R.id.text1},
			childList,
			R.layout.simple_expandable_list_item_2_custom,
			new String []{"CHILD_TITLE", "SUMMARY"},
			new int []{android.R.id.text1, android.R.id.text2}
		);
		elv.setAdapter(adapter);
	}
	public void openEditor(){
		Intent editor=new Intent();
		editor.putExtra("ACTION",action);
		editor.putExtra("CATEGORY",category);
		editor.putExtra("DATA",data);
		editor.setClass(this,IntentEditActivity.class);
		startActivityForResult(editor,editorResult);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent activityResult){
		// TODO: Implement this method
		super.onActivityResult(requestCode, resultCode, activityResult);
		if(resultCode!=RESULT_OK)return;
		switch(requestCode){
			case editorResult:
				action=activityResult.getStringExtra("ACTION");
				if(action.length()==0)action=null;//action=Intent.ACTION_MAIN;
				category=activityResult.getStringExtra("CATEGORY");
				if(category.length()==0)category=Intent.CATEGORY_LAUNCHER;
				data=activityResult.getStringExtra("DATA");
				if(data.length()==0)data=null;
				Log.d("DEBUG","action:"+action+"/category:"+category+"/data:"+data);
				Log.d("DEBUG","action:"+(action==null)+"/category:"+(category==null)+"/data:"+(data==null));
				//Log.d("DEBUG","action:"+action.length()+"/category:"+category.length()+"/data:"+data.length());
				updateList();
				break;
		}
	}
	@Override
	public void onClick(View v){
		if(v==title){
			openEditor();
		}
	}
}
