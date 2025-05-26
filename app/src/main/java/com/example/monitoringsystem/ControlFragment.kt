package com.example.monitoringsystem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.monitoringsystem.databinding.FragmentControlBinding
import com.jraska.console.Console

class ControlFragment : Fragment() {

    private var _binding: FragmentControlBinding? = null
    private val binding: FragmentControlBinding get() = _binding!!

    private val viewModel: ControlViewModel by viewModels {
        ControlViewModelFactory((requireActivity().application as App).adapterProvider)
    }

    private val editButtonOnClickListener = object : View.OnClickListener {
        override fun onClick(v: View?) {
            viewModel.printData(binding.editText.text.toString())
        }
    }

    private val getDataButtonClickListener = object : View.OnClickListener {
        override fun onClick(v: View?) {
            var data = viewModel.getData()
            if (data != "") {
                Console.writeLine(viewModel.getData())
            }
        }
    }

    private val disconnectButtonOnClickListener = object : View.OnClickListener {
        override fun onClick(v: View?) {
            viewModel.disconnect()
            Console.clear()
            parentFragmentManager.beginTransaction()
                .replace(R.id.containerFragment, DevicesFragment())
                .commit()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentControlBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editButton.setOnClickListener(editButtonOnClickListener)
        binding.disconnectButton.setOnClickListener(disconnectButtonOnClickListener)
        binding.getDataButton.setOnClickListener(getDataButtonClickListener)
    }

    override fun onStop() {
        super.onStop()
        viewModel.disconnect()
    }

    override fun onResume() {
        super.onResume()

        val deviceAddress = requireArguments().getString(KEY_DEVICE_ADDRESS)!!
        viewModel.connect(deviceAddress)
    }

    companion object {
        private const val KEY_DEVICE_ADDRESS = "key_device_address"
        @JvmStatic
        fun newInstance(deviceAddress: String) = ControlFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_DEVICE_ADDRESS, deviceAddress)
                }
            }
    }
}