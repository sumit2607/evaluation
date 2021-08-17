package YouPart

import java.util.*

fun main(){
    val sc = Scanner(System.`in`)
    val day = sc.next();
   val arrayList = arrayListOf<String>("India", "africa" , "new Zealand", "Ghana", "America" ,"Australia")
  if (arrayList.contains(day)){
      println(arrayList.indexOf(day))
      println("countrty found")
  }else{
      println("not found")
  }
}