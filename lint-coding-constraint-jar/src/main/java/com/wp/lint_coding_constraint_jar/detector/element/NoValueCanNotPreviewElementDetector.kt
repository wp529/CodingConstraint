package com.wp.lint_coding_constraint_jar.detector.element

import com.android.SdkConstants.*
import com.android.tools.lint.detector.api.*
import org.w3c.dom.Element

/**
 * 一些控件如果不设置值那么预览不出来,eg:TextView,EditText
 * 必须使用tools:text || tools:hint标签以便于预览
 * create by WangPing
 * on 2020/8/11
 */
class NoValueCanNotPreviewElementDetector : Detector(), Detector.XmlScanner {
    companion object {
        val ISSUE = Issue.create(
            id = "NoValueCanNotPreviewError",
            briefDescription = "布局无法预览",
            explanation = "布局无法预览,请添加对应的tools属性以便于预览",
            category = Category.SECURITY,
            priority = 5,
            severity = Severity.ERROR,
            implementation = Implementation(
                NoValueCanNotPreviewElementDetector::class.java,
                Scope.RESOURCE_FILE_SCOPE
            )
        )
    }

    override fun getApplicableElements(): Collection<String>? {
        //这没办法,源码里不支持后缀扫描,那就只有全盘扫描
        //例如我自定义了个DemoTextView就扫描不出来,你要是自定义的View继承自TextView但不以TextView结尾那我没啥好说的了
        return XmlScannerConstants.ALL
    }

    override fun visitElement(context: XmlContext, element: Element) {
        when {
            element.nodeName.endsWith(TEXT_VIEW) -> {
                //TextView如果没有设置text那么预览不了
                val androidTextAttribute = element.getAttributeNodeNS(ANDROID_URI, ATTR_TEXT)
                val toolsTextAttribute = element.getAttributeNodeNS(TOOLS_URI, ATTR_TEXT)
                if (!toolsTextAttribute?.value.isNullOrEmpty() || !androidTextAttribute?.value.isNullOrEmpty()) {
                    //android:text或者tools:text属性值不为空,那么能预览
                    return
                }
                context.report(
                    ISSUE,
                    element,
                    context.getLocation(element),
                    message = "TextView如果不设置android:text或者tools:text属性,那么不能预览,请设置。" +
                            "什么情况选择android:text,什么情况选择tools:text请了解好自行选择",
                    quickfixData = LintFix.create()
                        .set(TOOLS_URI, ATTR_TEXT, "自行设置")
                        .build()
                )
            }
            element.nodeName.endsWith(IMAGE_VIEW) -> {
                val androidSrcAttribute = element.getAttributeNodeNS(ANDROID_URI, ATTR_SRC)
                val toolsSrcAttribute = element.getAttributeNodeNS(TOOLS_URI, ATTR_SRC)
                if (!androidSrcAttribute?.value.isNullOrEmpty() || !toolsSrcAttribute?.value.isNullOrEmpty()) {
                    //android:src或者tools:src属性值不为空,那么能预览
                    return
                }
                context.report(
                    ISSUE,
                    element,
                    context.getLocation(element),
                    message = "ImageView如果不设置android:src或者tools:src属性,那么不能预览,请设置。" +
                            "什么情况选择android:src,什么情况选择tools:src请了解好自行选择",
                    quickfixData = LintFix.create()
                        .set(TOOLS_URI, ATTR_SRC, "自行设置")
                        .build()
                )
            }
            element.nodeName.endsWith(EDIT_TEXT) -> {
                val androidTextAttribute = element.getAttributeNodeNS(ANDROID_URI, ATTR_TEXT)
                val androidHintAttribute = element.getAttributeNodeNS(ANDROID_URI, ATTR_HINT)
                val toolsTextAttribute = element.getAttributeNodeNS(TOOLS_URI, ATTR_TEXT)
                val toolsHintAttribute = element.getAttributeNodeNS(TOOLS_URI, ATTR_HINT)
                arrayListOf(
                    androidTextAttribute,
                    androidHintAttribute,
                    toolsTextAttribute,
                    toolsHintAttribute
                ).forEach {
                    if (!it?.value.isNullOrEmpty()) {
                        //但凡设置了一个都可以预览,就不报错
                        return
                    }
                }
                context.report(
                    ISSUE,
                    element,
                    context.getLocation(element),
                    message = "EditText如果不设置android:text || android:hint || tools:text || tools:hint属性," +
                            "那么不能预览,请设置。上述属性请自行选择设置",
                    quickfixData = LintFix.create()
                        .set(TOOLS_URI, ATTR_TEXT, "自行设置")
                        .build()
                )
            }
            element.nodeName.endsWith("RecyclerView") -> {
                val androidItemCountAttribute =
                    element.getAttributeNodeNS(ANDROID_URI, ATTR_ITEM_COUNT)
                val toolsItemCountAttribute = element.getAttributeNodeNS(TOOLS_URI, ATTR_ITEM_COUNT)
                val toolsListItemAttribute = element.getAttributeNodeNS(TOOLS_URI, ATTR_LISTITEM)
                if (androidItemCountAttribute?.value.isNullOrEmpty() && toolsItemCountAttribute?.value.isNullOrEmpty()) {
                    //android:itemCount或者tools:itemCount属性值为空,那么预览效果很差
                    context.report(
                        ISSUE,
                        element,
                        context.getLocation(element),
                        message = "RecyclerView如果不设置android:itemCount或者tools:itemCount属性,那么条目预览很差,请设置。" +
                                "什么情况选择android:itemCount,什么情况选择tools:itemCount请了解好自行选择",
                        quickfixData = LintFix.create()
                            .set(TOOLS_URI, ATTR_ITEM_COUNT, "4")
                            .build()
                    )
                }
                if (toolsListItemAttribute?.value.isNullOrEmpty()) {
                    //android:listItem或者tools:listItem属性值为空,那么预览效果很差
                    context.report(
                        ISSUE,
                        element,
                        context.getLocation(element),
                        message = "RecyclerView如果不设置tools:listitem属性,那么条目预览很差,请设置。多条目数据类型如不好设置可忽略,但最好设置一个",
                        quickfixData = LintFix.create()
                            .set(TOOLS_URI, ATTR_LISTITEM, "自行设置")
                            .build()
                    )
                }
            }
            element.nodeName.endsWith(PROGRESS_BAR) -> {
                //判断是否设置进度
                val androidProgressAttribute =
                    element.getAttributeNodeNS(ANDROID_URI, ATTR_PROGRESS)
                val toolsProgressAttribute = element.getAttributeNodeNS(TOOLS_URI, ATTR_PROGRESS)
                if (androidProgressAttribute?.value.isNullOrEmpty() && toolsProgressAttribute?.value.isNullOrEmpty()) {
                    //android:progress或者tools:progress属性值为空,那么不能预览
                    context.report(
                        ISSUE,
                        element,
                        context.getLocation(element),
                        message = "ProgressBar如果不设置android:progress或者tools:progress属性,那么进度效果不能预览,请设置。" +
                                "什么情况选择android:progress,什么情况选择tools:progress请了解好自行选择",
                        quickfixData = LintFix.create()
                            .set(TOOLS_URI, ATTR_PROGRESS, "自行设置")
                            .build()
                    )
                }

                //判断是否设置进度样式
                val androidProgressDrawableAttribute =
                    element.getAttributeNodeNS(ANDROID_URI, ATTR_PROGRESS_DRAWABLE)
                val toolsProgressDrawableAttribute =
                    element.getAttributeNodeNS(TOOLS_URI, ATTR_PROGRESS_DRAWABLE)
                if (androidProgressDrawableAttribute?.value.isNullOrEmpty() && toolsProgressDrawableAttribute?.value.isNullOrEmpty()) {
                    //android:progressDrawable或者tools:progressDrawable属性值为空,那么不能预览
                    context.report(
                        ISSUE,
                        element,
                        context.getLocation(element),
                        message = "ProgressBar如果不设置android:progressDrawable或者tools:progressDrawable属性,那么进度条样式不能预览,请设置。" +
                                "什么情况选择android:progressDrawable,什么情况选择tools:progressDrawable请了解好自行选择",
                        quickfixData = LintFix.create()
                            .set(TOOLS_URI, ATTR_PROGRESS_DRAWABLE, "自行设置")
                            .build()
                    )
                }
            }
        }
    }
}