package io.dataease.visualization.dto;

import lombok.Data;

/**
 * 水印内容数据传输对象
 * 用于配置可视化仪表板的水印显示效果
 */
@Data
public class WatermarkContentDTO {

    /** 是否启用水印 */
    private Boolean enable;

    /** 是否在Excel导出时启用水印 */
    private Boolean excelEnable = false;

    /** 是否启用面板自定义水印 */
    private Boolean enablePanelCustom;

    /** 水印类型 */
    private String type;

    /** 水印内容 */
    private String content;

    /** 水印颜色 */
    private String watermark_color;

    /** 水印水平间距 */
    private Integer watermark_x_space;

    /** 水印垂直间距 */
    private Integer watermark_y_space;

    /** 水印字体大小 */
    private Integer watermark_fontsize;

}
