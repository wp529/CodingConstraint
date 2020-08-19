package com.wp.lint_coding_constraint_jar.detector

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.*
import org.jetbrains.uast.visitor.AbstractUastVisitor


/**
 * 检查类里是否包含某些方法,有些类必须要有固定方法
 * 例如:
 * Activity里必须要有静态方法startActivity(params)
 * Fragment里必须要有静态方法newInstance(params)
 * 以便于可以直观的知道使用此Activity或Fragment必须要什么东西以及哪里需要启动此页面
 * create by WangPing
 * on 2020/8/11
 */
class ActivityAndFragmentDetector : Detector(), Detector.UastScanner {
    companion object {
        val ISSUE = Issue.create(
            id = "NoRequiredMethodError",
            briefDescription = "缺失必要方法",
            explanation = "请添加必要的方法",
            category = Category.USABILITY,
            priority = 5,
            severity = Severity.ERROR,
            implementation = Implementation(
                ActivityAndFragmentDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }

    //指定扫描范围
    override fun getApplicableUastTypes(): List<Class<out UElement>>? {
        return arrayListOf(UClass::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return object : UElementHandler() {
            override fun visitClass(node: UClass) {
                node.accept(object : AbstractUastVisitor() {
                    override fun visitClass(node: UClass): Boolean {
                        return classContainsRequiredMethod(context, node)
                    }

                    private fun classContainsRequiredMethod(
                        context: JavaContext,
                        node: UClass
                    ): Boolean {
                        if (node.name?.startsWith("Base") == true) {
                            //activity和fragment的基类不需要验证是否有startActivity、newInstance静态方法
                            return super.visitClass(node)
                        }
                        var classSuper = node.supers.find {
                            !it.isInterface
                        }
                        var isActivity = false
                        var isFragment = false
                        while (classSuper != null) {
                            if (classSuper.name == "Activity") {
                                isActivity = true
                                break
                            }
                            if (classSuper.name == "Fragment") {
                                isFragment = true
                                break
                            }
                            if (classSuper.supers.isNullOrEmpty()) {
                                break
                            }
                            classSuper = classSuper.supers.find {
                                !it.isInterface
                            }
                        }
                        if (isActivity) {
                            //kotlin版和Java版判断,满足一个就行
                            val hadRequiredMethod = node.innerClasses?.find {
                                it.isStatic && it.name == "Companion" && it.methods.find { method ->
                                    method.name == "startActivity"
                                } != null
                            } != null || node.methods.find {
                                it.isStatic && it.name == "startActivity"
                            } != null
                            if (!hadRequiredMethod) {
                                context.report(
                                    ISSUE,
                                    context.getNameLocation(node),
                                    message = "Activity里必须要有静态方法startActivity(),以便于可以统一管理此Activity必须要什么参数以及哪里需要启动此页面"
                                )
                                return true
                            }
                        }
                        if (isFragment) {
                            //kotlin版和Java版判断,满足一个就行
                            val hadRequiredMethod = node.innerClasses?.find {
                                it.isStatic && it.name == "Companion" && it.methods.find { method ->
                                    method.name == "newInstance"
                                } != null
                            } != null || node.methods.find {
                                it.isStatic && it.name == "newInstance"
                            } != null
                            if (!hadRequiredMethod) {
                                context.report(
                                    ISSUE,
                                    context.getNameLocation(node),
                                    message = "Fragment里必须要有静态方法newInstance(),以便于可以统一管理此newInstance必须要什么参数以及哪里需要启动此页面"
                                )
                                return true
                            }
                        }
                        return super.visitClass(node)
                    }
                })
            }
        }
    }

}