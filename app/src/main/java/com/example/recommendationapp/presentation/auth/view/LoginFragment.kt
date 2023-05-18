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
import com.example.recommendationapp.databinding.FragmentLoginBinding
import com.example.recommendationapp.domain.interactor.DatabaseInteractor
import com.example.recommendationapp.domain.interactor.LocalInteractor
import com.example.recommendationapp.domain.interactor.RecommendationInteractor
import com.example.recommendationapp.domain.model.RestaurantShort
import com.example.recommendationapp.presentation.auth.viewmodel.AuthViewModel
import com.example.recommendationapp.presentation.auth.viewmodel.AuthViewModelFactory
import com.example.recommendationapp.presentation.onboarding.search.view.SearchActivity
import com.example.recommendationapp.utils.scheduler.SchedulerProvider
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class LoginFragment : Fragment() {
    lateinit var binding: FragmentLoginBinding
    lateinit var viewModel: AuthViewModel

    private var halfDataSaved = false
    private var favIds = listOf<Int>()
    private var markIds = listOf<Int>()

    @Inject
    lateinit var recommendationInteractor: RecommendationInteractor
    @Inject
    lateinit var localInteractor: LocalInteractor
    @Inject
    lateinit var databaseInteractor: DatabaseInteractor
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
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createViewModel()
        observeLiveData()

        binding.createBtn.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.login_fragment_container, RegisterFragment.newInstance(), RegisterFragment.TAG)
                .commit()
        }
        binding.enterBtn.setOnClickListener {
            viewModel.login(
                binding.inputEmail.editText?.text.toString().trim(),
                binding.inputPassword.editText?.text.toString().trim()
            )
        }
    }

    private fun createViewModel() {
        viewModel = ViewModelProvider(
            this,
            AuthViewModelFactory(recommendationInteractor, localInteractor, databaseInteractor, schedulers)
        )[AuthViewModel::class.java]
    }

    private fun observeLiveData() {
        viewModel.getRestaurantIdsLiveData(true).observe(viewLifecycleOwner, this::putLikeIds)
        viewModel.getRestaurantIdsLiveData(false).observe(viewLifecycleOwner, this::putMarkIds)
        viewModel.getErrorLiveData().observe(viewLifecycleOwner, this::showError)
        viewModel.getSuccessLiveData().observe(viewLifecycleOwner, this::success)
        viewModel.getLikesSavedLiveData().observe(viewLifecycleOwner, this::dataSaved)
        viewModel.getMarksSavedLiveData().observe(viewLifecycleOwner, this::dataSaved)
    }

    private fun showError(throwable: Throwable) {
        binding.wrongCredentials.visibility = View.VISIBLE
    }

    private fun success(token: String) {
        binding.wrongCredentials.visibility = View.GONE
        viewModel.sendLikesToAccount(token, favIds)
        viewModel.sendMarksToAccount(token, markIds)
    }

    private fun putLikeIds(restaurantIds: List<Int>) {
        favIds = restaurantIds
    }

    private fun putMarkIds(restaurantIds: List<Int>) {
        markIds = restaurantIds
    }

    private fun dataSaved(saved: Boolean) {
        if (saved) {
            if (halfDataSaved) {
                val result = Bundle()
                result.putBoolean("success", true)
                parentFragmentManager.setFragmentResult("loginSuccess", result)
            } else
                halfDataSaved = true
        }
    }

    companion object {
        const val TAG = "LoginFragment"
        private const val TAG_ADD = "$TAG ADD"
        private const val TAG_ERROR = "$TAG ERROR"
        private const val TAG_PROGRESS = "$TAG PROGRESS"

        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }
}