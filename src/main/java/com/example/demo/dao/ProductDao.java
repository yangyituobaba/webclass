package com.example.demo.dao;

import com.example.demo.bean.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductDao {

    //从已有的产品中找到对应id的
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "image", column = "image_url"),
            // 不映射订单项
    })
    @Select("SELECT * FROM product WHERE id = #{id}")
    Product findById(Integer id);

    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "image", column = "image_url"),
            // 不映射订单项
    })
    @Select("SELECT * FROM product")
    List<Product> findAll();

    @Update("UPDATE product SET image_url = #{imageUrl} WHERE id = #{id}")
    void updateImageUrl(@Param("id") Integer id, @Param("imageUrl") String imageUrl);


    // 根据名称模糊查询
    @Select("SELECT * FROM product WHERE name LIKE CONCAT('%', #{name}, '%')")
    List<Product> findByNameLike(@Param("name") String name);

    // 插入新产品
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("INSERT INTO product (name, description, price, amount, image_url) VALUES (#{name}, #{description}, #{price}, #{amount}, #{image})")
    void insert(Product product);


    // 更新产品
    @Update("UPDATE product SET name=#{name}, description=#{description}, price=#{price}, amount=#{amount}, image_url=#{image} WHERE id=#{id}")
    void update(Product product);


    // 删除产品
    @Delete("DELETE FROM product WHERE id = #{id}")
    void delete(@Param("id") Integer id);

    // 减少指定产品的库存数量
    // 要求库存足够才减少
    @Update("UPDATE product SET amount = amount - #{quantity} WHERE id = #{productId} AND amount >= #{quantity}")
    int reduceStock(Integer productId, Integer quantity);
}
