package io.ktlab.bshelper.service

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import io.ktlab.bshelper.model.BSHelperDatabase
import io.ktlab.bshelper.model.BSMap
import io.ktlab.bshelper.model.BSMapVersion
import io.ktlab.bshelper.model.BSUser
import io.ktlab.bshelper.model.enums.ECharacteristic
import io.ktlab.bshelper.model.enums.EMapDifficulty
import io.ktlab.bshelper.model.FSMap
import io.ktlab.bshelper.model.FSPlaylist
import io.ktlab.bshelper.model.MapDifficulty
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit


object DBAdapter {
    private lateinit var driver: SqlDriver
    fun getDriver(): SqlDriver {
        return driver
    }
    fun createDatabase(driverFactory: DBDriverFactory): BSHelperDatabase {
        driver = driverFactory.createDriver()
        return BSHelperDatabase(
            driver = driver,
            MapDifficultyAdapter = MapDifficulty.Adapter(
                characteristicAdapter = stringOfECharacteristicAdapter,
                difficultyAdapter = stringOfEMapDifficultyAdapter
            ),
            FSMapAdapter = FSMap.Adapter(
                durationAdapter = longOfDurationAdapter,
            ),
            BSMapVersionAdapter = BSMapVersion.Adapter(
                createdAtAdapter = stringOfLocalDateTimeAdapter,
            ),
            BSMapAdapter = BSMap.Adapter(
                uploadedAdapter = stringOfLocalDateTimeAdapter,
                tagsAdapter = listOfStringsAdapter,

                createdAtAdapter = stringOfLocalDateTimeAdapter,
                updatedAtAdapter = stringOfLocalDateTimeAdapter,
                lastPublishedAtAdapter = stringOfLocalDateTimeAdapter,
                uploaderIdAdapter = longOfIntAdapter,
                ),
            BSUserAdapter = BSUser.Adapter(
                idAdapter = longOfIntAdapter,
            ),
            FSPlaylistAdapter = FSPlaylist.Adapter(
                mapAmountAdapter = longOfIntAdapter,
            ),
        )
    }

    private val datetimeOfLongAdapter = object : ColumnAdapter<LocalDateTime, Long> {
        override fun decode(databaseValue: Long) = Instant.fromEpochMilliseconds(databaseValue).toLocalDateTime(timeZone = TimeZone.currentSystemDefault())
        override fun encode(value: LocalDateTime) = value.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
    }

    private val listOfStringsAdapter = object : ColumnAdapter<List<String>, String> {
        override fun decode(databaseValue: String) =
            if (databaseValue.isEmpty()) {
                listOf()
            } else {
                databaseValue.split(",")
            }
        override fun encode(value: List<String>) = value.joinToString(separator = ",")
    }

    private val longOfIntAdapter = object : ColumnAdapter<Int, Long> {
        override fun decode(databaseValue: Long): Int {
            return databaseValue.toInt()
        }

        override fun encode(value: Int): Long {
            return value.toLong()
        }
    }
    private val stringOfECharacteristicAdapter = object : ColumnAdapter<ECharacteristic, String> {
        override fun decode(databaseValue: String): ECharacteristic {
            return ECharacteristic.from(databaseValue)
        }

        override fun encode(value: ECharacteristic): String {
            return value.slug
        }
    }
    private val stringOfEMapDifficultyAdapter = object : ColumnAdapter<EMapDifficulty, String> {
        override fun decode(databaseValue: String): EMapDifficulty {
            return EMapDifficulty.from(databaseValue)
        }

        override fun encode(value: EMapDifficulty): String {
            return value.slug
        }
    }

    private val longOfDurationAdapter = object : ColumnAdapter<Duration, Long> {
        override fun decode(databaseValue: Long): Duration {
            return databaseValue.milliseconds
        }

        override fun encode(value: Duration): Long {
            return value.toLong(DurationUnit.MILLISECONDS)
        }
    }

    private val stringOfLocalDateTimeAdapter = object : ColumnAdapter<LocalDateTime, String> {
        override fun decode(databaseValue: String): LocalDateTime {
            return LocalDateTime.parse(databaseValue)
        }

        override fun encode(value: LocalDateTime): String {
            return value.toString()
        }
    }
}