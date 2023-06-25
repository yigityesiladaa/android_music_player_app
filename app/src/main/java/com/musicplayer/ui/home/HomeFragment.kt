package com.musicplayer.ui.home

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.musicplayer.R
import com.musicplayer.common.utils.Utils
import com.musicplayer.databinding.FragmentHomeBinding
import com.musicplayer.firebase.repositories.FirebaseAuthRepository
import com.musicplayer.listeners.IHome
import com.musicplayer.models.Music
import com.musicplayer.retrofit.configs.ApiClient
import com.musicplayer.retrofit.services.IMusicService

class HomeFragment : Fragment(), IHome {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var customMusicsAdapter: CustomMusicCategoriesAdapter
    private lateinit var navController: NavController
    var categoryTitles = mutableListOf<String>()
    var musicTitles = hashMapOf<String, List<Music>>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        registerEvents()
        listenEvents()
    }

    private fun init(view: View) {
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        navController = Navigation.findNavController(view)
        Thread {
            homeViewModel.getAll()
        }.start()
        customMusicsAdapter = CustomMusicCategoriesAdapter(requireContext(), this)
        homeViewModel.musicService = ApiClient.getClient().create(IMusicService::class.java)

    }

    private fun registerEvents() {
        binding.musicCategoriesListView.setAdapter(customMusicsAdapter)
        homeViewModel.setContext(requireContext())
        binding.customToolbar.btnSignOutImage.setOnClickListener {
            homeViewModel.signOut()
        }
        binding.customToolbar.btnFavorites.setOnClickListener {
            navController.navigate(R.id.action_homeFragment_to_favoritesFragment)
        }
    }

    private fun listenEvents() {

        homeViewModel.state.observe(viewLifecycleOwner) { baseState ->
            Utils.checkState(activity, baseState, onLoading()) {
                binding.progressBar.visibility = View.GONE
                binding.musicCategoriesListView.visibility = View.VISIBLE
            }

            if (FirebaseAuthRepository.getCurrentUserId() == null) {
                navController.navigate(R.id.action_homeFragment_to_signInFragment)
            }
        }

        homeViewModel.categoryTitles.observe(viewLifecycleOwner) { list ->
            categoryTitles = list
            customMusicsAdapter.submitHeaderList(list)
        }

        homeViewModel.musicList.observe(viewLifecycleOwner) { hashMap ->
            activity?.let {
                it.runOnUiThread {
                    musicTitles = hashMap
                    customMusicsAdapter.submitBodyList(hashMap)
                }
            }
        }
    }

    private fun onLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.musicCategoriesListView.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onListViewItemClick(music: Music) {
        val bundle = Bundle()
        bundle.putParcelable("music", music)
        navController.navigate(R.id.action_homeFragment_to_playerFragment, bundle)
    }

    override fun expandCollapseListView(position: Int) {
        if (binding.musicCategoriesListView.isGroupExpanded(position)) {
            binding.musicCategoriesListView.collapseGroup(position)
        } else {
            binding.musicCategoriesListView.expandGroup(position)
        }
    }

}