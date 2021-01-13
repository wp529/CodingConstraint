### CodingConstraint

是否还在为项目命名风格不一致而头痛？是否还在为预览布局文件一片空白而捶胸顿足？Android编码规范约束工具，使用**自定义lint规则**对代码进行编码规范扫描，对不符合编码规范的代码进行**error告警**，强制统一项目组编码规范，解决你的烦恼。

![img](https://raw.githubusercontent.com/wp529/CodingConstraint/master/pic/1.png)

![img](https://raw.githubusercontent.com/wp529/CodingConstraint/master/pic/2.png)

![img](https://raw.githubusercontent.com/wp529/CodingConstraint/master/pic/3.png)

![img](https://raw.githubusercontent.com/wp529/CodingConstraint/master/pic/4.png)

##### 使用方式

将lint-coding-constraint-arr和lint-coding-constraint-jar导入项目中,然后在需要进行编码规范扫描的module添加implementation project(path: ':lint-coding-constraint-arr')然后rebuild，稍等片刻即可生效

##### 包含的自定义规则有

1. 命名相关
   * 所有的单词命名必须使用正确的英文单词
   * 类命名必须使用大驼峰
   * 成员变量命名必须使用小驼峰
   * 局部变量命名必须使用小驼峰
   * 方法命名必须使用小驼峰
   * 静态变量、常量命名必须使用全大写+下划线
   * 控件id命名必须使用小驼峰且必须以控件类型开头。eg：TextView的id必须为android:id="@+id/tvTest"

2. Activity和Fargment规范

   * Activity类必须包含静态startActivity()方法
   * Fragment类必须包含静态newInstance()方法

3. 布局编写规范

   * 控件必须使用相对应的tools属性，限定这条规则是不使用tools预览效果很差。eg:TextView必须要有tools:text="XXX"或者android:text="XXX"属性，具体详见下表

     | 控件         |                                                   检查的属性 |
     | :----------- | -----------------------------------------------------------: |
     | 所有控件     | 若使用了android:visibility="gone"或者android:visibility="invisible"那么必须添加tools:visibility="visible" |
     | TextView     |                 必须有android:text="XXX"或者tools:text="XXX" |
     | ImageView    |                   必须有android:src="XXX"或者tools:src="XXX" |
     | EditText     | 必须有android:text="XXX"或者tools:text="XXX"或者android:hint="XXX"或者android:hint="XXX" |
     | RecyclerView |            必须有tools:itemCount="XXX"和tools:listitem="XXX" |
     | ProgressBar  |         必须有android:progress="XXX"或者tools:progress="XXX" |
