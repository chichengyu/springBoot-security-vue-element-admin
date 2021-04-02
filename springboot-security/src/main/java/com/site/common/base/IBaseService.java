package com.site.common.base;

import com.mybatis.pj.example.select.SelectBaseExample;
import com.site.common.PageResult;
import org.apache.ibatis.annotations.Mapper;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.List;


/**
 * 增删改查通用service
 *
 * 这里加 @Mapper 作用：
 *  使用的tk的开源项目进行mybatis集成，百度了很多解决方案，最终看到一位前辈介绍：
 *  doScan()会扫描启动类同级目录下的mapper接口，但是合理的目录结果绝对不允许所有的mapper都在启动类目录下，所以在启动类目录下添加了一个伪mapper
*/
@Mapper
public interface IBaseService<T> {

    /**
     * 根据主键查询
     * @param id 主键
     * @return 实体对象
     */
    public T findOne(Serializable id);

    /**
     * 查询全部
     * @return 实体对象集合
     */
    public List<T> findAll();

    /**
     * 根据条件查询列表
     * @param t 查询条件对象
     * @return
     */
    public List<T> findByWhere(T t);

    /**
     * 根据复杂条件查询列表
     * @param example 查询条件对象
     * @return
     */
    public List<T> findByWhere(Example example);

    /**
     * 分页查询列表
     * @param page 页号
     * @param size 页大小
     * @return 分页实体对象
     */
   public PageResult<T> findPage(Integer page, Integer size);

    /**
     * 根据条件分页查询列表
     * @param page 页号
     * @param size 页大小
     * @param t    查询条件对象
     * @return 分页实体对象
     */
   public PageResult<T> findPage(Integer page, Integer size, T t);

    /**
     * 根据复杂条件分页查询列表
     * @param page 页号
     * @param size 页大小
     * @param example 查询条件对象
     * @return 分页实体对象
     */
    public PageResult<T> findPage(Integer page, Integer size, Example example);

    /**
     * 多表查询
     * @return
     */
   public PageResult<T> findJoinPage(Integer page, Integer size, SelectBaseExample selectBaseExample);

    /**
     * 新增
     * @param t 实体对象
     */
    public int insert(T t);

    /**
     * 批量新增
     * @param list 实体对象
     */
    public int insertBatch(List<T> list);

    /**
     * 根据主键更新
     * @param t 实体对象
     */
    public int update(T t);

    /**
     * 批量删除
     * @param ids 主键集合
     */
    public void deleteByIds(Serializable[] ids);

}