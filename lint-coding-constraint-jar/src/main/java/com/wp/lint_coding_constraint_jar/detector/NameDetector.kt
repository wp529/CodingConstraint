package com.wp.lint_coding_constraint_jar.detector

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.wp.lint_coding_constraint_jar.util.*
import org.jetbrains.uast.*
import org.jetbrains.uast.visitor.AbstractUastVisitor
import java.util.*


/**
 * 检查命名是否规范,是否有拼写错误
 * 类名大驼峰:形如TestClass
 * 方法名小驼峰:形如testFunction()
 * 变量名小驼峰:形如testVariable
 * 静态变量名全大写下划线分割:形如TEST_VARIABLE
 * create by WangPing
 * on 2020/8/11
 */
class NameDetector : Detector(), Detector.UastScanner {
    companion object {
        const val VARIABLE_TYPE_IS_LOCAL = 0x0001
        const val VARIABLE_TYPE_IS_MEMBER = 0x0010
        const val VARIABLE_TYPE_IS_METHOD_PARAMETER = 0x0100
        val ISSUE = Issue.create(
            id = "NamingInvalidError",
            briefDescription = "命名不合法",
            explanation = "请使用规范的命名",
            category = Category.USABILITY,
            priority = 5,
            severity = Severity.ERROR,
            implementation = Implementation(NameDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }

    //指定扫描范围
    override fun getApplicableUastTypes(): List<Class<out UElement>>? {
        return arrayListOf(UClass::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return object : UElementHandler() {
            override fun visitClass(node: UClass) {
                node.accept(NamingConventionVisitor(context))
            }
        }
    }

    class NamingConventionVisitor(
        private val context: JavaContext
    ) : AbstractUastVisitor() {

        //扫描类
        override fun visitClass(node: UClass): Boolean {
            val className = node.name ?: ""
            if (className.isEmpty()) {
                //伴生对象生成的类不扫描
                return super.visitClass(node)
            }
            if (className.isAllLowerCase()) {
                //如果类名全小写证明命名就不规范了
                context.report(
                    ISSUE,
                    context.getNameLocation(node),
                    message = noticeClassNameInvalid(className)
                )
                return true
            } else {
                if (className.isBigCamelCase()) {
                    //是大驼峰
                    if (!className.eachWordSpellCorrect(true)) {
                        //拼写不正确
                        context.report(
                            ISSUE,
                            context.getNameLocation(node),
                            message = noticeWordSpellIncorrect(className)
                        )
                        return true
                    }
                } else {
                    //不是大驼峰
                    context.report(
                        ISSUE,
                        context.getNameLocation(node),
                        message = noticeClassNameInvalid(className)
                    )
                    return true
                }
            }
            return super.visitClass(node)
        }

        //扫描成员变量
        override fun visitField(node: UField): Boolean {
            val variableName = node.name
            if (variableName.startsWith("var")) {
                //kotlin自动生成的乱七八糟的变量
                return super.visitField(node)
            }
            if (node.isStatic) {
                //如果是静态变量
                if (variableName.isAllUpperCase()) {
                    var wordSpellCorrect = true
                    variableName.split("_").forEach {
                        if (!it.toLowerCase(Locale.ROOT).wordSpellCorrect()) {
                            wordSpellCorrect = false
                            return@forEach
                        }
                    }
                    if (!wordSpellCorrect) {
                        //拼写不正确
                        context.report(
                            ISSUE,
                            context.getNameLocation(node),
                            message = noticeWordSpellIncorrect(variableName)
                        )
                        return true
                    }
                } else {
                    //不是全大写,命名不规范
                    context.report(
                        ISSUE,
                        context.getNameLocation(node),
                        message = noticeStaticVariableNameInvalid(variableName)
                    )
                    return true
                }
            } else {
                //不是静态变量
                if (variableName.isAllLowerCase()) {
                    //全小写
                    if (variableName.containsCharIsNotLetter()) {
                        //有非字母的字符,命名不规范
                        context.report(
                            ISSUE,
                            context.getNameLocation(node),
                            message = noticeVariableNameInvalid(
                                variableName,
                                VARIABLE_TYPE_IS_MEMBER
                            )
                        )
                        return true
                    } else {
                        if (!variableName.wordSpellCorrect()) {
                            //拼写不正确
                            context.report(
                                ISSUE,
                                context.getNameLocation(node),
                                message = noticeWordSpellIncorrect(variableName)
                            )
                            return true
                        }
                    }
                } else {
                    if (variableName.isLowerCamelCase()) {
                        //是小驼峰,检查拼写是否有误
                        if (!variableName.eachWordSpellCorrect()) {
                            context.report(
                                ISSUE,
                                context.getNameLocation(node),
                                message = noticeWordSpellIncorrect(variableName)
                            )
                            return true
                        }
                    } else {
                        //不是小驼峰,命名不规范
                        if (!variableName.eachWordSpellCorrect()) {
                            context.report(
                                ISSUE,
                                context.getNameLocation(node),
                                message = noticeVariableNameInvalid(
                                    variableName,
                                    VARIABLE_TYPE_IS_MEMBER
                                )
                            )
                            return true
                        }
                    }
                }
            }
            return super.visitField(node)
        }

        //扫描方法
        override fun visitMethod(node: UMethod): Boolean {
            if (node.name.startsWith("get")
                && node.name.substring(3, node.name.length).isAllUpperCase()
            ) {
                //有些kotlin自动生成的代码确实不好区分
                return super.visitMethod(node)
            }
            if (!node.isConstructor && !node.name.startsWith("component")) {
                //当前方法不是构造方法
                val methodName = node.name
                if (methodName.isAllLowerCase()) {
                    if (methodName.containsCharIsNotLetter()) {
                        //有非字母的字符,命名不规范
                        context.report(
                            ISSUE,
                            context.getNameLocation(node),
                            message = noticeMethodNameInvalid(methodName)
                        )
                        return true
                    } else {
                        if (!methodName.wordSpellCorrect()) {
                            //拼写不正确,直接当命名不规范甩出去
                            context.report(
                                ISSUE,
                                context.getNameLocation(node),
                                message = noticeWordSpellIncorrect(methodName)
                            )
                            return true
                        }
                    }
                } else {
                    if (methodName.isLowerCamelCase()) {
                        //是小驼峰,检查拼写是否有误
                        if (!methodName.eachWordSpellCorrect()) {
                            context.report(
                                ISSUE,
                                context.getNameLocation(node),
                                message = noticeWordSpellIncorrect(methodName)
                            )
                            return true
                        }
                    } else {
                        //不是小驼峰,命名不规范
                        if (!methodName.eachWordSpellCorrect()) {
                            context.report(
                                ISSUE,
                                context.getNameLocation(node),
                                message = noticeMethodNameInvalid(methodName)
                            )
                            return true
                        }
                    }
                }
            }

            if (node.hasParameters()) {
                //方法有参数
                node.parameterList.parameters.forEach {
                    val parameterName = it.name
                    if (!parameterName.startsWith("$")) {
                        //不是kotlin自动生成的乱七八糟的变量
                        if (parameterName.isAllLowerCase()) {
                            //全小写,检查是否拼写有误
                            if (parameterName.containsCharIsNotLetter()) {
                                //有非字母的字符,命名不规范
                                context.report(
                                    ISSUE,
                                    context.getNameLocation(it),
                                    message = noticeVariableNameInvalid(
                                        parameterName,
                                        VARIABLE_TYPE_IS_METHOD_PARAMETER
                                    )
                                )
                                return true
                            } else {
                                if (!parameterName.wordSpellCorrect()) {
                                    //拼写不正确,直接当命名不规范甩出去
                                    context.report(
                                        ISSUE,
                                        context.getNameLocation(it),
                                        message = noticeWordSpellIncorrect(parameterName)
                                    )
                                    return true
                                }
                            }
                        } else {
                            if (parameterName.isLowerCamelCase()) {
                                //是小驼峰,检查拼写是否有误
                                if (!parameterName.eachWordSpellCorrect()) {
                                    context.report(
                                        ISSUE,
                                        context.getNameLocation(it),
                                        message = noticeWordSpellIncorrect(parameterName)
                                    )
                                    return true
                                }
                            } else {
                                //不是小驼峰,命名不规范
                                if (!parameterName.eachWordSpellCorrect()) {
                                    context.report(
                                        ISSUE,
                                        context.getNameLocation(it),
                                        message = noticeVariableNameInvalid(
                                            parameterName,
                                            VARIABLE_TYPE_IS_METHOD_PARAMETER
                                        )
                                    )
                                    return true
                                }
                            }
                        }
                    }
                }
            }
            return super.visitMethod(node)
        }

        //扫描局部变量
        override fun visitLocalVariable(node: ULocalVariable): Boolean {
            val variableName = node.name
            if (variableName.startsWith("var")) {
                //kotlin自动生成的乱七八糟的变量
                return super.visitLocalVariable(node)
            }
            if (variableName.isAllLowerCase()) {
                //全小写,检查是否拼写有误
                if (variableName.containsCharIsNotLetter()) {
                    //有非字母的字符,命名不规范
                    context.report(
                        ISSUE,
                        context.getNameLocation(node),
                        message = noticeVariableNameInvalid(variableName, VARIABLE_TYPE_IS_LOCAL)
                    )
                    return true
                } else {
                    if (!variableName.wordSpellCorrect()) {
                        //拼写不正确,直接当命名不规范甩出去
                        context.report(
                            ISSUE,
                            context.getNameLocation(node),
                            message = noticeWordSpellIncorrect(variableName)
                        )
                        return true
                    }
                }
            } else {
                if (variableName.isLowerCamelCase()) {
                    //是小驼峰,检查拼写是否有误
                    if (!variableName.eachWordSpellCorrect()) {
                        context.report(
                            ISSUE,
                            context.getNameLocation(node),
                            message = noticeWordSpellIncorrect(variableName)
                        )
                        return true
                    }
                } else {
                    //不是小驼峰,命名不规范
                    if (!variableName.eachWordSpellCorrect()) {
                        context.report(
                            ISSUE,
                            context.getNameLocation(node),
                            message = noticeVariableNameInvalid(
                                variableName,
                                VARIABLE_TYPE_IS_LOCAL
                            )
                        )
                        return true
                    }
                }
            }
            return super.visitLocalVariable(node)
        }

        //单词拼写错误提示
        private fun noticeWordSpellIncorrect(incorrectWord: String): String =
            "${incorrectWord}单词拼写不正确,请检查单词是否拼写有误,若确认无误,请联系lint规则提供者"

        //类命名不规范错误提示
        private fun noticeClassNameInvalid(className: String): String =
            "${className}命名不规范,类命名采用大驼峰方式,形如:TestClass"

        //变量命名不规范错误提示
        private fun noticeVariableNameInvalid(
            variableName: String,
            variableType: Int
        ): String =
            "${variableName}命名不规范,${when (variableType) {
                VARIABLE_TYPE_IS_LOCAL -> "局部"
                VARIABLE_TYPE_IS_MEMBER -> "成员"
                VARIABLE_TYPE_IS_METHOD_PARAMETER -> "方法参数"
                else -> ""
            }
            }变量命名采用小驼峰方式,形如:testVariable"

        //静态变量命名不规范错误提示
        private fun noticeStaticVariableNameInvalid(variableName: String): String =
            "${variableName}命名不规范,静态变量命名采用全大写加下划线方式,形如:TEST_VARIABLE"

        //方法命名不规范错误提示
        private fun noticeMethodNameInvalid(variableName: String): String =
            "${variableName}命名不规范,方法命名采用小驼峰方式,形如:testFunction()"
    }
}