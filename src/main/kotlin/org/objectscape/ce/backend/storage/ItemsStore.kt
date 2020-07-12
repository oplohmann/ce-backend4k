package org.objectscape.ce.backend.storage

import org.objectscape.ce.backend.model.Category
import org.objectscape.ce.backend.model.Item
import org.objectscape.ce.backend.storage.exceptions.DatabaseException
import java.lang.StringBuilder
import java.sql.Connection
import java.sql.ResultSet
import java.util.*
import kotlin.collections.ArrayList

class ItemsStore : AbstractStore {

    override fun tableName(): String {
        return Item.TableName
    }

    private val categoryItemsStore : CategoryItemsStore

    constructor(connection: Connection, categoryItemsStore : CategoryItemsStore) : super(connection) {
        this.categoryItemsStore = categoryItemsStore
    }

    fun addItem(item : Item, categories : Collection<Category>) {
        if(categories.isEmpty()) {
            throw DatabaseException("Cannot add items with empty list of categories!")
        }
        var sql = StringBuilder()
            .append("insert into ${Item.TableName} (text, note, entry_date_java, entry_date_cs, last_changed_java, last_changed_cs) ")
            .append(" values('${item.text}', '${item.note}', ${item.entryDate.time}, -1, ${item.lastChanged.time}, -1)")
            .toString()

        execute(sql)

        val resultSet = executeQuery("select id from ${Item.TableName} where entry_date_java = ${item.entryDate.time}")
        resultSet.use {
            if(!it.next()) {
                throw DatabaseException("Error inserting item: could not obtain id")
            }
            item.id = it.getLong(1)
        }

        categoryItemsStore.addItem(item, categories)
    }

    fun getItemsIds(categoryIds: Collection<Category>) : List<Long> {
        return categoryItemsStore.getItemsIds(categoryIds);
    }

    fun getItems(itemIds: Collection<Long>) : List<Item> {
        val items = ArrayList<Item>()
        val inClause = toInClauseList(itemIds)
        val resultSet = executeQuery("select * from ${Item.TableName} where id in $inClause")
        resultSet.use {
            while(resultSet.next()) {
                items.add(fromResultSet(resultSet))
            }
        }
        return items
    }

    private fun fromResultSet(resultSet: ResultSet): Item {
        val id = resultSet.getLong(1)
        var text = ""
        resultSet.getString(2)?.let { text = it }
        var note = ""
        resultSet.getString(3)?.let { note = it }

        val entry_date_java = resultSet.getLong(4)
        val entry_date_cs = resultSet.getLong(5)

        val last_changed_java = resultSet.getLong(6)
        val last_changed_cs = resultSet.getLong(7)

        return Item(id, text, note, Date(entry_date_java), Date(last_changed_java))
    }

}