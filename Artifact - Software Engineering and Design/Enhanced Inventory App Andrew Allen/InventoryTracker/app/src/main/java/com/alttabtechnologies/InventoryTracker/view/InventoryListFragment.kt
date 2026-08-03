package com.alttabtechnologies.inventorytracker.view

import android.os.Bundle
import android.view.*
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.fragment.findNavController
import com.alttabtechnologies.inventorytracker.databinding.FragmentInventoryListBinding
import com.alttabtechnologies.inventorytracker.viewmodel.InventoryViewModel
import com.alttabtechnologies.inventorytracker.R
import com.alttabtechnologies.inventorytracker.viewmodel.InventoryViewModelFactory

/**
 * Fragment responsible for displaying the inventory list.
 * Handles user interactions for selecting existing items and updating them.
 * Also, provides a means of adding new items.
 */

class InventoryListFragment : Fragment() {

    // Backing for view binding to handle life cycle.
    private var _binding: FragmentInventoryListBinding? = null
    private val binding get() = _binding!!

    // Scopes ViewModel for view binding to ensure shared state among fragments.
    private val viewModel: InventoryViewModel by activityViewModels() {
        InventoryViewModelFactory.create()
    }

    // Handles layout rendering and mapping for individual inventory items.
    private lateinit var adapter: InventoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflates view layout using View Binding.
        _binding = FragmentInventoryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize UI
        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    // Setup RecyclerView component, configures layout, and the item click listener callback.
    private fun setupRecyclerView() {

        // Clicking an item routes directly to the detail view.
        adapter = InventoryAdapter { item ->

        // Pass selected item ID by navigation arguments to populate details.
            val bundle = Bundle().apply {
                putInt("itemId", item.id)
            }
            findNavController().navigate(
                R.id.action_inventoryListFragment_to_inventoryDetailFragment,
                bundle
            )
        }

        binding.rvInventory.apply{
            adapter = this@InventoryListFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    // Attaches interaction listeners to visual buttons.
    private fun setupListeners() {

        // Floating action button for adding new items.
        binding.fabAddItem.setOnClickListener {

            // Passing -1 signals detail view to provide a clean insert instead of edit.
            val bundle = Bundle().apply {
                putInt("itemId", -1)

            }
            findNavController().navigate(
                R.id.action_inventoryListFragment_to_inventoryDetailFragment,
                bundle
            )
        }
    }

    // Subscribes to data stream states inside logic layer to keep UI synced.
    private fun observeViewModel() {

        // UI updates automatically when database changes.
        viewModel.allInventoryItems.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)

            // Toggle visibility based on live storage state.
            if (items.isNullOrEmpty()) {
                binding.tvEmptyState.visibility = View.VISIBLE
                binding.rvInventory.visibility = View.GONE

            } else {
                binding.tvEmptyState.visibility = View.GONE
                binding.rvInventory.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Clears binding to prevent memory leaks.
        _binding = null
    }
}