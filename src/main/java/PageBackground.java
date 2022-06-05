import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public class PageBackground {

    public static void main(String[] args)  throws IOException {

        // 读取配置文件中的地址信息
        Properties properties = new Properties();
        FileInputStream in = new FileInputStream("project.properties");
        properties.load(in);
        String orig_path = properties.get("orig_path").toString();
        String colored_path = properties.get("colored_path").toString();
        in.close();
        // 具体操作文件
        PageBackground pbClass = new PageBackground();
        pbClass.manipulateDir(orig_path, colored_path);

    }

    // 要保证这两个目录都是存在的
    private void manipulateDir(String origDirPath, String coloredDirPath){
        File origDir = new File(origDirPath);
        File[] origFiles = origDir.listFiles();

        for (File origFile : origFiles) {
            // 判断是文件还是目录
            if (origFile.isFile()) {
                // 是文件

                // 判断是否是pdf文件
                Boolean isPDF = checkPDF(origFile.toString());
                if (!isPDF) {
                    continue;
                }

                // 是pdf文件
                // 判断是否存在相应colored pdf文件
                String origFileName = origFile.toString();
                String noPathFileName = new File(origFileName).getName();
                String newColoredFilePath = coloredDirPath + "/" + noPathFileName;
                File newColoredFile = new File(newColoredFilePath);
                if (!newColoredFile.exists()) {
                    // 不存在相应PDF文件，复制pdf文件，并进行颜色转换
                    try {
                        manipulatePdf(origFileName, newColoredFilePath);
                        System.out.println("【New PDF file】: "+ newColoredFilePath);
                    }
                    catch (java.lang.Exception e){
                        e.printStackTrace();
                    }
                }

            } else {
                //是目录，递归

                //判断是否存在相应colored目录
                String origDirName = origFile.toString();
                String[] origPathParts = origDirName.split("/");
                String noPathFileName = origPathParts[origPathParts.length - 1];
                String newColoredDirPath = coloredDirPath + "/" + noPathFileName;
                File newColoredDir = new File(newColoredDirPath);
                if (!newColoredDir.exists()) {
                    // 不存在相应colored目录，新建一个
                    newColoredDir.mkdir();
                    System.out.println("【New Directory】: "+ newColoredDirPath);
                }

                // 递归操作子目录
                manipulateDir(origDirName, newColoredDirPath);
            }
        }
    }

    // 判断一个文件是否是pdf文件
    private Boolean checkPDF(String filePath) {
        boolean isPDF = false;
        try{
            PdfDocument doc = new PdfDocument(new PdfReader(filePath));
            isPDF = true;
            doc.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        // 捕获非pdf文件 (如`.DS_Store`文件) 异常
        catch (com.itextpdf.io.exceptions.IOException e){
//            System.out.println("Not PDF File: " + filePath);
        }

        return isPDF;
    }

    private void manipulatePdf(String src, String dest) throws Exception {
        PdfDocument srcDoc = new PdfDocument(new PdfReader(src).setUnethicalReading(true));
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
        pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, new PageBackgroundsEventHandler());
        srcDoc.copyPagesTo(1, srcDoc.getNumberOfPages(), pdfDoc);
        pdfDoc.close();

    }

    private static class PageBackgroundsEventHandler implements IEventHandler {
        @Override
        public void handleEvent(Event currentEvent) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) currentEvent;
            PdfDocument pdfDoc = docEvent.getDocument();
            PdfPage page = docEvent.getPage();

            PdfCanvas canvas = new PdfCanvas(page.newContentStreamBefore(),
                    page.getResources(), pdfDoc);
            Rectangle rect = page.getPageSize();
            canvas
                    .saveState()
                    .setFillColor(new DeviceRgb(171, 207, 177))
//                    .setFillColor(new DeviceRgb(178, 215, 183))
                    .rectangle(rect)
                    .fill()
                    .restoreState();

            canvas.release();

        }
    }
}
