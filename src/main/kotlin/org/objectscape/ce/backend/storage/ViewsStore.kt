package org.objectscape.ce.backend.storage

import org.objectscape.ce.backend.DatabaseException
import org.objectscape.ce.backend.NotPersistentException
import org.objectscape.ce.backend.ViewAlreadyExists
import org.objectscape.ce.backend.model.Category
import org.objectscape.ce.backend.model.CategoryView
import org.objectscape.ce.backend.model.View
import org.objectscape.ce.backend.storage.exceptions.CategorySortException
import java.sql.Connection
import java.sql.ResultSet

open class ViewsStore : AbstractStore {

    private val categoryViewsStore : CategoryViewsStore

    constructor(connection: Connection, categoryViewsStore : CategoryViewsStore) : super(connection) {
        this.categoryViewsStore = categoryViewsStore
    }

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

    protected fun fromResultSet(resultSet : ResultSet) : View {
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

    fun addCategoryToView(view: View, category: Category, position: Int, categoriesOfSameView: List<CategoryView>) {
        if(!category.isPersistent()) {
            throw NotPersistentException("Category $category not persistent!")
        }
        if(!view.isPersistent()) {
            throw NotPersistentException("View $view not persistent!")
        }
        assertPersistent(categoriesOfSameView)
        assertPositionInRange(position, categoriesOfSameView)

        val newCategoryView = CategoryView(-1, view.id, category.id, position)
        val changedCategoryViews = insertInOrder(newCategoryView, categoriesOfSameView)
        categoryViewsStore.updatePositions(changedCategoryViews)

    }

    private fun assertPositionInRange(position: Int, categoriesOfSameView: List<CategoryView>) {
        if(position < 0) {
            throw CategorySortException("Position $position out of range!")
        }

        if(categoriesOfSameView.isEmpty()) {
            if(position != 0) {
                throw CategorySortException("Position $position out of range!")
            }
            return
        }

        val positions = categoriesOfSameView.map { it.position }.toSortedSet()
        if(!positions.contains(position) && positions.last() + 1 != position) {
            throw CategorySortException("Position $position out of range!")
        }
    }

    fun loadViewNamed(name: String) : View? {
        val views = ArrayList<View>()
        val resultSet = executeQuery("select * from ${tableName()} where name = '$name'")
        resultSet.use {
            while(resultSet.next()) {
                views.add(fromResultSet(resultSet))
            }
        }
        if(views.isEmpty()) {
            return null
        }
        return views.get(0)
    }

    /**
     * Returns changed CategoryViews only
     */
    protected fun insertInOrder(cvNew: CategoryView, cvs: List<CategoryView>) : List<CategoryView> {
        val newCvs = ArrayList<CategoryView>()
        val updatedCvs = ArrayList<CategoryView>()
        if(cvs.isEmpty()) {
            cvNew.position = 0
            newCvs.add(cvNew)
            return newCvs
        }
        val cvsSorted = cvs.toSortedSet(compareBy { it.position })
        var added = false
        cvsSorted.forEach {
            if(it.position < cvNew.position) {
                newCvs.add(it)
                return@forEach
            }
            if(it.position == cvNew.position){
                newCvs.add(cvNew)
                added = true
                it.position++
                newCvs.add(it)
                updatedCvs.add(it)
                return@forEach
            }
            it.position++
            newCvs.add(it)
            updatedCvs.add(it)
        }

        if(!added) {
            if(cvNew.position == newCvs.last().position + 1) {
                newCvs.add(cvNew)
            } else {
                throw CategorySortException("Cannot insert categoryView $newCvs in correct order")
            }
        }

        return updatedCvs
    }

    override fun tableName(): String = View.TableName

}