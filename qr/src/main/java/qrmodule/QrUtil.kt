package qrmodule

import android.app.Activity
import android.media.AudioAttributes
import android.media.SoundPool
import android.media.audiofx.AudioEffect
import android.os.Handler
import android.os.Looper
import common.modules.qr.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QrUtil {
    companion object {
        suspend fun playSound(ctx: Activity, sound: Int) = withContext(Dispatchers.IO) {
            // 소리 init 및 재생 처리.
            val mSoundPool = SoundPool.Builder()
                    .setMaxStreams(1)
                    .setAudioAttributes(AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                            .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)             // 모든 상황에서 스피커 밖으로 소리를 내게 강제한다.
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build())
                    .build()

            mSoundPool.load(ctx, sound, 0)
            mSoundPool.setOnLoadCompleteListener { soundPool, sampleId, status ->
                when (status) {
                    AudioEffect.SUCCESS -> {
                        // 성공
                        soundPool.play(sampleId, 0.02f, 0.02f, 0, 0, 1.0f)
                        Handler(Looper.getMainLooper()).postDelayed({
                            soundPool.release()
                        }, 1500)
                    }
                }
            }
        }
    }
}