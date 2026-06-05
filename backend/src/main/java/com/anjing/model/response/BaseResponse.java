package com.anjing.model.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 通用响应基类
 * 
 * <p>所有业务响应类的基类，提供通用的响应状态字段</p>
 * 
 * <h3>🎯 设计原则：</h3>
 * <ul>
 *   <li>✅ 统一状态 - success字段直接表示操作结果</li>
 *   <li>📝 标准字段 - responseCode和responseMessage统一命名</li>
 *   <li>🔧 序列化支持 - 实现Serializable接口</li>
 * </ul>
 * 
 * @author Backend Template Team  
 * @version 1.0
 * @deprecated 新业务接口统一返回 {@link APIResponse}；该类仅用于兼容外部或历史远程响应格式。
 */
@Deprecated(since = "1.1.0", forRemoval = false)
@Setter
@Getter
public class BaseResponse implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * 操作是否成功
     */
    private Boolean success;

    /**
     * 响应状态码
     */
    private String responseCode;

    /**
     * 响应消息
     */
    private String responseMessage;
}
