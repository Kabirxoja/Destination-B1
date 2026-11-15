package uz.kabirhoja.destination.ui.test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.kabirhoja.destination.R
import com.kabirhoja.destination.databinding.FragmentChoiceBinding
import uz.kabirhoja.destination.data.model.OptionItem
import uz.kabirhoja.destination.data.model.TestChoiceItem
import uz.kabirhoja.destination.ui.common.AnimationButton.animateClick
import uz.kabirhoja.destination.ui.common.MainViewModelFactory
import uz.kabirhoja.destination.ui.common.setupSmoothScroll

class ChoiceFragment : Fragment(), IndicatorAdapter.OnClickItemListener {

    private var _binding: FragmentChoiceBinding? = null
    private val binding get() = _binding!!
    private lateinit var testViewModel: ChoiceViewModel
    private lateinit var indicatorAdapter: IndicatorAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChoiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        testViewModel = ViewModelProvider(
            this,
            MainViewModelFactory(requireActivity().application)
        )[ChoiceViewModel::class.java]

        indicatorAdapter = IndicatorAdapter()
        binding.indicatorRv.adapter = indicatorAdapter
        binding.indicatorRv.layoutManager = GridLayoutManager(context, 4)
        binding.indicatorRv.setupSmoothScroll(bottomPaddingDp = 16)
        indicatorAdapter.setOnClickListener(this)

        indicatorAdapter.updateList(testViewModel.getUnits())
        updateStartButtonState()

        testViewModel.optionList.observe(viewLifecycleOwner) { optionsList ->
            updateOptionsUI(optionsList)
            updateCheckedIndicator()
        }

        binding.startButton.setOnClickListener {
            startExam()
            it.animateClick()
        }

        binding.adjustmentLayout.setOnClickListener {
            binding.additionalOptionsLayout.visibility =
                if (binding.additionalOptionsLayout.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            it.animateClick()
        }

        binding.text1.text = "Topic vocabulary"
        binding.text2.text = "Phrasal verbs"
        binding.text3.text = "Prepositional phrases"
        binding.text4.text = "Word patterns"
        binding.text5.text = "Word formation"

        binding.button1.setOnClickListener {
            testViewModel.selectMode("Test")
            it.animateClick()
        }

        binding.button2.setOnClickListener {
            testViewModel.selectMode("Write")
            it.animateClick()
        }

        binding.row1.setOnClickListener {
            testViewModel.toggleCategory(0)
        }

        binding.row2.setOnClickListener {
            testViewModel.toggleCategory(1)
        }

        binding.row3.setOnClickListener {
            testViewModel.toggleCategory(2)
        }

        binding.row4.setOnClickListener {
            testViewModel.toggleCategory(3)
        }

        binding.row5.setOnClickListener {
            testViewModel.toggleCategory(4)
        }
    }

    private fun updateOptionsUI(options: List<OptionItem>) {
        if (options.size >= 7) {
            binding.icon1.setImageResource(if (options[0].isChecked) R.drawable.ic_checkbox_true else R.drawable.ic_unselected)
            binding.icon2.setImageResource(if (options[1].isChecked) R.drawable.ic_checkbox_true else R.drawable.ic_unselected)
            binding.icon3.setImageResource(if (options[2].isChecked) R.drawable.ic_checkbox_true else R.drawable.ic_unselected)
            binding.icon4.setImageResource(if (options[3].isChecked) R.drawable.ic_checkbox_true else R.drawable.ic_unselected)
            binding.icon5.setImageResource(if (options[4].isChecked) R.drawable.ic_checkbox_true else R.drawable.ic_unselected)

            val isTestMode = options[5].isChecked
            val isWriteMode = options[6].isChecked

            if (isTestMode) {
                binding.imgTest.setImageResource(R.drawable.ic_option_on)
                binding.imgWrite.setImageResource(R.drawable.ic_pencil_off)
                binding.txtTest.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                binding.txtWrite.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            } else if (isWriteMode) {
                binding.imgTest.setImageResource(R.drawable.ic_option_off)
                binding.imgWrite.setImageResource(R.drawable.ic_pencil_on)
                binding.txtTest.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                binding.txtWrite.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
            }
        }
    }

    private fun updateCheckedIndicator() {
        val options = testViewModel.getOptions()
        val isOptionSelected = if (options.size >= 7) (options[5].isChecked || options[6].isChecked) else false
        val colorRes = if (isOptionSelected) R.color.green else R.color.red
        binding.checkedIndicator.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), colorRes)
    }

    private fun updateStartButtonState() {
        val units = testViewModel.getUnits()
        val hasSelectedUnits = units.any { it.checked }
        binding.startButton.isEnabled = hasSelectedUnits
        binding.startButton.backgroundTintList = ContextCompat.getColorStateList(
            requireContext(),
            if (hasSelectedUnits) R.color.blue else R.color.gray
        )
    }

    override fun onClickItem(item: TestChoiceItem) {
        testViewModel.toggleUnit(item)
        updateStartButtonState()
    }

    private fun startExam() {
        val options = testViewModel.getOptions()
        val units = testViewModel.getUnits()

        val isOptionTypeSelected = options.size >= 7 && (options[5].isChecked || options[6].isChecked)
        if (!isOptionTypeSelected) {
            Toast.makeText(
                requireContext(),
                "first of all, you should select options",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val selectedUnits = units.filter { it.checked }
        if (selectedUnits.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Please select at least one unit",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val isAnyCategorySelected = options.take(5).any { it.isChecked }
        if (!isAnyCategorySelected) {
            Toast.makeText(
                requireContext(),
                "Please select at least one category",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val bundle = Bundle().apply {
            putSerializable("updatedOptionList", ArrayList(options))
            putSerializable("updatedNumberList", ArrayList(units))
        }

        if (options[6].isChecked) {
            // "Write" mode selected
            findNavController().navigate(
                R.id.action_navigation_test_choice_to_testFragment,
                bundle
            )
        } else {
            // "Test" mode selected
            findNavController().navigate(
                R.id.action_navigation_test_choice_to_testChoiceFragment,
                bundle
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
