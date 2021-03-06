package com.taohu.androidcookie;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@TargetApi(19)
public class MainActivity extends Activity {

	private WebView wv;
	private String url;
	private final static String COOKIE = "BF2CB0E24FE45117827E689E0B45092AF05135EAB360115A3C2F5ACC00ADA859F5F93CBBE4D4E95E0B838238CD94063FD4CB717D9DE16086A90816D8F61EA69A8CAA07DFB2F47FB58F9D47BFB45E01AF76F7B9C9BDC63C21677C7EA533F36DBFEAF10DBF36943AD50052FCDAF8A22D234A463CF7CF27AC9DA680F0CD23CAA0C8791868495F5FFF487DC04484C0755A91028BD55ECDF984FE286BD5A40223F67FF399E5690843298B01418C05FB7308B7";

	enum DebugCookie {
		LoadData, AddHeader, AddJS, CookieManager,
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// should be called before webview or cookieManager is initialized
		CookieManager.setAcceptFileSchemeCookies(true);
		
		// allow remote webview debugging
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
		    WebView.setWebContentsDebuggingEnabled(true);
		}

		// url = "http://www.pivotaltracker.com/";
		// url = "http://99designs.com/";
		wv = (WebView) findViewById(R.id.wv_webtab);

		url = "http://9slides.com/";
//		DebugCookie type = DebugCookie.AddHeader;
		DebugCookie type = DebugCookie.AddJS;
//		DebugCookie type = DebugCookie.CookieManager;
		CookieManager.setAcceptFileSchemeCookies(true);

		if (type == DebugCookie.LoadData) {
			initWebView(R.id.wv_webtab);
			Communicator comm = new Communicator();
			String htmlStr = "";
			try {
				htmlStr = comm.httpGet(url);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			wv.loadData(htmlStr, "text/html", "UTF-8");
		} else if (type == DebugCookie.AddHeader) {
			initWebView(R.id.wv_webtab);
			String cookieStr = "forms9slides=9162BD577880CE411E4F875C5A6AD624D1C4399AC821D0FD048D6CA2D152BB8A0C9322E071A9A7EF814CB514657C54151075E8B363010B86E41818CA67F29FEF63C1CFBCCE7AC5A445234974350D532830E1DF6F37B07B43FD5905CBFF7C44633B0D3707F8545731BC31EAC0357F5791A8D563DFFD87DBE29E0BC08C6A340FB2F65DEECB48768D14D89B43BA6FC563DD0D77C955629DCFA407E5DEE2EB5199E17EBB35A353ACDC79D844414F892C4AB1;path=/;expires=Thu, 2 Aug 2021 20:47:11 UTC";
			Map<String, String> header = new HashMap<String, String>();
			header.put("Cookie", cookieStr);
			wv.loadUrl(url, header);
		} else if (type == DebugCookie.AddJS) {
			initWebView2(R.id.wv_webtab);
			wv.loadUrl(url);
			CookieManager.getInstance().setAcceptCookie(true);
		} else if (type == DebugCookie.CookieManager) {
			setCookies2();
			String cookie = CookieManager.getInstance().getCookie(url);
			Log.d("Cookie", url + "   " + cookie);
			initWebView(R.id.wv_webtab);
			wv.loadUrl(url);
		}

	}
	
	private void removeCook(){
		String cookies = "forms9slides=";
		String removeCookieJS = "javascript:(function(){" 
				+ "var expDate = new Date();"
				+ "expDate.setDate(expDate.getDate() - 365);"
				+ "expData = expDate.toGMTString();"
				+ "var cookieStr='" + cookies + ";expires=" + "'" + "+expDate;"
				+ "document.cookie=cookieStr;"
				+ "})()";
		Log.d("Cookie", removeCookieJS);
		wv.loadUrl(removeCookieJS);
	}
	
	private void setCook(WebView wv){
		String cookies = "forms9slides=" + COOKIE;
		String addCookieJS = "javascript:(function(){ " 
				+ "var expDate = new Date();"
				+ "expDate.setDate(expDate.getDate() + 365);"
				+ "expDate=expDate.toGMTString();"
				//+ "var cookieStr='" + cookies + ";path=/;expires=" + "'" + "+expDate;"
				+ "var cookieStr='" + cookies + ";domain=.9slides.com;path=/;expires=" + "'" + "+expDate;"
				//+ "var cookieStr='" + cookies + ";domain=.9slides.com;expires=" + "'" + "+expDate;"
				//+ "var cookieStr='" + cookies + ";expires=" + "'" + "+expDate;"
				//+ "var cookieStr='" + cookies + "';"
				+ "document.cookie=cookieStr;"
				+ "})()";
		Log.d("Cookie", addCookieJS);
		wv.loadUrl(addCookieJS);
	}
	
	private void showCookie(WebView wv){
		wv.loadUrl("javascript:(function(){"
				+ "alert(document.cookie)"
				+ "})()");  
	}
	
	private void jsTest(){
		String cookies = "forms9slides=" + COOKIE;
		Log.d("Cookie", "jsTest()");
		wv.loadUrl("javascript:(function(){"
				+ "var expDate = new Date();"
				+ "expDate.setDate(expDate.getDate() + 365);"
				+ "expDate=expDate.toGMTString();"
				+ "var cookStr='" + cookies + ";path=/;expires=" + "'" + "+expDate;"
				+ "document.cookie=cookStr;"
				+ "})()");  
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void setCookies2() {
		// Log.d("Cookie", "enter setCookies()");
		CookieSyncManager.createInstance(this);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
		cookieManager.setAcceptCookie(true);
		String cookieStr = "forms9slides=9162BD577880CE411E4F875C5A6AD624D1C4399AC821D0FD048D6CA2D152BB8A0C9322E071A9A7EF814CB514657C54151075E8B363010B86E41818CA67F29FEF63C1CFBCCE7AC5A445234974350D532830E1DF6F37B07B43FD5905CBFF7C44633B0D3707F8545731BC31EAC0357F5791A8D563DFFD87DBE29E0BC08C6A340FB2F65DEECB48768D14D89B43BA6FC563DD0D77C955629DCFA407E5DEE2EB5199E17EBB35A353ACDC79D844414F892C4AB1";
		cookieManager.setCookie(url, cookieStr);
		CookieSyncManager.getInstance().sync();
	}

	public void initWebView(int containerId) {
		wv.getSettings().setAppCacheEnabled(true);
		wv.getSettings().setAppCachePath(this.getCacheDir().getAbsolutePath());
		wv.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		wv.getSettings().setJavaScriptEnabled(true);
		CookieManager.setAcceptFileSchemeCookies(true);
		wv.setWebViewClient(new WebTabClient());
		wv.setWebChromeClient(new WebTabChromeClient());
	}

	public void initWebView2(int containerId) {
		wv.getSettings().setAppCacheEnabled(true);
		wv.getSettings().setAppCachePath(this.getCacheDir().getAbsolutePath());
		wv.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		wv.getSettings().setJavaScriptEnabled(true);
		wv.setWebViewClient(new WebTabClient2());
		wv.setWebChromeClient(new WebTabChromeClient());
	}

	public class WebTabClient2 extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
//			String cookies = "forms9slides=" + COOKIE;
//			Log.d("Cookie", "jsTest()");
//			view.loadUrl("javascript:(function(){"
//					+ "var expDate = new Date();"
//					+ "expDate.setDate(expDate.getDate() + 365);"
//					+ "expDate=expDate.toGMTString();"
//					+ "var cookStr='" + cookies + ";path=/;expires=" + "'" + "+expDate;"
//					//+ "alert(cookStr)"
//					+ "document.cookie=cookStr;"
//					+ "})()");  
			//view.loadUrl(url);
			//setCook(view);
			return false;
		}

		@Override
		public void onLoadResource(WebView view, String url) {
			// Log.d("Search", url);
			//super.onLoadResource(view, url);
//			setCook();
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			//jsTest();
			super.onPageStarted(view, url, favicon);
//			setCook(view);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
//			showCookie(view);
			//setCook(view);
//			jsTest();
			//removeCook();
		}
	}

	public class WebTabClient extends WebViewClient {
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			//showCookie(view);
		}
	}

	public class WebTabChromeClient extends WebChromeClient {
		
	}

}
