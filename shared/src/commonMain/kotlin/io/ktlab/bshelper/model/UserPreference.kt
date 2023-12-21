package io.ktlab.bshelper.model

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import io.ktlab.bshelper.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream


enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}
enum class ImageSource(val value:String) {
    BS("BeatSaver"), PROXY("Proxy"),CUSTOM("Custom");
    companion object {
        fun fromValue(value: String): ImageSource {
            return when(value) {
                "BeatSaver" -> BS
                "Proxy" -> PROXY
                "Custom" -> CUSTOM
                else -> BS
            }
        }
    }
}


sealed interface BSAPIProvider {
    @Serializable
    data object BS: BSAPIProvider
    @Serializable
    data class Custom(val url:String): BSAPIProvider
}

@Serializable
data class UserPreferenceV2(
    val currentManageFolder: SManageFolder? = null,
    val themeColor:Long = BuildConfig.THEME_COLOR,
    val themeMode: ThemeMode,
    val imageSource: ImageSource,
    val customImageSource: String? = null,
    val bsApiProvider: BSAPIProvider = BSAPIProvider.BS,
) {

    fun getProxiedImageSource(url:String): String {
        return when(imageSource) {
            ImageSource.BS -> url
            ImageSource.PROXY -> BuildConfig.BS_IMG_PATTERNS.fold(url) { acc, regex ->
                acc.replace(regex, BuildConfig.BS_IMG_PROXY)
            }
            ImageSource.CUSTOM -> customImageSource?.let {
                BuildConfig.BS_IMG_PATTERNS.fold(url) { acc, regex ->
                    acc.replace(regex, customImageSource)
                }
            }?:url
        }
    }

    companion object {
        fun getDefaultUserPreference(): UserPreferenceV2 {
            return UserPreferenceV2(
                themeColor = BuildConfig.THEME_COLOR,
                themeMode = ThemeMode.SYSTEM,
                imageSource = ImageSource.BS,
            )
        }
    }

}
object UserPreferencesSerializer : Serializer<UserPreferenceV2> {
    override val defaultValue = UserPreferenceV2(
        themeColor = BuildConfig.THEME_COLOR,
        themeMode = ThemeMode.SYSTEM,
        imageSource = ImageSource.BS,
    )

    override suspend fun readFrom(input: InputStream): UserPreferenceV2 {
        try {
            return Json.decodeFromString(
                UserPreferenceV2.serializer(), input.readBytes().decodeToString())
        } catch (serialization: SerializationException) {
            throw CorruptionException("Unable to read UserPrefs", serialization)
        }
    }

    override suspend fun writeTo(t: UserPreferenceV2, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(Json.encodeToString(UserPreferenceV2.serializer(), t).encodeToByteArray())
        }
    }
}

data class UserPreference(
    val currentManageDir: String = "",
    val currentThemeColor: String = "",
    val currentThemeMode: String = "",
    val currentImageSource: String = "",
) {
    companion object {
        fun getDefaultUserPreference(): UserPreference {
            return UserPreference()
        }
    }

    // to hex color like 0x00EDEFFF
    fun getThemeColor(): Long? {
        if (currentThemeColor.length != 10 || !Regex("0x[0-9A-Fa-f]{8}").matches(currentThemeColor)) {
            return null
        }
        return currentThemeColor.substring(2).toLong(16)
    }
}
