# Modify_PDF (background color, bookmarks, ...)
使用Java [iText7](https://itextpdf.com/en/products/itext-7/itext-7-core) 库，对pdf文件进行修改优化，实现的功能有: 
1. 将pdf背景色改为护眼色 (豆沙绿), 白底论文看着太累眼了...
2. 为pdf增加目录/书签, 便于在pdf中跳转查阅.
3. 合并多个pdf文件，主要为了合并论文的正文和附录 (如NeurIPS论文的正文和附录是分开的, 查阅起来不方便).
4. 切分pdf文件 (交论文有时需要正文和附录分开提交).

## 示例图片
### 修改背景色功能
<img src="https://github.com/renli1024/Modify_PDF/blob/master/example1.png" width = 50% height = 50%/>

### 添加目录功能
<img src="https://github.com/renli1024/Modify_PDF/blob/master/example2.png" width = 30% height = 30%/>

## 使用流程
1. 使用Maven导入jar包依赖 (也可手动下载, 配置信息在`pom.xml`文件中);
    - 版本要求: [iText7](https://search.maven.org/search?q=g:com.itextpdf%20AND%20a:itext7-core&core=gav) >= 7.2.2
2. 填写`project.properties`配置文件中的参数;
3. 运行相关代码:
    - `PageBackgrounds.java`: 修改pdf背景色;
    - `AddOutline.java`: 为pdf添加标签;
    - `PdfMerge.java`: 将多个pdf文件合并为一个;
    - `PdfSplit.java`: 将一个pdf文件切分为多个;

## 修改背景色功能
读入指定目录中包含的pdf文件, 将其背景色转换为护眼色 (豆沙绿), 
并将转换颜色后的pdf文件输出到新目录中 (新目录的结构会和原目录保持一致).

在`project.properties`中指定相关参数: 
```bash
# 原始pdf文件所在目录（可包含子目录、多个pdf文件）
orig_path = <your_address>/Papers
# 转换颜色后的pdf输出目录
colored_path = <your_address>/cPapers
```

## 添加目录
为pdf添加目录, 方便在pdf内进行跳转查阅.

所添加的目录信息需要写到`outline.txt`文件中, 格式如下: 
```
offset--指定偏差数
一级目录a--目录页码
    二级目录a--目录页码
    二级目录b--目录页码
        三级目录a--目录页码
        三级目录b--目录页码
一级目录b--目录页码
```
其中二级目录前有1个tab (4个空格), 三级目录前有2个tab (8个空格), 
主要用来区分一/二/三级目录, 视觉上也容易辨认; 其次目录标题和目录页码间用双短杠 -- 分隔. 

- 关于offset偏差: 
  - 指目录页码和pdf文档页码间的偏差. 
  - 目录页码: 内容目录中的页, 文档页码: 内容在pdf中的实际页码; 
  - 通常两者会存在偏差, 即文档页码=目录页码+偏差;
  - 最好将offset信息直接写到目录文件中, 而非`.properties`文件, 这样更利于信息保存;

在`project.properties`中指定相关参数: 
```bash
# 原始pdf文件地址（单个pdf文件）
bk_input = input.pdf
# 添加书签后的pdf输出地址
bk_output = output2.pdf
# 目录信息文件地址
outline = outline.txt
```

## 合并文件
将多个pdf文件合并为一个.

在`project.properties`中指定相关参数: 
```bash
# 需要合并的文件名, 格式: 按先后顺序指定, 用逗号+空格分隔
merge_input: merge_1.pdf, merge_2.pdf
# 合并后的输出文件
merge_output: merged.pdf
```

## 切分文件
将一个pdf文件切分为多个.

在`project.properties`中指定相关参数: 
```bash
# 源文件地址
split_input: split.pdf
# 目标文件地址, 后缀会依次加 _1 / _2 ...
split_output: split_%s.pdf
# 在哪一页进行划分, 格式: 按先后顺序指用逗号+空格分隔
# 划分效果: 按结束页划分, 如指定"2, 4", 文档会被分为: 1~2, 3~4, 5~end三部分
split_pages: 2, 4
```
