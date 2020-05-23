package com.zy.model;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopCartItemDiscount implements Serializable {

  @ApiModelProperty
   private   List<ShopCartItem> shopCartItems =Collections.emptyList();
}
