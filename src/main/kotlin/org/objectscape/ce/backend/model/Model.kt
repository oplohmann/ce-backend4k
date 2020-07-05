package org.objectscape.ce.backend.model

abstract class Model {

    var id: Long

    constructor(id: Long) {
        this.id = id
    }

    open fun isPersistent(): Boolean {
        return id >= 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Model

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }


}