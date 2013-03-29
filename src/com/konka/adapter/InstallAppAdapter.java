package com.konka.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.konka.onekey.R;
import com.konka.utils.ApkDetails;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class InstallAppAdapter extends BaseAdapter {
	
	private final static String TAG = "onekey";
	private List<ApkDetails> mData = new ArrayList<ApkDetails>();
	private List<ApkDetails> mInstallSuccess = new ArrayList<ApkDetails>();
	private List<ApkDetails> mInstallFailure = new ArrayList<ApkDetails>();
	private List<String> mStrInstallSuccess = new ArrayList<String>();
	private List<String> mStrInstallFailure = new ArrayList<String>();
	/**
	 * ѡ�д���װ��apkλ��
	 */
	private List<Integer> installApks = new ArrayList<Integer>();
	//��װ�ɹ���ʧ�ܵ�apk�б�
	private List<Integer> installSuccess = new ArrayList<Integer>();
	private List<Integer> installFail = new ArrayList<Integer>();
	private HashMap<Integer, Boolean> isSelected;
	LayoutInflater mInflater;
	//��ѡ�е�app����
	private int selectedCounts = 0;
	private int allApkCount = 0;
	private Context mContext;
	public static final String INSTALL_COUNTS = "install_counts";
	public static final String INSTALL_SUCESS_COUNTS = "install_sucess_counts";
	public static final String INSTALL_FAILURE_COUNTS = "install_failure_counts";
	

	public InstallAppAdapter(LayoutInflater inflater, Context mContext, List<ApkDetails> mData)
	{
		mInflater = inflater;
		isSelected = new HashMap<Integer, Boolean>();
		this.mData = mData;
		this.allApkCount = mData.size();
		this.mContext = mContext;
		initSelectedMap();
	}

	public void setListData(List<ApkDetails> data)
	{
		this.mData = data;
		this.notifyDataSetChanged();
	}
	
	public void initSelectedMap()
	{
		int len = mData.size();
		for(int i = 0; i< len; i ++)
		{
			isSelected.put(i, false);
		}
	}
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		//Log.i(TAG, "getView position = " + position);

		ViewHolder viewHolder = new ViewHolder();
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.apps_list_item, null);
			viewHolder.cb_installed = (CheckBox) convertView
					.findViewById(R.id.cb_checkedApp);
			viewHolder.iv_appIcon = (ImageView) convertView
					.findViewById(R.id.iv_app_icon);
			viewHolder.tv_appName = (TextView) convertView
					.findViewById(R.id.tv_app_name);
			viewHolder.tv_appVersion = (TextView)convertView
					.findViewById(R.id.tv_app_version);
			convertView.setTag(viewHolder);
		}else
		{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		// ����Ӧ������
//		ȥ��Ӧ������ǰ��ġ����ơ�������������ȥ��
		//String mAppName = mContext.getString(R.string.default_app_name_tag) + mData.get(position).getFile().getName();
		String mAppName = mData.get(position).getFile().getName();
		String mAppVersion = mContext.getString(R.string.default_app_version_tag) + mData.get(position).getAppVersion();
		Log.i("new", "position " + position + " mAppName = " + mAppName);
		viewHolder.iv_appIcon.setImageDrawable(mData.get(position).getApkIcon());//.setImageResource(R.drawable.ic_launcher)
		viewHolder.tv_appName.setText(mAppName);
		viewHolder.tv_appVersion.setText(mAppVersion);
		viewHolder.cb_installed.setChecked(mData.get(position).IsChecked());//isSelected.get(position)
		
		return convertView;		
	}
	
	/**
	 * ����ĳһ��Ŀcheckbox״̬
	 * @param mView
	 * @param index
	 */
	public void setItemCheckedBoxState(View mView, int index)
	{
		ViewHolder holder = (ViewHolder)mView.getTag();
		holder.cb_installed.toggle();
		mData.get(index).setIsChecked(holder.cb_installed.isChecked());
		if(holder.cb_installed.isChecked())
		{
			Log.i(TAG, "InstallAppAdapter ADD checked " + mData.get(index).toString());
			//installApks.add(Integer.valueOf(index));
			selectedCounts ++;
		}else
		{
			Log.i(TAG, "InstallAppAdapter REMOVE checked " + mData.get(index).toString());
			//installApks.remove(Integer.valueOf(index));
			selectedCounts --;
		}
	}
	
	/**
	 * ����ȫѡ��ȡ��ȫѡ����Ҫ�޸İ�װ�б��ѡ�еĸ���
	 * @param mFlag
	 */
	public void setAllCheckboxStatus(Boolean mFlag)
	{
		int allApkCounts = mData.size();
		//ȡ��ȫѡ
		if(!mFlag)
		{
			selectedCounts = 0;
			//installApks.clear();
			Log.i(TAG, "setAllCheckboxStatus unSelectAll!");
			for(int i = 0; i < allApkCounts; i ++)
			{
				mData.get(i).setIsChecked(false);
			}
		}else//ȫѡ����
		{
			selectedCounts = mData.size();
			//installApks.clear();
			for(int i = 0; i < allApkCounts; i ++)
			{	
			//	installApks.add(Integer.valueOf(i));
				mData.get(i).setIsChecked(true);
			}
			Log.i(TAG, "setAllCheckboxStatus selectall");
		}		
	}
	
	/**
	 * ��ÿ��������װ֮ǰ������հ�װ�ɹ���ʧ���б���Ϊ
	 * ÿ�ΰ�װ�������¼���
	 */
	public void clearSuceesAndFailList()
	{
		mStrInstallFailure.clear();
		mStrInstallSuccess.clear();
	}
	
	/**
	 * ��ȡ��ѡ�е�Ӧ�ø�����ֱ�ӷ��ش���װ��list���ȼ��ɡ���ֹ����������޸�
	 * @return
	 */
	public int getCheckedCounts()
	{//installApks.size()
		return selectedCounts;
	}
	
	/**
	 * ��ȡ�Ѿ�ѡ�е�Ҫ��װ��Ӧ���б�
	 * @return
	 */
	public List<Integer> getInstallApks()
	{
		return installApks;
	}
	
//	public void addSuccessList(Integer add)
//	{
//		installSuccess.add(add);
//	}

	/**
	 * ����һ������ĺ����������֤ͨ���Ͳ���Ҫ������ˡ�
	 * �����첽ԭ��mInstallSuccess����б�����ȷ����������Ӧ����ok�ġ�
	 * @param sucessApk
	 */
	public void addSuccessList(ApkDetails sucessApk)
	{
		mInstallSuccess.add(sucessApk);
	}
	
	/**
	 * ����һ������ĺ����������֤ͨ���Ͳ���Ҫ������ˡ�
	 * �����첽ԭ��mInstallSuccess����б�����ȷ����������Ӧ����ok�ġ�
	 * @param sucessApk
	 */
	public void addSuccessList(String sucessApk)
	{
		mStrInstallSuccess.add(sucessApk);
	}
	
	public void removeSuccessList(ApkDetails add)
	{
		installSuccess.remove(add);
	}
	
	public void addFailList(Integer add)
	{
		installFail.add(add);
	}
	/**
	 * ����һ�°�װʧ���б�Ĳ�����
	 * @param failure
	 */
	public void addFailList(ApkDetails failure)
	{
		mInstallFailure.add(failure);
	}
	
	/**
	 * ����һ�°�װʧ���б�Ĳ�����
	 * @param failure
	 */
	public void addFailList(String failure)
	{
		mStrInstallFailure.add(failure);
	}
	
	public void removeFailList(ApkDetails add)
	{
		installFail.remove(add);
	}
	/**
	 * ��ȡ�ɹ���װ��Ӧ���б�
	 * @return
	 */
	public List<Integer> getSuccessList()
	{
		return installSuccess;
	}
	
	/**
	 * ��ȡ��װ�ɹ���Ӧ�ø���
	 * @return
	 */
	public int getSucessCounts()
	{
		//return installSuccess.size();
		return mStrInstallSuccess.size();
		//return mSetInstallSuccess.size();
		
	}
	
	public int getFailureCounts()
	{
		//return installSuccess.size();
		return mStrInstallFailure.size();
		
	}
	
	/**
	 * ��ȡ��װʧ�ܵ�Ӧ���б�
	 * @return
	 */
	public List<Integer> getFialList()
	{
		return installFail;
	}
	
	public int getAllApkCounts()
	{
		return mData.size();
	}
	
	/**
	 * ��ִ��ɾ��������ʱ��ͬʱɾ��ѡ��Ӧ�ó����б��е�Ӧ��
	 * @param apkIndex
	 */
	public void removeApk(int apkIndex)
	{
//		Log.i(TAG, "apkIndex = " + apkIndex);
//		for(int i = 0; i < installApks.size(); i ++)
//		{
//			Log.i(TAG, "removeApk---installApks index = " + i + ", contents = " + installApks.get(i));
//		}
		
		//ȥ��ָ��λ�õ�����
		Log.i(TAG, "removeApk mData.size() = " + mData.size());
		//���ɾ��������ѡ�е�apk����ô������ı�ѡ�е���ֵ�ˡ�
		if(mData.get(apkIndex).IsChecked())
		{
			selectedCounts --;
		}
		mData.remove(apkIndex);
		Log.i(TAG, "removeApk mData.size()2 = " + mData.size());
		//�ж�ָ��λ���Ƿ���installApks��installSuccess�������б����档α����Ϊ��
		//�ж�ɾ����ź͵�ǰ���ݣ����һ����˵��ѡ�е����������ǰɾ���ģ�ֱ��remove���������ȡ���ıȵ�ǰɾ����С
		//˵����Ҫ���洢����ż�1
//		int installApksSize = installApks.size();
//		for(int i = 0; i < installApksSize; i ++)
//		{
//			//���installApks��ص����ݣ��Ƴ�֮��ᵼ�¸�list���ȼ��٣�������Ҫ����list���Ⱥ͵�ǰ������Ԫ��
//			if(apkIndex == installApks.get(i))
//			{
//				Log.i(TAG, "installApks size = " + installApks.size());
//				//����֮ǰû�н�apkIndexת����Integer���󣬵���remove���Ĳ���list���õĶ��󣬶���ָ��λ�õĶ���
//				//�����쳣���
//				installApks.remove(new Integer(apkIndex));
//				i --;
//				installApksSize --;
//			}
//			//���µ���installapks�б�����洢��mData�еĶ����±�
//			if((i >= 0) && (apkIndex < installApks.get(i)))
//			{
//				Log.i(TAG, "i else if = " + i + ", the contents = " + (installApks.get(i) -1));
//				installApks.set(i, new Integer(installApks.get(i) -1));
//			}
//		}

	}
	
	
	private final static class ViewHolder
	{
		CheckBox cb_installed;
		ImageView iv_appIcon;
		TextView tv_appName;
		TextView tv_appVersion;
	}

}













