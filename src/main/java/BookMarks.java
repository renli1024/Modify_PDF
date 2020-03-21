import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfOutline;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitRemoteGoToDestination;
import java.io.*;
import java.util.Properties;


public class BookMarks {

    public static void main(String args[]) throws IOException {
        // 读取配置文件中的地址信息
        Properties properties = new Properties();
        FileInputStream in = new FileInputStream("project.properties");
        properties.load(in);
        String src = properties.get("src").toString();
        String dest = properties.get("dest").toString();
        String outline = properties.get("outline").toString();
        // 设置书籍目录和pdf文档页码间的偏差
        int offset = Integer.parseInt(properties.get("offset").toString());

        in.close();

        new BookMarks().createPdf(src, dest, outline, offset);
    }

    public void createPdf(String src, String dest,
                          String outline, int offset) throws IOException {
        PdfDocument pdf = new PdfDocument(new PdfReader(src), new PdfWriter(dest));
        pdf.getCatalog().setPageMode(PdfName.UseOutlines);
        PdfOutline root = pdf.getOutlines(false);

        BufferedReader br = new BufferedReader(new FileReader(outline));
        String line;
        PdfOutline lastFirstClassTitle = null;
        PdfOutline lastSecondClassTitle = null;

        while ((line = br.readLine())!=null){
            String[] line_sp =
                    line.split("\\s{4}|--");  // 按空格分隔（>=4个空格为分隔符）

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

    }

}

