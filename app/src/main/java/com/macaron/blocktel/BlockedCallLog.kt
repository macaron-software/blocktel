package com.macaron.blocktel

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Logs blocked calls with timestamp and matched prefix.
 * Keeps the last 200 entries in SharedPreferences.
 */
class BlockedCallLog(context: Context) {

    data class Entry(
        val number: String,
        val prefix: String,
        val timestamp: Long
    )

    private val prefs: SharedPreferences =
        context.getSharedPreferences("blocktel_log", Context.MODE_PRIVATE)

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE)

    fun log(number: String, prefix: String) {
        val entries = loadEntries().toMutableList()
        entries.add(0, Entry(number, prefix, System.currentTimeMillis()))
        if (entries.size > MAX_ENTRIES) {
            entries.subList(MAX_ENTRIES, entries.size).clear()
        }
        saveEntries(entries)
        incrementTotal()
    }

    fun getRecentEntries(limit: Int = 50): List<Entry> {
        return loadEntries().take(limit)
    }

    fun getTotalBlocked(): Int = prefs.getInt(KEY_TOTAL, 0)

    fun getTodayBlocked(): Int {
        val today = dateFormat.format(Date()).substring(0, 10)
        return loadEntries().count {
            dateFormat.format(Date(it.timestamp)).substring(0, 10) == today
        }
    }

    fun clear() {
        prefs.edit()
            .remove(KEY_ENTRIES)
            .putInt(KEY_TOTAL, 0)
            .apply()
    }

    private fun incrementTotal() {
        val current = prefs.getInt(KEY_TOTAL, 0)
        prefs.edit().putInt(KEY_TOTAL, current + 1).apply()
    }

    private fun loadEntries(): List<Entry> {
        val json = prefs.getString(KEY_ENTRIES, null) ?: return emptyList()
        val arr = JSONArray(json)
        val result = mutableListOf<Entry>()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            result.add(
                Entry(
                    number = obj.getString("number"),
                    prefix = obj.getString("prefix"),
                    timestamp = obj.getLong("timestamp")
                )
            )
        }
        return result
    }

    private fun saveEntries(entries: List<Entry>) {
        val arr = JSONArray()
        entries.forEach { entry ->
            arr.put(
                JSONObject().apply {
                    put("number", entry.number)
                    put("prefix", entry.prefix)
                    put("timestamp", entry.timestamp)
                }
            )
        }
        prefs.edit().putString(KEY_ENTRIES, arr.toString()).apply()
    }

    companion object {
        private const val KEY_ENTRIES = "blocked_entries"
        private const val KEY_TOTAL = "total_blocked"
        private const val MAX_ENTRIES = 200
    }
}
