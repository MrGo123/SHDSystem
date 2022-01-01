package top.zy68.service.impl;

import top.zy68.entity.Product;
import top.zy68.dao.ProductDao;
import top.zy68.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Resource;
import java.util.List;

/**
 * (Product)表服务实现类
 *
 * @author makejava
 * @since 2021-12-25 14:50:32
 */
@Service("productService")
public class ProductServiceImpl implements ProductService {
    @Resource
    private ProductDao productDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Product queryById(Integer id) {
        return this.productDao.queryById(id);
    }

    /**
     * 通过用户id查询多条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public List<Product> queryBySellerId(Integer id){
        return this.productDao.queryBySellerId(id);
    }
    /**
     * 通过用户id查询多条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public List<Product> queryByBuyerId(Integer id){
        return this.productDao.queryByBuyerId(id);
    }
    /**
     * 查询所有商品
     *
     * @return 实例对象
     */
    @Override
    public List<Product> queryAll(){
        return this.productDao.queryAll();
    }

    /**
     * 分页查询
     *
     * @param product 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    @Override
    public Page<Product> queryByPage(Product product, PageRequest pageRequest) {
        long total = this.productDao.count(product);
        return new PageImpl<>(this.productDao.queryAllByLimit(product, pageRequest), pageRequest, total);
    }

    /**
     * 新增数据
     *
     * @param product 实例对象
     * @return 实例对象
     */
    @Override
    public Product insert(Product product) {
        this.productDao.insert(product);
        return product;
    }

    /**
     * 修改数据
     *
     * @param product 实例对象
     * @return 实例对象
     */
    @Override
    public Product update(Product product) {
        this.productDao.update(product);
        return this.queryById(product.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.productDao.deleteById(id) > 0;
    }
}
