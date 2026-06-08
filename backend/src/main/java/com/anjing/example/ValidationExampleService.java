package com.anjing.example;

import com.anjing.annotation.Facade;
import com.anjing.annotation.ScaffoldSample;
import com.anjing.model.validation.EmailAddress;
import com.anjing.model.validation.PhoneNumber;
import com.anjing.model.request.BaseRequest;
import com.anjing.model.response.APIResponse;
import com.anjing.model.validation.ValidationGroups;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Service;

import jakarta.validation.constraints.*;

/**
 * 🎯 统一校验使用示例
 * 
 * <p>展示@Facade注解、BeanValidator工具类、自定义校验注解等的完整使用方法</p>
 * 
 * <h3>📋 本示例包含：</h3>
 * <ul>
 *   <li>🔍 自定义校验注解使用 (@PhoneNumber, @EmailAddress)</li>
 *   <li>🎛️ 分组校验使用 (ValidationGroups)</li>
 *   <li>🎯 @Facade注解在不同场景下的应用</li>
 *   <li>📊 完整的参数校验和异常处理流程</li>
 * </ul>
 * 
 * <h3>⚠️ 重要说明：</h3>
 * <p>这是一个<b>示例类</b>，用于演示各种校验功能的使用方法。</p>
 * <p>在实际项目中，请根据具体业务需求创建相应的Service和Request类。</p>
 * 
 * @author Backend Template Team
 * @version 1.0
 * @since 1.0.0
 */
@ScaffoldSample("参数校验与 Facade 切面示例")
@Service
public class ValidationExampleService {

    /**
     * 🆕 用户注册示例
     * 
     * <p>展示创建场景下的完整参数校验</p>
     * 
     * @param request 用户注册请求
     * @return 注册结果
     */
    @Facade(
        scene = "用户注册",
        validationGroups = ValidationGroups.Register.class,
        enableLogging = true,
        enableValidation = true
    )
    public APIResponse<UserExampleVO> registerUser(UserRegisterRequest request) {
        // 模拟业务逻辑
        UserExampleVO user = new UserExampleVO();
        user.setId(1001L);
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        
        return APIResponse.success(user);
    }

    /**
     * 🔄 用户信息更新示例
     * 
     * <p>展示更新场景下的分组校验</p>
     * 
     * @param request 用户更新请求
     * @return 更新结果
     */
    @Facade(
        scene = "用户信息更新",
        validationGroups = ValidationGroups.Update.class,
        enableLogging = true
    )
    public APIResponse<UserExampleVO> updateUser(UserUpdateRequest request) {
        // 模拟业务逻辑
        UserExampleVO user = new UserExampleVO();
        user.setId(request.getId());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        
        return APIResponse.success(user);
    }

    /**
     * 🔍 用户查询示例
     * 
     * <p>展示查询场景下的简单校验</p>
     * 
     * @param request 用户查询请求
     * @return 查询结果
     */
    @Facade(
        scene = "用户查询",
        validationGroups = ValidationGroups.Query.class,
        enableLogging = false,  // 查询操作可以关闭详细日志
        enableValidation = true
    )
    public APIResponse<UserExampleVO> queryUser(UserQueryRequest request) {
        // 模拟业务逻辑
        UserExampleVO user = new UserExampleVO();
        user.setId(request.getUserId());
        user.setUsername("示例用户");
        
        return APIResponse.success(user);
    }

    /**
     * 📊 批量操作示例
     * 
     * <p>展示批量操作的校验处理</p>
     * 
     * @param request 批量操作请求
     * @return 操作结果
     */
    @Facade(
        scene = "批量用户操作",
        validationGroups = ValidationGroups.Batch.class
    )
    public APIResponse<String> batchOperateUsers(BatchUserRequest request) {
        // 模拟批量业务逻辑
        return APIResponse.successData("批量操作成功，处理了" + request.getUserIds().size() + "个用户");
    }

    /**
     * 🚫 不使用@Facade的普通方法示例
     * 
     * <p>展示普通Service方法，依赖Spring Boot标准校验</p>
     * 
     * @param username 用户名
     * @return 处理结果
     */
    public String simpleMethod(String username) {
        // 普通业务方法，不需要复杂的统一校验
        return "Hello, " + username;
    }

    // ==================== 示例Request类 ====================

    /**
     * 👤 用户注册请求示例
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class UserRegisterRequest extends BaseRequest {

        @NotBlank(groups = ValidationGroups.Register.class, message = "用户名不能为空")
        @Size(min = 3, max = 20, groups = ValidationGroups.Register.class, message = "用户名长度必须在3-20个字符之间")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", groups = ValidationGroups.Register.class, message = "用户名只能包含字母、数字和下划线")
        private String username;

        @NotBlank(groups = ValidationGroups.Register.class, message = "密码不能为空")
        @Size(min = 6, max = 20, groups = ValidationGroups.Register.class, message = "密码长度必须在6-20个字符之间")
        private String password;

        @EmailAddress(groups = ValidationGroups.Register.class, message = "邮箱格式不正确")
        private String email;

        @PhoneNumber(groups = ValidationGroups.Register.class, message = "手机号格式不正确")
        private String phone;

        @NotBlank(groups = ValidationGroups.Register.class, message = "真实姓名不能为空")
        @Size(max = 50, groups = ValidationGroups.Register.class, message = "真实姓名长度不能超过50个字符")
        private String realName;

        @Min(value = 1, groups = ValidationGroups.Register.class, message = "年龄必须大于0")
        @Max(value = 150, groups = ValidationGroups.Register.class, message = "年龄不能超过150")
        private Integer age;
    }

    /**
     * 🔄 用户更新请求示例
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class UserUpdateRequest extends BaseRequest {

        @NotNull(groups = ValidationGroups.Update.class, message = "用户ID不能为空")
        @Positive(groups = ValidationGroups.Update.class, message = "用户ID必须为正数")
        private Long id;

        @Size(min = 3, max = 20, groups = ValidationGroups.Update.class, message = "用户名长度必须在3-20个字符之间")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", groups = ValidationGroups.Update.class, message = "用户名只能包含字母、数字和下划线")
        private String username;

        @EmailAddress(groups = ValidationGroups.Update.class, required = false, message = "邮箱格式不正确")
        private String email;

        @PhoneNumber(groups = ValidationGroups.Update.class, required = false, message = "手机号格式不正确")
        private String phone;

        @Size(max = 50, groups = ValidationGroups.Update.class, message = "真实姓名长度不能超过50个字符")
        private String realName;

        @Min(value = 1, groups = ValidationGroups.Update.class, message = "年龄必须大于0")
        @Max(value = 150, groups = ValidationGroups.Update.class, message = "年龄不能超过150")
        private Integer age;
    }

    /**
     * 🔍 用户查询请求示例
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class UserQueryRequest extends BaseRequest {

        @NotNull(groups = ValidationGroups.Query.class, message = "用户ID不能为空")
        @Positive(groups = ValidationGroups.Query.class, message = "用户ID必须为正数")
        private Long userId;

        @Size(max = 20, groups = ValidationGroups.Query.class, message = "用户名长度不能超过20个字符")
        private String username;

        @EmailAddress(groups = ValidationGroups.Query.class, required = false, message = "邮箱格式不正确")
        private String email;
    }

    /**
     * 📊 批量操作请求示例
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class BatchUserRequest extends BaseRequest {

        @NotEmpty(groups = ValidationGroups.Batch.class, message = "用户ID列表不能为空")
        @Size(max = 100, groups = ValidationGroups.Batch.class, message = "批量操作用户数量不能超过100个")
        private java.util.List<@Positive(message = "用户ID必须为正数") Long> userIds;

        @NotBlank(groups = ValidationGroups.Batch.class, message = "操作类型不能为空")
        @Pattern(regexp = "^(enable|disable|delete)$", groups = ValidationGroups.Batch.class, 
                message = "操作类型只能是enable、disable或delete")
        private String operation;
    }

    // ==================== 示例VO类 ====================

    /**
     * 👤 用户信息VO示例
     */
    @Data
    public static class UserExampleVO {
        private Long id;
        private String username;
        private String email;
        private String phone;
        private String realName;
        private Integer age;
        private String status;
        private java.time.LocalDateTime createTime;
        private java.time.LocalDateTime updateTime;
    }
}
