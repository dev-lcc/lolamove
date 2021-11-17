package hk.com.lolamove.datasource.remote.di

import android.content.Context
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import hk.com.lolamove.datasource.remote.DeliveriesRestApiDatasource
import hk.com.lolamove.datasource.remote.retrofit.services.DeliveriesRetrofitService
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.create
import java.util.concurrent.TimeUnit

class RemoteDatasourceModule(
    private val apiUrlEndPoint: String,
    private val isDebug: Boolean = false
) {

    fun build(): Module = module {
        single(named(NamedDependency.REST_API_URL_ENDPOINT)) { provideCache(context = get()) }
        single(named(NamedDependency.OKHTTPCLIENT)) {
            provideOkHttpClient(
                cache = get(named(NamedDependency.CACHE)),
                isDebug = isDebug,
            )
        }

        single(named(NamedDependency.JSON)) { provideJson() }
        single(named(NamedDependency.RETROFIT)) {
            provideRetrofit(
                urlEndpoint = apiUrlEndPoint,
                client = get(named(NamedDependency.OKHTTPCLIENT)),
                json = get(named(NamedDependency.JSON)),
            )
        }

        /**
         * Retrofit Service(s)
         */
        single { provideRetrofitService<DeliveriesRetrofitService>(retrofit = get(named(NamedDependency.RETROFIT))) }
        // TODO:: Add more Retrofit Services here...

        /**
         * REST API Datasource(s)
         */
        single {
            DeliveriesRestApiDatasource(
                deliveriesRetrofitService = get(),
            )
        }

        // TODO:: Add more REST API Datasource(s) here...
    }

    private fun provideCache(context: Context): Cache =
        Cache(context.filesDir, 10 * 1024 * 1024)

    private fun provideOkHttpClient(
        cache: Cache,
        isDebug: Boolean,
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addNetworkInterceptor { chain ->
                chain.proceed(
                    chain.request()
                        .newBuilder()
                        // .addHeader("x-access-token", "...")
                        .build()
                )
            }
            .apply {
                if(isDebug) {
                    // Attach logger on DEBUG build
                    addNetworkInterceptor(
                        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
                    )
                    // Attach Facebook Stetho SDK on DEBUG build
                    addNetworkInterceptor(StethoInterceptor())
                }
            }
            .cache(cache)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()

    private fun provideJson(): Json =
        Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        }

    private fun provideRetrofit(
        urlEndpoint: String,
        client: OkHttpClient,
        json: Json
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(urlEndpoint)
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType())
            )
            .client(client)
            .build()

    private inline fun <reified T> provideRetrofitService(retrofit: Retrofit): T =
        retrofit.create()

    object NamedDependency {
        val REST_API_URL_ENDPOINT = "${RemoteDatasourceModule::class.java.`package`.name}.${RemoteDatasourceModule::class.java.simpleName}.REST_API_URL_ENDPOINT"
        val CACHE = "${RemoteDatasourceModule::class.java.`package`.name}.${RemoteDatasourceModule::class.java.simpleName}.CACHE"
        val JSON = "${RemoteDatasourceModule::class.java.`package`.name}.${RemoteDatasourceModule::class.java.simpleName}.JSON"
        val RETROFIT = "${RemoteDatasourceModule::class.java.`package`.name}.${RemoteDatasourceModule::class.java.simpleName}.RETROFIT"
        val OKHTTPCLIENT = "${RemoteDatasourceModule::class.java.`package`.name}.${RemoteDatasourceModule::class.java.simpleName}.OKHTTPCLIENT"
    }

}