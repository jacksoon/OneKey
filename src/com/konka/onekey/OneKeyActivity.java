package com.konka.onekey;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageParser;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.konka.onekey.AboutActivity;
import com.konka.onekey.R;
import com.konka.adapter.InstallAppAdapter;
import com.konka.utils.ApkDetails;
import com.konka.utils.PackageUtil;
import com.konka.utils.ScanApks;

/**
 * ��T���γ�״̬��ʱ���Ƿ���Ҫ�رձ������
 * ��Ϊ���ɨ�����T�������apk�ļ������������ʾ��ô�ᵼ�°�װʧ�ܡ�
 * ����ֱ���˳���Ȼ����ʾ�û���
 * @author konka
 *
 */
public class OneKeyActivity extends Activity {
	
	private static final String TAG = "onekey";
	private static final boolean DBG = true;
	private Button bt_selectAll, bt_install;
	private TextView tv_selected_apps;
	private Context mContext;
	//����������˳���ť���˳�����
	private long mFirstCancelTime = 0;
	
	private ListView lv_installApps;
	//�ñ�����Ŵ���װ�Ͱ�װ�ɹ���Ӧ���б���Ϣ��
	private InstallAppAdapter iaa;
	//ɨ�赽������apk��Ϣ������mData����
	private List<ApkDetails> mData = new ArrayList<ApkDetails>();
	private int apksCounts = 0;
//	private ArrayList successList;
//	static ArrayList checkList;
//	private Uri mPackageURI;
	private WakeLock mWakeLock;
	private PackageManager mPm;
//	private PackageParser.Package mPkgInfo;
//	private List<File> apks;
	private int installindex = 0;
	private boolean installflag = true;
	ArrayList failList=null;
//	private ApplicationInfo mAppInfo = null;
	private final int INSTALL_COMPLETE = 1;
	private final int SCAN_COMPLETE = 2;
//	int installcount;//��װ���ܸ���
//	int installsuccesscount;//�ɹ�����
//	int installfailcount;//ʧ�ܸ���
	int mInstallComplete = 0;
	//��ʶ������listview�������Ŀ
	private int mLongClickSelectedFileIndex = 0;
	private static final int CHANNELSEARCH = 1;
	private static final int INSTALLINGDIALOG = 2;
	private static final int DIALOG_OPERATION_MENU = 3;
	private static final int DIALOG_YES_NO = 4;
	private static final int DIALOG_RENAME = 5;
	private static final int DIALOG_SHARE = 6;
	private static final int DIALOG_DETAIL = 7;
	
	private static final int OPERATION_DELETE = 0;
	private static final int OPERATION_RENAME = OPERATION_DELETE + 1;
	private static final int OPERATION_SHARE = OPERATION_DELETE + 2;
	private static final int OPERATION_DETAIL = OPERATION_DELETE + 3;
	
	private File mLongClickFile = null;
	private EditText mRename;
	private int mSelectedDialog;
	private ProgressDialog dialog;
	private ProgressDialog proDialog;
	private ProgressDialog searchDialog;
	private ScanApks sa;
	//��ѡ�У�����װ��apk����ԭʼ������ֻ��mData������list��ֻ��¼Ӧ�õ����
	private List<Integer> installList = new ArrayList<Integer>();	
	
	/**
	 * A: ��ֵ�����洢���������ϲ��ظ���Ӧ��apk����Ӧ�á�����&�汾�š���ΪΨһ��key��
	 * �м��&�����������������Ͱ汾�š��ü򵥵�split���ɡ������Ժ�������;��
	 * �����Ӧ���ļ�����һ��������key��һ���ģ���ô���ظ���ʾ�ˣ���ȫû�����塣
	 * �����Ӧ�ð���һ�������ǰ汾�Ų�һ������ô��Ȼ��ʾ����Ϊ�û����ܻ�ѡ��װ�Ͱ汾�������
	 * 
	 * B: ���߿������ļ�·������Ϊkey���������е�apk�Ͷ�����ʾ���������֮ǰ��װ�ˣ��������ǰ�װ��
	 */
	private HashMap<String, ApkDetails> allAppMap = new HashMap<String, ApkDetails>();
	
	private Boolean mAllSelected = false;
//	private HashMap<Integer, Boolean> mCheckedObj = new HashMap<Integer, Boolean>();
//	private int selectedItems = 0;
	private List<String> mTargetLauncher = null;
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_key);
        mContext = this.getApplicationContext();
        PowerManager powerManager = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "Installapk");
        bt_selectAll = (Button)findViewById(R.id.bt_select);
        bt_install = (Button) findViewById(R.id.bt_install);
        bt_selectAll.setOnClickListener(cl);
        bt_install.setOnClickListener(cl);
        tv_selected_apps = (TextView)findViewById(R.id.tv_number);
        lv_installApps = (ListView)findViewById(R.id.lv_installApps);
        lv_installApps.setOnItemClickListener(icl);
        //�趨��������Ӧ����
        lv_installApps.setOnItemLongClickListener(ilcl);
        proDialog = new ProgressDialog(OneKeyActivity.this);
        searchDialog = new ProgressDialog(mContext);
        
        sa = new ScanApks(mContext);
        removeDialog(CHANNELSEARCH);
        showDialog(CHANNELSEARCH);
        //��Ҫ��һ���µ��߳̽����ļ�ɨ��
        new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				//����ר��
				sa.scanExternalStorage(null);//"/storage/sdcard0/test"
				Message msg = new Message();
				msg.what = SCAN_COMPLETE;
				mHandler.sendMessage(msg);
			}        	
        }).start();
        iaa = new InstallAppAdapter(getLayoutInflater(), this, mData);
        apksCounts = mData.size();
        lv_installApps.setAdapter(iaa);
    }

   
    OnClickListener cl = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId())
			{
			//��װ����ѡ���apk�ļ�
			case R.id.bt_install:
				//��ʾ�����ڰ�װ�б�����ĳ�����Ϣ��installList
//				installList = iaa.getInstallApks();
				//�����װ����Ϊ0����ʾ�û�ѡ��һ��Ӧ���ٰ�װ
				if(iaa.getCheckedCounts() == 0)/*installList.isEmpty()*/
				{
					Toast.makeText(mContext, mContext.getResources().getString(R.string.no_app_selected), Toast.LENGTH_SHORT).show();
					break;
				}
//				Log.i(TAG, "show the progress Dialog");
				proDialog.setMessage(mContext.getResources().getString(R.string.installing));
				proDialog.show();
//				�����ʹ������ķ�ʽ�����progressdialog�Ͳ��ܹ���ʾ������ԭ��δ֪
				new Thread(new Runnable(){
					@Override
					public void run() {
						// TODO Auto-generated method stub						
						try {
							//installBatch2();	
//							Log.i(TAG, "onclicklistner start to install apk batch");
							installApkBatch();
							//���ڰ�װ���֮��launcher��Ҫ�Ƚϳ�ʱ����ܽ�����ͼ����mainmenu����ˢ�³���
							//�������ӳ�5���ӣ����ǻ�����һ����ͼ����ӳ���ʾ��û�취�ˣ�������Ҫ��һ��launcher���ˢ�µ�
							//������ֵ����������package��װ�����Ҫһ����ʱ�䣬���Բ���Ҫ�����ʱ�ˡ�
//							Thread.sleep(2000);
//							proDialog.cancel();
						} catch (Exception e) {//InterruptedException
							// TODO Auto-generated catch block
							Log.i(TAG, "e.printStackTrace()");
							e.printStackTrace();
						}						
					}
					
				}).start();
				break;
			//���ݵ�ǰ��ѡ��״̬����ȫѡ��ȡ��ȫѡ�Ĳ���
			case R.id.bt_select:
				//��ȡ��ǰѡ�еĸ��������ݸ������ж��Ƿ���ȫѡ
				int selectedNum = iaa.getCheckedCounts();
				Log.i(TAG, "selected num = " + selectedNum + "iaa.getAllApkCounts = " + iaa.getAllApkCounts());
				if(selectedNum != iaa.getAllApkCounts())
				{//���ѡ�и�������ǰ��һ����˵�����֮ǰ��ʾ����ȫѡ�����Դ�������ȫѡ�������ʾȡ��ȫѡ
					iaa.setAllCheckboxStatus(true);
					setAllListViewState(true);
					tv_selected_apps.setVisibility(View.VISIBLE);
					tv_selected_apps.setText("" + iaa.getCheckedCounts());
					bt_selectAll.setText(R.string.unSelectAll);
				}else
				{//�����һ����˵��֮ǰ��ť��ʾ����ȡ��ȫѡ��˵����ǰ��������ȡ��ȫѡ�������ʾȫѡ
					iaa.setAllCheckboxStatus(false);
					setAllListViewState(false);
					tv_selected_apps.setVisibility(View.INVISIBLE);
					bt_selectAll.setText(R.string.selectAll);					
				}				
				break;
			}
		}
	};
	
	/**
	 *  ��ʾ��װ������ڡ���ʾ��װ�ɹ���ʧ�ܸ�������Ϣ
	 */
	private void showResultScreen()
	{
		mWakeLock.release();
		Bundle bundle = new Bundle();
		bundle.putInt(InstallAppAdapter.INSTALL_COUNTS, iaa.getCheckedCounts());
		bundle.putInt(InstallAppAdapter.INSTALL_SUCESS_COUNTS, iaa.getSucessCounts());
		bundle.putInt(InstallAppAdapter.INSTALL_FAILURE_COUNTS, iaa.getFailureCounts());
		Log.i(TAG, iaa.getCheckedCounts() + "    ," + iaa.getSucessCounts());
		Intent intent = new Intent(OneKeyActivity.this, ResultShowActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);		
	}
	
	
	/**
	 * �����������е�listview��Ŀȫѡ����ȡ��ȫѡ��
	 * @param status ��Ҫ���õ�״̬��true ȫѡ��false ȡ��ȫѡ
	 */
	public void setAllListViewState(boolean status)
	{
		int counts = lv_installApps.getChildCount();
		for(int i = 0; i < counts; i ++)
		{			
			final LinearLayout layout = (LinearLayout)lv_installApps.getChildAt(i);
			CheckBox cb = (CheckBox)layout.findViewById(R.id.cb_checkedApp);
			cb.setChecked(status);
		}
	}
	
	/**
	 * ���ݸ��ֵ���¼����°�ť���������
	 */
	public void updateBtnText()
	{
		//����ѡ����Ŀ�жϰ�ť��ʾ�����֣��ж�ѡ�е���Ŀ�Ƿ����ʾ����Ŀһ��
		int counts = iaa.getCheckedCounts();
		if(counts == apksCounts)
		{
			mAllSelected = true;
			bt_selectAll.setText(R.string.unSelectAll);
		}else
		{
			mAllSelected = false;
			bt_selectAll.setText(R.string.selectAll);
		}
		//��ʾ��ť���������
		if(iaa.getCheckedCounts() == 0)
		{
			tv_selected_apps.setVisibility(View.INVISIBLE);
		} else {
			tv_selected_apps.setVisibility(View.VISIBLE);
			tv_selected_apps.setText(counts + "");
		}
	}
	
	
	/**
	 * ��Ӧlistview�е���ĵ����Ӧ���������ʱ����Ҫ���õ����checkbox״̬����ť������ʾ��ѡ�еĳ����б�
	 */
	OnItemClickListener icl = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			//�������¼����mCheckedObj�б�û��ѡ�е�item position�ǲ������������б��еġ�
			mAllSelected = false;
			iaa.setItemCheckedBoxState(view, position);

			updateBtnText();
//			//����ѡ����Ŀ�жϰ�ť��ʾ�����֣��ж�ѡ�е���Ŀ�Ƿ����ʾ����Ŀһ��
//			int counts = iaa.getCheckedCounts();
//			if(counts == apksCounts)
//			{
//				mAllSelected = true;
//				bt_selectAll.setText(R.string.unSelectAll);
//			}else
//			{
//				mAllSelected = false;
//				bt_selectAll.setText(R.string.selectAll);
//			}
//			//��ʾ��ť���������
//			if(iaa.getCheckedCounts() == 0)
//			{
//				tv_selected_apps.setVisibility(View.INVISIBLE);
//			} else {
//				tv_selected_apps.setVisibility(View.VISIBLE);
//				tv_selected_apps.setText(counts + "");
//			}
		}
	};
	
	AdapterView.OnItemLongClickListener ilcl = new AdapterView.OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			// TODO Auto-generated method stub
			
			//�ñ����ǳ���Ҫ����ʶ��ǰ��������ĸ�ѡ�������ɵĶԻ���ocl����
			//���Ǽ����������ɵ�AlertDialog�Ķ���
			mSelectedDialog = DIALOG_OPERATION_MENU;
			mLongClickFile = mData.get(arg2).getFile();
			mLongClickSelectedFileIndex = arg2;
//			Log.i(TAG, "arg2 = " + arg2 + ", mLongClickFile = " + mLongClickFile.getName() + ", the File is = " + mData.get(arg2).getFile().toString());
			showDialog(DIALOG_OPERATION_MENU);
			return false;
		}
	};
	
	DialogInterface.OnClickListener ocl = new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			// TODO Auto-generated method stub
			switch(mSelectedDialog)
			{
			case DIALOG_OPERATION_MENU:
				dialogLongClickMenuOnClick(arg1);
				break;
			case DIALOG_YES_NO:
				dialogYesNoOnClick(arg1);
				break;
			case DIALOG_RENAME:
				dialogRenameOnClick(arg1);
				break;
			case DIALOG_SHARE:
				break;			
			}
			
		}
	};
	
	private void dialogYesNoOnClick(int which)
	{
		if(DialogInterface.BUTTON_POSITIVE == which)
		{
			delete();
		}
	}
	
	/**
	 * �޸�ѡ�е��ļ���������������ֱ��ȡ����apk��Ӧ�����ƣ������Ƿ��б�Ҫ�������������á�
	 * @param which ָ���޸ĵ���
	 */
	private void dialogRenameOnClick(int which)
	{
		if(DialogInterface.BUTTON_POSITIVE == which)
		{
			String newName = mRename.getText().toString().trim();
			//����û����óɿգ���ô�Ͳ��޸��ļ�����
			if(newName.equals(""))
			{
//				Log.i(TAG, "the newName is null");
				return;
			}
			//������edittext�����ִ��������.apk�����������ļ���
			newName = newName + ".apk";
//			Log.i(TAG, "dialogRenameOnClick---------mLongClickFile = " + mLongClickFile.getName() + ", chageFileName = " + newName);
			String desFileName = mLongClickFile.getParent() + "/" + newName;
			File destFile = new File(desFileName);
			boolean result = mLongClickFile.renameTo(destFile);
//			Log.i(TAG, "dialogRenameOnClick---------sourceFile = " + mLongClickFile.getName() 
//					+ ", destFile = " + destFile.getName() + ", result = " + (result?"true":"false"));
			mData.get(mLongClickSelectedFileIndex).setFile(destFile);
			iaa.setListData(mData);

		}
	}
	
    /**
     * ����ѡ�е�apk�ļ��Ĵ��롣
     * �ر�ע��setType�����MIMETYPEֵ��
     * ������Ҫ����Intent.EXTRA_STREAM���ֵ��Ҳ�����ļ���ַ��
     * ���ߴ�����������Ӧ�û���ʾ"��Ч�ĵ�ַ"
     */
    private void shareAPK()
    {
    	Intent intent = new Intent(Intent.ACTION_SEND);
    	intent.setType("application/octet-stream");
    	Uri uri = Uri.fromFile(mLongClickFile);
    	intent.putExtra(Intent.EXTRA_STREAM, uri);
    	startActivity(Intent.createChooser(intent, mContext.getString(R.string.share_file)));    	
    }
	
	
	/**
	 * ɾ����ǰѡ�е�Ӧ���ļ������Ҹ���listview
	 * �Ǵ��б���ɾ������ͬʱɾ�������ϵ��ļ���
	 * Ҫ��֤��һ������Դ�����������ֹ�������ҡ�
	 */
	private void delete()
	{
		iaa.removeApk(mLongClickSelectedFileIndex);
		mLongClickFile.delete();//ɾ��ѡ�е�����ļ�
		updateBtnText();
		iaa.setListData(mData);
	}
	
	/**
	 * ��Ե�����ContextItemѡ���������Ӧ��
	 * @param which ѡ�����ĸ��˵�
	 */
	private void dialogLongClickMenuOnClick(int which)
	{
		switch(which)
		{
		case OPERATION_DELETE:
			mSelectedDialog = DIALOG_YES_NO;
			showDialog(DIALOG_YES_NO);
			break;
		case OPERATION_RENAME:
			mSelectedDialog = DIALOG_RENAME;
			showDialog(DIALOG_RENAME);
			break;
		case OPERATION_SHARE:
			mSelectedDialog = DIALOG_SHARE;
			showDialog(DIALOG_SHARE);
			break;
		case OPERATION_DETAIL:
			mSelectedDialog = DIALOG_DETAIL;
			showDialog(DIALOG_DETAIL);
			break;
		}
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_one_key, menu);
        return true;
    }

    //����˵��¼�
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	
    	int item_id=item.getItemId();
 
    	switch(item_id)
    	{
    		case R.id.about:
	
    		Intent ab_intent =new Intent();
    		ab_intent.setClass(OneKeyActivity.this,AboutActivity.class);
    		startActivity(ab_intent);
    		
    		break;

    	}
    	
		return true;
    	
    }

    /**
     * ������װѡ�е�apk
     */
    public void installApkBatch()    {
    	
    	mWakeLock.acquire();
    	int dataSize = mData.size();
    	Log.i(TAG, "----> installApkBatch mData size = " + mData.size());
    	mPm = getPackageManager();
    	PackageInstallObserver observer = new PackageInstallObserver();
    	/*ÿ��������װ֮ǰ�������Ƚ���װ��ɵ������־λ����, ������հ�װ�ɹ��б���װʧ���б�*/
    	mInstallComplete = 0;
    	iaa.clearSuceesAndFailList();
    	for(int i = 0; i < dataSize; i ++)
    	{
    		installindex  = i;
//    		Log.i(TAG, "for mData i = " + i);
    		/*��ѡ��Ҫ��װ��ʱ���ִ�а�װ����*/
    		if((mData.get(i).IsChecked()) && (mData.get(i).getPackage() != null))
    		{
    			Log.i(TAG, "����ѡ�У����԰�װ��i = " + i);
    	    	String pkgName = mData.get(i).getPackage().packageName;    	    	
    	    	String[] oldName = mPm.canonicalToCurrentPackageNames(new String[]{pkgName});
    	    	if(oldName != null && oldName.length > 0 && oldName[0] != null)
    	    	{
    	    		pkgName = oldName[0];
    	    		mData.get(i).getPackage().setPackageName(pkgName);
    	    	}
    	    	try{
    	    		mData.get(i).setApplicationInfo(mPm.getApplicationInfo(pkgName, PackageManager.GET_UNINSTALLED_PACKAGES));
    	    	}catch(NameNotFoundException e)
    	    	{
    	    		mData.get(i).setApplicationInfo(null);
    	    	}
    	    	ApkDetails mAd = mData.get(i);
    	    	int iFlag = mAd.getInstallFlag();
    	    	String installerPackagename = getIntent().getStringExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME);    	    	
    	    	mPm.installPackage(mAd.getPackageURI(), observer, iFlag, installerPackagename);    	    	
    		}
    	}
    }
    
    
    /**
     * ������װ�ĳ���    
     */
//    public void installBatch2()
//    {
//
//    	mWakeLock.acquire();
//    	//��ȡ�Ѿ�ѡ���Ҫ��װ�ĳ����б�
//    	List<Integer> installingApks = iaa.getInstallApks();
//    	int installCount = installingApks.size();
//    	while(installindex < installCount)
//    	{//������ڰ�װ�����apk��package��������Ϊ�գ���ô�Ͱ�װ
//    		if(mData.get(installingApks.get(installindex)).getPackage() != null)
//    		{//��װ��ѡ�е�Ӧ���б��еĵ�������
//    			initiateInstall2();
//    		}
//    		installindex ++;
//    	}
//    }
    
    /**
     * ��װ��ǰ�ֵ���apkӦ��
     */
//    private void initiateInstall2()
//    {
//    	String pkgName = mData.get(iaa.getInstallApks().get(installindex)).getPackage().packageName;
//    	mPm = getPackageManager();
//    	String[] oldName = mPm.canonicalToCurrentPackageNames(new String[]{pkgName});
//    	if(oldName != null && oldName.length > 0 && oldName[0] != null)
//    	{
//    		pkgName = oldName[0];
//    		mData.get(iaa.getInstallApks().get(installindex)).getPackage().setPackageName(pkgName);
//    	}
//    	try{
//    		mData.get(iaa.getInstallApks().get(installindex)).setApplicationInfo(mPm.getApplicationInfo(pkgName, PackageManager.GET_UNINSTALLED_PACKAGES));
//    	}catch(NameNotFoundException e)
//    	{
//    		mData.get(iaa.getInstallApks().get(installindex)).setApplicationInfo(null);
//    	}
//    	initView2();
//    }
    
    /**
     * ��Ҫ�ǻ�ȡinstallFlag��ֵ������installPackage���а�װ��
     */
//    public void initView2()
//    {
//    	ApkDetails mAd = mData.get(iaa.getInstallApks().get(installindex));
//    	int iFlag = mAd.getInstallFlag();
//    	String installerPackagename = getIntent().getStringExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME);
//    	PackageInstallObserver observer = new PackageInstallObserver();
//    	mPm.installPackage(mAd.getPackageURI(), observer, iFlag, installerPackagename);
//    }
    
    /**
     * ��ذ�װ״����Ρ��鿴PackageManager.INSTALL_SUCCEEDED������������ж��20�ְ�װ���ɹ���״��
     * ������Ҫע���Ƿ�ϸ�֡�
     * @author konka
     *
     */
    class PackageInstallObserver extends IPackageInstallObserver.Stub
    {

		@Override
		public void packageInstalled(String packageName, int returnCode)
				throws RemoteException {
			// TODO Auto-generated method stub
			//�����װ�ɹ�ִ�����²���
			if(returnCode == PackageManager.INSTALL_SUCCEEDED)
			{
				Log.i(TAG, "install success =" + packageName + ", installindex = " + installindex);
				//ֻ�ܴ�����ˣ����Կ���apk��ɨ�跽ʽ
				iaa.addSuccessList(packageName);
//				Integer tmp = iaa.getInstallApks().get(mInstallComplete);
//				iaa.addSuccessList(iaa.getInstallApks().get(mInstallComplete));
			}else
			{
				//ֻ�ܴ�����ˣ����Կ���apk��ɨ�跽ʽ
				iaa.addFailList(packageName);//mData.get(installindex)
				Log.i(TAG, "install failure =" + packageName + ", installindex = " + installindex);
			}
			Log.i(TAG, "install returnCode =" + returnCode);
			mInstallComplete ++;
			Log.i(TAG, "mInstallComplete = " + mInstallComplete + ", checked counts = " + iaa.getCheckedCounts());
			if(mInstallComplete == iaa.getCheckedCounts())
			{//�����е�Ӧ�ð�װ��Ϻ�����ʾ��װ���
				Message msg = mHandler.obtainMessage(INSTALL_COMPLETE);
				mHandler.sendMessage(msg);				
			}
		}
    	
    }
    
    /**
     * ����ɨ����ϺͰ�װ���֮����δ���
     */
    private Handler mHandler = new Handler()
    {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what)
			{
			case INSTALL_COMPLETE:
				Log.i(TAG, "��װ��ϣ��رս�������");
				//��װ����ȹص���������
				proDialog.cancel();
				//��װ���֮���ִ�и����������ʾ�´��ڲ���
//				Log.i(TAG, "into refreshDesk");
				//��ȫѡ����Ӧ�õ�ʱ���ˢ�����棬��Ϊ���һ���ǹ�����װ�Ż��õ���
				if(iaa.getCheckedCounts() == iaa.getAllApkCounts())
				{
					//ˢ�����棬��һЩ����ͼ����ʾ����ȷ��λ�á�
					refreshDesk();
				}
				showResultScreen();
				break;
			case SCAN_COMPLETE:
				//�ò���ǳ���Ҫ����ʼ������ɨ�赽��apkֵ
//		        apks = sa.getApksFileList();//������Ԫ��File����
		        mData = sa.getScanApksList();//sa.getApksNameList();//������Ԫ��ApkDetails����
		        apksCounts = mData.size();
		        iaa = new InstallAppAdapter(getLayoutInflater(), mContext, mData);
		        lv_installApps.setAdapter(iaa);
		        lv_installApps.invalidate();
//		        dismissDialog(CHANNELSEARCH);
		        removeDialog(CHANNELSEARCH);
				break;
			default:
				break;
			}
		}
    	
    };
    
    protected Dialog onCreateDialog(int id)
    {
    	switch(id)
    	{
    	case CHANNELSEARCH:
    		return searchDialog(this, this.getResources().getString(R.string.scanning));
    	case INSTALLINGDIALOG:
    		return searchDialog(this, this.getResources().getString(R.string.installing));
    	case DIALOG_OPERATION_MENU:
//    		Log.i(TAG, "onCreateDialog-----DIALOG_OPERATION_MENU");
			return new AlertDialog.Builder(this).setTitle(mContext.getString(R.string.manipulate))
    				.setIcon(R.drawable.ic_launcher)
    				.setItems(R.array.operation_menu, ocl).create();
			//ɾ���ļ�
    	case DIALOG_YES_NO:
    		return new AlertDialog.Builder(this).setTitle(mContext.getString(R.string.warning))
    				.setMessage(mContext.getString(R.string.delete_confirm)).setPositiveButton(mContext.getString(R.string.sure), ocl)
    				.setNegativeButton(mContext.getString(R.string.cancel), null).create();
    		//�������ļ�
    	case DIALOG_RENAME:
//    		Log.i(TAG, "onCreateDialog-----DIALOG_RENAME");
    		mRename = new EditText(this);
    		mRename.setText(getFileName(mLongClickFile.getName()));
    		return new AlertDialog.Builder(this).setTitle(mContext.getString(R.string.rename))
    				.setView(mRename).setPositiveButton(mContext.getString(R.string.sure), ocl)
    				.setNegativeButton(mContext.getString(R.string.cancel), ocl).create();
    		//�����ļ�
    	case DIALOG_SHARE:
    		shareAPK();
    		break;
    		//��ʾ����
    	case DIALOG_DETAIL:
//    		Log.i(TAG, "onCreateDialog-----DIALOG_DETAIL");
    		return showFileDetail(); 
    		
    	}
    	return null;
    }
    
    /**
     * 
     * @param fileName ���磺΢��.apk
     * @return �����ļ����Ʊ��磺΢��
     */
    private String getFileName(String fileName)
    {
    	if(fileName != null && fileName.endsWith(".apk"))
    	{
    		int ends = fileName.lastIndexOf(".apk");
    		String file_name = fileName.substring(0, ends);
    		Log.i(TAG, "the file name = " + file_name);
    		return file_name;
    	}
    	return null;
    }
    
    @Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		// TODO Auto-generated method stub
    	switch(id)
    	{
    	case DIALOG_OPERATION_MENU:
    	case DIALOG_YES_NO:
    	case DIALOG_RENAME:
    	case DIALOG_SHARE:
    	case DIALOG_DETAIL:
    		removeDialog(id);
    		break;
    	}
    	super.onPrepareDialog(id, dialog);
	}

	private Dialog searchDialog(Context context, String showStr)
    {
    	dialog = new ProgressDialog(context);
//    	CharSequence msg = getResources().getText(R.string.scanning);
    	dialog.setMessage(showStr);
    	dialog.setCanceledOnTouchOutside(false);
//    	ProgressDialog progressdialog = (ProgressDialog)dialog
    	dialog.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface arg0, int arg1, KeyEvent arg2) {
				// TODO Auto-generated method stub
				switch(arg2.getKeyCode())
				{
				case KeyEvent.KEYCODE_BACK:
					if(dialog != null && dialog.isShowing())
						dialog.dismiss();				
					finish();
					break;
				}
				return false;
			}
		});
    	return dialog;
    }
    
    /**
     * ��ʾѡ���ļ�����ϸ��Ϣ
     * @return ������ʾ��ϸ��Ϣ���ִ�
     */
    private AlertDialog showFileDetail()
    {
    	AlertDialog detailDialog = null;
    	String name, size, modifiedTime, path, permission;
    	StringBuilder string_builder = new StringBuilder();
//    	Log.i(TAG, "the file detail is = " + mLongClickFile.toString());
		//��ӡ��ǰmData�������е����ݡ�
//		for(int i = 0; i < mData.size(); i ++)
//		{
//			String print = mData.get(i).getFile().getName();
//			Log.i(TAG, "dialogRenameOnClick---------mData[" + i +"] = " + print);
//		}    	
//    	Log.i(TAG, "the detail file is 2= " + mData.get(mLongClickSelectedFileIndex).getFile().toString());
    	
    	//�ļ�����
    	string_builder.setLength(0);
    	name = string_builder.append(getString(R.string.name))
    			.append(": ").append(mLongClickFile.getName())
    			.append("\n")
    			.toString();
    	
    	//�ļ���С
    	string_builder.setLength(0);
    	size = string_builder.append(getString(R.string.size))
    			.append(": ").append(transSizeToString(mLongClickFile.length()))
    			.append("\n").toString();
    	
    	//�趨�޸�ʱ��
    	long time = mLongClickFile.lastModified();
    	string_builder.setLength(0);
    	modifiedTime = string_builder.append(getString(R.string.modified_time))
    			.append(": ").append(DateFormat.getDateInstance().format(new Date(time)))
    			.append("\n").toString();
    	
    	//�趨·��
    	string_builder.setLength(0);
    	path = string_builder.append(getString(R.string.path)).append(": ")
    		.append(mLongClickFile.getAbsolutePath()).append("\n").toString();

    	//�趨Ȩ��
    	string_builder.setLength(0);
    	string_builder.append(getString(R.string.readable)).append(": ")
    		.append(mLongClickFile.canRead()?getString(R.string.yes):getString(R.string.no))
    		.append("\n");
    	string_builder.append(getString(R.string.writable)).append(": ")
    		.append(mLongClickFile.canWrite()?getString(R.string.yes):getString(R.string.no))
    		.append("\n");
    	string_builder.append(getString(R.string.executable)).append(": ")
    		.append(mLongClickFile.canExecute()?getString(R.string.yes):getString(R.string.no));
    	permission = string_builder.toString();
    	
    	string_builder.setLength(0);
    	String detail = string_builder.append(name).append(size).append(modifiedTime)
    			.append(path).append(permission).toString();
    	//�ϳ����ơ���С���޸����ڡ�·�����ɶ�����д����ִ����Щ����
    	detailDialog = new AlertDialog.Builder(this).setTitle(mContext.getString(R.string.details))
    			.setMessage(detail).setPositiveButton(mContext.getString(R.string.sure), null).create();
    	return detailDialog;
    }
    
    private String transSizeToString(long size)
    {
    	final String UNIT_B = "B";
    	final String UNIT_KB = "KB";
    	final String UNIT_MB = "MB";
    	final String UNIT_GB = "GB";
    	final String UNIT_TB = "TB";
    	final int UNIT_INTERVAL = 1024;
    	final double ROUNDING_OFF = 0.005;
    	final int DECIMAL_NUMBER = 100;
    	
    	String unit = UNIT_B;
    	if(size < DECIMAL_NUMBER)
    	{
    		return Long.toString(size) + " " + unit;
    	}
    	
    	unit = UNIT_KB;
    	double sizeDouble = (double) size / (double) UNIT_INTERVAL;
    	if(sizeDouble > UNIT_INTERVAL)
    	{
    		sizeDouble = (double) sizeDouble / (double) UNIT_INTERVAL;
    		unit = UNIT_MB;
    	}
    	if(sizeDouble > UNIT_INTERVAL)
    	{
    		sizeDouble = (double) sizeDouble / (double) UNIT_INTERVAL;
    		unit = UNIT_GB;
    	}
    	if(sizeDouble > UNIT_INTERVAL)
    	{
    		sizeDouble = (double) sizeDouble / (double) UNIT_INTERVAL;
    		unit = UNIT_TB;
    	}
    	
    	long sizeInt = (long)((sizeDouble + ROUNDING_OFF) * DECIMAL_NUMBER);
    	double formatedSize = ((double) sizeInt) / DECIMAL_NUMBER;
//    	Log.d(TAG, "transSizeToString: " + formatedSize + unit);
    	
    	if(formatedSize == 0)
    	{
    		return "0" + " " + unit;
    	}else{
    		return Double.toString(formatedSize) + " " + unit;
    	}
    }
    /**
     * ��дonDestroy����Ϊ�����ڵ�������װ�����л����has leaked window����
     * �ο����ף�http://www.cnblogs.com/royenhome/archive/2011/05/20/2051879.html
     */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
//		removeDialog(CHANNELSEARCH);
		if(proDialog != null && proDialog.isShowing())
		{
			proDialog.dismiss();
		}
		super.onDestroy();
	}
	
	/**
	 * ��ʼ����Ҫ������Launcher���б�
	 */
	private void initTargetLauncher()
	{
		mTargetLauncher = new ArrayList<String>();
		//�ʹ�������
		mTargetLauncher.add("com.guobi.winguo.hybrid");
		//ϵͳ���棬��E900���棬�����Ӧ�İ�����com.android.launcher������launcher2
		mTargetLauncher.add("com.android.launcher");
//		mTargetLauncher.add("com.guobi.winguo.hybrid");
	}
	
	/**
	 * ��������������档
	 */
	private void refreshDesk()
	{
		Log.i(TAG, "refreshDesk()----->");
		initTargetLauncher();
		ActivityManager am = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
		
		try {
			Log.i(TAG, "refreshDesk()----->clear user data");
			Method clearUserdata = am.getClass()
					.getDeclaredMethod("clearApplicationUserData", 
							String.class, IPackageDataObserver.class);
			clearUserdata.setAccessible(true);
			clearUserdata.invoke(am, mTargetLauncher.get(1), new PackageDataClearObserver());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	class PackageDataClearObserver implements IPackageDataObserver{

		@Override
		public IBinder asBinder() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void onRemoveCompleted(String arg0, boolean arg1)
				throws RemoteException {
			// TODO Auto-generated method stub
			
		}
		
	}

	/**
	 * �ع����������������������back�����˳�����ֹ�û���back����
	 */
/*	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			long secondCancelTime = System.currentTimeMillis();
			//������ε�����С��1000������˳���
			if(secondCancelTime - mFirstCancelTime > 1000)
			{
				Toast.makeText(mContext, 
						mContext.getString(R.string.more_back_exit), 
						Toast.LENGTH_SHORT).show();
				mFirstCancelTime = secondCancelTime;
				return true;
		
			}else
			{
				System.exit(0);
			}
		}
		return super.onKeyUp(keyCode, event);
	}
	*/

    
}




























