import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfOutline;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitRemoteGoToDestination;
import java.io.*;
import java.util.Properties;


public class BookMark {

    public static void main(String args[]) throws IOException {
        // 读取配置文件中的地址信息
        Properties properties = new Properties();
        FileInputStream in = new FileInputStream("project.properties");
        properties.load(in);
        String dir = properties.get("file_dir").toString();
        String src = dir + properties.get("bk_input").toString();
        String dst = dir + properties.get("bk_output").toString();
        String bookmarks = dir + properties.get("bookmark").toString();
        // 设置书籍目录和pdf文档页码间的偏差
        int offset = Integer.parseInt(properties.get("offset").toString());

        in.close();

        new BookMark().createPdf(src, dst, bookmarks, offset);
    }

    public void createPdf(String src, String dst,
                          String bookmarks, int offset) throws IOException {
        // 传入PdfWriter: stamping mode, 会先copy一个文件, 再在该文件上修改;
        PdfDocument pdf = new PdfDocument(new PdfReader(src), new PdfWriter(dst));
        pdf.getCatalog().setPageMode(PdfName.UseOutlines);

        PdfOutline root = pdf.getOutlines(true);

        // 清除之前存在的目录
        while (!root.getAllChildren().isEmpty()) {
            root.getAllChildren().remove(0);
        }

        BufferedReader br = new BufferedReader(new FileReader(bookmarks));
        String line;
        PdfOutline lastFirstClassTitle = null;
        PdfOutline lastSecondClassTitle = null;

        while ((line = br.readLine())!=null){
            String[] line_sp =
                    line.split("\\s{4}|--");  // 按tab(4空格)或--分隔

            if (line_sp.length == 2){
                // 一级标题
                PdfOutline firstClassTitle = root.addOutline(line_sp[0]);
                // 注意页数是从0开始算的，因此要减1
                // 跳转时不改变页面缩放大小，
                firstClassTitle.addDestination(PdfExplicitRemoteGoToDestination.
                        createXYZ(Integer.parseInt(line_sp[1]) - 1 + offset,
                                0, 0, 0));
                firstClassTitle.setOpen(false);
                lastFirstClassTitle = firstClassTitle;
            }
            else if (line_sp.length == 3){
                PdfOutline secondClassTitle = lastFirstClassTitle.addOutline(line_sp[1]);
                secondClassTitle.addDestination(PdfExplicitRemoteGoToDestination.
                        createXYZ(Integer.parseInt(line_sp[2]) - 1 + offset,
                                0, 0, 0));
                secondClassTitle.setOpen(false);
                lastSecondClassTitle = secondClassTitle;
            }
            else if (line_sp.length == 4){
                PdfOutline thirdClassTitle = lastSecondClassTitle.addOutline(line_sp[2]);
                thirdClassTitle.addDestination(PdfExplicitRemoteGoToDestination.
                        createXYZ(Integer.parseInt(line_sp[3]) - 1 + offset,
                                0, 0 ,0));
            }
            else {
                System.out.println("Error!");
                System.out.println("目录数据为：" + line);
                System.exit(1);
            }
        }
        pdf.close();
        String sep = File.separator;
        System.out.printf("Bookmark file: %s\n", bookmarks.split(sep)[bookmarks.split(sep).length-1]);
        System.out.printf("Input pdf: %s\n", src.split(sep)[src.split(sep).length-1]);
        System.out.printf("Output pdf with bookmarks: %s\n", dst.split(sep)[dst.split(sep).length-1]);
    }

}

