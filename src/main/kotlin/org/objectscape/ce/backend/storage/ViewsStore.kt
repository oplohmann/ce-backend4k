package org.objectscape.ce.backend.storage

import org.objectscape.ce.backend.CategoryAlreadyExists
import org.objectscape.ce.backend.DatabaseException
import org.objectscape.ce.backend.ViewAlreadyExists
import org.objectscape.ce.backend.model.Category
import org.objectscape.ce.backend.model.View
import java.sql.Connection
import java.sql.ResultSet

class ViewsStore(connection: Connection) : AbstractStore(connection) {

    init {
        ensureRootViewExists()
    }

    private fun ensureRootViewExists() {
        if(!hasRootView()) {
            createRootView()
        }
    }

    private fun createRootView() {
        execute("insert into ${tableName()}(parent_id, name) values(${View.RootParentId}, '${View.RootDefaultName}');")
    }

    private fun hasRootView() : Boolean {
        return getCount("where parent_id = ${View.RootParentId}") == 1
    }

    fun getRootView() : View {
        val resultSet = executeQuery("select * from ${tableName()} where parent_id == ${View.RootParentId}")
        resultSet.use {
            if(it.next()) {
                return fromResultSet(it)
            }
        }

        throw DatabaseException("root view not found!")
    }

    private fun fromResultSet(resultSet : ResultSet) : View {
        return View(
            resultSet.getLong(1),
            resultSet.getLong(2),
            resultSet.getString(3))
    }

    fun addView(view: View): ViewAlreadyExists? {
        val alreadyExists = getCount("where parent_id = ${view.parentId} and name = '${view.name}'") == 1
        if(alreadyExists) {
            return ViewAlreadyExists("View ${view} already exists!")
        }

        execute("insert into ${tableName()}(parent_id, name) values(${view.parentId}, '${view.name}');")
        val resultSet = executeQuery("select id from ${tableName()} where parent_id = ${view.parentId} and name = '${view.name}'")
        resultSet.use {
            if(it.next()) {
                view.id = it.getLong(1)
                return null
            }
        }

        throw DatabaseException("Error inserting $view !")
    }

    override fun tableName(): String = View.TableName

}