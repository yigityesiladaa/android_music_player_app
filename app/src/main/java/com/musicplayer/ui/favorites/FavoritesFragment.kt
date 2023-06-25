package com.musicplayer.ui.favorites

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.musicplayer.R
import com.musicplayer.common.utils.Utils
import com.musicplayer.databinding.FragmentFavoritesBinding
import com.musicplayer.listeners.IFavorites
import com.musicplayer.models.Music

class FavoritesFragment : Fragment(), IFavorites {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private lateinit var favoritesViewModel: FavoritesViewModel
    private lateinit var customFavoritesAdapter: CustomFavoritesAdapter
    private lateinit var navController: NavController
    private var favorites = mutableListOf<Music>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        registerEvents()
        listenEvents()
    }

    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        favoritesViewModel = ViewModelProvider(this)[FavoritesViewModel::class.java]
        favoritesViewModel.setContext(requireContext())
        customFavoritesAdapter = CustomFavoritesAdapter(requireContext(), this)
        favoritesViewModel.getAllFavorites()
    }

    private fun registerEvents() {
        binding.customToolbar.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.favoritesListView.adapter = customFavoritesAdapter
        binding.favoritesListView.setOnItemClickListener { parent, view, position, id ->
            val bundle = Bundle()
            bundle.putParcelable("music", favorites[position])
            navController.navigate(R.id.action_favoritesFragment_to_playerFragment, bundle)
        }
    }

    private fun listenEvents() {
        favoritesViewModel.state.observe(viewLifecycleOwner) { baseState ->
            Utils.checkState(activity, baseState, onLoading()) {
                binding.favoritesProgressBar.visibility = View.GONE
                binding.favoritesListView.visibility = View.VISIBLE
                checkListIsEmpty()
            }
        }

        favoritesViewModel.favorites.observe(viewLifecycleOwner) {
            favorites = it
            customFavoritesAdapter.submitList(it)
        }
    }

    private fun checkListIsEmpty() {
        if (favorites.isEmpty()) {
            binding.txtNothingToShowYet.visibility = View.VISIBLE
            binding.favoritesListView.visibility = View.GONE
        } else {
            binding.txtNothingToShowYet.visibility = View.GONE
            binding.favoritesListView.visibility = View.VISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        favoritesViewModel.getAllFavorites()
    }

    private fun onLoading() {
        binding.favoritesProgressBar.visibility = View.VISIBLE
        binding.favoritesListView.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onDeleteFromFavoritesClickListener(music: Music) {
        favorites.remove(music)
        favoritesViewModel.removeFromFavorite(music.mid)
        customFavoritesAdapter.submitList(favorites)
    }


}