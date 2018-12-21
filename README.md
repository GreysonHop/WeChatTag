# WeChatTag
> **简介**

模仿微信标签功能的多行LinearLayout效果实现，及其它一些拓展功能；2.0版本加入一些类的Kotlin代码，并且对自定义组件进行完善，让它以及其内部子组件都更完美地支持内边距和外距。

（提示：因为一开始本人所要做的项目是要做一个”分组“的编辑功能，但是因为该功能借鉴微信，所以本人喜欢叫它”标签“，所以该项目中的”分组“和”标签“其实是同一个意思）



> **页面/功能介绍**

#### 首页

第一个组件是显示分组的结果，图中已有三个标签，用英文的逗号分开，点击后会进入“添加分组”界面；

第二个组件点击进去的是多行LinearLayout组件的拓展功能展示，下面再介绍。

![main](https://git-res.nos-eastchina1.126.net/wechatTag/git_wechatTag_main.png)

#### 添加分组-界面

1、从主页进来，会把默认的三个标签显示在上面的“已选中一栏”中，下面的所有标签如果已经出现在已选中一栏中会高亮。

![add_group1](https://git-res.nos-eastchina1.126.net/wechatTag/git_wechatTag_multiLine1.png)



2、在虚线框中能输入新的标签名，如果点击”选中一栏“中的空白空间，它会自动增加到选中一栏，然后清空虚线框中的内容，如序号3中的图

![add_group2](https://git-res.nos-eastchina1.126.net/wechatTag/git_wechatTag_multiLine2.png)



3、在选中一栏中，点击某个标签，标签会高亮，再次点击高亮的标签会取消选中，即”所有的分组“中对应的标签会取消高亮。而点击”所有的分组“的某个标签，这个标签就会直接被选中，即放入”选中一栏“中。

![add_group3](https://git-res.nos-eastchina1.126.net/wechatTag/git_wechatTag_multiLine3.png)

4、点击保存后回到主页的效果：

![main2](https://git-res.nos-eastchina1.126.net/wechatTag/git_wechatTag_main2.png)



#### 扩展功能-界面

第一个组件，点击后会显示toast，内容为它下一个组件的框中选中的标签内容

第二个组件，是多行LinearLayout修改而成的FlowTagView组件，这里它是单选模式，而且通过自定义属性res_id传入自定义的布局；还展示了对内边距的支持；

第三个组件，也是FlowTagView组件，只是它是默认样式，而且没有设置内边距

![custom](https://git-res.nos-eastchina1.126.net/wechatTag/git_wechatTag_customTag.png)

