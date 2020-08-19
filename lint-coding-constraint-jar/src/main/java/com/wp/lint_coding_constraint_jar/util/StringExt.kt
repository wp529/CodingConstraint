package com.wp.lint_coding_constraint_jar.util

import java.lang.StringBuilder
import java.util.*

/**
 * 字符串是否全小写
 *
 * @return 是否全小写
 */
fun String.isAllLowerCase(): Boolean {
    this.forEach {
        if (it.isUpperCase()) {
            return false
        }
    }
    return true
}

/**
 * 是否是大驼峰命名
 * @return 是否合法
 */
fun String.isBigCamelCase(): Boolean = this.matches("^(([A-Z][a-z]+)+)\$".toRegex())

/**
 * 是否是小驼峰命名
 * @return 是否合法
 */
fun String.isLowerCamelCase(): Boolean = this.matches("^[a-z]+(([A-Z][a-z]+)+)\$".toRegex())

/**
 * 每个单词是否都拼写正确
 * @param isBigCamelCase 是否是大驼峰的命名方式
 * @return 是否正确
 */
fun String.eachWordSpellCorrect(isBigCamelCase: Boolean = false): Boolean {
    splitByUpperCase(isBigCamelCase).forEach {
        if (!it.wordSpellCorrect()) {
            return false
        }
    }
    return true
}

/**
 * 单个单词是否拼写正确
 *
 * @return 是否正确
 */
fun String.wordSpellCorrect(): Boolean {
    return EnglishDictionary.contains(this)
}

/**
 * 判断单词中是否有除字母外的字符
 *
 * @return true有字母外的字符
 */
fun String.containsCharIsNotLetter(): Boolean {
    this.forEach {
        if (it !in 'a'..'z' && it !in 'A'..'Z') {
            return true
        }
    }
    return false
}

/**
 * 单词是否全大写,没管特殊符号
 *
 * @return 是否全大写
 */
fun String.isAllUpperCase(): Boolean {
    this.forEach {
        if (it.isLowerCase()) {
            return false
        }
    }
    return true
}

//将字符串按大写拆分成单词
private fun String.splitByUpperCase(isBigCamelCase: Boolean = false): List<String> {
    val words = arrayListOf<String>()
    val word = StringBuilder()
    var currentIndex = 0
    this.forEach {
        if (it.isUpperCase()) {
            if (!isBigCamelCase || currentIndex != 0) {
                words.add(word.toString().toLowerCase(Locale.ROOT))
                word.clear()
            }
        }
        word.append(it)
        currentIndex++
    }
    words.add(word.toString().toLowerCase(Locale.ROOT))
    return words
}


