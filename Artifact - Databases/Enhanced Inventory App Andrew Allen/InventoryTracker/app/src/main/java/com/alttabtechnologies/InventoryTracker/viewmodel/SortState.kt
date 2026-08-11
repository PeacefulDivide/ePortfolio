package com.alttabtechnologies.inventorytracker.viewmodel

/**
 *  Encapsulates a single sorting rule (NAME, QUANTITY, OR PRICE)
 *  and binds attributes to a specific sort direction (ASC or DSC).
 */

data class SortRule (
    val criteria: SortCriteria,
    val order: SortOrder = SortOrder.ASCENDING
)

// Immutable state container holding current sorting config for inventory list.
// Default is empty list.
data class SortState(
    val activeRules: List<SortRule> = emptyList()
)