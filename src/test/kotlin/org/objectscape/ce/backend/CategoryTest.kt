package org.objectscape.ce.backend

import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.objectscape.ce.backend.model.Category
import org.objectscape.ce.backend.storage.CategoryAlreadyExists
import org.objectscape.ce.backend.storage.DatabaseException
import org.objectscape.ce.backend.util.TestDatabase

class CategoryTest : AbstractTest() {

    @Before
    fun setUp() {
        testDatabase = TestDatabase(TestDatabase.DBFile)
        assertNotNull(testDatabase)
    }

    @After
    fun tearDown() {
        testDatabase.close()
    }

    @Test
    fun createCategoryRepository() {
        val categoriesStore = getCategoriesStore()
        assertNotNull(categoriesStore)
    }

    @Test
    fun getRootCategory() {
        val rootCategory = getCategoriesStore().getRootCategory()
        assertTrue(rootCategory.isRoot())
    }

    @Test
    fun addCategory() {
        val categoriesStore = getCategoriesStore()
        val rootCategory = categoriesStore.getRootCategory()
        testDatabase.deleteAllCategoriesExceptRootCategory();

        var newCategory = Category(-1, rootCategory.id, "SubCategory")
        newCategory = categoriesStore.addCategory(listOf(rootCategory.id), newCategory)
        assertNotNull(newCategory.id)
        assertTrue(newCategory.id > 1)
        assertFalse(newCategory.isRoot())

        var exceptionOccurred = false

        try {
            categoriesStore.addCategory(listOf(rootCategory.id), newCategory)
        } catch (e: Exception) {
            exceptionOccurred = true
            assertTrue(e is CategoryAlreadyExists)
        }

        assertTrue(exceptionOccurred)
    }

    @Test
    fun addCategoryChildrenOneLevel() {
        val categoriesStore = getCategoriesStore()
        val rootCategory = categoriesStore.getRootCategory()
        testDatabase.deleteAllCategoriesExceptRootCategory()

        var newCategory = Category(-1, rootCategory.id, "SubCategory1Level1")
        newCategory = categoriesStore.addCategory(listOf(rootCategory.id), newCategory)
        assertNotNull(newCategory.id)
        assertTrue(newCategory.id > 1)
        assertFalse(newCategory.isRoot())

        newCategory = Category(-1, rootCategory.id, "SubCategory2Level1")
        newCategory = categoriesStore.addCategory(listOf(rootCategory.id), newCategory)
        assertNotNull(newCategory)
        assertNotNull(newCategory.id)
        assertTrue(newCategory.id > 1)
        assertFalse(newCategory.isRoot())

        newCategory = Category(-1, rootCategory.id, "SubCategory3Level1")
        newCategory = categoriesStore.addCategory(listOf(rootCategory.id), newCategory)
        assertNotNull(newCategory)
        assertNotNull(newCategory.id)
        assertTrue(newCategory.id > 1)
        assertFalse(newCategory.isRoot())

        val childrenCount = categoriesStore.getChildrenCountFirstLevel(rootCategory.id)
        assertTrue(childrenCount == 3)

        testDatabase.deleteAllCategoriesExceptRootCategory()
    }

    @Test
    fun addCategoryChildrenTwoLevels() {
        addCategoryChildrenTwoLevels(true)
    }

    @Test(expected = DatabaseException::class)
    fun addCategoryIdMismatch() {
        val categoriesStore = getCategoriesStore()
        val rootCategory = categoriesStore.getRootCategory()
        val newCategory = Category(-1, rootCategory.id + 1, "Subcategory")
        categoriesStore.addCategory(listOf(rootCategory.id), newCategory)
    }

}