package org.objectscape.ce.backend.util

import org.objectscape.ce.backend.Database
import org.objectscape.ce.backend.model.*
import org.objectscape.ce.backend.storage.ViewsStore

class TestDatabase(dbFilePath: String) : Database(dbFilePath) {

    companion object {
        val DBFile = "C:/Users/Oliver/IdeaProjects/ce-backend/db/ce.db";
    }

    val testViewsStore: TestViewsStore by lazy {
        TestViewsStore(connection, categoryViewsStore)
    }

    fun deleteAllCategoriesExceptRootCategory() {
        deleteAllCategoryViews()
        deleteAllCategoryItems()
        deleteAllCategoryParents()
        val stmt = connection.createStatement()
        stmt.closeOnCompletion()
        stmt.execute("delete from ${Category.TableName} where first_parent_id != ${Category.RootParentId}")
        stmt.execute("delete from ${CategoryParent.TableName}")
    }

    fun deleteAllItems() {
        val stmt = connection.createStatement()
        stmt.closeOnCompletion()
        stmt.execute("delete from ${CategoryItem.TableName}")
        stmt.execute("delete from ${Item.TableName}")
    }

    fun deleteViewRelatedData() {
        deleteAllItems()
        deleteAllCategoriesExceptRootCategory()
        deleteAllViewsExceptRootView()
    }

    private fun deleteAllCategoryParents() {
        val stmt = connection.createStatement()
        stmt.closeOnCompletion()
        stmt.execute("delete from ${CategoryParent.TableName}")
    }

    private fun deleteAllCategoryItems() {
        val stmt = connection.createStatement()
        stmt.closeOnCompletion()
        stmt.execute("delete from ${CategoryItem.TableName}")
    }

    fun deleteAllCategoryViews() {
        val stmt = connection.createStatement()
        stmt.closeOnCompletion()
        stmt.execute("delete from ${CategoryView.TableName}")
    }

    fun deleteAllViewsExceptRootView() {
        val stmt = connection.createStatement()
        stmt.closeOnCompletion()
        stmt.execute("delete from ${View.TableName} where parent_id != ${View.RootParentId}")
    }

}