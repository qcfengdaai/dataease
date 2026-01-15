package io.dataease.extensions.view.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 格式化配置DTO
 * 用于配置数值的格式化显示方式
 *
 * @Author Junjun
 */
@Data
@Accessors(chain = true)
public class FormatterCfgDTO {
    /** 格式化类型：auto(自动), value(数值), percent(百分比)，默认为auto */
    private String type = "auto";

    /** 单位语言：ch(中文), en(英文)，默认为ch */
    private String unitLanguage = "ch";

    /** 换算单位，默认为1 */
    private Integer unit = 1;

    /** 单位后缀，默认为空 */
    private String suffix = "";

    /** 小数位数，默认为0 */
    private Integer decimalCount = 0;

    /** 是否使用千分符，默认为false */
    private Boolean thousandSeparator = false;
}
