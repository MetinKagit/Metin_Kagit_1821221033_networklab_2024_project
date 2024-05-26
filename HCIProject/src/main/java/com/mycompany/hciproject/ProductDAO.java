/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.hciproject;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.sf.dynamicreports.report.datasource.DRDataSource;
//import net.sf.dynamicreports.report.datasource.JRDataSource;
import net.sf.jasperreports.engine.JRDataSource;

public class ProductDAO {

    public static JRDataSource createDataSource(Connection connection) throws SQLException {
        DRDataSource dataSource = new DRDataSource("employee_id", "first_name", "job_id", "salary");

        
        String sql = "SELECT employee_id, first_name, job_id, salary FROM hr.employees limit 25";

        BigDecimal totalPrice = BigDecimal.ZERO; // Initialize total price

        try (PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int empId = resultSet.getInt("employee_id");
                String name = resultSet.getString("first_name");
                String jobId = resultSet.getString("job_id");
                BigDecimal salary = resultSet.getBigDecimal("salary");

                // Add data to the data source
                dataSource.add(empId, name, jobId, salary);

                // Increment total price by the price of the current item
                totalPrice = totalPrice.add(salary);
            }

            // Add "Total Fee:" row to the data source with total price in the price column
            dataSource.add(null, null, null, null);
            dataSource.add(null, null, null, null);
            dataSource.add(null, null, "Total Salary:", totalPrice);
        }

        return dataSource;
    }
    
    public static DRDataSource fetchChartData(Connection connection) throws SQLException {
    String sql = "SELECT job_id, SUM(salary) as total_salary FROM hr.employees GROUP BY job_id";
    DRDataSource chartDataSource = new DRDataSource("job_id", "total_salary");

    try (PreparedStatement statement = connection.prepareStatement(sql);
         ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
            String jobId = resultSet.getString("job_id");
            BigDecimal totalSalary = resultSet.getBigDecimal("total_salary");
            chartDataSource.add(jobId, totalSalary);
        }
    }
    
    return chartDataSource;
}
}

