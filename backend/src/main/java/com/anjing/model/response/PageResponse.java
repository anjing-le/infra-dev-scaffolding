package com.anjing.model.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 分页响应类
 * 
 * <p>按照标准实现的分页响应，继承MultiResponse</p>
 * 
 * <h3>🎯 设计特点：</h3>
 * <ul>
 *   <li>📄 标准字段 - currentPage、pageSize、totalPage、total</li>
 *   <li>🏗️ 继承结构 - 继承MultiResponse，复用数据列表字段</li>
 *   <li>🔧 便捷方法 - 提供静态of方法快速创建</li>
 * </ul>
 * 
 * @param <T> 数据类型
 * @author Backend Template Team
 * @version 1.0
 * @deprecated 新分页接口使用 {@code APIResponse<PageResult<T>>}，字段固定为 records/current/size/total。
 */
@Deprecated(since = "1.1.0", forRemoval = false)
@Setter
@Getter
public class PageResponse<T> extends MultiResponse<T>
{
    private static final long serialVersionUID = 1L;

    /**
     * 当前页
     */
    private Integer currentPage;

    /**
     * 每页结果数
     */
    private Integer pageSize;

    /**
     * 总页数
     */
    private Integer totalPage;

    /**
     * 总记录数
     */
    private Integer total;

    /**
     * 创建分页响应
     * 
     * @param datas    数据列表
     * @param total    总记录数
     * @param pageSize 每页大小
     * @param <T>      数据类型
     * @return 分页响应
     */
    public static <T> PageResponse<T> of(List<T> datas, int total, int pageSize)
    {
        PageResponse<T> multiResponse = new PageResponse<>();
        multiResponse.setSuccess(true);
        multiResponse.setResponseCode("0");
        multiResponse.setResponseMessage("查询成功");
        multiResponse.setDatas(datas);
        multiResponse.setTotal(total);
        multiResponse.setPageSize(pageSize);
        multiResponse.setTotalPage((total + pageSize - 1) / pageSize);
        return multiResponse;
    }

    /**
     * 根据Spring Data Page对象创建分页结果
     * 
     * @param page Spring Data Page对象
     * @param <T>  数据类型
     * @return 分页结果
     */
    public static <T> PageResponse<T> of(Page<T> page) {
        PageResponse<T> result = new PageResponse<>();
        result.setSuccess(true);
        result.setResponseCode("0");
        result.setResponseMessage("查询成功");
        result.setDatas(page.getContent());
        result.setTotal((int) page.getTotalElements());
        result.setCurrentPage(page.getNumber() + 1); // Spring Data的页码从0开始，转换为从1开始
        result.setPageSize(page.getSize());
        result.setTotalPage(page.getTotalPages());
        return result;
    }
}
