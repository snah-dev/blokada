package blocka

import android.content.Context
import android.os.Build
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.singleton
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import core.ProductType
import core.getActiveContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.blokada.BuildConfig
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import tunnel.EXPIRATION_OFFSET
import tunnel.tunnelMain
import java.text.DateFormat
import java.util.*

fun blokadaUserAgent(ctx: Context = getActiveContext()!!, viewer: Boolean? = null)
    = "blokada/%s (android-%s %s %s %s %s-%s %s %s %s)".format(
        BuildConfig.VERSION_NAME,
        Build.VERSION.SDK_INT,
        BuildConfig.FLAVOR,
        BuildConfig.BUILD_TYPE,
        Build.SUPPORTED_ABIS[0],
        Build.MANUFACTURER,
        Build.DEVICE,
        if (ctx.packageManager.hasSystemFeature("android.hardware.touchscreen"))
            "touch" else "donttouch",
        if (viewer == true) "chrometab" else if (viewer == false) "webview" else "api",
        if (BoringtunLoader.supported) "compatible" else "incompatible"
)

