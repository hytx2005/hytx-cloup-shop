package com.chenzhihao.products.domain.doc;

import lombok.Data;
import org.dromara.easyes.annotation.IndexField;
import org.dromara.easyes.annotation.IndexId;
import org.dromara.easyes.annotation.IndexName;
import org.dromara.easyes.annotation.rely.Analyzer;
import org.dromara.easyes.annotation.rely.FieldType;

import java.math.BigDecimal;

@Data
@IndexName("commodity")
public class CommodityEsDoc {
    /**
     * 文档的唯一ID, 对应ES的_id字段.
     */
    @IndexId
    private String id;

    /**
     * 业务ID, 用于和数据库关联.
     * 需要一个新字段来存储原来在_source中存的数字ID.
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private Long commodityId;
    @IndexField(fieldType = FieldType.TEXT, analyzer = Analyzer.IK_MAX_WORD)
    private String name;
    @IndexField(fieldType = FieldType.KEYWORD)
    private Long userId;
    @IndexField(fieldType = FieldType.DOUBLE)
    private BigDecimal price;
    @IndexField(fieldType = FieldType.KEYWORD)
    private String imageUrl;
    @IndexField(fieldType = FieldType.INTEGER)
    private Integer stock;
    @IndexField(fieldType = FieldType.INTEGER)
    private Integer sold;
    @IndexField(fieldType = FieldType.TEXT, analyzer = Analyzer.STANDARD)
    private String spec;
    @IndexField(fieldType = FieldType.INTEGER)
    private Integer status;
}