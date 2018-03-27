package com.webview.app;

import android.annotation.TargetApi;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.view.*;
import android.widget.*;
import android.app.*;
import java.io.*;
import java.net.*;
import java.util.*;
import android.webkit.*;
import android.os.*;
import android.content.*;
import android.net.*;
import android.util.*;
import android.support.design.widget.*;
import android.support.v4.widget.*;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.*;
import com.daimajia.numberprogressbar.*;
public class MainActivity extends AppCompatActivity

{
	NumberProgressBar mNumberProgressBar;
	public static ValueCallback<Uri> mUploadMessage;
    public static ValueCallback<Uri[]> mUploadMessage5;
	public static final int FILECHOOSER_RESULTCODE = 5173;
    public static final int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 5174;
    private NestedWebView mWebview;
    private View mCustomView;
    private int mOriginalSystemUiVisibility;
    private int mOriginalOrientation;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;

   	@Override
    protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_main);	
		Toolbar toolBar= (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolBar);
		setActionBar();
		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view)
				{
					ShareUtils.shareText(MainActivity.this, mWebview.getTitle() + " " + mWebview.getUrl() + " 来自 浏览器_Y 应用");
				}});
		initData();
	}
	
    private void initData()
	{
		mNumberProgressBar = (NumberProgressBar)findViewById(R.id.npb_web);
        mWebview = (NestedWebView) findViewById(R.id.webview);
        WebSettings settings = mWebview.getSettings();
        settings.setJavaScriptEnabled(true);    // 启用js
        settings.setJavaScriptCanOpenWindowsAutomatically(true);    // js和android交互
		mWebview.getSettings().setDefaultTextEncodingName("UTF -8");
        mWebview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebview.getSettings().setBuiltInZoomControls(true);
        mWebview.getSettings().setPluginState(WebSettings.PluginState.ON);//必有播放flash
        mWebview.getSettings().setSupportZoom(true); //缩放
        mWebview.getSettings().setAllowFileAccess(true);
		mWebview.getSettings().setLoadWithOverviewMode(true);//自适应屏
		mWebview.getSettings().setDatabaseEnabled(true);
		mWebview.getSettings().setGeolocationEnabled(true);//定位
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebview.getSettings().setAllowFileAccess(true);
        mWebview.getSettings().setAppCacheEnabled(true);
        mWebview.getSettings().setDomStorageEnabled(true);
        mWebview.getSettings().setDatabaseEnabled(true);
        mWebview.getSettings().setUseWideViewPort(true);
        mWebview.setLayerType(View.LAYER_TYPE_HARDWARE, null);//这句是重点
        mWebview.getSettings().setLoadWithOverviewMode(true);
		mWebview.clearCache(true);
		mWebview.clearHistory();
		mWebview.clearFormData();
        setUpWebViewDefaults(mWebview);
        mWebview.loadUrl("http://www.jianshu.com");
		mWebview.setDownloadListener(new MyWebViewDownLoadListener());
        mWebview.setWebChromeClient(new WebChromeClient() {
				@Override
				public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg)
				{
					WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
					transport.setWebView(view);
					resultMsg.sendToTarget();
					return true;
				}
				@Override
				public void onProgressChanged(WebView view, int newProgress)
				{
					if (mNumberProgressBar.getVisibility() == View.GONE)
					{
						mNumberProgressBar.setVisibility(View.VISIBLE);
					}
					mNumberProgressBar.setProgress(newProgress);
					if (newProgress == 100)
					{
						mNumberProgressBar.setVisibility(View.GONE);
					}
				}
				//得带一个默认的视频海报
                @Override
                public Bitmap getDefaultVideoPoster()
				{
                    return BitmapFactory.decodeResource(getResources(), R.drawable.image_2);
                }
                //处理全屏
                @Override
                public void onShowCustomView(View view,
                                             WebChromeClient.CustomViewCallback callback)
				{
                    // if a view already exists then immediately terminate the new one
                    if (mCustomView != null)
					{
                        onHideCustomView();
                        return;
                    }

                    // 1. Stash the current state
                    //保存状态栏的状态以及全屏的状态
                    mCustomView = view;
                    mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
                    mOriginalOrientation = getRequestedOrientation();

                    // 2. Stash the custom view callback
                    mCustomViewCallback = callback;

                    // 3. Add the custom view to the view hierarchy
                    FrameLayout decor = (FrameLayout)getWindow().getDecorView();
                    decor.addView(mCustomView, new FrameLayout.LayoutParams(
                                      ViewGroup.LayoutParams.MATCH_PARENT,
                                      ViewGroup.LayoutParams.MATCH_PARENT));

                    // 4. Change the state of the window
                    //处理状态栏的问题
					getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_IMMERSIVE);
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                //退出全屏
                @Override
                public void onHideCustomView()
				{
                    // 1. Remove the custom view
                    FrameLayout decor = (FrameLayout)getWindow().getDecorView();
                    decor.removeView(mCustomView);
                    mCustomView = null;

                    // 2. Restore the state to it's original form
                    decor.setSystemUiVisibility(mOriginalSystemUiVisibility);
					setRequestedOrientation(mOriginalOrientation);

                    // 3. Call the custom view callback
                    mCustomViewCallback.onCustomViewHidden();
                    mCustomViewCallback = null;
                }
				@Override
                public View getVideoLoadingProgressView()
				{
                    return super.getVideoLoadingProgressView();
                }
                //文件上传
                // For Android >= 4.1
                public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture)
				{
                    mUploadMessage = uploadMsg;
                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setType("*/*");
                    startActivityForResult(Intent.createChooser(i, "文件选择"), FILECHOOSER_RESULTCODE);
                }
				// For Lollipop 5.0+ 
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams)
				{
                    if (mUploadMessage5 != null)
					{
                        mUploadMessage5.onReceiveValue(null);
                        mUploadMessage5 = null;
                    }
                    mUploadMessage5 = filePathCallback;
                    Intent intent = fileChooserParams.createIntent();
                    try
					{
                        startActivityForResult(intent, FILECHOOSER_RESULTCODE_FOR_ANDROID_5);
                    }
					catch (ActivityNotFoundException e)
					{
                        mUploadMessage5 = null;
                        return false;
                    }
                    return true;
                }
            });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setUpWebViewDefaults(WebView webView)
	{
        WebSettings settings = webView.getSettings();
        // Enable Javascript
        settings.setJavaScriptEnabled(true);
        // Use WideViewport and Zoom out if there is no viewport defined
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        // Enable pinch to zoom without the zoom buttons
        settings.setBuiltInZoomControls(true);
        // Allow use of Local Storage
        settings.setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB)
		{
            // Hide the zoom controls for HONEYCOMB+
            settings.setDisplayZoomControls(false);
        }

        // Enable remote debugging via chrome://inspect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		{
            WebView.setWebContentsDebuggingEnabled(true);
        }
        webView.setWebViewClient(new WebViewClient(){
				@Override
				public void onPageFinished(WebView view, String url)
				{

				}
                @Override
                public void onScaleChanged(WebView view, float oldScale, float newScale)
				{
                    mWebview.setInitialScale((int) (oldScale - newScale - 0.5F));
                }
            });
    }

	@Override
    public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.web_view, menu);

        return true;

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
	{
        int id = item.getItemId();
		if (id == R.id.vh)
		{
			if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
			{
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 横屏
			}
			else
			{
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
			}
		}
		if (id == R.id.refresh)
		{
			mWebview.reload();
		}
		if (id == R.id.jianshu)
		{
			mWebview.loadUrl("http://www.jianshu.com");
		}
		if (id == R.id.ac)
		{
			mWebview.loadUrl("http://www.acfun.cn");
		}
		if (id == R.id.gowebsite)
		{
			final EditText et = new EditText(MainActivity.this);
			et.setText("http://www.baidu.com");
			et.setSelection(et.length());
			et.setGravity(Gravity.CENTER);
			android.support.v7.app. AlertDialog.Builder weblist=new android.support.v7.app. AlertDialog.Builder(MainActivity.this)
				.setTitle("输入网址")
				.setView(et);
			weblist.setIcon(R.drawable.ic_bookmark_24dp);
			weblist.setNegativeButton("取消", null)
				.setPositiveButton(
				"确定",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface p1, int p2)
					{String in = et.getText().toString(); 
						if (in.equals(""))
						{
							Toast.makeText(MainActivity.this, "没有输入!", Toast.LENGTH_SHORT).show();
						}
						else
						{
							String str1=null;
							str1 = et.getText().toString();		
							mWebview.loadUrl(str1);
						}
					}
				});weblist.show();
		}
		if (id == R.id.copywebsite)
		{
			BrowserUtils.copyToClipBoard(MainActivity.this, mWebview.getUrl());
		}
	
		if (id == R.id.usebrowser)
		{
			BrowserUtils.openInBrowser(MainActivity.this, mWebview.getUrl());
		}

		else
            return super.onOptionsItemSelected(item);
		return false;
	}

	// 监听浏览器下载任务事件
	//设置下载连接调用浏览器打开

	private class MyWebViewDownLoadListener implements DownloadListener
	{  
		@Override  
		public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength)
		{             
			Uri uri = Uri.parse(url);  
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);  
			startActivity(intent);     
		}  
	} 

	public void openApp(Activity activity, String url)
	{
        try
		{
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            activity.startActivity(intent);
        }
		catch (Exception e)
		{
            Toast.makeText(activity,"没有相关客户端",1000).show();
        }
    }
	public void CallBack(int requestCode, int resultCode, Intent intent)
	{
        if (requestCode == MainActivity.FILECHOOSER_RESULTCODE)
		{
            if (null == MainActivity.mUploadMessage)
			{
                return;
            }
            Uri result = intent == null || resultCode != Activity.RESULT_OK ? null  : intent.getData();
            MainActivity.mUploadMessage.onReceiveValue(result);
            MainActivity.mUploadMessage = null;
        }
		else if (requestCode == MainActivity.FILECHOOSER_RESULTCODE_FOR_ANDROID_5)
		{
            if (null == MainActivity.mUploadMessage5)
			{
                return;
            }
            MainActivity.mUploadMessage5.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
            MainActivity.mUploadMessage5 = null;
        }
	}
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
        if (keyCode == event.KEYCODE_BACK)
        {
			if (mWebview.canGoBack())
			{
				mWebview.goBack();
			}
			else
			{
				AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
				dialog.setTitle("提示");
				dialog.setMessage("确定退出？");
				dialog.setNegativeButton("取消", null);
				dialog.setPositiveButton("确认", new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface p1, int p2)
						{
							android.os.Process.killProcess(android.os.Process.myPid());
						}
					});
				AlertDialog alertDialog = dialog.create();
				alertDialog.show();
				alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(0xffff4081);
			}
		}
		return false;
    }
	private void setActionBar()
	{
        setTitle("浏览器_Y");
    }
}

