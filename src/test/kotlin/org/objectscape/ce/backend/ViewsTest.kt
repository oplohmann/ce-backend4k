package org.objectscape.ce.backend

import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.objectscape.ce.backend.model.View
import org.objectscape.ce.backend.util.TestDatabase

class ViewsTest  : AbstractTest() {

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
    fun addView() {
        val viewsStore = testDatabase.viewsStore
        testDatabase.deleteViewRelatedData()

        val rootView = viewsStore.getRootView()
        val view = View(-1, rootView.id, "SubView1_1")
        viewsStore.addView(view = view)

        Assert.assertTrue(view.id > 0)

        val error = viewsStore.addView(view)
        Assert.assertNotNull(error)
        Assert.assertTrue(error is ViewAlreadyExists)
    }

}