package org.example

import khttp.structures.authorization.BasicAuthorization

fun main(){
    println("choose action:\n\nsign up\n\nlogin\n\nrestore password\n\ncheck receipt\n\nreceipt info\n")
    when(readLine()?:""){
        "sign up" -> {
            sign_up()
        }
        "login" -> {
            login()
        }
        "restore password" -> {
            restore_password()
        }
        "check receipt" -> {
            check_receipt()
        }
        "receipt info" -> {
            get_info()
        }
        else -> {
            println("incorrect input")
        }
    }
}

//регистрация
fun sign_up(){
    val url_reg = "https://proverkacheka.nalog.ru:9999/v1/mobile/users/signup"
    println("write your email: ")
    val email= readLine()?:""
    println("write your phone number: ")
    val number= readLine()?:""
    println("write your name: ")
    val name= readLine()?:""
    val answer=khttp.post(
        url = url_reg,
        json = mapOf("email" to email, "name" to name, "phone" to number),
        headers = mapOf("Content-type" to "application/json", "Accept" to "application/json")
    )
    if (answer.statusCode==204){
        println("see sms with password")
    }
    else{
        println("error: ${answer.statusCode}")
        //409 - пользователь уже существует
        //500 - номер телефона некорректный
        //400 - адрес электронной почты некорректный
    }

}

//логин (неизвестно зачем, но есть)
fun login(){
    val url_login="https://proverkacheka.nalog.ru:9999/v1/mobile/users/login"
    println("write your phone number: ")
    val number= readLine()?:""
    println("write your password: ")
    val password= readLine()?:""
    val answer = khttp.get(
        url_login,
        auth = BasicAuthorization(number, password),
        headers = mapOf("Content-type" to "application/json", "Accept" to "application/json")
    )
    if (answer.statusCode==200){
        println(answer.jsonObject)
    }
    else{
        println("error: ${answer.statusCode}")
        //403 - некорректные данные пользователя
    }
}

//восстановления пароля
fun restore_password(){
    val url_pass= "https://proverkacheka.nalog.ru:9999/v1/mobile/users/restore"
    println("write your phone number: ")
    val number = readLine()?:""
    val answer = khttp.post(
        url_pass,
        headers = mapOf("Content-type" to "application/json", "Accept" to "application/json"),
        json = mapOf("phone" to number)
    )
    if (answer.statusCode==204){
        println("see sms with password")
    }
    else{
        println("error: ${answer.statusCode}")
        //404 - пользователь не найден
    }
}

//проверка существования чека
fun check_receipt(){
    println("write fn number (16 digits): ")
    val fn = readLine()?:""
    println("write fd number (up to 10 digits): ")
    val fd = readLine()?:""
    println("write fp number (up to 10 digits): ")
    val fp = readLine()?:""
    println("write year: ")
    val year= readLine()?:""
    println("write month number (example: 08): ")
    val month= readLine()?:""
    println("write day: ")
    val day= readLine()?:""
    println("write time without colon (example: 1036 is 10:36): ")
    val time= readLine()?:""
    println("write sum without point (example: 14590 is 145.90): ")
    val sum = readLine()?:""
    println("write your phone number: ")
    val number = readLine()?:""
    println("write your password: ")
    val password= readLine()?:""
    val url_check = "https://proverkacheka.nalog.ru:9999/v1/ofds/*/inns/*/fss/"+fn+"/operations/1/tickets/"+fd+"?fiscalSign="+fp+"&date="+year+"-"+month+"-"+day+"T"+time+"&sum="+sum
    val answer=khttp.get(
        url_check,
        headers = mapOf("Content-type" to "application/json", "Accept" to "application/json", "Device-Id" to "", "Device-OS" to ""),
        auth = BasicAuthorization(number, password)
    )
    if (answer.statusCode==204){
        println("check exists")
    }
    else{
        println("error: ${answer.statusCode}")
        //406 - чека нет в базе фнс, либо его не существует, либо он слишком старый, или если дата/сумма некорректная или не совпадает с датой/суммой, указанной в чеке
        //400 - не указан параметр дата/сумма
    }
}

//детальная информация по чеку
fun get_info(){
    println("write fn number (16 digits): ")
    val fn = readLine()?:""
    println("write fd number (up to 10 digits): ")
    val fd = readLine()?:""
    println("write fp number (up to 10 digits): ")
    val fp = readLine()?:""
    println("write your phone number: ")
    val number = readLine()?:""
    println("write your password: ")
    val password= readLine()?:""
    val url_get = "https://proverkacheka.nalog.ru:9999/v1/inns/*/kkts/*/fss/" + fn + "/tickets/" + fd + "?fiscalSign=" + fp + "&sendToEmail=no"
    val answer=khttp.get(
        url_get,
        headers = mapOf("Content-type" to "application/json", "Accept" to "application/json", "Device-Id" to "", "Device-OS" to ""),
        auth = BasicAuthorization(number, password)
    )
    if (answer.statusCode==200){
        println(answer.jsonObject)
    }
    else if (answer.statusCode==202){
        val answer2=khttp.get(
            url_get,
            headers = mapOf("Content-type" to "application/json", "Accept" to "application/json", "Device-Id" to "", "Device-OS" to ""),
            auth = BasicAuthorization(number, password)
        )
        println(answer2.jsonObject)
    }
    else{
        println("error: ${answer.statusCode}")
        //406 - чека нет в базе фнс, либо его не существует, либо он слишком старый
        //403 - некорректные данные пользователя
        //500 и 400 - некорректные данные чека
    }
}