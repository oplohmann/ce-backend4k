package org.objectscape.ce.backend.storage

import org.objectscape.ce.backend.model.Category
import org.objectscape.ce.backend.model.CategoryParent
import java.sql.Connection

class CategoryHierarchyStore (connection: Connection) : AbstractStore(connection) {

    override fun tableName(): String = CategoryParent.TableName

    fun addParents(parentIdsInOrder: List<Long>, insertedCategory: Category) {
        parentIdsInOrder.forEach { parentId ->
            execute("insert into ${CategoryParent.TableName} (parent_id, category_id) values($parentId, ${insertedCategory.id})")
        }
    }

    fun getParentIds(id: Long): List<Long> {
        val resultSet = executeQuery("select parent_id from ${tableName()} where category_id == $id order by id desc")
        val parentIds = ArrayList<Long>()
        resultSet.use {
            while(resultSet.next()) {
                parentIds.add(resultSet.getLong(1))
            }
            return parentIds
        }
    }

    fun getChildrenIds(id: Long): List<Long> {
        val resultSet = executeQuery("select category_id from ${tableName()} where parent_id == $id order by id")
        val parentIds = ArrayList<Long>()
        resultSet.use {
            while(resultSet.next()) {
                parentIds.add(resultSet.getLong(1))
            }
            return parentIds
        }
    }

}