package com.nao20010128nao.Self.Launcher;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;

import uk.co.ashtonbrsc.intentexplode.widget.BlockEnterEditText;
import android.text.*;

public class IntentEditActivity extends AppCompatActivity implements View.OnClickListener{
	
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
		if(!TextUtils.isEmpty(a))action.setText(a);else action.setText("");
		a=intent.getStringExtra("DATA");
		if(!TextUtils.isEmpty(a))data.setText(a);else data.setText("");
		a=intent.getStringExtra("CATEGORY");
		if(!TextUtils.isEmpty(a))category.setText(a);else category.setText("");
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
			if(TextUtils.isEmpty(a))a=null;
			ret.putExtra("ACTION",a);
			a=data.getText().toString();
			if(TextUtils.isEmpty(a))a=null;
			ret.putExtra("DATA",a);
			a=category.getText().toString();
			if(TextUtils.isEmpty(a))a=null;
			ret.putExtra("CATEGORY",a);
			setResult(RESULT_OK,ret);
			finish();
			return;
		}
	}
}
