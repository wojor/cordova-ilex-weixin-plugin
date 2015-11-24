package com.ilex.plugins.weixin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaInterface;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
/**
 * 微信支付插件
 * 
 * @author NCIT
 * 
 */
public class Wechat extends CordovaPlugin {

    public static final String WECHAT_APPID_KEY = "wechatappid";

	public static final String ERROR_WX_NOT_INSTALLED = "Not installed";
	public static final String ERROR_ARGUMENTS = "Argument Error";

	public static final String KEY_ARG_MESSAGE = "message";
	public static final String KEY_ARG_SCENE = "scene";
	public static final String KEY_ARG_TEXT = "text";
	public static final String KEY_ARG_MESSAGE_TITLE = "title";
	public static final String KEY_ARG_MESSAGE_DESCRIPTION = "description";
	public static final String KEY_ARG_MESSAGE_THUMB = "thumb";
	public static final String KEY_ARG_MESSAGE_MEDIA = "media";
	public static final String KEY_ARG_MESSAGE_MEDIA_TYPE = "type";
	public static final String KEY_ARG_MESSAGE_MEDIA_WEBPAGEURL = "webpageUrl";
	public static final String KEY_ARG_MESSAGE_MEDIA_TEXT = "text";

	public static final String ERR_WECHAT_NOT_INSTALLED = "ERR_WECHAT_NOT_INSTALLED";
    public static final String ERR_INVALID_OPTIONS = "ERR_INVALID_OPTIONS";
    public static final String ERR_UNSUPPORTED_MEDIA_TYPE = "ERR_UNSUPPORTED_MEDIA_TYPE";
    public static final String ERR_USER_CANCEL = "ERR_USER_CANCEL";
    public static final String ERR_AUTH_DENIED = "ERR_AUTH_DENIED";
    public static final String ERR_SENT_FAILED = "ERR_SENT_FAILED";
    public static final String ERR_UNSUPPORT = "ERR_UNSUPPORT";
    public static final String ERR_COMM = "ERR_COMM";
    public static final String ERR_UNKNOWN = "ERR_UNKNOWN";
    public static final String NO_RESULT = "NO_RESULT";
    
    public static final int SHARE_TYPE_APP = 1;
    public static final int SHARE_TYPE_EMOTION = 2;
    public static final int SHARE_TYPE_FILE = 3;
    public static final int SHARE_TYPE_IMAGE = 4;
    public static final int SHARE_TYPE_MUSIC = 5;
    public static final int SHARE_TYPE_VIDEO = 6;
    public static final int SHARE_TYPE_WEBPAGE = 7;

    public static final int SCENE_SESSION = 0;
    public static final int SCENE_TIMELINE = 1;
    public static final int SCENE_FAVORITE = 2;
	
	public static IWXAPI wxAPI;

	/** LOG TAG */
	private static final String LOG_TAG = Wechat.class.getSimpleName();
	/** JS回调接口对象 */
	public static CallbackContext currentCallbackContext = null;
	private String APPID;


    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        APPID = preferences.getString(WECHAT_APPID_KEY, "");
        wxAPI = WXAPIFactory.createWXAPI(webView.getContext(), APPID, true);
        wxAPI.registerApp(APPID);
    }
	/**
	 * 插件主入口
	 */
	@Override
	public boolean execute(String action, final JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		LOG.d(LOG_TAG, "Wechat#execute");

		if ("payment".equalsIgnoreCase(action)) {
			return sendPay(args, callbackContext);
		} else if (action.equals("share")) {
			// sharing
			return share(args, callbackContext);
		} else if(action.equals("sendAuthRequest")) {
			return sendAuthRequest(args, callbackContext);
		} else if(action.equals("isWXAppInstalled")) {
			return isInstalled(callbackContext);
		}

		return super.execute(action, args, callbackContext);
	}

	protected boolean sendPay(JSONArray args, CallbackContext callbackContext) throws JSONException{
		LOG.d(LOG_TAG, "Wechat#payment.start");
		
		boolean ret = false;

		currentCallbackContext = callbackContext;

		PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
		pluginResult.setKeepCallback(true);
		callbackContext.sendPluginResult(pluginResult);

		// 参数检查
		if (args.length() != 1) {
			LOG.e(LOG_TAG, "args is empty", new NullPointerException());
			ret = false;
            PluginResult result = new PluginResult(PluginResult.Status.ERROR, "args is empty");
            result.setKeepCallback(true);
            currentCallbackContext.sendPluginResult(result);
			return ret;
		}

		JSONObject jsonObj = args.getJSONObject(0);

		final String appid = jsonObj.getString("appid");
		if (appid == null || "".equals(appid)) {
			LOG.e(LOG_TAG, "appid is empty", new NullPointerException());
			ret = false;
            PluginResult result = new PluginResult(PluginResult.Status.ERROR, "appid is empty");
            result.setKeepCallback(true);
            currentCallbackContext.sendPluginResult(result);
			return ret;
		}
		final String noncestr = jsonObj.getString("noncestr");
		if (noncestr == null || "".equals(noncestr)) {
			LOG.e(LOG_TAG, "noncestr is empty", new NullPointerException());
			ret = false;
            PluginResult result = new PluginResult(PluginResult.Status.ERROR, "noncestr is empty");
            result.setKeepCallback(true);
            currentCallbackContext.sendPluginResult(result);
			return ret;
		}
		final String packageValue = jsonObj.getString("package");
		if (packageValue == null || "".equals(packageValue)) {
			LOG.e(LOG_TAG, "packageValue is empty", new NullPointerException());
			ret = false;
            PluginResult result = new PluginResult(PluginResult.Status.OK, "packageValue is empty");
            result.setKeepCallback(true);
            currentCallbackContext.sendPluginResult(result);
			return ret;
		}
		final String partnerid = jsonObj.getString("partnerid");
		if (partnerid == null || "".equals(partnerid)) {
			LOG.e(LOG_TAG, "partnerid is empty", new NullPointerException());
			ret = false;
            PluginResult result = new PluginResult(PluginResult.Status.ERROR, "partnerid is empty");
            result.setKeepCallback(true);
            currentCallbackContext.sendPluginResult(result);
			return ret;
		}
		final String prepayid = jsonObj.getString("prepayid");
		if (prepayid == null || "".equals(prepayid)) {
			LOG.e(LOG_TAG, "prepayid is empty", new NullPointerException());
			ret = false;
            PluginResult result = new PluginResult(PluginResult.Status.ERROR, "prepayid is empty");
            result.setKeepCallback(true);
            currentCallbackContext.sendPluginResult(result);
			return ret;
		}
		final String timestamp = jsonObj.getString("timestamp");
		if (timestamp == null || "".equals(timestamp)) {
			LOG.e(LOG_TAG, "timestamp is empty", new NullPointerException());
			ret = false;
            PluginResult result = new PluginResult(PluginResult.Status.ERROR, "timestamp is empty");
            result.setKeepCallback(true);
            currentCallbackContext.sendPluginResult(result);
			return ret;
		}
		final String sign = jsonObj.getString("sign");
		if (sign == null || "".equals(timestamp)) {
			LOG.e(LOG_TAG, "sign is empty", new NullPointerException());
			ret = false;
            PluginResult result = new PluginResult(PluginResult.Status.ERROR, "sign is empty");
            result.setKeepCallback(true);
            currentCallbackContext.sendPluginResult(result);
			return ret;
		}

		//////////////////////
		// 请求微信支付
		//////////////////////
		
		if (!wxAPI.isWXAppInstalled()) {
			LOG.e(LOG_TAG, "Wechat is not installed", new IllegalAccessException());
			ret = false;
            PluginResult result = new PluginResult(PluginResult.Status.ERROR, "Wechat is not installed");
            result.setKeepCallback(true);
            currentCallbackContext.sendPluginResult(result);
			return ret;
		}

		LOG.d(LOG_TAG, "Wechat#payment.end");

		cordova.getThreadPool().execute(new Runnable() {
			public void run() {
				PayReq payreq = new PayReq();

				payreq.appId = appid;
				payreq.partnerId = partnerid;
				payreq.prepayId = prepayid;
				payreq.packageValue = packageValue;
				payreq.nonceStr = noncestr;
				payreq.timeStamp = timestamp;
				payreq.sign = sign;

				boolean ret = wxAPI.sendReq(payreq);
				if (!ret) {
		            PluginResult result = new PluginResult(PluginResult.Status.ERROR, "unifiedorder requst failured.");
		            result.setKeepCallback(true);
		            currentCallbackContext.sendPluginResult(result);
				}
			}
		});
		return true;
	}

    public static String getAPPID(JSONArray args)  throws JSONException{
        String result = null;

		JSONObject jsonObj = args.getJSONObject(0);
        try {
        	result = jsonObj.getString("appid");
            //result = getObjectFromArray(ary, 0).getString("appid");
        } catch (JSONException e) {

        }
        return result;
    }

	protected IWXAPI getWXAPI() {
		if (wxAPI == null) {
			wxAPI = WXAPIFactory.createWXAPI(webView.getContext(), APPID, true);
		}

		return wxAPI;
	}
	
	protected boolean sendAuthRequest(JSONArray args, CallbackContext callbackContext) throws JSONException
	{
		final SendAuth.Req req = new SendAuth.Req();
		req.state = "wechat_auth";
		

		JSONObject jsonObj = args.getJSONObject(0);
		// check if # of arguments is correct
		if (jsonObj.length() > 1) {
			try {
				req.scope = jsonObj.getString("scope");
			} catch (Exception e) {
				Log.e(Wechat.class.getName()
						, "sendAuthRequest parameter parsing failure"
						, e);
			}
		}
		else
		{
			req.scope = "snsapi_userinfo";
		}
		wxAPI.sendReq(req);
		currentCallbackContext = callbackContext;
		
		return true;
	}

	    protected boolean share(JSONArray args, CallbackContext callbackContext)
            throws JSONException, NullPointerException {
        // check if installed
        if (!wxAPI.isWXAppInstalled()) {
            callbackContext.error(ERR_WECHAT_NOT_INSTALLED);
            return false;
        }

        JSONObject params = args.getJSONObject(0);

        if (params == null) {
            callbackContext.error(ERR_INVALID_OPTIONS);
            return false;
        }

        SendMessageToWX.Req request = new SendMessageToWX.Req();

        request.transaction = String.valueOf(System.currentTimeMillis());

        int paramScene = params.getInt("scene");
        switch (paramScene) {
            case SCENE_SESSION:
                request.scene = SendMessageToWX.Req.WXSceneSession;
                break;
            case SCENE_TIMELINE:
            default:
                request.scene = SendMessageToWX.Req.WXSceneTimeline;
                break;
        }

        WXMediaMessage message = null;

        String text = null;
        JSONObject messageOptions = null;

        if (!params.isNull("text")) {
            text = params.getString("text");
        }

        if (!params.isNull("message")) {
            messageOptions = params.getJSONObject("message");
        }

        if (messageOptions != null) {
            String url = null;
            String data = null;

            if (!messageOptions.isNull("url")) {
                url = messageOptions.getString("url");
            }

            if (!messageOptions.isNull("data")) {
                data = messageOptions.getString("data");
            }

            int type = SHARE_TYPE_WEBPAGE;

            if (!messageOptions.isNull("type")) {
                type = messageOptions.getInt("type");
            }

            switch (type) {
                case SHARE_TYPE_APP:
                    break;
                case SHARE_TYPE_EMOTION:
                    break;
                case SHARE_TYPE_FILE:
                    break;
                case SHARE_TYPE_IMAGE:
                    WXImageObject imageObject = new WXImageObject();
                    if (url != null) {
                        imageObject.imageUrl = url;
                    } else if (data != null) {
                        imageObject.imageData = Base64.decode(data, Base64.DEFAULT);
                    } else {
                        callbackContext.error(ERR_INVALID_OPTIONS);
                        return false;
                    }
                    message = new WXMediaMessage(imageObject);
                    break;
                case SHARE_TYPE_MUSIC:
                    break;
                case SHARE_TYPE_VIDEO:
                    break;
                case SHARE_TYPE_WEBPAGE:
                default:
                    WXWebpageObject webpageObject = new WXWebpageObject();
                    webpageObject.webpageUrl = url;
                    message = new WXMediaMessage(webpageObject);
                    break;
            }

            if (message == null) {
                callbackContext.error(ERR_UNSUPPORTED_MEDIA_TYPE);
                return false;
            }

            if (!messageOptions.isNull("title")) {
                message.title = messageOptions.getString("title");
            }

            if (!messageOptions.isNull("description")) {
                message.description = messageOptions.getString("description");
            }

            if (!messageOptions.isNull("thumbData")) {
                String thumbData = messageOptions.getString("thumbData");
                message.thumbData = Base64.decode(thumbData, Base64.DEFAULT);
            }
        } else if (text != null) {
            WXTextObject textObject = new WXTextObject();
            textObject.text = text;

            message = new WXMediaMessage(textObject);
            message.description = text;
        } else {
            callbackContext.error(ERR_INVALID_OPTIONS);
            return false;
        }

        request.message = message;

        try {
            boolean success = wxAPI.sendReq(request);
            if (!success) {
                callbackContext.error(ERR_UNKNOWN);
                return false;
            }
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
            return false;
        }

        currentCallbackContext = callbackContext;
        return true;
    }

	protected boolean isInstalled(CallbackContext callbackContext){
		if (!wxAPI.isWXAppInstalled()) {
			callbackContext.error(ERROR_WX_NOT_INSTALLED);
			return false;
		}else{
			callbackContext.success("true");
		}
		currentCallbackContext = callbackContext;
		return true;
	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}

}
