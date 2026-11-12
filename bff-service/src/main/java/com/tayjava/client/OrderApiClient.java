package com.tayjava.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tayjava.common.ApiVersions;
import com.tayjava.common.enums.ErrorCode;
import com.tayjava.common.exception.ErrorException;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class OrderApiClient {

  private final RestClient orderRestClient;

  public OrderApiClient(@Qualifier("orderRestClient") RestClient orderRestClient) {
    this.orderRestClient = orderRestClient;
  }

  public OrderItem getById(Long orderId) {
    try {
      OrderEnvelope body =
          orderRestClient
              .get()
              .uri(ApiVersions.PATH_V1 + "/orders/{id}", orderId)
              .header(ApiVersions.HEADER, ApiVersions.V1)
              .retrieve()
              .body(OrderEnvelope.class);
      if (body == null || body.getData() == null) {
        throw new ErrorException(ErrorCode.BAD_GATEWAY, "Empty order response");
      }
      return body.getData();
    } catch (HttpClientErrorException.NotFound ex) {
      throw new ErrorException(ErrorCode.DATA_NOT_FOUND, "Order not found");
    } catch (ErrorException ex) {
      throw ex;
    } catch (RestClientException ex) {
      throw new ErrorException(ErrorCode.BAD_GATEWAY, "Order service unavailable");
    }
  }

  public List<OrderItem> listFulfillingByStudent(String studentUsername) {
    try {
      OrderListEnvelope body =
          orderRestClient
              .get()
              .uri(
                  uriBuilder ->
                      uriBuilder
                          .path(ApiVersions.PATH_V1 + "/orders")
                          .queryParam("student", studentUsername)
                          .queryParam("status", "FULFILLING")
                          .build())
              .header(ApiVersions.HEADER, ApiVersions.V1)
              .retrieve()
              .body(OrderListEnvelope.class);
      if (body == null || body.getData() == null) {
        return Collections.emptyList();
      }
      return body.getData();
    } catch (ErrorException ex) {
      throw ex;
    } catch (RestClientException ex) {
      throw new ErrorException(ErrorCode.BAD_GATEWAY, "Order service unavailable");
    }
  }

  @Getter
  @Setter
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class OrderEnvelope {
    private OrderItem data;
  }

  @Getter
  @Setter
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class OrderListEnvelope {
    private List<OrderItem> data;
  }

  @Getter
  @Setter
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class OrderItem {
    private Long id;
    private Long enrollmentId;
    private String studentUsername;
    private Long courseId;
    private String status;
  }
}
