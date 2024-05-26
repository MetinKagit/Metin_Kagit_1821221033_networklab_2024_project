/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.hciproject;

//import static net.sf.dynamicreports.report.builder.DynamicReports;
//import net.sf.dynamicreports.examples.Templates;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.ReportTemplateBuilder;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.VerticalTextAlignment;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.dynamicreports.report.exception.DRException;

import java.awt.Color;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import javax.xml.transform.Templates;
import net.sf.dynamicreports.report.base.expression.AbstractSimpleExpression;
import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.col;
import static net.sf.dynamicreports.report.builder.DynamicReports.report;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;
import static net.sf.dynamicreports.report.builder.DynamicReports.template;
import static net.sf.dynamicreports.report.builder.DynamicReports.type;
import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.Components;
import net.sf.dynamicreports.report.constant.HorizontalImageAlignment;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.constant.PageType;
import net.sf.dynamicreports.report.definition.ReportParameters;
import net.sf.jasperreports.engine.JRDataSource;

public class HCIProject {

    private final static String DB_URL = "jdbc:mysql://localhost:4545/hr";
    private final static String DB_USER = "root";
    private final static String DB_PASSWORD = "121212";

    public HCIProject() {
        build();
    }

    private void build() {
        StyleBuilder style1 = stl.style()
                .setName("style1")
                .setVerticalTextAlignment(VerticalTextAlignment.MIDDLE)
                .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

        StyleBuilder style2 = stl.style(style1)
                .setName("style2")
                .setVerticalTextAlignment(VerticalTextAlignment.MIDDLE)
                .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER)
                .italic();
        StyleBuilder columnStyle = stl.style()
                .setName("columnStyle")
                .setVerticalTextAlignment(VerticalTextAlignment.MIDDLE)
                .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER)
                .bold();
        StyleBuilder columnTitleStyle = stl.style()
                .setName("columnTitleStyle")
                .setBorder(stl.pen1Point())
                //                .setHorizontalTextAlignment(HorizontalAlignment.CENTER)
                .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER)
                .setBackgroundColor(Color.LIGHT_GRAY);
        ReportTemplateBuilder template = template()
                .templateStyles(style1, style2, columnStyle, columnTitleStyle);

        TextColumnBuilder<Integer> productIdColumn = col.column("Employee ID", "employee_id", type.integerType())
                .setStyle(stl.templateStyle("style1"));
        TextColumnBuilder<String> productNameColumn = col.column("First Name", "first_name", type.stringType())
                .setStyle(stl.templateStyle("style2"));
        TextColumnBuilder<String> pieceColumn = col.column("Job ID", "job_id", type.stringType())
                .setStyle(stl.templateStyle("style2"));
        TextColumnBuilder<BigDecimal> priceColumn = col.column("Salary", "salary", type.bigDecimalType());

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            JasperReportBuilder report = report()
                    .setTemplate(template)
                    .setColumnStyle(stl.templateStyle("columnStyle"))
                    .setColumnTitleStyle(stl.templateStyle("columnTitleStyle"))
                    .columns(productIdColumn, productNameColumn, pieceColumn, priceColumn)
                    .pageHeader(createPageHeader())
                    .setPageFormat(PageType.A4, PageOrientation.PORTRAIT)
                    .setDataSource(ProductDAO.createDataSource(connection));

            report.show();
        } catch (SQLException | DRException e) {
            e.printStackTrace();
        }
    }
    private ComponentBuilder<?, ?> createPageHeader() {
        return Components.horizontalList()
                .add(Components.image("/Users/metinkagit/Downloads/FSM/EN/logo.png")
                        .setHorizontalImageAlignment(HorizontalImageAlignment.LEFT))
                .newRow()
                .add(Components.text("Employees Salary")
                        .setStyle(stl.style()
                                .setFontSize(18)
                                .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER))).newRow()
                ;
    }
    
     public static void main(String[] args) {
        new HCIProject();
    }
}
   
