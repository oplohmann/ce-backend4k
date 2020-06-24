package org.objectscape.ce.backend

import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.objectscape.ce.backend.model.Item
import org.objectscape.ce.backend.util.TestDatabase
import java.util.*

class ItemsTest : AbstractTest() {

    @Before
    fun setUp() {
        testDatabase = TestDatabase(TestDatabase.DBFile)
        Assert.assertNotNull(testDatabase)
    }

    @After
    fun tearDown() {
        testDatabase.close()
    }

    @Test
    fun addItem() {
        val itemsStore = testDatabase.itemsStore
        testDatabase.deleteAllItems()

        val insertedCategories = addCategoryChildrenTwoLevels(false).values

        val now = Date()
        val timeNow = now.time
        var newItem = Item(-1, "text-" + timeNow, "note-" + timeNow, now)
        itemsStore.addItem(newItem, insertedCategories)

        assertTrue(newItem.id > 0)
        val itemIds = itemsStore.getItemsIds(insertedCategories)

        assertEquals(1, itemIds.size)
        assertEquals(itemIds.get(0), newItem.id)

        val items = itemsStore.getItems(itemIds)

        assertEquals(1, items.size)

        val loadedItem = items.get(0)
        assertEquals(loadedItem.id, newItem.id)
        assertEquals(loadedItem.text, newItem.text)
        assertEquals(loadedItem.note, newItem.note)
        assertEquals(loadedItem.entryDate.time, timeNow)
        assertEquals(loadedItem.lastChanged.time, timeNow)

        testDatabase.deleteAllItems()
        testDatabase.deleteAllCategoriesExceptRootCategory()
    }
    
}