package Kotlin

fun main(){
    println("Masai School")

    //Print your name by it storing in a variable

    var str  =" sumit"
    println(str)

    var name = "father name"
    println(name)
    name = "son name"
    println(name)

    //Given the side of the square calculate its perimeter
    var side = 4;
    println(side*4)

    //Given two numbers A and B print which is smaller
    var a = 5;
    var b = 6;
    if(a>b){
        print(a)
    } else {
        print(b)
    }

    //Given a number if it is odd print "Odd Number"
    var number  = 9;
    if(number%2==1){
        println("number is odd")
    }

    //Print the sum of all the numbers starting from 0 to the given limit that are multiples of 3
    var sum =0;
    for (i in 0 .. 10 step 3) {
       if(i%3==0){
           sum+=i

       }
        println(sum)


    }
    //Given two numbers in string format find their difference
    val number1 = "123"
    val number2 = "235"
    val diff = number1.toInt()-number2.toInt()
    println(diff)

    //Given the day in long format like "Monday", "Tuesday" ... print in short format "Mon", "Tue"
    val day = "monday"
    when(day){
      "monday" -> print("mon")
        "tuesday" -> print("tue")
        "wednesday" -> print("wednesday")
        "Thursday" -> print("thu")
        "friday" -> print("fri")
        "saturday" -> print("sat")

    }
    //Given a number print the below set of pattern (Sample for input 3) ```

    for (i in 1..3){
        for (j in 1..i){
            println("$i.$j")
        }
    }
    //Given an array of game scores, print the lowest score
    val score = arrayOf(10,12,2,1,25)

    var low  = score[0]
    for (i in score){
        if(i < low){
            low = i
        }
    }
print(low)



}