package com.wp.lint_coding_constraint_jar.util

/**
 * 英文字典
 * create by WangPing
 * on 2020/8/13
 */
object EnglishDictionary {

    private val wordsList by lazy {
        EnglishDictionary.javaClass.getResourceAsStream("/words.txt").bufferedReader().readLines()
    }

    /**
     * 字典里是否包含此单词
     *
     * @param word 单词
     * @return 是否包含
     */
    fun contains(word: String) = wordsList.contains(word)
}