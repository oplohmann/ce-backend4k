package org.objectscape.ce.backend.util

import org.objectscape.ce.backend.Database
import org.objectscape.ce.backend.model.*

class TestDatabase(dbFilePath: String) : Database(dbFilePath) {

    companion object {
        val DBFile = "D:/dev/CategoryExplorer/ce.db";
    }

    fun deleteAllCategoriesExceptRootCategory() {
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

    private fun deleteAllViewsExceptRootView() {
        val stmt = connection.createStatement()
        stmt.closeOnCompletion()
        stmt.execute("delete from ${View.TableName} where parent_id != ${View.RootParentId}")
    }

}