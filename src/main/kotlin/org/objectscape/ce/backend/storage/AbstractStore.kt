package org.objectscape.ce.backend.storage

import org.objectscape.ce.backend.DatabaseException
import org.objectscape.ce.backend.NotPersistentException
import org.objectscape.ce.backend.model.Category
import org.objectscape.ce.backend.model.Model
import org.objectscape.ce.backend.util.forEachIsLast
import java.lang.StringBuilder
import java.sql.Connection
import java.sql.ResultSet

abstract class AbstractStore {

    protected val connection: Connection

    constructor(connection: Connection) {
        if(connection.isClosed) {
            throw DatabaseException("No connection to database")
        }
        this.connection = connection
    }

    protected abstract fun tableName(): String

    protected fun execute(sql : String): Boolean {
        val stmt = connection.createStatement()
        stmt.closeOnCompletion()
        return stmt.execute(sql)
    }

    protected fun executeQuery(sql : String) : ResultSet {
        val stmt = connection.createStatement()
        stmt.closeOnCompletion()
        return stmt.executeQuery(sql)
    }

    protected fun getCount(whereClause : String?) : Int {
        var sql = "select count(*) from " + tableName()
        if(whereClause != null) {
            sql += " $whereClause"
        }

        executeQuery(sql).use { resultSet ->
            resultSet.next()
            return resultSet.getInt(1)
        }
    }

    fun toInClauseList(categoryIds: Collection<Long>): String {
        val inClause = StringBuilder().append("(")
        categoryIds.forEachIsLast { value, isLast ->
            inClause.append(value)
            if(!isLast) {
                inClause.append(", ")
            }
        }
        inClause.append(")")
        return inClause.toString()
    }

    protected fun assertPersistent(models: List<Model>) {
        models.forEach {
            if(!it.isPersistent()) {
                throw NotPersistentException("$it not persistent!")
            }
        }
    }

}