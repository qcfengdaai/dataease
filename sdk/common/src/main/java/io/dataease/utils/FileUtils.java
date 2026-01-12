package io.dataease.utils;

import io.dataease.exception.DEException;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件操作工具类
 * <p>
 * 提供文件和目录的常用操作功能，包括文件上传、复制、删除、读取等。
 * 封装了Java NIO和传统IO的文件操作，提供简洁易用的API。
 * </p>
 *
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li>目录管理 - 创建、验证、递归删除目录</li>
 *   <li>文件上传 - 处理MultipartFile文件上传</li>
 *   <li>文件操作 - 复制、移动、删除文件</li>
 *   <li>文件读取 - 读取文件内容为字符串或字节数组</li>
 *   <li>文件信息 - 获取文件名、扩展名、前缀等信息</li>
 *   <li>文件夹操作 - 递归复制、删除文件夹</li>
 * </ul>
 *
 * <p><b>使用位置：</b></p>
 * <ul>
 *   <li>{@link io.dataease.datasource.provider.ExcelUtils} - Excel数据源文件管理</li>
 *   <li>{@link io.dataease.datasource.server.DatasourceDriverServer} - 数据源驱动文件管理</li>
 *   <li>{@link io.dataease.exportCenter.manage.ExportCenterManage} - 导出文件管理</li>
 *   <li>{@link io.dataease.font.manage.FontManage} - 字体文件管理</li>
 *   <li>{@link io.dataease.map.manage.MapManage} - 地图文件上传管理</li>
 *   <li>{@link io.dataease.visualization.server.StaticResourceServer} - 静态资源管理</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 示例1：创建目录（如果不存在）
 * Path dirPath = Paths.get("/opt/dataease/data");
 * FileUtils.createIfAbsent(dirPath);
 * // 目录不存在时会自动创建，包括父目录
 *
 * // 示例2：文件上传
 * MultipartFile file = request.getFile("file");
 * String uploadPath = "/opt/dataease/uploads/";
 * File uploadedFile = FileUtils.upload(file, uploadPath);
 * System.out.println("文件已上传: " + uploadedFile.getAbsolutePath());
 *
 * // 示例3：获取文件名和扩展名
 * String fileName = "report.xlsx";
 * String nameNoExt = FileUtils.getFileNameNoEx(fileName);  // "report"
 * String extension = FileUtils.getExtensionName(fileName);  // "xlsx"
 * System.out.println("文件名: " + nameNoExt + ", 扩展名: " + extension);
 *
 * // 示例4：复制文件
 * File sourceFile = new File("/data/source.txt");
 * String targetDir = "/data/backup/";
 * String copiedPath = FileUtils.copy(sourceFile, targetDir);
 * System.out.println("文件已复制到: " + copiedPath);
 *
 * // 示例5：复制整个文件夹
 * String sourceDir = "/opt/dataease/templates/";
 * String targetDir = "/opt/dataease/instances/";
 * FileUtils.copyFolder(sourceDir, targetDir);
 * // 递归复制源目录下的所有文件和子目录
 *
 * // 示例6：读取JSON文件
 * File jsonFile = new File("/config/settings.json");
 * String jsonContent = FileUtils.readJson(jsonFile);
 * // 将文件内容读取为UTF-8字符串
 *
 * // 示例7：读取文件为字节数组
 * String filePath = "/data/image.png";
 * byte[] fileBytes = FileUtils.readBytes(filePath);
 * // 用于读取二进制文件，如图片、PDF等
 *
 * // 示例8：删除文件或目录
 * String pathToDelete = "/tmp/old-data/";
 * FileUtils.deleteFile(pathToDelete);
 * // 递归删除目录及其所有内容
 *
 * // 示例9：递归删除目录
 * String exportPath = "/opt/dataease/export/task-123/";
 * boolean success = FileUtils.deleteDirectoryRecursively(exportPath);
 * System.out.println("删除" + (success ? "成功" : "失败"));
 *
 * // 示例10：列出目录下的所有文件名
 * String dirPath = "/opt/dataease/fonts/";
 * List&lt;String&gt; fileNames = FileUtils.listFileNames(dirPath);
 * fileNames.forEach(name -&gt; System.out.println("文件: " + name));
 *
 * // 示例11：实际使用场景 - Excel数据源文件清理
 * // 参考 ExcelUtils.java:202
 * String excelPath = "/opt/dataease/excel/";
 * String tranName = "temp_20230101.xlsx";
 * FileUtils.deleteFile(excelPath + tranName);
 *
 * // 示例12：实际使用场景 - 导出任务清理
 * // 参考 ExportCenterManage.java:101
 * String exportDataPath = "/opt/dataease/export/";
 * String taskId = "task-456";
 * FileUtils.deleteDirectoryRecursively(exportDataPath + taskId);
 * </pre>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>文件路径建议使用绝对路径，避免相对路径引起的混淆</li>
 *   <li>删除操作不可逆，使用前请确认路径正确</li>
 *   <li>递归删除目录会删除所有子文件和子目录，请谨慎使用</li>
 *   <li>文件上传时会覆盖同名文件</li>
 *   <li>大文件操作可能耗时较长，建议异步处理</li>
 *   <li>Windows和Linux路径分隔符不同，建议使用File.separator</li>
 *   <li>文件操作可能抛出IOException，需要妥善处理异常</li>
 *   <li>读取文件时默认使用UTF-8编码</li>
 * </ul>
 *
 * <p><b>最佳实践：</b></p>
 * <ul>
 *   <li>上传文件前先使用validateExist()确保目录存在</li>
 *   <li>删除文件前先使用exist()检查文件是否存在</li>
 *   <li>操作临时文件后及时清理，避免磁盘空间浪费</li>
 *   <li>批量操作时考虑使用事务或回滚机制</li>
 *   <li>敏感文件删除后考虑安全擦除</li>
 *   <li>跨平台应用注意路径分隔符的处理</li>
 *   <li>对于大文件，使用流式处理而非一次性加载到内存</li>
 * </ul>
 *
 * <p><b>安全建议：</b></p>
 * <ul>
 *   <li>验证文件路径，防止路径遍历攻击（../ 等）</li>
 *   <li>限制文件上传大小，防止磁盘空间耗尽</li>
 *   <li>验证文件类型，防止恶意文件上传</li>
 *   <li>对用户输入的文件名进行清理和验证</li>
 *   <li>敏感目录和文件设置适当的访问权限</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0.0
 * @see java.nio.file.Files
 * @see java.io.File
 * @see org.springframework.web.multipart.MultipartFile
 */
public class FileUtils {

    /**
     * 创建目录（如果不存在）
     * <p>
     * 检查指定路径的目录是否存在，如果不存在则创建该目录及其所有必需的父目录。
     * 这是一个幂等操作，多次调用不会产生副作用。
     * </p>
     *
     * <p><b>使用场景：</b></p>
     * <ul>
     *   <li>初始化应用数据目录</li>
     *   <li>上传文件前确保目标目录存在</li>
     *   <li>创建日志、缓存等目录</li>
     * </ul>
     *
     * @param path 目录路径，不能为null
     * @throws IOException 如果创建目录失败
     * @throws IllegalArgumentException 如果path为null
     * @see java.nio.file.Files#createDirectories(Path, java.nio.file.attribute.FileAttribute[])
     */
    public static void createIfAbsent(@NonNull Path path) throws IOException {
        Assert.notNull(path, "Path must not be null");

        if (Files.notExists(path)) {
            // Create directories
            Files.createDirectories(path);
            LogUtil.debug("Created directory: [{}]", path);
        }
    }


    /**
     * 获取不带扩展名的文件名
     * <p>
     * 从完整文件名中提取不包含扩展名的部分。
     * 例如："report.xlsx" 返回 "report"，"data.tar.gz" 返回 "data.tar"。
     * </p>
     *
     * @param filename 完整的文件名，可以为null
     * @return 不带扩展名的文件名；如果文件名为null、空字符串或没有扩展名，则返回原文件名
     */
    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    /**
     * 获取文件扩展名（不带点号）
     * <p>
     * 从完整文件名中提取扩展名部分，不包含点号。
     * 例如："report.xlsx" 返回 "xlsx"，"data.tar.gz" 返回 "gz"。
     * </p>
     *
     * <p><b>实际使用位置：</b></p>
     * <ul>
     *   <li>{@link io.dataease.map.manage.MapManage} - 验证地图文件上传时的文件格式</li>
     * </ul>
     *
     * @param filename 完整的文件名，可以为null
     * @return 文件扩展名（不带点号）；如果文件名为null、空字符串或没有扩展名，则返回原文件名
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    /**
     * 验证目录是否存在，不存在则创建
     * <p>
     * 检查指定路径的目录是否存在，如果不存在则创建该目录及其所有父目录。
     * 与{@link #createIfAbsent(Path)}功能类似，但参数类型为String。
     * </p>
     *
     * <p><b>实际使用位置：</b></p>
     * <ul>
     *   <li>{@link io.dataease.visualization.utils.VisualizationExcelUtils} - 导出Excel前确保目录存在</li>
     * </ul>
     *
     * @param path 目录路径字符串
     */
    public static void validateExist(String path) {
        File dir = new File(path);
        if (dir.exists()) return;
        dir.mkdirs();
    }

    /**
     * 上传文件到指定目录
     * <p>
     * 处理Spring MultipartFile文件上传，将文件保存到指定目录。
     * 如果目标目录不存在，会自动创建。文件名保持原文件名不变。
     * </p>
     *
     * <p><b>功能特点：</b></p>
     * <ul>
     *   <li>自动创建目标目录（如果不存在）</li>
     *   <li>解析规范化的文件路径（处理相对路径、符号链接等）</li>
     *   <li>保持原始文件名和扩展名</li>
     *   <li>同名文件会被覆盖</li>
     * </ul>
     *
     * @param file 要上传的文件（MultipartFile对象）
     * @param filePath 目标目录路径，应以路径分隔符结尾
     * @return 上传后的文件对象；如果上传失败则返回null
     */
    public static File upload(MultipartFile file, String filePath) {
        String name = getFileNameNoEx(file.getOriginalFilename());
        String suffix = getExtensionName(file.getOriginalFilename());
        try {
            validateExist(filePath);
            String fileName = name + "." + suffix;
            String path = filePath + fileName;
            // getCanonicalFile 可解析正确各种路径
            File dest = new File(path).getCanonicalFile();

            // 文件写入
            FileOutputStream fileOutputStream = new FileOutputStream(dest);
            fileOutputStream.write(file.getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
            return dest;
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 递归复制文件夹及其所有内容
     * <p>
     * 将源文件夹及其所有子文件夹和文件复制到目标位置。
     * 保持原有的目录结构，如果目标目录不存在会自动创建。
     * </p>
     *
     * <p><b>功能特点：</b></p>
     * <ul>
     *   <li>递归复制所有子目录和文件</li>
     *   <li>保持原有目录结构</li>
     *   <li>自动创建目标目录</li>
     *   <li>使用缓冲流提高复制效率</li>
     * </ul>
     *
     * @param sourcePath 源文件夹路径
     * @param targetPath 目标文件夹路径
     * @throws Exception 如果源文件夹不存在、不是目录，或目标不是目录时抛出异常
     */
    public static void copyFolder(String sourcePath, String targetPath) throws Exception {
        //源文件夹路径
        File sourceFile = new File(sourcePath);
        //目标文件夹路径
        File targetFile = new File(targetPath);

        if (!sourceFile.exists()) {
            throw new Exception("文件夹不存在");
        }
        if (!sourceFile.isDirectory()) {
            throw new Exception("源文件夹不是目录");
        }
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        if (!targetFile.isDirectory()) {
            throw new Exception("目标文件夹不是目录");
        }

        File[] files = sourceFile.listFiles();
        if (files == null || files.length == 0) {
            return;
        }

        for (File file : files) {
            //文件要移动的路径
            String movePath = targetFile + File.separator + file.getName();
            if (file.isDirectory()) {
                //如果是目录则递归调用
                copyFolder(file.getAbsolutePath(), movePath);
            } else {
                //如果是文件则复制文件
                BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(movePath));

                byte[] b = new byte[1024];
                int temp = 0;
                while ((temp = in.read(b)) != -1) {
                    out.write(b, 0, temp);
                }
                out.close();
                in.close();
            }
        }
    }


    /**
     * 复制单个文件到目标目录
     * <p>
     * 将源文件复制到指定的目标目录，保持原文件名不变。
     * 使用NIO的FileChannel进行高效复制。如果目标目录不存在会自动创建。
     * </p>
     *
     * @param source 源文件对象
     * @param targetDir 目标目录路径
     * @return 复制后的文件完整路径
     * @throws IOException 如果文件复制失败
     * @see #copyFileUsingFileChannels(File, File)
     */
    public static String copy(File source, String targetDir) throws IOException {
        String name = source.getName();
        String destPath = null;
        if (targetDir.endsWith("/") || targetDir.endsWith("\\")) {
            destPath = targetDir + name;
        } else {
            destPath = targetDir + "/" + name;
        }
        File DestFile = new File(destPath);
        if (!DestFile.getParentFile().exists()) {
            DestFile.getParentFile().mkdirs();
        }
        copyFileUsingFileChannels(source, DestFile);
        return destPath;
    }

    /**
     * 使用FileChannel复制文件（内部方法）
     * <p>
     * 使用NIO的FileChannel进行文件复制，比传统的字节流复制更高效。
     * FileChannel使用操作系统的零拷贝技术，直接在内核空间传输数据。
     * </p>
     *
     * @param source 源文件
     * @param dest 目标文件
     * @throws IOException 如果文件复制失败
     */
    private static void copyFileUsingFileChannels(File source, File dest) throws IOException {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            inputChannel.close();
            outputChannel.close();
        }
    }

    /**
     * 读取JSON文件内容为字符串
     * <p>
     * 以UTF-8编码读取文件的全部内容，返回字符串形式。
     * 主要用于读取JSON配置文件、数据文件等文本文件。
     * </p>
     *
     * @param file 要读取的文件对象
     * @return 文件内容字符串；如果读取失败则返回null
     */
    public static String readJson(File file) {
        String str = null;
        try {
            FileReader fileReader = new FileReader(file);
            Reader reader = new InputStreamReader(new FileInputStream(file), "utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            str = sb.toString();
            return str;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 删除文件或目录
     * <p>
     * 删除指定路径的文件或目录。如果是目录，会递归删除目录下的所有文件和子目录。
     * 这是一个危险操作，删除的内容无法恢复，请谨慎使用。
     * </p>
     *
     * <p><b>实际使用位置：</b></p>
     * <ul>
     *   <li>{@link io.dataease.datasource.provider.ExcelUtils} - 清理Excel临时文件</li>
     *   <li>{@link io.dataease.datasource.server.DatasourceDriverServer} - 删除数据源驱动文件</li>
     *   <li>{@link io.dataease.font.manage.FontManage} - 删除字体文件</li>
     * </ul>
     *
     * @param path 要删除的文件或目录路径
     */
    public static void deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            if (file.isDirectory()) {
                Arrays.stream(file.listFiles()).forEach(item -> deleteFile(item.getAbsolutePath()));
            }
            file.delete();
        }
    }

    /**
     * 检查文件或目录是否存在
     * <p>
     * 判断指定路径的文件或目录是否存在于文件系统中。
     * </p>
     *
     * @param path 要检查的文件或目录路径
     * @return 如果文件或目录存在返回true，否则返回false
     */
    public static boolean exist(String path) {
        File file = new File(path);
        return file.exists();
    }

    /**
     * 列出目录下所有文件和子目录的名称
     * <p>
     * 获取指定目录下所有文件和子目录的名称列表（不包含子目录的内容）。
     * 只返回直接子项的名称，不递归遍历。
     * </p>
     *
     * @param path 目录路径
     * @return 文件和目录名称列表；如果目录不存在则返回null
     */
    public static List<String> listFileNames(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        } else {
            File[] files = file.listFiles();

            assert files != null;

            return Arrays.stream(files).map(File::getName).collect(Collectors.toList());
        }
    }

    /**
     * 获取文件后缀名（不带点号）
     * <p>
     * 从文件名中提取扩展名，不包含点号。
     * 与{@link #getExtensionName(String)}功能相同，但实现方式略有不同。
     * </p>
     *
     * @param fileName 文件名
     * @return 文件扩展名（不带点号）
     * @throws StringIndexOutOfBoundsException 如果文件名不包含点号
     */
    public static String getSuffix(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /**
     * 获取文件前缀名（不带扩展名）
     * <p>
     * 从文件名中提取不包含扩展名的部分。
     * 与{@link #getFileNameNoEx(String)}功能相同，但实现方式略有不同。
     * </p>
     *
     * @param fileName 文件名
     * @return 不带扩展名的文件名
     * @throws StringIndexOutOfBoundsException 如果文件名不包含点号
     */
    public static String getPrefix(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    /**
     * 读取文件为字节数组
     * <p>
     * 将文件的全部内容读取为字节数组。适用于读取二进制文件，如图片、PDF、视频等。
     * 使用4KB缓冲区读取，适合中小型文件。对于大文件，建议使用流式处理。
     * </p>
     *
     * <p><b>注意事项：</b></p>
     * <ul>
     *   <li>整个文件内容会加载到内存，大文件可能导致内存溢出</li>
     *   <li>文件必须存在且是普通文件（非目录）</li>
     * </ul>
     *
     * @param path 文件路径
     * @return 文件内容的字节数组
     * @throws DEException 如果文件不存在或不是普通文件
     */
    public static byte[] readBytes(String path) {
        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            DEException.throwException("文件不存在");
        }

        byte[] bytes = null;

        try {
            FileInputStream fis = new FileInputStream(file);

            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();

                try {
                    byte[] buffer = new byte[4096];

                    while (true) {
                        int bytesRead;
                        if ((bytesRead = fis.read(buffer)) == -1) {
                            bytes = bos.toByteArray();
                            break;
                        }

                        bos.write(buffer, 0, bytesRead);
                    }
                } catch (Throwable var9) {
                    try {
                        bos.close();
                    } catch (Throwable var8) {
                        var9.addSuppressed(var8);
                    }

                    throw var9;
                }

                bos.close();
            } catch (Throwable var10) {
                try {
                    fis.close();
                } catch (Throwable var7) {
                    var10.addSuppressed(var7);
                }

                throw var10;
            }

            fis.close();
        } catch (Exception var11) {
            var11.printStackTrace();
        }

        return bytes;
    }


    /**
     * 递归删除目录及其所有内容
     * <p>
     * 递归删除指定目录及其所有子目录和文件。
     * 这是一个危险操作，删除的内容无法恢复，使用前请确认路径正确。
     * </p>
     *
     * <p><b>实际使用位置：</b></p>
     * <ul>
     *   <li>{@link io.dataease.exportCenter.manage.ExportCenterManage} - 清理导出任务目录</li>
     * </ul>
     *
     * <p><b>功能特点：</b></p>
     * <ul>
     *   <li>递归删除所有子文件和子目录</li>
     *   <li>如果目录不存在，返回true（视为删除成功）</li>
     *   <li>从最深层级开始删除，最后删除根目录</li>
     * </ul>
     *
     * @param directoryPath 要删除的目录路径
     * @return 如果删除成功返回true，否则返回false
     */
    public static boolean deleteDirectoryRecursively(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            return true;
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectoryRecursively(file.getAbsolutePath());
                } else {
                    boolean deletionSuccess = file.delete();
                }
            }
        }
        return directory.delete();
    }
}
