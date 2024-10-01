package com.example.quickcartadmin.auth

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.quickcartadmin.R
import com.example.quickcartadmin.utils.Utils
import com.example.quickcartadmin.databinding.FragmentSignInBinding


@Suppress("DEPRECATION")
class SignInFragment : Fragment() {

    private lateinit var bindingF: FragmentSignInBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingF = FragmentSignInBinding.inflate(layoutInflater)
        setStatusBarColor()
        getUserNumber()
        onContinueClick()

        return bindingF.root
    }

    private fun onContinueClick() {
        bindingF.btnContinue.setOnClickListener {
            val number = bindingF.etUserNumber.text.toString()
            if (number.isEmpty() || number.length != 10) {
                Utils.showToast(requireContext(), "Please enter valid phone number")
            } else {
                val bundle = Bundle()
                bundle.putString("number", number)
                findNavController().navigate(R.id.action_signInFragment_to_OTPFragment, bundle)
            }
        }
    }

    private fun getUserNumber() {
        bindingF.etUserNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(number: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val len = number?.length
                if (len == 10) {
                    bindingF.btnContinue.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.green
                        )
                    )
                } else {
                    bindingF.btnContinue.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.black
                        )
                    )

                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun setStatusBarColor() {
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(requireContext(), R.color.yellow)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }
}