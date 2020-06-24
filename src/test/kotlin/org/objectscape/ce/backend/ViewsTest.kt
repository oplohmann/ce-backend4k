package org.objectscape.ce.backend

import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
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
    fun addItem() {
        val viewsStore = testDatabase.viewsStore
        testDatabase.deleteViewRelatedData()
    }

}