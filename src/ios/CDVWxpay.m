//
//  CDVWxpay.m
//  cordova-plugin-wxpay
//
//  Created by tong.wu on 06/30/15.
//
//

#import "CDVWxpay.h"

@implementation CDVWxpay

#pragma mark "API"
NSString *weixinCallbackId;

NSString* WECHAT_APPID_KEY = @"wechatappid";
NSString* ERR_WECHAT_NOT_INSTALLED = @"ERR_WECHAT_NOT_INSTALLED";
NSString* ERR_INVALID_OPTIONS = @"ERR_INVALID_OPTIONS";
NSString* ERR_UNSUPPORTED_MEDIA_TYPE = @"ERR_UNSUPPORTED_MEDIA_TYPE";
NSString* ERR_USER_CANCEL = @"ERR_USER_CANCEL";
NSString* ERR_AUTH_DENIED = @"ERR_AUTH_DENIED";
NSString* ERR_SENT_FAILED = @"ERR_SENT_FAILED";
NSString* ERR_COMM = @"ERR_COMM";
NSString* ERR_UNSUPPORT = @"ERR_UNSUPPORT";
NSString* ERR_UNKNOWN = @"ERR_UNKNOWN";
NSString* NO_RESULT = @"NO_RESULT";

const int SCENE_CHOSEN_BY_USER = 0;
const int SCENE_SESSION = 1;
const int SCENE_TIMELINE = 2;


- (void)pluginInitialize {
    self.wechatAppId = [[self.commandDelegate settings] objectForKey:WECHAT_APPID_KEY];
    [WXApi registerApp: self.wechatAppId];
}

- (void)payment:(CDVInvokedUrlCommand *)command
{
    [self.commandDelegate runInBackground:^{
        // check arguments
        NSDictionary *params = [command.arguments objectAtIndex:0];
        if (!params)
        {
            [self failWithCallbackID:command.callbackId withMessage:@"参数格式错误"];
            return ;
        }
        
        NSString *appid = nil;
        NSString *noncestr = nil;
        NSString *package = nil;
        NSString *partnerid = nil;
        NSString *prepayid = nil;
        NSString *timestamp = nil;
        NSString *sign = nil;
        
        // check the params
        if (![params objectForKey:@"appid"])
        {
            [self failWithCallbackID:command.callbackId withMessage:@"appid参数错误"];
            return ;
        }
        appid = [params objectForKey:@"appid"];

        if (![params objectForKey:@"noncestr"])
        {
            [self failWithCallbackID:command.callbackId withMessage:@"noncestr参数错误"];
            return ;
        }
        noncestr = [params objectForKey:@"noncestr"];

        if (![params objectForKey:@"package"])
        {
            [self failWithCallbackID:command.callbackId withMessage:@"package参数错误"];
            return ;
        }
        package = [params objectForKey:@"package"];

        if (![params objectForKey:@"partnerid"])
        {
            [self failWithCallbackID:command.callbackId withMessage:@"partnerid参数错误"];
            return ;
        }
        partnerid = [params objectForKey:@"partnerid"];

        if (![params objectForKey:@"prepayid"])
        {
            [self failWithCallbackID:command.callbackId withMessage:@"prepayid参数错误"];
            return ;
        }
        prepayid = [params objectForKey:@"prepayid"];

        if (![params objectForKey:@"timestamp"])
        {
            [self failWithCallbackID:command.callbackId withMessage:@"timestamp参数错误"];
            return ;
        }
        timestamp = [params objectForKey:@"timestamp"];

        if (![params objectForKey:@"sign"])
        {
            [self failWithCallbackID:command.callbackId withMessage:@"sign参数错误"];
            return ;
        }
        sign = [params objectForKey:@"sign"];

        // 向微信注册
        [WXApi registerApp:appid];
        
        if (![WXApi isWXAppInstalled]) {
            [self failWithCallbackID:command.callbackId withMessage:@"未安装微信"];
            return;
        }
    
        PayReq *req = [[PayReq alloc] init];
        req.openID = appid;
        req.partnerId = partnerid;
        req.prepayId = prepayid;
        req.nonceStr = noncestr;
        req.timeStamp = timestamp.intValue;
        req.package = package;
        req.sign = sign;
        
        //[WXApi sendReq:req];
        //日志输出
        NSLog(@"\nappid=%@\npartid=%@\nprepayid=%@\nnoncestr=%@\ntimestamp=%ld\npackage=%@\nsign=%@",req.openID,req.partnerId,req.prepayId,req.nonceStr,(long)req.timeStamp,req.package,req.sign );

        // save the callback id
        self.currentCallbackId = command.callbackId;
        
        CDVPluginResult *commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"调起成功"];
        
        [self.commandDelegate sendPluginResult:commandResult callbackId:command.callbackId];
    }];
}

- (void)registerApp:(NSString *)wechatAppId
{
    
    NSLog(@"Register wechat app: %@", wechatAppId);
}

#pragma mark "WXApiDelegate"

-(void)sendAuthRequest:(CDVInvokedUrlCommand *)command
{
    
    [self.commandDelegate runInBackground:^{
        // check arguments
        NSDictionary *params = [command.arguments objectAtIndex:0];
        if (!params)
        {
            [self failWithCallbackID:command.callbackId withMessage:@"参数格式错误"];
            return ;
        }
        
        // NSString *appid = nil;
        
        // // check the params
        // if (![params objectForKey:@"appid"])
        // {
        //     [self failWithCallbackID:command.callbackId withMessage:@"appid参数错误"];
        //     return ;
        // }
        // appid = [params objectForKey:@"appid"];
        
        // self.wechatAppId = appid;
        
        //[WXApi registerApp:appid];
        
        
        weixinCallbackId =command.callbackId;
        SendAuthReq* req =[[SendAuthReq alloc ] init];
        req.scope = @ "snsapi_userinfo,snsapi_base" ;
        req.state = @ "0744" ;
        //第三方向微信终端发送一个SendAuthReq消息结构
        [WXApi sendReq:req];

        
        }];
}

- (void)share:(CDVInvokedUrlCommand*)command {
    CDVPluginResult* result = nil;
    NSDictionary *params = [command.arguments objectAtIndex:0];
    if (!params)
    {
        [self failWithCallbackID:command.callbackId withMessage:@"参数格式错误"];
        return ;
    }
    NSString *appid = nil;
    
    // // check the params
    // if (![params objectForKey:@"appid"])
    // {
    //     [self failWithCallbackID:command.callbackId withMessage:@"appid参数错误"];
    //     return ;
    // }
    // appid = [params objectForKey:@"appid"];
    
    // self.wechatAppId = appid;
    
    
    // // 向微信注册
    // [WXApi registerApp:appid];
    
    if (![WXApi isWXAppInstalled]) {
        [self failWithCallbackID:command.callbackId withMessage:@"未安装微信"];
        return;
    }
    
    SendMessageToWXReq* request = [SendMessageToWXReq new];
    
    if ([params objectForKey:@"scene"]) {
        int paramScene = [[params objectForKey:@"scene"] integerValue];
        
        switch (paramScene) {
            case SCENE_SESSION:
                request.scene = WXSceneSession;
                break;
            case SCENE_CHOSEN_BY_USER:
            case SCENE_TIMELINE:
            default:
                request.scene = WXSceneTimeline;
                break;
        }
    } else {
        request.scene = WXSceneTimeline;
    }
    
    NSDictionary* messageOptions = [params objectForKey:@"message"];
    NSString* text = [params objectForKey:@"text"];
    
    if ((id)messageOptions == [NSNull null]) {
        messageOptions = nil;
    }
    if ((id)text == [NSNull null]) {
        text = nil;
    }
    
    if (messageOptions) {
        request.bText = NO;
        
        NSString* url = [messageOptions objectForKey:@"url"];
        NSString* data = [messageOptions objectForKey:@"data"];
        
        if ((id)url == [NSNull null]) {
            url = nil;
        }
        if ((id)data == [NSNull null]) {
            data = nil;
        }
        
        WXMediaMessage* message = [WXMediaMessage message];
        id mediaObject = nil;
        
        int type = [[messageOptions objectForKey:@"type"] integerValue];
        
        if (!type) {
            type = CDVWeChatShareTypeWebpage;
        }
        
        switch (type) {
            case CDVWeChatShareTypeApp:
                break;
            case CDVWeChatShareTypeEmotion:
                break;
            case CDVWeChatShareTypeFile:
                break;
            case CDVWeChatShareTypeImage:
                mediaObject = [WXImageObject object];
                if (url) {
                    ((WXImageObject*)mediaObject).imageUrl = url;
                } else if (data) {
                    ((WXImageObject*)mediaObject).imageData = [self decodeBase64:data];
                } else {
                    result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:ERR_INVALID_OPTIONS];
                    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
                    return ;
                }
                break;
            case CDVWeChatShareTypeMusic:
                break;
            case CDVWeChatShareTypeVideo:
                break;
            case CDVWeChatShareTypeWebpage:
            default:
                mediaObject = [WXWebpageObject object];
                ((WXWebpageObject*)mediaObject).webpageUrl = url;
                break;
        }
        
        if (!mediaObject) {
            result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:ERR_UNSUPPORTED_MEDIA_TYPE];
            [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
            return ;
        }
        
        message.mediaObject = mediaObject;
        
        message.title = [messageOptions objectForKey:@"title"];
        message.description = [messageOptions objectForKey:@"description"];
        
        NSString* thumbData = [messageOptions objectForKey:@"thumbData"];
        
        if ((id)thumbData == [NSNull null]) {
            thumbData = nil;
        }
        
        if (thumbData) {
            message.thumbData = [self decodeBase64:thumbData];
        }
        
        request.message = message;
    } else if (text) {
        request.bText = YES;
        request.text = text;
    } else {
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:ERR_INVALID_OPTIONS];
        [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
        return ;
    }
    
    BOOL success = [WXApi sendReq:request];
    
    if (success) {
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
    } else {
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:ERR_UNKNOWN];
        [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
        return;
    }
    
    self.currentCallbackId = command.callbackId;
}


/**
 * Not implemented
 */
- (void)onReq:(BaseReq *)req
{
    NSLog(@"%@", req);
}

- (void)onResp:(BaseResp *)resp
{
    BOOL success = NO;
    NSString *message = @"Unknown";
    
    switch (resp.errCode)
    {
        case WXSuccess:
            success = YES;
            break;
            
        case WXErrCodeCommon:
            message = @"普通错误类型";
            break;
            
        case WXErrCodeUserCancel:
            message = @"用户点击取消并返回";
            break;
            
        case WXErrCodeSentFail:
            message = @"发送失败";
            break;
            
        case WXErrCodeAuthDeny:
            message = @"授权失败";
            break;
            
        case WXErrCodeUnsupport:
            message = @"微信不支持";
            break;
    }
    
    if (success)
    {
        CDVPluginResult *commandResult = nil;
        if ([resp isKindOfClass:[PayResp class]])
        {
            NSString *strMsg = [NSString stringWithFormat:@"支付结果：retcode = %d, retstr = %@", resp.errCode,resp.errStr];
            
            
            if (resp.errCode == 0)
            {
                commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:strMsg];
            }
            else
            {
                commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:strMsg];
            }
            
            [self.commandDelegate sendPluginResult:commandResult callbackId:self.currentCallbackId];
        }
        else if([resp isKindOfClass:[SendAuthResp class]])
        {
            SendAuthResp *r = ((SendAuthResp *)resp);
            NSString *strMsg = [NSString stringWithFormat:@"结果：retcode = %d, retstr = %@", resp.errCode,resp.errStr];
            
            if (resp.errCode == 0)
            {
                strMsg = [NSString stringWithFormat:@"{\"retcode\" : \"%d\", \"code\":\"%@\"}", resp.errCode, r.code];
                commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:strMsg];
            }
            else
            {
                commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:
                                 strMsg];
            }
            self.currentCallbackId = weixinCallbackId;
            [self.commandDelegate sendPluginResult:commandResult callbackId:self.currentCallbackId];
        }
        else  if([resp isKindOfClass:[SendMessageToWXResp class]]) {
            commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:
                             message];
            [self.commandDelegate sendPluginResult:commandResult callbackId:self.currentCallbackId];
        }
        else
        {
            NSLog(@"回调类型不匹配");

            [self successWithCallbackID:self.currentCallbackId];
        }
    }
    else
    {
        [self failWithCallbackID:self.currentCallbackId withMessage:message];
    }
    
    self.currentCallbackId = nil;
}

#pragma mark "CDVPlugin Overrides"


- (NSData*)decodeBase64:(NSString*)base64String {
    NSString* dataUrl =[NSString stringWithFormat:@"data:application/octet-stream;base64,%@", base64String];
    NSURL* url = [NSURL URLWithString: dataUrl];
    return [NSData dataWithContentsOfURL:url];
}

- (void)handleOpenURL:(NSNotification *)notification
{
    NSURL* url = [notification object];
    
    if ([url isKindOfClass:[NSURL class]] && [url.scheme isEqualToString:self.wechatAppId])
    {
        [WXApi handleOpenURL:url delegate:self];
    }
}

#pragma mark "Private methods"

- (NSData *)getNSDataFromURL:(NSString *)url
{
    NSData *data = nil;
    
    if ([url hasPrefix:@"http://"] || [url hasPrefix:@"https://"])
    {
        data = [NSData dataWithContentsOfURL:[NSURL URLWithString:url]];
    }else if([url containsString:@"temp:"]){
        url =  [NSTemporaryDirectory() stringByAppendingPathComponent:[url componentsSeparatedByString:@"temp:"][1]];
        data = [NSData dataWithContentsOfFile:url];
    }
    else
    {
        // local file
        url = [[NSBundle mainBundle] pathForResource:[url stringByDeletingPathExtension] ofType:[url pathExtension]];
        data = [NSData dataWithContentsOfFile:url];
    }
    
    return data;
}

- (UIImage *)getUIImageFromURL:(NSString *)url
{
    NSData *data = [self getNSDataFromURL:url];
    return [UIImage imageWithData:data];
}

- (void)successWithCallbackID:(NSString *)callbackID
{
    [self successWithCallbackID:callbackID withMessage:@"OK"];
}

- (void)successWithCallbackID:(NSString *)callbackID withMessage:(NSString *)message
{
    CDVPluginResult *commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:message];
    [self.commandDelegate sendPluginResult:commandResult callbackId:callbackID];
}

- (void)failWithCallbackID:(NSString *)callbackID withError:(NSError *)error
{
    [self failWithCallbackID:callbackID withMessage:[error localizedDescription]];
}

- (void)failWithCallbackID:(NSString *)callbackID withMessage:(NSString *)message
{
    CDVPluginResult *commandResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:message];
    [self.commandDelegate sendPluginResult:commandResult callbackId:callbackID];
}

@end