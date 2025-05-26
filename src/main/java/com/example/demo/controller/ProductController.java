package com.example.demo.controller;

import com.example.demo.bean.Product;
import com.example.demo.bean.Result;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;
    
    @GetMapping("/list")
    public Result list(){
        return Result.success(productService.getAllProducts());
    }

    @PostMapping("/add")
    public Result add(@RequestBody Product product) {
        productService.addProduct(product);
        return Result.success("增加成功");
    }

    @PutMapping("/update")
    public Result update(@RequestBody Product product) {
        productService.updateProduct(product);
        return Result.success("更新成功");
    }

    @DeleteMapping("/delete")
    public Result delete(@RequestParam Product product) {
        productService.deleteProduct(product);
        return Result.success("删除成功");
    }
}
