package com.example.demo.service;

import com.example.demo.bean.Product;
import com.example.demo.dao.ProductDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductDao productDao;

    //展示所有产品
    public List<Product> getAllProducts(){
        return productDao.findAll();
    }

    //通过Id查找产品
    public Product findById(Integer id) {
        return productDao.findById(id);
    }

    public void updateImageUrl(Integer productId, String imageUrl) {
        productDao.updateImageUrl(productId, imageUrl);
        System.out.println("uploadImage: id=" + productId );
        System.out.println("生成图片URL: " + imageUrl);

    }

    // 根据名称模糊查询产品
    public List<Product> getProductsByName(String name) {
        return productDao.findByNameLike(name);
    }

    public Integer addProduct(Product product) {
        productDao.insert(product);
        return product.getId();  // insert后，id被自动赋值
    }
    public void updateProduct(Product product) {
        productDao.update(product);
    }

    public void deleteProductById(Integer id) {
        productDao.delete(id);
    }
}
