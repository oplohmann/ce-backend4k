package org.objectscape.ce.backend.storage.exceptions

import java.lang.Exception

class MustNotBePersistentException(message: String?) : Exception(message) {
}