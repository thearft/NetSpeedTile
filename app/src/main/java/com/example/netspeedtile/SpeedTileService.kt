package com.example.netspeedtile

import android.net.TrafficStats
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import kotlinx.coroutines.*

@RequiresApi(Build.VERSION_CODES.N)
class SpeedTileService : TileService() {

    /* ────────── Coroutine scope (main/binder thread) ────────── */
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    /* ────────── State ────────── */
    private var loopActive = false
    private var lastRx = 0L
    private var lastTx = 0L
    private lateinit var prefs: android.content.SharedPreferences

    /* ────────── Preferences helper ────────── */
    private fun isByteMode(): Boolean =
        prefs.getString("unit_mode", "bits") == "bytes"

    /* ────────── Formatting helper ────────── */
    private fun formatSpeed(kbps: Float, byteMode: Boolean): String {
        return if (byteMode) {                       //  Bytes
            val kBps = kbps / 8f
            if (kBps >= 1_000f) "%.1f MB/s".format(kBps / 1_000f)
            else "%.0f KB/s".format(kBps)
        } else {                                    //  Bits
            if (kbps >= 1_000f) "%.1f Mbps".format(kbps / 1_000f)
            else "%.0f Kbps".format(kbps)
        }
    }

    /* ────────── QS-tile lifecycle ────────── */

    override fun onTileAdded() {
        super.onTileAdded()
        qsTile?.apply {
            state = Tile.STATE_INACTIVE
            label = getString(R.string.tile_label)
            updateTile()
        }
    }

    override fun onStartListening() {
        super.onStartListening()

        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        loopActive = true
        lastRx = TrafficStats.getTotalRxBytes()
        lastTx = TrafficStats.getTotalTxBytes()
        setTileActive(true)

        scope.launch { speedLoop() }
    }

    override fun onStopListening() {
        loopActive = false
        setTileActive(false)
        super.onStopListening()
    }

    /* ────────── Main polling loop ────────── */

    private suspend fun speedLoop() {
        while (loopActive) {
            delay(1_000)

            val newRx = TrafficStats.getTotalRxBytes()
            val newTx = TrafficStats.getTotalTxBytes()

            val downK = ((newRx - lastRx) * 8f / 1_000).coerceAtLeast(0f)
            val upK   = ((newTx - lastTx) * 8f / 1_000).coerceAtLeast(0f)

            lastRx = newRx
            lastTx = newTx

            val byteMode = isByteMode()             // read preference live

            qsTile?.let {
                it.subtitle = "↓ ${formatSpeed(downK, byteMode)} | ↑ ${formatSpeed(upK, byteMode)}"
                it.updateTile()
            }
        }
    }

    /* ────────── Utility ────────── */

    private fun setTileActive(active: Boolean) {
        qsTile?.apply {
            state = if (active) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            subtitle = if (active) getString(R.string.loading) else ""
            updateTile()
        }
    }
}
