package io.dataease.api.permissions.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户批量导入结果视图对象
 *
 * <p>用于返回批量导入用户操作的结果统计。
 * 包含导入成功和失败的数量，以及失败记录的下载标识。</p>
 *
 * <p>导入流程：</p>
 * <ul>
 *   <li>用户上传包含用户信息的 Excel 文件</li>
 *   <li>系统解析文件并逐行验证和导入</li>
 *   <li>导入完成后返回成功和失败统计</li>
 *   <li>如有失败记录，可通过 dataKey 下载失败详情</li>
 * </ul>
 *
 * <p>失败处理：</p>
 * <ul>
 *   <li>失败记录会被保存为临时文件</li>
 *   <li>通过 dataKey 可以下载失败记录的 Excel 文件</li>
 *   <li>失败记录包含错误原因，方便用户修正后重新导入</li>
 *   <li>临时文件可通过清理接口删除</li>
 * </ul>
 */
@Schema(description = "批量导入结果")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserImportVO implements Serializable {
    @Serial
    private static final long serialVersionUID = -3371025717928287780L;

    /**
     * 数据标识键
     * 失败记录的唯一标识，用于下载失败记录文件
     * 通过此 key 可以调用下载接口获取失败详情
     */
    @Schema(description = "数据标志")
    private String dataKey;

    /**
     * 成功导入数量
     * 成功创建的用户数量
     */
    @Schema(description = "成功数量")
    private int successCount;

    /**
     * 失败数量
     * 导入失败的记录数量
     * 失败原因可通过下载失败记录查看
     */
    @Schema(description = "失败数量")
    private int errorCount;

    /**
     * 构造函数：只初始化 dataKey
     *
     * @param dataKey 数据标识键
     */
    public UserImportVO(String dataKey) {
        this.dataKey = dataKey;
    }
}
