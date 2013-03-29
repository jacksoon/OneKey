package com.konka.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class ScanApks {
	private static final String TAG = "onekey";
	private List<File> apksList = new ArrayList<File>();
	private List<String> apksNameList = new ArrayList<String>();
	private List<ApkDetails> scanApksList = new ArrayList<ApkDetails>();
	private Context mContext;
	
	public ScanApks(Context mContext)
	{
		this.mContext = mContext;
	}
	/**
	 * �ж�����T���Ƿ��Ѿ�װ���ˣ����û�еĻ��ǲ��ܽ���T��ɨ��ġ�
	 * @return
	 */
	public boolean isExternalStorageAvailable()
	{		
		return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}
	
	/**
	 * ����������storage�������е�apk�ļ����������ֵ����activity��
	 * Ϊ�˱��ڲ��ԣ����Դ���һ���ǿյ�Stringֵ��
	 * @return
	 */
	public void scanExternalStorage(String path)
	{
//		String filePath = Environment.getExternalStorageDirectory().toString();
//		Log.i(TAG, "externalDirectory " + filePath); ��4.1ϵͳ��·��Ϊ��/storage/sdcard0��/storage/sdcard1
//��Ҫɨ��/storage����������ļ����ɡ���Ӧ4.0���µļ�Ϊɨ��/mnt/�ļ������������ļ��ˡ�
		if(path != null)
		{
			Log.i(TAG, "transmit unnull path value for test");
			File scanFile = new File(path);
			iteratorDirectory(scanFile, true);
		}else{
			File externalFile = Environment.getExternalStorageDirectory().getParentFile();
			Log.i(TAG, "external Directory " + externalFile.toString());
			iteratorDirectory(externalFile, true);
		}
	}
	
	/**
	 * ������ǰĿ¼������ض��ļ�������filefilterʹ�ã������г���ǰĿ¼���������ض���׺�ļ�
	 * @param filename ��Ҫ������Ŀ¼���ơ���������������ļ�����һ���ǰ�װ˳������
	 */
	private void iteratorDirectory(File filename, boolean deepth)
	{
		File[] fileLists = filename.listFiles(new MyFileFilter("apk", deepth));
		if(fileLists == null)
			return ;
		for(File fileList : fileLists)
		{
			if(fileList.isDirectory() && !fileList.getName().equals("asec"))
			{
				iteratorDirectory(fileList, deepth);
			}else if(fileList.isDirectory())
			{
				continue;
			}
			//ͻȻ����/storage/sdcard0/powerword/voice/727b79a9a430ccddcf9260a381d5ab10.p��������Ҳ�ܹ��ӽ�ȥ����̫������
			//���Լ���һ����׺���жϣ���֤�����ų��������������Ǹ�apk��ʲô���ء�
			else if(fileList.getName().endsWith("apk"))
			{
				Log.v(TAG, "file add: " + fileList.toString());	
				scanApksList.add(new ApkDetails(fileList, mContext));
				apksList.add(fileList);
				apksNameList.add(fileList.getName());
			}
		}
 
	}
	
	public List<String> getApksNameList()
	{
		return apksNameList;
	}
	
	public List<File> getApksFileList()
	{
		return apksList;
	}
	
	public List<ApkDetails> getScanApksList()
	{
		return scanApksList;
	}
	
	
	

}
