package com.project.simplegw.approval.dtos;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class PurchaseDTO {
    private int seq;
    private String itemName;
    private String itemSpec;
    private LocalDate dueDate;
    private String store;
    private String url;
    private int price;
    private int qty;
    private int sum;
}
