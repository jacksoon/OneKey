package com.konka.utils;

import java.io.File;
import java.util.ArrayList;

import com.konka.onekey.R;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageParser;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

/**
 * ����������ȡһ��apk�ļ���ͼ�ꡢӦ����Ϣ������Ϣ�ȡ�
 * Ӧ����Ϣ��һЩ��AndroidManifest.xml�����application��ǩ����������Ϣ��
 * ����Ϣ�Ǵ�androidmanifest.xml�����ռ�����Ϣ����Ȼ����Ӧ����Ϣ��
 * @author {wphoenix9}
 *
 */
public class ApkDetails{
	private static final String TAG = "onekey";
	private static final boolean DBG = true;
	private PackageInfo packageInfo;
	private ApplicationInfo appInfo;
	private Context mContext;
	//apk�ļ��ľ���·��
	private String absPath;
	private PackageManager pm;
	private String appName = null;
	private String packageName = null;
	private String version = "1.0";
	private Drawable icon1, icon2;
	private File apks = null;
//	//�ж��Ƿ��Ѿ�ѡ�н��а�װ
//	private boolean isChecked = false;
	private Uri mPackageURI = null;
	private PackageParser.Package mPkgInfo = null;
	private int installFlag = 0;
	//����ȷ����Ӧ���Ƿ���Ҫ��װ
	private boolean ifInstall = false;
	//��װ�Ƿ�ɹ�
	private boolean success = false;
	
	/**
	 * Ϊ����+Ӧ�ð汾�š�ͨ����key����ΪӦ����hashmap�еļ���
	 * ÿ��Ӧ�õİ������ǲ�һ���ġ�����Ӧ�����ƿ���һ����������������ϵķ�ʽ
	 * ��֤key�Ķ�һ�ԡ������keyֵһ������ôֻ����һ���ļ��������ļ������Ƿ���ͬ��
	 */
	private String app_key;	
	
	public ApkDetails(File apks, Context mContent)
	{
		this.apks = apks;
		this.mContext = mContent;
		this.absPath = apks.getAbsolutePath();
		this.pm = mContent.getPackageManager();
		parserApkDetail();
	}
	
	/**
	 * ����apk��Ϣ��������ȡ���е�һЩ��Ϣ��
	 * Ӧ�����ơ�Ӧ��ͼ�ꡢӦ�ð汾
	 */
	public void parserApkDetail()
	{
		packageInfo = pm.getPackageArchiveInfo(absPath, PackageManager.GET_ACTIVITIES);
		if(packageInfo != null)
		{
			appInfo = packageInfo.applicationInfo;
			appInfo.sourceDir = absPath;
			appInfo.publicSourceDir = absPath;
			appName = pm.getApplicationLabel(appInfo).toString();
			packageName = appInfo.packageName;
			version = packageInfo.versionName;
			icon1 = pm.getApplicationIcon(appInfo);
			icon2 = appInfo.loadIcon(pm);
			String pkgInfoStr = String.format("PackageName: %s, Version: %s, AppName:%s", packageName, version, appName);
			if(DBG) Log.i(TAG, String.format("PkgInfo: %s", pkgInfoStr));
			
			mPackageURI = Uri.fromFile(apks);
			mPkgInfo = PackageUtil.getPackageInfo(mPackageURI);
			app_key = packageName + version;
		}else
		{
			if(DBG) Log.e(TAG, "packageInfo is null");
		}
	}
	
	/**
	 * ����Ӧ��key����Ϊ��ֵ��
	 * @return
	 */
	public String getAppKey()
	{
		return app_key;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String pkgInfoStr = String.format("PackageName: %s, Version: %s, AppName:%s", packageName, version, appName);
		return String.format("PkgInfo: %s", pkgInfoStr);
	}

	/**
	 * ��ȡӦ��ͼ��
	 * @return
	 */
	public Drawable getApkIcon()
	{
		if(icon1 != null)
			return icon1;
		if(icon2 != null)
			return icon2;
		return mContext.getResources().getDrawable(R.drawable.ic_action_search);
	}
	
	public String getAppName()
	{
		if(appName == null)
		{
//			appName = apks.getName();ʹ���ļ������档���Ǻܺã������÷���
			appName = mContext.getResources().getString(R.string.unknownApp);
		}
		return appName;
	}
	
	public String getPackageName()
	{
		if(getApplicationInfo() != null)
			return getApplicationInfo().packageName;//packageName;
		else
			return null;
	}
	
	public String getAppVersion()
	{
		if(getPackageInfo() != null)
			return getPackageInfo().versionName;
		else
			return null;
	}
	
	public PackageInfo getPackageInfo()
	{
		return pm.getPackageArchiveInfo(getAppPath(), PackageManager.GET_ACTIVITIES);//packageInfo
	}
	
	public ApplicationInfo getApplicationInfo()
	{
		if(getPackageInfo() != null)
			return getPackageInfo().applicationInfo;
		else
			return null;
	}
	public void setApplicationInfo(ApplicationInfo ai)
	{
		appInfo = ai;
	}
	
	public boolean IsChecked()
	{//isChecked
		return ifInstall;
	}
	
	public void setIsChecked(boolean isChecked)
	{
		this.ifInstall = isChecked;
	}
	
	public Uri getPackageURI()
	{
		return Uri.fromFile(apks);
	}
	
	public PackageParser.Package getPackage()
	{
		return PackageUtil.getPackageInfo(Uri.fromFile(apks));
	}
	
	/**
	 * ��ȡ��װ��ʾλ
	 * @return ���ذ�װ��־λ
	 */
	public int getInstallFlag()
	{
		mPkgInfo = getPackage();
		try {
			PackageInfo pi = pm.getPackageInfo(mPkgInfo.applicationInfo.packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
			if(pi != null)
			{
				installFlag |= PackageManager.INSTALL_REPLACE_EXISTING;
				if(DBG) Log.i(TAG, "Replacing package: " + mPkgInfo.applicationInfo.packageName);
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if((mPkgInfo.applicationInfo.installLocation == PackageInfo.INSTALL_LOCATION_INTERNAL_ONLY)
				|| (mPkgInfo.applicationInfo.installLocation == PackageInfo.INSTALL_LOCATION_UNSPECIFIED)
				|| (mPkgInfo.applicationInfo.installLocation == PackageInfo.INSTALL_LOCATION_AUTO))
		{
			installFlag |= PackageManager.INSTALL_INTERNAL;
			installFlag &= ~PackageManager.INSTALL_EXTERNAL;
		}else{
			installFlag |= PackageManager.INSTALL_EXTERNAL;
			installFlag &= ~PackageManager.INSTALL_INTERNAL;
		}
		if(DBG) Log.i(TAG, mPkgInfo.applicationInfo.installLocation + "--" + mPkgInfo.applicationInfo.packageName);
		
		ArrayList<PackageParser.Activity> activityArray = mPkgInfo.receivers;
		int N = activityArray.size();
		for(int i = 0; i < N; i ++)
		{
			ArrayList<PackageParser.ActivityIntentInfo> intents = activityArray.get(i).intents;
			int intentN = intents.size();
			for(int j = 0; j < intentN; j ++)
			{
				if(DBG) Log.i(TAG, "Intent--" + intents.get(j).toString());
				if(intents.get(j).hasAction("Android.appwidget.action.APPWIDGET_UPDATE"))
				{
					//�������widget�ؼ�����ô�ñ�־λΪINSTALL_INTERNAL�����Ҳ��ܹ���ΪINSTALL_EXTERNAL;
					if(DBG) Log.i(TAG, "Widget Intent--" + intents.get(j).toString());
					installFlag |= PackageManager.INSTALL_INTERNAL;
					installFlag &= ~PackageManager.INSTALL_EXTERNAL;
					return installFlag;
				}
			}
		}
		
		return installFlag;
	}
	
	public File getFile()
	{
		return apks;
	}
	public void setFile(File value)
	{
		apks = value;
	}
	
	/**
	 * ��ȡ���ļ��ľ���·��
	 * @return
	 */
	public String getAppPath()
	{
		return apks.getAbsolutePath();
	}
	
	/**
	 * �趨��Ӧ���Ƿ�װ�ɹ�
	 * @return
	 */
	public boolean isSuccess()
	{
		return success;
	}
	
	public void setSucess(boolean isSucess)
	{
		success = isSucess;
	}
}




















