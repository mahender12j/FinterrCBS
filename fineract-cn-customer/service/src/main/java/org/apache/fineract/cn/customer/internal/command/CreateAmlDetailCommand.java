package org.apache.fineract.cn.customer.internal.command;

import org.apache.fineract.cn.customer.api.v1.domain.AmlDetail;;

/**
 * @author Padma Raju
 */
public class CreateAmlDetailCommand {
  private final String customerIdentifier;
  private final AmlDetail  amlDetail;

  public CreateAmlDetailCommand(
      final String customerIdentifier,
      final AmlDetail amlDetail) {
    this.customerIdentifier = customerIdentifier;
    this.amlDetail = amlDetail;
  }

  public String getCustomerIdentifier() {
    return customerIdentifier;
  }

  public AmlDetail getAmlDetail() {
    return amlDetail;
  }

  @Override
  public String toString() {
    return "CreateAmlDetailCommand {" +
        "customerIdentifier='" + customerIdentifier + '\'' +
        ", amlDetail=" + amlDetail.getStatus() +
        '}';
  }
}

