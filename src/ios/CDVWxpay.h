#import <Cordova/CDV.h>
#import "WXApi.h"
#import "WXApiObject.h"

@interface CDVWxpay:CDVPlugin <WXApiDelegate>

enum CDVWeChatShareType {
    CDVWeChatShareTypeApp = 1,
    CDVWeChatShareTypeEmotion,
    CDVWeChatShareTypeFile,
    CDVWeChatShareTypeImage,
    CDVWeChatShareTypeMusic,
    CDVWeChatShareTypeVideo,
    CDVWeChatShareTypeWebpage
};

@property (nonatomic, strong) NSString *currentCallbackId;
@property (nonatomic, strong) NSString *wechatAppId;

- (void)payment:(CDVInvokedUrlCommand *)command;
- (void)registerApp:(NSString *)wechatAppId;
- (void)sendAuthRequest:(CDVInvokedUrlCommand *)command;
- (void)share:(CDVInvokedUrlCommand *)command;
- (void)isWXAppInstalled:(CDVInvokedUrlCommand *)command;

@end