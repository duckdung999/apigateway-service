package com.example.orderservice.entity;

import lombok.Cleanup;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Data
@Entity
@Table(name = "orders")
// 직렬화 : 한 객체의 메모리에서의 표현방식을 저장 또한 전송에 적합한 다른 데이터 형식으로 변환하는 과정
public class OrderEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120, unique = true)
    private String productId;

    @Column(nullable = false)
    private Integer qty;
    @Column(nullable = false)
    private Integer totalPrice;
    @Column(nullable = false)
    private Integer unitPrice;

    @Column(nullable = false)
    private String userId;
    @Column(nullable = false, unique = true)
    private String orderId;


    @Column(nullable = false, updatable = false, insertable = false)
    @ColumnDefault(value = "CURRENT_TIMESTAMP") // H2 DB
    private Date createAt;

}
