import java.io.File
import java.io.IOException
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class TaskManager {
    private data class Task(
        val title: String,
        val description: String,
        var isCompleted: Boolean,
        val id: String = UUID.randomUUID().toString(),
    )

    data class TaskInfo(
        val id: String,
        val title: String,
        val description: String,
        val isCompleted: Boolean,
    )

    private val tasks = mutableListOf<Task>()
    private var filePassword: String? = null
    private var currentFilePath: String? = null

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

    private val addTask = { title: String, description: String ->
        tasks.add(Task(title, description, false))
        println("Task added successfully.")
    }

    private val completeTask = { id: String ->
        val task = tasks.find { it.id == id }
        if (task != null) {
            task.isCompleted = true
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

    private val getTasks = { ->
        tasks.map { TaskInfo(it.id, it.title, it.description, it.isCompleted) }
    }

    private fun saveToFile(fileName: String, password: String) {
        try {
            val tasksAsString = tasks.joinToString("\n") {
                "${it.id},${it.title.replace(",", ";")},${
                    it.description.replace(
                        ",",
                        ";"
                    )
                },${it.isCompleted}"
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
                            val parts = it.split(",", limit = 4)
                            if (parts.size == 4) {
                                val task = Task(
                                    id = parts[0],
                                    title = parts[1].replace(";", ","),
                                    description = parts[2].replace(";", ","),
                                    isCompleted = parts[3].toBoolean()
                                )
                                tasks.add(task)
                            }
                        }
                    }
                    println("Tasks loaded from $fileName")
                } catch (e: Exception) {
                    println("Error decrypting file. Check your password.")
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
        println("9. Exit")
        print("Enter your choice (1-9): ")
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
                    addTask(title, description)
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
                        val completedWidth = 10

                        println(
                            "| %-${idWidth}s | %-${titleWidth}s | %-${descriptionWidth}s | %-${completedWidth}s |".format(
                                "ID",
                                "Title",
                                "Description",
                                "Completed"
                            )
                        )
                        println(
                            "|${"-".repeat(idWidth + 2)}|${"-".repeat(titleWidth + 2)}|${
                                "-".repeat(
                                    descriptionWidth + 2
                                )
                            }|${"-".repeat(completedWidth + 2)}|"
                        )
                        tasks.forEach {
                            println(
                                "| %-${idWidth}s | %-${titleWidth}s | %-${descriptionWidth}s | %-${completedWidth}s |".format(
                                    it.id.truncate(idWidth),
                                    it.title.truncate(titleWidth),
                                    it.description.truncate(descriptionWidth),
                                    if (it.isCompleted) "Yes" else "No"
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
                    println("Exiting Task Manager.")
                    break
                }

                else -> println("Invalid choice. Please try again.")
            }
        }
    }
}