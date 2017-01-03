package com.github.aarsy.loginviagithubapi.githublogin;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.github.aarsy.loginviagithubapi.common.CommonGlobalVariable;

import org.json.JSONObject;



public class GithubApp {
	private GithubSession mSession;
	private GithubDialog mDialog;
	private OAuthAuthenticationListener mListener;
	private ProgressDialog mProgress;
	private String mAuthUrl;
	private String mTokenUrl;
	private String mAccessToken;

	public static String mCallbackUrl = "";


	private static final String TAG = "GitHubAPI";

	public GithubApp(Context context, String clientId, String clientSecret,
					 String callbackUrl) {
		mSession = new GithubSession(context);
		mAccessToken = mSession.getAccessToken();
		mCallbackUrl = callbackUrl;
		mTokenUrl = CommonGlobalVariable.TOKEN_URL + "client_id=" + clientId + "&client_secret="
				+ clientSecret + "&redirect_uri=" + mCallbackUrl;
		mAuthUrl = CommonGlobalVariable.AUTH_URL + "client_id=" + clientId + "&scope=user%20public_repo%20repo%20repo:status"+ "&redirect_uri="
				+ mCallbackUrl;

		GithubDialog.OAuthDialogListener listener = new GithubDialog.OAuthDialogListener() {
			@Override
			public void onComplete(String code) {
				getAccessToken(code);
			}

			@Override
			public void onError(String error) {
				mListener.onFail("Authorization failed");
			}
		};

		mDialog = new GithubDialog(context, mAuthUrl, listener);
		mProgress = new ProgressDialog(context);
		mProgress.setCancelable(false);
	}

	private void getAccessToken(final String code) {
		mProgress.setMessage("Getting access token ...");
		mProgress.show();

		new Thread() {
			@Override
			public void run() {
				Log.i(TAG, "Getting access token");
				int what = 0;

				try {
					String response=CommonGlobalVariable.get(mTokenUrl + "&code=" + code);
					Log.i(TAG, "response " + response);
					mAccessToken = response.substring(
							response.indexOf("access_token=") + 13,
							response.indexOf("&scope"));
					Log.i(TAG, "Got access token: " + mAccessToken);
				} catch (Exception ex) {
					what = 1;
					ex.printStackTrace();
				}

				mHandler.sendMessage(mHandler.obtainMessage(what, 1, 0));
			}
		}.start();
	}

	private void fetchUserName() {
		mProgress.setMessage("Finalizing ...");

		new Thread() {
			@Override
			public void run() {
				Log.i(TAG, "Fetching user info");
				int what = 0;
				try {
					String response=CommonGlobalVariable.get(CommonGlobalVariable.HOST+ "/user?access_token="
							+ mAccessToken);
					Log.d("resppponse", ""+response);
					JSONObject jsonObj = new JSONObject(response);
					String id = jsonObj.getString("id");
					String login = jsonObj.getString("login");
					Log.i(TAG, "Got user name: " + login);
					mSession.storeAccessToken(mAccessToken, id, login);
				} catch (Exception ex) {
					what = 1;
					ex.printStackTrace();
				}

				mHandler.sendMessage(mHandler.obtainMessage(what, 2, 0));
			}
		}.start();
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == 1) {
				if (msg.what == 0) {
					fetchUserName();
				} else {
					mProgress.dismiss();
					mListener.onFail("Failed to get access token");
				}
			} else {
				mProgress.dismiss();
				mListener.onSuccess();
			}
		}
	};

	public boolean hasAccessToken() {
		return (mAccessToken == null) ? false : true;
	}

	public void setListener(OAuthAuthenticationListener listener) {
		mListener = listener;
	}

	public String getUserName() {
		return mSession.getUsername();
	}

	public void authorize() {
		mDialog.show();
	}


	public void resetAccessToken() {
		if (mAccessToken != null) {
			mSession.resetAccessToken();
			mAccessToken = null;
		}
	}

	public interface OAuthAuthenticationListener {
		public abstract void onSuccess();

		public abstract void onFail(String error);
	}
	public String getAccessToken() {
		return mAccessToken;
	}
}