package com.nao20010128nao.Self.Launcher;
import android.app.*;
import android.os.*;
import uk.co.ashtonbrsc.intentexplode.widget.*;
import android.content.*;
import android.view.*;
import android.widget.*;
import android.text.*;

public class IntentEditActivity extends Activity implements View.OnClickListener{
	
	BlockEnterEditText action,category,data;
	Button reset,search;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intenteditor);
		action=(BlockEnterEditText)findViewById(R.id.action);
		data=(BlockEnterEditText)findViewById(R.id.data);
		category=(BlockEnterEditText)findViewById(R.id.category);
		resetView();
		reset=(Button)findViewById(R.id.reset_intent_button);
		search=(Button)findViewById(R.id.resend_intent_button);
		reset.setOnClickListener(this);
		search.setOnClickListener(this);
		data.setInputType(data.getInputType()|InputType.TYPE_TEXT_VARIATION_URI);
	}
	public void resetView(){
		Intent intent=getIntent();
		String a=intent.getStringExtra("ACTION");
		if(a!=null)action.setText(a);else action.setText("");
		a=intent.getStringExtra("DATA");
		if(a!=null)data.setText(a);else data.setText("");
		a=intent.getStringExtra("CATEGORY");
		if(a!=null)category.setText(a);else category.setText("");
	}

	@Override
	public void onClick(View p1)
	{
		// TODO: Implement this method
		if(p1==reset){
			resetView();
		}else if(p1==search){
			Intent ret=new Intent();
			String a=action.getText().toString();
			if(a=="")a=null;
			ret.putExtra("ACTION",a);
			a=data.getText().toString();
			if(a=="")a=null;
			ret.putExtra("DATA",a);
			a=category.getText().toString();
			if(a=="")a=null;
			ret.putExtra("CATEGORY",a);
			setResult(RESULT_OK,ret);
			finish();
			return;
		}
	}
}
