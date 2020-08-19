package com.wp.codingconstraint

//正确类名
class CorrectClass {
}

//正确类名
class Correct {
}

//类名没用大驼峰
class errorClass {
}

//类名没用大驼峰
class error_class {
}

//类名没用大驼峰
class Error_class {
}

//类名拼写错误
class ErrorClbss {
}

//成员变量命名验证
class FieldValid {
    //正确变量名
    private val errorField = ""

    //正确变量名
    private val field = ""

    //变量名没使用小驼峰
    private val ErrorField = ""

    //变量名没使用小驼峰
    private val Error_Field = ""

    //变量名没使用小驼峰
    private val Error_field = ""

    //变量名拼写错误
    private val errorFiald = ""
}

//局部变量命名验证
class LocalVariableValid {
    private fun testFunction() {
        //正确变量名
        val errorField = ""
        //正确变量名
        val field = ""
        //变量名没使用小驼峰
        val ErrorField = ""
        //变量名没使用小驼峰
        val Error_Field = ""
        //变量名没使用小驼峰
        val Error_field = ""
        //变量名拼写错误
        val errorFiald = ""
    }
}

//方法命名验证
class MethodNameValid {
    //正确方法名
    private fun testFunction() {
    }

    //正确方法名
    private fun function() {
    }

    //方法名没使用小驼峰
    private fun ErrorFunction() {
    }

    //方法名没使用小驼峰
    private fun Error_Function() {
    }

    //方法名没使用小驼峰
    private fun Error_function() {
    }

    //方法名拼写错误
    private fun errorFanction() {
    }
}

class StaticFieldValid {
    companion object {
        //正确静态变量
        val TEST_FIELD = ""

        //正确静态变量
        val TEST = ""

        //静态变量不符合规范
        val test_FIELD = ""

        //静态变量拼写不正确
        val TEST_FIALD = ""
    }
}

