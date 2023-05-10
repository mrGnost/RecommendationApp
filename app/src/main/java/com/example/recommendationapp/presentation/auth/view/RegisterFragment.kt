package com.example.recommendationapp.presentation.auth.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.recommendationapp.App
import com.example.recommendationapp.R
import com.example.recommendationapp.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.applicationContext as App).appComp().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.enterBtn.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.login_fragment_container, LoginFragment.newInstance(), LoginFragment.TAG)
                .commit()
        }
        binding.createBtn.setOnClickListener {
            val password = binding.passwordEdit.text.toString().trim()
            val passwordAgain = binding.passwordAgainEdit.text.toString().trim()
            if (password != passwordAgain) {
                binding.passwordAgainInput.error = "Пароли не совпадают"
            } else {
                binding.passwordAgainInput.error = null
            }
        }
    }

    companion object {
        const val TAG = "RegisterFragment"
        private const val TAG_ADD = "$TAG ADD"
        private const val TAG_ERROR = "$TAG ERROR"
        private const val TAG_PROGRESS = "$TAG PROGRESS"

        fun newInstance(): RegisterFragment {
            return RegisterFragment()
        }
    }
}