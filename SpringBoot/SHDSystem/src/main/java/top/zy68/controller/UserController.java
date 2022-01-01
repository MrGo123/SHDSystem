package top.zy68.controller;

import top.zy68.common.BaseResponse;
import top.zy68.entity.User;
import top.zy68.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * (User)表控制层
 *
 * @author makejava
 * @since 2021-12-25 14:50:39
 */
@RestController
@RequestMapping("/user")
public class UserController {
    /**
     * 服务对象
     */
    @Resource
    private UserService userService;

    @GetMapping("/hello")
    public String getHelloWorld() {
        String s = "Hello World!";
        System.out.println(s);
        return s;
    }

    /**
     * 分页查询
     *
     * @param user        筛选条件
     * @param pageRequest 分页对象
     * @return 查询结果
     */
    @GetMapping
    public ResponseEntity<Page<User>> queryByPage(User user, PageRequest pageRequest) {
        return ResponseEntity.ok(this.userService.queryByPage(user, pageRequest));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("/queryById/{id}")
    public BaseResponse<User> queryById(@PathVariable("id") Integer id) {
        System.out.println("查询了用户id=" + id);
        User user;
        try {
            user = this.userService.queryById(id);
            System.out.println(user.toString());
        }catch (NullPointerException e){
            e.printStackTrace();
            return new BaseResponse<>(200, "ok，但没有该用户", null);
        }
        return new BaseResponse<>(200, "ok", user);
    }
    /**
     * 通过手机号查询单条数据
     *
     * @param phone 主键
     * @return 单条数据
     */
    @GetMapping("/queryByPhone/{phone}")
    public BaseResponse<User> queryByPhone(@PathVariable("phone") String phone) {
        System.out.println("查询了用户phone=" + phone);
        User user;
        try {
            user = this.userService.queryByPhone(phone);
            System.out.println(user.toString());
        }catch (NullPointerException e){
            e.printStackTrace();
            return new BaseResponse<>(200, "ok，但没有该用户", null);
        }
        return new BaseResponse<>(200, "ok", user);
    }

    /**
     * 新增数据
     *
     * @param user 实体
     * @return 新增结果
     */
    @PostMapping
    public ResponseEntity<User> add(User user) {
        return ResponseEntity.ok(this.userService.insert(user));
    }

    /**
     * 编辑数据
     *
     * @param user 实体
     * @return 编辑结果
     */
    @PutMapping
    public ResponseEntity<User> edit(User user) {
        return ResponseEntity.ok(this.userService.update(user));
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除是否成功
     */
    @DeleteMapping
    public ResponseEntity<Boolean> deleteById(Integer id) {
        return ResponseEntity.ok(this.userService.deleteById(id));
    }

}

