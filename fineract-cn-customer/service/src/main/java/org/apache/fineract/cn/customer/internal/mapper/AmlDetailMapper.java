package org.apache.fineract.cn.customer.internal.mapper;

import org.apache.fineract.cn.customer.api.v1.domain.AmlDetail;
import org.apache.fineract.cn.customer.internal.repository.AmlDetailEntity;;

public final class AmlDetailMapper {

  private AmlDetailMapper() {
    super();
  }
  
   public static AmlDetailEntity map(final AmlDetail amlDetail) {
    final AmlDetailEntity amlDetailEntity = new AmlDetailEntity();
    amlDetailEntity.setStatus(amlDetail.getStatus());
    amlDetailEntity.setAmlObject(amlDetail.getAmlObject());
    return amlDetailEntity;
  }

  public static AmlDetail map(final AmlDetailEntity amlDetailEntity) {
    final AmlDetail amlDetail = new AmlDetail();
    amlDetail.setStatus(amlDetailEntity.getStatus());
    amlDetail.setAmlObject(amlDetailEntity.getAmlObject());
    return amlDetail;
  }
}
