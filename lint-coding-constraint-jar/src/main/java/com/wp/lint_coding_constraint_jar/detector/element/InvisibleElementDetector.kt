package com.wp.lint_coding_constraint_jar.detector.element

import com.android.SdkConstants.*
import com.android.tools.lint.detector.api.*
import org.w3c.dom.Attr

/**
 * 使用了android:gone或者android:invisible的标签
 * 必须使用tools:visible标签以便于预览
 * create by WangPing
 * on 2020/8/11
 */
class InvisibleElementDetector : Detector(), Detector.XmlScanner {
    companion object {
        val ISSUE = Issue.create(
            id = "CanNotPreviewError",
            briefDescription = "布局无法预览",
            explanation = "布局无法预览,请添加tools:visible以便于预览",
            category = Category.SECURITY,
            priority = 5,
            severity = Severity.ERROR,
            implementation = Implementation(
                InvisibleElementDetector::class.java,
                Scope.RESOURCE_FILE_SCOPE
            )
        )
    }

    override fun getApplicableAttributes(): Collection<String>? {
        return arrayListOf(ATTR_VISIBILITY)
    }

    override fun visitAttribute(context: XmlContext, attribute: Attr) {
        if (!elementCanPreview(attribute)) {
            context.report(
                ISSUE,
                attribute,
                context.getLocation(attribute),
                message = "添加此属性后布局无法预览,请添加tools:visible以便于预览,如是重叠布局确实无需预览,可以忽略此error",
                quickfixData = LintFix.create().set(TOOLS_URI, ATTR_VISIBILITY, "visible").build()
            )
        }
    }

    //标签是否可以预览
    private fun elementCanPreview(attribute: Attr): Boolean {
        if (attribute.namespaceURI == ANDROID_URI && (attribute.value == "gone" || attribute.value == "invisible")) {
            //使用的是android命名空间,并且visibility在布局文件设置为invisible或者gone
            val toolVisibilityAttribute =
                attribute.ownerElement.getAttributeNodeNS(TOOLS_URI, ATTR_VISIBILITY)
            if (toolVisibilityAttribute?.value != "visible") {
                return false
            }
        }
        return true
    }
}