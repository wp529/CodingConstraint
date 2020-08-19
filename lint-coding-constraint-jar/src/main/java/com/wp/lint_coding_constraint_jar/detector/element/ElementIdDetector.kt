package com.wp.lint_coding_constraint_jar.detector.element

import com.android.SdkConstants.*
import com.android.tools.lint.detector.api.*
import com.wp.lint_coding_constraint_jar.util.*
import org.w3c.dom.Attr

/**
 * 规范布局控件的id命名
 * create by WangPing
 * on 2020/8/11
 */
class ElementIdDetector : Detector(), Detector.XmlScanner {
    companion object {
        val ISSUE = Issue.create(
            id = "ElementIdInvalidError",
            briefDescription = "控件id命名错误",
            explanation = "控件id命名不规范,请过饭命名",
            category = Category.SECURITY,
            priority = 5,
            severity = Severity.ERROR,
            implementation = Implementation(
                ElementIdDetector::class.java,
                Scope.RESOURCE_FILE_SCOPE
            )
        )
    }

    override fun getApplicableAttributes(): Collection<String>? {
        return arrayListOf(ATTR_ID)
    }

    override fun visitAttribute(context: XmlContext, attribute: Attr) {
        val attrIdValue = attribute.value.substring(5, attribute.value.length)
        val elementName = attribute.ownerElement.nodeName
        //验证前缀
        when {
            elementName.endsWith(FRAME_LAYOUT) -> {
                if (!attrIdValue.startsWith("fl")) {
                    context.report(
                        ISSUE,
                        context.getNameLocation(attribute),
                        message = noticeElementIdNameInvalid(elementName, "fl")
                    )
                    return
                }
            }
            elementName.endsWith(LINEAR_LAYOUT) -> {
                if (!attrIdValue.startsWith("ll")) {
                    context.report(
                        ISSUE,
                        context.getNameLocation(attribute),
                        message = noticeElementIdNameInvalid(elementName, "ll")
                    )
                    return
                }
            }
            elementName.endsWith(RELATIVE_LAYOUT) -> {
                if (!attrIdValue.startsWith("rl")) {
                    context.report(
                        ISSUE,
                        context.getNameLocation(attribute),
                        message = noticeElementIdNameInvalid(elementName, "rl")
                    )
                    return
                }
            }
            elementName.endsWith(SCROLL_VIEW) -> {
                if (!attrIdValue.startsWith("sv")) {
                    context.report(
                        ISSUE,
                        context.getNameLocation(attribute),
                        message = noticeElementIdNameInvalid(elementName, "sv")
                    )
                    return
                }
            }
            elementName.endsWith(BUTTON) -> {
                if (!attrIdValue.startsWith("btn")) {
                    context.report(
                        ISSUE,
                        context.getNameLocation(attribute),
                        message = noticeElementIdNameInvalid(elementName, "btn")
                    )
                    return
                }
            }
            elementName.endsWith(GRID_VIEW) -> {
                if (!attrIdValue.startsWith("gv")) {
                    context.report(
                        ISSUE,
                        context.getNameLocation(attribute),
                        message = noticeElementIdNameInvalid(elementName, "gv")
                    )
                    return
                }
            }
            elementName.endsWith(EDIT_TEXT) -> {
                if (!attrIdValue.startsWith("et")) {
                    context.report(
                        ISSUE,
                        context.getNameLocation(attribute),
                        message = noticeElementIdNameInvalid(elementName, "et")
                    )
                    return
                }
            }
            elementName.endsWith(LIST_VIEW) -> {
                if (!attrIdValue.startsWith("lv")) {
                    context.report(
                        ISSUE,
                        context.getNameLocation(attribute),
                        message = noticeElementIdNameInvalid(elementName, "lv")
                    )
                    return
                }
            }
            elementName.endsWith(TEXT_VIEW) -> {
                if (!attrIdValue.startsWith("tv")) {
                    context.report(
                        ISSUE,
                        context.getNameLocation(attribute),
                        message = noticeElementIdNameInvalid(elementName, "tv")
                    )
                    return
                }
            }
            elementName.endsWith(IMAGE_VIEW) -> {
                if (!attrIdValue.startsWith("iv")) {
                    context.report(
                        ISSUE,
                        context.getNameLocation(attribute),
                        message = noticeElementIdNameInvalid(elementName, "iv")
                    )
                    return
                }
            }
            elementName.endsWith(PROGRESS_BAR) -> {
                if (!attrIdValue.startsWith("pb")) {
                    context.report(
                        ISSUE,
                        context.getNameLocation(attribute),
                        message = noticeElementIdNameInvalid(elementName, "pb")
                    )
                    return
                }
            }
            elementName.endsWith(HORIZONTAL_SCROLL_VIEW) -> {
                if (!attrIdValue.startsWith("hsv")) {
                    context.report(
                        ISSUE,
                        context.getNameLocation(attribute),
                        message = noticeElementIdNameInvalid(elementName, "hsv")
                    )
                    return
                }
            }
            elementName.endsWith(RELATIVE_LAYOUT) -> {
                if (!attrIdValue.startsWith("rl")) {
                    context.report(
                        ISSUE,
                        context.getNameLocation(attribute),
                        message = noticeElementIdNameInvalid(elementName, "rl")
                    )
                    return
                }
            }
            elementName.endsWith("RecyclerView") -> {
                if (!attrIdValue.startsWith("rv")) {
                    context.report(
                        ISSUE,
                        context.getNameLocation(attribute),
                        message = noticeElementIdNameInvalid(elementName, "rv")
                    )
                    return
                }
            }
        }
        //验证命名规范
        if (attrIdValue.isAllLowerCase()) {
            //全小写
            if (attrIdValue.containsCharIsNotLetter()) {
                //有非字母的字符,命名不规范
                context.report(
                    ISSUE,
                    context.getNameLocation(attribute),
                    message = noticeVariableNameInvalid(attrIdValue)
                )
            } else {
                if (!attrIdValue.wordSpellCorrect()) {
                    //拼写不正确
                    context.report(
                        ISSUE,
                        context.getNameLocation(attribute),
                        message = noticeWordSpellIncorrect(attrIdValue)
                    )
                }
            }
        } else {
            if (attrIdValue.isLowerCamelCase()) {
                //是小驼峰,检查拼写是否有误
                if (!attrIdValue.eachWordSpellCorrect()) {
                    context.report(
                        ISSUE,
                        context.getNameLocation(attribute),
                        message = noticeWordSpellIncorrect(attrIdValue)
                    )
                }
            } else {
                //不是小驼峰,命名不规范
                if (!attrIdValue.eachWordSpellCorrect()) {
                    context.report(
                        ISSUE,
                        context.getNameLocation(attribute),
                        message = noticeVariableNameInvalid(attrIdValue)
                    )
                }
            }
        }
    }

    //单词拼写错误提示
    private fun noticeWordSpellIncorrect(incorrectWord: String): String =
        "${incorrectWord}单词拼写不正确,请检查单词是否拼写有误,若确认无误,请联系lint规则提供者"

    //变量命名不规范错误提示
    private fun noticeVariableNameInvalid(variableName: String): String =
        "${variableName}命名不规范,变量命名采用小驼峰方式,形如:testVariable"

    //控件命名不规范错误提示
    private fun noticeElementIdNameInvalid(elementName: String, prefix: String): String =
        "${elementName}的id应以${prefix}开头,以便能直接看出使用的什么控件"

}