package org.objectscape.ce.backend.model

import java.sql.Connection
import java.sql.ResultSet
import kotlin.properties.Delegates

class Category : Model {

    companion object {
        val RootParentId = 0L
        val RootDefaultName = "Categories"
        val TableName = "categories"
    }

    var firstParentId: Long
    var name: String

    constructor(id: Long, firstParentId: Long, name: String) : super(id) {
        this.firstParentId = firstParentId
        this.name = name
    }

    fun isRoot(): Boolean {
        return firstParentId == RootParentId
    }

    override fun isPersistent(): Boolean {
        return super.isPersistent() && firstParentId >= 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as Category

        if (firstParentId != other.firstParentId) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + firstParentId.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }


}