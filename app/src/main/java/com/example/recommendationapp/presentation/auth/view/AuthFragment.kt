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
import com.example.recommendationapp.databinding.FragmentAuthBinding
import com.example.recommendationapp.domain.interactor.DatabaseInteractor
import com.example.recommendationapp.domain.interactor.LocalInteractor
import com.example.recommendationapp.domain.interactor.RecommendationInteractor
import com.example.recommendationapp.domain.model.AccountLocal
import com.example.recommendationapp.presentation.auth.viewmodel.AuthViewModel
import com.example.recommendationapp.presentation.auth.viewmodel.AuthViewModelFactory
import com.example.recommendationapp.presentation.favourite.view.FavouriteFragment
import com.example.recommendationapp.presentation.onboarding.search.view.SearchActivity
import com.example.recommendationapp.utils.scheduler.SchedulerProvider
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class AuthFragment : Fragment() {
    private lateinit var binding: FragmentAuthBinding
    lateinit var viewModel: AuthViewModel

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
        binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createViewModel()
        observeLiveData()

        viewModel.getAccount()

        childFragmentManager
            .setFragmentResultListener("loginSuccess", this) { _, _ ->
                childFragmentManager
                    .beginTransaction()
                    .replace(R.id.login_fragment_container, AccountFragment.newInstance(), AccountFragment.TAG)
                    .commit()
            }
    }

    private fun createViewModel() {
        viewModel = ViewModelProvider(
            this,
            AuthViewModelFactory(recommendationInteractor, localInteractor, databaseInteractor, schedulers)
        )[AuthViewModel::class.java]
    }

    private fun observeLiveData() {
        viewModel.getErrorLiveData().observe(viewLifecycleOwner, this::showError)
        viewModel.getAccountLiveData().observe(viewLifecycleOwner, this::showAccount)
    }

    private fun showError(throwable: Throwable) {
        Log.d(SearchActivity.TAG, "showError() called with: throwable = $throwable")
        Snackbar.make(binding.root, throwable.toString(), BaseTransientBottomBar.LENGTH_SHORT).show()
    }

    private fun showAccount(account: AccountLocal) {
        if (account.email == "") {
            childFragmentManager
                .beginTransaction()
                .replace(R.id.login_fragment_container, RegisterFragment.newInstance(), RegisterFragment.TAG)
                .commit()
        } else {
            childFragmentManager
                .beginTransaction()
                .replace(R.id.login_fragment_container, AccountFragment.newInstance(), AccountFragment.TAG)
                .commit()
        }
    }

    companion object {
        const val TAG = "AuthFragment"
        private const val TAG_ADD = "$TAG ADD"
        private const val TAG_ERROR = "$TAG ERROR"
        private const val TAG_PROGRESS = "$TAG PROGRESS"

        fun newInstance(): AuthFragment {
            return AuthFragment()
        }
    }
}