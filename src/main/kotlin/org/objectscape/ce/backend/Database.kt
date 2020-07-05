package org.objectscape.ce.backend

import org.objectscape.ce.backend.storage.*
import java.sql.Connection
import java.sql.DriverManager

open class Database {

    companion object {
        private val Connections = HashMap<String, Connection>()
    }

    protected val connection: Connection
    private val dbFilePath: String

    val categoryStore: CategoryStore
    val categoryHierarchyStore: CategoryHierarchyStore
    val categoryItemsStore : CategoryItemsStore
    val categoryViewsStore : CategoryViewsStore
    val itemsStore: ItemsStore
    val viewsStore: ViewsStore

    constructor(dbFilePath: String) {
        synchronized(Connections) {
            this.connection = createSingleConnection(dbFilePath)
            this.dbFilePath = dbFilePath
            this.categoryHierarchyStore = CategoryHierarchyStore(connection)
            this.categoryStore = CategoryStore(connection, categoryHierarchyStore)
            this.categoryItemsStore = CategoryItemsStore(connection)
            this.itemsStore = ItemsStore(connection, categoryItemsStore)
            this.categoryViewsStore = CategoryViewsStore(connection)
            this.viewsStore = ViewsStore(connection, categoryViewsStore)
        }
    }

    private fun createSingleConnection(dbFilePath: String): Connection {
        // Sqlite only allows a single connection to exist per database. Make sure not
        // more than one connection for the same database can be created.
        if (Connections.containsKey(dbFilePath)) {
            throw DatabaseException("Database connection for $dbFilePath already created!")
        }
        val singleConnection = DriverManager.getConnection("jdbc:sqlite:" + dbFilePath)
        enableForeignKeyConstraints(singleConnection)
        Connections.put(dbFilePath, singleConnection)
        return singleConnection
    }

    private fun enableForeignKeyConstraints(singleConnection: Connection) {
        val stmt = singleConnection.createStatement()
        stmt.closeOnCompletion()
        stmt.execute("PRAGMA foreign_keys = ON;")
    }

    fun close() {
        synchronized(Connections) {
            val connectionFromStore = Connections.remove(dbFilePath)
            if(connectionFromStore == null) {
                throw DatabaseException("Connection for $dbFilePath already closed!")
            }
            connectionFromStore.close()
        }
    }

}