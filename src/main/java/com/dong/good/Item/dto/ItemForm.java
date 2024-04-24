package com.dong.good.Item.dto;

import lombok.Data;

@Data
public class ItemForm {
    private Long id;
    private String name;
    private int price;
    private int stockQuantity;
}
