package com.site.common.base.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mybatis.pj.example.select.SelectBaseExample;
import com.mybatis.pj.mapper.BaseExampleMapper;
import com.site.common.PageResult;
import com.site.common.base.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.List;

/**
 * 增删改查通用service实现类
*/
public abstract class BaseServiceImpl<T> implements IBaseService<T> {

    @Autowired
    private Mapper<T> mapper;
    @Autowired
    private MySqlMapper<T> mySqlMapper;
    @Autowired
    private BaseExampleMapper<T> baseExampleMapper;

    /**
     * 根据主键查询
     * @param id 主键
     * @return 实体对象
     */
    @Override
    public T findOne(Serializable id) {
        return mapper.selectByPrimaryKey(id);
    }

    /**
     * 查询全部
     * @return 实体对象集合
     */
    @Override
    public List<T> findAll() {
        return mapper.selectAll();
    }

    /**
     * 根据条件查询列表
     * @param t 查询条件对象
     * @return
     */
    @Override
    public List<T> findByWhere(T t) {
        return mapper.select(t);
    }

    /**
     * 根据复杂条件查询列表
     * @param example 查询条件对象
     * @return
     */
    public List<T> findByWhere(Example example){
        return mapper.selectByExample(example);
    }

    /**
     * 分页查询列表
     * @param page 页号
     * @param size 页大小
     * @return 分页实体对象
     */
    @Override
    public PageResult<T> findPage(Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<T> list = mapper.selectAll();
        PageInfo<T> pageInfo = new PageInfo<>(list);
        return new PageResult<T>(pageInfo);
    }

    /**
     * 多表查询(pj.mybati基于 tk.mapper 扩展了多表查询与更新)
     * @param page
     * @param size
     * @param selectBaseExample
     * @return
     */
    public PageResult<T> findJoinPage(Integer page, Integer size, SelectBaseExample selectBaseExample){
        PageHelper.startPage(page, size);
        List<T> ts = baseExampleMapper.selectPage(selectBaseExample);
        PageInfo<T> tPageInfo = new PageInfo<>(ts);
        return new PageResult<T>(tPageInfo);
    }

    /**
     * 根据条件分页查询列表
     * @param page 页号
     * @param size 页大小
     * @param t    查询条件对象
     * @return 分页实体对象
     */
    @Override
    public PageResult<T> findPage(Integer page, Integer size, T t) {
        PageHelper.startPage(page, size);
        List<T> list = mapper.select(t);
        PageInfo<T> pageInfo = new PageInfo<>(list);
        return new PageResult<T>(pageInfo);
    }

    /**
     * 根据复杂条件分页查询列表
     * @param page 页号
     * @param size 页大小
     * @param example 查询条件对象
     * @return 分页实体对象
     */
    public PageResult<T> findPage(Integer page, Integer size, Example example){
        PageHelper.startPage(page, size);
        List<T> list = mapper.selectByExample(example);
        PageInfo<T> pageInfo = new PageInfo<>(list);
        return new PageResult<T>(pageInfo);
    }

    /**
     * 新增
     * @param t 实体对象
     */
    @Override
    public int insert(T t) {
        return mapper.insertSelective(t);
    }

    /**
     * 批量新增（注意：这里主键 id 必须为自增的，否则报错没有默认值）
     * @param list 实体对象
     */
    public int insertBatch(List<T> list){
        return mySqlMapper.insertList(list);
    }

    /**
     * 根据主键更新
     * @param t 实体对象
     */
    @Override
    public int update(T t) {
        return mapper.updateByPrimaryKeySelective(t);
    }

    /**
     * 批量删除
     * @param ids 主键集合
     */
    @Override
    public void deleteByIds(Serializable[] ids) {
        if (ids != null && ids.length > 0) {
            for (Serializable id : ids) {
                mapper.deleteByPrimaryKey(id);
            }
        }
    }
}