package com.andrognito.patternlockview.utils

import com.andrognito.patternlockview.PatternLockView
import java.security.MessageDigest

object PatternLockUtils {
    // Compute a SHA-1 hash derived from the pattern (and view) so callers get a stable string.
    fun patternToSha1(view: PatternLockView, pattern: MutableList<PatternLockView.Dot>?): String {
        // build a reproducible string from the pattern contents
        val patternDescription = pattern?.map { System.identityHashCode(it).toString() }?.joinToString(separator = ",") ?: ""
        val input = "${view.hashCode()}:$patternDescription"
        return sha1(input)
    }

    private fun sha1(input: String): String {
        val md = MessageDigest.getInstance("SHA-1")
        val bytes = md.digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString(separator = "") { "%02x".format(it) }
    }
}
