package com.alttabtechnologies.inventorytracker.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.fragment.findNavController
import com.alttabtechnologies.inventorytracker.databinding.FragmentInventoryListBinding
import com.alttabtechnologies.inventorytracker.viewmodel.InventoryViewModel
import com.alttabtechnologies.inventorytracker.R
import com.alttabtechnologies.inventorytracker.viewmodel.InventoryViewModelFactory
import com.alttabtechnologies.inventorytracker.viewmodel.SortCriteria
import com.alttabtechnologies.inventorytracker.viewmodel.SortOrder

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
    private lateinit var mainAdapter: InventoryAdapter
    private lateinit var searchAdapter: InventoryAdapter

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

        // Wire the searchbars navigation icon to open the nav drawer.
        binding.searchBar.setNavigationOnClickListener { (requireActivity() as? MainActivity)?.openDrawer() }

        // Set custom icon for filter menu in search bar.
        binding.searchBar.overflowIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_sort)

        // Initialize UI
        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    // Setup RecyclerView component, configures layout, and the item click listener callback.
    private fun setupRecyclerView() {

        // Shared navigation click handler for item selected from main list and search view.
        val onContainerItemClick: (com.alttabtechnologies.inventorytracker.model.InventoryItem) -> Unit = { item ->

            // Close overlay if open.
            binding.searchView.setText("")
            viewModel.setSearchQuery("")
            binding.searchView.hide()

        // Pass selected item ID by navigation arguments to populate details.
            val bundle = Bundle().apply {
                putInt("itemId", item.id)
            }
            findNavController().navigate(
                R.id.action_inventoryListFragment_to_inventoryDetailFragment,
                bundle
            )
        }

        // Initialize main list adapter.
        mainAdapter = InventoryAdapter(onContainerItemClick)
        binding.rvInventory.apply{
            adapter = mainAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        // Initialize search adapter.
        searchAdapter = InventoryAdapter(onContainerItemClick)
        binding.rvSearchResults.apply {
            adapter = searchAdapter
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

        // Connect SearchView to SearchBar.
        binding.searchView.setupWithSearchBar(binding.searchBar)

        // Clear search text and reset query when search is closed with back arrow.
        binding.searchView.toolbar.setNavigationOnClickListener {
            binding.searchView.setText("")
            viewModel.setSearchQuery("")
            binding.searchView.hide()
        }

        // Listens to text modifications within SearchView.
        binding.searchView.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setSearchQuery(s?.toString().orEmpty())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Dismiss keyboard and clear focus on actions.
        binding.searchView.editText.setOnEditorActionListener { _, _, _ ->
            binding.searchView.hide()
            false
        }

        // Sort menu selection listener.
        binding.searchBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_sort_name -> {
                    viewModel.toggleSortDirection(SortCriteria.NAME)
                    true
                }
                R.id.action_sort_quantity -> {
                    viewModel.toggleSortDirection(SortCriteria.QUANTITY)
                    true
                }
                R.id.action_sort_price -> {
                    viewModel.toggleSortDirection(SortCriteria.PRICE)
                    true
                }
                R.id.action_sort_reset -> {
                    viewModel.resetSorting()
                    true
                }
                else -> false
            }
        }
    }

    // Subscribes to data stream states inside logic layer to keep UI synced.
    private fun observeViewModel() {

        // UI updates automatically when database changes.
        viewModel.filteredInventoryItems.observe(viewLifecycleOwner) { items ->

            // Update main list and live search results
            mainAdapter.submitList(items)
            searchAdapter.submitList(items)

            // Toggle visibility based on live storage state.
            if (items.isNullOrEmpty()) {
                binding.tvEmptyState.visibility = View.VISIBLE
                binding.rvInventory.visibility = View.GONE

            } else {
                binding.tvEmptyState.visibility = View.GONE
                binding.rvInventory.visibility = View.VISIBLE
            }
        }

        // Observer to update menu item indicators when SortState changes.
        viewModel.sortState.observe(viewLifecycleOwner) { state ->
            binding.chipGroupSort.removeAllViews()

            // Hide chip group container when no sort rules applied.
            binding.chipGroupSort.visibility = if (state.activeRules.isEmpty()) View.GONE else View.VISIBLE

            state.activeRules.forEachIndexed { index, rule ->
                val chip = com.google.android.material.chip.Chip(requireContext()).apply {
                    val rank = if (state.activeRules.size > 1) "${index + 1}. " else ""
                    val criteriaName = when (rule.criteria) {
                        SortCriteria.NAME -> "Name"
                        SortCriteria.PRICE -> "Price"
                        SortCriteria.QUANTITY -> "Quantity"
                    }

                    text = "$rank$criteriaName"

                    // Set vector icon for sorting directions.
                    isChipIconVisible = true
                    val iconRes = if (rule.order == SortOrder.ASCENDING) {
                        R.drawable.ic_arrow_up
                    } else {
                        R.drawable.ic_arrow_down
                    }
                    setChipIconResource(iconRes)

                    // Tapping chip body only toggles sorting direction.
                    setOnClickListener {
                        viewModel.toggleSortDirection(rule.criteria)
                    }

                    // Only tapping 'x' removes the filter rule.
                    isCloseIconVisible = true
                    setOnCloseIconClickListener {
                        viewModel.removeSortRule(rule.criteria)
                    }
                }
                binding.chipGroupSort.addView(chip)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Clears binding to prevent memory leaks.
        _binding = null
    }
}