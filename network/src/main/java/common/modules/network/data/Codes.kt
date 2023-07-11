package common.modules.network.data

/** 네트워크 응답 관련 상수 */
class NetworkResponseConst {
    companion object {
        // 공용 Response
        const val NETWORK_RESPONSE = "response"
        const val NETWORK_RESPONSE_ACTION_RESULT = "action_result"
        const val NETWORK_RESPONSE_SUCCESS = "success"
        const val NETWORK_RESPONSE_ACTION_SUCCESS_MESSAGE = "action_success_message"
        const val NETWORK_RESPONSE_CONTENT = "content"
        const val NETWORK_RESPONSE_ACTION_FAILURE_CODE = "action_failure_code"
        const val NETWORK_RESPONSE_ACTION_FAILURE_REASON = "action_failure_reason"
        const val NETWORK_RESPONSE_EMPTY =""

        // DeviceSN Response (AppUpdate 관련)
        const val NETWORK_RESPONSE_APP_UPDATE_INFO = "app_update_info"
        const val NETWORK_RESPONSE_APP_UPDATE_STATUS = "app_update_status"

        // DeviceSN Response (WebViewExtraDTO 관련)
        const val NETWORK_RESPONSE_ARGUMENT = "argument"
        const val NETWORK_RESPONSE_BTN_LEFT = "btn_left"
        const val NETWORK_RESPONSE_JS_LEFT = "js_left"
        const val NETWORK_RESPONSE_TITLE = "title"
        const val NETWORK_RESPONSE_BTN_RIGHT = "btn_right"
        const val NETWORK_RESPONSE_JS_RIGHT = "js_right"
        const val NETWORK_RESPONSE_TEMPLATE = "template"
        const val NETWORK_RESPONSE_POST = "post"

        // Intro Response
        const val NETWORK_RESPONSE_INTRO_MESSAGE = "intro_message"
        const val NETWORK_RESPONSE_DISPLAY_INFO = "display_info"
        const val NETWORK_RESPONSE_UID = "uid"
        const val NETWORK_RESPONSE_MESSAGE = "message"
        const val NETWORK_RESPONSE_DISPLAY_DO_NOT_SHOW_AGAIN = "display_donotshowagain"
        const val NETWORK_RESPONSE_DISPLAY_CLOSE = "display_close"
        const val NETWORK_RESPONSE_DISPLAY_VIEW = "display_view"
        const val NETWORK_RESPONSE_LINK_INFO = "link_info"
        const val NETWORK_RESPONSE_MARKET_URL = "market_url"
        const val NETWORK_RESPONSE_FUNCTION = "function"
    }
}

/** 네트워크 요청 관련 상수 */
class NetworkRequestConst {
    companion object {
        // DEVICE MESSAGE
        const val NETWORK_REQUEST_OPERATOR_NAME = "network_operator_name"
        const val NETWORK_REQUEST_MD5_CHECKSUM = "md5_checksum"
        const val NETWORK_REQUEST_FIREBASE_REFERRER = "firebase_referrer"

        // PUSH MESSAGE
        const val NETWORK_REQUEST_PUSH_DEVICE_ID = "push_device_id"

        // INTRO MESSAGE
        const val NETWORK_REQUEST_DEVICE_MODEL_NAME = "device_model_name"
        const val NETWORK_REQUEST_DEVICE_OS = "os"
        const val NETWORK_REQUEST_CONNECTION_TYPE = "connection_type"
        const val NETWORK_REQUEST_DEVICE_SN_PARAMETER = "device_sn_permanent"
    }
}

// 네트워크 에러 코드
class NetworkErrorConst {
    companion object {
        const val NETWORK_ERROR_CUSTOM = "E999"             // 앱 종료 에러 코드.
    }
}