package com.example.demo.controller;

import com.example.demo.annotation.AccessVerification;
import com.example.demo.annotation.VerificationLevel;
import com.example.demo.bean.Product;
import com.example.demo.bean.Result;
import com.example.demo.bean.User;
import com.example.demo.dao.ProductDao;
import com.example.demo.dao.UserDao;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String TEMP_IMAGE_KEY_PREFIX = "tempProductImage:";



    // 根据 ID 查询商品详情
    @AccessVerification(VerificationLevel.NONE)
    @GetMapping("/{id}")
    public Result<Product> getProductById(@PathVariable Integer id) {
        Product product =productService.findById(id);
        if (product == null) {
            return Result.fail("未找到该商品");
        }
        return Result.success(product);
    }

    @AccessVerification(VerificationLevel.NONE)
    @GetMapping("/list")
    public Result list(){
        return Result.success(productService.getAllProducts());
    }

    // 根据名称模糊查询（无需认证）
    @AccessVerification(VerificationLevel.NONE)
    @GetMapping("/search")
    public Result<List<Product>> searchByName(@RequestParam String name) {
        List<Product> products = productService.getProductsByName(name);
        if (products == null || products.isEmpty()) {
            return Result.fail("未找到相关产品");
        }
        return Result.success(products);
    }


    @PutMapping("/update")
    @AccessVerification(VerificationLevel.PRIMARY)
    public Result update(@RequestBody Product product, HttpServletRequest request) {
        User currentUser = (User) request.getAttribute("currentUser");
        if (currentUser == null) {
            return Result.fail("用户未登录");
        }
        Integer userId = currentUser.getId();

        // 1. 更新产品基础信息
        productService.updateProduct(product);

        // 2. 读取 Redis 缓存图片
        String key = TEMP_IMAGE_KEY_PREFIX + userId;
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String cachedImageUrl = ops.get(key);

        if (cachedImageUrl != null) {
            try {
                productService.updateImageUrl(product.getId(), cachedImageUrl);
                redisTemplate.delete(key);
            } catch (Exception e) {
                // 可选：记录异常日志
                return Result.fail("更新图片URL失败：" + e.getMessage());
            }
        }

        return Result.success("更新成功");
    }

    //管理员删除产品
    @AccessVerification(VerificationLevel.PRIMARY)
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        productService.deleteProductById(id);
        return Result.success("删除成功");
    }

    @PostMapping("/upimage")
    @AccessVerification(VerificationLevel.PRIMARY)
    public Result<String> uploadImage(
            @RequestParam(value = "id", required = false) Integer id,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {

        User currentUser = (User) request.getAttribute("currentUser");
        if (currentUser == null) {
            return Result.fail("用户未登录");
        }
        Integer userId = currentUser.getId();

        if (file.isEmpty()) {
            return Result.fail("上传文件不能为空");
        }
        try {
            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "products" + File.separator;

            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String originalFilename = file.getOriginalFilename();
            String filename = UUID.randomUUID().toString() + "-" + originalFilename;
            File dest = new File(uploadDir + filename);
            file.transferTo(dest);

            String baseUrl = "http://localhost:8000/uploads/";
            String imageUrl = baseUrl + "products/" + filename;

            if (id != null) {
                // 有产品ID，直接更新数据库
                productService.updateImageUrl(id, imageUrl);
            } else {
                // 无产品ID，缓存图片URL，key 用 userId
                String key = TEMP_IMAGE_KEY_PREFIX + userId;
                ValueOperations<String, String> ops = redisTemplate.opsForValue();
                ops.set(key, imageUrl, 1, TimeUnit.HOURS);
            }

            return Result.success(imageUrl);
        } catch (IOException e) {
            return Result.fail("上传失败：" + e.getMessage());
        }
    }


    @PostMapping("/add")
    @AccessVerification(VerificationLevel.PRIMARY)
    public Result add(@RequestBody Product product, HttpServletRequest request) {
        User currentUser = (User) request.getAttribute("currentUser");
        if (currentUser == null) {
            return Result.fail("用户未登录");
        }
        Integer userId = currentUser.getId();

        // 1. 调用服务新增产品，确保返回新ID
        Integer newProductId = productService.addProduct(product);
        if (newProductId == null) {
            return Result.fail("新增产品失败");
        }
        product.setId(newProductId);

        // 2. 从 Redis 取缓存图片
        String key = TEMP_IMAGE_KEY_PREFIX + userId;
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String cachedImageUrl = ops.get(key);

        if (cachedImageUrl != null) {
            try {
                productService.updateImageUrl(product.getId(), cachedImageUrl);
                redisTemplate.delete(key);
            } catch (Exception e) {
                // 可选：记录异常日志
                return Result.fail("更新图片URL失败：" + e.getMessage());
            }
        }

        return Result.success("新增成功");
    }
}
