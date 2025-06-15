package com.remgagagali727.discord.survplanet.entity;

import lombok.Getter;

@Getter
public class Item {
    private String id;
    private String name;
    private String description;
    private Boolean crafteable;
    private String buy_price;
    private String sell_price;
}
