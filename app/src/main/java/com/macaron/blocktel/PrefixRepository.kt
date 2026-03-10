package com.macaron.blocktel

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

/**
 * Stores prefix blocking rules in SharedPreferences.
 * Default rules match the official ARCEP spam prefixes for France.
 */
class PrefixRepository(context: Context) {

    data class PrefixRule(
        val prefix: String,
        val label: String,
        val region: String,
        var enabled: Boolean = true
    )

    private val prefs: SharedPreferences =
        context.getSharedPreferences("blocktel_prefixes", Context.MODE_PRIVATE)

    private val rules: MutableList<PrefixRule> = mutableListOf()

    init {
        loadRules()
    }

    fun getAllRules(): List<PrefixRule> = rules.toList()

    fun getEnabledPrefixes(): List<String> =
        rules.filter { it.enabled }.map { it.prefix }

    fun setEnabled(prefix: String, enabled: Boolean) {
        val rule = rules.find { it.prefix == prefix } ?: return
        rule.enabled = enabled
        saveRules()
    }

    fun setAllEnabled(enabled: Boolean) {
        rules.forEach { it.enabled = enabled }
        saveRules()
    }

    fun addCustomPrefix(prefix: String, label: String) {
        val clean = prefix.replace(Regex("[^0-9]"), "")
        if (clean.length < 3 || rules.any { it.prefix == clean }) return
        rules.add(PrefixRule(clean, label, "Personnalise", true))
        saveRules()
    }

    fun removeCustomPrefix(prefix: String) {
        val removed = rules.removeAll { it.prefix == prefix && it.region == "Personnalise" }
        if (removed) saveRules()
    }

    fun isBlockingActive(): Boolean = prefs.getBoolean(KEY_ACTIVE, true)

    fun setBlockingActive(active: Boolean) {
        prefs.edit().putBoolean(KEY_ACTIVE, active).apply()
    }

    private fun loadRules() {
        rules.clear()
        val json = prefs.getString(KEY_RULES, null)
        if (json != null) {
            val arr = JSONArray(json)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                rules.add(
                    PrefixRule(
                        prefix = obj.getString("prefix"),
                        label = obj.getString("label"),
                        region = obj.getString("region"),
                        enabled = obj.getBoolean("enabled")
                    )
                )
            }
        } else {
            rules.addAll(DEFAULT_PREFIXES)
            saveRules()
        }
    }

    private fun saveRules() {
        val arr = JSONArray()
        rules.forEach { rule ->
            arr.put(
                JSONObject().apply {
                    put("prefix", rule.prefix)
                    put("label", rule.label)
                    put("region", rule.region)
                    put("enabled", rule.enabled)
                }
            )
        }
        prefs.edit().putString(KEY_RULES, arr.toString()).apply()
    }

    companion object {
        private const val KEY_RULES = "prefix_rules"
        private const val KEY_ACTIVE = "blocking_active"

        val DEFAULT_PREFIXES = listOf(
            // Metropolitan France — ARCEP demarchage prefixes
            PrefixRule("0162", "01 62", "Ile-de-France"),
            PrefixRule("0163", "01 63", "Ile-de-France"),
            PrefixRule("0270", "02 70", "Nord-Ouest"),
            PrefixRule("0271", "02 71", "Nord-Ouest"),
            PrefixRule("0377", "03 77", "Nord-Est"),
            PrefixRule("0378", "03 78", "Nord-Est"),
            PrefixRule("0424", "04 24", "Sud-Est"),
            PrefixRule("0425", "04 25", "Sud-Est"),
            PrefixRule("0568", "05 68", "Sud-Ouest"),
            PrefixRule("0569", "05 69", "Sud-Ouest"),
            PrefixRule("0948", "09 48", "VoIP national"),
            PrefixRule("0949", "09 49", "VoIP national"),
            // Overseas territories
            PrefixRule("09475", "09 475", "Guadeloupe / St-Martin / St-Barth"),
            PrefixRule("09476", "09 476", "Guyane"),
            PrefixRule("09477", "09 477", "Martinique"),
            PrefixRule("09478", "09 478", "La Reunion / Mayotte"),
            PrefixRule("09479", "09 479", "La Reunion / Mayotte"),
        )
    }
}
