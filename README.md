# Modify_PDF (background color, bookmarks, ...)
使用Java iText7库，对PDF文件进行修改优化，实现的功能有：
1. 将PDF背景色改为豆沙绿（护眼色），白底论文看着太累眼了orz。
2. 为PDF增加书签（目录），便于在pdf中进行跳转查阅。

## 示例图片
### 修改背景色功能
<img src="https://github.com/renli1024/Modify_PDF/blob/master/example1.png" width = 50% height = 50%/>

### 添加书签功能
<img src="https://github.com/renli1024/Modify_PDF/blob/master/example2.png" width = 50% height = 50%/>

## 使用流程
1. 使用Maven或其他IDE导入需要的依赖。
2. 添加project.properties配置文件
3. 直接运行相关文件即可。
    - PageBackgrounds.java：修改pdf背景色；
    - BookMarks.java：为pdf添加标签。

注：project.properties文件中指定了代码的相关参数，具体如下：
```$xslt
# PageBackground.java 相关配置
orig_path = 原始pdf文件所在目录（可包含子目录、多个pdf文件）
colored_path = 转换颜色后的pdf输出目录

# BookMarks.java 相关配置
src = 原始pdf文件地址（单个pdf文件）
dest = 添加书签后的pdf输出地址
outline = pdf书签信息文件
offset = 目录页码和pdf文档页面间的偏差（通常文档页码=目录页码+偏差）
```

## 修改背景色功能
读入指定目录中包含的pdf文件，将其背景色转为为豆沙绿（护眼色），
并将转换颜色后的pdf文件输出到新目录中。

注：新目录的目录结构会和原目录保持一致。

## 添加书签
为pdf添加目录书签，可以方便在pdf内进行跳转查阅。

所添加的书签信息需要写到outline文件中，格式如下：
```aidl
一级目录1--目录页码
    二级目录1--目录页码
    二级目录2--目录页码
        三级目录1--目录页码
        三级目录2--目录页码
一级目录2-目录页码
```
其中二级目录前有1个tab（4个空格），三级目录前有2个tab（8个空格），
主要用来区分一/二/三级目录，视觉上也容易辨认；其次目录标题和目录页码间用双短杠 -- 分隔。


