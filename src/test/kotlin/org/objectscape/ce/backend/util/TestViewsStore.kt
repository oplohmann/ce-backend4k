package org.objectscape.ce.backend.util

import org.junit.Assert
import org.junit.Assert.assertTrue
import org.objectscape.ce.backend.model.CategoryView
import org.objectscape.ce.backend.model.View
import org.objectscape.ce.backend.storage.CategoryViewsStore
import org.objectscape.ce.backend.storage.ViewsStore
import java.sql.Connection

class TestViewsStore : ViewsStore {

    constructor(connection: Connection, categoryViewsStore : CategoryViewsStore) : super(connection, categoryViewsStore)

    fun insertInOrderTest(cvNew: CategoryView, cvs: List<CategoryView>) : List<CategoryView> {
        return insertInOrder(cvNew, cvs)
    }

    fun ensureViewExists(name: String) : View {
        var view = loadViewNamed(name)
        if(view != null)
            return view
        view = View(-1, getRootView().id, name)
        addView(view)
        assertTrue(view.isPersistent())
        return view
    }

}