package Kotlin

fun main(){
    val arr = intArrayOf(10, 20, 10,40,30)

    println("Duplicate elements in given array: ")
    //Searches for duplicate element
    //Searches for duplicate element
    for (i in arr.indices) {
        for (j in i + 1 until arr.size) {
            if (arr[i] == arr[j])
            println(arr[j]);

        }

    }

}