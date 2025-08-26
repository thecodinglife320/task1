package org.example

import java.util.UUID

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

   private val printOptions = { ->
      println("***Task Manager***")
      println("Task Manager Menu:")
      println("1. Add task")
      println("2. Complete task")
      println("3. Delete task")
      println("4. Get tasks")
      println("5. Exit")
      print("Enter your choice (1-5): ")
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
               println("Exiting Task Manager.")
               break
            }

            else -> println("Invalid choice. Please try again.")
         }
      }
   }
}