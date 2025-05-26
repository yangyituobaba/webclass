package com.example.demo.dao;

import com.example.demo.bean.Product;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductDao {
    List<Product> findAll();
    void insertProduct(Product product);
    void updateProduct(Product product);
    void deleteProduct(Product product);
}
