package com.example.recommendationapp.presentation.auth.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.recommendationapp.App
import com.example.recommendationapp.R
import com.example.recommendationapp.databinding.FragmentRegisterBinding
import com.example.recommendationapp.domain.interactor.LocalInteractor
import com.example.recommendationapp.domain.interactor.RecommendationInteractor
import com.example.recommendationapp.presentation.auth.viewmodel.AuthViewModel
import com.example.recommendationapp.presentation.auth.viewmodel.AuthViewModelFactory
import com.example.recommendationapp.presentation.onboarding.search.view.SearchActivity
import com.example.recommendationapp.utils.scheduler.SchedulerProvider
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    lateinit var viewModel: AuthViewModel

    @Inject
    lateinit var recommendationInteractor: RecommendationInteractor
    @Inject
    lateinit var localInteractor: LocalInteractor
    @Inject
    lateinit var schedulers: SchedulerProvider

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

        createViewModel()
        observeLiveData()

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
                viewModel.register(binding.inputEmail.editText.toString().trim(), password)
            }
        }
    }

    private fun createViewModel() {
        viewModel = ViewModelProvider(
            this,
            AuthViewModelFactory(recommendationInteractor, localInteractor, schedulers)
        )[AuthViewModel::class.java]
    }

    private fun observeLiveData() {
        viewModel.getErrorLiveData().observe(viewLifecycleOwner, this::showError)
        viewModel.getSuccessLiveData().observe(viewLifecycleOwner, this::success)
    }

    private fun showError(throwable: Throwable) {
        Log.d(SearchActivity.TAG, "showError() called with: throwable = $throwable")
        Snackbar.make(binding.root, throwable.toString(), BaseTransientBottomBar.LENGTH_SHORT).show()
    }

    private fun success(finished: Boolean) {
        if (finished) {
            val result = Bundle()
            result.putBoolean("success", true)
            parentFragmentManager.setFragmentResult("loginSuccess", result)
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