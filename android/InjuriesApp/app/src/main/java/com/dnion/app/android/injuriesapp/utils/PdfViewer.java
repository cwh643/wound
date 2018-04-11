package com.dnion.app.android.injuriesapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.format.DateFormat;
import android.util.Log;

import com.dnion.app.android.injuriesapp.ArchivesData;
import com.dnion.app.android.injuriesapp.MainActivity;
import com.dnion.app.android.injuriesapp.R;
import com.dnion.app.android.injuriesapp.dao.DeepCameraInfoDao;
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
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.CMYKColor;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImage;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfIndirectObject;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by 卫华 on 2018/3/15.
 */

public class PdfViewer {

    public static void createPdf(MainActivity context, String dest, PatientInfo patientInfo, RecordInfo recordInfo) throws IOException, DocumentException {
        File file = new File(dest);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        // 使用微软雅黑字体显示中文
        String fontName = context.getResources().getString(R.raw.msyh);
        fontName += ",1";
        BaseFont chineseFont = BaseFont.createFont(fontName, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);//中文简体

        //createPdf(context, dest, chineseFont, patientInfo, recordInfo);
        createPdf(context, dest, chineseFont, patientInfo, recordInfo);

    }

    private static void createPdf(MainActivity context, String pdfPath, BaseFont chineseFont, PatientInfo patientInfo, RecordInfo recordInfo){
        try {
            FileOutputStream out = new FileOutputStream(pdfPath);// 输出流
            PdfReader reader = new PdfReader(context.getResources().openRawResource(R.raw.report_model));// 读取pdf模板
            PdfStamper stamper = new PdfStamper(reader, out);
            try {
                AcroFields form = stamper.getAcroFields();
                addText(form, "patient_id", chineseFont, patientInfo.getInpatientNo());
                addText(form, "patient_age", chineseFont, patientInfo.getAge());
                int sex = patientInfo.getSex();
                String sexStr = context.getString(R.string.base_info_female);
                if (1 == sex) {
                    sexStr = context.getString(R.string.base_info_male);
                }
                addText(form, "patient_sex", chineseFont, sexStr);
                addText(form, "patient_name", chineseFont, patientInfo.getName());
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                addText(form, "patient_birthday", chineseFont, (patientInfo.getBirthday() == null ? "" : df.format(patientInfo.getBirthday())));
                addText(form, "doctor", chineseFont, patientInfo.getDoctor());
                addText(form, "exam_date", chineseFont, recordInfo.getRecordTime());
                addText(form, "exam_no", chineseFont, "10");
                Integer woundType = recordInfo.getWoundType();

                String woundName = "";
                if (CommonUtil.isEn(context)) {
                    woundName = CommonUtil.getDictName(ArchivesData.typeEnDict, woundType);
                } else {
                    woundName = CommonUtil.getDictName(ArchivesData.typeDict, woundType);
                }
                addText(form, "wound_type", chineseFont, woundName);
                addText(form, "wound_date", chineseFont, recordInfo.getWoundTime());
                addText(form, "wound_position", chineseFont, recordInfo.getWoundPositionDesc());

                addTable(stamper, form, "wound_table", chineseFont, recordInfo);

                addDraw(stamper, form, "wound_bar", recordInfo);
                String deepPath = context.getPdfDeepImagePath("" + recordInfo.getId());
                if (deepPath != null && deepPath.length() > 0) {
                    addImage(stamper, form, "wound_deep", deepPath);
                }
                String irPath = context.getPdfIrImagePath("" + recordInfo.getId());
                if (irPath != null && irPath.length() > 0) {
                    addImage(stamper, form, "wound_ir", irPath);
                }
                String rgbPath = context.getPdfRgbImagePath("" + recordInfo.getId());
                if (rgbPath != null && rgbPath.length() > 0) {
                    addImage(stamper, form, "wound_rgb", rgbPath);

                }



            } finally {
                stamper.setFormFlattening(true);// 如果为false那么生成的PDF文件还能编辑，一定要设为true
                stamper.close();
                //copy.close();
                //out.close();
                reader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addTable(PdfStamper stamper, AcroFields form, String fieldName, BaseFont baseFont, RecordInfo recordInfo) {
        int pageNo = form.getFieldPositions(fieldName).get(0).page;
        Rectangle signRect = form.getFieldPositions(fieldName).get(0).position;
        PdfContentByte canvas = stamper.getOverContent(pageNo);

        Font titleChinese = new Font(baseFont, 10);
        //PdfPTable table = new PdfPTable(new float[] { 24f, 10f, 10f, 10f, 10f, 12f, 12f, 12f });// 建立一个pdf表格
        PdfPTable table = new PdfPTable(new float[] { 10f, 10f, 10f, 10f, 12f, 12f, 12f });
        float totalWidth = signRect.getRight() - signRect.getLeft() - 1;
        table.setTotalWidth(totalWidth);

        //PdfPCell cell = createCell("时间", titleChinese);
        //table.addCell(cell);
        PdfPCell cell = createCell("长度", titleChinese);
        table.addCell(cell);
        cell = createCell("宽度", titleChinese);
        table.addCell(cell);
        cell = createCell("面积", titleChinese);
        table.addCell(cell);
        cell = createCell("深度", titleChinese);
        table.addCell(cell);
        cell = createCell("红色组织", titleChinese);
        table.addCell(cell);
        cell = createCell("黄色组织", titleChinese);
        table.addCell(cell);
        cell = createCell("黑色组织", titleChinese);
        table.addCell(cell);

        //cell = createCell(recordInfo.getRecordTime(), titleChinese);
        //table.addCell(cell);
        cell = createCell("" + formatFloatValue(recordInfo.getWoundHeight()), titleChinese);
        table.addCell(cell);
        cell = createCell("" + formatFloatValue(recordInfo.getWoundWidth()), titleChinese);
        table.addCell(cell);
        cell = createCell("" + formatFloatValue(recordInfo.getWoundArea()), titleChinese);
        table.addCell(cell);
        cell = createCell("" + formatFloatValue(recordInfo.getWoundDeep()), titleChinese);
        table.addCell(cell);
        cell = createCell("" + formatFloatValue(recordInfo.getWoundColorRed()), titleChinese);
        table.addCell(cell);
        cell = createCell("" + formatFloatValue(recordInfo.getWoundColorYellow()), titleChinese);
        table.addCell(cell);
        cell = createCell("" + formatFloatValue(recordInfo.getWoundColorBlack()), titleChinese);
        table.addCell(cell);

        table.writeSelectedRows(0, -1, signRect.getLeft(), signRect.getTop(), canvas);
    }

    private static void addText(AcroFields form, String fieldName, BaseFont baseFont, String txt) {
        try {
            form.setFieldProperty(fieldName, "textfont", baseFont, null);
            form.setField(fieldName, txt);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private static void addDraw(PdfStamper stamper, AcroFields form, String fieldName, RecordInfo recordInfo) {
        try {
            // 通过域名获取所在页和坐标，左下角为起点
            int pageNo = form.getFieldPositions(fieldName).get(0).page;
            Rectangle signRect = form.getFieldPositions(fieldName).get(0).position;
            float x = signRect.getLeft();
            float y = signRect.getBottom();
            float width = signRect.getWidth();
            float height = signRect.getHeight();

            float redValue = (recordInfo.getWoundColorRed() == null ? 0 : recordInfo.getWoundColorRed());
            float yellowValue = (recordInfo.getWoundColorYellow() == null ? 0 : recordInfo.getWoundColorYellow());
            float blackValue = (recordInfo.getWoundColorBlack() == null ? 0 : recordInfo.getWoundColorBlack());
            float maxValue = Math.max(redValue, Math.max(yellowValue, blackValue));

            PdfContentByte canvas = stamper.getOverContent(pageNo);
            if (maxValue > 0) {
                canvas.saveState();
                float barWidth = width / 5 ;
                float barHeight = (redValue / maxValue) * (height * 0.8f);
                float barX = x + barWidth / 2 ;
                float barY = y ;
                Rectangle redRect = new Rectangle(barX, barY, barX + barWidth, barY + barHeight);
                redRect.setBackgroundColor(BaseColor.RED);
                canvas.rectangle(redRect);

                barX = barX + barWidth + barWidth / 2;
                barHeight = (yellowValue / maxValue) * (height * 0.8f);
                Rectangle yellowRect = new Rectangle(barX, barY, barX + barWidth, barY + barHeight);
                yellowRect.setBackgroundColor(BaseColor.YELLOW);
                canvas.rectangle(yellowRect);

                barX = barX + barWidth + barWidth / 2;
                barHeight = (blackValue / maxValue) * (height * 0.8f);
                Rectangle blackRect = new Rectangle(barX, barY, barX + barWidth, barY + barHeight);
                blackRect.setBackgroundColor(BaseColor.BLACK);
                canvas.rectangle(blackRect);
                canvas.restoreState();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addImage(PdfStamper stamper, AcroFields form, String fieldName, String imagePath) {
        try {
            // 通过域名获取所在页和坐标，左下角为起点
            int pageNo = form.getFieldPositions(fieldName).get(0).page;
            Rectangle signRect = form.getFieldPositions(fieldName).get(0).position;
            float x = signRect.getLeft();
            float y = signRect.getBottom();

            // 读图片
            Image image = Image.getInstance(imagePath);
            // 获取操作的页面
            PdfContentByte under = stamper.getOverContent(pageNo);
            // 根据域的大小缩放图片
            image.scaleToFit(signRect.getWidth(), signRect.getHeight());
            // 添加图片
            x = x + (signRect.getWidth() - image.getWidth()) / 2;
            y = y + (signRect.getHeight() - image.getHeight()) / 2;
            image.setAbsolutePosition(x, y);
            under.addImage(image);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private static PdfPCell createCell(String value, Font font) {
        PdfPCell cell = new PdfPCell(new Paragraph(value, font));
        cell.setPadding(5f);
        //cell.setFixedHeight(20);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);// 设置内容水平居中显示
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);  // 设置垂直居中
        return cell;
    }

    private static PdfPCell createCell(String title, String value, Font font) {
        PdfPCell cell = new PdfPCell(new Paragraph(title + ": " + value, font));
        cell.setPadding(5f);
        //cell.setFixedHeight(20);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);// 设置内容水平居中显示
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);  // 设置垂直居中
        return cell;
    }

    private static void createPdfOld(MainActivity context, String pdfPath, BaseFont chineseFont, PatientInfo patientInfo, RecordInfo recordInfo) throws IOException, DocumentException {
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

        //float[] widths = { 35f, 35f, 30f };
        PdfPTable table = new PdfPTable(new float[] { 35f, 35f, 30f });// 建立一个pdf表格
        table.setSpacingBefore(20f);// 设置表格上面空白宽度
        //table.setTotalWidth(500);// 设置表格的宽度
        table.setWidthPercentage(100);//设置表格宽度为%100

        PdfPCell cell = createCell("患者ID", patientInfo.getInpatientNo(), titleChinese);
        table.addCell(cell);
        cell = createCell("年龄", patientInfo.getAge(), titleChinese);
        table.addCell(cell);

        int sex = patientInfo.getSex();
        String sexStr = context.getString(R.string.base_info_female);
        if (1 == sex) {
            sexStr = context.getString(R.string.base_info_male);
        }
        cell = createCell("性别", sexStr, titleChinese);
        table.addCell(cell);

        cell = createCell("姓名", patientInfo.getName(), titleChinese);
        table.addCell(cell);
        cell = createCell("出生日期", "", titleChinese);//DateFormat.format("yyyy-MM-dd", calendar)
        table.addCell(cell);
        cell = createCell("主治医师", patientInfo.getDoctor(), titleChinese);
        table.addCell(cell);

        document.add(table);


        patientTitle = new Paragraph("检测结果", titleChinese);
        patientTitle.setSpacingBefore(25f);//设置上面空白宽度
        document.add(patientTitle);


        table = new PdfPTable(new float[] { 35f, 35f, 30f });// 建立一个pdf表格
        table.setSpacingBefore(20f);// 设置表格上面空白宽度
        //table.setTotalWidth(500);// 设置表格的宽度
        table.setWidthPercentage(100);//设置表格宽度为%100


        cell = createCell("检测日期", recordInfo.getRecordTime(), titleChinese);
        table.addCell(cell);
        cell = createCell("检测编号", "90", titleChinese);
        table.addCell(cell);

        Integer woundType = recordInfo.getWoundType();
        String woundName = "";
        if (CommonUtil.isEn(context)) {
            woundName = CommonUtil.getDictName(ArchivesData.typeEnDict, woundType);
        } else {
            woundName = CommonUtil.getDictName(ArchivesData.typeDict, woundType);
        }
        cell = createCell("伤口类型", woundName, titleChinese);
        table.addCell(cell);

        cell = createCell("形成时间", recordInfo.getWoundTime(), titleChinese);
        table.addCell(cell);
        cell = createCell("伤口位置", recordInfo.getWoundPositionDesc(), titleChinese);
        cell.setColspan(2);
        table.addCell(cell);

        document.add(table);

        //画图
        Rectangle pageSize = PageSize.LETTER;
        float space = 30;
        float width = (pageSize.getWidth() / 2) - (3 * space);
        float height = width;
        float x = pageSize.getLeft() + space;
        float y = pageSize.getTop() - 300 - height;

        PdfContentByte canvas = writer.getDirectContent();

        //canvas.saveState();
        //canvas.setColorStroke(new GrayColor(0.2f));
        //canvas.setColorFill(new GrayColor(0.9f));
        //canvas.circle(x, y, 5);
        //canvas.restoreState();

        canvas.saveState();
        Rectangle rect = new Rectangle(x, y, x + width, y + height);
        rect.setBackgroundColor(new GrayColor(0.9f));
        canvas.rectangle(rect);
        canvas.restoreState();

        float redValue = (recordInfo.getWoundColorRed() == null ? 0 : recordInfo.getWoundColorRed());
        float yellowValue = (recordInfo.getWoundColorYellow() == null ? 0 : recordInfo.getWoundColorYellow());
        float blackValue = (recordInfo.getWoundColorBlack() == null ? 0 : recordInfo.getWoundColorBlack());
        float maxValue = Math.max(redValue, Math.max(yellowValue, blackValue));

        if (maxValue > 0) {
            canvas.saveState();
            float barWidth = width / 5 ;
            float barHeight = (redValue / maxValue) * (height * 0.8f);
            float barX = x + barWidth / 2 ;
            float barY = y ;
            Rectangle redRect = new Rectangle(barX, barY, barX + barWidth, barY + barHeight);
            redRect.setBackgroundColor(BaseColor.RED);
            canvas.rectangle(redRect);

            barX = barX + barWidth + barWidth / 2;
            barHeight = (yellowValue / maxValue) * (height * 0.8f);
            Rectangle yellowRect = new Rectangle(barX, barY, barX + barWidth, barY + barHeight);
            yellowRect.setBackgroundColor(BaseColor.YELLOW);
            canvas.rectangle(yellowRect);

            barX = barX + barWidth + barWidth / 2;
            barHeight = (blackValue / maxValue) * (height * 0.8f);
            Rectangle blackRect = new Rectangle(barX, barY, barX + barWidth, barY + barHeight);
            blackRect.setBackgroundColor(BaseColor.BLACK);
            canvas.rectangle(blackRect);
            canvas.restoreState();
        }


        //RGB
        String path = context.getPdfRgbImagePath("" + recordInfo.getId());
        if (path != null && path.length() > 0) {
            Image img = Image.getInstance(path);//选择图片
            //height = (int)(img.getHeight() * width / img.getWidth());
            //img.scaleAbsolute(width,height);//控制图片大小
            img.scaleAbsolute(width,height);
            img.setAbsolutePosition(x + width + space,y);//控制图片位置
            document.add(img);
        }


        //深度
        path = context.getPdfDeepImagePath("" + recordInfo.getId());
        if (path != null && path.length() > 0) {
            Image img = Image.getInstance(path);//选择图片
            //height = (int)(img.getHeight() * width / img.getWidth());
            //img.scaleAbsolute(width,height);//控制图片大小
            img.scaleAbsolute(width,height);
            img.setAbsolutePosition(x,y - height - space);//控制图片位置
            document.add(img);
        }

        //深度
        path = context.getPdfIrImagePath("" + recordInfo.getId());
        if (path != null && path.length() > 0) {
            Image img = Image.getInstance(path);//选择图片
            //img.scaleAbsolute(100,100);//控制图片大小
            img.scaleAbsolute(width,height);
            img.setAbsolutePosition(x + width + space,y - height - space);//控制图片位置
            document.add(img);
        }

        document.newPage();
        table = new PdfPTable(new float[] { 30f, 10f, 10f, 10f, 10f, 10f, 10f, 10f });// 建立一个pdf表格
        table.setSpacingBefore(20f);// 设置表格上面空白宽度
        //table.setTotalWidth(500);// 设置表格的宽度
        table.setWidthPercentage(100);//设置表格宽度为%100

        cell = createCell("时间", titleChinese);
        table.addCell(cell);
        cell = createCell("长度", titleChinese);
        table.addCell(cell);
        cell = createCell("宽度", titleChinese);
        table.addCell(cell);
        cell = createCell("面积", titleChinese);
        table.addCell(cell);
        cell = createCell("深度", titleChinese);
        table.addCell(cell);
        cell = createCell("红色组织", titleChinese);
        table.addCell(cell);
        cell = createCell("黄色组织", titleChinese);
        table.addCell(cell);
        cell = createCell("黑色组织", titleChinese);
        table.addCell(cell);

        cell = createCell(recordInfo.getRecordTime(), titleChinese);
        table.addCell(cell);
        cell = createCell("" + formatFloatValue(recordInfo.getWoundHeight()), titleChinese);
        table.addCell(cell);
        cell = createCell("" + formatFloatValue(recordInfo.getWoundWidth()), titleChinese);
        table.addCell(cell);
        cell = createCell("" + formatFloatValue(recordInfo.getWoundArea()), titleChinese);
        table.addCell(cell);
        cell = createCell("" + formatFloatValue(recordInfo.getWoundDeep()), titleChinese);
        table.addCell(cell);
        cell = createCell("" + formatFloatValue(recordInfo.getWoundColorRed()), titleChinese);
        table.addCell(cell);
        cell = createCell("" + formatFloatValue(recordInfo.getWoundColorYellow()), titleChinese);
        table.addCell(cell);
        cell = createCell("" + formatFloatValue(recordInfo.getWoundColorBlack()), titleChinese);
        table.addCell(cell);

        document.add(table);

        patientTitle = new Paragraph("注释", titleChinese);
        patientTitle.setSpacingBefore(20f);//设置上面空白宽度
        document.add(patientTitle);

        patientTitle = new Paragraph("科室：创伤科    医生签名：", titleChinese);
        patientTitle.setSpacingBefore(25f);//设置上面空白宽度
        patientTitle.setAlignment(Paragraph.ALIGN_RIGHT);
        document.add(patientTitle);

        document.close();
    }

    private static float formatFloatValue(Float value) {
        if (value == null) {
            return 0;
        }
        return value.floatValue();
    }

    private void demo() {
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
