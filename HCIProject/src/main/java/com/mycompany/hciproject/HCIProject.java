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
import java.awt.Font;
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
import net.sf.dynamicreports.report.builder.DynamicReports;
import static net.sf.dynamicreports.report.builder.DynamicReports.cht;
import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.col;
import static net.sf.dynamicreports.report.builder.DynamicReports.report;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;
import static net.sf.dynamicreports.report.builder.DynamicReports.template;
import static net.sf.dynamicreports.report.builder.DynamicReports.type;
import net.sf.dynamicreports.report.builder.chart.AxisFormatBuilder;
import net.sf.dynamicreports.report.builder.chart.BarChartBuilder;
import net.sf.dynamicreports.report.builder.chart.PieChartBuilder;
import net.sf.dynamicreports.report.builder.column.Columns;
import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.Components;
import net.sf.dynamicreports.report.builder.datatype.DataTypes;
import net.sf.dynamicreports.report.builder.style.StyleBuilders;
import net.sf.dynamicreports.report.builder.style.Styles;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.constant.HorizontalImageAlignment;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.constant.PageType;
import net.sf.dynamicreports.report.constant.Rotation;
import net.sf.dynamicreports.report.definition.ReportParameters;
import net.sf.jasperreports.engine.JRDataSource;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;

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
                    .pageHeader(createPageHeader())
                    .setColumnStyle(stl.templateStyle("columnStyle"))
                    .setColumnTitleStyle(stl.templateStyle("columnTitleStyle"))
                    .columns(productIdColumn, productNameColumn, pieceColumn, priceColumn)
                    .setPageFormat(PageType.A4, PageOrientation.PORTRAIT)
                    .summary(generateChart(connection))
                    .setDataSource(ProductDAO.createDataSource(connection));

            report.show();
        } catch (SQLException | DRException e) {
            e.printStackTrace();
        }
    }
    
    private ComponentBuilder<?, ?> createPageHeader() {
    // Create a custom style for the title
    StyleBuilder titleStyle = stl.style()
            .setFontSize(22)
            .bold()
            .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER) // Align left to be next to the logo
            .setVerticalTextAlignment(VerticalTextAlignment.MIDDLE);
//            .setForegroundColor(); // Example color

    // Create a style for the logo
    StyleBuilder logoStyle = stl.style()
            .setHorizontalImageAlignment(HorizontalImageAlignment.LEFT)
            .setVerticalTextAlignment(VerticalTextAlignment.MIDDLE);

    return Components.verticalList(
            Components.verticalGap(20),
            Components.horizontalList(
                    
                    Components.horizontalGap(40),
                    Components.image("/Users/metinkagit/Downloads/FSM/EN/logo.png")
                            .setFixedDimension(50, 50) // Set fixed size for the logo
                            .setStyle(logoStyle),
//                    Components.horizontalGap(50),
                    Components.text("Employees Salary")
                            .setStyle(titleStyle)
                            
            ),
            Components.verticalGap(40) // Adding a vertical gap of 20 units
    );
}
    
   

//    private ComponentBuilder<?, ?> createChart(Connection connection) throws SQLException {
//        String sql = "SELECT job_id, SUM(salary) as total_salary FROM hr.employees GROUP BY job_id";
//        DRDataSource chartDataSource = new DRDataSource("job_id", "total_salary");
//
//        try (PreparedStatement statement = connection.prepareStatement(sql);
//             ResultSet resultSet = statement.executeQuery()) {
//            while (resultSet.next()) {
//                chartDataSource.add(resultSet.getString("job_id"), resultSet.getBigDecimal("total_salary"));
//            }
//        }
//
//        // ----- Create the bar chart -----
//        BarChartBuilder barChart = DynamicReports.cht.barChart()
//                .setTitle("Total Salary by Job ID")
////                .setStyle(StyleBuilders.style().bold())  // Apply style to the title
//                .setCategory(DynamicReports.col.column("Job ID", "job_id", DynamicReports.type.stringType()))
//                .series(DynamicReports.cht.serie(DynamicReports.col.column("Total Salary", "total_salary", DynamicReports.type.bigDecimalType())));
////                .setHorizontalAlignment(HorizontalAlignment.CENTER);
//
//        // Customize Chart Area Style
////        barChart.setChartStyle(StyleBuilders.style()
////                .setBorder(StyleBuilders.pen().setStyle(StyleBuilders.pen1Point())) // Call pen1Point() on an instance
////                .setPadding(20)
//        return barChart.setDataSource(chartDataSource);
//    }
    
  

private ComponentBuilder<?, ?> createChart(DRDataSource chartDataSource) {
    PieChartBuilder pieChart = DynamicReports.cht.pieChart()                
            .setTitle("Total Salary paid by Job ID")
            .setKey(DynamicReports.col.column("Job ID", "job_id", DynamicReports.type.stringType()))
            .series(DynamicReports.cht.serie(DynamicReports.col.column("Total Salary", "total_salary", DynamicReports.type.bigDecimalType())));

    return pieChart.setDataSource(chartDataSource);
}

public ComponentBuilder<?, ?> generateChart(Connection connection) throws SQLException {
    DRDataSource chartDataSource = ProductDAO.fetchChartData(connection);
    return createChart(chartDataSource);
}






    
     public static void main(String[] args) {
        new HCIProject();
    }
}
   
