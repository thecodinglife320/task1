import java.io.File
import java.io.IOException
import java.security.MessageDigest
import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

enum class TaskStatus {
    PENDING,
    COMPLETED,
    OVERDUE
}

enum class SortOrder {
    ASC,
    DESC
}

enum class SortField {
    NONE,
    PRIORITY,
    DUEDATE
}

class TaskManager {
    private data class Task(
        val title: String,
        var description: String,
        var status: TaskStatus, // PENDING, COMPLETED
        val id: String = UUID.randomUUID().toString(),
        val dueDate: LocalDate?,
        val priority: Int
    )

    data class TaskInfo(
        val id: String,
        val title: String,
        val description: String,
        val status: TaskStatus, // PENDING, COMPLETED, OVERDUE
        val dueDate: LocalDate?,
        val priority: Int
    )

    private val tasks = mutableListOf<Task>()
    private var filePassword: String? = null
    private var currentFilePath: String? = null
    private var sortField = SortField.NONE
    private var sortOrder = SortOrder.ASC

    private fun encrypt(data: String, password: String): String {
        val key = generateKey(password)
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encryptedBytes = cipher.doFinal(data.toByteArray())
        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    private fun decrypt(encryptedData: String, password: String): String {
        val key = generateKey(password)
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, key)
        val decodedBytes = Base64.getDecoder().decode(encryptedData)
        val decryptedBytes = cipher.doFinal(decodedBytes)
        return String(decryptedBytes)
    }

    private fun generateKey(password: String): SecretKeySpec {
        val digest = MessageDigest.getInstance("SHA-256")
        val keyBytes = digest.digest(password.toByteArray())
        return SecretKeySpec(keyBytes, 0, 16, "AES") // Use first 128 bits (16 bytes) of the hash
    }

    private val addTask = { title: String, description: String, dueDate: LocalDate?, priority: Int ->
        tasks.add(Task(title, description, TaskStatus.PENDING, UUID.randomUUID().toString(), dueDate, priority))
        println("Task added successfully.")
    }

    private val completeTask = { id: String ->
        val task = tasks.find { it.id == id }
        if (task != null) {
            task.status = TaskStatus.COMPLETED
            println("Task completed successfully.")
        } else {
            println("Task with ID '$id' not found.")
        }
    }

    private val deleteTask = { id: String ->
        val removed = tasks.removeIf { it.id == id }
        if (removed) {
            println("Task deleted successfully.")
        } else {
            println("Task with ID '$id' not found.")
        }
    }

    private val editTaskDescription = { id: String, newDescription: String ->
        val task = tasks.find { it.id == id }
        if (task != null) {
            task.description = newDescription
            println("Task description updated successfully.")
        } else {
            println("Task with ID '$id' not found.")
        }
    }

    private val getTasks = { ->
        val sortedTasks = when (sortField) {
            SortField.NONE -> tasks
            SortField.PRIORITY -> {
                if (sortOrder == SortOrder.ASC) tasks.sortedBy { it.priority }
                else tasks.sortedByDescending { it.priority }
            }

            SortField.DUEDATE -> {
                if (sortOrder == SortOrder.ASC) tasks.sortedWith(compareBy(nullsLast()) { it.dueDate })
                else tasks.sortedByDescending { it.dueDate }
            }
        }

        sortedTasks.map {
            val currentStatus =
                if (it.status != TaskStatus.COMPLETED && it.dueDate != null && it.dueDate.isBefore(LocalDate.now())) {
                    TaskStatus.OVERDUE
                } else {
                    it.status
                }
            TaskInfo(it.id, it.title, it.description, currentStatus, it.dueDate, it.priority)
        }
    }

    private fun saveToFile(fileName: String, password: String) {
        try {
            val tasksAsString = tasks.joinToString("\n") {
                "${it.id},${it.title.replace(",", ";")},${
                    it.description.replace(
                        ",",
                        ";"
                    )
                },${it.status.name},${it.dueDate?.toString() ?: ""},${it.priority}"
            }
            val encryptedData = encrypt(tasksAsString, password)
            File(fileName).writeText(encryptedData)
            println("Tasks saved to $fileName")
        } catch (e: IOException) {
            println("Error saving tasks to file: ${e.message}")
        }
    }

    private fun loadFromFile(fileName: String, password: String) {
        try {
            val file = File(fileName)
            if (file.exists()) {
                val encryptedData = file.readText()
                try {
                    val decryptedData = decrypt(encryptedData, password)
                    tasks.clear() // Clear existing tasks before loading
                    decryptedData.lines().forEach {
                        if (it.isNotBlank()) {
                            val parts = it.split(",", limit = 6)
                            if (parts.size == 6) {
                                val task = Task(
                                    id = parts[0],
                                    title = parts[1].replace(";", ","),
                                    description = parts[2].replace(";", ","),
                                    status = TaskStatus.valueOf(parts[3]),
                                    dueDate = if (parts[4].isNotBlank()) LocalDate.parse(parts[4]) else null,
                                    priority = parts[5].toInt()
                                )
                                tasks.add(task)
                            }
                        }
                    }
                    println("Tasks loaded from $fileName")
                } catch (e: Exception) {
                    println("Error decrypting file. Check your password or file content.")
                }
            } else {
                println("File not found: $fileName")
            }
        } catch (e: IOException) {
            println("Error loading tasks from file: ${e.message}")
        }
    }

    private val printOptions = {
        println("***Task Manager***")
        println("Current file: ${currentFilePath ?: "Not set"}")
        println("Task Manager Menu:")
        println("1. Add task")
        println("2. Complete task")
        println("3. Delete task")
        println("4. Get tasks")
        println("5. Save tasks")
        println("6. Load tasks")
        println("7. Set/Change file password")
        println("8. Set/Change file")
        println("9. Edit task description")
        println("10. Sort tasks")
        println("11. Exit")
        print("Enter your choice (1-11): ")
    }

    // Helper function to truncate string
    private fun String.truncate(maxWidth: Int): String {
        return if (this.length > maxWidth) this.substring(0, maxWidth - 3) + "..." else this
    }

    val run = {
        while (true) {
            printOptions()
            when (readln()) {
                "1" -> {
                    print("Enter task title: ")
                    val title = readln()
                    print("Enter task description: ")
                    val description = readln()

                    var dueDate: LocalDate? = null
                    while (true) {
                        print("Enter due date (YYYY-MM-DD) or leave empty: ")
                        val dueDateString = readln()
                        if (dueDateString.isBlank()) {
                            break
                        }
                        try {
                            dueDate = LocalDate.parse(dueDateString)
                            break
                        } catch (e: DateTimeParseException) {
                            println("Invalid date format. Please use YYYY-MM-DD.")
                        }
                    }

                    var priority: Int = 0
                    while (true) {
                        print("Enter priority (1-5, 1 being highest): ")
                        try {
                            val priorityInput = readln().toInt()
                            if (priorityInput in 1..5) {
                                priority = priorityInput
                                break
                            } else {
                                println("Priority must be between 1 and 5.")
                            }
                        } catch (e: NumberFormatException) {
                            println("Invalid input. Please enter a number.")
                        }
                    }

                    addTask(title, description, dueDate, priority)
                }

                "2" -> {
                    print("Enter task ID to complete: ")
                    val id = readln()
                    completeTask(id)
                }

                "3" -> {
                    print("Enter task ID to delete: ")
                    val id = readln()
                    deleteTask(id)
                }

                "4" -> {
                    val tasks = getTasks()
                    println("Tasks:")
                    if (tasks.isEmpty()) {
                        println("No tasks found.")
                    } else {
                        // Define column widths
                        val idWidth = 36
                        val titleWidth = 20
                        val descriptionWidth = 30
                        val statusWidth = 10
                        val dueDateWidth = 12
                        val priorityWidth = 8

                        println(
                            "| %-${idWidth}s | %-${titleWidth}s | %-${descriptionWidth}s | %-${statusWidth}s | %-${dueDateWidth}s | %-${priorityWidth}s |".format(
                                "ID",
                                "Title",
                                "Description",
                                "Status",
                                "Due Date",
                                "Priority"
                            )
                        )
                        println(
                            "|${"-".repeat(idWidth + 2)}|${"-".repeat(titleWidth + 2)}|${"-".repeat(descriptionWidth + 2)}|${
                                "-".repeat(
                                    statusWidth + 2
                                )
                            }|${"-".repeat(dueDateWidth + 2)}|${"-".repeat(priorityWidth + 2)}|"
                        )
                        tasks.forEach {
                            println(
                                "| %-${idWidth}s | %-${titleWidth}s | %-${descriptionWidth}s | %-${statusWidth}s | %-${dueDateWidth}s | %-${priorityWidth}s |".format(
                                    it.id.truncate(idWidth),
                                    it.title.truncate(titleWidth),
                                    it.description.truncate(descriptionWidth),
                                    it.status.name,
                                    it.dueDate?.toString() ?: "N/A",
                                    it.priority
                                )
                            )
                        }
                    }
                }

                "5" -> {
                    if (filePassword == null) {
                        println("Please set a password first using option 7.")
                        continue
                    }
                    if (currentFilePath == null) {
                        println("Please set a file first using option 8.")
                        continue
                    }
                    saveToFile(currentFilePath!!, filePassword!!)
                }

                "6" -> {
                    if (filePassword == null) {
                        println("Please set a password first using option 7.")
                        continue
                    }
                    if (currentFilePath == null) {
                        println("Please set a file first using option 8.")
                        continue
                    }
                    loadFromFile(currentFilePath!!, filePassword!!)
                }

                "7" -> {
                    print("Enter new password: ")
                    val newPassword = readln()
                    if (newPassword.isNotBlank()) {
                        filePassword = newPassword
                        println("Password has been set.")
                    } else {
                        println("Password cannot be empty.")
                    }
                }

                "8" -> {
                    print("Enter filename: ")
                    val fileName = readln()
                    if (fileName.isNotBlank()) {
                        currentFilePath = fileName
                        println("Current file set to $fileName")
                    } else {
                        println("Filename cannot be empty.")
                    }
                }

                "9" -> {
                    print("Enter task ID to edit: ")
                    val id = readln()
                    print("Enter new description: ")
                    val newDescription = readln()
                    editTaskDescription(id, newDescription)
                }

                "10" -> {
                    print("Sort by (priority/duedate/none): ")
                    val field = readln().lowercase()
                    if (field == "none") {
                        sortField = SortField.NONE
                        println("Task sorting has been reset.")
                    } else if (field == "priority" || field == "duedate") {
                        print("Sort order (asc/desc): ")
                        val order = readln().lowercase()
                        if (order == "asc" || order == "desc") {
                            sortField = if (field == "priority") SortField.PRIORITY else SortField.DUEDATE
                            sortOrder = if (order == "asc") SortOrder.ASC else SortOrder.DESC
                            println("Tasks will be sorted by $field in $order order.")
                        } else {
                            println("Invalid sort order.")
                        }
                    } else {
                        println("Invalid field.")
                    }
                }

                "11" -> {
                    println("Exiting Task Manager.")
                    break
                }

                else -> println("Invalid choice. Please try again.")
            }
        }
    }
}