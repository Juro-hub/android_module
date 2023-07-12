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
        const val NETWORK_RESPONSE_EMPTY = ""
    }
}

// 네트워크 에러 코드
class NetworkErrorConst {
    companion object {
        const val NETWORK_ERROR_CUSTOM = "E999"             // 앱 종료 에러 코드.
    }
}