package com.wp.lint_coding_constraint_jar

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue
import com.wp.lint_coding_constraint_jar.detector.ActivityAndFragmentDetector
import com.wp.lint_coding_constraint_jar.detector.NameDetector
import com.wp.lint_coding_constraint_jar.detector.element.ElementIdDetector
import com.wp.lint_coding_constraint_jar.detector.element.InvisibleElementDetector
import com.wp.lint_coding_constraint_jar.detector.element.NoValueCanNotPreviewElementDetector

/**
 * 注册Issue
 * create by WangPing
 * on 2020/8/11
 */
class IssueRegister : IssueRegistry() {
    override val issues: List<Issue>
        get() = arrayListOf(
            NameDetector.ISSUE,
            InvisibleElementDetector.ISSUE,
            NoValueCanNotPreviewElementDetector.ISSUE,
            ElementIdDetector.ISSUE,
            ActivityAndFragmentDetector.ISSUE
        )

    override val api: Int
        get() = CURRENT_API
}