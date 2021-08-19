package Kotlin

import java.util.*
import kotlin.collections.HashMap

//Main Function, entry Point of Program
fun main(args: Array<String>) {

    //Input Stream
    val sc = Scanner(System.`in`)

    //input April20.string value
    println("Input String : ")
    val str: String = sc.nextLine()

    val characterHashMap: HashMap<Char, Int> = HashMap<Char, Int>()
    var c: Char
    // Scan string and build hash table
    for(i in str.indices){
        c = str[i]
        if (characterHashMap.containsKey(c)) {
            // increment count corresponding to c
            characterHashMap[c] = characterHashMap[c]!!+1

        } else {
            characterHashMap[c] = 1
        }
    }

    //print All Occurrence of character
    println("All character Count: $characterHashMap")
}