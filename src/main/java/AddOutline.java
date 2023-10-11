import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import java.io.*;
import java.util.Properties;


public class AddOutline {

    public static void main(String args[]) throws IOException {
        // 读取配置文件中的地址信息
        Properties properties = new Properties();
        FileInputStream in = new FileInputStream("project.properties");
        properties.load(in);
        String dir = properties.get("file_dir").toString();
        String src = dir + properties.get("bk_input").toString();
        String dst = dir + properties.get("bk_output").toString();
        String outline = dir + properties.get("outline").toString();
        in.close();

        new AddOutline().createPdf(src, dst, outline);
    }

    public void createPdf(String src, String dst, String outline) throws IOException {
        // 传入PdfWriter: stamping mode, 会先copy一个文件, 再在该文件上修改;
        PdfDocument doc = new PdfDocument(new PdfReader(src), new PdfWriter(dst));
        doc.getCatalog().setPageMode(PdfName.UseOutlines);

        com.itextpdf.kernel.pdf.PdfOutline root = doc.getOutlines(true);

        // 清除之前存在的目录
        while (!root.getAllChildren().isEmpty()) {
            root.getAllChildren().remove(0);
        }

        BufferedReader br = new BufferedReader(new FileReader(outline));
        String line;
        com.itextpdf.kernel.pdf.PdfOutline lastFirstClassTitle = null;
        com.itextpdf.kernel.pdf.PdfOutline lastSecondClassTitle = null;

        int offset = 0;
        int lineNum = 0;
        while ((line = br.readLine())!=null){
            // 按 tab(4空格) 和 -- split
            String[] lineSplit = line.split("\\s{4}|--");
            if (lineNum == 0){
                // 设置书籍目录和pdf文档页码间的偏差
                // 目录页码: 内容在目录中的页码, 文档页码: 内容在pdf中的页码
                // 通常两者会存在偏差, 即文档页码=目录页码+偏差
                assert lineSplit[0].equals("offset") : "第一行必须指定offset信息";
                offset = Integer.parseInt(lineSplit[1]);
                lineNum = lineNum + 1;
                continue;
            }
            if (lineSplit.length == 2){
                // 一级标题
                com.itextpdf.kernel.pdf.PdfOutline firstClassTitle = root.addOutline(lineSplit[0]);
                // 页数
                int destPageNum = Integer.parseInt(lineSplit[1]) + offset;
                PdfPage destPage = doc.getPage(destPageNum);
                // 添加跳转, 跳转时不改变页面缩放大小
                firstClassTitle.addDestination(PdfExplicitDestination.createXYZ(
                        destPage, -1, destPage.getPageSize().getHeight(), 0));
                firstClassTitle.setOpen(false);
                lastFirstClassTitle = firstClassTitle;
            }
            else if (lineSplit.length == 3){
                com.itextpdf.kernel.pdf.PdfOutline secondClassTitle = lastFirstClassTitle.addOutline(lineSplit[1]);
                // 页数
                int destPageNum = Integer.parseInt(lineSplit[2]) + offset;
                PdfPage destPage = doc.getPage(destPageNum);
                // 添加跳转
                secondClassTitle.addDestination(PdfExplicitDestination.createXYZ(
                        destPage, -1, destPage.getPageSize().getHeight(), 0));
                secondClassTitle.setOpen(false);
                lastSecondClassTitle = secondClassTitle;
            }
            else if (lineSplit.length == 4){
                com.itextpdf.kernel.pdf.PdfOutline thirdClassTitle = lastSecondClassTitle.addOutline(lineSplit[2]);
                // 页数
                int destPageNum = Integer.parseInt(lineSplit[3]) + offset;
                PdfPage destPage = doc.getPage(destPageNum);
                // 添加跳转
                thirdClassTitle.addDestination(PdfExplicitDestination.createXYZ(
                        destPage, -1, destPage.getPageSize().getHeight(), 0));
            }
            else {
                System.out.println("Error!");
                System.out.println("目录数据为：" + line);
                System.exit(1);
            }
            lineNum = lineNum + 1;
        }
        doc.close();
        String sep = File.separator;
        System.out.printf("Outline file: %s\n", outline.split(sep)[outline.split(sep).length-1]);
        System.out.printf("Input pdf: %s\n", src.split(sep)[src.split(sep).length-1]);
        System.out.printf("Outline added pdf: %s\n", dst.split(sep)[dst.split(sep).length-1]);
    }

}

