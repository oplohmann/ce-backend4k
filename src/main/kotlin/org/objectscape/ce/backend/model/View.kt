package org.objectscape.ce.backend.model

import java.util.*

class View : Model {

    companion object {
        val TableName = "views"
    }

    var parentId: Long
    var name: String

    constructor(id: Long, parentId: Long, name: String) : super(id) {
        this.parentId = parentId
        this.name = name
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as View

        if (parentId != other.parentId) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + parentId.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

}