package fr.lapoumerole

class User(var num: Int, val tel: String, val auth: String = "A") {
}

fun numCompare(user: User, num: Int) = (user.num - num)

val vincent = User(0,"0664804606")
val jeanne = User(1,"0668755503")
val brigitte = User(2,"0611714554")
val users = listOf(vincent, jeanne, brigitte)
