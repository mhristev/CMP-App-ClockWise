// Test file to demonstrate the summary text generation

import com.clockwise.features.consumption.domain.model.ConsumptionItem
import com.clockwise.features.consumption.domain.model.SelectedConsumptionItem
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Clock

// Mock data for testing
val mockItem1 = ConsumptionItem(
    id = "1",
    name = "Coffee",
    price = 3.50,
    type = "Beverage",
    businessUnitId = "bu1",
    createdAt = Clock.System.now().toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()),
    updatedAt = Clock.System.now().toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
)

val mockItem2 = ConsumptionItem(
    id = "2", 
    name = "Sandwich",
    price = 8.00,
    type = "Food",
    businessUnitId = "bu1",
    createdAt = Clock.System.now().toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()),
    updatedAt = Clock.System.now().toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
)

// Test cases
fun testSummaryGeneration() {
    // Test 1: Note only
    val summaryNoteOnly = generateSummaryText(
        note = "Completed inventory check and cleaned work area",
        selectedConsumptionItems = emptyList()
    )
    println("Test 1 - Note Only:")
    println(summaryNoteOnly)
    println("=" * 50)
    
    // Test 2: Consumption items only
    val selectedItems = listOf(
        SelectedConsumptionItem(mockItem1, 2),
        SelectedConsumptionItem(mockItem2, 1)
    )
    val summaryItemsOnly = generateSummaryText(
        note = "",
        selectedConsumptionItems = selectedItems
    )
    println("Test 2 - Items Only:")
    println(summaryItemsOnly)
    println("=" * 50)
    
    // Test 3: Both note and items
    val summaryBoth = generateSummaryText(
        note = "Handled customer complaints and restocked supplies",
        selectedConsumptionItems = selectedItems
    )
    println("Test 3 - Both Note and Items:")
    println(summaryBoth)
    println("=" * 50)
    
    // Test 4: Neither note nor items
    val summaryEmpty = generateSummaryText(
        note = "",
        selectedConsumptionItems = emptyList()
    )
    println("Test 4 - Empty:")
    println(summaryEmpty)
}

/* Expected Output:

Test 1 - Note Only:
Session Notes:
Completed inventory check and cleaned work area

==================================================

Test 2 - Items Only:
Consumption Items (2 selected):
• Coffee x2 = $7.0
• Sandwich x1 = $8.0

Total Cost: $15.0

==================================================

Test 3 - Both Note and Items:
Session Notes:
Handled customer complaints and restocked supplies

Consumption Items (2 selected):
• Coffee x2 = $7.0
• Sandwich x1 = $8.0

Total Cost: $15.0

==================================================

Test 4 - Empty:
No additional notes or consumption items for this session.

*/