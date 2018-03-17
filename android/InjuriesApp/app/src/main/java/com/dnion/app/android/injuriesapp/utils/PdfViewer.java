package com.dnion.app.android.injuriesapp.utils;

import android.content.Context;

import com.dnion.app.android.injuriesapp.R;
import com.dnion.app.android.injuriesapp.dao.PatientInfo;
import com.dnion.app.android.injuriesapp.dao.RecordInfo;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by 卫华 on 2018/3/15.
 */

public class PdfViewer {

    public static void createPdf(Context context, String dest, PatientInfo patientInfo, RecordInfo recordInfo) throws IOException, DocumentException {
        File file = new File(dest);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        //if (file.exists()) {
        //    file.delete();
        //}
        // 使用微软雅黑字体显示中文
        String fontName = context.getResources().getString(R.raw.msyh);
        fontName += ",1";
        BaseFont chineseFont = BaseFont.createFont(fontName, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);//中文简体

        createPdf(dest, chineseFont);

//        Document document = new Document();
//        PdfWriter.getInstance(document, new FileOutputStream(dest));
//        document.open();
//
//        Font chapterFont = new Font(BaseFont.createFont(yaHeiFontName, BaseFont.IDENTITY_H, BaseFont.EMBEDDED), 16);//中文简体
//        Font paragraphFont = new Font(BaseFont.createFont(yaHeiFontName, BaseFont.IDENTITY_H, BaseFont.EMBEDDED), 12);//中文简体

        //Font chapterFont = FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLDITALIC);
        //Font paragraphFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL);
       // Chunk chunk = new Chunk("This is the title", chapterFont);
//        Chapter chapter = new Chapter(new Paragraph(chunk), 1);
//        chapter.setNumberDepth(0);
//        chapter.add(new Paragraph("This is the paragraph", paragraphFont));
//        document.add(chapter);

//        Chunk chunk = new Chunk("这是文档标题", chapterFont);
//        Chapter chapter = new Chapter(new Paragraph(chunk), 1);
//        chapter.setNumberDepth(0);
//        chapter.add(new Paragraph("副标题", paragraphFont));
//        document.add(chapter);
//        document.close();


    }

    private static PdfPCell createCell(String title, String value, Font font) {
        PdfPCell cell = new PdfPCell(new Paragraph(title + ": " + value, font));
        cell.setPadding(5f);
        //cell.setFixedHeight(20);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);// 设置内容水平居中显示
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);  // 设置垂直居中
        return cell;
    }

    private static void createPdf(String pdfPath, BaseFont chineseFont) throws IOException, DocumentException {
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfPath));

        //设置pdf背景
        //PdfBackground event = new PdfBackground();
        //writer.setPageEvent(event);

        Font boldChinese = new Font(chineseFont, 26, Font.BOLD);
        Font titleChinese = new Font(chineseFont, 16);


        document.open();
        //------------开始写数据-------------------
        Paragraph title = new Paragraph("伤口检测报告", boldChinese);
        title.setAlignment(Element.ALIGN_CENTER); // 居中设置
        title.setSpacingBefore(10f);//设置行间距//设置上面空白宽度
        document.add(title);

        Paragraph patientTitle = new Paragraph("患者信息", titleChinese);
        patientTitle.setSpacingBefore(25f);//设置上面空白宽度
        document.add(patientTitle);

        float[] widths = { 35f, 35f, 30f };
        PdfPTable table = new PdfPTable(widths);// 建立一个pdf表格
        table.setSpacingBefore(20f);// 设置表格上面空白宽度
        //table.setTotalWidth(500);// 设置表格的宽度
        table.setWidthPercentage(100);//设置表格宽度为%100

        PdfPCell cell = createCell("患者ID", "55588990", titleChinese);
        table.addCell(cell);
        cell = createCell("年龄", "90", titleChinese);
        table.addCell(cell);
        cell = createCell("性别", "女", titleChinese);
        table.addCell(cell);

        cell = createCell("姓名", "测试员", titleChinese);
        table.addCell(cell);
        cell = createCell("出生日期", "2015/10/05", titleChinese);
        table.addCell(cell);
        cell = createCell("主治医师", "Dr 高", titleChinese);
        table.addCell(cell);

        document.add(table);
        /*
        title = new Paragraph("致：XXX公司", BoldChinese);// 抬头
        title.setSpacingBefore(25f);//设置上面空白宽度
        document.add(title);

        title = new Paragraph("         贵我双方签署的编号为 XXX有关起租条件已满足，现将租赁合同项下相关租赁要素明示如下：", FontChinese);
        title.setLeading(22f);//设置行间距
        document.add(title);

        float[] widths = { 10f,25f,30f,30f };// 设置表格的列宽和列数  默认是4列
        if(depositBean.isExpress()==5){ //如果是明示就是6列
            widths = new float[]{ 8f,15f,19f,19f,19f,20f };
        }else if(depositBean.isExpress()==6){   //如果是业发事业部就是7列
            widths = new float[]{ 8f,15f,15f,15f,15f,16f,16f };
        }

        PdfPTable table = new PdfPTable(widths);// 建立一个pdf表格
        table.setSpacingBefore(20f);// 设置表格上面空白宽度
        table.setTotalWidth(500);// 设置表格的宽度
        table.setWidthPercentage(100);//设置表格宽度为%100
        // table.getDefaultCell().setBorder(0);//设置表格默认为无边框

        String[] tempValue = new Stirng[4]{"1","2011-07-07","2222","11.11","11.11","3000","9999"};  //租金期次列表
        int rowCount=1; //行计数器
        PdfPCell cell = null;
        //---表头
        cell = new PdfPCell(new Paragraph("期次", subBoldFontChinese));//描述
        cell.setFixedHeight(20);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);// 设置内容水平居中显示
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);  // 设置垂直居中
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph("租金日", subBoldFontChinese));//描述
        cell.setFixedHeight(20);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);// 设置内容水平居中显示
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);  // 设置垂直居中
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph("各期租金金额", subBoldFontChinese));//描述
        cell.setFixedHeight(20);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);// 设置内容水平居中显示
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);  // 设置垂直居中
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph("各期租金后\n剩余租金", subBoldFontChinese));//描述
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);// 设置内容水平居中显示
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);  // 设置垂直居中
        cell.setFixedHeight(20);
        table.addCell(cell);


        for (int j = 1 ; j< tempValue.length; j++){
            if(j%argument==1){      //第一列 日期
                cell = new PdfPCell(new Paragraph(rowCount+"", moneyFontChinese));//描述
                cell.setFixedHeight(20);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);// 设置内容水平居中显示
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);  // 设置垂直居中
                table.addCell(cell);
                rowCount++;
            }
            cell = new PdfPCell(new Paragraph(tempValue[j], moneyFontChinese));//描述
            cell.setFixedHeight(20);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);// 设置内容水平居中显示
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);  // 设置垂直居中
            table.addCell(cell);
        }
        document.add(table);

        title = new Paragraph("                租金总额：XXX", FontChinese);
        title.setLeading(22f);//设置行间距
        document.add(title);
        title = new Paragraph("         特此通知！", FontChinese);
        title.setLeading(22f);//设置行间距
        document.add(title);
        //-------此处增加图片和日期，因为图片会遇到跨页的问题，图片跨页，图片下方的日期就会脱离图片下方会放到上一页。
        //所以必须用表格加以固定的技巧来实现
        float[] widthes = { 50f };// 设置表格的列宽和列数
        PdfPTable hiddenTable = new PdfPTable(widthes);// 建立一个pdf表格
        hiddenTable.setSpacingBefore(11f);  //设置表格上空间
        hiddenTable.setTotalWidth(500);// 设置表格的宽度
        hiddenTable.setWidthPercentage(100);//设置表格宽度为%100
        hiddenTable.getDefaultCell().disableBorderSide(1);
        hiddenTable.getDefaultCell().disableBorderSide(2);
        hiddenTable.getDefaultCell().disableBorderSide(4);
        hiddenTable.getDefaultCell().disableBorderSide(8);

        Image upgif = Image.getInstance("D:/opt/yd_apps/rim/uploadfolder/stamp1.jpg");
        upgif.scalePercent(7.5f);//设置缩放的百分比%7.5
        upgif.setAlignment(Element.ALIGN_RIGHT);

        cell = new PdfPCell(new Paragraph("", FontChinese));//描述
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);// 设置内容水平居中显示
        cell.addElement(upgif);
        cell.setPaddingTop(0f);             //设置内容靠上位置
        cell.setPaddingBottom(0f);
        cell.setPaddingRight(20f);
        cell.setBorder(Rectangle.NO_BORDER);//设置单元格无边框
        hiddenTable.addCell(cell);

        cell = new PdfPCell(new Paragraph("XX 年 XX 月 XX 日                    ", FontChinese));//金额
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);// 设置内容水平居中显示
        cell.setPaddingTop(0f);
        cell.setPaddingRight(20f);
        cell.setBorder(Rectangle.NO_BORDER);
        hiddenTable.addCell(cell);
        document.add(hiddenTable);
        */

        document.close();
    }

    class PdfBackground extends PdfPageEventHelper {
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            //设置pdf背景色为白色
            PdfContentByte canvas = writer.getDirectContentUnder();
            Rectangle rect = document.getPageSize();
            canvas.setColorFill(BaseColor.WHITE);
            canvas.rectangle(rect.getLeft(), rect.getBottom(), rect.getWidth(), rect.getHeight());
            canvas.fill();

            //设置pdf页面内间距
            PdfContentByte canvasBorder = writer.getDirectContent();
            Rectangle rectBorder = document.getPageSize();
            rectBorder.setBorder(Rectangle.BOX);
            rectBorder.setBorderWidth(200);
            rectBorder.setBorderColor(BaseColor.WHITE);
            rectBorder.setUseVariableBorders(true);
            canvasBorder.rectangle(rectBorder);
        }
    }
}
