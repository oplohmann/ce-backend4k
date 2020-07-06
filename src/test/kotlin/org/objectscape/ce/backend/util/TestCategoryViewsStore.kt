package org.objectscape.ce.backend.util

import org.objectscape.ce.backend.model.CategoryView
import org.objectscape.ce.backend.storage.CategoryViewsStore
import java.sql.Connection

class TestCategoryViewsStore(connection: Connection) : CategoryViewsStore(connection) {

    fun testAddRawCategoryView(categoryView : CategoryView): Boolean {
        return super.addRawCategoryView(categoryView)
    }

    fun testInsertInOrder(cvNew: CategoryView, cvs: List<CategoryView>) : List<CategoryView> {
        return insertInOrder(cvNew, cvs)
    }

}