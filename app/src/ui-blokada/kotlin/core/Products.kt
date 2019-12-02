package core

import android.content.Context
import org.blokada.BuildConfig

enum class Product {
    FULL, GOOGLE;

    companion object {
        fun current(ctx: Context): Product {
            return Product.FULL
        }
    }
}

enum class ProductType {
    DEBUG, RELEASE, OFFICIAL, BETA;

    companion object {
        fun current(): ProductType {
            return when(BuildConfig.BUILD_TYPE.toLowerCase()) {
                "debug" -> DEBUG
                "official" -> OFFICIAL
                "beta" -> BETA
                else -> RELEASE
            }
        }

        fun isPublic(): Boolean {
            return true
        }
    }
}
