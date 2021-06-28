/***************************************************************************
 SSO Controller.
 for CA siteminder's browser session control
 ***************************************************************************/

/**
 *	global variables but FIX VALUE (plz never change)
 */
const DEBUG = false; // alert or don't alert
const LOGOUT_SITE = 'LOGOUT_SITE'; // never change
const SMSESSION = 'SMSESSION'; // never change
const SMSSO_ERROR_CODE = 'smevent'; // never change
const SMSSO_DISABLED_CODE = 'smdisabled'; // never change
const SMSSO_PWDFAILCNT = 'pwd_fail_cnt'; // never change
const SMSSO_PWD_USER = 'sm_hli_user'; // never change
const SMSSO_USER_IP = 'sm_user_ip'; // never change
const SMSSO_PWCHG_PAGE = 'http://10.10.9.35:9090/im/hanwha/profile/password.do'; // password change redirection
const SMSSO_PWCHG_PAGE_EXT = 'http://sso.hanwhalife.com:9080/IDM/im/hanwha/profile/password.do'; // external password change redirection
const SMSSO_PWCHG_PAGE2 = 'http://10.10.9.35:9090/im/hanwha/profile/password.do?mode=changeNewPasswordNK'; // nk password change popup
const SMSSO_PWCHG_PAGE2_EXT = 'http://sso.hanwhalife.com:9080/IDM/im/hanwha/profile/password.do?mode=changeNewPasswordNK'; // external nk password change popup
const SMSSO_LOGIN_FCC = 'http://eptest.hanwhalife.com:6080/siteminderagent/forms/login.fcc'; // sso login web url

/**
 *	global variables for easy setting. Customize values.
 */

const SYSTEM_NAME = 'KWC'; // system name
const SYSTEM_DOMAIN = 'http://kubewatcher.hanwhalife.com:8080'; // system domain
const SYSTEM_ROOT = SYSTEM_DOMAIN + ''; // system root in browser
const SYSTEM_INDEX = SYSTEM_ROOT + ''; // system index page
const SYSTEM_LOGIN_FORM = SYSTEM_ROOT + '/login'; // system default login page
const SYSTEM_LOGIN_PROC = SYSTEM_ROOT + '/login'; // system default login procedure
const SYSTEM_LOGOUT = SYSTEM_ROOT + '/logout'; // system logout page

const SMSSO_COOKIE_DOMAIN = '.hanwhalife.com'; // sso cookie check domain
const SMSSO_SMAGENTNAME = 'r12_sso_dev_agent'; // siteminder agent name
const SMSSO_PROXY_ROOT = 'http://eptest.hanwhalife.com:6080/KWC'; // sso proxy web server mapping (proxy type only)
const SMSSO_ROOT = SYSTEM_ROOT + ''; // sso context uri
const SMSSO_INDEX = SYSTEM_ROOT + ''; // index page for sso
const SMSSO_LOGIN_FORM = SYSTEM_ROOT + '/login'; // login form for siteminder sso
const SMSSO_PROTECTED_PAGE = SMSSO_PROXY_ROOT + '/sso/protected-page'; // login target page for siteminder sso
const SMSSO_LOGIN_PROCEDURE = SYSTEM_ROOT + '/login'; // login procedure(session check & setting)
const SMSSO_LOGOUT = SMSSO_ROOT  + '/logout'; // logout and siteminder logout

// check url
function checkURL(){
    if(top.location.href.indexOf(SYSTEM_DOMAIN) >= 0){
        return true;
    }else{
        return false;
    }
}

// check session for redirect
function checkSession(){
    var redirect_url = "";

    debug("start");
    if(checkRedirectFromLogoutPage()){
        debug("1");
        if(!isLoginPage()){
            debug("1-1");
            redirect_url =  SMSSO_LOGIN_FORM;
        }
    }else{
        debug("2");
        var smcookie = getCookie(SMSESSION);
        smcookie = (smcookie == null) ? "" : smcookie;
        if((smcookie != "" && smcookie != "LOGGEDOFF") && getRequestQueryString("TYPE") != "33554432" ){
            redirect_url = SMSSO_PROTECTED_PAGE;
        }else{
            debug("2-2");
            if(!isLoginPage()){
                redirect_url = SMSSO_LOGIN_FORM;
            }
        }
    }
    return redirect_url;
}

// check SSO Login Failed Error Code
function checkSmError(){
    var ssoerrcd = "";
    ssoerrcd = getCookie(SMSSO_ERROR_CODE);
    ssoerrcd = (ssoerrcd == null) ? "" : ssoerrcd;

    var smdisablecd = "";
    smdisablecd = getCookie(SMSSO_DISABLED_CODE);
    smdisablecd = (smdisablecd == null) ? "" : smdisablecd;

    if (ssoerrcd == "40005" && smdisablecd == "10") {
        ssoerrcd = "40009";
    }
    setCookie(SMSSO_ERROR_CODE, "", "/", 0, SMSSO_COOKIE_DOMAIN);
    setCookie(SMSSO_DISABLED_CODE, "", "/", 0, SMSSO_COOKIE_DOMAIN);

    return ssoerrcd;
}

// processing for SSO Login Failed Event
function procSmEvent(ssoerrcd){
    switch (ssoerrcd) {
        case "40002":	//ID Error
            alert("아이디 또는 비밀번호가 틀렸습니다.\n비밀번호가 5회이상 틀렸을 경우 접근이 차단됩니다.");
            break;
        case "40003":	//PWD Error
            var pwdfailcnt = getCookie(SMSSO_PWDFAILCNT);
            pwdfailcnt = (pwdfailcnt == null) ? "0" : pwdfailcnt;
            setCookie(SMSSO_PWDFAILCNT, "", "/", 0, SMSSO_COOKIE_DOMAIN);
            setCookie(SMSSO_USER_IP, "", "/", 0, SMSSO_COOKIE_DOMAIN);
            alert("아이디 또는 비밀번호가 틀렸습니다.\n비밀번호가 5회이상 틀렸을 경우 접근이 차단됩니다." + "\n(" + pwdfailcnt + "회 오류)");
            break;
        case "40005":   //User Lockout
            alert("패스워드 인증실패가 규정횟수를 초과하였습니다.\n로그인 화면에서 '비밀번호 분실시 클릭하세요'\n아이콘을 클릭하여 안내를 받으시기 바랍니다.");
            break;
        case "40009":   //PWD Expired
            var smuserid = getCookie(SMSSO_PWD_USER);
            smuserid = (smuserid == null) ? "" : smuserid;
            setCookie(SMSSO_PWD_USER, "", "/", 0, SMSSO_COOKIE_DOMAIN);
            setCookie(SMSSO_PWDFAILCNT, "", "/", 0, SMSSO_COOKIE_DOMAIN);
            alert("패스워드 유효기간이 지났습니다.\n패스워드를 변경 해야합니다.");
            if (isInteralUserIP()) {
                location.href = SMSSO_PWCHG_PAGE + "?USER_ID=" + smuserid + "&RETURN_URL=" + SMSSO_LOGIN_FORM;
            } else {
                location.href = SMSSO_PWCHG_PAGE_EXT + "?USER_ID=" + smuserid + "&RETURN_URL=" + SMSSO_LOGIN_FORM;
            }
            break;
        case "40008":   //First Login
            var smuserid = getCookie(SMSSO_PWD_USER);
            smuserid = (smuserid == null) ? "" : smuserid;
            setCookie(SMSSO_PWD_USER, "", "/", 0, SMSSO_COOKIE_DOMAIN);
            setCookie(SMSSO_PWDFAILCNT, "", "/", 0, SMSSO_COOKIE_DOMAIN);
            if (isInteralUserIP()) {
                location.href = SMSSO_PWCHG_PAGE + "?USER_ID=" + smuserid + "&RETURN_URL=" + SMSSO_LOGIN_FORM;
            } else {
                location.href = SMSSO_PWCHG_PAGE_EXT + "?USER_ID=" + smuserid + "&RETURN_URL=" + SMSSO_LOGIN_FORM;
            }
            break;
        case "41000":   //Account Disabled
            alert("로그인 계정이 잠긴 상태 입니다.\n시스템 관리자에게 문의하여 안내를 받으시기 바랍니다.");
            break;
        default:	//Account Disabled
            alert("로그인 계정이 잠긴 상태 입니다.\n시스템 관리자에게 문의하여 안내를 받으시기 바랍니다.");
            break;
    }
}

// logout list check
function checkRedirectFromLogoutPage(){
    var fromLogoutPage = false;
    var logoutSite = LOGOUT_SITE + "_" + SYSTEM_NAME;

    var logoutflag = getCookie(logoutSite);
    logoutflag = (logoutflag == null) ? "" : logoutflag;

    if(logoutflag == "Y"){
        fromLogoutPage = true;
    }

    debug("fromLogoutPage : "+fromLogoutPage);
    return fromLogoutPage;
}

// login page check
function isLoginPage() {
    var rtnValue = false;
    debug(document.location.href);
    if(document.location.href.indexOf(SMSSO_LOGIN_FORM)>=0){
        rtnValue = true;
    }else{
        rtnValue = false;
    }
    debug("isLoginPage() : " + rtnValue);
    return rtnValue;
}

// get specific querystring value
function getRequestQueryString(valuename)
{
    var rtnval = "";
    var nowAddress = unescape(location.href);
    var parameters = (nowAddress.slice(nowAddress.indexOf("?")+1,nowAddress.length)).split("&");

    for(var i = 0 ; i < parameters.length ; i++){
        var varName = parameters[i].split("=")[0];
        if(varName.toUpperCase() == valuename.toUpperCase())
        {
            rtnval = parameters[i].split("=")[1];
            break;
        }
    }
    return rtnval;
}

// check for user ip internal or external
function isInteralUserIP() {
    var rtnValue = true;

    var smuserip = getCookie(SMSSO_USER_IP);
    smuserip = (smuserip == null) ? "" : smuserip;

    if (smuserip.length > 2) {
        if(smuserip.substring(0,3) != "10."){
            rtnValue = false;
        }
    }
    setCookie(SMSSO_USER_IP, "", "/", 0, SMSSO_COOKIE_DOMAIN);
    return rtnValue;
}

// popup location in screen center
function openCenterPop(sw, sh, uri) {
    cw = screen.availWidth; //화면 너비
    ch = screen.availHeight; //화면 높이
    //sw = 500; //띄울 창의 너비
    //sh = 520; //띄울 창의 높이

    ml = (cw - sw) / 2; //가운데 띄우기위한 창의 x위치
    mt = (ch - sh) / 2; //가운데 띄우기위한 창의 y위치

    var poppwdwin = window.open(uri, 'newpwdchg', 'width=' + sw + ',height=' + sh + ',top=' + mt + ',left=' + ml + ',scrollbars=no,status=no,toolbar=no,resizable=0,location=no,menu=no');
    poppwdwin.focus();
}

// confirm box button value rename
/*
function window.confirm(str) {
	execScript('n = msgbox("'+str+'","4132")', "vbscript");
	return(n==6);
}
*/

// invoke this function :: window (x) button click
function closeSSOClearYN(){
    var strmsg = "*중요알림*  \
현재 사용한 PC가 본인의 PC가 아닌 경우,  \
반드시 종료 버튼을 클릭하여 로그인 정보를 모두 삭제하시기 바랍니다.  \
감사합니다.  \
로그인 정보를 모두 삭제 하시겠습니까?";
    if (confirm(strmsg)) {
        setCookie(SMSESSION, "LOGGEDOFF", "/", 1, SMSSO_COOKIE_DOMAIN);
    }
    else {
        // nothing to do.
    }
}

// invoke this function :: logout button click
function logoutSSOClearYN(){
    var strmsg = "*중요알림*  \
로그아웃은 현재 시스템만 로그아웃 됩니다.  \
현재 사용한 PC가 본인의 PC가 아닌 경우,  \
반드시 종료 버튼을 클릭하여 로그인 정보를 모두 삭제하시기 바랍니다.  \
감사합니다.  \
로그인 정보를 모두 삭제 하시겠습니까?";
    if (confirm(strmsg)) {
        setCookie(SMSESSION, "LOGGEDOFF", "/", 1, SMSSO_COOKIE_DOMAIN);
    }
    else {
        addLogoutSite();
    }
}

// delete SSO Session Cookie
function clearSSOSession(){
    setCookie(SMSESSION, "LOGGEDOFF", "/", 1, SMSSO_COOKIE_DOMAIN);
}

// set to logout
function addLogoutSite(){
    debug(LOGOUT_SITE + "_" + SYSTEM_NAME);
    var logoutSite = LOGOUT_SITE + "_" + SYSTEM_NAME;
    setCookie(logoutSite, "Y", "/", 1, SMSSO_COOKIE_DOMAIN);
}

// delete logout information
function deleteLogoutSite(){
    debug("deleteLogoutSite_before :"+ getCookie(LOGOUT_SITE + "_" + SYSTEM_NAME));
    var logoutSite = LOGOUT_SITE + "_" + SYSTEM_NAME;
    var logoutflag = getCookie(logoutSite);
    logoutflag = (logoutflag == null) ? "" : logoutflag;

    if(logoutflag == "Y"){
        setCookie(logoutSite, "", "/", 0, SMSSO_COOKIE_DOMAIN);
    }
}

// proxy url check
function checkSSOProxy() {
    var rtnValue = false;
    debug(document.location.href);
    if(document.location.href.indexOf(SMSSO_PROXY_ROOT)>=0){
        rtnValue = true;
    }else{
        rtnValue = false;
    }
    debug("checkSSOProxy() : " + rtnValue);
    return rtnValue;
}

// get header cookie
function getCookie(cookieName) {
    var search = cookieName + "=";
    var cookie = document.cookie;
    if (cookie.length > 0) {
        startIndex = cookie.indexOf(cookieName);
        if (startIndex != -1) {
            startIndex += cookieName.length;
            endIndex = cookie.indexOf(";", startIndex);
            if (endIndex == -1) {
                endIndex = cookie.length;
            }
            return cookie.substring(startIndex + 1, endIndex);
        } else {
            return null;
        }
    } else {
        return null;
    }
}

// set header cookie
function setCookie( name, value, path, expiredays, domain)
{
    var todayDate = new Date();
    todayDate.setDate( todayDate.getDate() + expiredays );
    document.cookie = name + "=" + escape( value ) + "; path=" + path + "; expires=" + todayDate.toGMTString() + "; domain=" + domain + ";"
}

// debug message display
function debug(string) {
    if (DEBUG) {
        alert("[debug]\n\n" + string);
    }
}
