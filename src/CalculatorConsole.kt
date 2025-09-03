import kotlin.math.*
import kotlin.system.exitProcess

class CalculatorConsole() {

   val run = {
      println("*** Console Calculator ***")
      while (true) {

         //chon phep tinh
         val operation = getArithmeticOperation() as Operation
         val number1: Double
         val number2: Double

         //nhap lieu
         when (operation) {
            Operation.ADDITION,
            Operation.SUBTRACTION,
            Operation.MULTIPLICATION,
            Operation.DIVISION,
            Operation.MODULO,
            Operation.POWER,
               -> {
               println("Enter two numbers:")
               number1 = readDoubleInput("Number 1: ") as Double
               number2 = readDoubleInput("Number 2: ") as Double
            }

            Operation.SQUARE_ROOT, Operation.EXP, Operation.NATURAL_LOG -> {
               number1 = readDoubleInput("Enter mostFrequentElement number: ") as Double
               number2 = 0.0
            }

            Operation.SIN,
            Operation.COS,
            Operation.TAN,
               -> {
               number1 = readDoubleInput("Enter an angle in degrees: ") as Double
               number2 = 0.0
            }
         }

         //tinh toan
         operation.op(number1, number2).also {
            println(
               when (operation) {
                  Operation.SQUARE_ROOT, Operation.SIN, Operation.COS, Operation.TAN, Operation.EXP, Operation.NATURAL_LOG ->
                     "${operation.symbol.replace("x", "$number1")} = $it"

                  else -> "$number1 ${operation.symbol} $number2 = $it"
               }
            )

            print("Do you want to continue? (y/n): ")
            if (readln() != "y") exitProcess(0)
         }
      }
   }

    private val getArithmeticOperation = getArithmeticOperation@{
      println("Operation Options:")
      println("[1]. Addition (${Operation.ADDITION.symbol})")
      println("[2]. Subtraction (${Operation.SUBTRACTION.symbol})")
      println("[3]. Multiplication (${Operation.MULTIPLICATION.symbol})")
      println("[4]. Division (${Operation.DIVISION.symbol})")
      println("[5]. Modulo (${Operation.MODULO.symbol})")
      println("[6]. Power (${Operation.POWER.symbol})")
      println("[7]. Square root ${Operation.SQUARE_ROOT.symbol}")
      println("[8]. Sine (${Operation.SIN.symbol})")
      println("[9]. Cosine (${Operation.COS.symbol})")
      println("[10]. Tangent (${Operation.TAN.symbol})")
      println("[11]. Exponential (${Operation.EXP.symbol})")
      println("[12]. Natural log (${Operation.NATURAL_LOG.symbol})")

      while (true) {
         print("Enter your choice (1/2/3/4/5/6/7/8/9/10/11/12): ")
         when (readln()) {
            "1" -> return@getArithmeticOperation Operation.ADDITION
            "2" -> return@getArithmeticOperation Operation.SUBTRACTION
            "3" -> return@getArithmeticOperation Operation.MULTIPLICATION
            "4" -> return@getArithmeticOperation Operation.DIVISION
            "5" -> return@getArithmeticOperation Operation.MODULO
            "6" -> return@getArithmeticOperation Operation.POWER
            "7" -> return@getArithmeticOperation Operation.SQUARE_ROOT
            "8" -> return@getArithmeticOperation Operation.SIN
            "9" -> return@getArithmeticOperation Operation.COS
            "10" -> return@getArithmeticOperation Operation.TAN
            "11" -> return@getArithmeticOperation Operation.EXP
            "12" -> return@getArithmeticOperation Operation.NATURAL_LOG
            else -> println("Invalid choice. Please try again.")
         }
      }
   }

    private val readDoubleInput = readDoubleInput@{ prompt: String ->
      while (true) {
         print(prompt)
         val num = readln()
         try {
            return@readDoubleInput num.toDouble()
         } catch (e: Exception) {
            println("Error reading input: ${e.message}")
         }
      }
   }

   enum class Operation(
      val op: (Double, Double) -> Double,
      val symbol: String,
   ) {
      ADDITION({ a, b -> a + b }, "+"),
      SUBTRACTION({ a, b -> a - b }, "-"),
      MULTIPLICATION({ a, b -> a * b }, "*"),
      DIVISION({ a, b -> a / b }, "/"),
      MODULO({ a, b -> a % b }, "%"),
      POWER({ a, b -> a.pow(b) }, "^"),
      SQUARE_ROOT({ a, _ -> sqrt(a) }, "√(x)"),
      SIN({ a, _ -> sin(Math.toRadians(a)) }, "sin(x)"),
      COS({ a: Double, _ -> cos(Math.toRadians(a)) }, "cos(x)"),
      TAN({ a: Double, _ -> tan(Math.toRadians(a)) }, "tan(x)"),
      EXP({ a: Double, _ -> exp(a) }, "EXP(x)"),
      NATURAL_LOG({ a: Double, _ -> ln(a) }, "ln(x)"),
   }
}