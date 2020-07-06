package org.objectscape.ce.backend.storage

import java.lang.Exception

class MustNotBePersistentException(message: String?) : Exception(message) {
}