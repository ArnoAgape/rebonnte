package com.openclassrooms.rebonnte

import com.google.firebase.Timestamp
import com.openclassrooms.rebonnte.domain.model.Aisle
import com.openclassrooms.rebonnte.domain.model.History
import com.openclassrooms.rebonnte.domain.model.Medicine
import com.openclassrooms.rebonnte.domain.model.StockChangeType
import com.openclassrooms.rebonnte.domain.model.User
import java.time.Instant

/**
 * Utility object providing fake domain models for unit tests.
 */
object TestUtils {

    /**
     * Creates a fake [Medicine] instance with predefined values.
     *
     * @param id Unique identifier for the medicine.
     * @return A fake [Medicine] used for testing.
     */
    fun fakeMedicine(id: String): Medicine {
        return Medicine(
            id = id,
            aisleName = "Painkiller",
            name = "Paracetamol",
            stock = 10,
            createdAt = Timestamp(1233356000, 212120),
            dateTime = Instant.now(),
            author = User(
                id = "1",
                displayName = "John Doe",
                phoneNumber = "06 01 02 03 04",
                email = "jdoe@mail.com"
            )
        )
    }

    /**
     * Creates a fake [Aisle] instance with predefined values.
     *
     * @param id Unique identifier for the aisle.
     * @return A fake [Aisle] used for testing.
     */
    fun fakeAisle(id: String): Aisle {
        return Aisle(
            id = id,
            name = "Painkiller",
            createdAt = Timestamp(1233356000, 212120),
            medicines = listOf(
                Medicine(
                    id = "1",
                    aisleName = "Painkiller",
                    name = "Paracetamol",
                    stock = 10,
                    createdAt = Timestamp(1233356000, 212120),
                    dateTime = Instant.now(),
                    author = User(
                        id = "1",
                        displayName = "John Doe",
                        phoneNumber = "06 01 02 03 04",
                        email = "jdoe@mail.com"
                    )
                ),
                Medicine(
                    id = "2",
                    aisleName = "Antibiotics",
                    name = "Biot",
                    stock = 56,
                    createdAt = Timestamp(1233356000, 212120),
                    dateTime = Instant.now(),
                    author = User(
                        id = "2",
                        displayName = "Agatha Christie",
                        phoneNumber = "06 01 01 02 02",
                        email = "achristie@mail.com"
                    )
                ),
                Medicine(
                    id = "3",
                    aisleName = "Cream",
                    name = "CreamVe",
                    stock = 120,
                    createdAt = Timestamp(1233356000, 212120),
                    dateTime = Instant.now(),
                    author = User(
                        id = "3",
                        displayName = "Arno Test",
                        phoneNumber = "06 01 03 03 03",
                        email = "atest@mail.com"
                    )
                )
            )
        )
    }

    /**
     * Creates a fake [History] instance with predefined values.
     *
     * @param id Unique identifier for the history.
     * @return A fake [History] used for testing.
     */
    fun fakeHistory(id: String): History {
        return History(
            id = id,
            medicineName = "Paracetamol",
            changeType = StockChangeType.ADDED,
            quantity = 10,
            dateTime = Instant.now(),
            author = User(
                id = "1",
                displayName = "John Doe",
                phoneNumber = "06 01 02 03 04",
                email = "jdoe@mail.com"
            )
        )
    }

    /**
     * Creates a fake [User] instance for testing purposes.
     *
     * @param id Unique identifier for the user.
     * @return A fake [User].
     */
    fun fakeUser(id: String): User {
        return User(
            id,
            displayName = "Gerry Ariella",
            phoneNumber = "0606060606",
            email = "gariella@mail.com"
        )
    }

}