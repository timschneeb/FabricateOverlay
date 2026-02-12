package tk.zwander.fabricateoverlaysample.util

import android.content.Context
import androidx.core.content.edit
import tk.zwander.fabricateoverlaysample.data.ResPrefixes
import tk.zwander.fabricateoverlaysample.data.TriState

object FilterPrefs {
    private const val PREFS_NAME = "resource_filters"

    // Only values != DEFAULT are stored; DEFAULT means no preference set.
    fun saveFilters(context: Context, map: Map<ResPrefixes, TriState>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            // For each possible prefix, either store value.name (INCLUDE/EXCLUDE) or remove the key if DEFAULT
            ResPrefixes.entries.forEach { p ->
                val state = map[p] ?: TriState.DEFAULT
                if (state == TriState.DEFAULT) putString(p.name, null) else putString(p.name, state.name)
            }
        }
    }

    // Load saved filters into a MutableMap. Missing entries default to TriState.DEFAULT.
    fun loadFilters(context: Context): MutableMap<ResPrefixes, TriState> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val out = mutableMapOf<ResPrefixes, TriState>()
        ResPrefixes.entries.forEach { p ->
            val v = prefs.getString(p.name, null)
            out[p] = when (v) {
                null -> TriState.DEFAULT
                TriState.INCLUDE.name -> TriState.INCLUDE
                TriState.EXCLUDE.name -> TriState.EXCLUDE
                else -> TriState.DEFAULT
            }
        }
        return out
    }
}