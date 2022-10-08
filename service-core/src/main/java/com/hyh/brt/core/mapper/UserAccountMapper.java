package com.hyh.brt.core.mapper;

import com.hyh.brt.core.pojo.entity.UserAccount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * <p>
 * 用户账户 Mapper 接口
 * </p>
 *
 * @author hyh
 * @since 2022-06-19
 */
public interface UserAccountMapper extends BaseMapper<UserAccount> {

    void updateAccount(
            @Param("bindCode")String bindCode,
            @Param("amount") BigDecimal amount,
            @Param("freezeAmount")BigDecimal freezeAmount);
}
