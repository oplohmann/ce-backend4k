package org.objectscape.ce.backend

import org.junit.Assert
import org.junit.Test
import org.objectscape.ce.backend.model.Category
import org.objectscape.ce.backend.storage.CategoryStore
import org.objectscape.ce.backend.storage.CategoryViewsStore
import org.objectscape.ce.backend.util.TestCategoryViewsStore
import org.objectscape.ce.backend.util.TestDatabase
import org.objectscape.ce.backend.util.TestViewsStore

abstract class AbstractTest {

    lateinit protected var testDatabase : TestDatabase

    fun addCategoryChildrenTwoLevels(clearNonRootCategoriesAfterTest: Boolean) : Map<String, Category> {
        val categories = HashMap<String, Category>()

        val categoriesStore = testDatabase.categoryStore
        val rootCategory = categoriesStore.getRootCategory()
        testDatabase.deleteAllCategoriesExceptRootCategory()

        var name = "Category1_1"
        var category1_1 = Category(-1, rootCategory.id, name)
        category1_1 = categoriesStore.addCategory(listOf(rootCategory.id), category1_1)
        Assert.assertNotNull(category1_1.id)
        Assert.assertTrue(category1_1.id > 1)
        Assert.assertFalse(category1_1.isRoot())

        categories.put(name, category1_1)

        name = "Category2_1"
        var category2_1 = Category(-1, category1_1.id, name)
        category2_1 = categoriesStore.addCategory(listOf(rootCategory.id, category1_1.id), category2_1)
        Assert.assertNotNull(category2_1.id)
        Assert.assertTrue(category2_1.id > 1)
        Assert.assertFalse(category2_1.isRoot())

        categories.put(name, category2_1)

        name = "Category3_1"
        var category3_1 = Category(-1, category2_1.id, name)
        category3_1 = categoriesStore.addCategory(listOf(rootCategory.id, category1_1.id, category2_1.id), category3_1)
        Assert.assertNotNull(category3_1.id)
        Assert.assertTrue(category3_1.id > 1)
        Assert.assertFalse(category3_1.isRoot())

        categories.put(name, category3_1)

        name = "Category3_2"
        var category3_2 = Category(-1, category2_1.id, name)
        category3_2 = categoriesStore.addCategory(listOf(rootCategory.id, category1_1.id, category2_1.id), category3_2)
        Assert.assertNotNull(category3_2.id)
        Assert.assertTrue(category3_2.id > 1)
        Assert.assertFalse(category3_2.isRoot())

        categories.put(name, category3_2)

        var parentIds = categoriesStore.getParentIds(category3_1.id)
        Assert.assertTrue(parentIds.size == 3)
        Assert.assertEquals(parentIds.get(0), category2_1.id)
        Assert.assertEquals(parentIds.get(1), category1_1.id)
        Assert.assertEquals(parentIds.get(2), rootCategory.id)

        parentIds = categoriesStore.getParentIds(category2_1.id)
        Assert.assertTrue(parentIds.size == 2)
        Assert.assertEquals(parentIds.get(0), category1_1.id)
        Assert.assertEquals(parentIds.get(1), rootCategory.id)

        var childIds = categoriesStore.getChildrenIds(rootCategory.id)
        Assert.assertTrue(childIds.size == 4)
        Assert.assertEquals(childIds.get(0), category1_1.id)
        Assert.assertEquals(childIds.get(1), category2_1.id)
        Assert.assertEquals(childIds.get(2), category3_1.id)
        Assert.assertEquals(childIds.get(3), category3_2.id)

        childIds = categoriesStore.getChildrenIds(category1_1.id)
        Assert.assertTrue(childIds.size == 3)
        Assert.assertEquals(childIds.get(0), category2_1.id)
        Assert.assertEquals(childIds.get(1), category3_1.id)
        Assert.assertEquals(childIds.get(2), category3_2.id)

        childIds = categoriesStore.getChildrenIds(category2_1.id)
        Assert.assertTrue(childIds.size == 2)
        Assert.assertEquals(childIds.get(0), category3_1.id)
        Assert.assertEquals(childIds.get(1), category3_2.id)

        if(clearNonRootCategoriesAfterTest) {
            testDatabase.deleteAllCategoriesExceptRootCategory()
        }

        return categories
    }

    protected fun getTestViewsStore() : TestViewsStore {
        return testDatabase.testViewsStore
    }

    protected fun getCategoryViewsStore(): TestCategoryViewsStore {
        return testDatabase.testCategoryViewsStore
    }

    protected fun getCategoriesStore() : CategoryStore {
        val categoriesStore = testDatabase.categoryStore
        Assert.assertNotNull(categoriesStore)
        return categoriesStore
    }

    fun ensureRootChildCategoryExists(name: String): Category {
        val rootCategoryId = getCategoriesStore().getRootCategory().id
        var category = getCategoriesStore().loadCategory(name, rootCategoryId)
        if(category != null) {
            return category
        }
        return getCategoriesStore().addCategory(listOf(rootCategoryId), Category(-1, rootCategoryId, name))
    }

}