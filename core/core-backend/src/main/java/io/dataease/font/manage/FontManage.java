package io.dataease.font.manage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.dataease.api.font.dto.FontDto;
import io.dataease.exception.DEException;
import io.dataease.font.dao.auto.entity.CoreFont;
import io.dataease.font.dao.auto.mapper.CoreFontMapper;
import io.dataease.utils.BeanUtils;
import io.dataease.utils.FileUtils;
import io.dataease.utils.IDUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 字体管理组件
 * 负责系统字体的完整生命周期管理，包括字体的上传、存储、下载和删除
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>字体文件的上传和存储管理</li>
 *   <li>字体信息的CRUD操作</li>
 *   <li>默认字体的设置和管理</li>
 *   <li>字体文件的下载和导出</li>
 *   <li>字体文件格式验证和安全检查</li>
 * </ul>
 *
 * <p>支持的字体格式：</p>
 * <ul>
 *   <li>TTF (TrueType Font) - 主要支持的字体格式</li>
 * </ul>
 *
 * <p>管理功能：</p>
 * <ul>
 *   <li>字体列表查询和显示</li>
 *   <li>字体信息的创建和更新</li>
 *   <li>字体文件的物理删除</li>
 *   <li>默认字体的切换管理</li>
 * </ul>
 */
@Component
public class FontManage {

    /**
     * 字体文件存储路径
     * 从配置文件中读取，默认为/opt/dataease2.0/data/font/
     */
    @Value("${dataease.path.font:/opt/dataease2.0/data/font/}")
    private String path;

    /**
     * 字体数据访问层
     * 用于执行字体信息的数据库操作
     */
    @Resource
    private CoreFontMapper coreFontMapper;

    /**
     * Spring资源加载器
     * 用于加载类路径下的资源文件
     */
    @Autowired
    private ResourceLoader resourceLoader;

    /**
     * 获取所有字体列表
     * 查询系统中所有的字体信息，返回字体详细信息列表
     *
     * @return 字体DTO列表，包含所有字体的详细信息
     */
    public List<FontDto> list() {
        // 创建查询条件，查询所有字体
        QueryWrapper<CoreFont> queryWrapper = new QueryWrapper<>();
        List<CoreFont> coreFonts = coreFontMapper.selectList(queryWrapper);

        // 将实体类转换为DTO对象
        List<FontDto> fontDtos = new ArrayList<>();
        for (CoreFont coreFont : coreFonts) {
            FontDto dto = new FontDto();
            BeanUtils.copyBean(dto, coreFont);
            fontDtos.add(dto);
        }

        return fontDtos;
    }

    /**
     * 创建新字体记录
     * 在数据库中创建一个新的字体记录，检查名称重复并生成唯一ID
     *
     * @param fontDto 字体信息DTO，包含要创建的字体详细信息
     * @return 创建成功的字体DTO，包含生成的ID和更新时间
     * @throws DEException 如果字体名称已存在则抛出异常
     */
    public FontDto create(FontDto fontDto) {
        // 检查字体名称是否已存在
        QueryWrapper<CoreFont> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", fontDto.getName());
        if (CollectionUtils.isNotEmpty(coreFontMapper.selectList(queryWrapper))) {
            DEException.throwException("存在重名字库");
        }

        // 生成唯一ID和设置更新时间
        fontDto.setId(IDUtils.snowID());
        CoreFont coreFont = new CoreFont();
        BeanUtils.copyBean(coreFont, fontDto);
        coreFont.setUpdateTime(System.currentTimeMillis());

        // 插入数据库
        coreFontMapper.insert(coreFont);
        return fontDto;
    }


    /**
     * 编辑字体信息
     * 更新已存在的字体记录，如果没有ID则自动创建新记录
     * 如果设置为默认字体，会自动取消其他字体的默认状态
     *
     * @param fontDto 字体信息DTO，包含要更新的字体信息
     * @return 更新成功的字体DTO
     */
    public FontDto edit(FontDto fontDto) {
        // 如果没有ID，则创建新记录
        if (ObjectUtils.isEmpty(fontDto.getId())) {
            return create(fontDto);
        }

        // 如果设置为默认字体，先取消其他字体的默认状态
        if (fontDto.getIsDefault()) {
            UpdateWrapper<CoreFont> updateWrapper = new UpdateWrapper<>();
            updateWrapper.ne("id", fontDto.getId());
            CoreFont record = new CoreFont();
            record.setIsDefault(false);
            coreFontMapper.update(record, updateWrapper);
        }

        // 更新字体信息
        CoreFont coreFont = new CoreFont();
        BeanUtils.copyBean(coreFont, fontDto);
        coreFont.setUpdateTime(System.currentTimeMillis());
        coreFontMapper.updateById(coreFont);
        return fontDto;
    }

    /**
     * 删除字体
     * 从数据库中删除字体记录，并同时删除对应的字体文件
     *
     * @param id 字体ID，要删除的字体记录的唯一标识
     */
    public void delete(Long id) {
        // 查询字体信息
        CoreFont coreFont = coreFontMapper.selectById(id);
        if (coreFont != null) {
            // 删除数据库记录
            coreFontMapper.deleteById(id);
            // 删除对应的文件
            if (StringUtils.isNotEmpty(coreFont.getFileTransName())) {
                FileUtils.deleteFile(path + coreFont.getFileTransName());
            }
        }
    }

    /**
     * 切换默认字体
     * 设置指定字体的默认状态，用于标记系统默认使用的字体
     *
     * @param fontDto 字体信息DTO，包含ID和默认状态信息
     */
    public void changeDefault(FontDto fontDto) {
        // 根据ID更新默认状态
        QueryWrapper<CoreFont> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", fontDto.getId());
        CoreFont record = new CoreFont();
        record.setIsDefault(fontDto.getIsDefault());
        coreFontMapper.update(record, queryWrapper);
    }

    /**
     * 上传字体文件
     * 接收用户上传的字体文件，生成唯一文件名并保存到指定目录
     *
     * @param file 上传的字体文件，必须是TTF格式
     * @return 字体DTO，包含解析出的字体信息和文件信息
     * @throws DEException 如果文件格式不正确或上传失败则抛出异常
     */
    public FontDto upload(MultipartFile file) {
        // 生成唯一文件名UUID
        String fileUuid = UUID.randomUUID().toString();
        return saveFile(file, fileUuid);
    }

    /**
     * 下载字体文件
     * 根据文件转换名称查找字体记录，并将对应的字体文件作为附件下载返回给客户端
     *
     * @param file 文件转换名称，用于查找对应的字体记录
     * @param response HTTP响应对象，用于输出文件流到客户端
     * @throws DEException 如果字体文件不存在或文件读取失败则抛出异常
     */
    public void download(String file, HttpServletResponse response) {
        // 根据文件转换名称查询字体记录
        QueryWrapper<CoreFont> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("file_trans_name", file);
        List<CoreFont> coreFonts = coreFontMapper.selectList(queryWrapper);

        // 检查字体记录是否存在
        if (CollectionUtils.isEmpty(coreFonts)) {
            DEException.throwException("不存在的字库文件");
        }

        try {
            // 设置响应头，指示浏览器以下载方式处理文件
            response.setContentType("application/x-download");
            response.setHeader("Content-Disposition", "attachment;filename=" + coreFonts.get(0).getFileTransName());

            // 使用try-with-resources确保资源正确关闭
            try (ServletOutputStream out = response.getOutputStream();
                 InputStream stream = new FileInputStream(path + coreFonts.get(0).getFileTransName())) {

                // 分块读取文件并写入输出流
                byte buff[] = new byte[1024];
                int length;
                while ((length = stream.read(buff)) > 0) {
                    out.write(buff, 0, length);
                }
                out.flush();
            }
        } catch (IOException e) {
            DEException.throwException(e.getMessage());
        }
    }

    /**
     * 获取默认字体列表
     * 查询系统中所有标记为默认的字体信息，用于在应用程序中设置默认字体样式
     *
     * @return 默认字体DTO列表，包含所有设置为默认的字体详细信息
     */
    public List<FontDto> defaultFont() {
        // 创建查询条件，查找所有标记为默认的字体
        QueryWrapper<CoreFont> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_default", 1);
        List<CoreFont> coreFonts = coreFontMapper.selectList(queryWrapper);

        // 将实体类转换为DTO对象列表
        List<FontDto> fontDtos = new ArrayList<>();
        for (CoreFont coreFont : coreFonts) {
            FontDto dto = new FontDto();
            BeanUtils.copyBean(dto, coreFont);
            fontDtos.add(dto);
        }
        return fontDtos;
    }

    /**
     * 保存字体文件到本地文件系统
     * 将上传的字体文件保存到指定目录，并解析字体信息创建对应的DTO对象
     *
     * @param file 上传的字体文件，必须是TTF格式的字体文件
     * @param fileNameUUID 生成的唯一文件名UUID，用于避免文件名冲突
     * @return 字体DTO对象，包含解析出的字体名称、文件大小等信息
     * @throws DEException 如果文件格式不正确、文件保存失败或字体解析失败则抛出异常
     */
    private FontDto saveFile(MultipartFile file, String fileNameUUID) throws DEException {
        FontDto fontDto = new FontDto();
        try {
            // 获取原始文件名并验证文件格式
            String filename = file.getOriginalFilename();
            if (StringUtils.isEmpty(filename) || !filename.toLowerCase().endsWith(".ttf")) {
                DEException.throwException("非法格式的文件！");
            }

            // 提取文件后缀并构建完整的文件路径
            String suffix = filename.substring(filename.lastIndexOf(".") + 1);
            String filePath = path + fileNameUUID + "." + suffix;

            // 保存文件到本地文件系统
            File f = new File(filePath);
            FileOutputStream fileOutputStream = new FileOutputStream(f);
            fileOutputStream.write(file.getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();

            // 设置文件转换名称
            fontDto.setFileTransName(fileNameUUID + "." + suffix);

            // 计算并格式化文件大小
            long length = file.getSize();
            String unit = "MB";
            Double size = 0.0;
            if ((double) length / 1024 / 1024 > 1) {
                if ((double) length / 1024 / 1024 / 1024 > 1) {
                    // 大于1GB的文件使用GB单位
                    unit = "GB";
                    size = Double.valueOf(String.format("%.2f", (double) length / 1024 / 1024 / 1024));
                } else {
                    // 1MB到1GB之间的文件使用MB单位
                    size = Double.valueOf(String.format("%.2f", (double) length / 1024 / 1024));
                }
            } else {
                // 小于1MB的文件使用KB单位
                unit = "KB";
                size = Double.valueOf(String.format("%.2f", (double) length / 1024));
            }

            // 解析TTF字体文件获取字体名称
            Font font = Font.createFont(Font.TRUETYPE_FONT, new File(filePath));

            // 设置字体DTO的属性
            fontDto.setSize(size);
            fontDto.setSizeType(unit);
            fontDto.setName(font.getFontName());
        } catch (Exception e) {
            DEException.throwException(e);
        }
        return fontDto;
    }

}
