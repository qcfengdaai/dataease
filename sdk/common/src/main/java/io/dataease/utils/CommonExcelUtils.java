package io.dataease.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import io.dataease.model.excel.AutoAdaptWidthStyleStrategy;
import io.dataease.model.excel.ErrWriteHandler;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.IndexedColors;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * Excel通用工具类
 * <p>
 * 基于阿里巴巴EasyExcel框架封装的Excel导入导出工具类。
 * 提供简单易用的API进行Excel文件的读写操作，支持自定义样式、错误标记、自动列宽调整等功能。
 * </p>
 *
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li>Excel导出 - 将Java对象列表导出为Excel文件</li>
 *   <li>Excel导入 - 解析Excel文件为Java对象</li>
 *   <li>样式自定义 - 支持自定义单元格样式、字体等</li>
 *   <li>错误标记 - 支持在导出时标记错误数据行</li>
 *   <li>自动列宽 - 根据内容自动调整列宽</li>
 *   <li>HTTP响应 - 直接输出到HTTP响应流</li>
 * </ul>
 *
 * <p><b>技术栈：</b></p>
 * <ul>
 *   <li>EasyExcel - 阿里巴巴开源的Excel处理框架</li>
 *   <li>Apache POI - 底层Excel文件操作库</li>
 *   <li>支持xlsx格式（Excel 2007+）</li>
 * </ul>
 *
 * <p><b>使用位置：</b></p>
 * <ul>
 *   <li>数据导出 - 报表导出、数据备份等场景</li>
 *   <li>数据导入 - 批量导入用户、数据等场景</li>
 *   <li>数据校验 - 导入时校验数据并标记错误</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 示例1：导出Excel到HTTP响应（最常用）
 * {@literal @}RestController
 * public class ExportController {
 *     {@literal @}GetMapping("/export/users")
 *     public void exportUsers(HttpServletResponse response) throws IOException {
 *         // 查询数据
 *         List&lt;UserDTO&gt; users = userService.getAllUsers();
 *
 *         // 导出Excel
 *         CommonExcelUtils.writeExcel(
 *             response,           // HTTP响应对象
 *             users,             // 要导出的数据列表
 *             UserDTO.class,     // 数据对象类型
 *             null,              // 错误信息列表（无错误传null）
 *             "用户列表"          // 文件名（不含扩展名）
 *         );
 *     }
 * }
 *
 * // 示例2：导出带错误标记的Excel
 * List&lt;DataDTO&gt; dataList = processData();
 * List&lt;Map&lt;Integer, String&gt;&gt; errors = new ArrayList&lt;&gt;();
 *
 * // 标记第2行有错误
 * Map&lt;Integer, String&gt; errorRow = new HashMap&lt;&gt;();
 * errorRow.put(0, "姓名不能为空");  // 第0列的错误信息
 * errorRow.put(1, "邮箱格式错误");  // 第1列的错误信息
 * errors.add(errorRow);
 *
 * // 导出（错误行会被标记为红色）
 * CommonExcelUtils.writeExcel(response, dataList, DataDTO.class, errors, "数据校验结果");
 *
 * // 示例3：导出到输出流（用于非HTTP场景）
 * FileOutputStream fos = new FileOutputStream("/data/export.xlsx");
 * CommonExcelUtils.writeOutputStream(
 *     fos,               // 输出流
 *     UserDTO.class,     // 数据类型
 *     userList,          // 数据列表
 *     null,              // 错误信息
 *     "Sheet1"           // Sheet名称
 * );
 * fos.close();
 *
 * // 示例4：导入Excel文件
 * {@literal @}PostMapping("/import/users")
 * public Result importUsers(MultipartFile file) throws Exception {
 *     InputStream inputStream = file.getInputStream();
 *
 *     // 创建监听器处理每一行数据
 *     AnalysisEventListener&lt;UserDTO&gt; listener = new AnalysisEventListener&lt;UserDTO&gt;() {
 *         {@literal @}Override
 *         public void invoke(UserDTO data, AnalysisContext context) {
 *             // 处理每一行数据
 *             userService.save(data);
 *         }
 *
 *         {@literal @}Override
 *         public void doAfterAllAnalysed(AnalysisContext context) {
 *             // 所有数据解析完成后的操作
 *             System.out.println("导入完成");
 *         }
 *     };
 *
 *     // 执行导入
 *     CommonExcelUtils.importExcel(inputStream, UserDTO.class, listener);
 *     return Result.success("导入成功");
 * }
 *
 * // 示例5：定义导出数据模型
 * public class UserDTO {
 *     {@literal @}ExcelProperty(value = "用户ID", index = 0)
 *     private Long id;
 *
 *     {@literal @}ExcelProperty(value = "用户名", index = 1)
 *     private String username;
 *
 *     {@literal @}ExcelProperty(value = "邮箱", index = 2)
 *     private String email;
 *
 *     {@literal @}ExcelProperty(value = "创建时间", index = 3)
 *     {@literal @}DateTimeFormat("yyyy-MM-dd HH:mm:ss")
 *     private Date createTime;
 *
 *     // getters and setters
 * }
 *
 * // 示例6：自定义Sheet名称
 * CommonExcelUtils.writeExcel(
 *     response,
 *     dataList,
 *     DataDTO.class,
 *     null,
 *     "导出文件",      // 文件名
 *     "数据汇总表"     // Sheet名称
 * );
 * </pre>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>导出的数据类需要使用EasyExcel的注解（@ExcelProperty等）</li>
 *   <li>导入时需要实现AnalysisEventListener来处理每一行数据</li>
 *   <li>大数据量导出时，EasyExcel会自动使用流式处理，避免内存溢出</li>
 *   <li>导出到HTTP响应时，文件名会自动进行URL编码</li>
 *   <li>错误信息的Map键为列索引（从0开始），值为错误描述</li>
 *   <li>导出的Excel为xlsx格式（Excel 2007+）</li>
 *   <li>表头字体12号，内容字体11号</li>
 *   <li>列宽会根据内容自动调整</li>
 * </ul>
 *
 * <p><b>最佳实践：</b></p>
 * <ul>
 *   <li>数据模型类使用@ExcelProperty注解指定列名和顺序</li>
 *   <li>时间类型使用@DateTimeFormat注解指定格式</li>
 *   <li>数字类型使用@NumberFormat注解指定格式</li>
 *   <li>导入时在监听器中进行数据校验</li>
 *   <li>导入大文件时使用批处理，避免一次性加载所有数据</li>
 *   <li>导出时如果有错误，使用errMsgList参数标记错误行</li>
 *   <li>Sheet名称不要包含特殊字符（如: / \ ? * [ ]等）</li>
 * </ul>
 *
 * <p><b>性能优化：</b></p>
 * <ul>
 *   <li>EasyExcel使用SAX解析，内存占用低</li>
 *   <li>大数据量导出自动使用流式写入</li>
 *   <li>导入时使用监听器模式，避免一次性加载所有数据</li>
 *   <li>如需导出超大数据量（百万级），建议分批导出或使用CSV格式</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0.0
 * @see com.alibaba.excel.EasyExcel
 * @see com.alibaba.excel.annotation.ExcelProperty
 * @see com.alibaba.excel.event.AnalysisEventListener
 */
public class CommonExcelUtils {

    /**
     * 导出Excel到HTTP响应（Sheet名称与文件名相同）
     * <p>
     * 将数据列表导出为Excel文件并写入HTTP响应流，浏览器会提示下载。
     * Sheet名称默认与文件名相同。
     * </p>
     *
     * @param response HTTP响应对象
     * @param objects 要导出的数据列表
     * @param clazz 数据对象的类型（需要用@ExcelProperty注解标注字段）
     * @param errMsgList 错误信息列表，每个元素是一个Map，键为列索引（从0开始），值为错误描述；传null表示无错误
     * @param fileName 导出的文件名（不含扩展名，会自动添加.xlsx）
     * @throws IOException 如果写入响应流失败
     * @see #writeExcel(HttpServletResponse, List, Class, List, String, String)
     */
    public static void writeExcel(HttpServletResponse response, List objects, Class clazz,List<Map<Integer, String>> errMsgList, String fileName) throws IOException {
        String sheetName = fileName;
        writeExcel(response, objects, clazz, errMsgList, fileName, sheetName);
    }


    /**
     * 导出Excel到HTTP响应（自定义Sheet名称）
     * <p>
     * 将数据列表导出为Excel文件并写入HTTP响应流，浏览器会提示下载。
     * 可以自定义Sheet名称，适用于需要区分文件名和Sheet名的场景。
     * </p>
     *
     * <p><b>功能特点：</b></p>
     * <ul>
     *   <li>自动设置HTTP响应头，触发浏览器下载</li>
     *   <li>文件名自动进行URL编码，支持中文</li>
     *   <li>自动设置Content-Type为Excel格式</li>
     *   <li>导出完成后自动关闭输出流</li>
     * </ul>
     *
     * @param response HTTP响应对象
     * @param objects 要导出的数据列表
     * @param clazz 数据对象的类型（需要用@ExcelProperty注解标注字段）
     * @param errMsgList 错误信息列表，每个元素是一个Map，键为列索引（从0开始），值为错误描述；传null表示无错误
     * @param fileName 导出的文件名（不含扩展名，会自动添加.xlsx）
     * @param sheetName Sheet名称
     * @throws IOException 如果写入响应流失败
     * @see #writeOutputStream(OutputStream, Class, List, List, String)
     */
    public static void writeExcel(HttpServletResponse response, List objects, Class clazz, List<Map<Integer, String>> errMsgList, String fileName, String sheetName) throws IOException {
        response.addHeader("responseType", "blob");
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment:filename=" + URLEncoder.encode(fileName + "." + "xlsx", "UTF-8"));
        ServletOutputStream outputStream = response.getOutputStream();
        writeOutputStream(outputStream, clazz, objects, errMsgList, sheetName);
        try {
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将数据写入输出流为Excel格式
     * <p>
     * 将数据列表写入指定的输出流，支持文件输出流、网络流等。
     * 这是底层方法，可用于非HTTP响应的场景（如文件导出、邮件附件等）。
     * </p>
     *
     * <p><b>样式配置：</b></p>
     * <ul>
     *   <li>表头：白色背景，12号字体</li>
     *   <li>内容：11号字体</li>
     *   <li>列宽：自动根据内容调整</li>
     *   <li>错误行：如果有错误信息，相应单元格会被标记（红色边框或背景）</li>
     * </ul>
     *
     * <p><b>功能特点：</b></p>
     * <ul>
     *   <li>支持自定义样式策略</li>
     *   <li>自动调整列宽以适应内容</li>
     *   <li>支持错误标记（通过ErrWriteHandler）</li>
     *   <li>如果有错误信息，会使用内存模式写入（inMemory=true）</li>
     * </ul>
     *
     * @param outputStream 输出流（如FileOutputStream、ServletOutputStream等）
     * @param clazz 数据对象的类型（需要用@ExcelProperty注解标注字段）
     * @param objects 要导出的数据列表
     * @param errMsgList 错误信息列表，每个元素是一个Map，键为列索引（从0开始），值为错误描述；传null表示无错误
     * @param sheetName Sheet名称
     * @throws RuntimeException 如果写入过程发生错误
     * @see io.dataease.model.excel.AutoAdaptWidthStyleStrategy
     * @see io.dataease.model.excel.ErrWriteHandler
     */
    public static void writeOutputStream(OutputStream outputStream, Class clazz, List objects, List<Map<Integer, String>> errMsgList, String sheetName) {
        WriteCellStyle headCellStyle = new WriteCellStyle();
        headCellStyle.setFillForegroundColor(Short.valueOf(IndexedColors.WHITE.getIndex()));
        WriteFont headFont = new WriteFont();
        headFont.setFontHeightInPoints((short) 12);
        headCellStyle.setWriteFont(headFont);
        WriteCellStyle contentCellStyle = new WriteCellStyle();

        WriteFont contentFont = new WriteFont();
        contentFont.setFontHeightInPoints((short) 11);
        contentCellStyle.setWriteFont(contentFont);

        HorizontalCellStyleStrategy horizontalCellStyleStrategy = new HorizontalCellStyleStrategy(headCellStyle, contentCellStyle);
        try {
            ExcelWriterBuilder writerBuilder = EasyExcel.write(outputStream, clazz);
            if (CollectionUtils.isNotEmpty(errMsgList)) {
                writerBuilder.inMemory(Boolean.TRUE).registerWriteHandler(new ErrWriteHandler(errMsgList));
            }
            writerBuilder.registerWriteHandler(new AutoAdaptWidthStyleStrategy()).registerWriteHandler(horizontalCellStyleStrategy).sheet(sheetName).doWrite(objects);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 从输入流导入Excel文件
     * <p>
     * 读取Excel文件并将每一行数据转换为Java对象，通过监听器处理。
     * 使用流式读取，适合处理大文件，不会一次性加载所有数据到内存。
     * </p>
     *
     * <p><b>工作原理：</b></p>
     * <ol>
     *   <li>EasyExcel逐行读取Excel文件</li>
     *   <li>将每一行数据转换为指定的Java对象</li>
     *   <li>调用监听器的invoke方法处理每一行数据</li>
     *   <li>读取完成后调用监听器的doAfterAllAnalysed方法</li>
     * </ol>
     *
     * <p><b>监听器实现要点：</b></p>
     * <ul>
     *   <li>继承AnalysisEventListener并实现invoke和doAfterAllAnalysed方法</li>
     *   <li>在invoke方法中处理每一行数据（如数据校验、保存到数据库等）</li>
     *   <li>在doAfterAllAnalysed方法中处理导入完成后的操作（如批量提交）</li>
     *   <li>建议使用批处理提高性能（如每1000条提交一次）</li>
     * </ul>
     *
     * @param inputStream Excel文件输入流
     * @param clazsz 数据对象的类型（需要用@ExcelProperty注解标注字段）
     * @param analysisEventListener 数据处理监听器，用于处理每一行数据
     * @throws Exception 如果读取或解析失败
     * @see com.alibaba.excel.event.AnalysisEventListener
     * @see com.alibaba.excel.annotation.ExcelProperty
     */
    public static void importExcel(InputStream inputStream, Class clazsz, AnalysisEventListener analysisEventListener) throws Exception {
        EasyExcel.read(inputStream, clazsz, analysisEventListener).sheet().headRowNumber(0).doRead();
    }

}
