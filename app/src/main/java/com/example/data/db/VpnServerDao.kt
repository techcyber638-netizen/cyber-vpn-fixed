package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.VpnServer
import kotlinx.coroutines.flow.Flow

@Dao
interface VpnServerDao {
    @Query("SELECT * FROM vpn_servers ORDER BY isFavorite DESC, id ASC")
    fun getAllServersFlow(): Flow<List<VpnServer>>

    @Query("SELECT * FROM vpn_servers WHERE id = :id")
    suspend fun getServerById(id: Long): VpnServer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServer(server: VpnServer): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServers(servers: List<VpnServer>)

    @Update
    suspend fun updateServer(server: VpnServer)

    @Query("UPDATE vpn_servers SET pingMs = :pingMs WHERE id = :id")
    suspend fun updatePing(id: Long, pingMs: Int)

    @Delete
    suspend fun deleteServer(server: VpnServer)

    @Query("DELETE FROM vpn_servers WHERE id = :id")
    suspend fun deleteServerById(id: Long)

    @Query("DELETE FROM vpn_servers")
    suspend fun deleteAllServers()
}
