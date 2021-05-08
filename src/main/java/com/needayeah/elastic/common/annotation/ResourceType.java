package com.needayeah.elastic.common.annotation;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 资源类型
 */
public enum ResourceType {

    Menu, Button, Column, Shop, Distributor, GoodsName;
    @Getter
    @Setter
    private String value;

    ResourceType() {
        this.value = name();
    }


    public static ResourceType valueOf(int ordinal) {
        if (ordinal < 0 || ordinal >= values().length) {
            return null;
        }
        return values()[ordinal];
    }

    public static List<ResourceType> getDataPrivilege() {
        return Lists.newArrayList(ResourceType.Shop, ResourceType.Distributor);
    }
}
