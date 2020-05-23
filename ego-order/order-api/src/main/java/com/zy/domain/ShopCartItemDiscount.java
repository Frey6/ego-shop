package com.zy.domain;

import com.zy.model.ShopCartItem;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Builder
@Data
public class ShopCartItemDiscount implements Serializable {

    List<ShopCartItem> shopCartItems= Collections.emptyList();
}
