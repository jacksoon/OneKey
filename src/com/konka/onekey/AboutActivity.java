package com.konka.onekey;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * ���� �˵� Demo
 * 2013.1.18
 * @author zhangshaopeng
 */

public class AboutActivity extends Activity{
    /** Called when the activity is first created. */
	
	private String TAG="AboutActivity";
	
	/*���ص���һ�����*/
	private ImageView about_return;
	/*��ʾ����汾��*/
	private TextView show_sv;
	//Muse�ٷ���̳
	private ImageView MS_bbs;
	//����΢����ַ
	private ImageView XL_wb;
	//��Ѷ΢����ַ
	private ImageView QQ_wb;
	//��������
	private TextView about_text;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.about_activity_main);
        
        Log.i(TAG,"entry AboutActivity");
        
        
       about_init(); 
       aboutreturn();
       show_software_version();
       link_musebbs();
       link_xlwb();
       link_qqwb();
       
       Link_abtext();
       
    }

	private void about_init() {
	
		about_return=(ImageView)findViewById(R.id.ab_return);
        show_sv=(TextView)findViewById(R.id.ab_st_version);
		MS_bbs=(ImageView)findViewById(R.id.museui_bbs);
		XL_wb=(ImageView)findViewById(R.id.xinlang_wb);
		QQ_wb=(ImageView)findViewById(R.id.qq_wb);
		
		about_text=(TextView)findViewById(R.id.myAboutTextView);
		
	}
	//-----------------------------
	/*���ص���һ�����*/
	private void aboutreturn() {
		
		about_return.setOnClickListener(return_click);
		             
	}
	private OnClickListener return_click=new Button.OnClickListener(){

		@Override
		public void onClick(View v) {
			
//			Intent intent=new Intent();
//			intent.setClass(AboutActivity.this, AboutDemoActivity.class);
//			startActivity(intent);
			AboutActivity.this.finish();
		}
	};

	
	//-----------------------------
	//���ڷ��ص�ͼƬ̫С�����趨�������ڡ�ʱҲ�Ƿ��ع���
	private void Link_abtext(){
		about_text.setOnClickListener(returntext_click);
		
	}
	private OnClickListener returntext_click=new Button.OnClickListener(){

		@Override
		public void onClick(View v) {
			
//			Intent intent=new Intent();
//			intent.setClass(AboutActivity.this, AboutDemoActivity.class);
//			startActivity(intent);
			AboutActivity.this.finish();
		}
		
	};
	
   //------------------------------
	/*��ʾ����汾��*/

	private void show_software_version() {

		show_sv.setText(getVersionName());
	}
	
	   //	��ȡ��ǰӦ�õİ汾�ţ�

	private String getVersionName()
	{
	// ��ȡpackagemanager��ʵ��
	    PackageManager packageManager = getPackageManager();
	// getPackageName()���㵱ǰ��İ�����0�����ǻ�ȡ�汾��Ϣ
	    PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(),0);
		} catch (NameNotFoundException e) {

			e.printStackTrace();
		}
	    String version = packInfo.versionName;
	    if(version == null)
	    {
	    	version = "";
	    }
	
   	Log.i(TAG,"software version="+version);
	
     	return version;
	}  	
    //-------------------------------
    //MuseUI�ٷ���̳��ַ
	private void link_musebbs() {
		
		MS_bbs.setOnClickListener(ms_click);
		
	}
	private OnClickListener ms_click=new Button.OnClickListener(){
		@Override
		public void onClick(View v) {
			
			Uri uri = Uri.parse("http://www.museui.com");  
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		}
	};
	
	//------------------------------------
	//����΢��MuseUI�ٷ���ַ
	private void link_xlwb() {
		
		XL_wb.setOnClickListener(xl_click);
		
	}
	private OnClickListener xl_click=new Button.OnClickListener(){
		@Override
		public void onClick(View v) {
			
			Uri uri = Uri.parse("http://e.weibo.com/u/2916538744");  
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		}
	};
	
	//------------------------------------
   // ��Ѷ΢��MuseUI�ٷ���ַ

	private void link_qqwb() {
		
		QQ_wb.setOnClickListener(qq_lick);
	}
	private OnClickListener qq_lick=new Button.OnClickListener(){
		@Override
		public void onClick(View v) {
			
			Uri uri = Uri.parse("http://e.t.qq.com/heidi_KONKA");  
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		}
	};
	

}
