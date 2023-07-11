package common.modules.network

import android.annotation.SuppressLint
import android.net.TrafficStats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * 공용 NetworkClient
 */
class NetworkClient(private val networkUrl: String, private val data: Any) {

    private val timeOut: Long = 30
    private var request: Request? = null
    private val client: OkHttpClient

    init {
        val interceptor = HttpLoggingInterceptor()

        // 통신 디버깅 목적 설정.
        interceptor.level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }

        val trustAllCerts = arrayOf<TrustManager>(@SuppressLint("CustomX509TrustManager") object : X509TrustManager {
            @SuppressLint("TrustAllX509TrustManager")
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            @SuppressLint("TrustAllX509TrustManager")
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        })

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())

        val sslSocketFactory = sslContext.socketFactory

        client = OkHttpClient().newBuilder()
                .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                .addInterceptor(interceptor)
                .readTimeout(timeOut, TimeUnit.SECONDS)
                .callTimeout(timeOut, TimeUnit.SECONDS)
                .writeTimeout(timeOut, TimeUnit.SECONDS)
                .build()

        if (BuildConfig.DEBUG) {
            TrafficStats.setThreadStatsTag(System.currentTimeMillis().toInt())
        }
    }

    suspend fun executeRequest(userAgent: String): Response? = withContext(Dispatchers.IO) {
        val requestBody: MultipartBody.Builder = MultipartBody.Builder().setType(MultipartBody.FORM)

        for (i in (data as JSONObject).keys()) {
            requestBody.addFormDataPart(i, data[i].toString())
        }

        request = Request.Builder().apply {
            addHeader("User-Agent", userAgent)
            url(networkUrl)
            if (data.length() > 0) {
                post(requestBody.build())
            }
        }.build()

        request?.let {
            kotlin.runCatching {
                client.newCall(it).execute()
            }.getOrNull()
        }
    }
}