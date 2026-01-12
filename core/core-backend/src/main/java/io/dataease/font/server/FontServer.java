package io.dataease.font.server;

import io.dataease.api.font.api.FontApi;
import io.dataease.api.font.dto.FontDto;
import io.dataease.exception.DEException;
import io.dataease.font.manage.FontManage;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 字体服务控制器
 * 提供字体管理相关的REST API接口，作为前端与字体管理业务逻辑之间的桥梁
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>字体列表查询和展示</li>
 *   <li>字体信息的创建和更新</li>
 *   <li>字体文件的上传和下载</li>
 *   <li>默认字体的设置和管理</li>
 *   <li>字体记录的删除操作</li>
 * </ul>
 *
 * <p>URL映射：</p>
 * <ul>
 *   <li>基础路径：/typeface</li>
 *   <li>所有字体相关的API调用都通过此控制器处理</li>
 * </ul>
 *
 * <p>实现模式：</p>
 * <ul>
 *   <li>实现FontApi接口，确保API契约的一致性</li>
 *   <li>将所有业务逻辑委托给FontManage组件处理</li>
 *   <li>负责HTTP请求的接收和响应的格式化</li>
 * </ul>
 */
@RestController
@RequestMapping("/typeface")
public class FontServer implements FontApi {

    /**
     * 字体管理组件
     * 负责处理所有字体相关的业务逻辑操作
     */
    @Resource
    private FontManage fontManage;

    /**
     * 获取字体列表
     * 查询系统中所有可用的字体信息，提供给前端进行字体选择和展示
     *
     * @return 字体DTO列表，包含所有字体的详细信息
     */
    @Override
    public List<FontDto> list() {
        return fontManage.list();
    }

    /**
     * 创建字体记录
     * 在系统中创建一个新的字体记录，用于注册新添加的字体信息
     *
     * @param fontDto 字体信息DTO，包含要创建的字体详细信息
     * @return 创建成功的字体DTO，包含生成的ID和更新时间
     * @throws DEException 如果字体名称已存在或创建过程中出现错误
     */
    @Override
    public FontDto create(FontDto fontDto) {
        return fontManage.create(fontDto);
    }

    /**
     * 编辑字体信息
     * 更新已存在的字体记录，支持修改字体名称、默认状态等属性
     *
     * @param fontDto 字体信息DTO，包含要更新的字体信息
     * @return 更新成功的字体DTO
     */
    @Override
    public FontDto edit(FontDto fontDto) {
        return fontManage.edit(fontDto);
    }

    /**
     * 删除字体
     * 从系统中删除指定的字体记录，同时清理对应的字体文件
     *
     * @param id 字体ID，要删除的字体记录的唯一标识
     */
    @Override
    public void delete(Long id) {
        fontManage.delete(id);
    }

    /**
     * 切换默认字体
     * 设置或取消指定字体的默认状态，用于系统默认字体的管理
     *
     * @param fontDto 字体信息DTO，包含ID和默认状态信息
     */
    @Override
    public void changeDefault(FontDto fontDto) {
        fontManage.changeDefault(fontDto);
    }

    /**
     * 上传字体文件
     * 接收前端上传的字体文件，进行验证、保存并解析字体信息
     *
     * @param file 上传的字体文件，必须是TTF格式
     * @return 字体DTO对象，包含解析出的字体信息和文件信息
     * @throws DEException 如果文件格式不正确或上传失败
     */
    @Override
    public FontDto upload(MultipartFile file) throws DEException {
        return fontManage.upload(file);
    }

    /**
     * 下载字体文件
     * 根据文件名下载对应的字体文件，以附件形式返回给客户端
     *
     * @param file 文件转换名称，用于标识要下载的字体文件
     * @param response HTTP响应对象，用于输出文件流
     * @throws DEException 如果文件不存在或下载过程中出现错误
     */
    @Override
    public void download(String file, HttpServletResponse response) throws DEException {
        fontManage.download(file, response);
    }

    /**
     * 获取默认字体列表
     * 查询系统中所有标记为默认的字体信息，用于系统界面的默认字体设置
     *
     * @return 默认字体DTO列表，包含所有默认字体的详细信息
     * @throws DEException 如果查询过程中出现错误
     */
    @Override
    public List<FontDto> defaultFont() throws DEException {
        return fontManage.defaultFont();
    }
}
