package com.konka.utils;

import java.io.File;
import java.io.FileFilter;

public class MyFileFilter implements FileFilter{
	private final static String TAG = "onekey";
	private String searchType = null;
	private boolean deepthSearch = true;
	private String fileSuffixName;	
	
	/**
	 * 
	 * @param type �����type����Ϊ"txt"����"txt/pdf/umd/jar"
	 * @param deepth �����Ƿ�����������
	 */
	public MyFileFilter(String type, boolean deepth)
	{
		this.searchType = type;
		this.deepthSearch = deepth;
	}
	
	
	@Override
	public boolean accept(File pathname) {
		// TODO Auto-generated method stub
		//���ǵ�Ҫ�������������������Ҫ
		if(deepthSearch && pathname.isDirectory())
			return true;
		//��ȡ�ļ��ĺ�׺��
		fileSuffixName = pathname.getName();
		fileSuffixName = fileSuffixName.
				substring(fileSuffixName.lastIndexOf(".") + 1, fileSuffixName.length()).
				toLowerCase();
		//Log.i(TAG, "fileSuffixName = " + fileSuffixName + " searchType: " + searchType + " + fileSuffixName:" + fileSuffixName);	

		if(searchType.contains(fileSuffixName))
		{
			return true;
		}
		
		return false;
	}

}
