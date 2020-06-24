package org.objectscape.ce.backend.storage

import org.objectscape.ce.backend.model.Category
import org.objectscape.ce.backend.model.CategoryItem
import org.objectscape.ce.backend.model.Item
import java.sql.Connection

class CategoryItemsStore(connection: Connection) : AbstractStore(connection) {

    fun addItem(item: Item, categories:  Collection<Category>) {
        categories.forEach { category ->
            execute("insert into ${tableName()}(item_id, category_id) values(${item.id}, '${category.id}');")
        }
    }

    fun getItemsIds(categoryIds: Collection<Category>): List<Long> {
        val itemIds = ArrayList<Long>()
        val inClause = toInClauseList(categoryIds.map { it.id })
        val resultSet = executeQuery("select distinct item_id from ${tableName()} where category_id in $inClause")
        resultSet.use {
            while(resultSet.next()) {
                itemIds.add(resultSet.getLong(1))
            }
        }
        return itemIds
    }

    override fun tableName(): String = CategoryItem.TableName

}