package common.modules.network

import android.app.Activity
import common.modules.network.data.NetworkErrorConst.Companion.NETWORK_ERROR_CUSTOM
import common.modules.network.data.NetworkResponseConst.Companion.NETWORK_RESPONSE
import common.modules.network.data.NetworkResponseConst.Companion.NETWORK_RESPONSE_ACTION_FAILURE_CODE
import common.modules.network.data.NetworkResponseConst.Companion.NETWORK_RESPONSE_ACTION_FAILURE_REASON
import common.modules.network.data.NetworkResponseConst.Companion.NETWORK_RESPONSE_ACTION_RESULT
import common.modules.network.data.NetworkResponseConst.Companion.NETWORK_RESPONSE_ACTION_SUCCESS_MESSAGE
import common.modules.network.data.NetworkResponseConst.Companion.NETWORK_RESPONSE_CONTENT
import common.modules.network.data.NetworkResponseConst.Companion.NETWORK_RESPONSE_EMPTY
import common.modules.network.data.NetworkResponseConst.Companion.NETWORK_RESPONSE_SUCCESS
import okhttp3.Response
import org.json.JSONObject

class NetworkResParser {
    companion object {
        // 통신 결과 파싱 함수
        fun resParser(act: Activity, networkResponse: Response?, onSuccess: (msg: String, data: String) -> Unit, onFailure: (code: String, msg: String) -> Unit) {
            // 파싱 대상이 없는 경우
            if (networkResponse == null) {
                onFailure(NETWORK_ERROR_CUSTOM, act.getString(R.string.network_error_unable_server_msg))
                return
            }

            networkResponse.body?.run {
                kotlin.runCatching {
                    // post(등록)의 경우 201 반환.
                    when (networkResponse.code) {
                        201 -> {
                            onSuccess(NETWORK_RESPONSE_EMPTY, NETWORK_RESPONSE_EMPTY)
                            return
                        }
                        400 -> {
                            onFailure(NETWORK_RESPONSE_EMPTY, act.getString(R.string.network_error_parameter))
                            return
                        }
                        500 -> {
                            onFailure(NETWORK_RESPONSE_EMPTY, act.getString(R.string.network_error_server))
                            return
                        }
                    }
                    val bodyObject = JSONObject(string())

                    // response 존재할 경우
                    if (bodyObject.has(NETWORK_RESPONSE)) {
                        val responseObject = bodyObject.getJSONObject(NETWORK_RESPONSE)

                        // action_result 존재할 경우
                        if (responseObject.has(NETWORK_RESPONSE_ACTION_RESULT)) {
                            val result = responseObject.getString(NETWORK_RESPONSE_ACTION_RESULT)

                            // 성공시
                            if (result == NETWORK_RESPONSE_SUCCESS) {
                                val successMsg = hasJsonCheck(responseObject, NETWORK_RESPONSE_ACTION_SUCCESS_MESSAGE, NETWORK_RESPONSE_EMPTY)

                                if (bodyObject.has(NETWORK_RESPONSE_CONTENT)) {
                                    onSuccess(successMsg, bodyObject.getString(NETWORK_RESPONSE_CONTENT))
                                } else {
                                    onSuccess(successMsg, NETWORK_RESPONSE_EMPTY)
                                }
                                return
                            } else {
                                // 실패의 경우
                                if (responseObject.has(NETWORK_RESPONSE_ACTION_FAILURE_CODE)) {
                                    val errCode = responseObject.getString(NETWORK_RESPONSE_ACTION_FAILURE_CODE)

                                    if (responseObject.has(NETWORK_RESPONSE_ACTION_FAILURE_REASON)) {
                                        onFailure(errCode, responseObject.getString(NETWORK_RESPONSE_ACTION_FAILURE_REASON))
                                    } else {
                                        onFailure(errCode, NETWORK_RESPONSE_EMPTY)
                                    }
                                } else {
                                    if (responseObject.has(NETWORK_RESPONSE_ACTION_FAILURE_REASON)) {
                                        onFailure(NETWORK_ERROR_CUSTOM, responseObject.getString(NETWORK_RESPONSE_ACTION_FAILURE_REASON))
                                    } else {
                                        onFailure(NETWORK_ERROR_CUSTOM, act.getString(R.string.network_error_action_failure_code_is_null))
                                    }
                                }
                                return
                            }
                        } else {
                            onFailure(NETWORK_ERROR_CUSTOM, act.getString(R.string.network_error_action_result_null))
                        }
                    } else {
                        onFailure(NETWORK_ERROR_CUSTOM, act.getString(R.string.network_error_response_null))
                    }
                }.onFailure {
                    onFailure(NETWORK_ERROR_CUSTOM, act.getString(R.string.network_error_get_data_fail_msg))
                    return
                }
            }?.onFailure {
                onFailure(NETWORK_ERROR_CUSTOM,  act.getString(R.string.network_error_get_data_fail_msg))
            }
        }

        fun hasJsonCheck(objJson: JSONObject, strCheckHas: String?, strDefault: String): String {
            val strValue: String = if (objJson.has(strCheckHas)) {
                // json.get 사용 시 NPE 가능성 높음.
                // optString 사용
                // 2020-10-13
                objJson.optString(strCheckHas)
            } else {
                strDefault
            }
            return strValue
        }
    }
}