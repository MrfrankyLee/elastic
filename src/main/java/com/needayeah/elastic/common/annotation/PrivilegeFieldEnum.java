package com.needayeah.elastic.common.annotation;


public enum PrivilegeFieldEnum {

    /**
     * 店铺
     */
    shopCodes(ResourceType.Shop, "shopCodes"),

    /**
     * 关键字
     */
    keyWord(ResourceType.Distributor, "keyWord"),
    /**
     * 商品名称
     */
    goodsName(ResourceType.GoodsName, "goodsName");

    private final ResourceType resourceType;
    private final String fieldName;

    PrivilegeFieldEnum(ResourceType resourceType, String fieldName) {
        this.resourceType = resourceType;
        this.fieldName = fieldName;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public String getFieldName() {
        return fieldName;
    }

}
