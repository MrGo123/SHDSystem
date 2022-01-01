package top.zy68.service;

import top.zy68.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

/**
 * (Product)表服务接口
 *
 * @author makejava
 * @since 2021-12-25 14:50:31
 */
public interface ProductService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Product queryById(Integer id);

    /**
     * 通过用户id查询多条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    List<Product> queryBySellerId(Integer id);    /**
     * 通过用户id查询多条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    List<Product> queryByBuyerId(Integer id);

    /**
     * 查询所有商品
     *
     * @return 实例对象
     */
    List<Product> queryAll();

    /**
     * 分页查询
     *
     * @param product     筛选条件
     * @param pageRequest 分页对象
     * @return 查询结果
     */
    Page<Product> queryByPage(Product product, PageRequest pageRequest);

    /**
     * 新增数据
     *
     * @param product 实例对象
     * @return 实例对象
     */
    Product insert(Product product);

    /**
     * 修改数据
     *
     * @param product 实例对象
     * @return 实例对象
     */
    Product update(Product product);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id);

}
