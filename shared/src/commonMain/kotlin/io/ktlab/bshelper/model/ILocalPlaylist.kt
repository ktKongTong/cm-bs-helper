package io.ktlab.bshelper.model

interface ILocalPlaylist : IPlaylist {
    override fun getMangerFolderId(): Long
}