package Kotlin

import java.util.*

fun main(){


        val sc = Scanner(System.`in`)
        val n = sc.nextInt()
        val arr = IntArray(n)
        for (i in 0 until n) {
            arr[i] = sc.nextInt()
        }
        var sum = 0
        for (i in arr.indices) {
            sum += arr[i]
        }
        val avg = sum / n
        println(avg)
    }

