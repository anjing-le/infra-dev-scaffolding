package com.anjing.model.errorcode;

/**
 * 错误码接口
 * 
 * <p>统一错误码规范，所有错误码枚举都必须实现此接口</p>
 * 
 * <h3>🎯 错误码规范：</h3>
 * <ul>
 *   <li>成功: 0</li>
 *   <li>系统错误: 1xxx</li>
 *   <li>业务错误: 2xxx</li>
 *   <li>参数错误: 3xxx</li>
 *   <li>权限错误: 4xxx</li>
 * </ul>
 *
 * <p>完整分段、重试策略和新模块分配方式见
 * {@code project_document/ERROR_CODE_GUIDE.md}。</p>
 * 
 * @author Backend Template Team
 * @version 1.0
 */
public interface ErrorCode
{

    /**
     * 获取错误码
     * 
     * @return 错误码
     */
    String getCode();

    /**
     * 获取错误信息
     * 
     * @return 错误信息
     */
    String getMessage();
}
