package org.objectscape.ce.backend

import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.objectscape.ce.backend.model.Category
import org.objectscape.ce.backend.model.CategoryView
import org.objectscape.ce.backend.model.View
import org.objectscape.ce.backend.storage.exceptions.ViewAlreadyExists
import org.objectscape.ce.backend.util.TestDatabase
import org.sqlite.SQLiteException
import java.lang.IndexOutOfBoundsException
import kotlin.collections.ArrayList

class ViewsTest  : AbstractTest() {

    @Before
    fun setUp() {
        testDatabase = TestDatabase(TestDatabase.DBFile)
        assertNotNull(testDatabase)
        testDatabase.deleteViewRelatedData()
    }

    @After
    fun tearDown() {
        testDatabase.deleteViewRelatedData()
        testDatabase.close()
    }

    @Test
    fun addView() {
        val viewsStore = testDatabase.viewsStore

        val rootView = viewsStore.getRootView()
        val view = View(-1, rootView.id, "SubView1_1")
        viewsStore.addView(view = view)

        assertTrue(view.id > 0)

        val error = viewsStore.addView(view)
        assertNotNull(error)
        assertTrue(error is ViewAlreadyExists)

        val subView = View(-1, view.id, "SubView2_1")
        viewsStore.addView(view = subView)

        assertTrue(subView.id > 0)
    }

    @Test
    fun insertCategoryViews() {
        testDatabase.deleteAllCategoryViews()
        var viewId = getTestViewsStore().ensureViewExists("MyView").id

        val category1 = ensureRootChildCategoryExists("Category1")
        val category2 = ensureRootChildCategoryExists("Category2")
        val category3 = ensureRootChildCategoryExists("Category3")

        val categoryViewsStore = getCategoryViewsStore()
        categoryViewsStore.testAddRawCategoryView(CategoryView(-1, viewId, category1.id, 0))
        categoryViewsStore.testAddRawCategoryView(CategoryView(-1, viewId, category2.id, 1))
        categoryViewsStore.testAddRawCategoryView(CategoryView(-1, viewId, category3.id, 2))

        val categoryViews = categoryViewsStore.getCategoryViewsOrdered(viewId)
        assertEquals(3, categoryViews.size)

        val loadedCategoryView1 = categoryViews.get(0)
        val loadedCategoryView2 = categoryViews.get(1)
        val loadedCategoryView3 = categoryViews.get(2)

        assertEquals(category1.id, loadedCategoryView1.categoryId)
        assertEquals(0, loadedCategoryView1.position)
        assertEquals(category2.id, loadedCategoryView2.categoryId)
        assertEquals(1, loadedCategoryView2.position)
        assertEquals(category3.id, loadedCategoryView3.categoryId)
        assertEquals(2, loadedCategoryView3.position)
    }

    @Test
    fun insertCategoryViewInOrder() {
        insertCategoryViews()

        val categoryViewsStore = getCategoryViewsStore()

        val view = getTestViewsStore().ensureViewExists("MyView")
        var viewId = view.id
        var categoryViews = categoryViewsStore.getCategoryViewsOrdered(viewId)
        assertEquals(3, categoryViews.size)

        var positions = categoryViews.map { it.position }.toSortedSet()
        assertTrue(positions.containsAll(listOf(0, 1, 2)))

        val category4 = ensureRootChildCategoryExists("Category4")
        getTestViewsStore().addCategoryToView(view, category4, 2, categoryViews)

        categoryViews = categoryViewsStore.getCategoryViewsOrdered(viewId)
        assertEquals(4, categoryViews.size)

        positions = categoryViews.map { it.position }.toSortedSet()
        assertTrue(positions.containsAll(listOf(0, 1, 2, 3)))
    }

    @Test
    fun moveCategoryView() {
        moveCategoryView(0, 1)
        moveCategoryView(0, 2)

        moveCategoryView(1, 0)
        moveCategoryView(1, 2)

        moveCategoryView(2, 0)
        moveCategoryView(2, 1)

        moveCategoryViewCheckWrongIndexGetsRejected()
    }

    private fun moveCategoryViewCheckWrongIndexGetsRejected() {
        var outOfRangeExceptionOccured = false;

        try {
            moveCategoryView(-1, 1)
        } catch (e: IndexOutOfBoundsException) {
            outOfRangeExceptionOccured = true
        }

        assertTrue(outOfRangeExceptionOccured)

        outOfRangeExceptionOccured = false

        try {
            moveCategoryView(0, 3)
        } catch (e: IndexOutOfBoundsException) {
            outOfRangeExceptionOccured = true
        }

        assertTrue(outOfRangeExceptionOccured)

        outOfRangeExceptionOccured = false

        try {
            moveCategoryView(1, 1)
        } catch (e: IndexOutOfBoundsException) {
            outOfRangeExceptionOccured = true
        }

        assertTrue(outOfRangeExceptionOccured)
    }

    private fun moveCategoryView(from: Int, to: Int) {
        testDatabase.deleteViewRelatedData()
        insertCategoryViews()

        val categoryViewsStore = getCategoryViewsStore()
        val viewStore = getTestViewsStore()
        val view = viewStore.ensureViewExists("MyView")
        var viewId = view.id
        var categoryViews = categoryViewsStore.getCategoryViewsOrdered(viewId)
        assertEquals(3, categoryViews.size)

        val categoryViewsAfterMove = viewStore.moveCategoryView(from, to, categoryViews)

        var categoryViewsAfterMoveReloaded = categoryViewsStore.getCategoryViewsOrdered(viewId)

        assertEquals(categoryViewsAfterMove, categoryViewsAfterMoveReloaded)
        assertEquals(3, categoryViewsAfterMove.size)
        assertEquals(from, categoryViewsAfterMove.get(from).position)
        assertEquals(to, categoryViewsAfterMove.get(to).position)

        assertEquals(listOf(0, 1, 2), categoryViewsAfterMove.map { it.position })
    }

    @Test
    fun deleteCategoryViewInOrder() {
        // delete from all position: start, middle, end (0, 1, 2)
        deleteCategoryViewInOrder(0)
        deleteCategoryViewInOrder(1)
        deleteCategoryViewInOrder(2)
    }

    fun deleteCategoryViewInOrder(indexOfItemToBeRemoved : Int) {
        testDatabase.deleteViewRelatedData()
        insertCategoryViews()

        val categoryViewsStore = getCategoryViewsStore()

        val view = getTestViewsStore().ensureViewExists("MyView")
        var viewId = view.id
        var categoryViews = categoryViewsStore.getCategoryViewsOrdered(viewId)
        assertEquals(3, categoryViews.size)

        var positions = categoryViews.map { it.position }.toSortedSet()
        assertTrue(positions.containsAll(listOf(0, 1, 2)))

        var categoryToBeRemoved = categoryViews.get(indexOfItemToBeRemoved)
        val updatedViews = getTestViewsStore().removeCategoryView(view, categoryToBeRemoved, categoryViews)

        assertTrue(updatedViews.size == 2)
        assertTrue(!updatedViews.contains(categoryToBeRemoved))
        assertTrue(!categoryToBeRemoved.isPersistent())

        assertEquals(listOf(0, 1), updatedViews.map { it.position }.toList())
    }

    @Test(expected = SQLiteException::class)
    fun insertCategoryViewDuplicateCategoryConstraint() {
        testDatabase.deleteAllCategoryViews()
        var viewId = getTestViewsStore().ensureViewExists("MyView").id

        // causes unique key constraint as category with id categoryId already exists for the same view
        val categoryId = 1L
        getCategoryViewsStore().testAddRawCategoryView(CategoryView(-1, viewId, categoryId, 0))
        getCategoryViewsStore().testAddRawCategoryView(CategoryView(-1, viewId, categoryId, 1))
    }

    @Test
    fun ensureViewExists() {
        testDatabase.deleteViewRelatedData()
        val view1 = getTestViewsStore().ensureViewExists("MyView")
        val view2 = getTestViewsStore().ensureViewExists("MyView")
        assertEquals(view1, view2)
    }

    @Test
    fun addCategoryToView() {
        val viewsStore = getTestViewsStore()

        val rootView = viewsStore.getRootView()
        val view = View(-1, rootView.id, "SubView1_1")

        val error = viewsStore.addView(view)
        assertNull(error)
        assertTrue(view.id > 0)

        val categoriesStore = getCategoriesStore()
        val rootCategory = categoriesStore.getRootCategory()
        testDatabase.deleteAllCategoriesExceptRootCategory();

        var category = Category(-1, rootCategory.id, "SubCategory")
        category = categoriesStore.addCategory(listOf(rootCategory.id), category)
        assertNotNull(category.id)
        assertTrue(category.id > 1)
        assertFalse(category.isRoot())

        // viewsStore.addCategoryToView(view, category, 0)
    }

    @Test
    fun insertInOrder() {
        var cv0 = CategoryView(-1, 1, 0, 0)
        var cv1 = CategoryView(-1, 1, 1, 1)
        var cv2 = CategoryView(-1, 1, 2, 2)
        var cv3 = CategoryView(-1, 1, 3, 3)
        var cvs = listOf(cv0, cv1, cv2, cv3)

        var newPosition = 0
        var cvNew = CategoryView(-1, 1, 5, newPosition)
        var cvsUpdatedInOrder = insertInOrder(cvNew, cvs)

        assertEquals(newPosition, cvNew.position)
        assertEquals(4, cvsUpdatedInOrder.size)
        assertEquals(1, cv0.position)
        assertEquals(0, cvsUpdatedInOrder.indexOf(cv0))
        assertEquals(2, cv1.position)
        assertEquals(1, cvsUpdatedInOrder.indexOf(cv1))
        assertEquals(3, cv2.position)
        assertEquals(2, cvsUpdatedInOrder.indexOf(cv2))
        assertEquals(4, cv3.position)
        assertEquals(3, cvsUpdatedInOrder.indexOf(cv3))

        cv0 = CategoryView(-1, 1, 1, 0)
        cv1 = CategoryView(-1, 1, 2, 1)
        cv2 = CategoryView(-1, 1, 3, 2)
        cv3 = CategoryView(-1, 1, 4, 3)
        cvs = listOf(cv0, cv1, cv2, cv3)

        newPosition = 1
        cvNew = CategoryView(-1, 1, 5, newPosition)
        cvsUpdatedInOrder = insertInOrder(cvNew, cvs)

        assertEquals(newPosition, cvNew.position)

        assertEquals(3, cvsUpdatedInOrder.size)
        assertEquals(2, cv1.position)
        assertEquals(0, cvsUpdatedInOrder.indexOf(cv1))
        assertEquals(3, cv2.position)
        assertEquals(1, cvsUpdatedInOrder.indexOf(cv2))
        assertEquals(4, cv3.position)
        assertEquals(2, cvsUpdatedInOrder.indexOf(cv3))
        assertEquals(0, cv0.position)
        assertEquals(2, cv1.position)
        assertEquals(3, cv2.position)
        assertEquals(4, cv3.position)

        cv0 = CategoryView(-1, 1, 1, 0)
        cv1 = CategoryView(-1, 1, 2, 1)
        cv2 = CategoryView(-1, 1, 3, 2)
        cv3 = CategoryView(-1, 1, 4, 3)
        cvs = listOf(cv0, cv1, cv2, cv3)

        newPosition = 2
        cvNew = CategoryView(-1, 1, 5, newPosition)
        cvsUpdatedInOrder = insertInOrder(cvNew, cvs)

        assertEquals(newPosition, cvNew.position)
        assertEquals(2, cvsUpdatedInOrder.size)
        assertEquals(3, cv2.position)
        assertEquals(0, cvsUpdatedInOrder.indexOf(cv2))
        assertEquals(4, cv3.position)
        assertEquals(1, cvsUpdatedInOrder.indexOf(cv3))
        assertEquals(0, cv0.position)
        assertEquals(1, cv1.position)
        assertEquals(3, cv2.position)
        assertEquals(4, cv3.position)


        cv0 = CategoryView(-1, 1, 1, 0)
        cv1 = CategoryView(-1, 1, 2, 1)
        cv2 = CategoryView(-1, 1, 3, 2)
        cv3 = CategoryView(-1, 1, 4, 3)
        cvs = listOf(cv0, cv1, cv2, cv3)

        newPosition = 3
        cvNew = CategoryView(-1, 1, 5, newPosition)
        cvsUpdatedInOrder = insertInOrder(cvNew, cvs)

        assertEquals(1, cvsUpdatedInOrder.size)
        assertEquals(4, cv3.position)
        assertEquals(0, cvsUpdatedInOrder.indexOf(cv3))
        assertEquals(0, cv0.position)
        assertEquals(1, cv1.position)
        assertEquals(2, cv2.position)
        assertEquals(4, cv3.position)

        cv0 = CategoryView(-1, 1, 1, 0)
        cv1 = CategoryView(-1, 1, 2, 1)
        cv2 = CategoryView(-1, 1, 3, 2)
        cv3 = CategoryView(-1, 1, 4, 3)
        cvs = listOf(cv0, cv1, cv2, cv3)

        newPosition = 4
        cvNew = CategoryView(-1, 1, 5, newPosition)
        cvsUpdatedInOrder = insertInOrder(cvNew, cvs)

        assertEquals(newPosition, cvNew.position)
        assertEquals(0, cvsUpdatedInOrder.size)
        assertEquals(0, cv0.position)
        assertEquals(1, cv1.position)
        assertEquals(2, cv2.position)
        assertEquals(3, cv3.position)

        cvsUpdatedInOrder = insertInOrder(cvNew, ArrayList())
        assertEquals(1, cvsUpdatedInOrder.size)
        assertEquals(0, cvNew.position)
    }

    private fun insertInOrder(cvNew: CategoryView, cvs: List<CategoryView>) : List<CategoryView> {
        return getCategoryViewsStore().testInsertInOrder(cvNew, cvs)
    }

}