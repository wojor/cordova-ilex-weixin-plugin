var exec = require('cordova/exec');

var Wechat = {
  Scene: {
        SESSION:  0, // 朋友圈
        TIMELINE: 1, // 聊天界面
        FAVORITE: 2  // 收藏
    },
    Type: {
        APP:     1,
        EMOTION: 2,
        FILE:    3,
        IMAGE:   4,
        MUSIC:   5,
        VIDEO:   6,
        WEBPAGE: 7
    },
  payment: function(json, successFn, failureFn) {
    exec(successFn, failureFn, 'Wechat', 'payment', [json]);
  },
    /**
     * json => array(appid)
    */
    isInstalled: function (json, onSuccess, onError) {
        exec(onSuccess, onError, "Wechat", "isWXAppInstalled", [json]);
    },
    /**
     * json => array(appid, message)
    */
    share: function (json, onSuccess, onError) {
        exec(onSuccess, onError, "Wechat", "share", [json]);
    },
    /**
     * json => array(appid, scope, state)
    */
    auth: function (json, onSuccess, onError) {
        return exec(onSuccess, onError, "Wechat", "sendAuthRequest", [json]);

    }
}

module.exports = Wechat;
