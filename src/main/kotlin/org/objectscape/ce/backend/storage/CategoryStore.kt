package org.objectscape.ce.backend.storage

import org.objectscape.ce.backend.model.Category
import org.objectscape.ce.backend.storage.exceptions.CategoryAlreadyExists
import org.objectscape.ce.backend.storage.exceptions.DatabaseException
import java.sql.Connection
import java.sql.ResultSet

class CategoryStore : AbstractStore {

    private val categoryHierarchyStore: CategoryHierarchyStore

    constructor(connection: Connection, categoryHierarchyStore: CategoryHierarchyStore) : super(connection) {
        this.categoryHierarchyStore = categoryHierarchyStore
    }

    init {
        ensureRootCategoryExists()
    }

    private fun ensureRootCategoryExists() {
        if(!hasRootCategory()) {
            createRootCategory()
        }
    }

    private fun createRootCategory() {
        execute("insert into ${tableName()}(first_parent_id, name) values(${Category.RootParentId}, '${Category.RootDefaultName}');")
    }

    private fun hasRootCategory() : Boolean {
        return getCount("where first_parent_id = ${Category.RootParentId}") == 1
    }

    fun getRootCategory() : Category {
        val resultSet = executeQuery("select * from ${tableName()} where first_parent_id == ${Category.RootParentId}")
        resultSet.use {
            if(it.next()) {
                return fromResultSet(it)
            }
        }

        throw DatabaseException("root category not found!")
    }

    @Throws(CategoryAlreadyExists::class, DatabaseException::class)
    fun addCategory(parentIdsInOrder : List<Long>, category: Category) : Category {
        if(parentIdsInOrder.isEmpty()) {
            throw DatabaseException("no parent ids provided")
        }
        val parentId = parentIdsInOrder.last()
        if(category.firstParentId != parentId) {
            throw DatabaseException("parent id mismatch!")
        }
        val alreadyExists = getCount("where first_parent_id = $parentId and name = '${category.name}'") == 1
        if(alreadyExists) {
            throw CategoryAlreadyExists("Category ${category} already exists at that level!")
        }
        execute("insert into ${tableName()}(first_parent_id, name) values(${category.firstParentId}, '${category.name}')")
        val resultSet = executeQuery("select id from ${tableName()} where first_parent_id = $parentId and name = '${category.name}'")
        resultSet.use {
            if(it.next()) {
                category.id = it.getLong(1)
                categoryHierarchyStore.addParents(parentIdsInOrder, category)
                return category
            }
        }

        throw DatabaseException("Error inserting $category !")
    }

    override fun tableName(): String = Category.TableName

    private fun fromResultSet(resultSet : ResultSet) : Category {
        return Category(
            resultSet.getLong(1),
            resultSet.getLong(2),
            resultSet.getString(3))
    }

    fun getChildrenCountFirstLevel(id: Long): Int {
        return getCount("where first_parent_id == $id")
    }

    fun getParentIds(id: Long) : List<Long>  {
        return categoryHierarchyStore.getParentIds(id)
    }

    fun getChildrenIds(id: Long) : List<Long>  {
        return categoryHierarchyStore.getChildrenIds(id)
    }

    fun loadCategory(name: String, parentId: Long): Category? {
        val resultSet = executeQuery("select * from ${tableName()} where name = '$name' and first_parent_id = $parentId")
        resultSet.use {
            if(it.next()) {
                return fromResultSet(it)
            }
        }
        return null
    }


}