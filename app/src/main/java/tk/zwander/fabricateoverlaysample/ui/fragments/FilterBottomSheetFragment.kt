package tk.zwander.fabricateoverlaysample.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import tk.zwander.fabricateoverlaysample.R
import tk.zwander.fabricateoverlaysample.data.TriState
import tk.zwander.fabricateoverlaysample.databinding.BottomSheetFiltersBinding
import tk.zwander.fabricateoverlaysample.databinding.ChipFilterBinding
import tk.zwander.fabricateoverlaysample.ui.model.ResourceSelectViewModel

class FilterBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetFiltersBinding
    private lateinit var vm: ResourceSelectViewModel
    private lateinit var prefixChips: List<Chip>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = BottomSheetFiltersBinding.inflate(inflater, container, false)
        vm = ViewModelProvider(requireActivity())[ResourceSelectViewModel::class.java]

        Prefixes.entries
            .map { value ->
                ChipFilterBinding.inflate(layoutInflater).root.apply {
                    text = value.displayName
                    tag = value
                }
            }
            .also { prefixChips = it }
            .forEach(binding.chips::addView)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        fun Chip.setTri(state: TriState) = when (state) {
            TriState.DEFAULT -> apply { isChecked = false; chipIcon = null }
            TriState.INCLUDE -> apply { isChecked = true; setCheckedIconResource(R.drawable.ic_check) }
            TriState.EXCLUDE -> apply { isChecked = true; setCheckedIconResource(R.drawable.ic_close) }
        }

        fun nextTri(s: TriState) = when (s) {
            TriState.DEFAULT -> TriState.EXCLUDE
            TriState.EXCLUDE -> TriState.INCLUDE
            TriState.INCLUDE -> TriState.DEFAULT
        }
        
        fun bindTriState(chip: Chip, getter: () -> TriState, setter: (TriState) -> Unit) {
            chip.setOnClickListener {
                setter(nextTri(getter()))
                vm.memberFilterLive.postValue(vm.memberFilter)
            }
        }

        // keep UI in sync with ViewModel
        vm.memberFilterLive.observe(viewLifecycleOwner) { f ->
            prefixChips.forEach { chip ->
                val prefixes = (chip.tag as Prefixes).prefixes
                val state = when {
                    prefixes.all { p -> f.entries.any { it.key.prefixes.contains(p) && it.value == TriState.INCLUDE } } -> TriState.INCLUDE
                    prefixes.all { p -> f.entries.any { it.key.prefixes.contains(p) && it.value == TriState.EXCLUDE } } -> TriState.EXCLUDE
                    else -> TriState.DEFAULT
                }
                chip.setTri(state)
            }
        }

        // wire interactions concisely using the bind helpers
        prefixChips.forEach { chip ->
            val key = chip.tag as Prefixes
            bindTriState(chip,
                { vm.memberFilter[key] ?: TriState.DEFAULT },
                { vm.memberFilter[key] = it }
            )
        }
    }

    companion object {
        enum class Prefixes(val displayName: String, val prefixes: List<String>) {
            ANDROIDX("AndroidX", listOf("androidx_", "abc_")),
            LEGACY_SUPPORT("Legacy Support library", listOf("design_")),
            MATERIAL_COMPONENTS("Material", listOf("m3_", "mtrl_", "material_")),
            SAMSUNG_EXTENDED_SUPPORT("Samsung Extended Support library", listOf("sesl_")),
            OTHER_3RD_PARTY_LIBS("3rd-party libraries", listOf(
                "exo_", "apploving_", "glide_", "com_facebook_", "common_google_signin_", "mbridge_",
                "ad_mob_", "com_braze_", "secmtp_", "ia_", "uxc_"
            ));
        }
    }
}
