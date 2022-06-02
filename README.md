# Modify_PDF (background color, bookmarks, ...)
使用Java iText7库，对pdf文件进行修改优化，实现的功能有：
1. 将pdf背景色改为护眼色 (豆沙绿), 看白底论文眼睛容易疲劳.
2. 为pdf增加书签 / 目录, 便于在pdf中跳转查阅.
3. 合并多个pdf文件，主要为了合并论文的正文和附录 (如NeurIPS论文的正文和附录是分开的, 查阅起来不方便).
4. 分割pdf文件 (交论文有时需要正文和附录分开提交).

## 示例图片
### 修改背景色功能
<img src="https://github.com/renli1024/Modify_PDF/blob/master/example1.png" width = 50% height = 50%/>

### 添加书签功能
<img src="https://github.com/renli1024/Modify_PDF/blob/master/example2.png" width = 30% height = 30%/>

## 使用流程
1. 使用Maven导入jar包依赖 (也可手动下载, 配置信息在`pom.xml`文件中);
2. 填写`project.properties`配置文件中的参数;
3. 运行相关代码:
    - `PageBackgrounds.java`: 修改pdf背景色;
    - `BookMarks.java`: 为pdf添加标签;
    - `PdfMerge.java`: 合并多个pdf文件;
    - `PdfSplit.java`: 将单个文件分割为多个pdf文件;

## 修改背景色功能
读入指定目录中包含的pdf文件, 将其背景色转为为护眼色 (护眼色), 
并将转换颜色后的pdf文件输出到新目录中 (新目录的目录结构会和原目录保持一致).

`project.properties`中的相关设置: 
```
# 原始pdf文件所在目录（可包含子目录、多个pdf文件）
orig_path = <your_address>/Papers
# 转换颜色后的pdf输出目录
colored_path = <your_address>/cPapers
```

## 添加书签
为pdf添加目录书签, 方便在pdf内进行跳转查阅.

所添加的书签信息需要写到`book_marks.txt`文件中, 格式如下: 
```
一级目录a--目录页码
    二级目录a--目录页码
    二级目录b--目录页码
        三级目录a--目录页码
        三级目录b--目录页码
一级目录b--目录页码
```
其中二级目录前有1个tab (4个空格), 三级目录前有2个tab (8个空格), 
主要用来区分一/二/三级目录, 视觉上也容易辨认; 其次目录标题和目录页码间用双短杠 -- 分隔. 

`project.properties`中的相关设置: 
```
# 原始pdf文件地址（单个pdf文件）
src = <your_address>/input.pdf
# 添加书签后的pdf输出地址
dst = <your_address>/output.pdf
# 书签信息文件地址
bookmarks = <your_address>/bookmarks.txt
# 目录页码和pdf文档页码间的偏差
# 目录页码: 内容在书籍中的页码 (也是从原书目录中可复制到的页码), 文档页码: 内容在pdf中的页码
# 通常两者会存在偏差, 即文档页码=目录页码+偏差
offset = 4
```

## 合并文件
将多个pdf文件合并为一个.

`project.properties`中的相关设置: 
```
# 文件所在目录
file_dir: <your_address>/
# 需要合并的文件名, 按先后顺序指定, 用逗号+空格分隔
file_names: pdf_1, pdf_2
# 合并后的新文件名
new_name: pdf_merged
# 注: 上述名字不用加.pdf后缀, 代码会自动添加
```

## 分割文件
将一个pdf文件分割为多个.

`project.properties`中的相关设置: 
```
# 源文件地址
src_file: <your_address>/split.pdf
# 目标文件地址, 后缀会依次加 _1 / _2 ...
dst_files: <your_address>/split_%s.pdf
# 在哪一页划分
split_page: 2
```
