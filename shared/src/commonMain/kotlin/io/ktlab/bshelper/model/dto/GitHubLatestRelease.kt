package io.ktlab.bshelper.model.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubLatestRelease (
    val assets: List<String>,

    @SerialName("assets_url")
    val assetsURL: String,

    val author: Author,
    val body: String,

    @SerialName("created_at")
    val createdAt: String,

    val draft: Boolean,

    @SerialName("html_url")
    val htmlURL: String,

    val id: Long,
    val name: String,

    @SerialName("node_id")
    val nodeID: String,

    val prerelease: Boolean,

    @SerialName("published_at")
    val publishedAt: String,

    @SerialName("tag_name")
    val tagName: String,

    @SerialName("tarball_url")
    val tarballURL: String,

    @SerialName("target_commitish")
    val targetCommitish: String,

    @SerialName("upload_url")
    val uploadURL: String,

    val url: String,

    @SerialName("zipball_url")
    val zipballURL: String
)

@Serializable
data class Author (
    @SerialName("avatar_url")
    val avatarURL: String,

    @SerialName("events_url")
    val eventsURL: String,

    @SerialName("followers_url")
    val followersURL: String,

    @SerialName("following_url")
    val followingURL: String,

    @SerialName("gists_url")
    val gistsURL: String,

    @SerialName("gravatar_id")
    val gravatarID: String,

    @SerialName("html_url")
    val htmlURL: String,

    val id: Long,
    val login: String,

    @SerialName("node_id")
    val nodeID: String,

    @SerialName("organizations_url")
    val organizationsURL: String,

    @SerialName("received_events_url")
    val receivedEventsURL: String,

    @SerialName("repos_url")
    val reposURL: String,

    @SerialName("site_admin")
    val siteAdmin: Boolean,

    @SerialName("starred_url")
    val starredURL: String,

    @SerialName("subscriptions_url")
    val subscriptionsURL: String,

    val type: String,
    val url: String
)