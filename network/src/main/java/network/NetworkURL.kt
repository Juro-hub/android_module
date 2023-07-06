package network

import common.modules.network.BuildConfig
import network.data.AppURLType

/** 네트워크 관련 URL 모음 */
class NetworkURL {
    companion object {
        fun getSecurityUrl(): String {
            return when (BuildConfig.serverType) {
                // 개발 BASE 주소
                AppURLType.DEV -> {
                    //TODO 개발 주소 입력
                    ""
                }

                // 서비스 BASE 주소
                AppURLType.RELEASE -> {
                    //TODO 서비스 주소 입력
                    ""
                }

                // Default -> 실서버
                else -> {
                    //TODO 서비스 주소 입력
                    ""
                }
            }
        }

        // 아래 순서대로 Intro 통신 수행
        const val DEF_DEVICE_MSG_URL = "/m/2.0/grp_account/device_message.php"             // Device 정보 등록
        const val DEF_PUSH_URL = "/m/push/register_push_device_id.php"                     // PUSH
        const val DEF_INTRO_URL = "/m/2.0/grp_account/intro_message.php"                   // 인트로메시지
    }
}