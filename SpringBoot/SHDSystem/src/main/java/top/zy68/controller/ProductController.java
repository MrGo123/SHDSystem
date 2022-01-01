package top.zy68.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import top.zy68.entity.Product;
import top.zy68.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * (Product)表控制层
 *
 * @author makejava
 * @since 2021-12-25 14:50:26
 */
@RestController
@RequestMapping("product")
public class ProductController {
    /**
     * 图片的存储位置
     */
    @Value("${file-path}")
    String filePath;
    /**
     * 服务对象
     */
    @Resource
    private ProductService productService;

    /**
     * 分页查询
     *
     * @param product     筛选条件
     * @param pageRequest 分页对象
     * @return 查询结果
     */
    @GetMapping
    public ResponseEntity<Page<Product>> queryByPage(Product product, PageRequest pageRequest) {
        return ResponseEntity.ok(this.productService.queryByPage(product, pageRequest));
    }

    /**
     * 上传图片
     *
     * @param file
     * @return
     */
    @PostMapping("/uploadAvatar")
    public Object uploadAvatar(MultipartFile file) {
        System.out.println("接口请求成功");
        // 获取文件名
        String fileName = file.getOriginalFilename();
        System.out.println(fileName);
        // 获取后缀名
        if (fileName == null) {
            return "空文件";
        }
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        // 支持的文件格式
        List<String> suffixList = Arrays.asList(".jpg", ".png", "jpeg");
        // 判断上传的文件格式是否支持
        if (!suffixList.contains(suffixName)) {
            System.err.println("文件后缀名有误，支持的后缀名为：" + suffixList);
            return "不支持该格式";
        }
        // 文件路径
        String path = filePath + fileName;
        File dest = new File(path);
        try {
            // 上传图片
            file.transferTo(dest);
            System.out.println("文件上传成功！");
        } catch (IOException e) {
            System.err.println("上传失败！");
        }
        return "uploaded";
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public ResponseEntity<Product> queryById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(this.productService.queryById(id));
    }

    /**
     * 通过用户id查询多条数据
     *
     * @param id 用户主键
     * @return 单条数据
     */
    @GetMapping("/queryBySellerId/{id}")
    public ResponseEntity<List<Product>> queryBySellerId(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(this.productService.queryBySellerId(id));
    }
    /**
     * 通过用户id查询多条数据
     *
     * @param id 用户主键
     * @return 单条数据
     */
    @GetMapping("/queryByBuyerId/{id}")
    public ResponseEntity<List<Product>> queryByBuyerId(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(this.productService.queryByBuyerId(id));
    }

    /**
     * 获取所有商品数据
     *
     * @return 单条数据
     */
    @GetMapping("/queryAll")
    public ResponseEntity<List<Product>> queryAll() {
        return ResponseEntity.ok(this.productService.queryAll());
    }

    /**
     * 新增数据
     *
     * @param product 实体
     * @return 新增结果
     */
    @PostMapping
    public ResponseEntity<Product> add(Product product) {
        System.out.println("product/add接口收到户端请求：" + product.toString());
        // product.setPublishTime(new Date());
        return ResponseEntity.ok(this.productService.insert(product));
    }

    /**
     * 编辑数据
     *
     * @param product 实体
     * @return 编辑结果
     */
    @PostMapping("/edit")
    public ResponseEntity<Product> edit(Product product) {
        return ResponseEntity.ok(this.productService.update(product));
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除是否成功
     */
    @DeleteMapping
    public ResponseEntity<Boolean> deleteById(Integer id) {
        return ResponseEntity.ok(this.productService.deleteById(id));
    }

}

