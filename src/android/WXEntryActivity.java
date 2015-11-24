package __PACKAGE_NAME__;

import org.apache.cordova.PluginResult;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.sdk.modelmsg.SendAuth;
import org.json.JSONException;
import com.ilex.plugins.weixin.Wechat;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler{
  
  private static final String LOG_TAG = WXEntryActivity.class.getSimpleName();
  
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Wechat.wxAPI.handleIntent(getIntent(), this);
    }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);
    Wechat.wxAPI.handleIntent(intent, this);
  }

  @Override
  public void onReq(BaseReq req) {
    finish();
  }

  @Override
  public void onResp(BaseResp resp) {
    Log.d(LOG_TAG, "onPayFinish, errCode = " + resp.errCode);

    switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                switch(resp.getType())
                {
                 case ConstantsAPI.COMMAND_SENDAUTH:
                    auth(resp);
                    break;
                 case ConstantsAPI.COMMAND_PAY_BY_WX:
                    auth(resp);
                    break;
                 default:
                    Wechat.currentCallbackContext.success();
                    break;
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                Wechat.currentCallbackContext.error(Wechat.ERR_USER_CANCEL);
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                Wechat.currentCallbackContext.error(Wechat.ERR_AUTH_DENIED);
                break;
            case BaseResp.ErrCode.ERR_SENT_FAILED:
                Wechat.currentCallbackContext.error(Wechat.ERR_SENT_FAILED);
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                Wechat.currentCallbackContext.error(Wechat.ERR_UNSUPPORT);
                break;
            case BaseResp.ErrCode.ERR_COMM:
                Wechat.currentCallbackContext.error(Wechat.ERR_COMM);
                break;
            default:
                Wechat.currentCallbackContext.error(Wechat.ERR_UNKNOWN);
                break;
        }

    finish();
  }

  private void pay(BaseResp resp){
    JSONObject json = new JSONObject();
    
    try {
      if (resp.errStr != null && resp.errStr.length() >= 0) {
        json.put("errStr", resp.errStr);
      } else {
        json.put("errStr", "");
      }
      json.put("code", resp.errCode);
    } catch (Exception e) {
      Log.e(LOG_TAG, e.getMessage(), e);
    }

      PluginResult result = null;
      if (0 == resp.errCode) {
        result = new PluginResult(PluginResult.Status.OK, json.toString());
      } else {
        result = new PluginResult(PluginResult.Status.ERROR, json.toString());
      }
      result.setKeepCallback(true);
      Wechat.currentCallbackContext.sendPluginResult(result);
  }

    private void auth(BaseResp resp) {
        SendAuth.Resp res = ((SendAuth.Resp) resp);
        Log.i("WEChat", "AuthResp " + res);
        JSONObject response = new JSONObject();
        try {
            response.put("code",  res.code);
            response.put("state",  res.state);
            response.put("country",  res.country);
            response.put("lang",  res.lang);
        } catch (JSONException e) {
            Log.e(WXEntryActivity.class.getName()
                    , "auth response failure"
                    , e);
        }
        Wechat.currentCallbackContext.success(response);
    }
}
