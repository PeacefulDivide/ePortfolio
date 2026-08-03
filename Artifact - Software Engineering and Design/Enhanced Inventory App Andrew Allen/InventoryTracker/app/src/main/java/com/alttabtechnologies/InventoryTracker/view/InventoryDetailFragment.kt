package com.alttabtechnologies.inventorytracker.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.alttabtechnologies.inventorytracker.model.InventoryItem
import com.alttabtechnologies.inventorytracker.viewmodel.InventoryViewModel
import com.alttabtechnologies.inventorytracker.databinding.FragmentInventoryDetailBinding
import com.alttabtechnologies.inventorytracker.viewmodel.InventoryViewModelFactory

/**
 * Fragment for displaying, validating, and updating details
 * for inventory items. Uses a shared activity ViewModel to
 * maintain state across the CRUD navigation graph.
 */

class InventoryDetailFragment : Fragment() {

    // Backing property for view binding to handle life cycle.
    private var _binding: FragmentInventoryDetailBinding? = null
    private val binding get() = _binding!!

    // Scopes the parent activity to ensure same instance across fragments.
    private val viewModel: InventoryViewModel by activityViewModels() {
        InventoryViewModelFactory.create()
    }

    // Tracks current item ID and provides a clean insert.
    private var currentItemId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Extracts arguments from navigation bundle.
        arguments?.let {
            currentItemId = it.getInt("itemId", -1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View {

        // Inflates the view using View Binding.
        _binding = FragmentInventoryDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize view components, load state, and subscribes to streams.
        loadItemDetails()
        setupActionListeners()
        observeNavigationEvents()
    }

    // Queries ViewModel for inventory item and observes changes inside life cycle scope.
    private fun loadItemDetails() {
        if (currentItemId == -1) {
            return
        }

        // Fetch target instance out of live dataset stream.
        viewModel.getItemById(currentItemId).observe(viewLifecycleOwner) { item ->
            item?.let { populateUI(it) }
        }
    }

    // Binds core data parameters form InventoryItem into editable input views.
    private fun populateUI(item: InventoryItem) {
        binding.apply {
            etItemName.setText(item.name)
            etItemSku.setText(item.sku)
            etItemPrice.setText(item.price.toString())
            etItemThreshold.setText(item.threshold.toString())
            etItemQuantity.setText(item.quantity.toString())
        }
    }

    // Wires up visual component listeners for the save confirmation action.
    private fun setupActionListeners() {
        binding.btnSaveItem.setOnClickListener {
            saveChanges()
        }
    }

    // Sanitizes inputs, runs validations, and performs updates to the data layer.
    private fun saveChanges() {

        // Resets text fields from input errors.
        binding.tilItemName.error = null
        binding.tilItemSku.error = null

        // Collects and sanitizes input values.
        val updatedName = binding.etItemName.text.toString().trim()
        val updatedSku = binding.etItemSku.text.toString().trim()
        val updatedQuantity = binding.etItemQuantity.text.toString().toIntOrNull() ?: 0
        val updatedPrice = binding.etItemPrice.text.toString().toDoubleOrNull() ?: 0.0
        val updatedThreshold = binding.etItemThreshold.text.toString().toIntOrNull() ?: 0

        // Validation rule. Item must have a name.
        if (updatedName.isEmpty()) {
            binding.tilItemName.error = "Name cannot be empty"
            return
        }

        // Pass validation changes down to view model.
        viewModel.updateItemDetails(
            id = currentItemId,
            name = updatedName,
            sku = updatedSku,
            quantity = updatedQuantity,
            price = updatedPrice,
            threshold = updatedThreshold
        )
    }

    // Subscribes transaction state to logic layer to perform UI transitions.
    private fun observeNavigationEvents() {

        // Only navigate back when the save operation confirms completion.
        viewModel.saveOperationComplete.observe(viewLifecycleOwner) { isComplete ->
            if (isComplete) {
                Toast.makeText(requireContext(), "Inventory tracking saved.", Toast.LENGTH_SHORT).show()
                viewModel.resetSaveStatus()

                // Pop detail fragment off nav stack to return to inventory list.
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Clear binding reference to prevent memory leaks during fragment life cycle changes.
        _binding = null
    }
}