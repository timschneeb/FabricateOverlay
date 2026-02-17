package tk.zwander.fabricateoverlaysample.data

import androidx.annotation.RawRes
import tk.zwander.fabricateoverlaysample.R

enum class ResPrefixes(val displayName: String, val prefixes: List<String>, @param:RawRes val blocklistRes: Int? = null) {
    ANDROIDX("AndroidX", listOf("androidx_", "abc_"), R.raw.androidx_resources),
    LEGACY_SUPPORT("Legacy Support library", listOf("design_"), R.raw.support_resources),
    MATERIAL_COMPONENTS("Material", listOf("m3expressive_", "m3_", "mtrl_", "material_"), R.raw.material_resources),
    SAMSUNG_EXTENDED_SUPPORT("Samsung Extended Support library", listOf("sesl_")),
    SETTINGS_LIB("SettingsLib", listOf("settingslib_")),
    SETUP_WIZARD_LIB("SetupWizardLib", listOf("sud_")),
    OTHER_3RD_PARTY_LIBS("3rd-party libraries", listOf(
            "exo_", "apploving_", "glide_", "com_facebook_", "common_google_signin_", "mbridge_",
                                 "ad_mob_", "com_braze_", "secmtp_", "ia_", "uxc_"
    ));
}